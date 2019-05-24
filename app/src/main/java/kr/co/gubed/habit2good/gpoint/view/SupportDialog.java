package kr.co.gubed.habit2good.gpoint.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import kr.co.gubed.habit2good.R;


public class SupportDialog extends Dialog {

    private Button btn_faq;
    private View.OnClickListener faqListener;
    private Button btn_cash_reward;
    private View.OnClickListener cashRewardListener;
    private Button btn_store;
    private View.OnClickListener storeListener;
    private Button btn_extra;
    private View.OnClickListener extraListener;

//    private Button btn_close;

    public SupportDialog(Context context) {
        super(context, R.style.CPAlertDialog);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void setListener(View.OnClickListener faqListener, View.OnClickListener cashRewardListener, View.OnClickListener storeListener, View.OnClickListener extraListener){
        this.faqListener = faqListener;
        this.cashRewardListener = cashRewardListener;
        this.storeListener = storeListener;
        this.extraListener = extraListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void show() {
        setContentView(R.layout.dialog_support);
        btn_faq = (Button)findViewById(R.id.btn_faq);
        if( faqListener != null) {
            btn_faq.setVisibility(View.VISIBLE);
            btn_faq.setOnClickListener(faqListener);
        }else{
            btn_faq.setVisibility(View.GONE);
        }
        btn_cash_reward = (Button)findViewById(R.id.btn_cash_reward);
        btn_cash_reward.setOnClickListener(cashRewardListener);
        btn_store = (Button)findViewById(R.id.btn_store);
        btn_store.setOnClickListener(storeListener);
        btn_extra = (Button)findViewById(R.id.btn_extra);
        btn_extra.setOnClickListener(extraListener);
//        btn_close = (Button)findViewById(R.id.btn_close);
//        btn_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dismiss();
//            }
//        });
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
