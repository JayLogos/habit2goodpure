package kr.co.gubed.habit2goodpure;

import android.os.Parcel;
import android.os.Parcelable;

public class HfDay implements Parcelable {
    private int mYear;
    private final int mMonth;
    private int mDay;
    private int mResult;
    private boolean mIsSelected=false;

    public static final Parcelable.Creator CREATOR = new Creator() {
        public HfDay createFromParcel(Parcel in) {
            return new HfDay(in);
        }

        public HfDay[] newArray(int size) {
            return new HfDay[size];
        }
    };

    public HfDay(int year, int month, int day) {
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
    }

    public HfDay(int year, int month, int day, int result) {
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

    public boolean getIsSelected() {
        return this.mIsSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.mIsSelected = isSelected;
    }

    public int getResult() {
        return this.mResult;
    }

    public void setResult(int result) {
        this.mResult = result;
    }


    private HfDay(Parcel in) {
        int[] data = new int[3];
        in.readIntArray(data);
        this.mYear = data[0];
        this.mMonth = data[1];
        this.mYear = data[2];
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(new int[]{this.mYear, this.mMonth, this.mDay});
    }
}
