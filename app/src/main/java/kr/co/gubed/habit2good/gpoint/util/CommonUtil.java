package kr.co.gubed.habit2good.gpoint.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import kr.co.gubed.habit2good.gpoint.activity.NoticeActivity;
import kr.co.gubed.habit2good.gpoint.view.SupportDialog;
import kr.co.gubed.habit2good.R;

public class CommonUtil {

    public static final String SHARED_KEY = "92ebe83a34a036f7ff7da8ea05ff41cf";

    public static final String SERVER_URL = "http://a.habit2good.com/c.html";
    public static final String PROFILE_SERVER_URL = "http://a.habit2good.com/p.php";
    public static final String PROFILE_SERVER_IMAGE_URL = "http://a.habit2good.com/image/profile/";

    public static final String ACTION_LOGIN = "s";
    public static final String ACTION_JOIN = "j";
    public static final String ACTION_ACCOUNT_TRANSFER = "c";
    public static final String ACTION_SIGN_OUT = "out";
    public static final String ACTION_USER_UPDATE = "u";
    public static final String ACTION_CHECK_NICKNAME_REDUNDANCY = "cnr";
    public static final String ACTION_GET_USER = "m";
    public static final String ACTION_GET_INFO = "ni";
    public static final String ACTION_H2G_GET_INFO = "hwi";
    public static final String ACTION_H2G_PUT_PLUS1_GP = "ppg";
    public static final String ACTION_GET_HISTORYLIST = "nl";
    public static final String ACTION_GET_PURCHASELIST = "q";
    public static final String ACTION_PURCHASE_REQUEST = "p";
    public static final String ACTION_GET_STORELIST = "ql";
    public static final String ACTION_STORE_REQUEST = "pns";
    public static final String ACTION_GET_NOTICE = "t";
    public static final String ACTION_GET_MYGIFT = "g";
    public static final String ACTION_GET_NEW_NOTICELIST = "e";
    public static final String ACTION_GET_INVITELIST = "il";
    public static final String ACTION_GET_ADVERTISE = "a";
    public static final String ACTION_CPI = "cpi";
    public static final String ACTION_CPC = "cpc";
    public static final String ACTION_GET_EVENT = "el";
    public static final String ACTION_LOTTERY_REQUEST = "er";
    public static final String ACTION_EVENT_INFO_SEND = "ers";
    public static final String ACTION_MARKET_REVIEW = "mr";
    public static final String ACTION_MARKET_REVIEW_COMPLATE = "mrr";
    public static final String ACTION_POP_CLOSE = "d";
    public static final String ACTION_POP_LINKED = "f";
    public static final String ACTION_TIME_POP_LINKED = "tf";
    public static final String ACTION_NOTICE_POPUP = "np";
    public static final String ACTION_POPUP_INTENT = "pi";
    public static final String ACTION_POPUP_CASH = "pc";
    public static final String ACTION_DEVICE_TOKEN = "dt";
    public static final String ACTION_ALARM = "r";
    public static final String ACTION_ADPOP = "o";
    public static final String ACTION_VERSION = "nv";
    public static final String ACTION_EVENT = "ev";
    // habit2good 2018.10.23
    public static final String ACTION_PUT_TROPHY = "pt";
    public static final String ACTION_GET_TROPHY = "gt";
    public static final String ACTION_H2G_GET_GPOINT_RANKING = "ggr";
    public static final String ACTION_H2G_GET_TROPHY_RANKING = "gtr";

