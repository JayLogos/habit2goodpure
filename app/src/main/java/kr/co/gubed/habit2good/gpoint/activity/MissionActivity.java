package kr.co.gubed.habit2good.gpoint.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.buzzvil.buzzad.sdk.BuzzAd;
import com.buzzvil.buzzad.sdk.UserProfile;
import com.fpang.lib.FpangSession;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.messaging.FirebaseMessaging;
import com.igaworks.IgawCommon;
import com.igaworks.adpopcorn.IgawAdpopcorn;
import com.igaworks.adpopcorn.interfaces.IAdPOPcornEventListener;
import com.kyad.adlibrary.AppAllOfferwallSDK;
import com.nextapps.naswall.NASWall;
import com.nextapps.naswall.NASWallAdInfo;
import com.tnkfactory.ad.NativeAdItem;
import com.tnkfactory.ad.NativeAdManager;
import com.tnkfactory.ad.NativeAdManagerListener;
import com.tnkfactory.ad.TnkSession;
import com.tnkfactory.ad.VideoAdListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;

import kr.co.gubed.habit2good.R;
import kr.co.gubed.habit2good.gpoint.adapter.MissionAdapter;
import kr.co.gubed.habit2good.gpoint.filecache.ByteProviderUtil;
import kr.co.gubed.habit2good.gpoint.filecache.FileCache;
import kr.co.gubed.habit2good.gpoint.filecache.FileCacheFactory;
import kr.co.gubed.habit2good.gpoint.listener.AsyncTaskCompleteListener;
import kr.co.gubed.habit2good.gpoint.listener.CpiListener;
import kr.co.gubed.habit2good.gpoint.listener.RecyclerItemClickListener;
import kr.co.gubed.habit2good.gpoint.model.AdModel;
import kr.co.gubed.habit2good.gpoint.model.LastModel;
import kr.co.gubed.habit2good.gpoint.model.NetworkErrorModel;
import kr.co.gubed.habit2good.gpoint.model.TitleModel;
import kr.co.gubed.habit2good.gpoint.util.APICrypto;
import kr.co.gubed.habit2good.gpoint.util.Applications;
import kr.co.gubed.habit2good.gpoint.util.AsyncHTTPPost;
import kr.co.gubed.habit2good.gpoint.util.CommonUtil;
import kr.co.gubed.habit2good.gpoint.util.EPreference;
import kr.co.gubed.habit2good.gpoint.util.Preference;
import kr.co.gubed.habit2good.gpoint.view.CashPopDialog;
import kr.co.gubed.habit2good.gpoint.view.CpiDialog;
import kr.co.gubed.habit2good.gpoint.view.LoadingDialog;
import kr.co.gubed.habit2good.gpoint.view.NetworkDialog;
import kr.co.gubed.habit2good.gpoint.view.NoticeDialog;

public class MissionActivity extends Activity implements View.OnClickListener, AsyncTaskCompleteListener<String>, AppAllOfferwallSDK.AppAllOfferwallSDKListener {

    private String TAG = this.getClass().toString();
    
    private TextView tv_title;
    private Button btn_back;
    private Button btn_info;

    private LinearLayout layer_gold_coin;
    private ImageView iv_my_gpoint;
    private TextView tv_my_gpoint;
    private TextView tv_my_trophy;

    private RecyclerView recyclerView;
    private MissionAdapter missionAdapter;

    private LoadingDialog loadingDialog;
    private NetworkDialog networkDialog;
    private CashPopDialog cashPopDialog;
    private NoticeDialog noticeDialog;

    private HashMap<String, NetworkErrorModel> networkErrorHash;

    private Applications applications;
    private Tracker tracker;

    private String analiticsCategory = "/tc_mission";

    private ArrayList<JSONObject> cashpopList;
    private ArrayList<NativeAdItem> tnkList;
    private ArrayList<NASWallAdInfo> nasList;
    private ArrayList<Object> houseList;
    private ArrayList<Object> contentList;
    private ArrayList<Object> offerList;
    private ArrayList<Object> adList;
    private HashMap<String, String> wallsMap;
    private HashMap<String, Integer> wallsOrderMap;
    private HashMap<String, JSONObject> wallsTypeMap;
    private HashMap<String, String> wallsAdNo;
    private HashMap<String, Integer> missionOrderMap;

    private boolean isCashPopAd = false;
    private boolean isAdNetworkTnk = false;
    private boolean isAdNetworkNas = false;
    //    private boolean isFyberVideo = false;
    private boolean isTnkVideo = false;

    private FileCache fileCache;

    private Handler handler = new Handler();
    private NativeAdManager adManager = null;
    
    private Runnable videoButtonShowRunnable = new Runnable() {
        @Override
        public void run() {
            if( TnkSession.hasVideoAd(MissionActivity.this, "movie_ad")) {
                isTnkVideo = true;
            }else{
            }
            isTnkVideo = false;
        }
    };

    public static boolean isFirst = true;

