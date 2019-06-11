package kr.co.gubed.habit2goodpure;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.shrikanthravi.collapsiblecalendarview.view.ExpandIconView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HfCollapsibleCalendar extends HfUICalendar {
    private HfCalendarAdapter mAdapter;
    private HfCollapsibleCalendar.CalendarListener mListener;
    private boolean expanded = false;
    private int mInitHeight = 0;
    private final Handler mHandler = new Handler();
    private boolean mIsWaitingForUpdate = false;
    private int mCurrentWeekIndex;
    private HabitDbAdapter dbAdapter;

    public HfCollapsibleCalendar(Context context) {
        super(context);
    }

    public HfCollapsibleCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HfCollapsibleCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(Context context) {
        super.init(context);
        dbAdapter = new HabitDbAdapter(context);
        dbAdapter.open();
        Calendar cal = Calendar.getInstance();
        HfCalendarAdapter adapter = new HfCalendarAdapter(context, cal);
        this.setAdapter(adapter);
        this.mBtnPrevMonth.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                HfCollapsibleCalendar.this.prevMonth();
            }
        });
        this.mBtnNextMonth.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                HfCollapsibleCalendar.this.nextMonth();
            }
        });
        this.mBtnPrevWeek.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                HfCollapsibleCalendar.this.prevWeek();
            }
        });
        this.mBtnNextWeek.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                HfCollapsibleCalendar.this.nextWeek();
            }
        });
        //this.expandIconView.setState(0, true);
        this.expandIconView.setState(ExpandIconView.MORE, true);
        this.expandIconView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (HfCollapsibleCalendar.this.expanded) {
                    HfCollapsibleCalendar.this.collapse(50);
                } else {
                    HfCollapsibleCalendar.this.expand(50);
                }

                HfCollapsibleCalendar.this.expanded = !HfCollapsibleCalendar.this.expanded;
            }
        });
        this.post(new Runnable() {
            public void run() {
                HfCollapsibleCalendar.this.collapseTo(HfCollapsibleCalendar.this.mCurrentWeekIndex);
            }
        });
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mInitHeight = this.mTableBody.getMeasuredHeight();
        if (this.mIsWaitingForUpdate) {
            //this.redraw();
            this.redrawFromDb();
            this.mHandler.post(new Runnable() {
                public void run() {
                    HfCollapsibleCalendar.this.collapseTo(HfCollapsibleCalendar.this.mCurrentWeekIndex);
                }
            });
            this.mIsWaitingForUpdate = false;
            if (this.mListener != null) {
                this.mListener.onDataUpdate();
            }
        }

    }

    protected void redraw() {
        TableRow rowWeek = (TableRow)this.mTableHead.getChildAt(0);
        int i;
        if (rowWeek != null) {
            for(i = 0; i < rowWeek.getChildCount(); ++i) {
                ((TextView)rowWeek.getChildAt(i)).setTextColor(this.getTextColor());
            }
        }

        if (this.mAdapter != null) {
            for(i = 0; i < this.mAdapter.getCount(); ++i) {
                HfDay day = this.mAdapter.getItem(i);
                View view = this.mAdapter.getView(i);
                TextView txtDay = view.findViewById(com.shrikanthravi.collapsiblecalendarview.R.id.txt_day);
                txtDay.setBackgroundColor(0);
                txtDay.setTextColor(this.getTextColor());


/*
                if (this.isToady(day)) {
                    txtDay.setBackgroundDrawable(this.getTodayItemBackgroundDrawable());
                    txtDay.setTextColor(this.getTodayItemTextColor());
                }
*/
                if (this.isSelectedDay(day)) {
                    txtDay.setBackgroundDrawable(this.getSelectedItemBackgroundDrawable(RESULT_DEFAULT));
                    txtDay.setTextColor(this.getSelectedItemTextColor());
                    /* DELETE 선택 시 UI 변경을 위해 추가 */
                    if (day.getResult() == RESULT_DELETE) {
                        day.setIsSelected(false);
                    }
                }

                if (day.getIsSelected()) {
                    txtDay.setBackgroundDrawable(this.getSelectedItemBackgroundDrawable(day.getResult()));
                    txtDay.setTextColor(this.getSelectedItemTextColor());
                }
            }
        }

    }

    private void redrawFromDb() {
        TableRow rowWeek = (TableRow)this.mTableHead.getChildAt(0);
        int i;
        if (rowWeek != null) {
            for(i = 0; i < rowWeek.getChildCount(); ++i) {
                ((TextView)rowWeek.getChildAt(i)).setTextColor(this.getTextColor());
            }
        }

        if (this.mAdapter != null) {
            for(i = 0; i < this.mAdapter.getCount(); ++i) {
                HfDay day = this.mAdapter.getItem(i);
                View view = this.mAdapter.getView(i);
                HfEvent event = this.mAdapter.getEvent(i);
                TextView txtDay = view.findViewById(R.id.txt_day);
                txtDay.setBackgroundColor(0);
                txtDay.setTextColor(this.getTextColor());

                if (this.isSelectedDay(day)) {
                    txtDay.setBackgroundDrawable(this.getSelectedItemBackgroundDrawable(RESULT_DEFAULT));
                    txtDay.setTextColor(this.getSelectedItemTextColor());
                }

                int mResult = dbAdapter.getExecResult(this.mAdapter.getHabitid(), day);
                if (mResult > -1) {
                    setSelectedDayWithResult(i, mResult);
                } else {
                    day.setIsSelected(false);
                }

                if (day.getIsSelected()) {
                    txtDay.setBackgroundDrawable(this.getSelectedItemBackgroundDrawable(day.getResult()));
                    txtDay.setTextColor(this.getSelectedItemTextColor());
                }

                /* DB 에서 Memo event tag 가져오기 */
                ImageView imgEventTag = view.findViewById(R.id.img_event_tag);

                String memo = dbAdapter.getMemo(this.mAdapter.getHabitid(), day);
                if (memo != null) {
                    imgEventTag.setVisibility(VISIBLE);

                }
            }
        }
    }

    //protected void reload() {
    public void reload() {
        //dbAdapter = new HabitDbAdapter(getContext());

        if (this.mAdapter != null) {
            this.mAdapter.refresh();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM", Locale.getDefault());
            dateFormat.setTimeZone(this.mAdapter.getCalendar().getTimeZone());
            this.mTxtTitle.setText(dateFormat.format(this.mAdapter.getCalendar().getTime()));
            this.mTableHead.removeAllViews();
            this.mTableBody.removeAllViews();
            int[] dayOfWeekIds = new int[]{com.shrikanthravi.collapsiblecalendarview.R.string.sunday, com.shrikanthravi.collapsiblecalendarview.R.string.monday, com.shrikanthravi.collapsiblecalendarview.R.string.tuesday, com.shrikanthravi.collapsiblecalendarview.R.string.wednesday, com.shrikanthravi.collapsiblecalendarview.R.string.thursday, com.shrikanthravi.collapsiblecalendarview.R.string.friday, com.shrikanthravi.collapsiblecalendarview.R.string.saturday};
            TableRow rowCurrent = new TableRow(this.mContext);
            rowCurrent.setLayoutParams(new LayoutParams(-1, -2));

            //final int i;
            for(int i = 0; i < 7; ++i) {
                View view = this.mInflater.inflate(com.shrikanthravi.collapsiblecalendarview.R.layout.layout_day_of_week, null);
                TextView txtDayOfWeek = view.findViewById(com.shrikanthravi.collapsiblecalendarview.R.id.txt_day_of_week);
                txtDayOfWeek.setText(dayOfWeekIds[(i + this.getFirstDayOfWeek()) % 7]);
                view.setLayoutParams(new android.widget.TableRow.LayoutParams(0, -2, 1.0F));
                rowCurrent.addView(view);
            }

            this.mTableHead.addView(rowCurrent);


            for(int i = 0; i < this.mAdapter.getCount(); ++i) {
                if (i % 7 == 0) {
                    rowCurrent = new TableRow(this.mContext);
                    rowCurrent.setLayoutParams(new LayoutParams(-1, -2));
                    this.mTableBody.addView(rowCurrent);
                }
                final int j=i;
                View view = this.mAdapter.getView(i);
                view.setLayoutParams(new android.widget.TableRow.LayoutParams(0, -2, 1.0F));
                view.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        HfCollapsibleCalendar.this.onItemClicked(v, HfCollapsibleCalendar.this.mAdapter.getItem(j));
                    }
                });
                rowCurrent.addView(view);
            }

            this.redrawFromDb();
            this.mIsWaitingForUpdate = true;
        }

    }

    private int getSuitableRowIndex() {
        View view;
        TableRow row;
        if (this.getSelectedItemPosition() != -1) {
            view = this.mAdapter.getView(this.getSelectedItemPosition());
            row = (TableRow)view.getParent();
            return this.mTableBody.indexOfChild(row);
        } else if (this.getTodayItemPosition() != -1) {
            view = this.mAdapter.getView(this.getTodayItemPosition());
            row = (TableRow)view.getParent();
            return this.mTableBody.indexOfChild(row);
        } else {
            return 0;
        }
    }

    private void onItemClicked(View view, HfDay day) {
        Log.d("Calendar", "onItemClicked");
        this.select(day);
        Calendar cal = this.mAdapter.getCalendar();
        int newYear = day.getYear();
        int newMonth = day.getMonth();
        int oldYear = cal.get(Calendar.YEAR);
        int oldMonth = cal.get(Calendar.MONTH);
        if (newMonth != oldMonth) {
            cal.set(day.getYear(), day.getMonth(), 1);
            if (newYear > oldYear || newMonth > oldMonth) {
                this.mCurrentWeekIndex = 0;
            }

            if (newYear < oldYear || newMonth < oldMonth) {
                this.mCurrentWeekIndex = -1;
            }

            if (this.mListener != null) {
                this.mListener.onMonthChange();
            }

            this.reload();
        }

        if (this.mListener != null) {
            this.mListener.onItemClick(view);
        }

    }

    private void setAdapter(HfCalendarAdapter adapter) {
        this.mAdapter = adapter;
        adapter.setFirstDayOfWeek(this.getFirstDayOfWeek());
        this.reload();
        this.mCurrentWeekIndex = this.getSuitableRowIndex();
    }

    /*
    public void addEventTag(int numYear, int numMonth, int numDay, int result) {
        this.mAdapter.addEvent(new HfEvent(numYear, numMonth, numDay, result));
        this.reload();
    }
    */
    public void setEventTag(int numYear, int numMonth, int numDay, int result) {
        this.mAdapter.setEventResult(numYear, numMonth, numDay, result);
        this.reload();
    }

    public void deleteEventTag(int numYear, int numMonth, int numDay) {
        this.mAdapter.deleteEvent(numYear, numMonth, numDay);
        this.reload();
    }

    private void prevMonth() {
        Calendar cal = this.mAdapter.getCalendar();
        if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
            cal.set(cal.get(Calendar.YEAR) - 1, cal.getActualMaximum(Calendar.MONTH), 1);
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        }

        this.reload();
        if (this.mListener != null) {
            this.mListener.onMonthChange();
        }

    }

    private void nextMonth() {
        Calendar cal = this.mAdapter.getCalendar();
        if (cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
            cal.set(cal.get(Calendar.YEAR) + 1, cal.getActualMinimum(Calendar.MONTH), 1);
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        }

        this.reload();
        if (this.mListener != null) {
            this.mListener.onMonthChange();
        }

    }

    private void prevWeek() {
        if (this.mCurrentWeekIndex - 1 < 0) {
            this.mCurrentWeekIndex = -1;
            this.prevMonth();
        } else {
            --this.mCurrentWeekIndex;
            this.collapseTo(this.mCurrentWeekIndex);
        }

    }

    private void nextWeek() {
        if (this.mCurrentWeekIndex + 1 >= this.mTableBody.getChildCount()) {
            this.mCurrentWeekIndex = 0;
            this.nextMonth();
        } else {
            ++this.mCurrentWeekIndex;
            this.collapseTo(this.mCurrentWeekIndex);
        }

    }

    public int getYear() {
        return this.mAdapter.getCalendar().get(Calendar.YEAR);
    }

    public int getMonth() {
        return this.mAdapter.getCalendar().get(Calendar.MONTH);
    }

    public HfDay getSelectedDay() {
        if (this.getSelectedItem() == null) {
            Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            return new HfDay(year, month + 1, day);
        } else {
            return new HfDay(this.getSelectedItem().getYear(), this.getSelectedItem().getMonth(), this.getSelectedItem().getDay());
        }
    }

    private boolean isSelectedDay(HfDay day) {
        return day != null && this.getSelectedItem() != null && day.getYear() == this.getSelectedItem().getYear() && day.getMonth() == this.getSelectedItem().getMonth() && day.getDay() == this.getSelectedItem().getDay();
    }

    private boolean isToady(HfDay day) {
        Calendar todayCal = Calendar.getInstance();
        return day != null && day.getYear() == todayCal.get(Calendar.YEAR) && day.getMonth() == todayCal.get(Calendar.MONTH) && day.getDay() == todayCal.get(Calendar.DAY_OF_MONTH);
    }

    public int getSelectedItemPosition() {
        int position = -1;

        for(int i = 0; i < this.mAdapter.getCount(); ++i) {
            HfDay day = this.mAdapter.getItem(i);
            if (this.isSelectedDay(day)) {
                position = i;
                break;
            }
        }

        return position;
    }

    private int getTodayItemPosition() {
        int position = -1;

        for(int i = 0; i < this.mAdapter.getCount(); ++i) {
            HfDay day = this.mAdapter.getItem(i);
            if (this.isToady(day)) {
                position = i;
                break;
            }
        }

        return position;
    }

    private void collapse(int duration) {
        if (this.getState() == 0) {
            this.setState(2);
            this.mLayoutBtnGroupMonth.setVisibility(View.GONE);
            this.mLayoutBtnGroupWeek.setVisibility(View.VISIBLE);
            this.mBtnPrevWeek.setClickable(false);
            this.mBtnNextWeek.setClickable(false);
            int index = this.getSuitableRowIndex();
            this.mCurrentWeekIndex = index;
            final int currentHeight = this.mInitHeight;
            final int targetHeight = this.mTableBody.getChildAt(index).getMeasuredHeight();
            int tempHeight = 0;

            for(int i = 0; i < index; ++i) {
                tempHeight += this.mTableBody.getChildAt(i).getMeasuredHeight();
            }

            final int finalTempHeight = tempHeight;
            Animation anim = new Animation() {
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    HfCollapsibleCalendar.this.mScrollViewBody.getLayoutParams().height = interpolatedTime == 1.0F ? targetHeight : currentHeight - (int)((float)(currentHeight - targetHeight) * interpolatedTime);
                    HfCollapsibleCalendar.this.mScrollViewBody.requestLayout();
                    if (HfCollapsibleCalendar.this.mScrollViewBody.getMeasuredHeight() < finalTempHeight + targetHeight) {
                        int position = finalTempHeight + targetHeight - HfCollapsibleCalendar.this.mScrollViewBody.getMeasuredHeight();
                        HfCollapsibleCalendar.this.mScrollViewBody.smoothScrollTo(0, position);
                    }

                    if (interpolatedTime == 1.0F) {
                        HfCollapsibleCalendar.this.setState(1);
                        HfCollapsibleCalendar.this.mBtnPrevWeek.setClickable(true);
                        HfCollapsibleCalendar.this.mBtnNextWeek.setClickable(true);
                    }

                }
            };
            anim.setDuration((long)0);
            this.startAnimation(anim);
        }

        this.expandIconView.setState(ExpandIconView.MORE, true);
    }

    private void collapseTo(int index) {
        if (this.getState() == 1) {
            if (index == -1) {
                index = this.mTableBody.getChildCount() - 1;
            }

            this.mCurrentWeekIndex = index;
            int targetHeight = this.mTableBody.getChildAt(index).getMeasuredHeight();
            int tempHeight = 0;

            for(int i = 0; i < index; ++i) {
                tempHeight += this.mTableBody.getChildAt(i).getMeasuredHeight();
            }

            final int finalTempHeight = tempHeight;
            this.mScrollViewBody.getLayoutParams().height = targetHeight;
            this.mScrollViewBody.requestLayout();
            this.mHandler.post(new Runnable() {
                public void run() {
                    HfCollapsibleCalendar.this.mScrollViewBody.smoothScrollTo(0, finalTempHeight);
                }
            });
            if (this.mListener != null) {
                this.mListener.onWeekChange(this.mCurrentWeekIndex);
            }
        }

    }

    private void expand(int duration) {
        if (this.getState() == 1) {
            this.setState(2);
            this.mLayoutBtnGroupMonth.setVisibility(View.VISIBLE);
            this.mLayoutBtnGroupWeek.setVisibility(View.GONE);
            this.mBtnPrevMonth.setClickable(false);
            this.mBtnNextMonth.setClickable(false);
            final int currentHeight = this.mScrollViewBody.getMeasuredHeight();
            final int targetHeight = this.mInitHeight;
            Animation anim = new Animation() {
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    HfCollapsibleCalendar.this.mScrollViewBody.getLayoutParams().height = interpolatedTime == 1.0F ? -2 : currentHeight - (int)((float)(currentHeight - targetHeight) * interpolatedTime);
                    HfCollapsibleCalendar.this.mScrollViewBody.requestLayout();
                    if (interpolatedTime == 1.0F) {
                        HfCollapsibleCalendar.this.setState(0);
                        HfCollapsibleCalendar.this.mBtnPrevMonth.setClickable(true);
                        HfCollapsibleCalendar.this.mBtnNextMonth.setClickable(true);
                    }

                }
            };
            anim.setDuration((long)0);
            this.startAnimation(anim);
        }

        this.expandIconView.setState(ExpandIconView.LESS, true);
    }

    public void select(HfDay day) {
        Log.d("Calendar", "select");
        this.setSelectedItem(new HfDay(day.getYear(), day.getMonth(), day.getDay(), day.getResult()));
        //this.redraw();
        this.redrawFromDb();
        if (this.mListener != null) {
            this.mListener.onDaySelect();
        }

    }

    public void setSelectedDayWithResult(int position, int result) {
        this.mAdapter.setItemWithResult(position, result);
    }

    public void setHabitid(Integer habitid) {
        this.mAdapter.mHabitid = habitid;
    }

    public void setStateWithUpdateUI(int state) {
        this.setState(state);
        if (this.getState() != state) {
            this.mIsWaitingForUpdate = true;
            this.requestLayout();
        }

    }

    public void setCalendarListener(HfCollapsibleCalendar.CalendarListener listener) {
        this.mListener = listener;
    }

    public interface CalendarListener {
        void onDaySelect();

        void onItemClick(View var1);

        void onDataUpdate();

        void onMonthChange();

        void onWeekChange(int var1);
    }
}
