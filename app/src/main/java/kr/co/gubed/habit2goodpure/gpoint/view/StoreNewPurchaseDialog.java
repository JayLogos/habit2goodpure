package kr.co.gubed.habit2goodpure.gpoint.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import kr.co.gubed.habit2goodpure.gpoint.listener.BuyListener;
import kr.co.gubed.habit2goodpure.gpoint.util.Applications;
import kr.co.gubed.habit2goodpure.gpoint.util.CommonUtil;
import kr.co.gubed.habit2goodpure.R;

public class StoreNewPurchaseDialog extends Dialog {

    private TextView tv_title;
    private Button btn_back;

    private TextView tv_pay;
    private TextView tv_linked_gold;

    private RelativeLayout linked_gold_desc;
    private TextView tv_linked_gold_desc;

    private TextView tv_normal_gold;

    private LinearLayout not_enough_normal_gold_line;
    private RelativeLayout not_enough_normal_gold_layer;


    private TextView tv_not_enough_normal_gold;

    private LinearLayout input_layer1;
    private TextView tv_input1;
    private EditText et_input1;
    private LinearLayout input_layer2;
    private TextView tv_input2;
    private EditText et_input2;
    private LinearLayout input_layer3;
    private TextView tv_input3;
    private EditText et_input3;
    private LinearLayout input_layer4;
    private TextView tv_input4;
    private EditText et_input4;

    private Button btn_submit;
    private LinearLayout desc_layer;
    private Context context;

    boolean isPurchase = false;

    public StoreNewPurchaseDialog(Context context){
        super(context, R.style.CPAlertDialog);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void open(final JSONObject jobj, final HashMap<Integer, JSONObject> template, final BuyListener listener){
        setContentView(R.layout.store_new_purchase);
        try {
            final JSONArray inputArr = new JSONArray(template.get(jobj.getInt("inputNo")).getString("text"));
            tv_title = (TextView)findViewById(R.id.tv_title);
            btn_back = (Button)findViewById(R.id.btn_back);
            tv_pay = (TextView)findViewById(R.id.tv_pay);
            tv_linked_gold = (TextView)findViewById(R.id.tv_linked_gold);

            linked_gold_desc = (RelativeLayout)findViewById(R.id.linked_gold_desc);
            tv_linked_gold_desc = (TextView)findViewById(R.id.tv_linked_gold_desc);

            tv_normal_gold = (TextView)findViewById(R.id.tv_normal_gold);

            not_enough_normal_gold_line = (LinearLayout)findViewById(R.id.not_enough_normal_gold_line);
            not_enough_normal_gold_layer = (RelativeLayout)findViewById(R.id.not_enough_normal_gold_layer);

            tv_not_enough_normal_gold = (TextView)findViewById(R.id.tv_not_enough_normal_gold);

            input_layer1 = (LinearLayout)findViewById(R.id.input_layer1);
            tv_input1 = (TextView)findViewById(R.id.tv_input1);
            et_input1 = (EditText)findViewById(R.id.et_input1);
            input_layer2 = (LinearLayout)findViewById(R.id.input_layer2);
            tv_input2 = (TextView)findViewById(R.id.tv_input2);
            et_input2 = (EditText)findViewById(R.id.et_input2);
            input_layer3 = (LinearLayout)findViewById(R.id.input_layer3);
            tv_input3 = (TextView)findViewById(R.id.tv_input3);
            et_input3 = (EditText)findViewById(R.id.et_input3);
            input_layer4 = (LinearLayout)findViewById(R.id.input_layer4);
            tv_input4 = (TextView)findViewById(R.id.tv_input4);
            et_input4 = (EditText)findViewById(R.id.et_input4);

            btn_submit = (Button)findViewById(R.id.btn_submit);
            desc_layer = (LinearLayout)findViewById(R.id.desc_layer);

            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        if( isPurchase) {
                            String str_input1 = et_input1.getEditableText().toString();
                            String str_input2 = et_input2.getEditableText().toString();
                            String str_input3 = et_input3.getEditableText().toString();
                            String str_input4 = et_input4.getEditableText().toString();
                            if( inputArr.getJSONObject(0) != null && !inputArr.getJSONObject(0).getString("key").equals("") && !inputArr.getJSONObject(0).getString("val").equals("")) {
                                if( str_input1.equals("")) {
                                    et_input1.setError(inputArr.getJSONObject(0).getString("val"));
                                    return;
                                }
                            }
                            if( inputArr.getJSONObject(1) != null && !inputArr.getJSONObject(1).getString("key").equals("") && !inputArr.getJSONObject(1).getString("val").equals("")) {
                                if( str_input2.equals("")) {
                                    et_input2.setError(inputArr.getJSONObject(1).getString("val"));
                                    return;
                                }
                            }
                            if( inputArr.getJSONObject(2) != null && !inputArr.getJSONObject(2).getString("key").equals("") && !inputArr.getJSONObject(2).getString("val").equals("")) {
                                if( str_input3.equals("")) {
                                    et_input3.setError(inputArr.getJSONObject(2).getString("val"));
                                    return;
                                }
                            }
                            if( inputArr.getJSONObject(3) != null && !inputArr.getJSONObject(3).getString("key").equals("") && !inputArr.getJSONObject(3).getString("val").equals("")) {
                                if( str_input4.equals("")) {
                                    et_input4.setError(inputArr.getJSONObject(3).getString("val"));
                                    return;
                                }
                            }
                            listener.buyNewProc(jobj, str_input1, str_input2, str_input3, str_input4);
                        }else{
                            Toast toast = Toast.makeText(context, context.getResources().getString(R.string.more_gold), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                            toast.show();
                        }
                    }catch (Exception ignore){}
                }
            });

