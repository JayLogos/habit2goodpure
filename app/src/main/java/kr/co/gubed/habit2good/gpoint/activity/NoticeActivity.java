package kr.co.gubed.habit2good.gpoint.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import kr.co.gubed.habit2good.R;
import kr.co.gubed.habit2good.gpoint.filecache.ByteProviderUtil;
import kr.co.gubed.habit2good.gpoint.filecache.FileCache;
import kr.co.gubed.habit2good.gpoint.filecache.FileCacheFactory;
import kr.co.gubed.habit2good.gpoint.listener.AsyncTaskCompleteListener;
import kr.co.gubed.habit2good.gpoint.model.NoticeModel;
import kr.co.gubed.habit2good.gpoint.util.APICrypto;
import kr.co.gubed.habit2good.gpoint.util.Applications;
import kr.co.gubed.habit2good.gpoint.util.AsyncHTTPPost;
import kr.co.gubed.habit2good.gpoint.util.CommonUtil;
import kr.co.gubed.habit2good.gpoint.util.Preference;
import kr.co.gubed.habit2good.gpoint.view.LoadingDialog;
import kr.co.gubed.habit2good.gpoint.view.NetworkDialog;


public class NoticeActivity extends Activity implements View.OnClickListener, AsyncTaskCompleteListener<String> {

    private String TAG = this.getClass().toString();

    private LinearLayout type_admob;

    private Button btn_back;
    private Button btn_info;
    private TextView tv_title;
    private TextView tv_cnt;
    private ArrayList<NoticeModel> noticeList;
    private WebView webView;

    private LoadingDialog loadingDialog;
    private NetworkDialog networkDialog;

    private FileCache fileCache;

    private String b_type = "1";
    private int page = 0;
    private boolean isMore = true;
    private StringBuilder str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        fileCache = getFileCache();
        type_admob = (LinearLayout)findViewById(R.id.type_admob);

        /*ADMOB try {
            if ((Applications.adView.getParent()) != null) {
                ((ViewGroup) Applications.adView.getParent()).removeAllViews();
                type_admob.addView(Applications.adView);
            }else{
                type_admob.addView(Applications.adView);
            }
        }catch (Exception ignore){
            ignore.printStackTrace();
        }*/

        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_info = (Button)findViewById(R.id.btn_info);
        btn_info.setOnClickListener(this);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_cnt = (TextView)findViewById(R.id.tv_cnt);
        Intent intent = getIntent();
        if( intent.getStringExtra(CommonUtil.KEY_BOARDTYPE) != null){
            b_type = intent.getStringExtra(CommonUtil.KEY_BOARDTYPE);
        }
        if( b_type.equals("1")){
            tv_title.setText(this.getResources().getString(R.string.notice));
        }else if( b_type.equals("2")){
            tv_title.setText(this.getResources().getString(R.string.faq));
        }

        noticeList = new ArrayList<>();

