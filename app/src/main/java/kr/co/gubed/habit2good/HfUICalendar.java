package kr.co.gubed.habit2good;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.shrikanthravi.collapsiblecalendarview.view.ExpandIconView;
import com.shrikanthravi.collapsiblecalendarview.view.LockScrollView;

public abstract class HfUICalendar extends LinearLayout {
    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;
    public static final int STATE_EXPANDED = 0;
    public static final int STATE_COLLAPSED = 1;
    public static final int STATE_PROCESSING = 2;

    private final int RESULT_DONE=0;
    private final int RESULT_FAIL=1;
    private final int RESULT_SKIP=2;
    final int RESULT_DELETE=3;
    private final int RESULT_NOTE=4;
    final int RESULT_DEFAULT=5;

    Context mContext;
    LayoutInflater mInflater;
    private LinearLayout mLayoutRoot;
    TextView mTxtTitle;
    TableLayout mTableHead;
    LockScrollView mScrollViewBody;
    TableLayout mTableBody;
    RelativeLayout mLayoutBtnGroupMonth;
    RelativeLayout mLayoutBtnGroupWeek;
    ImageView mBtnPrevMonth;
    ImageView mBtnNextMonth;
    ImageView mBtnPrevWeek;
    ImageView mBtnNextWeek;
    ExpandIconView expandIconView;
    private boolean mShowWeek;
    private int mFirstDayOfWeek;
    private int mState;
    private int mTextColor;
    private int mPrimaryColor;
    private int mTodayItemTextColor;
    private Drawable mTodayItemBackgroundDrawable;
    private int mSelectedItemTextColor;
    private Drawable mSelectedItemBackgroundDrawable;
    private Drawable mButtonLeftDrawable;
    private Drawable mButtonRightDrawable;
    private HfDay mSelectedItem;
    private int mButtonLeftDrawableTintColor;
    private int mButtonRightDrawableTintColor;
    private int mExpandIconColor;

    public HfUICalendar(Context context) {
        this(context, null);
    }