            double gpoint = Applications.ePreference.getBalanceGpoint();
            double linked_gold = Applications.ePreference.getNLinkedGold();
            double normal_gold = Applications.ePreference.getBalanceGpoint();
            int linkedMaxPer = 0;
            if( jobj.getString("linkedMaxPer") != null && !jobj.getString("linkedMaxPer").toLowerCase().equals("null")){
                try{
                    linkedMaxPer = Integer.parseInt(jobj.getString("linkedMaxPer"));
                }catch (Exception e){
                    linkedMaxPer = 80;
                }
            }

            int item_gold = 0;
            if( jobj.getString("gold") != null && !jobj.getString("gold").toLowerCase().equals("null")){
                item_gold = Integer.parseInt(jobj.getString("gold"));
            }

            double linkedMaxPerVal = (double) linkedMaxPer/100;
//            double item_linked_gold = Math.min(linked_gold, (double)(item_gold*linkedMaxPerVal));
//            double item_normal_gold = Math.min(normal_gold, item_gold-item_linked_gold);
            double item_normal_gold = Math.min(normal_gold, (double)(item_gold*linkedMaxPerVal));
            double item_linked_gold = Math.min(linked_gold, item_gold-item_normal_gold);

            double total_gold = item_linked_gold+item_normal_gold;
            if( item_gold <= gpoint){
                isPurchase = true;
            }else{
                isPurchase = false;
            }

            String price = jobj.getString("price");

            tv_pay.setText(CommonUtil.setComma(item_gold+"", true, false));
            tv_linked_gold.setText(CommonUtil.setComma(-item_linked_gold+"", true, false));
            //tv_normal_gold.setText(CommonUtil.setComma(-item_normal_gold+"", true, false));
            tv_normal_gold.setText(CommonUtil.setComma((gpoint-item_gold)+"", true, false));