    public static final String KEY_USERID = "u";
    public static final String KEY_ACTION = "a";
    public static final String KEY_REWARD_KEY = "rk";
    public static final String KEY_REWARD_TIME = "rt";
    public static final String KEY_REWARD_INCLUDE = "ic";
    public static final String KEY_CASH = "c";
    public static final String KEY_ONOFF = "nf";
    public static final String KEY_TIMESTAMP = "t";
    public static final String KEY_RST = "rst";
    public static final String KEY_SIGN = "s";
    public static final String KEY_TRANSFER_CODE = "p";
    public static final String KEY_EMAIL = "m";
    public static final String KEY_CPID = "ci";
    public static final String KEY_NICKNAME = "ni";
    public static final String KEY_PAGE = "p";
    public static final String KEY_LOCALE = "n";
    public static final String KEY_BOARDTYPE = "b";
    public static final String KEY_MAX_ID = "m";
    public static final String KEY_GIFT_MAX_ID = "mi";
    public static final String KEY_ALARM = "m";
    public static final String KEY_HANDPHONE = "h";
    public static final String KEY_BANK= "b";
    public static final String KEY_ACCOUNT= "ac";
    public static final String KEY_NAME= "v";
    public static final String KEY_VERSION = "v";
    public static final String KEY_GOLDPER= "gp";
    public static final String KEY_INPUT1 = "ip1";
    public static final String KEY_INPUT2 = "ip2";
    public static final String KEY_INPUT3 = "ip3";
    public static final String KEY_INPUT4 = "ip4";
    public static final String KEY_ADID = "d";
    public static final String KEY_DEVICE_TOKEN = "dt";
    public static final String KEY_PHONE_NM = "pnm";
    public static final String KEY_YEAR = "y";
    public static final String KEY_LOCATION = "l";
    public static final String KEY_GENDER = "g";
    public static final String KEY_MARRIAGE = "i";
    public static final String KEY_PARTNERCODE = "f";
    public static final String KEY_PURCHASE_CODE = "p";
    public static final String KEY_STORE_ID = "si";
    public static final String RESULT_ERROR = "e";
    public static final String KEY_LOTTERY_ID = "li";
    public static final String KEY_IMEI = "mei";
    public static final String KEY_PACKAGE_NAME = "pn";
    public static final String KEY_ADNO = "an";
    public static final String KEY_LOTTERY_KEY = "lk";
    // habit2good 2018.10.24
    public static final String KEY_REWARD_TROPHY = "rt";
    public static final String KEY_HABIT_ID = "hi";
    public static final String KEY_RANKING_START = "rs";
    public static final String KEY_RANKING_END = "re";
    public static final String KEY_TABLE_NAME = "tn";

    public static final String RESULT_ACTION = "a";
    public static final String RESULT_USERID = "u";
    public static final String RESULT_CPID = "ci";
    public static final String RESULT_INVITE = "iv";
    public static final String RESULT_YEAR = "y";
    public static final String RESULT_LOCATION = "l";
    public static final String RESULT_GENDER = "g";
    public static final String RESULT_NICKNAME = "ni";
    public static final String RESULT_MARRIAGE = "i";
    public static final String RESULT_PARTNERCODE = "f";
    public static final String RESULT_REVIEW = "r";
    public static final String RESULT_MISSION = "h";
    public static final String RESULT_ALARM = "m";
    public static final String RESULT_RESULT = "r";
    public static final String RESULT_EMAIL = "m";
    public static final String RESULT_BUDGET = "b";
    public static final String RESULT_LINKED_GOLD = "lg";
    public static final String RESULT_BALANCE_GPOINT = "ng";
    public static final String RESULT_STORE_GOLD = "pg";
    public static final String RESULT_COIN = "uc";
    public static final String RESULT_NATION = "n";
    public static final String RESULT_PURCHASE = "p";
    public static final String RESULT_CORP = "c";
    public static final String RESULT_COMPLETE_TITLE = "ct";
    public static final String RESULT_COMPLETE_DESC = "cd";
    public static final String RESULT_RECOMMEND_1 = "ri1";
    public static final String RESULT_RECOMMEND_2 = "ri2";
    public static final String RESULT_DATE = "t";
    public static final String RESULT_INVITED_PARTNERS = "n";
    public static final String RESULT_PARTNER_GPOINT = "p";
    public static final String RESULT_REWARD_GPOINT = "sq";
    public static final String RESULT_MSG_VERSION = "msgv";

    public static final String RESULT_VERSION = "v";
    public static final String RESULT_PER = "p";
    public static final String RESULT_EVENT = "ev";
    public static final String RESULT_EVENT_MISSION = "en";
    public static final String RESULT_NOTICE_POPUP_TITLE = "nt";
    public static final String RESULT_NOTICE_POPUP = "np";
    public static final String RESULT_MILEAGE_BACK = "mb";
    public static final String RESULT_MILEAGE_BACK_PER = "lbp";
    public static final String RESULT_ERROR_TYPE = "et";