        webView = (WebView)findViewById(R.id.webView);
        webView.addJavascriptInterface(new CPIF(), "cpif");
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webView.setWebViewClient(new CPWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkLoads(false);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        int noticeCnt = Applications.dbHelper.getNoticeNewCnt(b_type);
        if( noticeCnt > 0){
            tv_cnt.setText(noticeCnt+"");
            tv_cnt.setVisibility(View.VISIBLE);
        }else{
            tv_cnt.setVisibility(View.GONE);
        }
        str = new StringBuilder();
        Applications.isSettingNOticeRefresh = true;
        StringBuilder sb = new StringBuilder();
        sb.append(this.getResources().getString(R.string.notice_top));
        sb.append(this.getResources().getString(R.string.notice_bottom));
        webView.loadDataWithBaseURL("file:///android_asset/", sb.toString(), "text/html", "utf-8", null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*ADMOB try {
            if( Applications.isAdmob && Applications.adView != null){
                //Applications.adView.pause();
                //Applications.adView.resume();
            }
        }catch (Exception ignore){
            ignore.printStackTrace();
        }*/
    }

    public FileCache getFileCache(){
        if( fileCache == null){
            FileCacheFactory.initialize(this);
            if( !FileCacheFactory.getInstance().has(CommonUtil.cacheNameNotice)){
                FileCacheFactory.getInstance().create(CommonUtil.cacheNameNotice, 1024);
            }
            fileCache = FileCacheFactory.getInstance().get(CommonUtil.cacheNameNotice);
        }
        return fileCache;
    }

    public void requestNotice(){
        if( isMore){
            ShowLoadingProgress();
            page++;
            boolean isCache = false;
            String cacheRst="";
            if( getFileCache().get(CommonUtil.noticeCache+b_type+""+page) != null){
                try{
                    InputStream is = getFileCache().get(CommonUtil.noticeCache+b_type+""+page).getInputStream();
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    cacheRst = new String(buffer);
                    JSONObject job = new JSONObject(cacheRst);
                    isCache = System.currentTimeMillis() - Long.parseLong(job.getString(CommonUtil.KEY_TIMESTAMP)) < 60 * 60 * 1000 * 12;
                }catch (Exception e){
                    isCache = false;
                }

            }
            if( isCache) {
                try{
                    JSONObject job = new JSONObject(cacheRst);
                    JSONObject jo = new JSONObject(job.getString(CommonUtil.KEY_RST));
                    setNoticeList(jo);
                }catch (Exception ignore){}
            }else{
                HashMap<String, String> map = new HashMap<>();
                map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID, ""));
                map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_GET_NEW_NOTICELIST);
                map.put(CommonUtil.KEY_BOARDTYPE, b_type);
                map.put(CommonUtil.KEY_PAGE, page + "");
                String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
                requestAsyncTask(param, CommonUtil.ACTION_GET_NEW_NOTICELIST);
            }
        }
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
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
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
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_info:
                CommonUtil.showSupport(NoticeActivity.this, false);
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
                    case CommonUtil.ACTION_GET_NEW_NOTICELIST:
                        setNoticeList(jo);
                        JSONObject cacheRst = new JSONObject();
                        cacheRst.put(CommonUtil.KEY_TIMESTAMP, System.currentTimeMillis());
                        cacheRst.put(CommonUtil.KEY_RST, rst);
                        getFileCache().put(CommonUtil.noticeCache+b_type+""+page, ByteProviderUtil.create(cacheRst.toString()));
                        break;
                }
            }else{

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public void onTaskError(String param, String action, String result) {
        try{
            Log.e(TAG, action);
            if( action.equals(CommonUtil.ACTION_GET_NEW_NOTICELIST)){
                showErrorNetwork(param, action);
            }
        }catch (Exception ignore){
        }
    }

    public void showErrorNetwork(final String param, final String action){
        if( networkDialog == null){
            networkDialog = new NetworkDialog(NoticeActivity.this);
        }
        if( !networkDialog.isShowing()) {
            networkDialog.setCancelClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HideLoadingProgress();
                    networkDialog.dismiss();
                    ActivityCompat.finishAffinity(NoticeActivity.this);
                }
            });
            networkDialog.setOkClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowLoadingProgress();
                    requestAsyncTask(param, action);
                    networkDialog.dismiss();
                }
            });
            networkDialog.show();
        }
    }

    private class CPIF{
        @JavascriptInterface
        public void readNotice(final int idx){
            webView.post(new Runnable() {
                @Override
                public void run() {
                    if( noticeList.get(idx).getIsRead().equals("new")){
                        Applications.isSettingNOticeRefresh = true;
                        noticeList.get(idx).setIsRead("read");
                        Applications.dbHelper.readNewNotice(noticeList.get(idx));
                    }
                    int noticeCnt = Applications.dbHelper.getNoticeNewCnt(b_type);
                    if( noticeCnt > 0){
                        tv_cnt.setText(noticeCnt+"");
                        tv_cnt.setVisibility(View.VISIBLE);
                    }else{
                        tv_cnt.setVisibility(View.GONE);
                    }

                    if( !noticeList.get(idx).isSelect()){
                        for(int i=0;i<noticeList.size();i++){
                            if( i != idx) {
                                noticeList.get(i).setSelect(false);
                            }
                        }
                        String javascript = "javascript:$(\"#listview\").find(\"li\").removeClass(\"on\");$(\".content\").hide();$(\"#r_"+noticeList.get(idx).getId()+"\").find(\".new\").hide();$(\"#r_"+noticeList.get(idx).getId()+"\").addClass(\"on\");$(\"#r_"+noticeList.get(idx).getId()+"\").find(\".content\").show();$('body').animate({scrollTop: $(\"#r_" + noticeList.get(idx).getId() + "\").offset().top},300);";
                        Log.e("javascript",""+javascript);
                        webView.loadUrl(javascript);
                        noticeList.get(idx).setSelect(true);
                    }else{
                        String javascript = "javascript:$(\"#listview\").find(\"li\").removeClass(\"on\");$(\".content\").hide();";
                        Log.e("javascript",""+javascript);
                        webView.loadUrl(javascript);
                        noticeList.get(idx).setSelect(false);
                    }
                }
            });
        }
        @JavascriptInterface
        public void loadMore(){
            webView.post(new Runnable() {
                @Override
                public void run() {
                    requestNotice();
                }
            });
        }
    }

    private class CPWebViewClient extends WebViewClient{
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri uri = request.getUrl();
            if( uri.getScheme().equals("http") || uri.getScheme().equals("https")){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                view.getContext().startActivity(intent);
                return true;
            }else{
                return super.shouldOverrideUrlLoading(view, request);
            }

        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if( uri.getScheme().equals("http") || uri.getScheme().equals("https")){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                view.getContext().startActivity(intent);
                return true;
            }else{
                return super.shouldOverrideUrlLoading(view, url);
            }
        }

        @Override
        public void onPageFinished(WebView view, final String url) {
            super.onPageFinished(view, url);
            Log.e("onPageFinished","onPageFinished");
            requestNotice();
        }
    }

    public void setNoticeList(JSONObject jo){
        int cnt = 0;
        try{
            JSONArray jobjArr = new JSONArray(jo.getString("n"));
            page = Integer.parseInt(jo.getString("p"));
            if( page == 1){
                noticeList.clear();
            }
            /*
            if( jobjArr.length() >= 15){
                isMore = true;
            }else{
                isMore = false;
            }
            */
            cnt = jobjArr.length();
            for(int i = 0; i < jobjArr.length(); i++) {
                JSONObject noticeObj = jobjArr.getJSONObject(i);
                NoticeModel noticeModel = new NoticeModel(noticeObj.getString("i"), noticeObj.getString("s"), noticeObj.getString("c"), noticeObj.getString("b"), Applications.dbHelper.isNoticeRead(noticeObj.getString("i")), noticeObj.getString("u"), noticeObj.getString("t"));
                noticeList.add(noticeModel);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if( str == null) {
                str = new StringBuilder();
            }else{
                str.setLength(0);
                str = new StringBuilder();
            }
            String newStr = this.getResources().getString(R.string.new_txt);
            String appendStr;
            for(int i=0;i<noticeList.size();i++){
                appendStr = "";
                /*
                str.append("<li id=r_"+i+" class=row>");
                str.append("<a href=\"javascript:window.cpif.readNotice("+i+");\">");
                str.append("<div class=title>");
                str.append(noticeList.get(i).getSubject());
                str.append("</div>");

                //str.append("<div class=date>");
                //str.append(CommonUtil.getDateTime(noticeList.get(i).getRegDate()));
                //str.append("</div>");

                str.append("<div class=ico></div>");
                if( noticeList.get(i).getIsRead().equals("new")){
                    str.append("<div class=new>"+newStr+"</div>");
                }else{
                    str.append("<div class=new style=\"display:none;\">"+newStr+"</div>");
                }
                str.append("</a>");
                str.append("<div class=content>");
                str.append(CommonUtil.addSlashes(noticeList.get(i).getContent()));
                str.append("</div>");
                str.append("</li>");
                */
                if( b_type.equals("1")) {
                    appendStr = "<li id=r_" + noticeList.get(i).getId() + " class=row>";
                }else{
                    appendStr = "<li id=r_" + noticeList.get(i).getId() + " class=row2>";
                }
                appendStr += "<a href=\"javascript:window.cpif.readNotice("+i+");\">";
                appendStr += "<div class=title>";
                appendStr += noticeList.get(i).getSubject();
                appendStr += "</div>";
                if( b_type.equals("1")) {
                    appendStr += "<div class=date>";
                    appendStr += CommonUtil.getDateTime(noticeList.get(i).getRegDate());
                    appendStr += "</div>";
                }
                appendStr += "<div class=ico></div>";

                if( noticeList.get(i).getIsRead().equals("new") && Long.parseLong(noticeList.get(i).getRegDate()) > CommonUtil.newDate()){
                    appendStr += "<div class=new>"+newStr+"</div>";
                }else{
                    appendStr += "<div class=new style=\"display:none;\">"+newStr+"</div>";
                }
                appendStr += "</a>";
                appendStr += "<div class=content>";
                appendStr += CommonUtil.addSlashes(noticeList.get(i).getContent());
                appendStr += "</div>";
                appendStr += "</li>";
                webView.loadUrl("javascript:if( $('#r_"+noticeList.get(i).getId()+"').length == 0){ $('#listview').append('" + appendStr + "'); }");
//                Log.e("sb",""+appendStr);
            }
//            Log.e("sb",""+str.toString());
//            webView.loadUrl("javascript:$('#listview').html('" + str.toString() + "')");
            //webView.loadUrl("javascript:$('body').scroll(function(){if( parseInt($('#listview').height())<=(parseInt($('body').scrollTop())+parseInt($('body').height())+5)){window.cpif.loadMore();}});");
            webView.loadUrl("javascript:$(window).scroll(function(){if( $(window).scrollTop()<=$(document).height()-$(window).height()){window.cpif.loadMore();}});$(window).ready(function(){if($('body').height()<=$(window).height()){window.cpif.loadMore();}});");
            HideLoadingProgress();
            if( cnt >= 15){
                isMore = true;
            }else{
                isMore = false;
            }
        }
    }

    public void ShowLoadingProgress() {
        //show loading
        try {
            if( loadingDialog == null){
                loadingDialog = new LoadingDialog(NoticeActivity.this);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    try{
                        loadingDialog.show();
                    }catch (Exception ignore){}
                }
            });
        } catch (Exception ignore) {

        }
    }

    public void HideLoadingProgress() {
        //hide loading
        try {
            loadingDialog.dismiss();
        } catch (Exception ignore) {

        }
    }

    public void requestAsyncTask(String param, String action){
        if( Applications.getCountry(this).equals("KR") && !Applications.isRoaming(this)) {
            new AsyncHTTPPost(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, CommonUtil.SERVER_URL, param, action);
        }else{
            new AsyncHTTPPost(this).execute(CommonUtil.SERVER_URL, param, action);
        }
    }

}
