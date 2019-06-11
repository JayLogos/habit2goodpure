package kr.co.gubed.habit2goodpure.gpoint.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import kr.co.gubed.habit2goodpure.gpoint.util.Applications;
import kr.co.gubed.habit2goodpure.R;

public class TermsActivity extends Activity implements View.OnClickListener{

    private Button btn_back;
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        webview= (WebView)findViewById(R.id.webview);
        webview.loadUrl("http://a.habit2good.com/user.html?country="+ Applications.getCountry(this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }
}
