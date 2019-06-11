package kr.co.gubed.habit2goodpure.gpoint.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import com.tnkfactory.ad.BannerAdListener;
import com.tnkfactory.ad.BannerAdType;
import com.tnkfactory.ad.BannerAdView;
import com.tnkfactory.ad.TnkSession;

import java.util.ArrayList;

import kr.co.gubed.habit2goodpure.R;
import kr.co.gubed.habit2goodpure.gpoint.listener.GiftBoxListener;
import kr.co.gubed.habit2goodpure.gpoint.model.GiftBoxModel;
import kr.co.gubed.habit2goodpure.gpoint.util.Applications;
import kr.co.gubed.habit2goodpure.gpoint.util.CommonUtil;
import kr.co.gubed.habit2goodpure.gpoint.util.GiftBoxAsyncTask;
import kr.co.gubed.habit2goodpure.gpoint.view.LoadingDialog;
import kr.co.gubed.habit2goodpure.gpoint.view.NetworkDialog;

public class GiftBoxActivity extends Activity implements View.OnClickListener, GiftBoxListener {
    private String TAG = this.getClass().toString();

    private LinearLayout type_admob;
    private BannerAdView bannerAdView;

    private Button btn_back;
    private Button btn_info;
    private TextView tv_cnt;
    private ArrayList<GiftBoxModel> giftboxList;
    private WebView webView;

