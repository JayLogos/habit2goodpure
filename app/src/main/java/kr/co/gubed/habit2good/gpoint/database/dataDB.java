package kr.co.gubed.habit2good.gpoint.database;

import android.provider.BaseColumns;

public class dataDB implements BaseColumns{
    public static final String ID = "id";
    public static final String SUBJECT = "subject";
    public static final String CONTENT = "content";
    public static final String B_TYPE = "b_type";
    public static final String UPDATE_DATE = "update_date";
    public static final String REGDATE = "regdate";
    public static final String ISREAD = "isRead";
    public static final String TB_NOTICE = "tb_notice";

    public static final String TB_NOTICE_CREATE =
            "create table IF NOT EXISTS " + TB_NOTICE + "("
                + ID + " integer primary key, "
                + SUBJECT + " text not null, "
                + CONTENT + " text not null, "
                + B_TYPE + " text not null, "
                + ISREAD + " text, "
                + UPDATE_DATE + " integer, "
                + REGDATE + " integer ) ";
    public static final String ISDEL = "isDel";
    public static final String TB_NEW_NOTICE = "tb_new_notice";
    public static final String TB_NEW_NOTICE_CREATE =
            "create table IF NOT EXISTS " + TB_NEW_NOTICE + "("
                    + ID + " integer primary key, "
                    + B_TYPE + " text not null, "
                    + ISREAD + " text, "
                    + ISDEL + " text, "
                    + UPDATE_DATE + " integer, "
                    + REGDATE + " integer ) ";

    public static final String CATEGORY_NO = "category_no";
    public static final String CATEGORY = "category";
    public static final String CATEGORY_ORDER = "corder";
    public static final String TB_CATEGORY = "tb_category";

    public static final String TB_CATEGORY_CREATE =
            "create table IF NOT EXISTS " + TB_CATEGORY + "("
                    + CATEGORY_NO + " text not null, "
                    + CATEGORY + " text not null, "
                    + CATEGORY_ORDER + " integer, "
                    + " UNIQUE ("+CATEGORY_NO+") ) ";

    public static final String PACKAGE_NAME = "package_name";

    public static final String TB_QUEUE = "tb_queue";
    public static final String REWARD_KEY = "reward_key";
    public static final String REWARD_GOLD = "reward_gold";

    public static final String TB_QUEUE_CREATE =
            "create table IF NOT EXISTS " + TB_QUEUE + "("
                + REWARD_KEY + " text not null, "
                + REWARD_GOLD + " integer, "
                + REGDATE + " integer, "
                + " UNIQUE ("+REWARD_KEY+") ) ";

    public static final String EXPIRE = "expire";
    public static final String TB_NOTICE_POPUP = "tb_notice_popup";

    public static final String TB_NOTICE_POPUP_CREATE =
            "create table IF NOT EXISTS " + TB_NOTICE_POPUP + "("
                + ID + " integer primary key, "
                + EXPIRE + " integer, "
                + " UNIQUE ("+ID+") ) ";
    public static final String TB_PACKAGE_CHK = "tb_package_chk";
    public static final String STATUS = "status";
    public static final String TB_PACKAGE_CHK_CREATE =
            "create table IF NOT EXISTS " + TB_PACKAGE_CHK + "("
                + PACKAGE_NAME + " text not null, "
                + STATUS + " integer not null, "
                + " UNIQUE ("+PACKAGE_NAME+") ) ";

    public static final String TB_GIFT_BOX = "tb_gift_box";
    public static final String GIFT_TITLE = "gift_title";
    public static final String GIFT_CONTENT = "gift_content";

    public static final String TB_GIFT_BOX_CREATE =
            "create table IF NOT EXISTS " + TB_GIFT_BOX + "("
                + ID + " integer primary key, "
                + GIFT_TITLE + " text not null, "
                + GIFT_CONTENT + " text not null, "
                + ISREAD + " text, "
                + REGDATE + " integer )";

    public static final String TB_ALARM = "tb_alarm";
    public static final String NOTI_ID = "a_noti";
    public static final String TITLE = "a_title";
    public static final String MESSAGE = "a_message";
    public static final String EXPIRE_TIMESTAMP = "ex_timestamp";

    public static final String TB_ALARM_CREATE =
            "create table IF NOT EXISTS " + TB_ALARM + "("
                    + NOTI_ID + " text primary key, "
                    + TITLE + " text not null, "
                    + MESSAGE + " text not null, "
                    + EXPIRE_TIMESTAMP + " text not null,"
                    + " UNIQUE ("+NOTI_ID+") ) ";

}