            if( isPurchase){
                linked_gold_desc.setVisibility(View.GONE);
                not_enough_normal_gold_line.setVisibility(View.GONE);
                not_enough_normal_gold_layer.setVisibility(View.GONE);
            }else{
                linked_gold_desc.setVisibility(View.VISIBLE);
                not_enough_normal_gold_line.setVisibility(View.VISIBLE);
                not_enough_normal_gold_layer.setVisibility(View.VISIBLE);
                tv_linked_gold_desc.setText(context.getResources().getString(R.string.use_gold, CommonUtil.setComma(linked_gold+"", true, false)+""));
                double more_gold = item_gold-gpoint;
                tv_not_enough_normal_gold.setText(CommonUtil.setComma(more_gold+"", true, false));
            }

            tv_title.setText(jobj.getString("product"));
            if( inputArr.getJSONObject(0) != null && !inputArr.getJSONObject(0).getString("key").equals("")) {
                input_layer1.setVisibility(View.VISIBLE);
                try{
                    tv_input1.setText(inputArr.getJSONObject(0).getString("key"));
                }catch (Exception ignore){}
                try{
                    et_input1.setHint(inputArr.getJSONObject(0).getString("val"));
                }catch (Exception ignore){}
                et_input1.setInputType(InputType.TYPE_CLASS_TEXT);
            }else{
                input_layer1.setVisibility(View.GONE);
            }
            if( inputArr.getJSONObject(1) != null && !inputArr.getJSONObject(1).getString("key").equals("")) {
                input_layer2.setVisibility(View.VISIBLE);
                try{
                    tv_input2.setText(inputArr.getJSONObject(1).getString("key"));
                }catch (Exception ignore){}
                try{
                    et_input2.setHint(inputArr.getJSONObject(1).getString("val"));
                }catch (Exception ignore){}
                et_input2.setInputType(InputType.TYPE_CLASS_TEXT);
            }else{
                input_layer2.setVisibility(View.GONE);
            }
            if( inputArr.getJSONObject(2) != null && !inputArr.getJSONObject(2).getString("key").equals("")) {
                input_layer3.setVisibility(View.VISIBLE);
                try{
                    tv_input3.setText(inputArr.getJSONObject(2).getString("key"));
                }catch (Exception ignore){}
                try{
                    et_input3.setHint(inputArr.getJSONObject(2).getString("val"));
                }catch (Exception ignore){}
                et_input3.setInputType(InputType.TYPE_CLASS_TEXT);
            }else{
                input_layer3.setVisibility(View.GONE);
            }
            if( inputArr.getJSONObject(3) != null && !inputArr.getJSONObject(3).getString("key").equals("")) {
                input_layer4.setVisibility(View.VISIBLE);
                try{
                    tv_input4.setText(inputArr.getJSONObject(3).getString("key"));
                }catch (Exception ignore){}
                try {
                    et_input4.setHint(inputArr.getJSONObject(3).getString("val"));
                }catch (Exception ignore){}
                et_input4.setInputType(InputType.TYPE_CLASS_TEXT);
            }else{
                input_layer4.setVisibility(View.GONE);
            }

            String descStr = template.get(jobj.getInt("descriptionNo")).getString("text");
            String[] descArr = descStr.split("\n");
            for(int i=0;i<descArr.length;i++){
                LinearLayout descItem = new LinearLayout(context);
                descItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                descItem.setGravity(Gravity.TOP|Gravity.LEFT);
                descItem.setOrientation(LinearLayout.HORIZONTAL);
                TextView tab = new TextView(context);
                tab.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tab.setTextColor(ContextCompat.getColor(context, R.color.text_desc));
                if( descArr[i].startsWith("•")){
                    tab.setText("- ");
                }
                descItem.addView(tab);
                TextView desc = new TextView(context);
                desc.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                desc.setTextColor(ContextCompat.getColor(context, R.color.text_desc));
                desc.setText(descArr[i].replaceAll("•","").trim());
                descItem.addView(desc);
                desc_layer.addView(descItem);
            }
        }catch (Exception ignore){
            ignore.printStackTrace();
        }
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        this.getWindow().setAttributes(params);
        this.show();
    }
}
