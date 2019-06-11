package kr.co.gubed.habit2goodpure;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.Objects;

@SuppressLint("ValidFragment")
public class MyDatePicker extends AppCompatDialogFragment {
    Context context;
    private Builder mBuilder;
    private OnDatePickListener mOnDatePickListener;

    private android.widget.DatePicker mDatePicker;
    private View mRoot;

    public MyDatePicker() {
    }


    private MyDatePicker(Builder mBuilder) {
        this.mBuilder = mBuilder;
    }

/*
    public MyDatePicker(Builder builder) {
        mBuilder = builder;
    }
    */

    public interface OnDatePickListener {
        void onDatePick(Calendar calendar);
    }

    public void show(FragmentManager fragmentManager, OnDatePickListener onDatePickListener) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(this, getTag());
        ft.commitAllowingStateLoss();

        mOnDatePickListener = onDatePickListener;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        setupDialog(dialog);
        setupLayout(dialog);
        setupDatePicker();
        setupButtonEvents();
    }

    /**
     * 확인, 취소 버튼 세팅
     */
    private void setupButtonEvents() {
        mRoot.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, mDatePicker.getYear());
                calendar.set(Calendar.MONTH, mDatePicker.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
                mOnDatePickListener.onDatePick(calendar);
                dismiss();
            }
        });
        mRoot.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 데이트피커 세팅
     */
    private void setupDatePicker() {
        mDatePicker = mRoot.findViewById(R.id.datePicker);
        mDatePicker.setMinDate(mBuilder.minDate);
        mDatePicker.updateDate(mBuilder.year, mBuilder.month, mBuilder.day);
    }

    /**
     * 레이아웃 세팅
     */
    private void setupLayout(Dialog dialog) {
        mRoot = LayoutInflater.from(mBuilder.mContext).inflate(R.layout.date_picker, null, false);
        dialog.setContentView(mRoot);
    }

    /**
     * 다이얼로그 세팅
     *
     * @param dialog
     */
    private void setupDialog(Dialog dialog) {
        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static class Builder {
        final Context mContext;
        int year;
        int month;
        int day;
        long minDate;

        public Builder(Context context) {
            this.mContext = context;
        }

        public MyDatePicker create() {
            MyDatePicker picker = new MyDatePicker(this);
            return picker;
        }

        void setYear(int year) {
            this.year = year;
        }

        void setMonth(int month) {
            this.month = month;
        }

        void setDay(int day) {
            this.day = day;
        }

        void setMinDate(long minDate) {
            this.minDate = minDate;
        }

        public Builder setDate(Calendar calendar) {
            setYear(calendar.get(Calendar.YEAR));
            setMonth(calendar.get(Calendar.MONTH));
            setDay(calendar.get(Calendar.DAY_OF_MONTH));

            return this;
        }
    }
}
