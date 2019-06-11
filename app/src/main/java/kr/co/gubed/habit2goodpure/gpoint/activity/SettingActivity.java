package kr.co.gubed.habit2goodpure.gpoint.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

import kr.co.gubed.habit2goodpure.gpoint.filecache.FileCache;
import kr.co.gubed.habit2goodpure.gpoint.filecache.FileCacheFactory;
import kr.co.gubed.habit2goodpure.gpoint.listener.AsyncTaskCompleteListener;
import kr.co.gubed.habit2goodpure.gpoint.model.NetworkErrorModel;
import kr.co.gubed.habit2goodpure.gpoint.util.APICrypto;
import kr.co.gubed.habit2goodpure.gpoint.util.Applications;
import kr.co.gubed.habit2goodpure.gpoint.util.AsyncHTTPPost;
import kr.co.gubed.habit2goodpure.gpoint.util.CommonUtil;
import kr.co.gubed.habit2goodpure.gpoint.util.Preference;
import kr.co.gubed.habit2goodpure.gpoint.view.CashPopDialog;
import kr.co.gubed.habit2goodpure.gpoint.view.LoadingDialog;
import kr.co.gubed.habit2goodpure.gpoint.view.NetworkDialog;
import kr.co.gubed.habit2goodpure.R;

public class SettingActivity extends Activity implements View.OnClickListener, AsyncTaskCompleteListener<String> {

    private String TAG = this.getClass().toString();

    private TextView tv_title;
    private Button btn_back;
    private Button btn_info;

    private TextView tv_version;

    private ToggleButton toggle_alarm;

    private RelativeLayout btn_autocash_store;

    private RelativeLayout btn_profile;
    private RelativeLayout btn_transfer;
    private RelativeLayout btn_notice;
    private TextView tv_notice_cnt;
    private RelativeLayout btn_faq;
    private TextView tv_faq_cnt;
    private RelativeLayout btn_support;
    private TextView tv_import_my_profile;

    private RelativeLayout btn_out;

    private TextView btn_user;
    private TextView btn_privacy;

    private CashPopDialog cashPopDialog;
    private LoadingDialog loadingDialog;
    private NetworkDialog networkDialog;
    
    private Applications applications;
    private Tracker tracker;

    private String analiticsCategory = "/setting";

    private FileCache fileCache;

