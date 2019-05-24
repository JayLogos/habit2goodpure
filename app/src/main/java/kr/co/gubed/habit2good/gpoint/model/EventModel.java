package kr.co.gubed.habit2good.gpoint.model;

public class EventModel {
    private String id;
    private String title;
    private int gold;
    private int coin;
    private String limitType;
    private int timeTerm;

    private int dayCnt;
    private int procCnt;
    private String isLimit;
    private String isAction;
    private String image;
    private String backColor;
    private String termMsg;
    private String termCnt;

    private String labelEnable;
    private String ltext;
    private String lbackColor;
    private String ltextColor;

    private int expire;

    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public int getTimeTerm() {
        return timeTerm;
    }

    public void setTimeTerm(int timeTerm) {
        this.timeTerm = timeTerm;
    }

    public String getTermCnt() {
        return termCnt;
    }

    public void setTermCnt(String termCnt) {
        this.termCnt = termCnt;
    }

    public String getTermMsg() {
        return termMsg;
    }

    public void setTermMsg(String termMsg) {
        this.termMsg = termMsg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getDayCnt() {
        return dayCnt;
    }

    public void setDayCnt(int dayCnt) {
        this.dayCnt = dayCnt;
    }

    public int getProcCnt() {
        return procCnt;
    }

    public void setProcCnt(int procCnt) {
        this.procCnt = procCnt;
    }

    public String getIsLimit() {
        return isLimit;
    }

    public void setIsLimit(String isLimit) {
        this.isLimit = isLimit;
    }

    public String getIsAction() {
        return isAction;
    }

    public void setIsAction(String isAction) {
        this.isAction = isAction;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public String getLabelEnable() {
        return labelEnable;
    }

    public void setLabelEnable(String labelEnable) {
        this.labelEnable = labelEnable;
    }

    public String getLtext() {
        return ltext;
    }

    public void setLtext(String ltext) {
        this.ltext = ltext;
    }

    public String getLbackColor() {
        return lbackColor;
    }

    public void setLbackColor(String lbackColor) {
        this.lbackColor = lbackColor;
    }

    public String getLtextColor() {
        return ltextColor;
    }

    public void setLtextColor(String ltextColor) {
        this.ltextColor = ltextColor;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }
}