    private Timer timer;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmission_new);

        Applications.isCashPopRefresh = true;

        applications = (Applications)getApplication();
        tracker = applications.getDefaultTracker();
        tracker.setScreenName(analiticsCategory);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        getFileCache();

        this.init();

    }

    @Override
    protected void onResume() {
        Log.e(TAG,"onResume");

        try {
            NASWall.embedOnResume();
        }catch(Exception ignore){}
        super.onResume();
        refresh();
        try {
            IgawCommon.startSession(this);
        } catch (Exception e) {
            Log.d("", e.getLocalizedMessage());
        }
        versionChk();
        applications.setRefreshActivity(this);
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
        try{
            IgawCommon.endSession();
        }catch (Exception ignore){}
    }
    
    @Override
    public void onStart() {
        Log.e(TAG,"onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
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

        Applications.isCashPopRefresh = true;
        super.onDestroy();
        isFirst = true;
    }
    
    public void init(){

        cashpopList = new ArrayList<>();
        tnkList = new ArrayList<>();
        nasList = new ArrayList<>();
        offerList = new ArrayList<>();
        adList = new ArrayList<>();
        houseList = new ArrayList<>();
        contentList = new ArrayList<>();

        wallsMap = new HashMap<>();
        wallsOrderMap = new HashMap<>();
        wallsTypeMap = new HashMap<>();
        wallsAdNo = new HashMap<>();
        missionOrderMap = new HashMap<>();

        tv_title = (TextView)findViewById(R.id.tv_title);
        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_info = (Button)findViewById(R.id.btn_info);
        btn_info.setOnClickListener(this);

        layer_gold_coin = (LinearLayout)findViewById(R.id.layer_gold_coin);
        layer_gold_coin.setOnClickListener(this);

        iv_my_gpoint = (ImageView)findViewById(R.id.iv_my_gpoint);
        tv_my_gpoint = (TextView)findViewById(R.id.tv_my_gpoint);
        tv_my_gpoint.setText(CommonUtil.setComma(Applications.ePreference.getBalanceGpoint() + "", true, false));

        tv_my_trophy = (TextView)findViewById(R.id.tv_my_trophy);
        Integer trophy = Applications.ePreference.getValue(EPreference.N_TROPHY, 0);
        tv_my_trophy.setText(String.format("%,d", trophy));

        //tv_my_trophy.setText(CommonUtil.setComma(Applications.ePreference.getValue(EPreference.N_TROPHY, 0) + "", false, false));

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        missionAdapter = new MissionAdapter(MissionActivity.this, adList);
        recyclerView.setAdapter(missionAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return Math.min(2, missionAdapter.getItemViewType(position));
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if( missionAdapter.getItem(position) != null) {
/* ADMOB                   if( missionAdapter.getItem(position) instanceof AdView){

                    }else {
                        setRowClick(view, position);
                    }*/
                    setRowClick(view, position);
                }else{
                    missionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        /** buzzvill **/
        BuzzAd.init("new_key", this);
        UserProfile userProfile = BuzzAd.getUserProfile();
        if( !Applications.preference.getValue(Preference.BIRTH,"").equals("")){
            try{
                int birth = Integer.parseInt(Applications.preference.getValue(Preference.BIRTH,""));
                userProfile.setBirthYear(birth);
            }catch (Exception ignore){
            }
        }
        if( !Applications.preference.getValue(Preference.GENDER,"").equals("")){
            if( Applications.preference.getValue(Preference.GENDER,"").equals("1")){
                userProfile.setGender(UserProfile.USER_GENDER_MALE);
            }else if( Applications.preference.getValue(Preference.GENDER,"").equals("2")) {
                userProfile.setGender(UserProfile.USER_GENDER_FEMALE);
            }
        }

        /** buzzvill **/

        /** appall **/
        AppAllOfferwallSDK.getInstance().initOfferWall(MissionActivity.this, "dc80fbaf4608150088199ef2ad295efd17003670", Applications.preference.getValue(Preference.USER_ID,""));
        /** appall **/

    }


    public void refresh(){
        ShowLoadingProgress();
        if( Applications.isOfferWall){
            Applications.isHomeRefresh = true;
        }
        requestAdvertise();
        requestNative();
        Applications.isCashPopRefresh = false;

        if( Applications.isMissionRefresh){
            requestInfo();
        }

    }
    
    private void requestAdvertise(){
        boolean isCache = false;
        String cacheRst = "";
        if( fileCache.get(CommonUtil.adCache) != null){
            try{
                InputStream is = fileCache.get(CommonUtil.adCache).getInputStream();
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                cacheRst = new String(buffer);
                Log.e(TAG, cacheRst);
                JSONObject job = new JSONObject(cacheRst);
                Log.e(TAG, System.currentTimeMillis() - Long.parseLong(job.getString(CommonUtil.KEY_TIMESTAMP)) + "");
                Log.e(TAG, (60 * 60 * 1000) + "");
                isCache = System.currentTimeMillis() - Long.parseLong(job.getString(CommonUtil.KEY_TIMESTAMP)) < 60 * 60 * 1000;
                Log.e(TAG, isCache+"");
                JSONObject jo = new JSONObject(job.getString(CommonUtil.KEY_RST));
                JSONArray jobjArr = new JSONArray(jo.getString("walls"));
                if( jobjArr.length() == 0){
                    isCache = false;
                }
            }catch (Exception e){
                isCache = false;
                e.printStackTrace();
            }
        }
        if( isCache){
            try{
                JSONObject job = new JSONObject(cacheRst);
                JSONObject jo = new JSONObject(job.getString(CommonUtil.KEY_RST));
                try {
                    JSONArray jobjArr = new JSONArray(jo.getString("offers"));
                    cashpopList.clear();
                    for (int i = 0; i < jobjArr.length(); i++) {
                        cashpopList.add(jobjArr.getJSONObject(i));
                    }
                }catch (Exception ignore){
                    ignore.printStackTrace();
                }
                isCashPopAd = true;
                wallsMap.clear();
                wallsOrderMap.clear();
                wallsTypeMap.clear();
                wallsAdNo.clear();
                missionOrderMap.clear();
                JSONArray missions = new JSONArray(jo.getString("missions"));
                for(int i=0;i<missions.length();i++){
                    missionOrderMap.put(missions.getJSONObject(i).getString("code"), missions.getJSONObject(i).getInt("orderNo"));
                }

                if( missionOrderMap.get("native") != null){
                    requestNative();
                    isAdNetworkNas = true;
                    isAdNetworkTnk = true;
                }else{
                    isAdNetworkNas = true;
                    isAdNetworkTnk = true;
                }

                Applications.isAdmob = false;
                JSONArray walls = new JSONArray(jo.getString("walls"));
                for(int i=0;i<walls.length();i++){
                    Log.e("code",""+walls.getJSONObject(i).getString("code"));
                    if( walls.getJSONObject(i).getString("code").equals(CommonUtil.NATIVE_ADMOB)){
                        Applications.isAdmob = true;
                    }else{
                        wallsMap.put(walls.getJSONObject(i).getString("code"), walls.getJSONObject(i).getString("cash"));
                        wallsOrderMap.put(walls.getJSONObject(i).getString("code"), walls.getJSONObject(i).getInt("orderNo"));
                        wallsTypeMap.put(walls.getJSONObject(i).getString("code"), walls.getJSONObject(i));
                        wallsAdNo.put(walls.getJSONObject(i).getString("code"), walls.getJSONObject(i).getString("adNo"));
                    }
                }

                if( isAdNetworkTnk && isAdNetworkNas) {
                    setAdList();
                }
            }catch (Exception ignore){
                ignore.printStackTrace();
            }
        }else{
            HashMap<String, String> map = new HashMap<>();
            map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID,""));
            map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_GET_ADVERTISE);
            map.put(CommonUtil.KEY_IMEI, Applications.getImei(this));
            String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
            requestAsyncTask(param, CommonUtil.ACTION_GET_ADVERTISE);
        }
    }

    private void requestNative(){
        requestTnk();
        requestNas();
    }

    private void requestTnk(){
        Log.e(TAG, "requestTnk");
        isAdNetworkTnk = true;
        if( true) {
            if (tnkList == null) {
                tnkList = new ArrayList<>();
            }
            if (tnkList.isEmpty()) {
                adManager = new NativeAdManager(this, "native_ad", NativeAdItem.STYLE_ICON, 80);
                adManager.setManagerListener(new NativeAdManagerListener() {
                    @Override
                    public void onFailure(int i) {
                        Log.e("onFailure", "onFailure " + i);
                        Applications.isCashPopRefresh = true;
                        isAdNetworkTnk = true;
                        if (isCashPopAd && isAdNetworkNas) {
                            try {
                                setAdList();
                            } catch (Exception ignore) {
                            }
                        }
                    }

                    @Override
                    public void onLoad() {
                        tnkList.clear();
                        Log.e("adManager", "" + adManager);
                        for (int i = 0; i < adManager.getUniqueAdCount(); i++) {
                            tnkList.add(adManager.getAdItemAt(i));
                        }
                        Log.e("tnkList", "" + tnkList.size());
                        isAdNetworkTnk = true;
                        if (isCashPopAd && isAdNetworkNas) {
                            try {
                                setAdList();
                            } catch (Exception ignore) {
                            }

                        }
                    }
                });
                adManager.prepareAds();
            }
        }
    }

    private void requestNas(){
        if( false){
            NASWall.getAdList(this, Applications.preference.getValue(Preference.USER_ID, ""), new NASWall.OnAdListListener() {
                @Override
                public void OnSuccess(ArrayList<NASWallAdInfo> adList) {
                    nasList.clear();
                    // 광고목록 가져오기 성공
                    for(NASWallAdInfo adInfo : adList) {
                        adInfo.getTitle(); //광고명
                        adInfo.getIconUrl(); //아이콘 Url
                        adInfo.getMissionText(); //참여방법
                        adInfo.getAdPrice(); //참여비용
                        adInfo.getRewardPrice(); //적립금
                        adInfo.getRewardUnit(); // 적립금단위
                        nasList.add(adInfo);
                    }
                    isAdNetworkNas = true;
                    if( isCashPopAd && isAdNetworkTnk) {
                        try {
                            setAdList();
                        } catch (Exception ignore) {
                        }

                    }
                }

                @Override
                public void OnError(int errorCode) {
                    // 광고목록 불러오기 실패
                    isAdNetworkNas = true;
                    if( isCashPopAd && isAdNetworkTnk) {
                        try {
                            setAdList();
                        } catch (Exception ignore) {
                        }

                    }
                }
            });

        }else{
            isAdNetworkNas = true;
        }
    }

    private void setAdList() throws Exception{
        try{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("setAdList","setAdList");
                    HideLoadingProgress();
                    adList.clear();
                    houseList.clear();
                    contentList.clear();
                    try {
                        String cacheRst = "";
                        InputStream is = fileCache.get(CommonUtil.adCache).getInputStream();
                        int size = is.available();
                        byte[] buffer = new byte[size];
                        is.read(buffer);
                        is.close();
                        cacheRst = new String(buffer);
                        Log.e(TAG, cacheRst);
                        JSONObject job = new JSONObject(cacheRst);
                        JSONObject jo = new JSONObject(job.getString(CommonUtil.KEY_RST));
                        JSONArray jobjArr = new JSONArray(jo.getString("offers"));
                        cashpopList.clear();
                        for (int i = 0; i < jobjArr.length(); i++) {
                            cashpopList.add(jobjArr.getJSONObject(i));
                        }
                        isCashPopAd = true;
                        wallsMap.clear();
                        wallsOrderMap.clear();
                        wallsTypeMap.clear();
                        wallsAdNo.clear();
                        missionOrderMap.clear();
                        JSONArray missions = new JSONArray(jo.getString("missions"));
                        for (int i = 0; i < missions.length(); i++) {
                            missionOrderMap.put(missions.getJSONObject(i).getString("code"), missions.getJSONObject(i).getInt("orderNo"));
                        }

                        Applications.isAdmob = false;
                        JSONArray walls = new JSONArray(jo.getString("walls"));
                        for (int i = 0; i < walls.length(); i++) {
                            Log.e("code", "" + walls.getJSONObject(i).getString("code"));
                            if (walls.getJSONObject(i).getString("code").equals(CommonUtil.NATIVE_ADMOB)) {
                                Applications.isAdmob = true;
                            } else {
                                wallsMap.put(walls.getJSONObject(i).getString("code"), walls.getJSONObject(i).getString("cash"));
                                wallsOrderMap.put(walls.getJSONObject(i).getString("code"), walls.getJSONObject(i).getInt("orderNo"));
                                wallsTypeMap.put(walls.getJSONObject(i).getString("code"), walls.getJSONObject(i));
                                wallsAdNo.put(walls.getJSONObject(i).getString("code"), walls.getJSONObject(i).getString("adNo"));
                            }
                        }
                    }catch (Exception ignore){
                        ignore.printStackTrace();
                    }

                    if( cashpopList.size() > 0){
                        for(int i=0;i<cashpopList.size();i++){
                            JSONObject job = cashpopList.get(i);
                            AdModel adModel = new AdModel();
                            adModel.setViewType(CommonUtil.AD_TYPE_TCASH);
                            try {
                                adModel.setIsTitle(false);
                                adModel.setAdNo(job.getString("id"));
                                adModel.setAdType(job.getString("ad_type"));
                                adModel.setActionType(job.getString("action_type"));
                                adModel.setCpiType(job.getString("cpi_type"));
                                adModel.setIsRun(job.getString("isRun"));
                                if( adModel.getIsRun().equals("Y")){
                                    adModel.setRunCnt(job.getString("runCnt"));
                                    adModel.setRunStep(job.getString("runStep"));
                                    adModel.setRunReward(job.getString("runReward"));
                                    adModel.setRunCoin(job.getString("runCoin"));
                                    adModel.setRunToday(job.getString("runToday"));
                                }
                                adModel.setIsPop(job.getString("isPop"));
                                adModel.setIsDelayReward(job.getString("isDelayReward"));
                                adModel.setName(job.getString("name"));
                                adModel.setCash(job.getString("cash"));
                                adModel.setCoin(job.getString("coin"));
                                adModel.setTask(job.getString("task"));
                                adModel.setAdtxt(job.getString("adtxt"));
                                adModel.setPackage_name(job.getString("package"));
                                adModel.setTargetLink(job.getString("targetLink"));
                                adModel.setCode(job.getString("code"));
                                adModel.setIdx(job.getInt("idx"));
                                adModel.setIsAction(job.getString("isAction"));
                                adModel.setExpire(job.getLong("expire"));
                                if( !job.getJSONArray("creatives").getJSONObject(0).getString("url").startsWith("http")){
                                    adModel.setImage("http://"+job.getJSONArray("creatives").getJSONObject(0).getString("url"));
                                }else{
                                    adModel.setImage(job.getJSONArray("creatives").getJSONObject(0).getString("url"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e(""+adModel.getPackage_name(),""+Applications.dbHelper.chkCPIPackage(adModel.getPackage_name()));
                            if( adModel.getActionType().equals("1")){
                                if( adModel.getIsRun().equals("Y")){
                                    if( adModel.getRunCnt().equals(adModel.getRunStep()) || (CommonUtil.isPackageExist(MissionActivity.this, adModel.getPackage_name()) && Applications.dbHelper.chkCPIPackage(adModel.getPackage_name()) && adModel.getRunStep().equals("-1")) ){
                                        continue;
                                    }
                                }else{
                                    if( CommonUtil.isPackageExist(MissionActivity.this, adModel.getPackage_name()) && Applications.dbHelper.chkCPIPackage(adModel.getPackage_name())){
                                        continue;
                                    }
                                }
                            }
                            if( System.currentTimeMillis() > (adModel.getExpire()*1000)){
                                continue;
                            }
                            houseList.add(adModel);
                        }
                    }
                    if( tnkList == null){
                        tnkList = new ArrayList<NativeAdItem>();
                    }
                    if( tnkList.size() > 0){
                        for(int i=0;i<tnkList.size();i++){
                            contentList.add(tnkList.get(i));
                        }
                    }
                    if( nasList.size() > 0){
                        for(int i=0;i<nasList.size();i++){
                            contentList.add(nasList.get(i));
                        }
                    }
                    if( !missionOrderMap.isEmpty() && missionOrderMap.size() > 0){
                        Iterator<String> it = CommonUtil.sortByValue(missionOrderMap).iterator();
                        while( it.hasNext()){
                            String key = it.next();
                            setMissionList(key);
                        }
                    }else{
                        Log.e("isAdmob",""+Applications.isAdmob);
                        if( Applications.isAdmob){
                            //adList.add(Applications.nativeExpressAdView);
                            /*ADMOB adList.add(Applications.adView);*/
                        }
                        adList.add(new TitleModel());
                        if( !houseList.isEmpty()) {
                            while (!houseList.isEmpty()) {
                                adList.add(houseList.get(0));
                                houseList.remove(0);
                            }
                        }
                        Iterator<String> it = CommonUtil.sortByValue(wallsOrderMap).iterator();
                        while( it.hasNext()){
                            String key = it.next();
                            setAdnetwork(key);
                        }
                        if( !offerList.isEmpty()) {
                            while (!offerList.isEmpty()) {
                                adList.add(offerList.get(0));
                                offerList.remove(0);
                            }
                        }

                        if( !contentList.isEmpty()) {
                            Collections.shuffle(contentList);
                            while (!contentList.isEmpty()) {
                                adList.add(contentList.get(0));
                                contentList.remove(0);
                            }
                        }
                    }

                    adList.add(new LastModel());

                    missionAdapter.notifyDataSetChanged();
                    HideLoadingProgress();
                }
            });

        }catch (Exception ignore){
            ignore.printStackTrace();
        }finally {
            HideLoadingProgress();
            if( isFirst){
                isFirst = false;
                this.settingOffers();
            }
        }
    }

    public void setHousList(String code, int idx, boolean isIdx){
        if( isIdx){
            if( !houseList.isEmpty()) {
                ArrayList<Object> removeList = new ArrayList<>();
                for(int i = 0; i < houseList.size(); i++) {
                    if( ((AdModel)houseList.get(i)).getCode().equals(code) && ((AdModel)houseList.get(i)).getIdx() == idx){
                        adList.add(houseList.get(i));
                        removeList.add(houseList.get(i));
                    }
                }
                for(int i = 0; i < removeList.size();i++){
                    houseList.remove(removeList.get(i));
                }
                removeList.clear();
            }
        }else{
            if( !houseList.isEmpty()) {
                ArrayList<Object> removeList = new ArrayList<>();
                for (int i = 0; i < houseList.size(); i++) {
                    if( ((AdModel)houseList.get(i)).getCode().equals(code)){
                        adList.add(houseList.get(i));
                        removeList.add(houseList.get(i));
                        Log.e("admodel",""+((AdModel) houseList.get(i)).getAdNo());
                    }
                }
                for(int i = 0; i < removeList.size();i++){
                    houseList.remove(removeList.get(i));
                }
                removeList.clear();
            }
        }
    }

    public void setMissionList(String code){
        /* ADMOB if( code.equals("admob")){
            if( Applications.isAdmob){
                setHousList(code, 0, true);
                //adList.add(Applications.nativeExpressAdView);
                adList.add(Applications.adView);
                setHousList(code, 0, false);
            }
            adList.add(new TitleModel());
        }else*/ if( code.equals("offer")){
            Iterator<String> it = CommonUtil.sortByValue(wallsOrderMap).iterator();
            while( it.hasNext()){
                String key = it.next();
                setAdnetwork(key);
            }

            if( !offerList.isEmpty()) {
                int idx = 0;
                while (!offerList.isEmpty()) {
                    setHousList(code, idx, true);
                    adList.add(offerList.get(0));
                    offerList.remove(0);
                    idx++;
                }
                setHousList(code, 0, false);
            }else{
                setHousList(code, 0, false);
            }
        }else if( code.equals("native")){
            if( !contentList.isEmpty()) {
                Collections.shuffle(contentList);
                int idx = 0;
                while (!contentList.isEmpty()) {
                    setHousList(code, idx, true);
                    adList.add(contentList.get(0));
                    contentList.remove(0);
                    idx++;
                }
                setHousList(code, 0, false);
            }else{
                setHousList(code, 0, false);
            }
        }else if( code.equals("house")){
            if( !houseList.isEmpty()) {
                while (!houseList.isEmpty()) {
                    adList.add(houseList.get(0));
                    houseList.remove(0);
                }
            }
        }
    }

    private void setAdnetwork(String offerType){
        if( !offerType.equals(CommonUtil.NATIVE_ADMOB)){
            AdModel adModel = new AdModel();
            adModel.setViewType(CommonUtil.AD_TYPE_OFFER);
            adModel.setTask(offerType);
            String title = "";
            if (offerType.equals(CommonUtil.OFFER_ADPOPCORN)) {
                title = getResources().getString(R.string.offer_adpopcorn);
            }else if (offerType.equals(CommonUtil.OFFER_TNKAD)) {
                title = getResources().getString(R.string.offer_tnk);
            }else if (offerType.equals(CommonUtil.OFFER_NAS)) {
                title = getResources().getString(R.string.offer_nas);
            }else if (offerType.equals(CommonUtil.OFFER_ADSYNC)) {
                title = getResources().getString(R.string.offer_adsync);
            }else if( offerType.equals(CommonUtil.OFFER_BUZZVILL)){
                title = getResources().getString(R.string.offer_buzzvill);
            }else if( offerType.equals(CommonUtil.OFFER_APPALL)){
                title = getResources().getString(R.string.offer_appall);
            }
            adModel.setName(title);
            adModel.setCash(wallsMap.get(offerType));
            try {
                JSONObject obj = wallsTypeMap.get(offerType);
                if( obj != null) {
                    adModel.setLabelEnable(obj.getString("le"));
                    adModel.setLabel(obj.getString("l"));
                    adModel.setTextColor(obj.getString("ltc"));
                    adModel.setBackColor(obj.getString("lbc"));
                }else{
                    adModel.setLabelEnable("N");
                    adModel.setLabel("");
                    adModel.setTextColor("");
                    adModel.setBackColor("");
                }
            }catch (Exception ignore){
                adModel.setLabelEnable("N");
                adModel.setLabel("");
                adModel.setTextColor("");
                adModel.setBackColor("");
            }
            adModel.setAdNo(wallsAdNo.get(offerType));
            offerList.add(adModel);
        }
    }
    
    public void settingOffers(){
        /** adpopcorn **/
        try {
            IgawCommon.startApplication(this);
//            IgawCommon.setUserId(Applications.preference.getValue(Preference.USER_ID, ""));
            IgawCommon.setUserId(this, Applications.preference.getValue(Preference.USER_ID, ""));
            IgawAdpopcorn.setSensorLandscapeEnable(this, false);
            IgawAdpopcorn.setEventListener(this, new IAdPOPcornEventListener() {
                @Override
                public void OnClosedOfferWallPage() {
                    Log.e(TAG, "OnClosedOfferWallPage");
                    Applications.isOfferWall = true;
                    Applications.isHomeRefresh = true;
                }
            });
        }catch (Exception ignore){
            ignore.printStackTrace();
        }
        /** adpopcorn **/

        /** tnkad **/
        TnkSession.setUserName(this, Applications.preference.getValue(Preference.USER_ID,""));
        /** tnkad **/

        /** nas **/
        boolean testMode = false;
        NASWall.init(this, testMode);
        NASWall.setOnCloseListener(new NASWall.OnCloseListener() {
            @Override
            public void OnClose() {
                try {
                    Applications.isOfferWall = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Applications.isHomeRefresh = true;
                            } catch (Exception ignore) {
                            }
                        }
                    });
                }catch (Exception ignore){}
            }
        });
        /** nas **/

        /** adSync **/
        FpangSession.init(this);
        FpangSession.setDebug(false);
        FpangSession.setUserId(Applications.preference.getValue(Preference.USER_ID,""));
        /** adSync **/

    }

    /**Tnk Video request**/
    private void requestVideoAd() {
        TnkSession.prepareVideoAd(MissionActivity.this, "movie_ad", new VideoAdListener() {
                    @Override
                    public void onClose(int type) {
                        Log.e("requestVideoAd","onClose : "+type);
                    }

                    @Override
                    public void onShow() {
                        Log.e("requestVideoAd","onShow");
                    }

                    @Override
                    public void onFailure(int errCode) {
                        // onFailure() never invoked when repeat parameter is true.
                        Log.e("requestVideoAd","onFailure : "+errCode);
                    }

                    @Override
                    public void onLoad() {
                        Log.e("requestVideoAd","onLoad");
                        handler.post(videoButtonShowRunnable);
                    }

                    @Override
                    public void onVideoCompleted(boolean skipped) {
                        Log.e("requestVideoAd","onVideoCompleted");
                    }
                },
                true); // set repeat paramter to true
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == 0){
            Applications.isOfferWall = true;
            Applications.isHomeRefresh = true;
        }
        Log.e("onActivityResult","onActivityResult");
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_info:
                CommonUtil.showSupport(MissionActivity.this, true);
                break;
            case R.id.layer_gold_coin:
                goHistory();
                break;
        }
    }

    public FileCache getFileCache(){
        if( fileCache == null){
            FileCacheFactory.initialize(this);
            if( !FileCacheFactory.getInstance().has(CommonUtil.adCache)){
                FileCacheFactory.getInstance().create(CommonUtil.adCache, 1024*4);
            }
            fileCache = FileCacheFactory.getInstance().get(CommonUtil.adCache);
        }
        return fileCache;
    }

    public void versionChk(){
        long now = System.currentTimeMillis();
        String versionChk = Applications.preference.getValue(Preference.VERSION_CHK_TIMESTAMP, now+"");
        long versionTime;
        if( versionChk != null && !versionChk.equals("") && versionChk.matches("^[0-9]+$")){
            try {
                versionTime = Long.parseLong(versionChk);
            }catch (Exception ignore){
                versionTime = now;
            }
        }else{
            versionTime = now;
        }
        if( versionTime <= now) {
            Applications.preference.put(Preference.VERSION_CHK_TIMESTAMP, (now+(3600000*12))+"");
            HashMap<String, String> map = new HashMap<>();
            map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_VERSION);
            String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
            requestAsyncTask(param, CommonUtil.ACTION_VERSION);
        }else{
            int version = CommonUtil.getVersionCode(this);
            int nowVersion = version;
            try {
                nowVersion = Integer.parseInt(Applications.ePreference.getValue(EPreference.VERSION_C, ""));
            }catch (Exception ignore){}
            final String v_p = Applications.ePreference.getValue(EPreference.VERSION_P, "");
            if( version < nowVersion){
                cashPopDialog = new CashPopDialog(this);
                cashPopDialog.setCpTitle(getResources().getString(R.string.habit2good_update));
                cashPopDialog.setCpDesc(getResources().getString(R.string.habit2good_update_desc));
                cashPopDialog.setCpBOkButton(getResources().getString(R.string.update_now), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        if( v_p != null && !v_p.equals("")) {
                            intent.setData(Uri.parse("market://details?id=" + v_p));
                        }else{
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

    public void requestAsyncTask(String param, String action){
        if( Applications.getCountry(this).equals("KR") && !Applications.isRoaming(this)) {
            new AsyncHTTPPost(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, CommonUtil.SERVER_URL, param, action);
        }else{
            new AsyncHTTPPost(this).execute(CommonUtil.SERVER_URL, param, action);
        }
    }

    private void setRowClick(final View ClickView, int position){
        Log.e(TAG, "position : "+position);
        if( missionAdapter.getItem(position) instanceof AdModel) {
            final AdModel adModel = (AdModel) missionAdapter.getItem(position);
            if( !adModel.getIsTitle()){
                switch (adModel.getViewType()) {
                    case CommonUtil.AD_TYPE_OFFER:
                        if( Applications.noticeMap.get(adModel.getAdNo()) != null){
                            try {
                                JSONObject noticeObj = (JSONObject) Applications.noticeMap.get(adModel.getAdNo());
                                String notice_title = noticeObj.getString("subject");
                                String notice_content = noticeObj.getString("content");
                                noticeDialog = new NoticeDialog(this);
                                noticeDialog.setNpTitle(notice_title);
                                noticeDialog.setNpHtml(notice_content);
                                noticeDialog.setToday(false);
                                noticeDialog.setCancelable(true);
                                DisplayMetrics dm = this.getApplicationContext().getResources().getDisplayMetrics();
                                int width = dm.widthPixels;
                                int height = dm.heightPixels;
                                noticeDialog.setSize(width, height);
                                noticeDialog.show();
                                noticeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        offerWall(adModel);
                                    }
                                });
                            }catch (Exception e){
                                offerWall(adModel);
                            }
                        }else{
                            offerWall(adModel);
                        }
                        break;
                    case CommonUtil.AD_TYPE_TCASH: {
                        try {
                            if( adModel.getActionType().equals("2")/* || adModel.getActionType().equals("1") */){
                                String cacheRst = "";
                                if( fileCache.get(CommonUtil.adCache) != null){
                                    try{
                                        InputStream is = fileCache.get(CommonUtil.adCache).getInputStream();
                                        int size = is.available();
                                        byte[] buffer = new byte[size];
                                        is.read(buffer);
                                        is.close();
                                        cacheRst = new String(buffer);
                                        Log.e(TAG, cacheRst);
                                        JSONObject job = new JSONObject(cacheRst);
                                        JSONObject jo = new JSONObject(job.getString(CommonUtil.KEY_RST));
                                        JSONArray jobjArr = new JSONArray(jo.getString("offers"));
                                        for(int i=0;i<jobjArr.length();i++) {
                                            if( jobjArr.getJSONObject(i).getString("id").equals(adModel.getAdNo())){
                                                jobjArr.getJSONObject(i).put("isAction", "N");
                                                break;
                                            }
                                        }
                                        jo.put("offers", jobjArr);
                                        job.put(CommonUtil.KEY_RST, jo);
                                        String newCacheRst = job.toString();
                                        fileCache.put(CommonUtil.adCache, ByteProviderUtil.create(newCacheRst));
                                        Log.e("newCacheRst",""+newCacheRst);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }

                            boolean isAction = true;
                            if( adModel.getActionType().equals("1")){
                                //cpi
                                if( adModel.getCpiType().equals("1")){
                                    //direct
                                    if( !Applications.dbHelper.chkCPIPackage(adModel.getPackage_name())){
                                        if( CommonUtil.isPackageExist(MissionActivity.this, adModel.getPackage_name())){
                                            //request CPI
                                            if( adModel.getTask().equals("2") || adModel.getTask().equals("3")){
                                                //install+run or install+action
                                                try{
                                                    isAction = false;
                                                    requestCPI(adModel);
                                                    Intent intent = this.getPackageManager().getLaunchIntentForPackage(adModel.getPackage_name());
                                                    startActivity(intent);
                                                    Applications.isHomeRefresh = true;
                                                }catch (Exception ignore){
                                                    isAction = true;
                                                }
                                            }else{
                                                isAction = false;
                                                requestCPI(adModel);
//                                                Applications.requestInfo();
                                                Applications.isHomeRefresh = true;
                                            }
                                        }else{
                                            if( adModel.getTask().equals("2") || adModel.getTask().equals("3")) {
                                                Toast toast = Toast.makeText(MissionActivity.this, getResources().getString(R.string.cpi_run_try), Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                                                toast.show();
                                            }else{
                                                Toast toast = Toast.makeText(MissionActivity.this, getResources().getString(R.string.cpi_confirm_try), Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                                                toast.show();
                                            }
                                            isAction = true;
                                        }
                                    }else{
                                        isAction = false;
                                        if( adModel.getIsRun().equals("N") || (adModel.getIsRun().equals("Y") && adModel.getRunStep().equals("-1"))) {
                                            //show pop up
                                            final CpiDialog cpiDialog = new CpiDialog(this);
                                            cpiDialog.open(adModel, new CpiListener() {
                                                @Override
                                                public void start(AdModel adModelItem) {
                                                    if (adModel.getTask().equals("2") || adModel.getTask().equals("3")) {
                                                        Toast toast = Toast.makeText(MissionActivity.this, getResources().getString(R.string.cpi_run_go), Toast.LENGTH_SHORT);
                                                        toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                                                        toast.show();
                                                    } else {
                                                        Toast toast = Toast.makeText(MissionActivity.this, getResources().getString(R.string.cpi_confirm_go), Toast.LENGTH_SHORT);
                                                        toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                                                        toast.show();
                                                    }
                                                    Applications.dbHelper.insertCPIPackage(adModelItem.getPackage_name());
                                                    tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/house_click").build());
                                                    cpiDialog.dismiss();
                                                    String target = adModelItem.getTargetLink().replace("{userId}", Applications.preference.getValue(Preference.USER_ID, "")).replace("{af_sub4}", Applications.getImei(MissionActivity.this));
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(target));
                                                    startActivity(intent);
                                                    Applications.isHomeRefresh = true;
                                                }
                                            });
                                        }else{
                                            if( CommonUtil.isPackageExist(this, adModel.getPackage_name())){
                                                //request CPI
                                                //every day
                                                try{
                                                    try {
                                                        requestCPI(adModel);
                                                        if (FileCacheFactory.getInstance().has(CommonUtil.adCache)) {
                                                            FileCacheFactory.getInstance().get(CommonUtil.adCache).clear();
                                                        }
                                                    }catch (Exception ignore){
                                                        ignore.printStackTrace();
                                                    }
                                                    isAction = false;
                                                    Intent intent = this.getPackageManager().getLaunchIntentForPackage(adModel.getPackage_name());
                                                    startActivity(intent);
                                                    Applications.isHomeRefresh = true;
                                                }catch (Exception ignore){
                                                    isAction = true;
                                                }
                                            }else{
                                                Toast toast = Toast.makeText(this, this.getResources().getString(R.string.check_install), Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                                                toast.show();
                                                isAction = true;
                                            }
                                        }
                                    }
                                }else{
                                    //callback
                                    isAction = true;
                                }

                            }else{
                                //cpc
                                isAction = true;
                                if( adModel.getIsPop().equals("Y")){
                                    isAction = false;
                                    final CpiDialog cpiDialog = new CpiDialog(this);
                                    cpiDialog.open(adModel, new CpiListener() {
                                        @Override
                                        public void start(final AdModel adModelItem) {
                                            if( adModelItem.getIsDelayReward().equals("Y")){
                                                requestCPC(adModel);
                                            }
                                            Applications.dbHelper.insertCPIPackage(adModelItem.getPackage_name());
                                            tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/house click").build());
                                            cpiDialog.dismiss();
                                            String target = adModelItem.getTargetLink().replace("{userId}", Applications.preference.getValue(Preference.USER_ID, "")).replace("{af_sub4}", Applications.getImei(MissionActivity.this));
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(target));
                                            startActivity(intent);
                                            Applications.isHomeRefresh = true;
                                        }
                                    });
                                }else{
                                    if( adModel.getIsDelayReward().equals("Y")){
                                        requestCPC(adModel);
                                    }
                                }
                            }
                            if( isAction) {
                                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/house click").build());
                                String target = adModel.getTargetLink().replace("{userId}", Applications.preference.getValue(Preference.USER_ID, "")).replace("{af_sub4}", Applications.getImei(this));
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(target));
                                startActivity(intent);

                                refresh();
//                                Applications.requestInfo();
                                Applications.isHomeRefresh = true;
                            }
                        }catch (Exception ignore){

                        }
                        break;
                    }
                }
            }


        }else if( missionAdapter.getItem(position) instanceof NativeAdItem){
            final NativeAdItem nativeAdItem = (NativeAdItem)missionAdapter.getItem(position);
            cashPopDialog = new CashPopDialog(this);
            cashPopDialog.setCpTitle(nativeAdItem.getTitle());
            cashPopDialog.setCpDesc(nativeAdItem.getActionText());
            String offer_desc = this.getResources().getString(R.string.offer_desc_tnk);
            if( !offer_desc.equals("")) {
                cashPopDialog.setCpDescSub(offer_desc);
            }
            cashPopDialog.setCpBOkButton(this.getResources().getString(R.string.offer_go), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cashPopDialog.dismiss();
                    nativeAdItem.attachLayout((ViewGroup) ClickView, new View(MissionActivity.this));
                    nativeAdItem.onClick(new View(MissionActivity.this));
                    missionAdapter.notifyDataSetChanged();
                    Applications.isOfferWall = true;
                }
            });
            cashPopDialog.show();
        }
        /*
        else if( missionAdapter.getItem(position) instanceof AdView){
            final AdView adView = (AdView) missionAdapter.getItem(position);
            adView.callOnClick();
        }
        */
    }

    public void offerWall(AdModel adModel){
        switch (adModel.getTask()) {
            case CommonUtil.OFFER_ADPOPCORN:
                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/offer_wall_adpopcon click").build());
                IgawAdpopcorn.openOfferWall(this);
                break;
            case CommonUtil.OFFER_TNKAD:
                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/offer_wall_tnkad click").build());
                TnkSession.showAdList(this, this.getResources().getString(R.string.gpoint));
                Applications.isOfferWall = true;
                break;
            case CommonUtil.OFFER_NAS:
                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/offer_wall_nas click").build());
                NASWall.open(this, Applications.preference.getValue(Preference.USER_ID, ""));
                break;
            case CommonUtil.OFFER_ADSYNC:
                FpangSession.showAdsyncList(this, this.getResources().getString(R.string.gpoint));
                break;
            case CommonUtil.OFFER_BUZZVILL:
                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/offer_wall_buzzvill click").build());
                BuzzAd.showOfferWall(this, this.getResources().getString(R.string.gpoint), Applications.preference.getValue(Preference.USER_ID, ""));
                break;
            case CommonUtil.OFFER_APPALL:
                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/offer_wall_appall click").build());
                if( AppAllOfferwallSDK.getInstance().showAppAllOfferwall(this)) {
                    //성공
                } else {
                    Toast.makeText(this, "SDK initialization error.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void requestCPI(final AdModel adModel){
        HashMap<String, String> map = new HashMap<>();
        map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID,""));
        map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_CPI);
        try {
            map.put(CommonUtil.KEY_IMEI, Applications.getImei(this));
        }catch (Exception ignore){
            map.put(CommonUtil.KEY_IMEI, "");
        }
        map.put(CommonUtil.KEY_PACKAGE_NAME, adModel.getPackage_name());
        map.put(CommonUtil.KEY_ADNO, adModel.getAdNo());
        String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
        requestAsyncTask(param, CommonUtil.ACTION_CPI);
    }

    public void requestCPC(final AdModel adModel){
        HashMap<String, String> map = new HashMap<>();
        map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID,""));
        map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_CPC);
        try {
            map.put(CommonUtil.KEY_IMEI, Applications.getImei(this));
        }catch (Exception ignore){
            map.put(CommonUtil.KEY_IMEI, "");
        }
        map.put(CommonUtil.KEY_ADNO, adModel.getAdNo());
        String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
        requestAsyncTask(param, CommonUtil.ACTION_CPC);
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
                if( action.equals(CommonUtil.ACTION_GET_INFO)){
                    String gold = jo.getString(CommonUtil.RESULT_BUDGET);
                    String coin = jo.getString(CommonUtil.RESULT_COIN);
                    final double nowGold = Applications.ePreference.getTotalGpoint();
                    final int nowCoin = Applications.preference.getValue(Preference.COIN, 0);
                    if( nowGold < Double.parseDouble(gold) || nowCoin < Integer.parseInt(coin)) {
                        setGoldCoin(Double.parseDouble(gold), Integer.parseInt(coin), "reward");
                    }else{
                        setGoldCoin(Double.parseDouble(gold), Integer.parseInt(coin), "");
                    }

                    String linked_gold = jo.getString(CommonUtil.RESULT_LINKED_GOLD);
                    setLinkedGold(Double.parseDouble(linked_gold));
                    String balance = jo.getString(CommonUtil.RESULT_BALANCE_GPOINT);
                    setNormalGold(Double.parseDouble(balance));
/*
                    String invited_partners = jo.getString(CommonUtil.RESULT_INVITED_PARTNERS);

                    if( !invited_partners.equals(Applications.preference.getValue(Preference.PARTNERS, "0"))){
                        FileCacheFactory.initialize(MissionActivity.this);
                        if( FileCacheFactory.getInstance().has(CommonUtil.cacheNameInvite)){
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

                    if( jo.getString(CommonUtil.RESULT_ALARM).equals("1") || jo.getString(CommonUtil.RESULT_ALARM).equals("")){
                        Applications.preference.put(Preference.CASH_POP_ALARM, true);
                        FirebaseMessaging.getInstance().subscribeToTopic(Applications.getTopicId(MissionActivity.this));
                    }else{
                        Applications.preference.put(Preference.CASH_POP_ALARM, false);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(Applications.getTopicId(MissionActivity.this));
                    }

                    HideLoadingProgress();

                }else if( action.equals(CommonUtil.ACTION_GET_ADVERTISE)){
                    if( jo.getString("walls") != null) {
                        wallsMap.clear();
                        wallsOrderMap.clear();
                        wallsTypeMap.clear();
                        wallsAdNo.clear();
                        missionOrderMap.clear();
                        JSONArray missions = new JSONArray(jo.getString("missions"));
                        for(int i=0;i<missions.length();i++){
                            missionOrderMap.put(missions.getJSONObject(i).getString("code"), missions.getJSONObject(i).getInt("orderNo"));
                        }
                        Applications.isAdmob = false;
                        JSONArray walls = new JSONArray(jo.getString("walls"));
                        for (int i = 0; i < walls.length(); i++) {
                            Log.e("code",""+walls.getJSONObject(i).getString("code"));
                            if( walls.getJSONObject(i).getString("code").equals(CommonUtil.NATIVE_ADMOB)){
                                Applications.isAdmob = true;
                            }else{
                                wallsMap.put(walls.getJSONObject(i).getString("code"), walls.getJSONObject(i).getString("cash"));
                                wallsOrderMap.put(walls.getJSONObject(i).getString("code"), walls.getJSONObject(i).getInt("orderNo"));
                                wallsTypeMap.put(walls.getJSONObject(i).getString("code"), walls.getJSONObject(i));
                                wallsAdNo.put(walls.getJSONObject(i).getString("code"), walls.getJSONObject(i).getString("adNo"));
                            }
                        }
                    }
                    if( missionOrderMap.get("native") != null){
                        requestNative();
                        isAdNetworkNas = true;
                        isAdNetworkTnk = true;
                    }else{
                        isAdNetworkNas = true;
                        isAdNetworkTnk = true;
                    }

                    try{
                        JSONArray jobjArr = new JSONArray(jo.getString("offers"));
                        cashpopList.clear();
                        for(int i=0;i<jobjArr.length();i++) {
                            cashpopList.add(jobjArr.getJSONObject(i));
                        }
                    }catch (Exception ignore){
                        ignore.printStackTrace();
                    }

                    isCashPopAd = true;
                    JSONObject cacheRst = new JSONObject();
                    cacheRst.put(CommonUtil.KEY_TIMESTAMP, System.currentTimeMillis());
                    cacheRst.put(CommonUtil.KEY_RST, rst);
                    fileCache.put(CommonUtil.adCache, ByteProviderUtil.create(cacheRst.toString()));
                    if( isCashPopAd && isAdNetworkTnk && isAdNetworkNas) {
                        try{
                            setAdList();
                        }catch (Exception ignore){
                            ignore.printStackTrace();
                        }
                    }

                }else if( action.equals(CommonUtil.ACTION_VERSION)){
                    final String v_n = jo.getString("v_n");
                    final String v_p = jo.getString("v_p");
                    final int v_c = Integer.parseInt(jo.getString("v_c"));
                    Applications.ePreference.put(EPreference.VERSION_N, v_n+"");
                    Applications.ePreference.put(EPreference.VERSION_P, v_p+"");
                    Applications.ePreference.put(EPreference.VERSION_C, v_c+"");
                    int version = CommonUtil.getVersionCode(this);
                    if( version < v_c){
                        cashPopDialog = new CashPopDialog(this);
                        cashPopDialog.setCpTitle(getResources().getString(R.string.habit2good_update));
                        cashPopDialog.setCpDesc(getResources().getString(R.string.habit2good_update_desc));
                        cashPopDialog.setCpBOkButton(getResources().getString(R.string.update_now), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                if( v_p != null && !v_p.equals("")) {
                                    intent.setData(Uri.parse("market://details?id=" + v_p));
                                }else{
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
                }else if( action.equals(CommonUtil.ACTION_CPI)){
//                    String adNo = jo.getString(CommonUtil.KEY_ADNO);
                    final String type = jo.getString(CommonUtil.RESULT_ERROR_TYPE);
                    final String packageName = jo.getString(CommonUtil.KEY_PACKAGE_NAME);
                    final String gold = jo.getString(CommonUtil.RESULT_BUDGET);
                    final String coin = jo.getString(CommonUtil.RESULT_COIN);
                    final String task = jo.getString("tk");
                    final String isRun = jo.getString("ir");
                    final String isFirst = jo.getString("if");
                    if( type.equals("s")) {
                        //success
                        if( Applications.dbHelper.deleteCPIPackage(packageName) && task.equals("1")){
                            setAdList();
                        }
                        if( !Applications.preference.getValue(Preference.CASH_POP_ALARM, true)) {
//                            setGoldCoin(Integer.parseInt(gold), Integer.parseInt(coin), "reward");
                        }
                        if( isRun.equals("Y") && isFirst.equals("N")){
                            Toast toast = Toast.makeText(this, this.getResources().getString(R.string.more_1m), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                            toast.show();
                        }else if( isFirst.equals("Y")){
                            if( FileCacheFactory.getInstance().has(CommonUtil.adCache)){
                                FileCacheFactory.getInstance().get(CommonUtil.adCache).clear();
                            }
                            requestAdvertise();
                        }
                    }else if( type.equals("d") || type.equals("k")){
                        //duplication or internal error
                        Toast toast = Toast.makeText(this, this.getResources().getString(R.string.cpi_end), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                        toast.show();
                        if( Applications.dbHelper.deleteCPIPackage(packageName)){
                            if( FileCacheFactory.getInstance().has(CommonUtil.adCache)){
                                FileCacheFactory.getInstance().get(CommonUtil.adCache).clear();
                            }
                            requestAdvertise();
                        }
                    }else if( type.equals("c")){
                        //call back type


                    }else if( type.equals("bt")) {
                        Log.e("next","next day");
//                        Toast toast = Toast.makeText(this, this.getResources().getString(R.string.already_today), Toast.LENGTH_SHORT);
//                        toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
//                        toast.show();
                    }
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
            }else if( error != null && error.equals(CommonUtil.ERROR_NO_AD)){
                Toast toast = Toast.makeText(this, this.getResources().getString(R.string.cpi_end), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                toast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            isCashPopAd = true;
            HideLoadingProgress();
        } finally {

        }
    }

    @Override
    public void onTaskError(String param, String action, String result) {
        HideLoadingProgress();
        try{
            Log.e(TAG, action);
            if( action.equals(CommonUtil.ACTION_VERSION)){
                //empty
            }else if( action.equals(CommonUtil.ACTION_GET_ADVERTISE)){
//                swipe_layout.setRefreshing(false);
                showErrorNetwork(param, action, "tcash");
            }else if( action.equals(CommonUtil.ACTION_EVENT)){
                showErrorNetwork(param, action, "tcash");
            }
        }catch (Exception ignore){
        }
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
            networkDialog = new NetworkDialog(MissionActivity.this);
        }
        if( !networkDialog.isShowing()) {
            networkDialog.setCancelClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HideLoadingProgress();
                    networkErrorHash.clear();
                    networkDialog.dismiss();
                    ActivityCompat.finishAffinity(MissionActivity.this);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

        FileCacheFactory.initialize(MissionActivity.this);
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

        Intent intent = new Intent(MissionActivity.this, SignActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public synchronized void requestInfo(){
        ShowLoadingProgress();
        Applications.isMissionRefresh = false;
        HashMap<String, String> map = new HashMap<>();
        map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID,""));
        map.put(CommonUtil.KEY_ADID, Applications.preference.getValue(Preference.AD_ID,""));
        map.put(CommonUtil.KEY_DEVICE_TOKEN, Applications.preference.getValue(Preference.DEVICE_TOKEN,""));
        map.put(CommonUtil.KEY_PHONE_NM, Applications.preference.getValue(Preference.PHONE_NM, ""));
        int version = CommonUtil.getVersionCode(MissionActivity.this);
        map.put(CommonUtil.KEY_NAME, version+"");
        map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_GET_INFO);
        String param = APICrypto.getParam(MissionActivity.this, map, CommonUtil.SHARED_KEY);
        requestAsyncTask(param, CommonUtil.ACTION_GET_INFO);
    }

    public void setGoldCoin(final double gold, final int coin, final String type) {
        Applications.isCashpopPopup = false;
        final double nowGold = Applications.ePreference.getTotalGpoint();
        final int nowCoin = Applications.ePreference.getValue(EPreference.N_TROPHY, 0);
        //final int nowCoin = Applications.preference.getValue(Preference.COIN, 0);
        if( nowGold != gold || nowCoin != coin){
            if( FileCacheFactory.getInstance().has(CommonUtil.cacheNameHistory)){
                FileCacheFactory.getInstance().get(CommonUtil.cacheNameHistory).clear();
            }
        }
        double reward = gold - nowGold;
        if( reward != 0){
            try {
                if (FileCacheFactory.getInstance().has(CommonUtil.cacheNameHistory)) {
                    FileCacheFactory.getInstance().get(CommonUtil.cacheNameHistory).clear();
                }
            }catch (Exception ignore){}
        }

        try {
            goldCoinRefresh(nowGold, gold, nowCoin, coin);
        }catch (Exception e){
            e.printStackTrace();
        }
        Applications.ePreference.putTotalGpoint(gold);
        Applications.preference.put(Preference.COIN, coin);
    }

    public void goldCoinRefresh(final double nowGold, final double refreshGold, final int nowCoin, final int refreshCoin) throws Exception{
        boolean isGold = false;
        boolean isCoin = false;
        if( refreshGold - nowGold != 0){
            isGold = true;
        }
        if( refreshCoin - nowCoin != 0){
            isCoin = true;
        }

        if( isGold && isCoin) {
            final int dtime = 40;
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 0:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 1:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2_2));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 2:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 3:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 4:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 5:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2_2));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 6:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 7:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2));
                            this.sendEmptyMessageDelayed(msg.what + 1, dtime);
                            break;
                        case 8:
                            ValueAnimator vaG = ValueAnimator.ofInt((int) nowGold, (int) refreshGold);
                            vaG.setDuration(1000);
                            vaG.setInterpolator(AnimationUtils.loadInterpolator(MissionActivity.this, android.R.anim.decelerate_interpolator));
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
                            vaC.setInterpolator(AnimationUtils.loadInterpolator(MissionActivity.this, android.R.anim.decelerate_interpolator));
                            vaC.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    try{
                                        Integer value = (Integer)valueAnimator.getAnimatedValue();
                                        tv_my_trophy.setText(CommonUtil.setComma(value+"", false, false));
                                    }catch (Exception ignore){}
                                }
                            });
                            vaC.start();
                            this.sendEmptyMessageDelayed(msg.what + 1, 1000);
                            break;
                        case 9:
                            if( !Applications.isReward) {
                                String tmsg = "";
                                if( (refreshGold - nowGold) > 0 && (refreshCoin - nowCoin) > 0){
                                    tmsg = getResources().getString(R.string.reward_toast, "" + (int) (refreshGold - nowGold), "" + (int) (refreshCoin - nowCoin));
                                }else if( (refreshGold - nowGold) > 0 && (refreshCoin - nowCoin) <= 0){
                                    tmsg = getResources().getString(R.string.reward_gpoint_toast, ""+(int)(refreshGold-nowGold));
                                }else if( (refreshGold - nowGold) <= 0 && (refreshCoin - nowCoin) > 0){
                                    tmsg = getResources().getString(R.string.reward_trophy_toast, ""+(int)(refreshCoin-nowCoin));
                                }
                                if( !tmsg.equals("")) {
                                    Toast toast = Toast.makeText(MissionActivity.this, tmsg, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                                    toast.show();
                                }
                            }
                            Applications.isReward = false;
                            break;
                    }
                }
            }.sendEmptyMessageDelayed(0, 200);
        }else{
            try {
                goldRefresh(nowGold, refreshGold);
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                coinRefresh(nowCoin, refreshCoin);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void goldRefresh(final double nowGold, final double refreshGold) throws Exception{
        if( refreshGold - nowGold > 0){
            final int dtime = 40;
            new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what){
                        case  0:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what+1, dtime);
                            break;
                        case 1:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2_2));
                            this.sendEmptyMessageDelayed(msg.what+1, dtime);
                            break;
                        case 2:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what+1, dtime);
                            break;
                        case 3:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2));
                            this.sendEmptyMessageDelayed(msg.what+1, dtime);
                            break;
                        case 4:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what+1, dtime);
                            break;
                        case 5:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2_2));
                            this.sendEmptyMessageDelayed(msg.what+1, dtime);
                            break;
                        case 6:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2_1));
                            this.sendEmptyMessageDelayed(msg.what+1, dtime);
                            break;
                        case 7:
                            iv_my_gpoint.setImageDrawable(ContextCompat.getDrawable(MissionActivity.this, R.drawable.cash2));
                            this.sendEmptyMessageDelayed(msg.what+1, dtime);
                            break;
                        case 8:
                            ValueAnimator va = ValueAnimator.ofInt((int) nowGold, (int) refreshGold);
                            va.setDuration(1000);
                            va.setInterpolator(AnimationUtils.loadInterpolator(MissionActivity.this, android.R.anim.decelerate_interpolator));
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
                                public void onAnimationStart(Animator animator) {}
                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    if( !Applications.isReward) {
                                        Toast toast = Toast.makeText(MissionActivity.this, getResources().getString(R.string.reward_gpoint_toast, "" + (int) (refreshGold - nowGold)), Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                                        toast.show();
                                    }
                                    Applications.isReward = false;
                                }
                                @Override
                                public void onAnimationCancel(Animator animator) {}
                                @Override
                                public void onAnimationRepeat(Animator animator) {}
                            });
                            break;
                    }
                }
            }.sendEmptyMessageDelayed(0, 200);
        }else {
            ValueAnimator va = ValueAnimator.ofInt((int) nowGold, (int) refreshGold);
            va.setDuration(1000);
            va.setInterpolator(AnimationUtils.loadInterpolator(MissionActivity.this, android.R.anim.decelerate_interpolator));
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
        if( refreshCoin - nowCoin > 0){
            ValueAnimator va = ValueAnimator.ofInt(nowCoin, refreshCoin);
            va.setDuration(1000);
            va.setInterpolator(AnimationUtils.loadInterpolator(MissionActivity.this, android.R.anim.decelerate_interpolator));
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    try{
                        Integer value = (Integer)valueAnimator.getAnimatedValue();
                        tv_my_trophy.setText(CommonUtil.setComma(value+"", false, false));
                    }catch (Exception ignore){}
                }
            });
            va.start();
            va.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {}
                @Override
                public void onAnimationEnd(Animator animator) {
                    if( !Applications.isReward) {
                        Toast toast = Toast.makeText(MissionActivity.this, getResources().getString(R.string.reward_trophy_toast, "" + (int) (refreshCoin - nowCoin)), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                        toast.show();
                    }
                    Applications.isReward = false;
                }
                @Override
                public void onAnimationCancel(Animator animator) {}
                @Override
                public void onAnimationRepeat(Animator animator) {}
            });
        }else{
            ValueAnimator va = ValueAnimator.ofInt(nowCoin, refreshCoin);
            va.setDuration(1000);
            va.setInterpolator(AnimationUtils.loadInterpolator(MissionActivity.this, android.R.anim.decelerate_interpolator));
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    try{
                        Integer value = (Integer)valueAnimator.getAnimatedValue();
                        tv_my_trophy.setText(CommonUtil.setComma(value+"", false, false));
                    }catch (Exception ignore){}
                }
            });
            va.start();
        }
    }

    public void setLinkedGold(double linkedGold) {
        Applications.ePreference.putNLinkedGold(linkedGold);
    }


    public void setNormalGold(double normalGold) {
        Applications.ePreference.putBalanceGpoint(normalGold);
    }

    public void goHistory(){
        startActivity(new Intent(MissionActivity.this, HistoryActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void AppAllOfferwallSDKCallback(int i) {
        switch (i) {
            case AppAllOfferwallSDK.AppAllOfferwallSDK_SUCCES:
                Toast.makeText(this, "성공", Toast.LENGTH_SHORT).show();
                break;
            case AppAllOfferwallSDK.AppAllOfferwallSDK_INVALID_USER_ID:
                Toast.makeText(this, "잘못 된 유저아이디입니다.", Toast.LENGTH_SHORT).show();
                break;
            case AppAllOfferwallSDK.AppAllOfferwallSDK_INVALID_KEY:
                Toast.makeText(this, "오퍼월 KEY를 확인해주세요.", Toast.LENGTH_SHORT).show();
                break;
            case AppAllOfferwallSDK.AppAllOfferwallSDK_NOT_GET_ADID:
                Toast.makeText(this, "고객님의 폰으로는 무료충전소를 이용하실 수 없습니다. 고객센터에 문의해주세요.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