    private LoadingDialog loadingDialog;
    private NetworkDialog networkDialog;
    private StringBuilder str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_box);

        bannerAdView = (BannerAdView) findViewById(R.id.banner_ad);
        bannerAdView.setBannerAdListener(new BannerAdListener() {

            @Override
            public void onFailure(int errCode) {
                Log.e(getClass().getName(), "TNK bannerAd loading fail: "+errCode);
            }

            @Override
            public void onShow() {

            }

            @Override
            public void onClick() {

            }
        });
        bannerAdView.loadAd(TnkSession.CPC, BannerAdType.LANDSCAPE); // or bannerAdView.loadAd(TnkSession.CPC, BannerAdType.LANDSCAPE)
        /*type_admob = (LinearLayout)findViewById(R.id.type_admob);

        try {
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
        tv_cnt = (TextView)findViewById(R.id.tv_cnt);

        giftboxList = new ArrayList<>();

        webView = (WebView)findViewById(R.id.webView);
        webView.addJavascriptInterface(new GiftBoxActivity.CPIF(), "cpif");
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webView.setWebViewClient(new GiftBoxActivity.CPWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkLoads(false);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        int newCnt = Applications.dbHelper.getGiftBoxNewCnt();
        if( newCnt > 0){
            tv_cnt.setText(newCnt+"");
            tv_cnt.setVisibility(View.VISIBLE);
        }else{
            tv_cnt.setVisibility(View.GONE);
        }
        str = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        sb.append(this.getResources().getString(R.string.notice_top));
        sb.append(this.getResources().getString(R.string.notice_bottom));
        webView.loadDataWithBaseURL("file:///android_asset/", sb.toString(), "text/html", "utf-8", null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (bannerAdView != null) {
            bannerAdView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (bannerAdView != null) {
            bannerAdView.onPause();
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

        if (bannerAdView != null) {
            bannerAdView.onDestroy();
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
                CommonUtil.showSupport(GiftBoxActivity.this, true);
                break;
        }
    }

    @Override
    public void giftBoxListener(ArrayList<GiftBoxModel> giftBoxModels) {
        giftboxList = giftBoxModels;
        setList();
    }

    private class CPIF{
        @JavascriptInterface
        public void readGiftBox(final int idx){
            webView.post(new Runnable() {
                @Override
                public void run() {
                    if( giftboxList.get(idx).getIsRead().equals("new")){
                        Applications.isSettingNOticeRefresh = true;
                        giftboxList.get(idx).setIsRead("read");
                        Applications.dbHelper.readNewGiftBox(giftboxList.get(idx));
                    }
                    int newCnt = Applications.dbHelper.getGiftBoxNewCnt();
                    if( newCnt > 0){
                        tv_cnt.setText(newCnt+"");
                        tv_cnt.setVisibility(View.VISIBLE);
                    }else{
                        tv_cnt.setVisibility(View.GONE);
                    }

                    if( !giftboxList.get(idx).isSelect()){
                        for(int i=0;i<giftboxList.size();i++){
                            if( i != idx) {
                                giftboxList.get(i).setSelect(false);
                            }
                        }
                        String javascript = "javascript:$(\"#listview\").find(\"li\").removeClass(\"on\");$(\".content\").hide();$(\"#r_"+giftboxList.get(idx).getId()+"\").find(\".new\").hide();$(\"#r_"+giftboxList.get(idx).getId()+"\").addClass(\"on\");$(\"#r_"+giftboxList.get(idx).getId()+"\").find(\".content\").show();$('body').animate({scrollTop: $(\"#r_" + giftboxList.get(idx).getId() + "\").offset().top},300);";
                        Log.e("javascript",""+javascript);
                        webView.loadUrl(javascript);
                        giftboxList.get(idx).setSelect(true);
                    }else{
                        String javascript = "javascript:$(\"#listview\").find(\"li\").removeClass(\"on\");$(\".content\").hide();";
                        Log.e("javascript",""+javascript);
                        webView.loadUrl(javascript);
                        giftboxList.get(idx).setSelect(false);
                    }
                }
            });
        }
        @JavascriptInterface
        public void loadMore(){

        }
    }

    private class CPWebViewClient extends WebViewClient {
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
            Log.e("onPageFinished","onPageFinished");
            super.onPageFinished(view, url);
            new GiftBoxAsyncTask(GiftBoxActivity.this, GiftBoxActivity.this).execute();
        }
    }

    public void setList(){
        if( str == null) {
            str = new StringBuilder();
        }else{
            str.setLength(0);
            str = new StringBuilder();
        }
        String newStr = this.getResources().getString(R.string.new_txt);
        String appendStr = "";
        if( !giftboxList.isEmpty() && giftboxList.size() > 0) {
            for (int i = 0; i < giftboxList.size(); i++) {
                appendStr = "<li id=r_" + giftboxList.get(i).getId() + " class=row>";
                appendStr += "<a href=\"javascript:window.cpif.readGiftBox(" + i + ");\">";
                appendStr += "<div class=title>";
                appendStr += giftboxList.get(i).getGiftTitle();
                appendStr += "</div>";

                appendStr += "<div class=date>";
                appendStr += CommonUtil.getDateTime(giftboxList.get(i).getRegdate());
                appendStr += "</div>";

                appendStr += "<div class=ico></div>";
                if (giftboxList.get(i).getIsRead().equals("new")) {
                    appendStr += "<div class=new>" + newStr + "</div>";
                } else {
                    appendStr += "<div class=new style=\"display:none;\">" + newStr + "</div>";
                }
                appendStr += "</a>";
                appendStr += "<div class=content>";
                appendStr += CommonUtil.addSlashes(giftboxList.get(i).getGiftContent());
                appendStr += "</div>";
                appendStr += "</li>";
                webView.loadUrl("javascript:if( $('#r_" + giftboxList.get(i).getId() + "').length == 0){ $('#listview').append('" + appendStr + "'); }");
            }
            webView.loadUrl("javascript:$(window).scroll(function(){if( $(window).scrollTop()==$(document).height()-$(window).height()){window.cpif.loadMore();}});$(window).ready(function(){if($('body').height()<$(window).height()){window.cpif.loadMore();}});");
        }else{
            appendStr = "<li class=row>";
            appendStr += "<div class=no_list>";
            appendStr += this.getResources().getString(R.string.no_gift);
            appendStr += "</div>";
            appendStr += "</li>";
            Log.e("appendStr",""+appendStr);
            webView.loadUrl("javascript:$('#listview').append('" + appendStr + "');");
        }

        HideLoadingProgress();
    }

    public void ShowLoadingProgress() {
        //show loading
        try {
            if( loadingDialog == null){
                loadingDialog = new LoadingDialog(GiftBoxActivity.this);
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

}
