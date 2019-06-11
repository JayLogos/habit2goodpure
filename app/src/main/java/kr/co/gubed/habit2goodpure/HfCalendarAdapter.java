package kr.co.gubed.habit2goodpure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class HfCalendarAdapter {
    private int mFirstDayOfWeek = 0;
    private final Calendar mCal;
    private final LayoutInflater mInflater;
    Integer mHabitid;
    private final List<HfDay> mItemList = new ArrayList();
    private final List<View> mViewList = new ArrayList();
    private final List<HfEvent> mEventList = new ArrayList();

    public HfCalendarAdapter(Context context, Calendar cal) {
        this.mCal = (Calendar)cal.clone();
        this.mCal.set(Calendar.DATE, 1);
        this.mInflater = LayoutInflater.from(context);
        this.refresh();
    }

    public int getCount() {
        return this.mItemList.size();
    }

    public HfDay getItem(int position) {
        return this.mItemList.get(position);
    }

    public Integer getHabitid() {
        return this.mHabitid;
    }
    public void setHabitid(Integer habitid){
        this.mHabitid = habitid;
    }

    public void setItemWithResult(int position, int result){
        if (this.mItemList != null) {
            HfDay day = this.mItemList.get(position);
            day.setResult(result);
            day.setIsSelected(true);
        }
    }

    public View getView(int position) {
        return this.mViewList.get(position);
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.mFirstDayOfWeek = firstDayOfWeek;
    }

    public Calendar getCalendar() {
        return this.mCal;
    }

    /*
    public void addEvent(HfEvent event) {
        this.mEventList.add(event);
    }
    */
    public void setEventResult(int year, int month, int day, int result) {
        HfEvent event = getEvent(getEventPosition(year, month, day));

        event.setResult(result);
    }

    public void deleteEvent(int year, int month, int day) {
        int position;

        position = getEventPosition(year, month, day);
        if (position > 0) {
            this.mEventList.remove(position);
        }
    }

    private int getEventPosition(int year, int month, int day) {
        int position=-1;
        for (int i=0 ; i < mEventList.size() ; i++) {
            if (year == mEventList.get(i).getYear() && month == mEventList.get(i).getMonth() && day == mEventList.get(i).getDay()) {
                position = i;
            }
        }
        return position;
    }

    public HfEvent getEvent(int position) {
        return this.mEventList.get(position);
    }


    public void refresh() {
        this.mItemList.clear();
        this.mViewList.clear();
        this.mEventList.clear();

        int year = this.mCal.get(Calendar.YEAR);
        int month = this.mCal.get(Calendar.MONTH);
        this.mCal.set(year, month, 1);
        int lastDayOfMonth = this.mCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = this.mCal.get(Calendar.DAY_OF_WEEK) - 1;
        int offset = 0 - (firstDayOfWeek - this.mFirstDayOfWeek) + 1;
        int length = (int)Math.ceil((double)((float)(lastDayOfMonth - offset + 1) / 7.0F)) * 7;

        for(int i = offset; i < length + offset; ++i) {
            Calendar tempCal = Calendar.getInstance();
            int numYear;
            int numMonth;
            int numDay;
            if (i <= 0) {
                if (month == 0) {
                    numYear = year - 1;
                    numMonth = 11;
                } else {
                    numYear = year;
                    numMonth = month - 1;
                }

                tempCal.set(numYear, numMonth, 1);
                numDay = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH) + i;
            } else if (i > lastDayOfMonth) {
                if (month == 11) {
                    numYear = year + 1;
                    numMonth = 0;
                } else {
                    numYear = year;
                    numMonth = month + 1;
                }

                tempCal.set(numYear, numMonth, 1);
                numDay = i - lastDayOfMonth;
            } else {
                numYear = year;
                numMonth = month;
                numDay = i;
            }

            HfDay day = new HfDay(numYear, numMonth, numDay);
            //View view = this.mInflater.inflate(com.shrikanthravi.collapsiblecalendarview.R.layout.day_layout, (ViewGroup)null);
            View view = this.mInflater.inflate(R.layout.day_layout, null);
            HfEvent event = new HfEvent(numYear, numMonth, numDay, 0);
            TextView txtDay = view.findViewById(R.id.txt_day);
            ImageView imgEventTag = view.findViewById(R.id.img_event_tag);
            txtDay.setText(String.valueOf(day.getDay()));
            if (day.getMonth() != this.mCal.get(Calendar.MONTH)) {
                txtDay.setAlpha(0.3F);
            }
/*
            for(int j = 0; j < this.mEventList.size(); ++j) {
                HfEvent event = (HfEvent)this.mEventList.get(j);
                if (day.getYear() == event.getYear() && day.getMonth() == event.getMonth() && day.getDay() == event.getDay()) {
                    imgEventTag.setVisibility(View.VISIBLE);
                }
            }
*/
            imgEventTag.setVisibility(View.GONE);

            this.mItemList.add(day);
            this.mViewList.add(view);
            this.mEventList.add(event);
        }

    }
}