    public static final String RESULT_H2G_MYPOINT = "mp";
    public static final String RESULT_H2G_TROPHY = "tr";
    public static final String RESULT_H2G_PARTNER_1_COUNT = "p1c";
    public static final String RESULT_H2G_PARTNER_2_COUNT = "p2c";
    public static final String RESULT_H2G_PARTNER_3_COUNT = "p3c";
    public static final String RESULT_H2G_PARTNER_4_COUNT = "p4c";
    public static final String RESULT_H2G_PARTNER_5_COUNT = "p5c";
    public static final String RESULT_H2G_PARTNER_6_COUNT = "p6c";
    public static final String RESULT_H2G_PARTNER_1_POINT = "p1p";
    public static final String RESULT_H2G_PARTNER_2_POINT = "p2p";
    public static final String RESULT_H2G_PARTNER_3_POINT = "p3p";
    public static final String RESULT_H2G_PARTNER_4_POINT = "p4p";
    public static final String RESULT_H2G_PARTNER_5_POINT = "p5p";
    public static final String RESULT_H2G_PARTNER_6_POINT = "p6p";
    public static final String RESULT_H2G_NOTI_SLIDE_1_NAME = "s1n";
    public static final String RESULT_H2G_NOTI_SLIDE_1_IMAGE_URL = "s1i";
    public static final String RESULT_H2G_NOTI_SLIDE_1_LINK_URL = "s1l";
    public static final String RESULT_H2G_NOTI_SLIDE_2_NAME = "s2n";
    public static final String RESULT_H2G_NOTI_SLIDE_2_IMAGE_URL = "s2i";
    public static final String RESULT_H2G_NOTI_SLIDE_2_LINK_URL = "s2l";
    public static final String RESULT_H2G_NOTI_SLIDE_3_NAME = "s3n";
    public static final String RESULT_H2G_NOTI_SLIDE_3_IMAGE_URL = "s3i";
    public static final String RESULT_H2G_NOTI_SLIDE_3_LINK_URL = "s3l";
    public static final String RESULT_H2G_NOTI_SLIDE_4_NAME = "s4n";
    public static final String RESULT_H2G_NOTI_SLIDE_4_IMAGE_URL = "s4i";
    public static final String RESULT_H2G_NOTI_SLIDE_4_LINK_URL = "s4l";
    public static final String RESULT_PLUS1_TIMER = "p1";

    public static final String ERROR_VARIABLE = "variable";
    public static final String ERROR_SIGN = "sign";
    public static final String ERROR_FAIL = "fail";
    public static final String ERROR_EXIST_DEVICE = "exist_device";
    public static final String ERROR_EXIST_USER = "exist_user";
    public static final String ERROR_NO_USER = "no_user";
    public static final String ERROR_NO_AD = "no_ad";
    public static final String ERROR_USIM = "error_usim";
    public static final String ERROR_NO_MINE = "no_mine";
    public static final String ERROR_OTHER_ADID = "other_adid";
    public static final String ERROR_EXIST_PARTNER_CODE = "exist_partner_code";
    public static final String ERROR_NO_PARTNER = "no_partner";
    public static final String ERROR_NOT_ENOUGH_PARTNER = "not_enough_partner";
    public static final String ERROR_WRONG_USER = "wrong_user";
    public static final String ERROR_NO_BUDGET = "no_budget";
    public static final String ERROR_LIMIT = "limit";
    public static final String ERROR_NO_PURCHASE = "no_purchase";
    public static final String ERROR_ERROR_PURCHASE = "error_purchase";
    public static final String ERROR_INPURT_TRANSFER_CODE = "input_transfer_code";
    public static final String ERROR_TRANSFER_CODE = "correct_transfer_code";
    public static final String ERROR_REWARD = "error_reward";
    public static final String ERROR_MAX_REWARD = "max_reward";
    public static final String ERROR_NO_BONUS = "no_bonus";
    public static final String ERROR_NO_TURN = "no_turn";
    public static final String ERROR_NO_LOTTERY = "no_lottery";
    public static final String ERROR_LOTTERY_LIMIT = "lottery_limit";
    public static final String ERROR_LOTTERY_BEFORE = "lottery_before";
    public static final String ERROR_LOTTERY_MORE = "lottery_more";
    public static final String ERROR_LOTTERY_MORE_TIME = "lottery_more_time";
    public static final String ERROR_VERSION = "error_version";

