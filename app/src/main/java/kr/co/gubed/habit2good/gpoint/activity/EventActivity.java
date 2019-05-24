package kr.co.gubed.habit2good.gpoint.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import kr.co.gubed.habit2good.gpoint.adapter.EventAdapter;
import kr.co.gubed.habit2good.gpoint.filecache.ByteProviderUtil;
import kr.co.gubed.habit2good.gpoint.filecache.FileCache;
import kr.co.gubed.habit2good.gpoint.filecache.FileCacheFactory;
import kr.co.gubed.habit2good.gpoint.listener.AsyncTaskCompleteListener;
import kr.co.gubed.habit2good.gpoint.listener.EventSendInfoListener;
import kr.co.gubed.habit2good.gpoint.listener.LotteryListener;
import kr.co.gubed.habit2good.gpoint.model.EventModel;
import kr.co.gubed.habit2good.gpoint.model.NetworkErrorModel;
import kr.co.gubed.habit2good.gpoint.util.APICrypto;
import kr.co.gubed.habit2good.gpoint.util.Applications;
import kr.co.gubed.habit2good.gpoint.util.AsyncHTTPPost;
import kr.co.gubed.habit2good.gpoint.util.CommonUtil;
import kr.co.gubed.habit2good.gpoint.util.EPreference;
import kr.co.gubed.habit2good.gpoint.util.Preference;
import kr.co.gubed.habit2good.gpoint.view.CashPopDialog;
import kr.co.gubed.habit2good.gpoint.view.EventSendInfoDialog;
import kr.co.gubed.habit2good.gpoint.view.EventUseDialog;
import kr.co.gubed.habit2good.gpoint.view.LoadingDialog;
import kr.co.gubed.habit2good.gpoint.view.LotteryDialog;
import kr.co.gubed.habit2good.gpoint.view.NetworkDialog;
import kr.co.gubed.habit2good.R;

public class EventActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, AsyncTaskCompleteListener<String> {

    private String TAG = this.getClass().toString();

    private TextView tv_title;
    private LinearLayout type_admob;
    private Button btn_back;
    private Button btn_info;

    private LinearLayout layer_gold_coin;

    private ImageView iv_my_gpoint;
    private TextView tv_my_gpoint;
    private TextView tv_my_trophy;
    private ListView listView;
    private TextView tv_prize_txt;
    private EventAdapter eventAdapter;

    private Applications applications;
    private Tracker tracker;

    private String analiticsCategory = "/event";
    private FileCache fileCache;

    private ArrayList<EventModel> eventList;
    private ArrayList<EventModel> eventCallBackList;
    private EventModel eventModel;

    private boolean isPrize = false;

    private LoadingDialog loadingDialog;
    private EventUseDialog eventUseDialog;
    private CashPopDialog cashPopDialog;
    private LotteryDialog lotteryDialog;
    private NetworkDialog networkDialog;
    private EventSendInfoDialog eventSendInfoDialog;

    private HashMap<String, NetworkErrorModel> networkErrorHash;

    private long requestTimeStamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        applications = (Applications) getApplication();
        tracker = applications.getDefaultTracker();
        tracker.setScreenName(analiticsCategory);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        getFileCache();

        this.init();

    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");

        try {
            if (eventUseDialog != null && eventUseDialog.isShowing()) {
                eventUseDialog.dismiss();
                eventUseDialog = null;
            }
            if( lotteryDialog != null && lotteryDialog.isShowing()){
                lotteryDialog.dismiss();
                lotteryDialog = null;
            }
            if (eventSendInfoDialog != null && eventSendInfoDialog.isShowing()) {
                eventSendInfoDialog.dismiss();
                eventSendInfoDialog = null;
            }
        } catch (Exception ignore) {
        }

