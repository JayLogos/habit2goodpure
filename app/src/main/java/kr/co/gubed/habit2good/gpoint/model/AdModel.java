package kr.co.gubed.habit2good.gpoint.model;

import java.io.Serializable;

public class AdModel implements Serializable{

    private boolean isEvent;
    private boolean isEventNext;

    private boolean isTitle;
    private String title;

    private String viewType;
    private String adType;
    private String actionType;
    private String cpiType;

    private String isRun;
    private String runCnt;
    private String runStep;
    private String runReward;
    private String runCoin;
    private String runToday;

    public String getIsRun() {
        return isRun;
    }

    public void setIsRun(String isRun) {
        this.isRun = isRun;
    }

    public String getRunCnt() {
        return runCnt;
    }

    public void setRunCnt(String runCnt) {
        this.runCnt = runCnt;
    }

    public String getRunStep() {
        return runStep;
    }

    public void setRunStep(String runStep) {
        this.runStep = runStep;
    }

    public String getRunReward() {
        return runReward;
    }

    public void setRunReward(String runReward) {
        this.runReward = runReward;
    }

    public String getRunCoin() {
        return runCoin;
    }

    public void setRunCoin(String runCoin) {
        this.runCoin = runCoin;
    }

    public String getRunToday() {
        return runToday;
    }

    public void setRunToday(String runToday) {
        this.runToday = runToday;
    }

    private String name;
    private String cash;
    private String coin;
    private String task;
    private String adtxt;
    private String targetLink;
    private String package_name;
    private String image;
    private String create_date;
    private String type;
    private String adNo;
    private String code;
    private int idx;
    private String isAction;

    private String labelEnable;
    private String label;
    private String textColor;
    private String backColor;

    private String isPop;
    private String isDelayReward;

    private long expire;

    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean event) {
        isEvent = event;
    }

    public boolean isEventNext() {
        return isEventNext;
    }

    public void setEventNext(boolean eventNext) {
        isEventNext = eventNext;
    }

    public boolean getIsTitle() {
        return isTitle;
    }

    public void setIsTitle(boolean isTitle) {
        this.isTitle = isTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getCpiType() {
        return cpiType;
    }

    public void setCpiType(String cpiType) {
        this.cpiType = cpiType;
    }
    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getAdtxt() {
        return adtxt;
    }

    public void setAdtxt(String adtxt) {
        this.adtxt = adtxt;
    }

    public String getTargetLink() {
        return targetLink;
    }

    public void setTargetLink(String targetLink) {
        this.targetLink = targetLink;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdNo() {
        return adNo;
    }

    public void setAdNo(String adNo) {
        this.adNo = adNo;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getIsAction() {
        return isAction;
    }

    public void setIsAction(String isAction) {
        this.isAction = isAction;
    }

    public String getLabelEnable() {
        return labelEnable;
    }

    public void setLabelEnable(String labelEnable) {
        this.labelEnable = labelEnable;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public String getIsPop() {
        return isPop;
    }

    public void setIsPop(String isPop) {
        this.isPop = isPop;
    }

    public String getIsDelayReward() {
        return isDelayReward;
    }

    public void setIsDelayReward(String isDelayReward) {
        this.isDelayReward = isDelayReward;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }
}