    public static final int ACTIVITY_RESULT_HISTORY = 1;
    public static final int ACTIVITY_RESULT_PROFILE = ACTIVITY_RESULT_HISTORY + 1;
    public static final int ACTIVITY_RESULT_NOTICE = ACTIVITY_RESULT_PROFILE + 1;
    public static final int ACTIVITY_RESULT_FAQ = ACTIVITY_RESULT_NOTICE + 1;
    public static final int ACTIVITY_RESULT_INVITE_PARTNER = ACTIVITY_RESULT_FAQ + 1;


    public static final String AD_TYPE_EVENT = "ad_event";
    public static final String AD_TYPE_TCASH = "ad_tcash";
    public static final String AD_TYPE_ADNETWORK = "ad_network";
    public static final String AD_TYPE_OFFER = "ad_offer";

    public static final String OFFER_ADPOPCORN = "adpopcorn";
    public static final String OFFER_ADPOPCORN_TITLE = "adPOPcorn";
    public static final String OFFER_TNKAD = "tnkad";
    public static final String OFFER_TNKAD_TITLE = "TNK";
    public static final String OFFER_NAS = "nas";
    public static final String OFFER_NAS_TITLE = "NAS";
    public static final String OFFER_ADSYNC = "adsync";
    public static final String OFFER_ADSYNC_TITLE = "adSync";
    public static final String NATIVE_ADMOB = "admob";
    public static final String OFFER_MOBVISTA = "mobvista";
    public static final String OFFER_MOBVISTA_TITLE = "Mobvista";

    public static final String OFFER_BUZZVILL = "buzzvill";
    public static final String OFFER_BUZZVILL_TITLE = "buzzvill";

    public static final String OFFER_APPALL = "appall";
    public static final String OFFER_APPALL_TITLE = "appall";

    public static final String cacheName = "TCASH";
    public static final String cacheNameNotice = "NoticeCashPop";
    public static final String cacheNameInvite = "InviteCashPop";
    public static final String cacheNameHistory = "HistoryCashPop";
    public static final String noticeCache = "noticeCache";
    public static final String giftboxCache = "giftboxCache";
    public static final String inviteCache = "inviteCache";
    public static final String adCache = "adCache";
    public static final String eventCache = "eventCache";
    public static final String storeCache = "storeCache";
    public static final String storeNewCache = "storeNewCache";
    public static final String noticePopCache = "noticePopCache";

    public static final int TOAST_YOFFSET = 320;

    public static final String CHANNEL_GROUP_HABIT = "습관";
    public static final String CHANNEL_GROUP_HABIT_ID = "h2g_group_habit";
    public static final String CHANNEL_NAME_HABIT = "습관 알람";
    public static final String CHANNEL_ID_HABIT = "h2g_habit_alarm";
    public static final String CHANNEL_NAME_GOOD_SAYING = "해빗투굿 생각";
    public static final String CHANNEL_ID_GOOD_SAYING = "h2g_good_saying";
    public static final String CHANNEL_NAME_NOTICE = "공지";
    public static final String CHANNEL_ID_NOTICE = "h2g_notice";
    public static final String CHANNEL_GROUP_GPOINT = "지포인트";
    public static final String CHANNEL_GROUP_GPOINT_ID = "h2g_group_gpoint";
    public static final String CHANNEL_NAME_REWARD_GPOINT = "지포인트 통지";
    public static final String CHANNEL_ID_REWARD_GPOINT = "h2g_reward_gpoint";
    public static final String CHANNEL_NAME_REWARD_TROPHY = "트로피 통지";
    public static final String CHANNEL_ID_REWARD_TROPHY = "h2g_reward_trophy";
    public static final String CHANNEL_NAME_PURCHASE = "구매 통지";
    public static final String CHANNEL_ID_PURCHASE = "h2g_purchase";

