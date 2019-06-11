package kr.co.gubed.habit2goodpure.gpoint.model;

public class NoticeModel {
    private String id;
    private String subject;
    private String content;
    private String b_type;
    private String isRead;
    private String updateDate;
    private String regDate;
    private boolean isSelect;

    public NoticeModel(String id, String subject, String content, String b_type, String isRead, String updateDate, String regDate){
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.b_type = b_type;
        this.isRead = isRead;
        this.updateDate = updateDate;
        this.regDate = regDate;
        this.isSelect = false;
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getB_type() {
        return b_type;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public String getRegDate() {
        return regDate;
    }

    public boolean isSelect() {
        return isSelect;
    }
    public void setSelect(boolean select) {
        isSelect = select;
    }
}