    private HashMap<String, NetworkErrorModel> networkErrorHash;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tsetting);

        Applications.isSettingRefresh = true;
        Applications.isSettingNOticeRefresh = true;

        applications = (Applications)getApplication();
        tracker = applications.getDefaultTracker();
        tracker.setScreenName(analiticsCategory);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        this.init();

    }

    @Override
    public void onDestroy() {
        Log.e(TAG,"onDestroy");

        try{
            if( cashPopDialog != null && cashPopDialog.isShowing()){
                cashPopDialog.dismiss();
                cashPopDialog = null;
            }
        }catch (Exception ignore){
        }

        Applications.isSettingRefresh = true;
        Applications.isSettingNOticeRefresh = true;
        super.onDestroy();
    }

    @Override
    public void onStart() {
        Log.e(TAG,"onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.e(TAG,"onResume");
        refresh();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        try{
            if( loadingDialog != null && loadingDialog.isShowing()){
                loadingDialog.dismiss();
                loadingDialog = null;
            }
            if( networkDialog != null && networkDialog.isShowing()){
                networkDialog.dismiss();
                networkDialog = null;
            }
        }catch (Exception ignore){
            ignore.printStackTrace();
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void refresh(){
        Log.e(TAG,"refresh");
        if( Applications.isSettingRefresh){
            Applications.isSettingRefresh = false;
        }
        settingRefresh();
        if( Applications.isSettingNOticeRefresh){
            Applications.isSettingNOticeRefresh = false;
            int noticeCnt = Applications.dbHelper.getNoticeNewCnt("1");
            if( noticeCnt > 0){
                tv_notice_cnt.setText(noticeCnt+"");
                tv_notice_cnt.setVisibility(View.VISIBLE);
            }else{
                tv_notice_cnt.setVisibility(View.GONE);
            }
            int faqCnt = Applications.dbHelper.getNoticeNewCnt("2");
            if( faqCnt > 0){
                tv_faq_cnt.setText(faqCnt+"");
                tv_faq_cnt.setVisibility(View.VISIBLE);
            }else{
                tv_faq_cnt.setVisibility(View.GONE);
            }
        }
    }

    public void init(){

        tv_title = (TextView)findViewById(R.id.tv_title);
        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_info = (Button)findViewById(R.id.btn_info);
        btn_info.setOnClickListener(this);

        toggle_alarm = (ToggleButton)findViewById(R.id.toggle_alarm);
        toggle_alarm.setOnClickListener(this);

        btn_autocash_store = (RelativeLayout)findViewById(R.id.btn_autocash_store);
        btn_autocash_store.setOnClickListener(this);

        btn_profile = (RelativeLayout)findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(this);

        btn_transfer = (RelativeLayout)findViewById(R.id.btn_transfer);
        btn_transfer.setOnClickListener(this);

        btn_notice = (RelativeLayout)findViewById(R.id.btn_notice);
        btn_notice.setOnClickListener(this);

        tv_notice_cnt = (TextView)findViewById(R.id.tv_notice_cnt);

        btn_faq = (RelativeLayout)findViewById(R.id.btn_faq);
        btn_faq.setOnClickListener(this);

        tv_faq_cnt = (TextView)findViewById(R.id.tv_faq_cnt);

        btn_support = (RelativeLayout)findViewById(R.id.btn_support);
        btn_support.setOnClickListener(this);

        tv_version = (TextView)findViewById(R.id.tv_version);
        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info.versionName;
        tv_version.setText(getResources().getString(R.string.setting_version, version));

        btn_out = (RelativeLayout)findViewById(R.id.btn_out);
        btn_out.setOnClickListener(this);

        btn_user = (TextView)findViewById(R.id.btn_user);
        btn_user.setOnClickListener(this);

        btn_privacy = (TextView)findViewById(R.id.btn_privacy);
        btn_privacy.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            btn_user.setText(Html.fromHtml("<u>"+getResources().getString(R.string.user).replaceAll("\n", "<br>")+"</u>", Html.FROM_HTML_MODE_LEGACY));
            btn_privacy.setText(Html.fromHtml("<u>"+getResources().getString(R.string.privacy).replaceAll("\n", "<br>")+"</u>", Html.FROM_HTML_MODE_LEGACY));
        }else{
            btn_user.setText(Html.fromHtml("<u>"+getResources().getString(R.string.user).replaceAll("\n", "<br>")+"</u>"));
            btn_privacy.setText(Html.fromHtml("<u>"+getResources().getString(R.string.privacy).replaceAll("\n", "<br>")+"</u>"));
        }

        tv_import_my_profile = (TextView)findViewById(R.id.tv_import_my_profile);

        cashPopDialog = new CashPopDialog(this);

        getFileCache();


    }

    public FileCache getFileCache(){
        if( fileCache == null){
            FileCacheFactory.initialize(this);
            if( !FileCacheFactory.getInstance().has(CommonUtil.noticeCache)){
                FileCacheFactory.getInstance().create(CommonUtil.noticeCache, 1024*4);
            }
            fileCache = FileCacheFactory.getInstance().get(CommonUtil.noticeCache);
        }
        return fileCache;
    }

    private void requestSetting(){
        boolean isCache = false;
        String cacheRst;
        if( fileCache.get(CommonUtil.noticeCache) != null){
            try{
                InputStream is = fileCache.get(CommonUtil.noticeCache).getInputStream();
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                cacheRst = new String(buffer);
                Log.e(TAG, cacheRst);
                JSONObject job = new JSONObject(cacheRst);
                isCache = System.currentTimeMillis() - Long.parseLong(job.getString(CommonUtil.KEY_TIMESTAMP)) < 60 * 60 * 1000 * 12;
            }catch (Exception e){
                isCache = false;
                e.printStackTrace();
            }

        }
        if( !isCache){
            ShowLoadingProgress();
            HashMap<String, String> map = new HashMap<>();
            map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID, ""));
            map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_GET_NOTICE);
            String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
            requestAsyncTask(param, CommonUtil.ACTION_GET_NOTICE);
        }
    }

    private void settingRefresh(){
        String import_my_profile = "";
        if( !Applications.preference.getValue(Preference.USER_ID,"").equals("")){
            import_my_profile += Long.toString(Integer.parseInt(Applications.preference.getValue(Preference.USER_ID,"")),36);
        }
        if( !Applications.preference.getValue(Preference.BIRTH,"").equals("")){
            import_my_profile += " / "+Applications.preference.getValue(Preference.BIRTH,"");
        }
        if( !Applications.preference.getValue(Preference.GENDER,"").equals("")){
            if( Applications.preference.getValue(Preference.GENDER,"").equals("1")){
                import_my_profile += " / "+getResources().getString(R.string.male);
            }else if( Applications.preference.getValue(Preference.GENDER,"").equals("2")) {
                import_my_profile += " / "+getResources().getString(R.string.female);
            }
        }
        if( !Applications.preference.getValue(Preference.LOCATION,"").equals("")){
            import_my_profile += " / "+Applications.preference.getValue(Preference.LOCATION,"");
        }
        tv_import_my_profile.setText(import_my_profile);
        toggle_alarm.setChecked(Applications.preference.getValue(Preference.CASH_POP_ALARM, true));
    }

    private void updateAlarm(boolean isChecked){
        String m;
        if( isChecked){
            m = "1";
        }else{
            m = "2";
        }
        HashMap<String, String> map = new HashMap<>();
        map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID,""));
        map.put(CommonUtil.KEY_ALARM, m);
        map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_ALARM);
        String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
        requestAsyncTask(param, CommonUtil.ACTION_ALARM);
        Applications.preference.put(Preference.CASH_POP_ALARM, isChecked);
        if( isChecked) {
            FirebaseMessaging.getInstance().subscribeToTopic(Applications.getTopicId(this));
        }else{
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Applications.getTopicId(this));
        }
    }

    private void accountTransfer(final String email){
        cashPopDialog = new CashPopDialog(this);
        cashPopDialog.setCpTitle(getResources().getString(R.string.email_chk));
        cashPopDialog.setCpDesc(getResources().getString(R.string.email_chk_desc, email));
        cashPopDialog.setCpCancelButton(getResources().getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cashPopDialog.dismiss();
            }
        });
        cashPopDialog.setCpOkButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cashPopDialog.dismiss();
                ShowLoadingProgress();
                HashMap<String, String> map = new HashMap<>();
                map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID,""));
                map.put(CommonUtil.KEY_EMAIL, email);
                map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_ACCOUNT_TRANSFER);
                String param = APICrypto.getParam(SettingActivity.this, map, CommonUtil.SHARED_KEY);
                requestAsyncTask(param, CommonUtil.ACTION_ACCOUNT_TRANSFER);
                fileCache.clear();
            }
        });
        cashPopDialog.show();
    }

    public void ShowLoadingProgress() {
        //show loading
        try {
            if( loadingDialog == null){
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
                    loadingDialog.dismiss();
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
        });
    }

    public void showErrorNetwork(final String param, final String action, final String type){
        System.out.println("action : "+action+", type : "+type+", param : "+param);
        HideLoadingProgress();
        if( networkErrorHash == null){
            networkErrorHash = new HashMap<>();
        }
        if( networkErrorHash.get(param+action+type) == null){
            NetworkErrorModel networkErrorModel = new NetworkErrorModel();
            networkErrorModel.setAction(action);
            networkErrorModel.setParam(param);
            networkErrorModel.setType(type);
            networkErrorHash.put(param+action+type, networkErrorModel);
        }

        if( networkDialog == null){
            networkDialog = new NetworkDialog(SettingActivity.this);
        }
        if( !networkDialog.isShowing()) {
            networkDialog.setCancelClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HideLoadingProgress();
                    networkErrorHash.clear();
                    networkDialog.dismiss();
                    ActivityCompat.finishAffinity(SettingActivity.this);
                }
            });
            networkDialog.setOkClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowLoadingProgress();
                    System.out.println(networkErrorHash.size());
                    synchronized (networkErrorHash) {
                        try {
                            for(String key : networkErrorHash.keySet()) {
                                NetworkErrorModel networkErrorModel = networkErrorHash.get(key);
                                System.out.println(String.format("action : %s,  키 : %s, 값 : %s", networkErrorModel.getAction(), key, networkErrorHash.get(key)));
                                requestAsyncTask(networkErrorModel.getParam(), networkErrorModel.getAction());
                                //networkErrorHash.remove(key);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
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

    public void requestSignOut(){
        ShowLoadingProgress();
        HashMap<String, String> map = new HashMap<>();
        map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID,""));
        map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_SIGN_OUT);
        String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
        requestAsyncTask(param, CommonUtil.ACTION_ACCOUNT_TRANSFER);
    }

    public void requestAsyncTask(String param, String action){
        if( Applications.getCountry(this).equals("KR") && !Applications.isRoaming(this)) {
            new AsyncHTTPPost(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, CommonUtil.SERVER_URL, param, action);
        }else{
            new AsyncHTTPPost(this).execute(CommonUtil.SERVER_URL, param, action);
        }
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_info:
                CommonUtil.showSupport(SettingActivity.this, true);
                break;
            case R.id.btn_giftbox:
                goToGiftBox();
                break;
            case R.id.toggle_alarm:
                updateAlarm(toggle_alarm.isChecked());
                break;
            case R.id.btn_profile:
                goToProfile();
                break;
            case R.id.btn_transfer:
                cashPopDialog.setCpTitle(this.getResources().getString(R.string.account_transfer));
                cashPopDialog.setCpDesc(this.getResources().getString(R.string.account_transfer_desc));
                cashPopDialog.setCpEdit(this.getResources().getString(R.string.email), InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                cashPopDialog.setCpOkButton(this.getResources().getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = cashPopDialog.getCpEditText();
                        if( email.equals("")){
                            cashPopDialog.setCpEditorError(getResources().getString(R.string.input_email));
                        }else{
                            if( CommonUtil.isEmailValid(email)){
                                cashPopDialog.dismiss();
                                accountTransfer(email);
                            }else{
                                cashPopDialog.setCpEditorError(getResources().getString(R.string.invalid_email_address));
                            }
                        }
                    }
                });
                cashPopDialog.setCpCancelButton(this.getResources().getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cashPopDialog.dismiss();
                    }
                });
                cashPopDialog.show();
                break;
            case R.id.btn_notice:
                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/Notice_click").build());
                goToNotice();
                break;
            case R.id.btn_faq:
                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/help_click").build());
                goToFaq();
                break;
            case R.id.btn_support:
                CommonUtil.showSupport(SettingActivity.this, true);
                break;
            case R.id.btn_user:
                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/terms_Terms_of_Use_click").build());
                goToTerm();
                break;
            case R.id.btn_privacy:
                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/terms_Privacy_Policy_click").build());
                goToPrivate();
                break;
            case R.id.btn_out:
                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/out_click").build());
                cashPopDialog = new CashPopDialog(this);
                cashPopDialog.setCpTitle(getResources().getString(R.string.out));
                cashPopDialog.setCpDesc(getResources().getString(R.string.out_desc));
                cashPopDialog.setCpOkButton(this.getResources().getString(R.string.sign_out), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cashPopDialog.dismiss();
                        requestSignOut();
                    }
                });
                cashPopDialog.setCpCancelButton(this.getResources().getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cashPopDialog.dismiss();
                    }
                });
                cashPopDialog.setCpCancel(true);
                cashPopDialog.show();
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
            JSONObject jo = new JSONObject(rst);
            String error = jo.getString(CommonUtil.RESULT_ERROR);
            String action = jo.getString(CommonUtil.RESULT_ACTION);
            if( error != null && error.isEmpty() && action != null && !action.isEmpty()){
                switch (action) {
                    case CommonUtil.ACTION_ACCOUNT_TRANSFER: {
                        String email = jo.getString(CommonUtil.RESULT_EMAIL);
                        cashPopDialog = new CashPopDialog(this);
                        cashPopDialog.setCpTitle(this.getResources().getString(R.string.transferTitle));
                        cashPopDialog.setCpDesc(this.getResources().getString(R.string.transferDesc));
                        cashPopDialog.setCpCancel(false);
                        cashPopDialog.setCpOkButton(this.getResources().getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cashPopDialog.dismiss();
                                Logout();
                            }
                        });
                        cashPopDialog.show();
                        break;
                    }
                    case CommonUtil.ACTION_SIGN_OUT:
                        cashPopDialog = new CashPopDialog(this);
                        cashPopDialog.setCpTitle(getResources().getString(R.string.out_result));
                        cashPopDialog.setCpDesc(getResources().getString(R.string.out_result_desc));
                        cashPopDialog.setCpOkButton(getResources().getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Logout();
                            }
                        });
                        cashPopDialog.setCpCancel(false);
                        cashPopDialog.show();
                        break;
                }
            }else if( error != null && error.equals(CommonUtil.ERROR_NO_USER)){
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
        try{
            Log.e(TAG, action);
            if( action.equals(CommonUtil.ACTION_GET_NOTICE)){
                //skip
            }else if( action.equals(CommonUtil.ACTION_ADPOP)){
                showErrorNetwork(param, action, "setting");
            }else if( action.equals(CommonUtil.ACTION_ALARM)){
                showErrorNetwork(param, action, "setting");
            }else if( action.equals(CommonUtil.ACTION_ACCOUNT_TRANSFER)){
                showErrorNetwork(param, action, "setting");
            }else if( action.equals(CommonUtil.ACTION_GET_MYGIFT)){
                //skip
            }
        }catch (Exception ignore){
        }
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

        FileCacheFactory.initialize(SettingActivity.this);
        if( FileCacheFactory.getInstance().has(CommonUtil.cacheNameInvite)){
            FileCacheFactory.getInstance().get(CommonUtil.cacheNameInvite).clear();
        }
        if( FileCacheFactory.getInstance().has(CommonUtil.cacheName)){
            FileCacheFactory.getInstance().get(CommonUtil.cacheName).clear();
        }
        if( FileCacheFactory.getInstance().has(CommonUtil.cacheNameNotice)){
            FileCacheFactory.getInstance().get(CommonUtil.cacheNameNotice).clear();
        }
        if( FileCacheFactory.getInstance().has(CommonUtil.cacheNameHistory)){
            FileCacheFactory.getInstance().get(CommonUtil.cacheNameHistory).clear();
        }
        if( FileCacheFactory.getInstance().has(CommonUtil.noticeCache)){
            FileCacheFactory.getInstance().get(CommonUtil.noticeCache).clear();
        }
        if( FileCacheFactory.getInstance().has(CommonUtil.inviteCache)){
            FileCacheFactory.getInstance().get(CommonUtil.inviteCache).clear();
        }
        if( FileCacheFactory.getInstance().has(CommonUtil.adCache)){
            FileCacheFactory.getInstance().get(CommonUtil.adCache).clear();
        }
        if( FileCacheFactory.getInstance().has(CommonUtil.eventCache)){
            FileCacheFactory.getInstance().get(CommonUtil.eventCache).clear();
        }
        if( FileCacheFactory.getInstance().has(CommonUtil.storeCache)){
            FileCacheFactory.getInstance().get(CommonUtil.storeCache).clear();
        }
        if( FileCacheFactory.getInstance().has(CommonUtil.storeNewCache)){
            FileCacheFactory.getInstance().get(CommonUtil.storeNewCache).clear();
        }
        if( FileCacheFactory.getInstance().has(CommonUtil.noticePopCache)){
            FileCacheFactory.getInstance().get(CommonUtil.noticePopCache).clear();
        }

        Intent intent = new Intent(SettingActivity.this, SignActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public void goToGiftBox() {
        startActivity(new Intent(SettingActivity.this, GiftBoxActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToProfile(){
        startActivity(new Intent(SettingActivity.this, ProfileActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToNotice() {
        startActivity(new Intent(SettingActivity.this, NoticeActivity.class).putExtra(CommonUtil.KEY_BOARDTYPE, "1"));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToFaq() {
        startActivity(new Intent(SettingActivity.this, NoticeActivity.class).putExtra(CommonUtil.KEY_BOARDTYPE, "2"));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToTerm() {
        startActivity(new Intent(SettingActivity.this, TermsActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToPrivate() {
        startActivity(new Intent(SettingActivity.this, PrivateActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