    public static final String INTENT_TYPE_HABIT = "habit";
    public static final String INTENT_TYPE_REWARD = "reward";
    public static final String INTENT_TYPE_PURCHASE = "purchase";
    public static final String INTENT_TYPE_GIFT = "gift";
    public static final String INTENT_TYPE_TROPHY = "trophy";
    public static final String INTENT_TYPE_GOOD_SAYING = "goodsaying";

    public static final String CLICK_ACTION_REWARD = "H2G_REWARD";
    public static final String CLICK_ACTION_TROPHY = "H2G_TROPHY";
    public static final String CLICK_ACTION_PURCHASE = "H2G_PURCHASE";
    public static final String CLICK_ACTION_GIFT = "H2G_GIFT";
    public static final String CLICK_ACTION_GOODSAYING = "H2G_GOODSAYING";
    public static final String CLICK_ACTION_HABIT = "H2G_HABIT";
    public static final String CLICK_ACTION_NOTICE = "H2G_NOTICE";

    public static final String EXTRA_NOTICE_CONTENTS = "notice_contents";

    public static final int REQUEST_CODE_SIGN_IN_FOR_BACKUP = 0;
    public static final int REQUEST_CODE_SIGN_IN_FOR_RESTORE = 1;
    public static final int REQUEST_CODE_HABIT_UPDATE = 100;
    public static final int REQUEST_CODE_HABIT_ADD = 101;
    public static final int REQUEST_CODE_HABIT_REMINDER = 200;
    public static final int REQUEST_CODE_UPDATE_MEMO = 300;

    public static final int CRITERIA_DAY = 1;
    public static final int CRITERIA_MONTH = 2;


    public static HashMap getExcludePackages(){
        HashMap hashMap = new HashMap();
        hashMap.put("com.android.settings","com.android.settings");
        hashMap.put("net.cashpop.id","net.cashpop.id");
//        hashMap.put("com.google.android.youtube","com.google.android.youtube");
        return hashMap;
    }

    public static String setComma(String number, boolean type, boolean isPlus){
        try {
            if( number == null || number.equals("")){
                return "";
            }
            NumberFormat nf = NumberFormat.getInstance();
            String pStr = "";
            if( type) {
                //number = new DecimalFormat("##.#").format(Double.parseDouble(number)).replace(".0","");
                number = new DecimalFormat("##").format(Double.parseDouble(number)).replace(".0","");
                if (Double.parseDouble(number) > 0 && isPlus) {
                    pStr = "+";
                } else {
                    pStr = "";
                }
                return pStr + nf.format(Double.parseDouble(number));
            }else{
                if (Integer.parseInt(number) > 0 && isPlus) {
                    pStr = "+";
                } else {
                    pStr = "";
                }
                return pStr + nf.format(Integer.parseInt(number));
            }
        }catch (Exception ignore){
            ignore.printStackTrace();
            return number;
        }
    }

    public static String replaceStr(String title){
        return title.replaceAll("_"," ");
    }

    public static boolean isPlus(String cash){
        if( cash== null || cash.equals("")){
            return false;
        }
        double rCash = Double.parseDouble(cash);
        return rCash > 0;
    }