    public HfUICalendar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HfUICalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mShowWeek = true;
        this.mFirstDayOfWeek = 0;
        this.mState = 1;
        this.mTextColor = -16777216;
        this.mPrimaryColor = -1;
        this.mTodayItemTextColor = -16777216;
        this.mTodayItemBackgroundDrawable = this.getResources().getDrawable(com.shrikanthravi.collapsiblecalendarview.R.drawable.circle_black_stroke_background);
        this.mSelectedItemTextColor = -1;
        this.mSelectedItemBackgroundDrawable = this.getResources().getDrawable(com.shrikanthravi.collapsiblecalendarview.R.drawable.circle_black_solid_background);
        this.mButtonLeftDrawable = this.getResources().getDrawable(com.shrikanthravi.collapsiblecalendarview.R.drawable.left_icon);
        this.mButtonRightDrawable = this.getResources().getDrawable(com.shrikanthravi.collapsiblecalendarview.R.drawable.right_icon);
        this.mSelectedItem = null;
        this.mButtonLeftDrawableTintColor = -16777216;
        this.mButtonRightDrawableTintColor = -16777216;
        this.mExpandIconColor = -16777216;
        this.init(context);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar, defStyleAttr, 0);
        this.setAttributes(attributes);
        attributes.recycle();
    }

    protected abstract void redraw();

    protected abstract void reload();

    void init(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
        View rootView = this.mInflater.inflate(com.shrikanthravi.collapsiblecalendarview.R.layout.widget_collapsible_calendarview, this, true);
        this.mLayoutRoot = rootView.findViewById(com.shrikanthravi.collapsiblecalendarview.R.id.layout_root);
        this.mTxtTitle = rootView.findViewById(com.shrikanthravi.collapsiblecalendarview.R.id.txt_title);
        this.mTableHead = rootView.findViewById(com.shrikanthravi.collapsiblecalendarview.R.id.table_head);
        this.mScrollViewBody = rootView.findViewById(com.shrikanthravi.collapsiblecalendarview.R.id.scroll_view_body);
        this.mTableBody = rootView.findViewById(com.shrikanthravi.collapsiblecalendarview.R.id.table_body);
        this.mLayoutBtnGroupMonth = rootView.findViewById(com.shrikanthravi.collapsiblecalendarview.R.id.layout_btn_group_month);
        this.mLayoutBtnGroupWeek = rootView.findViewById(com.shrikanthravi.collapsiblecalendarview.R.id.layout_btn_group_week);
        this.mBtnPrevMonth = rootView.findViewById(com.shrikanthravi.collapsiblecalendarview.R.id.btn_prev_month);
        this.mBtnNextMonth = rootView.findViewById(com.shrikanthravi.collapsiblecalendarview.R.id.btn_next_month);
        this.mBtnPrevWeek = rootView.findViewById(com.shrikanthravi.collapsiblecalendarview.R.id.btn_prev_week);
        this.mBtnNextWeek = rootView.findViewById(com.shrikanthravi.collapsiblecalendarview.R.id.btn_next_week);
        this.expandIconView = rootView.findViewById(com.shrikanthravi.collapsiblecalendarview.R.id.expandIcon);
    }

    private void setAttributes(TypedArray attrs) {
        this.setShowWeek(attrs.getBoolean(com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar_showWeek, this.mShowWeek));
        this.setFirstDayOfWeek(attrs.getInt(com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar_firstDayOfWeek, this.mFirstDayOfWeek));
        this.setState(attrs.getInt(com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar_state, this.mState));
        this.setTextColor(attrs.getColor(com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar_textColor, this.mTextColor));
        this.setPrimaryColor(attrs.getColor(com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar_primaryColor, this.mPrimaryColor));
        this.setTodayItemTextColor(attrs.getColor(com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar_todayItem_textColor, this.mTodayItemTextColor));
        Drawable todayItemBackgroundDrawable = attrs.getDrawable(com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar_todayItem_background);
        if (todayItemBackgroundDrawable != null) {
            this.setTodayItemBackgroundDrawable(todayItemBackgroundDrawable);
        } else {
            this.setTodayItemBackgroundDrawable(this.mTodayItemBackgroundDrawable);
        }

        this.setSelectedItemTextColor(attrs.getColor(com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar_selectedItem_textColor, this.mSelectedItemTextColor));
        Drawable selectedItemBackgroundDrawable = attrs.getDrawable(com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar_selectedItem_background);
        if (selectedItemBackgroundDrawable != null) {
            this.setSelectedItemBackgroundDrawable(selectedItemBackgroundDrawable);
        } else {
            this.setSelectedItemBackgroundDrawable(this.mSelectedItemBackgroundDrawable);
        }

        Drawable buttonLeftDrawable = attrs.getDrawable(com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar_buttonLeft_drawable);
        if (buttonLeftDrawable != null) {
            this.setButtonLeftDrawable(buttonLeftDrawable);
        } else {
            this.setButtonLeftDrawable(this.mButtonLeftDrawable);
        }

        Drawable buttonRightDrawable = attrs.getDrawable(com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar_buttonRight_drawable);
        if (buttonRightDrawable != null) {
            this.setButtonRightDrawable(buttonRightDrawable);
        } else {
            this.setButtonRightDrawable(this.mButtonRightDrawable);
        }

        this.setButtonLeftDrawableTintColor(attrs.getColor(com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar_buttonLeft_drawableTintColor, this.mButtonLeftDrawableTintColor));
        this.setButtonRightDrawableTintColor(attrs.getColor(com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar_buttonRight_drawableTintColor, this.mButtonRightDrawableTintColor));
        this.setExpandIconColor(attrs.getColor(com.shrikanthravi.collapsiblecalendarview.R.styleable.UICalendar_expandIconColor, this.mExpandIconColor));
        HfDay selectedItem = null;
    }

    private void setButtonLeftDrawableTintColor(int color) {
        this.mButtonLeftDrawableTintColor = color;
        this.mBtnPrevMonth.getDrawable().setColorFilter(color, Mode.SRC_ATOP);
        this.mBtnPrevWeek.getDrawable().setColorFilter(color, Mode.SRC_ATOP);
        this.redraw();
    }

    private void setButtonRightDrawableTintColor(int color) {
        this.mButtonRightDrawableTintColor = color;
        this.mBtnNextMonth.getDrawable().setColorFilter(color, Mode.SRC_ATOP);
        this.mBtnNextWeek.getDrawable().setColorFilter(color, Mode.SRC_ATOP);
        this.redraw();
    }

    private void setExpandIconColor(int color) {
        this.mExpandIconColor = color;
        this.expandIconView.setColor(color);
    }

    public boolean isShowWeek() {
        return this.mShowWeek;
    }

    private void setShowWeek(boolean showWeek) {
        this.mShowWeek = showWeek;
        if (showWeek) {
            this.mTableHead.setVisibility(VISIBLE);
        } else {
            this.mTableHead.setVisibility(GONE);
        }

    }

    int getFirstDayOfWeek() {
        return this.mFirstDayOfWeek;
    }

    private void setFirstDayOfWeek(int firstDayOfWeek) {
        this.mFirstDayOfWeek = firstDayOfWeek;
        this.reload();
    }

    int getState() {
        return this.mState;
    }

    void setState(int state) {
        this.mState = state;
        if (this.mState == 0) {
            this.mLayoutBtnGroupMonth.setVisibility(VISIBLE);
            this.mLayoutBtnGroupWeek.setVisibility(GONE);
        }

        if (this.mState == 1) {
            this.mLayoutBtnGroupMonth.setVisibility(GONE);
            this.mLayoutBtnGroupWeek.setVisibility(VISIBLE);
        }

    }

    int getTextColor() {
        return this.mTextColor;
    }

    private void setTextColor(int textColor) {
        this.mTextColor = textColor;
        this.redraw();
        this.mTxtTitle.setTextColor(this.mTextColor);
    }

    public int getPrimaryColor() {
        return this.mPrimaryColor;
    }

    private void setPrimaryColor(int primaryColor) {
        this.mPrimaryColor = primaryColor;
        this.redraw();
        this.mLayoutRoot.setBackgroundColor(this.mPrimaryColor);
    }

    public int getTodayItemTextColor() {
        return this.mTodayItemTextColor;
    }

    private void setTodayItemTextColor(int todayItemTextColor) {
        this.mTodayItemTextColor = todayItemTextColor;
        this.redraw();
    }

    public Drawable getTodayItemBackgroundDrawable() {
        return this.mTodayItemBackgroundDrawable;
    }

    private void setTodayItemBackgroundDrawable(Drawable todayItemBackgroundDrawable) {
        this.mTodayItemBackgroundDrawable = todayItemBackgroundDrawable;
        this.redraw();
    }

    int getSelectedItemTextColor() {
        return this.mSelectedItemTextColor;
    }

    private void setSelectedItemTextColor(int selectedItemTextColor) {
        this.mSelectedItemTextColor = selectedItemTextColor;
        this.redraw();
    }

    Drawable getSelectedItemBackgroundDrawable(int index) {
        switch (index) {
            case RESULT_DONE:
                this.mSelectedItemBackgroundDrawable = this.getResources().getDrawable(R.drawable.circle_green_solid_background);
                break;
            case RESULT_FAIL:
                this.mSelectedItemBackgroundDrawable = this.getResources().getDrawable(R.drawable.circle_red_solid_background);
                break;
            case RESULT_SKIP:
                this.mSelectedItemBackgroundDrawable = this.getResources().getDrawable(R.drawable.circle_blue_solid_background);
                break;
            case RESULT_DELETE:
                this.mSelectedItemBackgroundDrawable = this.getResources().getDrawable(R.drawable.circle_black_solid_background);
                break;
            case RESULT_NOTE:
                this.mSelectedItemBackgroundDrawable = this.getResources().getDrawable(R.drawable.circle_black_solid_background);
                break;
            default:
                this.mSelectedItemBackgroundDrawable = this.getResources().getDrawable(R.drawable.circle_gray_solid_background);
                break;
        }
        return this.mSelectedItemBackgroundDrawable;
    }

    private void setSelectedItemBackgroundDrawable(Drawable selectedItemBackground) {
        this.mSelectedItemBackgroundDrawable = selectedItemBackground;
        this.redraw();
    }

    public Drawable getButtonLeftDrawable() {
        return this.mButtonLeftDrawable;
    }

    private void setButtonLeftDrawable(Drawable buttonLeftDrawable) {
        this.mButtonLeftDrawable = buttonLeftDrawable;
        this.mBtnPrevMonth.setImageDrawable(buttonLeftDrawable);
        this.mBtnPrevWeek.setImageDrawable(buttonLeftDrawable);
    }

    public Drawable getButtonRightDrawable() {
        return this.mButtonRightDrawable;
    }

    private void setButtonRightDrawable(Drawable buttonRightDrawable) {
        this.mButtonRightDrawable = buttonRightDrawable;
        this.mBtnNextMonth.setImageDrawable(buttonRightDrawable);
        this.mBtnNextWeek.setImageDrawable(buttonRightDrawable);
    }

    HfDay getSelectedItem() {
        return this.mSelectedItem;
    }

    void setSelectedItem(HfDay selectedItem) {
        this.mSelectedItem = selectedItem;
    }
}
