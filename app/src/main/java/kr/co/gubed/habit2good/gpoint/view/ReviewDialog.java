package kr.co.gubed.habit2good.gpoint.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import kr.co.gubed.habit2good.gpoint.listener.ReviewListener;
import kr.co.gubed.habit2good.R;

public class ReviewDialog extends Dialog {

    private Context context;
    private RelativeLayout btn_yes;
    private RelativeLayout btn_bad;
    private Button btn_no;
    private ReviewListener reviewListener;

    public ReviewDialog(Context context, ReviewListener reviewListener) {
        super(context, R.style.CPAlertDialog);
        this.context = context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.reviewListener = reviewListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_review);
        btn_yes = (RelativeLayout)findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewListener.yes();
            }
        });
        btn_bad = (RelativeLayout)findViewById(R.id.btn_bad);
        btn_bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewListener.bad();
            }
        });
        btn_no = (Button)findViewById(R.id.btn_no);
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            btn_no.setText(Html.fromHtml("<u>"+context.getResources().getString(R.string.review_no)+"</u>", Html.FROM_HTML_MODE_LEGACY));
        }else{
            btn_no.setText(Html.fromHtml("<u>"+context.getResources().getString(R.string.review_no)+"</u>"));
        }
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewListener.no();
            }
        });
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
