package kr.co.gubed.habit2goodpure.gpoint.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.co.gubed.habit2goodpure.gpoint.util.CommonUtil;
import kr.co.gubed.habit2goodpure.R;

public class CashPopDialog extends Dialog {

    private RelativeLayout dialog_custom_view;
    private View addView;
    private View addView2;
    private TextView dialog_title;
    private String cpTitle;
    private TextView dialog_desc;
    private CharSequence cpDesc;
    private TextView dialog_desc1;
    private CharSequence cpDesc1;
    private LinearLayout dialog_gold_coin_layer;
    private LinearLayout layer_coin;
    private LinearLayout layer_gold;
    private TextView tv_gold;
    private CharSequence gold;
    private TextView tv_coin;
    private CharSequence coin;
    private TextView dialog_desc_sub;
    private LinearLayout offer_desc;
    private CharSequence cpDescSub;
    private EditText dialog_editor;
    private RelativeLayout dialog_custom_view2;
    private String cpEditHint;

    private LinearLayout dialog_btns;

    private Button btn_neutrality;
    private View.OnClickListener neutralityClickListener;
    private String neutralityTxt;
    private Button btn_cancel;
    private View.OnClickListener cancelClickListener;
    private String cancelTxt;
    private Button btn_ok;
    private View.OnClickListener okClickListener;
    private String okTxt;

    private RelativeLayout btn_line1;
    private RelativeLayout btn_line2;

    private LinearLayout dialog_btns_b;
    private RelativeLayout b_btn_line;
    private Button btn_b_cancel;
    private View.OnClickListener b_cancleClickListener;
    private String b_cancelTxt;

    private Button btn_b_ok;
    private View.OnClickListener b_okClickListener;
    private String b_okTxt;

    private boolean isCancel = true;
    private boolean isGone = false;
    private int inputType = 1;

