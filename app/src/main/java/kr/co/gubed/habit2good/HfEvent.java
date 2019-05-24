package kr.co.gubed.habit2good;

//import com.shrikanthravi.collapsiblecalendarview.data.Event;

public class HfEvent {
    private final int mYear;
    private final int mMonth;
    private final int mDay;
    private int mResult;

    public HfEvent(int year, int month, int day, int result) {
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
        this.mResult = result;
    }

    public int getMonth() {
        return this.mMonth;
    }

    public int getYear() {
        return this.mYear;
    }

    public int getDay() {
        return this.mDay;
    }

    public int getResult() {
        return this.mResult;
    }

    public void setResult(int mResult) {
        this.mResult = mResult;
    }
}
