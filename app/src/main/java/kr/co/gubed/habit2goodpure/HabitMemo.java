package kr.co.gubed.habit2goodpure;

class HabitMemo {
    private Integer habitid;
    private String selectedday;
    private String memo;

    public HabitMemo() {

    }

    public HabitMemo(Integer habitid, String selectedday, String memo) {
        this.habitid = habitid;
        this.selectedday = selectedday;
        this.memo = memo;
    }

    public Integer getHabitid() {
        return habitid;
    }

    public void setHabitid(Integer habitid) {
        this.habitid = habitid;
    }

    public String getSelectedday() {
        return selectedday;
    }

    public void setSelectedday(String selectedday) {
        this.selectedday = selectedday;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