    public CashPopDialog(Context context) {
        super(context, R.style.CPAlertDialog);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setCpView(View addView){
        this.addView = addView;
    }

    public void setCpView2(View addView2) {
        this.addView2 = addView2;
    }

    public void setCpTitle(String cpTitle){
        this.cpTitle = cpTitle;
    }
    public void setCpDesc(CharSequence cpDesc){
        this.cpDesc = cpDesc;
    }
    public void setCpDesc1(CharSequence cpDesc1){
        this.cpDesc1 = cpDesc1;
    }
    public void setGoldCoin(CharSequence gold, CharSequence coin){
        this.gold = gold;
        this.coin = coin;
    }
    public void setCpDescSub(CharSequence cpDescSub){
        this.cpDescSub = cpDescSub;
    }

    public void setCpEdit(String cpEditHint, int inputType){
        this.cpEditHint = cpEditHint;
        this.inputType = inputType;
    }

    public void setCpEditGone(){
        this.dialog_editor.setVisibility(View.GONE);
        this.cpEditHint = null;
    }

    public void setCpNeutralityButton(String neutralityTxt, View.OnClickListener clickListener){
        this.neutralityTxt = neutralityTxt;
        this.neutralityClickListener = clickListener;
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

    public void setCpEditorError(String error){
        dialog_editor.setError(error);
    }

    public String getCpEditText(){
        String editText = "";
        if( dialog_editor != null && !dialog_editor.getText().toString().equals("")){
            editText = dialog_editor.getText().toString();
        }
        return editText;
    }

    public void setCpBCancelButton(String b_cancelTxt, View.OnClickListener clickListener){
        this.b_cancelTxt = b_cancelTxt;
        this.b_cancleClickListener = clickListener;
    }
    public void setCpBOkButton(String b_okTxt, View.OnClickListener clickListener){
        this.b_okTxt = b_okTxt;
        this.b_okClickListener = clickListener;
    }

    @Override
    public void show() {
        try {
            setContentView(R.layout.dialog_cashpop);
            dialog_custom_view = (RelativeLayout) findViewById(R.id.dialog_custom_view);

            dialog_title = (TextView) findViewById(R.id.dialog_title);
            dialog_desc = (TextView) findViewById(R.id.dialog_desc);
            dialog_desc.setMovementMethod(new ScrollingMovementMethod());
            dialog_desc1 = (TextView) findViewById(R.id.dialog_desc1);
            dialog_desc1.setMovementMethod(new ScrollingMovementMethod());
            dialog_gold_coin_layer = (LinearLayout)findViewById(R.id.dialog_gold_coin_layer);
            layer_coin = (LinearLayout)findViewById(R.id.layer_coin);
            layer_gold = (LinearLayout)findViewById(R.id.layer_gold);

            tv_gold = (TextView)findViewById(R.id.tv_gold);
            tv_coin = (TextView)findViewById(R.id.tv_coin);

            dialog_desc_sub = (TextView) findViewById(R.id.dialog_desc_sub);
            dialog_desc_sub.setMovementMethod(new ScrollingMovementMethod());
            offer_desc = (LinearLayout)findViewById(R.id.offer_desc);

            dialog_editor = (EditText) findViewById(R.id.dialog_editor);
            dialog_editor.setFilters(new InputFilter[]{CommonUtil.spaceFilter});

            dialog_custom_view2 = (RelativeLayout) findViewById(R.id.dialog_custom_view2);

            dialog_btns = (LinearLayout) findViewById(R.id.dialog_btns);
            dialog_btns.setVisibility(View.GONE);
            btn_neutrality = (Button) findViewById(R.id.btn_neutrality);
            btn_cancel = (Button) findViewById(R.id.btn_cancel);
            btn_ok = (Button) findViewById(R.id.btn_ok);

            btn_neutrality.setVisibility(View.GONE);
            btn_cancel.setVisibility(View.GONE);
            btn_ok.setVisibility(View.GONE);

            btn_line1 = (RelativeLayout)findViewById(R.id.btn_line1);
            btn_line2 = (RelativeLayout)findViewById(R.id.btn_line2);

            btn_line1.setVisibility(View.GONE);
            btn_line2.setVisibility(View.GONE);

            dialog_btns_b = (LinearLayout) findViewById(R.id.dialog_btns_b);
            dialog_btns_b.setVisibility(View.GONE);
            btn_b_cancel = (Button) findViewById(R.id.btn_b_cancel);
            b_btn_line = (RelativeLayout) findViewById(R.id.b_btn_line);
            btn_b_ok = (Button) findViewById(R.id.btn_b_ok);
            btn_b_cancel.setVisibility(View.GONE);
            btn_b_ok.setVisibility(View.GONE);


            if( addView != null) {
                dialog_custom_view.addView(addView);
                dialog_custom_view.setVisibility(View.VISIBLE);
            }
            if( cpTitle != null && !cpTitle.equals("")) {
                dialog_title.setText(cpTitle);
                dialog_title.setVisibility(View.VISIBLE);
            }
            if( cpDesc != null && !cpDesc.equals("")) {
                dialog_desc.setText(cpDesc);
                dialog_desc.setVisibility(View.VISIBLE);
            }
            if( gold != null && !gold.equals("") && coin != null && !coin.equals("")){
                tv_gold.setText("-"+CommonUtil.setComma(gold+"", true, false));
                if( gold.equals("0") || gold.equals("")){
                    layer_gold.setVisibility(View.GONE);
                }else{
                    layer_gold.setVisibility(View.VISIBLE);
                }
                tv_coin.setText("-"+CommonUtil.setComma(coin+"", false, false));
                if( coin.equals("0") || coin.equals("")){
                    layer_coin.setVisibility(View.GONE);
                }else{
                    layer_coin.setVisibility(View.VISIBLE);
                }
                dialog_gold_coin_layer.setVisibility(View.VISIBLE);
            }
            if( cpDesc1 != null && !cpDesc1.equals("")) {
                dialog_desc1.setText(cpDesc1);
                dialog_desc1.setVisibility(View.VISIBLE);
            }
            if( cpDescSub != null && !cpDescSub.equals("")) {
                dialog_desc_sub.setText(cpDescSub);
                dialog_desc_sub.setVisibility(View.VISIBLE);
                offer_desc.setVisibility(View.VISIBLE);
            }
            if( cpEditHint != null && !cpEditHint.equals("")) {
                dialog_editor.setHint(cpEditHint);
                dialog_editor.setVisibility(View.VISIBLE);
                dialog_editor.setInputType(InputType.TYPE_CLASS_TEXT | inputType);
            }
            if( addView2 != null) {
                dialog_custom_view2.addView(addView2);
                dialog_custom_view2.setVisibility(View.VISIBLE);
            }

            if( neutralityTxt != null && !neutralityTxt.equals("") && neutralityClickListener != null) {
                dialog_btns.setVisibility(View.VISIBLE);
                btn_neutrality.setText(neutralityTxt);
                btn_neutrality.setOnClickListener(neutralityClickListener);
                btn_neutrality.setVisibility(View.VISIBLE);
            }
            if( cancelTxt != null && !cancelTxt.equals("") && cancelClickListener != null) {
                dialog_btns.setVisibility(View.VISIBLE);
                btn_cancel.setText(cancelTxt);
                btn_cancel.setOnClickListener(cancelClickListener);
                btn_cancel.setVisibility(View.VISIBLE);
            }
            if( okTxt != null && !okTxt.equals("") && okClickListener != null) {
                dialog_btns.setVisibility(View.VISIBLE);
                btn_ok.setText(okTxt);
                btn_ok.setOnClickListener(okClickListener);
                btn_ok.setVisibility(View.VISIBLE);
            }

            if( btn_neutrality.getVisibility() == View.VISIBLE && btn_cancel.getVisibility() == View.VISIBLE){
                btn_line1.setVisibility(View.VISIBLE);
            }
            if( btn_cancel.getVisibility() == View.VISIBLE && btn_ok.getVisibility() == View.VISIBLE){
                btn_line2.setVisibility(View.VISIBLE);
            }

            if( b_cancelTxt != null && !b_cancelTxt.equals("") && b_cancleClickListener != null) {
                dialog_btns_b.setVisibility(View.VISIBLE);
                btn_b_cancel.setText(b_cancelTxt);
                btn_b_cancel.setOnClickListener(b_cancleClickListener);
                btn_b_cancel.setVisibility(View.VISIBLE);
            }
            if( b_okTxt != null && !b_okTxt.equals("") && b_okClickListener != null) {
                dialog_btns_b.setVisibility(View.VISIBLE);
                btn_b_ok.setText(b_okTxt);
                btn_b_ok.setOnClickListener(b_okClickListener);
                btn_b_ok.setVisibility(View.VISIBLE);
            }
            if( btn_b_cancel.getVisibility() == View.VISIBLE && btn_b_ok.getVisibility() == View.VISIBLE){
                b_btn_line.setVisibility(View.VISIBLE);
            }
            this.setCancelable(isCancel);
            super.show();
        }catch (Exception ignore){}
    }

    @Override
    public void dismiss() {
        isCancel = true;
        inputType = 1;
        dialog_custom_view.removeAllViews();
        addView = null;
        dialog_custom_view.setVisibility(View.GONE);
        dialog_custom_view2.removeAllViews();
        addView2 = null;
        dialog_custom_view2.setVisibility(View.GONE);
        dialog_title.setText("");
        cpTitle = null;
        dialog_title.setVisibility(View.GONE);
        dialog_desc.setText("");
        cpDesc = null;
        dialog_desc.setVisibility(View.GONE);
        dialog_desc1.setText("");
        cpDesc1 = null;
        dialog_desc1.setVisibility(View.GONE);
        dialog_gold_coin_layer.setVisibility(View.GONE);
        tv_gold.setText("");
        tv_coin.setText("");
        dialog_desc_sub.setText("");
        cpDescSub = null;
        offer_desc.setVisibility(View.GONE);
        dialog_desc_sub.setVisibility(View.GONE);
        dialog_editor.setText("");
        dialog_editor.setHint("");
        cpEditHint = null;
        dialog_editor.setVisibility(View.GONE);
        btn_neutrality.setText("");
        neutralityTxt = null;
//        btn_neutrality.setOnClickListener(null);
//        neutralityClickListener = null;
        dialog_btns.setVisibility(View.GONE);
        btn_neutrality.setVisibility(View.GONE);
        btn_cancel.setText("");
        cancelTxt = null;
//        btn_cancel.setOnClickListener(null);
//        cancelClickListener = null;
        btn_cancel.setVisibility(View.GONE);
        btn_ok.setText("");
        okTxt = null;
        btn_ok.setOnClickListener(null);
//        okClickListener = null;
//        btn_ok.setVisibility(View.GONE);
        dialog_btns_b.setVisibility(View.GONE);
        btn_b_cancel.setText("");
//        btn_b_cancel.setOnClickListener(null);
//        b_cancleClickListener = null;
        btn_b_cancel.setVisibility(View.GONE);
        b_cancelTxt = null;
        btn_b_ok.setText("");
//        btn_b_ok.setOnClickListener(null);
//        b_okClickListener = null;
        btn_b_ok.setVisibility(View.GONE);
        b_okTxt = null;
        super.dismiss();
    }

}
