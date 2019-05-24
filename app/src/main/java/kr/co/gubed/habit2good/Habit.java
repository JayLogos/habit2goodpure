package kr.co.gubed.habit2good;

public class Habit {
    private Integer habitid;
    private Integer position;
    private String hname;
    private String goalimg;
    private String goal;
    private String signal;
    private String reward;
    private String category;
    private String sdate;
    private String edate;
    private String cycle;
    private Integer count;
    private String unit;

    public Habit() {
    }

    public Habit (Integer habitid, Integer position, String hname, String goalimg, String goal, String signal, String reward, String category, String sdate, String edate, String cycle, Integer count, String unit){
        this.habitid = habitid;
        this.position = position;
        this.hname = hname;
        this.goalimg = goalimg;
        this.goal = goal;
        this.signal = signal;
        this.reward = reward;
        this.category = category;
        this.sdate = sdate;
        this.edate = edate;
        this.cycle = cycle;
        this.count = count;
        this.unit = unit;
    }

    public Integer getHabitid() {
        return habitid;
    }

    public void setHabitid(Integer habitid) {
        this.habitid = habitid;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getHname() {
        return hname;
    }

    public String getGoalimg() {
        return goalimg;
    }

    public void setGoalimg(String goalimg) {
        this.goalimg = goalimg;
    }

    public void setHname(String hname) {
        this.hname = hname;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getSignal() {
        return signal;
    }

    public String getReward() {
        return reward;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }

    public String getEdate() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
