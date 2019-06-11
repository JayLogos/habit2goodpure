package kr.co.gubed.habit2goodpure.gpoint.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.gubed.habit2goodpure.gpoint.util.CommonUtil;
import kr.co.gubed.habit2goodpure.R;

public class EventUseDialog extends Dialog {

    private Context context;

    private TextView tv_title;
    private CharSequence title;

    private TextView tv_event_title;
    private LinearLayout event_coin_layer;

    private LinearLayout layer_coin;
    private LinearLayout layer_gold;
    private TextView tv_gold;
    private CharSequence gold;
    private TextView tv_coin;
    private CharSequence coin;

    private Button btn_cancel;
    private View.OnClickListener cancelClickListener;
    private String cancelTxt;
    private Button btn_ok;
    private View.OnClickListener okClickListener;
    private String okTxt;

    private boolean isCancel = true;

    public EventUseDialog(Context context) {
        super(context, R.style.CPAlertDialog);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setTitle(String title){
        this.title = title;
    }


    public void setGoldCoin(CharSequence gold, CharSequence coin){
        this.gold = gold;
        this.coin = coin;
    }

    public void setCpCancelButton(String cancelTxt, View.OnClickListener clickListener){
        this.cancelTxt = cancelTxt;
        this.cancelClickListener = clickListener;
    }

    public void setCpOkButton(String okTxt, View.OnClickListener clickListener){
        this.okTxt = okTxt;
        this.okClickListener = clickListener;
    }

    public void setCpCancel(boolean isCancel){
        this.isCancel = isCancel;
    }

    public void setCpBCancelButton(String b_cancelTxt, View.OnClickListener clickListener){
        this.cancelTxt = b_cancelTxt;
        this.cancelClickListener = clickListener;
    }
    public void setCpBOkButton(String b_okTxt, View.OnClickListener clickListener){
        this.okTxt = b_okTxt;
        this.okClickListener = clickListener;
    }

    @Override
    public void show() {
        try {
            setContentView(R.layout.dialog_event_user);

            tv_title = (TextView)findViewById(R.id.tv_title);
            if( title != null && !title.equals("")){
                tv_title.setText(title);
            }else{
                tv_title.setText(getContext().getResources().getString(R.string.lottery_title));
            }
            layer_coin = (LinearLayout)findViewById(R.id.layer_coin);
            layer_gold = (LinearLayout)findViewById(R.id.layer_gold);

            tv_event_title = (TextView)findViewById(R.id.tv_event_title);
            event_coin_layer = (LinearLayout)findViewById(R.id.event_coin_layer);

            tv_gold = (TextView)findViewById(R.id.tv_gold);
            tv_coin = (TextView)findViewById(R.id.tv_coin);

            btn_cancel = (Button) findViewById(R.id.btn_cancel);
            btn_ok = (Button) findViewById(R.id.btn_ok);

            if( gold != null && !gold.equals("") && coin != null && !coin.equals("")){
                tv_gold.setText("-"+ CommonUtil.setComma(gold+"", true, false));
                if( gold.equals("")){
                    layer_gold.setVisibility(View.GONE);
                }else{
                    layer_gold.setVisibility(View.VISIBLE);
                }
                tv_coin.setText("-"+CommonUtil.setComma(coin+"", false, false));
                if( coin.equals("")){
                    layer_coin.setVisibility(View.GONE);
                }else{
                    layer_coin.setVisibility(View.VISIBLE);
                }
            }

            int c_gold = Integer.parseInt(gold+"");
            int c_coin = Integer.parseInt(coin+"");
            if( c_gold > 0 || c_coin > 0){
                tv_event_title.setText(context.getResources().getString(R.string.lottery_desc));
                event_coin_layer.setVisibility(View.VISIBLE);
            }else{
                tv_event_title.setText(context.getResources().getString(R.string.lottery_desc));
                event_coin_layer.setVisibility(View.VISIBLE);
            }

            if (cancelTxt != null && !cancelTxt.equals("") && cancelClickListener != null) {
                btn_cancel.setText(cancelTxt);
                btn_cancel.setOnClickListener(cancelClickListener);
                btn_cancel.setVisibility(View.VISIBLE);
            }
            if (okTxt != null && !okTxt.equals("") && okClickListener != null) {
                btn_ok.setText(okTxt);
                btn_ok.setOnClickListener(okClickListener);
                btn_ok.setVisibility(View.VISIBLE);
            }

            this.setCancelable(isCancel);
            super.show();
        }catch (Exception ignore){}
    }

    @Override
    public void dismiss() {
        isCancel = true;
        tv_gold.setText("");
        tv_coin.setText("");
        btn_cancel.setText("");
        cancelTxt = null;
        btn_cancel.setVisibility(View.GONE);
        btn_ok.setText("");
        okTxt = null;
        btn_ok.setOnClickListener(null);
        super.dismiss();
    }

}
