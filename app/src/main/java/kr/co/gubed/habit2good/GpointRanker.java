package kr.co.gubed.habit2good;

public class GpointRanker {
    private Integer mRanking;
    private String mUserId;
    private Integer mGpoint;
    private Integer mTrophy;

    public GpointRanker(Integer mRanking, String mUserId, Integer mGpoint, Integer mTrophy) {
        this.mRanking = mRanking;
        this.mUserId = mUserId;
        this.mGpoint = mGpoint;
        this.mTrophy = mTrophy;
    }

    public Integer getmRanking() {
        return mRanking;
    }

    public void setmRanking(Integer mRanking) {
        this.mRanking = mRanking;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public Integer getmGpoint() {
        return mGpoint;
    }

    public void setmGpoint(Integer mGpoint) {
        this.mGpoint = mGpoint;
    }

    public Integer getmTrophy() {
        return mTrophy;
    }

    public void setmTrophy(Integer mTrophy) {
        this.mTrophy = mTrophy;
    }
}
