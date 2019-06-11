package kr.co.gubed.habit2goodpure.gpoint.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Locale;

import kr.co.gubed.habit2goodpure.MainActivity;
import kr.co.gubed.habit2goodpure.PointmallActivity;
import kr.co.gubed.habit2goodpure.gpoint.activity.MissionActivity;
import kr.co.gubed.habit2goodpure.gpoint.activity.StoreActivity;
import kr.co.gubed.habit2goodpure.HabitDbAdapter;
import kr.co.gubed.habit2goodpure.gpoint.listener.AdidListener;
import kr.co.gubed.habit2goodpure.R;

public class Applications extends MultiDexApplication {

    public static HabitDbAdapter dbHelper;

    public static Preference preference;

    public static EPreference ePreference;

    private static AdvertisingIdClient.Info adInfo;

    public static boolean isReward = false;
    public static boolean isStart = false;
    public static boolean isPopup = false;
    public static boolean isCashpopPopup = false;
    public static boolean isHomeRefresh = true;
    public static boolean isMissionRefresh = false;
    public static boolean isCashPopRefresh = true;
    public static boolean isEventRefresh = true;
    public static boolean isStoreRefresh = true;
    public static boolean isSettingRefresh = true;
    public static boolean isSettingNOticeRefresh = true;

    public static boolean isOfferWall = false;
    public static boolean isEevent = false;

    public static boolean isGiftBoxGo = false;

    public static boolean isGiftBoxRe = false;

    private static boolean isAnim = false;

    private Tracker mTracker;

    /*ADMOB public static AdView adView;*/
    //public static NativeExpressAdView nativeExpressAdView;
    public static boolean isAdmob = true;

    public static Activity refreshActivity;

    public static Context context;
    public static HashMap noticeMap;

    public static int mobAdCnt = 0;

    //admob
    /*ADMOB public static InterstitialAd mInterstitialAd;*/

    private static String TAG;

    synchronized public Tracker getDefaultTracker() {
        if( mTracker == null){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.app_tracker);
            mTracker.enableAdvertisingIdCollection(true);
        }
        return mTracker;
    }

    private FirebaseAnalytics firebaseAnalytics;

    public Applications() {
        super();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TAG = getClass().getName();
        dbHelper = new HabitDbAdapter(getApplicationContext());
        dbHelper.open();
        preference = new Preference(getApplicationContext());
        ePreference = new EPreference(getApplicationContext());
        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        context = getApplicationContext();
        noticeMap = new HashMap();
    }

    public static void getAdid(final Context context, final AdidListener adidListener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                    if( adInfo != null){
                        Log.e(TAG, "adId = "+adInfo.getId());
//                        preference.put(Preference.AD_ID, adInfo.getId());
                        preference.put(Preference.ADVERTISE_ID, adInfo.getId());
//                        adidListener.complete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void applicationDestroy(){
        isHomeRefresh = true;
        isCashPopRefresh = true;
        isStoreRefresh = true;
        isSettingRefresh = true;
        isSettingNOticeRefresh = true;
        isOfferWall = false;
        isStart = false;
        isPopup = false;
        isAnim = false;
        /*ADMOB if( Applications.isAdmob && Applications.adView != null){
            Applications.adView.destroy();
        }*/
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean isSystemAlertEnable(){
        boolean isEnabled;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isEnabled = Settings.canDrawOverlays(getApplicationContext());
        }else{
            AppOpsManager appOps = (AppOpsManager)getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOp(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, android.os.Process.myUid(), getPackageName());
            isEnabled = mode == AppOpsManager.MODE_ALLOWED;
        }
        return isEnabled;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean isAccessibilityEnable(){
        AppOpsManager appOps = (AppOpsManager)getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private static Locale getCurrentLocale(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

    public static String getCountry(Context context){
        return "KR";
    }

    public static String getUnit(Context context){
        String unit = "";
        if( getCountry(context).equals("KR")){
            unit = "￦";
        }else{
            unit = "$";
        }
        return unit;
    }

    public static boolean isRoaming(Context context){
        TelephonyManager tel = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
        boolean isRoaming = tel.isNetworkRoaming();
        // 리턴값이 1 인 경우 Roaming 된 상태이며, 0이면 Roaming 되지 않은 상태입니다.
        Log.e("isRoaming",""+isRoaming);
        return isRoaming;
    }

    public static String getImei(Context context){
        try{
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
            String imei = "";
            if( telephonyManager.getDeviceId() != null){
                imei = telephonyManager.getDeviceId();
            }

            return APICrypto.MD5(APICrypto.KEY+imei);
        }catch (Exception ignore){
            return "";
        }
    }

    public static String getTopicId(Context context){
        final String FCM_TOPIC = "habit2good";
        String topic = FCM_TOPIC+getCountry(context);
        return topic;
    }

    public void setRefreshActivity(Activity refreshActivity){
        this.refreshActivity = refreshActivity;
    }

    public static void requestInfo(){
        try {
            Applications.isOfferWall = true;
            Applications.isHomeRefresh = true;
            if( refreshActivity != null) {
                //if( refreshActivity instanceof MainActivity) {
                    //((MainActivity)refreshActivity).requestInfo();

                if( refreshActivity instanceof MissionActivity){
                    ((MissionActivity)refreshActivity).requestInfo();
                }else if( refreshActivity instanceof StoreActivity){
                    ((StoreActivity)refreshActivity).requestInfo();
                }
            }
        }catch (Exception ignore){
            ignore.printStackTrace();
        }
    }

    public static void rewardGift(){
        try {
            if( refreshActivity != null && refreshActivity instanceof MainActivity) {
                PointmallActivity pointmallActivity = new PointmallActivity();
                pointmallActivity.requestMyGift(true);
            }
        }catch (Exception ignore){
            ignore.printStackTrace();
        }
    }

/* ADMOB
    public static void admobRequest(){
        Log.i(TAG, "admobRequest");

        Applications.adView = null;
        Applications.adView = new AdView(context);
        Applications.adView.setAdSize(AdSize.SMART_BANNER);
        Applications.adView.setAdUnitId(context.getResources().getString(R.string.banner_ad_unit_id_test));  // 어떻게 얻은 값인지?
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        Applications.adView.loadAd(adRequestBuilder.build());
        Applications.adView.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Applications.adView.setTag(false);
                Log.i(TAG, "admobRequest onAdClosed");
            }
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.i(TAG, "admobRequest onAdFailedToLoad i="+i);
            }
            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Log.i(TAG, "admobRequest onAdLeftApplication");
            }
            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.i(TAG, "admobRequest onAdOpened");
            }
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.e(TAG, "admobRequest onAdLoaded");
            }
        });
    }
*/

}