    public static boolean isEmailValid(CharSequence email) {

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static Intent Support(Context context, String subject, String content, String chooser){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.setData(Uri.parse("mailto:"+context.getResources().getString(R.string.support_mail)));
        Intent.createChooser(intent, chooser);
        return intent;
    }

    public static String getDateTime(String timeStamp){
        Date date = new Date(Long.parseLong(timeStamp)*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
        return sdf.format(date);
    }

    public static String getDateTimeHistory(String timeStamp){
        Date date = new Date(Long.parseLong(timeStamp)*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm");
        return sdf.format(date);
    }

    public static long getDateTime(long timeStamp){
        Date date = new Date(timeStamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return Long.parseLong(sdf.format(date));
    }

    public static long newDate(){
        int newDate = 7;
        Date newday = new Date();
        newday.setTime(new Date().getTime()-((long)1000*60*60*24*newDate));
        return newday.getTime()/1000;
    }
    public static InputFilter spaceFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            Pattern ps = Pattern.compile(" ");
            if( ps.matcher(charSequence).matches()){
                return "";
            }
            return null;
        }
    };

    public static List sortByValue(final HashMap map){
        List<String> list = new ArrayList();
        list.addAll(map.keySet());

        Collections.sort(list,new Comparator(){

            public int compare(Object o1,Object o2){
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);
                return ((Comparable) v1).compareTo(v2);
            }

        });
        Collections.reverse(list);
        return list;
    }

    public static String addSlashes(String s) {
        s = s.replaceAll("\\\\", "\\\\\\\\");
        s = s.replaceAll("\\n", "\\\\n");
        s = s.replaceAll("\\r", "\\\\r");
        s = s.replaceAll("\\00", "\\\\0");
        s = s.replaceAll("'", "\\\\'");
        return s;
    }

    public static String getVersion(Context context){
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info.versionName;
        return version;
    }

    public static int getVersionCode(Context context){
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version = info.versionCode;
        return version;
    }

    public static boolean isPackageExist(Context context, String packageName){
        try{
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            ApplicationInfo appInfo = pi.applicationInfo;
            if( appInfo.packageName.equals(packageName)){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }

    }

    public static void showSupport(final Context context, boolean isFaq){
        final SupportDialog supportDialog = new SupportDialog(context);

        View.OnClickListener faqListener = null;
        if( isFaq){
            faqListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        context.startActivity(new Intent(context, NoticeActivity.class).putExtra(CommonUtil.KEY_BOARDTYPE, "2"));
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }catch (Exception ignore){
                        Toast toast = Toast.makeText(context, context.getResources().getString(R.string.ist_not_supported), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                        toast.show();
                    }
                    supportDialog.dismiss();
                }
            };
        }

        supportDialog.setListener(faqListener, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    context.startActivity(CommonUtil.Support(
                            context,
                            context.getResources().getString(R.string.email_title_reward_problem, Applications.preference.getValue(Preference.CPID, context.getResources().getString(R.string.no_id))),
                            context.getResources().getString(R.string.email_desc_reward_problem, Applications.preference.getValue(Preference.CPID, context.getResources().getString(R.string.input_your_id)), CommonUtil.getVersion(context), Build.MODEL),
                            context.getResources().getString(R.string.question))
                    );
                }catch (Exception ignore){
                    Toast toast = Toast.makeText(context, context.getResources().getString(R.string.ist_not_supported), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                    toast.show();
                }
                supportDialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    context.startActivity(CommonUtil.Support(
                            context,
                            context.getResources().getString(R.string.email_title_store_problem, Applications.preference.getValue(Preference.CPID, context.getResources().getString(R.string.no_id))),
                            context.getResources().getString(R.string.email_desc_store_problem, Applications.preference.getValue(Preference.CPID, context.getResources().getString(R.string.input_your_id)), CommonUtil.getVersion(context), Build.MODEL),
                            context.getResources().getString(R.string.question))
                    );
                }catch (Exception ignore){
                    Toast toast = Toast.makeText(context, context.getResources().getString(R.string.ist_not_supported), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                    toast.show();
                }
                supportDialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    context.startActivity(CommonUtil.Support(
                            context,
                            context.getResources().getString(R.string.email_title_etc, Applications.preference.getValue(Preference.CPID, context.getResources().getString(R.string.no_id))),
                            context.getResources().getString(R.string.email_desc_etc, Applications.preference.getValue(Preference.CPID, context.getResources().getString(R.string.input_your_id)), CommonUtil.getVersion(context), Build.MODEL),
                            context.getResources().getString(R.string.question)));
                }catch (Exception ignore){
                    Toast toast = Toast.makeText(context, context.getResources().getString(R.string.ist_not_supported), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                    toast.show();
                }
                supportDialog.dismiss();
            }
        });
        supportDialog.show();
    }
}