        Applications.isEventRefresh = true;
        super.onDestroy();
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        refresh();
        applications.setRefreshActivity(this);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        try {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
            if (networkDialog != null && networkDialog.isShowing()) {
                networkDialog.dismiss();
                networkDialog = null;
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void refresh() {
        Log.e(TAG, "refresh");
        if (Applications.isEventRefresh) {
            Applications.isEventRefresh = false;
            requestEvent();
        }
    }

    public void init() {

        type_admob = (LinearLayout) findViewById(R.id.type_admob);
        /*
        try {
            if ((Applications.nativeExpressAdView.getParent()) != null) {
                ((ViewGroup) Applications.nativeExpressAdView.getParent()).removeAllViews();
                type_admob.addView(Applications.nativeExpressAdView);
            }else{
                type_admob.addView(Applications.nativeExpressAdView);
            }
        }catch (Exception ignore){
            ignore.printStackTrace();
        }
        */
        tv_title = (TextView) findViewById(R.id.tv_title);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_info = (Button) findViewById(R.id.btn_info);
        btn_info.setOnClickListener(this);

        layer_gold_coin = (LinearLayout) findViewById(R.id.layer_gold_coin);
        layer_gold_coin.setOnClickListener(this);

        iv_my_gpoint = (ImageView) findViewById(R.id.iv_my_gpoint);
        tv_my_gpoint = (TextView) findViewById(R.id.tv_my_gpoint);
        tv_my_gpoint.setText(CommonUtil.setComma(Applications.ePreference.getTotalGpoint() + "", true, false));

        tv_my_trophy = (TextView) findViewById(R.id.tv_my_trophy);
        tv_my_trophy.setText(CommonUtil.setComma(Applications.preference.getValue(Preference.COIN, 0) + "", false, false));


        eventList = new ArrayList<>();
        eventCallBackList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.listView);
        eventAdapter = new EventAdapter(this, R.layout.row_event, eventList);
        listView.setAdapter(eventAdapter);
        listView.setDivider(null);
        listView.setOnItemClickListener(this);

        tv_prize_txt = (TextView) findViewById(R.id.tv_prize_txt);

        versionChk();

    }

    public FileCache getFileCache() {
        if (fileCache == null) {
            FileCacheFactory.initialize(this);
            if (!FileCacheFactory.getInstance().has(CommonUtil.eventCache)) {
                FileCacheFactory.getInstance().create(CommonUtil.eventCache, 1024 * 4);
            }
            fileCache = FileCacheFactory.getInstance().get(CommonUtil.eventCache);
        }
        return fileCache;
    }

    private void requestEvent() {
        boolean isCache = false;
        String cacheRst = "";
        if (fileCache.get(CommonUtil.eventCache) != null) {
            try {
                InputStream is = fileCache.get(CommonUtil.eventCache).getInputStream();
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                cacheRst = new String(buffer);
                Log.e(TAG, cacheRst);
                JSONObject job = new JSONObject(cacheRst);
                requestTimeStamp = Long.parseLong(job.getString(CommonUtil.KEY_TIMESTAMP));
                isCache = System.currentTimeMillis() - Long.parseLong(job.getString(CommonUtil.KEY_TIMESTAMP)) < 60 * 60 * 1000 * 2;
                JSONObject jo = new JSONObject(job.getString(CommonUtil.KEY_RST));
                JSONArray jobjArr = new JSONArray(jo.getString("el"));
                if (jobjArr.length() == 0) {
                    isCache = false;
                }
            } catch (Exception e) {
                isCache = false;
                e.printStackTrace();
            }
        }
        if (isCache) {
            try {
                JSONObject job = new JSONObject(cacheRst);
                JSONObject jo = new JSONObject(job.getString(CommonUtil.KEY_RST));
                JSONArray jobjArr = new JSONArray(jo.getString("el"));
                eventList.clear();
                for (int i = 0; i < jobjArr.length(); i++) {
                    EventModel eventModel = new EventModel();
                    JSONObject jobj = jobjArr.getJSONObject(i);
                    eventModel.setId(jobj.getString("id"));
                    eventModel.setTitle(jobj.getString("title"));
                    eventModel.setGold(jobj.getInt("gold"));
                    eventModel.setCoin(jobj.getInt("coin"));
                    eventModel.setLimitType(jobj.getString("limitType"));
                    eventModel.setTimeTerm(jobj.getInt("timeTerm"));
                    eventModel.setDayCnt(jobj.getInt("dayCnt"));
                    eventModel.setProcCnt(jobj.getInt("procCnt"));
                    eventModel.setImage(jobj.getString("image"));
                    eventModel.setIsAction(jobj.getString("isAction"));
                    eventModel.setIsLimit(jobj.getString("isLimit"));
                    eventModel.setBackColor(jobj.getString("backColor"));
                    eventModel.setTermMsg(jobj.getString("termMsg"));
                    eventModel.setTermCnt(jobj.getString("termCnt"));
                    eventModel.setLabelEnable(jobj.getString("labelEnable"));
                    eventModel.setLtext(jobj.getString("ltext"));
                    eventModel.setLbackColor(jobj.getString("lbackColor"));
                    eventModel.setLtextColor(jobj.getString("ltextColor"));
                    eventModel.setExpire(jobj.getInt("expire"));
                    eventList.add(eventModel);
                }
                listView.setVisibility(View.VISIBLE);
                eventAdapter.notifyDataSetChanged();
                try {
                    String em = jo.getString("em");
                    tv_prize_txt.setText(em);
                    tv_prize_txt.setSelected(true);
                    tv_prize_txt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    tv_prize_txt.setSingleLine();
                } catch (Exception ignore) {
                }
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID, ""));
            map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_GET_EVENT);
            String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
            requestAsyncTask(param, CommonUtil.ACTION_GET_EVENT);
        }
    }

    public void requestEvent(EventModel eventModel) {
        try {
            ShowLoadingProgress();
            HashMap<String, String> map = new HashMap<>();
            map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID, ""));
            map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_LOTTERY_REQUEST);
            map.put(CommonUtil.KEY_LOTTERY_ID, eventModel.getId());
            String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
            requestAsyncTask(param, CommonUtil.ACTION_LOTTERY_REQUEST);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    public void requestEventInfo(String lk, String name, String email, String phone) {
        try {
            ShowLoadingProgress();
            HashMap<String, String> map = new HashMap<>();
            map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID, ""));
            map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_EVENT_INFO_SEND);
            map.put(CommonUtil.KEY_LOTTERY_KEY, lk);
            map.put(CommonUtil.KEY_INPUT1, name);
            map.put(CommonUtil.KEY_INPUT2, email);
            map.put(CommonUtil.KEY_INPUT3, phone);
            String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
            requestAsyncTask(param, CommonUtil.ACTION_EVENT_INFO_SEND);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_info:
                CommonUtil.showSupport(EventActivity.this, true);
                break;
            case R.id.layer_gold_coin:
                goHistory();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.listView:
                if (isPrize && !eventModel.getId().equals(this.eventModel.getId())) {
                    Toast toast = Toast.makeText(this, this.getResources().getString(R.string.lottery_reward, eventModel.getTitle()), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                    toast.show();
                } else {
                    final EventModel eventModel = eventAdapter.getItem(i);
                    this.eventModel = eventModel;
                    //proc
                    if (eventModel.getIsAction().equals("Y")) {
                        eventUseDialog = new EventUseDialog(this);
                        eventUseDialog.setTitle(eventModel.getTitle());
                        eventUseDialog.setGoldCoin(eventModel.getGold() + "", eventModel.getCoin() + "");
                        eventUseDialog.setCpOkButton(this.getResources().getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (eventModel.getGold() > 0) {
                                    if (Applications.ePreference.getTotalGpoint() < eventModel.getGold()) {
//                                        Toast toast = Toast.makeText(TeventActivity.this, getResources().getString(R.string.lottery_error1), Toast.LENGTH_SHORT);
//                                        toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
//                                        toast.show();
                                        cashPopDialog = new CashPopDialog(EventActivity.this);
                                        cashPopDialog.setCpTitle(getResources().getString(R.string.not_enough_gold_title));
                                        cashPopDialog.setCpDesc(getResources().getString(R.string.not_enough_gold_event));
                                        cashPopDialog.setCpCancelButton(getResources().getString(R.string.hold), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                cashPopDialog.dismiss();
                                            }
                                        });
                                        cashPopDialog.setCpOkButton(getResources().getString(R.string.go_to_gold), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                goToMission();
                                                cashPopDialog.dismiss();
                                            }
                                        });
                                        cashPopDialog.show();
                                        return;
                                    }
                                }
                                if (eventModel.getCoin() > 0) {
                                    if (Applications.preference.getValue(Preference.COIN, 0) < eventModel.getCoin()) {
                                        /*
                                        Toast toast = Toast.makeText(TeventActivity.this, getResources().getString(R.string.lottery_error1), Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                                        toast.show();
                                        */
                                        cashPopDialog = new CashPopDialog(EventActivity.this);
                                        cashPopDialog.setCpTitle(getResources().getString(R.string.not_enough_trophy_title));
                                        cashPopDialog.setCpDesc(getResources().getString(R.string.not_enough_trophy_event));
                                        cashPopDialog.setCpCancelButton(getResources().getString(R.string.hold), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                cashPopDialog.dismiss();
                                            }
                                        });
                                        cashPopDialog.setCpOkButton(getResources().getString(R.string.go_to_gold), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Applications.preference.put(Preference.TROPHY_VIEW, true);
                                                goToMission();
                                                cashPopDialog.dismiss();
                                            }
                                        });
                                        cashPopDialog.show();
                                        return;
                                    }
                                }
                                if (eventModel.getIsLimit().equals("Y")) {
                                    Toast toast = Toast.makeText(EventActivity.this, getResources().getString(R.string.lottery_error2), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                                    toast.show();
                                    return;
                                }

                                if (!eventModel.getLimitType().equals("2") && eventModel.getExpire() > 0) {
                                    long currentTime = System.currentTimeMillis() / 1000L;
                                    long pTime = currentTime - getRequestTimeStamp() / 1000L;
                                    long expireTime = eventModel.getExpire();
                                    long lastExpireTime = expireTime - pTime;
                                    if (lastExpireTime > 0) {
                                        String msg = "";
                                        String hour = "";
                                        String min = "";
                                        if (Math.floor(lastExpireTime / 3600) > 0) {
                                            hour = getResources().getString(R.string.lottery_hour, (int) Math.floor(lastExpireTime / 3600) + "");
                                        }
                                        if (Math.floor((lastExpireTime / 60) % 60) > 0) {
                                            min = getResources().getString(R.string.lottery_min, (int) Math.floor((lastExpireTime / 60) % 60) + "");
                                        }
                                        msg = getResources().getString(R.string.lottery_msg, hour + min);
                                        Toast toast = Toast.makeText(EventActivity.this, msg, Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                                        toast.show();
                                        return;
                                    }
                                }
                                requestEvent(eventModel);
                                eventUseDialog.dismiss();
                            }
                        });
                        eventUseDialog.setCpCancelButton(this.getResources().getString(R.string.cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                eventUseDialog.dismiss();
                            }
                        });
                        eventUseDialog.show();
                    } else {
                        Toast toast = Toast.makeText(this, eventModel.getTermMsg(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                        toast.show();
                    }
                }
                break;
        }
    }

    @Override
    public void onTaskComplete(String result) {
        String rst;
        try {
            rst = APICrypto.decrypt(CommonUtil.SHARED_KEY, result);
        } catch (Exception e) {
            rst = result;
        }
        try {
            final JSONObject jo = new JSONObject(rst);
            String error = jo.getString(CommonUtil.RESULT_ERROR);
            String action = jo.getString(CommonUtil.RESULT_ACTION);
            if (error != null && error.isEmpty() && action != null && !action.isEmpty()) {
                switch (action) {
                    case CommonUtil.ACTION_GET_INFO:
                        String gold = jo.getString(CommonUtil.RESULT_BUDGET);
                        String coin = jo.getString(CommonUtil.RESULT_COIN);
                        final double nowGold = Applications.ePreference.getTotalGpoint();
                        final int nowCoin = Applications.preference.getValue(Preference.COIN, 0);
                        if (nowGold < Double.parseDouble(gold) || nowCoin < Integer.parseInt(coin)) {
                            setGoldCoin(Double.parseDouble(gold), Integer.parseInt(coin), "reward");
                        } else {
                            setGoldCoin(Double.parseDouble(gold), Integer.parseInt(coin), "");
                        }

                        String linked_gold = jo.getString(CommonUtil.RESULT_LINKED_GOLD);
                        setLinkedGold(Double.parseDouble(linked_gold));
                        String normal_gold = jo.getString(CommonUtil.RESULT_BALANCE_GPOINT);
                        setNormalGold(Double.parseDouble(normal_gold));

/*
                        String invited_partners = jo.getString(CommonUtil.RESULT_INVITED_PARTNERS);

                        if (!invited_partners.equals(Applications.preference.getValue(Preference.PARTNERS, "0"))) {
                            FileCacheFactory.initialize(EventActivity.this);
                            if (FileCacheFactory.getInstance().has(CommonUtil.cacheNameInvite)) {
                                FileCacheFactory.getInstance().get(CommonUtil.cacheNameInvite).clear();
                            }
                        }
                        String partner_gpoint = jo.getString(CommonUtil.RESULT_PARTNER_GPOINT);

                        Applications.preference.put(Preference.REDEEMCODE, Long.toString(Long.parseLong(Applications.preference.getValue(Preference.USER_ID, "")), 36));

                        Applications.preference.put(Preference.PARTNERS, invited_partners);
                        Applications.preference.put(Preference.PARTNER_GPOINT, partner_gpoint);
*/
                        Applications.preference.put(Preference.CPID, jo.getString("id"));

                        Applications.preference.put(Preference.BIRTH, jo.getString(CommonUtil.RESULT_YEAR));
                        Applications.preference.put(Preference.LOCATION, jo.getString(CommonUtil.RESULT_LOCATION));
                        Applications.preference.put(Preference.GENDER, jo.getString(CommonUtil.RESULT_GENDER));
                        Applications.preference.put(Preference.MARRIAGE, jo.getString(CommonUtil.RESULT_MARRIAGE));
                        Applications.preference.put(Preference.PARTNERCDOE, jo.getString(CommonUtil.RESULT_PARTNERCODE));
                        Applications.preference.put(Preference.REVIEW, jo.getString(CommonUtil.RESULT_REVIEW));
                        Applications.preference.put(Preference.MISSION, jo.getString(CommonUtil.RESULT_MISSION));

                        if (jo.getString(CommonUtil.RESULT_ALARM).equals("1") || jo.getString(CommonUtil.RESULT_ALARM).equals("")) {
                            Applications.preference.put(Preference.CASH_POP_ALARM, true);
                            FirebaseMessaging.getInstance().subscribeToTopic(Applications.getTopicId(EventActivity.this));
                        } else {
                            Applications.preference.put(Preference.CASH_POP_ALARM, false);
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(Applications.getTopicId(EventActivity.this));
                        }

                        HideLoadingProgress();

                        break;
                    case CommonUtil.ACTION_VERSION:
                        final String v_n = jo.getString("v_n");
                        final String v_p = jo.getString("v_p");
                        final int v_c = Integer.parseInt(jo.getString("v_c"));
                        Applications.ePreference.put(EPreference.VERSION_N, v_n + "");
                        Applications.ePreference.put(EPreference.VERSION_P, v_p + "");
                        Applications.ePreference.put(EPreference.VERSION_C, v_c + "");
                        int version = CommonUtil.getVersionCode(this);
                        if (version < v_c) {
                            cashPopDialog = new CashPopDialog(this);
                            cashPopDialog.setCpTitle(getResources().getString(R.string.habit2good_update));
                            cashPopDialog.setCpDesc(getResources().getString(R.string.habit2good_update_desc));
                            cashPopDialog.setCpBOkButton(getResources().getString(R.string.update_now), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    if (v_p != null && !v_p.equals("")) {
                                        intent.setData(Uri.parse("market://details?id=" + v_p));
                                    } else {
                                        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                                    }
                                    startActivity(intent);
                                }
                            });
                            cashPopDialog.setCpBCancelButton(getResources().getString(R.string.exit), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    System.exit(0);
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                }
                            });
                            cashPopDialog.setCpCancel(false);
                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cashPopDialog.show();
                                }
                            });
                        }
                        break;
                    case CommonUtil.ACTION_GET_EVENT:
                        JSONArray jobjArr = new JSONArray(jo.getString("el"));
                        eventList.clear();
                        for (int i = 0; i < jobjArr.length(); i++) {
                            EventModel eventModel = new EventModel();
                            JSONObject jobj = jobjArr.getJSONObject(i);
                            eventModel.setId(jobj.getString("id"));
                            eventModel.setTitle(jobj.getString("title"));
                            eventModel.setGold(jobj.getInt("gold"));
                            eventModel.setCoin(jobj.getInt("coin"));
                            eventModel.setLimitType(jobj.getString("limitType"));
                            eventModel.setTimeTerm(jobj.getInt("timeTerm"));
                            eventModel.setDayCnt(jobj.getInt("dayCnt"));
                            eventModel.setProcCnt(jobj.getInt("procCnt"));
                            eventModel.setImage(jobj.getString("image"));
                            eventModel.setIsAction(jobj.getString("isAction"));
                            eventModel.setIsLimit(jobj.getString("isLimit"));
                            eventModel.setBackColor(jobj.getString("backColor"));
                            eventModel.setTermMsg(jobj.getString("termMsg"));
                            eventModel.setTermCnt(jobj.getString("termCnt"));
                            eventModel.setLabelEnable(jobj.getString("labelEnable"));
                            eventModel.setLtext(jobj.getString("ltext"));
                            eventModel.setLbackColor(jobj.getString("lbackColor"));
                            eventModel.setLtextColor(jobj.getString("ltextColor"));
                            eventModel.setExpire(jobj.getInt("expire"));
                            eventList.add(eventModel);

                        }

                        JSONArray elrArr = new JSONArray(jo.getString("elr"));
                        Log.e("jo", "" + jo);
                        JSONObject cacheRst = new JSONObject();
                        requestTimeStamp = System.currentTimeMillis();
                        cacheRst.put(CommonUtil.KEY_TIMESTAMP, requestTimeStamp);
                        cacheRst.put(CommonUtil.KEY_RST, rst);
                        fileCache.put(CommonUtil.eventCache, ByteProviderUtil.create(cacheRst.toString()));
                        listView.setVisibility(View.VISIBLE);
                        eventAdapter.notifyDataSetChanged();
                        try {
                            String em = jo.getString("em");
                            tv_prize_txt.setText(em);
                            tv_prize_txt.setSelected(true);
                            tv_prize_txt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            tv_prize_txt.setSingleLine();
                        } catch (Exception ignore) {
                        }
                        break;
                    case CommonUtil.ACTION_LOTTERY_REQUEST:
                        final String request_result = rst;
                        String lri = jo.getString("lri");
                        String s_prize = jo.getString("s_prize");
                        String lrank = jo.getString("lrank");
                        final String rtype = jo.getString("rtype");
                        final String rtext = jo.getString("rtext");
                        final String rstext = jo.getString("rstext");
                        final String lk = jo.getString("lk");
                        try {
                            final String noti = jo.getString("noti");
                            if (noti.equals("Y")) {
                                final long ex_time = jo.getInt("ex_time");
                                final String ltitle = jo.getString("ltitle");
                                final String message = EventActivity.this.getResources().getString(R.string.event_go, ltitle);
                                final int notiid = jo.getInt("notiid");
                                Intent intent = new Intent("EVENTNOTI");
                                intent.putExtra("ltitle", ltitle);
                                intent.putExtra("msg", message);
                                intent.putExtra("notiid", notiid);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(EventActivity.this, notiid, intent, 0);
                                AlarmManager alarmManager = (AlarmManager) EventActivity.this.getSystemService(Context.ALARM_SERVICE);
                                //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (ex_time * 1000), pendingIntent);
                                //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000), (60*1000), pendingIntent);
                                long expire_time = System.currentTimeMillis() + (ex_time * 1000);
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, expire_time, (60 * 1000), pendingIntent);

                                Applications.dbHelper.setAlarmNoti(notiid + "", ltitle, message, expire_time + "");

                            }
                        } catch (Exception ignore) {
                        }
                        lotteryDialog = new LotteryDialog(this);
                        lotteryDialog.setPrizeImg(lri);
                        lotteryDialog.setLotteryListener(new LotteryListener() {
                            @Override
                            public void procList() {
                                try {
                                    String reward_gold = jo.getString(CommonUtil.RESULT_BUDGET);
                                    String reward_linked_gold = jo.getString(CommonUtil.RESULT_LINKED_GOLD);
                                    String reward_normal_gold = jo.getString(CommonUtil.RESULT_BALANCE_GPOINT);
                                    int reward_coin = jo.getInt(CommonUtil.RESULT_COIN);

                                    setGoldCoin(Double.parseDouble(reward_gold), reward_coin, "");
                                    Applications.ePreference.putNLinkedGold(Double.parseDouble(reward_linked_gold));
                                    Applications.ePreference.putBalanceGpoint(Double.parseDouble(reward_normal_gold));

                                    JSONArray jobjeArr = new JSONArray(jo.getString("el"));
                                    eventCallBackList.clear();
                                    for (int i = 0; i < jobjeArr.length(); i++) {
                                        EventModel eventModel = new EventModel();
                                        JSONObject jobj = jobjeArr.getJSONObject(i);
                                        eventModel.setId(jobj.getString("id"));
                                        eventModel.setTitle(jobj.getString("title"));
                                        eventModel.setGold(jobj.getInt("gold"));
                                        eventModel.setCoin(jobj.getInt("coin"));
                                        eventModel.setDayCnt(jobj.getInt("dayCnt"));
                                        eventModel.setProcCnt(jobj.getInt("procCnt"));
                                        eventModel.setImage(jobj.getString("image"));
                                        eventModel.setIsAction(jobj.getString("isAction"));
                                        eventModel.setIsLimit(jobj.getString("isLimit"));
                                        eventModel.setBackColor(jobj.getString("backColor"));
                                        eventModel.setTermMsg(jobj.getString("termMsg"));
                                        eventModel.setTermCnt(jobj.getString("termCnt"));
                                        eventCallBackList.add(eventModel);
                                    }

                                    eventList.clear();
                                    eventList.addAll(eventCallBackList);
                                    eventCallBackList.clear();

                                    Log.e("jo", "" + jo);
                                    JSONObject cacheRst = new JSONObject();
                                    cacheRst.put(CommonUtil.KEY_TIMESTAMP, System.currentTimeMillis());
                                    cacheRst.put(CommonUtil.KEY_RST, request_result);
                                    fileCache.put(CommonUtil.eventCache, ByteProviderUtil.create(cacheRst.toString()));
                                    listView.setVisibility(View.VISIBLE);
                                    eventAdapter.notifyDataSetChanged();
                                    try {
                                        String em = jo.getString("em");
                                        tv_prize_txt.setText(em);
                                        tv_prize_txt.setSelected(true);
                                        tv_prize_txt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                                        tv_prize_txt.setSingleLine();
                                    }catch (Exception ignore){
                                    }
                                    isPrize = true;

                                    if( rtype.equals("2") && !lk.equals("") && !rtext.equals("")){
                                        eventSendInfoDialog = new EventSendInfoDialog(EventActivity.this);
                                        eventSendInfoDialog.open(rtext, new EventSendInfoListener() {
                                            @Override
                                            public void eventSender(String input1, String input2, String input3) {
                                                Log.e("eventSender",input1+" "+input2+" "+input3);
                                                requestEventInfo(lk, input1, input2, input3);
                                                eventSendInfoDialog.dismiss();
                                            }
                                        });
                                    }

                                }catch (Exception ignore){
                                }
                            }
                        });
                        lotteryDialog.show();
                        Applications.isHomeRefresh = true;
                        break;
                }
            } else if (error != null && error.equals(CommonUtil.ERROR_NO_USER)) {
                cashPopDialog = new CashPopDialog(this);
                cashPopDialog.setCpTitle(this.getResources().getString(R.string.logout));
                cashPopDialog.setCpDesc(this.getResources().getString(R.string.auto_logout_no_user));
                cashPopDialog.setCpCancel(false);
                cashPopDialog.setCpOkButton(this.getResources().getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Logout();
                    }
                });
                cashPopDialog.show();
            } else if (error != null && error.equals(CommonUtil.ERROR_NO_TURN)) {
                Toast toast = Toast.makeText(this, this.getResources().getString(R.string.lottery_error4), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                toast.show();
            } else if (error != null && error.equals(CommonUtil.ERROR_NO_LOTTERY)) {
                Toast toast = Toast.makeText(this, this.getResources().getString(R.string.lottery_error3), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                toast.show();
            } else if (error != null && error.equals(CommonUtil.ERROR_LOTTERY_LIMIT)) {
                Toast toast = Toast.makeText(this, this.getResources().getString(R.string.lottery_error2), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                toast.show();
            } else if (error != null && error.equals(CommonUtil.ERROR_LOTTERY_BEFORE)) {
                Toast toast = Toast.makeText(this, eventModel.getTermMsg(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                toast.show();
            } else if (error != null && error.equals(CommonUtil.ERROR_LOTTERY_MORE)) {
//                Toast toast = Toast.makeText(this, this.getResources().getString(R.string.lottery_error1), Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
//                toast.show();
                cashPopDialog = new CashPopDialog(this);
                cashPopDialog.setCpTitle(getResources().getString(R.string.not_enough_trophy_title));
                cashPopDialog.setCpDesc(getResources().getString(R.string.not_enough_trophy_event));
                cashPopDialog.setCpCancelButton(getResources().getString(R.string.hold), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cashPopDialog.dismiss();
                    }
                });
                cashPopDialog.setCpOkButton(getResources().getString(R.string.go_to_gold), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Applications.preference.put(Preference.TROPHY_VIEW, true);
                        goToMission();
                        cashPopDialog.dismiss();
                    }
                });
                cashPopDialog.show();
            } else if (error != null && error.equals(CommonUtil.ERROR_LOTTERY_MORE_TIME)) {
                long time = jo.getLong("x");

                String msg = "";
                String hour = "";
                String min = "";
                if (Math.floor(time / 3600) > 0) {
                    hour = this.getResources().getString(R.string.lottery_hour, (int) Math.floor(time / 3600) + "");
                }
                if (Math.floor((time / 60) % 60) > 0) {
                    min = this.getResources().getString(R.string.lottery_min, (int) Math.floor((time / 60) % 60) + "");
                }
                msg = this.getResources().getString(R.string.lottery_msg, hour + min);

                Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                toast.show();
                FileCacheFactory.getInstance().get(CommonUtil.eventCache).clear();
                requestEvent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HideLoadingProgress();
        }
    }

    @Override
    public void onTaskError(String param, String action, String result) {
        HideLoadingProgress();
        try {
            Log.e(TAG, action);
            if (action.equals(CommonUtil.ACTION_GET_EVENT)) {
                String cacheRst = "";
                if (fileCache.get(CommonUtil.eventCache) != null) {
                    try {
                        InputStream is = fileCache.get(CommonUtil.eventCache).getInputStream();
                        int size = is.available();
                        byte[] buffer = new byte[size];
                        is.read(buffer);
                        is.close();
                        cacheRst = new String(buffer);
                        Log.e(TAG, cacheRst);
                        JSONObject job = new JSONObject(cacheRst);
                        JSONObject jo = new JSONObject(job.getString(CommonUtil.KEY_RST));
                        JSONArray jobjArr = new JSONArray(jo.getString("el"));
                        eventList.clear();
                        for (int i = 0; i < jobjArr.length(); i++) {
                            EventModel eventModel = new EventModel();
                            JSONObject jobj = jobjArr.getJSONObject(i);
                            eventModel.setId(jobj.getString("id"));
                            eventModel.setTitle(jobj.getString("title"));
                            eventModel.setGold(jobj.getInt("gold"));
                            eventModel.setCoin(jobj.getInt("coin"));
                            eventModel.setLimitType(jobj.getString("limitType"));
                            eventModel.setTimeTerm(jobj.getInt("timeTerm"));
                            eventModel.setDayCnt(jobj.getInt("dayCnt"));
                            eventModel.setProcCnt(jobj.getInt("procCnt"));
                            eventModel.setImage(jobj.getString("image"));
                            eventModel.setIsAction(jobj.getString("isAction"));
                            eventModel.setIsLimit(jobj.getString("isLimit"));
                            eventModel.setBackColor(jobj.getString("backColor"));
                            eventModel.setTermMsg(jobj.getString("termMsg"));
                            eventModel.setTermCnt(jobj.getString("termCnt"));
                            eventModel.setLabelEnable(jobj.getString("labelEnable"));
                            eventModel.setLtext(jobj.getString("ltext"));
                            eventModel.setLbackColor(jobj.getString("lbackColor"));
                            eventModel.setLtextColor(jobj.getString("ltextColor"));
                            eventModel.setExpire(jobj.getInt("expire"));
                            eventList.add(eventModel);
                        }
                        listView.setVisibility(View.VISIBLE);
                        eventAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    listView.setVisibility(View.GONE);
                }
                Applications.isEventRefresh = true;
                showErrorNetwork(param, action, "event");
            }
        } catch (Exception ignore) {
        }
    }

    public void versionChk() {
        long now = System.currentTimeMillis();
        String versionChk = Applications.preference.getValue(Preference.VERSION_CHK_TIMESTAMP, now + "");
        long versionTime;
        if (versionChk != null && !versionChk.equals("") && versionChk.matches("^[0-9]+$")) {
            try {
                versionTime = Long.parseLong(versionChk);
            } catch (Exception ignore) {
                versionTime = now;
            }
        } else {
            versionTime = now;
        }
        if (versionTime <= now) {
            Applications.preference.put(Preference.VERSION_CHK_TIMESTAMP, (now + (3600000 * 12)) + "");
            HashMap<String, String> map = new HashMap<>();
            map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_VERSION);
            String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
            requestAsyncTask(param, CommonUtil.ACTION_VERSION);
        } else {
            int version = CommonUtil.getVersionCode(this);
            int nowVersion = version;
            try {
                nowVersion = Integer.parseInt(Applications.ePreference.getValue(EPreference.VERSION_C, ""));
            } catch (Exception ignore) {
            }
            final String v_p = Applications.ePreference.getValue(EPreference.VERSION_P, "");
            if (version < nowVersion) {
                cashPopDialog = new CashPopDialog(this);
                cashPopDialog.setCpTitle(getResources().getString(R.string.habit2good_update));
                cashPopDialog.setCpDesc(getResources().getString(R.string.habit2good_update_desc));
                cashPopDialog.setCpBOkButton(getResources().getString(R.string.update_now), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        if (v_p != null && !v_p.equals("")) {
                            intent.setData(Uri.parse("market://details?id=" + v_p));
                        } else {
                            intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                        }
                        startActivity(intent);
                    }
                });
                cashPopDialog.setCpBCancelButton(getResources().getString(R.string.exit), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.exit(0);
                    }
                });
                cashPopDialog.setCpCancel(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cashPopDialog.show();
                    }
                });
            }
        }
    }

    public void setGoldCoin(final double gold, final int coin, final String type) {
        Applications.isCashpopPopup = false;
        final double nowGold = Applications.ePreference.getTotalGpoint();
        final int nowCoin = Applications.ePreference.getValue(EPreference.N_TROPHY, 0);
        //final int nowCoin = Applications.preference.getValue(Preference.COIN, 0);
        if (nowGold != gold || nowCoin != coin) {
            if (FileCacheFactory.getInstance().has(CommonUtil.cacheNameHistory)) {
                FileCacheFactory.getInstance().get(CommonUtil.cacheNameHistory).clear();
            }
        }
        double reward = gold - nowGold;
        int reward_coin = coin - nowCoin;
        if (reward != 0 || reward_coin != 0) {
            try {
                if (FileCacheFactory.getInstance().has(CommonUtil.cacheNameHistory)) {
                    FileCacheFactory.getInstance().get(CommonUtil.cacheNameHistory).clear();
                }
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
        try {
            Log.e(TAG, "nowGold : " + nowGold + ", gold : " + gold + ", nowCoin : " + nowCoin + ", coin : " + coin);
            goldCoinRefresh(nowGold, gold, nowCoin, coin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Applications.ePreference.putTotalGpoint(gold);
        Applications.preference.put(Preference.COIN, coin);
    }

    public void goldCoinRefresh(final double nowGold, final double refreshGold, final int nowCoin, final int refreshCoin) throws Exception {
        boolean isGold = false;
        boolean isCoin = false;
        if (refreshGold - nowGold != 0) {
            isGold = true;
        }
        if (refreshCoin - nowCoin != 0) {
            isCoin = true;
        }

        if (isGold && isCoin) {
            final int dtime = 40;
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 0:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 1:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2_2));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 2:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 3:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 4:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 5:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2_2));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 6:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 7:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 8:
                            ValueAnimator vaG = ValueAnimator.ofInt((int) nowGold, (int) refreshGold);
                            vaG.setDuration(1000);
                            vaG.setInterpolator(AnimationUtils.loadInterpolator(EventActivity.this, android.R.anim.decelerate_interpolator));
                            vaG.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    try {
                                        Integer value = (Integer) valueAnimator.getAnimatedValue();
                                        tv_my_gpoint.setText(CommonUtil.setComma(value + "", false, false));
                                    } catch (Exception ignore) {
                                    }
                                }
                            });
                            vaG.start();
                            ValueAnimator vaC = ValueAnimator.ofInt(nowCoin, refreshCoin);
                            vaC.setDuration(1000);
                            vaC.setInterpolator(AnimationUtils.loadInterpolator(EventActivity.this, android.R.anim.decelerate_interpolator));
                            vaC.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    try {
                                        Integer value = (Integer) valueAnimator.getAnimatedValue();
                                        tv_my_trophy.setText(CommonUtil.setComma(value + "", false, false));
                                    } catch (Exception ignore) {
                                    }
                                }
                            });
                            vaC.start();
                            this.sendEmptyMessageDelayed(msg.what + 1, 1000);
                            break;
                        case 9:
                            String tmsg = "";
                            if ((refreshGold - nowGold) > 0 && (refreshCoin - nowCoin) > 0) {
                                tmsg = getResources().getString(R.string.reward_toast, "" + (int) (refreshGold - nowGold), "" + (int) (refreshCoin - nowCoin));
                            } else if ((refreshGold - nowGold) > 0 && (refreshCoin - nowCoin) <= 0) {
                                tmsg = getResources().getString(R.string.reward_gpoint_toast, "" + (int) (refreshGold - nowGold));
                            } else if ((refreshGold - nowGold) <= 0 && (refreshCoin - nowCoin) > 0) {
                                tmsg = getResources().getString(R.string.reward_trophy_toast, "" + (int) (refreshCoin - nowCoin));
                            }
                            if (!tmsg.equals("")) {
                                Toast toast = Toast.makeText(EventActivity.this, tmsg, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                                toast.show();
                            }
                            break;
                    }
                }
            }.sendEmptyMessageDelayed(0, 200);
        } else {
            try {
                goldRefresh(nowGold, refreshGold);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                coinRefresh(nowCoin, refreshCoin);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void goldRefresh(final double nowGold, final double refreshGold) throws Exception {
        /*
        ValueAnimator va = new ValueAnimator();
        va.setObjectValues(nowGold, refreshGold);
        va.setDuration(1000);
        va.setEvaluator(new TypeEvaluator<Double>(){
            @Override
            public Double evaluate(float fraction, Double startValue, Double endValue) {
                return  (startValue + (double)((endValue - startValue) * fraction));
            }
        });
        va.setInterpolator(AnimationUtils.loadInterpolator(TcashActivity.this, android.R.anim.decelerate_interpolator));
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                try{
                    Double value = (Double)valueAnimator.getAnimatedValue();
                    tv_my_gpoint.setText(CommonUtil.setComma(value+"", true, false));
                }catch (Exception ignore){
                    ignore.printStackTrace();
                }
            }
        });
        va.start();
        */
        if (refreshGold - nowGold > 0) {
            final int dtime = 40;
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 0:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 1:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2_2));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 2:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 3:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 4:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 5:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2_2));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 6:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 7:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(EventActivity.this, R.drawable.cash2));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 8:
                            ValueAnimator va = ValueAnimator.ofInt((int) nowGold, (int) refreshGold);
                            va.setDuration(1000);
                            va.setInterpolator(AnimationUtils.loadInterpolator(EventActivity.this, android.R.anim.decelerate_interpolator));
                            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    try {
                                        Integer value = (Integer) valueAnimator.getAnimatedValue();
                                        tv_my_gpoint.setText(CommonUtil.setComma(value + "", false, false));
                                    } catch (Exception ignore) {
                                    }
                                }
                            });
                            va.start();
                            va.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    Toast toast = Toast.makeText(EventActivity.this, getResources().getString(R.string.reward_gpoint_toast, "" + (int) (refreshGold - nowGold)), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                                    toast.show();
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {
                                }
                            });
                            break;
                    }
                }
            }.sendEmptyMessageDelayed(0, 200);
        } else {
            ValueAnimator va = ValueAnimator.ofInt((int) nowGold, (int) refreshGold);
            va.setDuration(1000);
            va.setInterpolator(AnimationUtils.loadInterpolator(EventActivity.this, android.R.anim.decelerate_interpolator));
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    try {
                        Integer value = (Integer) valueAnimator.getAnimatedValue();
                        tv_my_gpoint.setText(CommonUtil.setComma(value + "", false, false));
                    } catch (Exception ignore) {
                    }
                }
            });
            va.start();
        }
    }

    public void coinRefresh(final int nowCoin, final int refreshCoin) throws Exception {
        if (refreshCoin - nowCoin > 0) {
            ValueAnimator va = ValueAnimator.ofInt(nowCoin, refreshCoin);
            va.setDuration(1000);
            va.setInterpolator(AnimationUtils.loadInterpolator(EventActivity.this, android.R.anim.decelerate_interpolator));
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    try {
                        Integer value = (Integer) valueAnimator.getAnimatedValue();
                        tv_my_trophy.setText(CommonUtil.setComma(value + "", false, false));
                    } catch (Exception ignore) {
                    }
                }
            });
            va.start();
            va.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    Toast toast = Toast.makeText(EventActivity.this, getResources().getString(R.string.reward_trophy_toast, "" + (int) (refreshCoin - nowCoin)), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                    toast.show();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        } else {
            ValueAnimator va = ValueAnimator.ofInt(nowCoin, refreshCoin);
            va.setDuration(1000);
            va.setInterpolator(AnimationUtils.loadInterpolator(EventActivity.this, android.R.anim.decelerate_interpolator));
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    try {
                        Integer value = (Integer) valueAnimator.getAnimatedValue();
                        tv_my_trophy.setText(CommonUtil.setComma(value + "", false, false));
                    } catch (Exception ignore) {
                    }
                }
            });
            va.start();
        }
    }

    public void showErrorNetwork(final String param, final String action, final String type) {
        System.out.println("action : " + action + ", type : " + type + ", param : " + param);
        HideLoadingProgress();
        if (networkErrorHash == null) {
            networkErrorHash = new HashMap<>();
        }
        if (networkErrorHash.get(param + action + type) == null) {
            NetworkErrorModel networkErrorModel = new NetworkErrorModel();
            networkErrorModel.setAction(action);
            networkErrorModel.setParam(param);
            networkErrorModel.setType(type);
            networkErrorHash.put(param + action + type, networkErrorModel);
        }

        if (networkDialog == null) {
            networkDialog = new NetworkDialog(EventActivity.this);
        }
        if (!networkDialog.isShowing()) {
            networkDialog.setCancelClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HideLoadingProgress();
                    networkErrorHash.clear();
                    networkDialog.dismiss();
                    ActivityCompat.finishAffinity(EventActivity.this);
                }
            });
            networkDialog.setOkClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowLoadingProgress();
                    System.out.println(networkErrorHash.size());
                    synchronized (networkErrorHash) {
                        try {
                            for (String key : networkErrorHash.keySet()) {
                                NetworkErrorModel networkErrorModel = networkErrorHash.get(key);
                                System.out.println(String.format("action : %s,  키 : %s, 값 : %s", networkErrorModel.getAction(), key, networkErrorHash.get(key)));
                                requestAsyncTask(networkErrorModel.getParam(), networkErrorModel.getAction());
                                //networkErrorHash.remove(key);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            networkErrorHash.clear();
                        }
                    }
                    HideLoadingProgress();
                    networkDialog.dismiss();
                }
            });
            networkDialog.show();
        }
    }

    public void requestAsyncTask(String param, String action) {
        if (Applications.getCountry(this).equals("KR") && !Applications.isRoaming(this)) {
            new AsyncHTTPPost(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, CommonUtil.SERVER_URL, param, action);
        } else {
            new AsyncHTTPPost(this).execute(CommonUtil.SERVER_URL, param, action);
        }
    }

    public void ShowLoadingProgress() {
        //show loading
        try {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(this);
            }
            this.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        loadingDialog.show();
                    } catch (Exception ignore) {
                        ignore.printStackTrace();
                    }
                }
            });
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    public void HideLoadingProgress() {
        //hide loading
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
        });
    }

    public void Logout() {
        Applications.preference.put(Preference.USER_ID, "");
        Applications.preference.put(Preference.CPID, "");
        Applications.preference.put(Preference.COIN, 0);
        Applications.preference.put(Preference.GENDER, "");
        Applications.preference.put(Preference.BIRTH, "");
        Applications.preference.put(Preference.LOCATION, "");
        Applications.preference.put(Preference.MARRIAGE, "");
        Applications.preference.put(Preference.PARTNERCDOE, "");
        Applications.preference.put(Preference.CASH_POP_ALARM, false);
        Applications.preference.put(Preference.LINKED_APPS, "");
        Applications.preference.put(Preference.INVITE_PARTNER, "");
        Applications.preference.put(Preference.NOTICE_POP_DATE, "");
        Applications.preference.put(Preference.VERSION_CHK_TIMESTAMP, "");

        Applications.ePreference.putTotalGpoint((double)0);
        Applications.ePreference.putBalanceGpoint(0);
        Applications.ePreference.putNLinkedGold(0);
        Applications.ePreference.putNPurchaseGold(0);

        Applications.preference.pclear();

        Applications.dbHelper.initDatabase();

        Applications.isStart = false;
        Applications.isPopup = false;
        Applications.isHomeRefresh = true;
        Applications.isCashPopRefresh = true;
        Applications.isEventRefresh = true;
        Applications.isStoreRefresh = true;
        Applications.isSettingRefresh = true;
        Applications.isSettingNOticeRefresh = true;

        FileCacheFactory.initialize(EventActivity.this);
        if (FileCacheFactory.getInstance().has(CommonUtil.cacheNameInvite)) {
            FileCacheFactory.getInstance().get(CommonUtil.cacheNameInvite).clear();
        }
        if (FileCacheFactory.getInstance().has(CommonUtil.cacheName)) {
            FileCacheFactory.getInstance().get(CommonUtil.cacheName).clear();
        }
        if (FileCacheFactory.getInstance().has(CommonUtil.cacheNameNotice)) {
            FileCacheFactory.getInstance().get(CommonUtil.cacheNameNotice).clear();
        }
        if (FileCacheFactory.getInstance().has(CommonUtil.cacheNameHistory)) {
            FileCacheFactory.getInstance().get(CommonUtil.cacheNameHistory).clear();
        }
        if (FileCacheFactory.getInstance().has(CommonUtil.noticeCache)) {
            FileCacheFactory.getInstance().get(CommonUtil.noticeCache).clear();
        }
        if (FileCacheFactory.getInstance().has(CommonUtil.inviteCache)) {
            FileCacheFactory.getInstance().get(CommonUtil.inviteCache).clear();
        }
        if (FileCacheFactory.getInstance().has(CommonUtil.adCache)) {
            FileCacheFactory.getInstance().get(CommonUtil.adCache).clear();
        }
        if (FileCacheFactory.getInstance().has(CommonUtil.eventCache)) {
            FileCacheFactory.getInstance().get(CommonUtil.eventCache).clear();
        }
        if (FileCacheFactory.getInstance().has(CommonUtil.storeCache)) {
            FileCacheFactory.getInstance().get(CommonUtil.storeCache).clear();
        }
        if (FileCacheFactory.getInstance().has(CommonUtil.storeNewCache)) {
            FileCacheFactory.getInstance().get(CommonUtil.storeNewCache).clear();
        }
        if (FileCacheFactory.getInstance().has(CommonUtil.noticePopCache)) {
            FileCacheFactory.getInstance().get(CommonUtil.noticePopCache).clear();
        }

        Intent intent = new Intent(EventActivity.this, SignActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public void setLinkedGold(double linkedGold) {
        Applications.ePreference.putNLinkedGold(linkedGold);
    }


    public void setNormalGold(double normalGold) {
        Applications.ePreference.putBalanceGpoint(normalGold);
    }

    public void requestMyInfo() {
        requestInfo();
    }

    private synchronized void requestInfo() {
        ShowLoadingProgress();
        Applications.isMissionRefresh = false;
        HashMap<String, String> map = new HashMap<>();
        map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID, ""));
        map.put(CommonUtil.KEY_ADID, Applications.preference.getValue(Preference.AD_ID, ""));
        map.put(CommonUtil.KEY_DEVICE_TOKEN, Applications.preference.getValue(Preference.DEVICE_TOKEN, ""));
        map.put(CommonUtil.KEY_PHONE_NM, Applications.preference.getValue(Preference.PHONE_NM, ""));
        int version = CommonUtil.getVersionCode(EventActivity.this);
        map.put(CommonUtil.KEY_NAME, version + "");
        map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_GET_INFO);
        String param = APICrypto.getParam(EventActivity.this, map, CommonUtil.SHARED_KEY);
        requestAsyncTask(param, CommonUtil.ACTION_GET_INFO);
    }

    public void goHistory() {
        startActivity(new Intent(EventActivity.this, HistoryActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    public void goToMission() {
        startActivity(new Intent(EventActivity.this, MissionActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    public long getRequestTimeStamp() {
        return requestTimeStamp;
    }
}
