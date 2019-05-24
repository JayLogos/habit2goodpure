package kr.co.gubed.habit2good.gpoint.view;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.co.gubed.habit2good.R;


public class NoticeDialog extends Dialog implements View.OnClickListener{

    private WebView webView;
    private RelativeLayout btn_layer;
    private LinearLayout box_today;
    private TextView tv_today;
    private Button btn_check;

    private Button btn_close;

    private Context context;

    private String npTitle;
    private String npHtml;

    private int width;
    private int height;
    private String isToday;
    private boolean isBox = true;

    public NoticeDialog(Context context) {
        super(context, R.style.CPContentPopup);
        this.context = context;
    }

    public void setNpTitle(String npTitle) {
        this.npTitle = npTitle;
    }

    public void setNpHtml(String npHtml) {
        this.npHtml = npHtml;
    }

    public void setSize(int width, int height){
        this.width = width-dpToPixel(20);
        this.height = height-dpToPixel(130);
    }

    @Override
    public void show() {
        setContentView(R.layout.dialog_notice);
        webView = (WebView)findViewById(R.id.webView);
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
        btn_layer = (RelativeLayout)findViewById(R.id.btn_layer);
        box_today = (LinearLayout)findViewById(R.id.box_today);
        tv_today = (TextView)findViewById(R.id.tv_today);
        tv_today.setOnClickListener(this);
        if( isToday.equals("Y")){
            tv_today.setText(context.getResources().getString(R.string.not_show));
        }else{
            tv_today.setText(context.getResources().getString(R.string.not_today));
        }
        btn_check = (Button)findViewById(R.id.btn_check);
        btn_check.setOnClickListener(this);
        btn_check.setSelected(false);
        btn_close = (Button)findViewById(R.id.btn_close);
        btn_close.setOnClickListener(this);

        resize();

        if( isBox){
            box_today.setVisibility(View.VISIBLE);
        }else{
            box_today.setVisibility(View.GONE);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(context.getResources().getString(R.string.notice_pop_top));
        //sb.append("<div class=notice_title>"+npTitle+"</div>");
        sb.append("<div class=notice_content>"+npHtml+"</div>");
        sb.append(context.getResources().getString(R.string.notice_pop_bottom));
        webView.loadDataWithBaseURL("file:///android_asset/", sb.toString(), "text/html", "utf-8", null);
        super.show();
    }

    public void resize(){
        int setWidth;
        if( height > width){
            setWidth = width;
            if( height < width + dpToPixel(80)){
                setWidth = width - dpToPixel(80);
            }
        }else{
            setWidth = height - dpToPixel(80);
        }
        ViewGroup.LayoutParams wparams = webView.getLayoutParams();
        wparams.width = setWidth;
        wparams.height = (int)Math.floor(setWidth*1.5);
        webView.setLayoutParams(wparams);
        ViewGroup.LayoutParams bparams = btn_layer.getLayoutParams();
        bparams.width = setWidth;
        bparams.height = dpToPixel(50);
        btn_layer.setLayoutParams(bparams);
    }
    public int dpToPixel(int dp){
//        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        final float scale = context.getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);
        Log.e("dpToPixel",px+"");
        return px;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_close:
                popupclose();
                break;
            case R.id.btn_check:
            case R.id.tv_today:
                btn_check.setSelected(!btn_check.isSelected());
                break;
        }
    }

    public void popupclose(){
        dismiss();
    }

    public Button getBtn_check() {
        return btn_check;
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
            Log.e("onPageFinished","onPageFinished");
            super.onPageFinished(view, url);
        }
    }

    public void setToday(boolean isBox){
        this.isBox = isBox;
    }

    public void setTodayTxt(String isToday){
        this.isToday = isToday;
    }

}
