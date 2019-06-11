package kr.co.gubed.habit2goodpure.gpoint.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import kr.co.gubed.habit2goodpure.gpoint.listener.CpiListener;
import kr.co.gubed.habit2goodpure.gpoint.model.AdModel;
import kr.co.gubed.habit2goodpure.gpoint.util.CommonUtil;
import kr.co.gubed.habit2goodpure.R;

public class CpiDialog extends Dialog {

    private LinearLayout layer_icon;
    private LinearLayout layer_wide;

    private ImageView iv_icon;
    private TextView tv_title;
    private TextView tv_desc;

    private LinearLayout layer_coin;
    private TextView tv_coin;
    private LinearLayout layer_gold;
    private TextView tv_gold;

    private ImageView iv_wide;
    private TextView tv_wtitle;
    private TextView tv_wdesc;

    private LinearLayout layer_wcoin;
    private TextView tv_wcoin;
    private LinearLayout layer_wgold;
    private TextView tv_wgold;

    private TextView tv_sub;

    private LinearLayout desc_layer;

    private Button btn_ok;
    private Context context;
    public CpiDialog(Context context) {
        super(context, R.style.CPAlertDialog);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void open(final AdModel adModel, final CpiListener cpiListener){
        try{
            setContentView(R.layout.dialog_cpi);
            layer_icon = (LinearLayout)findViewById(R.id.layer_icon);
            layer_wide = (LinearLayout)findViewById(R.id.layer_wide);

            iv_icon = (ImageView)findViewById(R.id.iv_icon);
            tv_title = (TextView) findViewById(R.id.tv_title);
            tv_desc = (TextView) findViewById(R.id.tv_desc);
            layer_coin = (LinearLayout) findViewById(R.id.layer_coin);
            tv_coin = (TextView) findViewById(R.id.tv_coin);
            layer_gold = (LinearLayout) findViewById(R.id.layer_gold);
            tv_gold = (TextView) findViewById(R.id.tv_gold);

            iv_wide = (ImageView)findViewById(R.id.iv_wide);
            tv_wtitle = (TextView) findViewById(R.id.tv_wtitle);
            tv_wdesc = (TextView) findViewById(R.id.tv_wdesc);
            layer_wcoin = (LinearLayout) findViewById(R.id.layer_wcoin);
            tv_wcoin = (TextView) findViewById(R.id.tv_wcoin);
            layer_wgold = (LinearLayout) findViewById(R.id.layer_wgold);
            tv_wgold = (TextView) findViewById(R.id.tv_wgold);

            tv_sub = (TextView) findViewById(R.id.tv_sub);
            desc_layer = (LinearLayout) findViewById(R.id.desc_layer);
            btn_ok = (Button)findViewById(R.id.btn_ok);

            if( adModel.getAdType().equals("1")) {
                layer_icon.setVisibility(View.VISIBLE);
                layer_wide.setVisibility(View.GONE);
                Picasso.with(context).load(adModel.getImage()).placeholder(R.drawable.ic_launcher).error(R.drawable.df_store).into(iv_icon);

                tv_title.setText(adModel.getName());
                tv_desc.setText(adModel.getAdtxt());

                try {
                    double reward_gold = Double.parseDouble(adModel.getCash());
                    if (reward_gold > 0) {
                        layer_gold.setVisibility(View.VISIBLE);
                        tv_gold.setText(CommonUtil.setComma(reward_gold + "", true, false));
                    } else {
                        layer_gold.setVisibility(View.GONE);
                    }
                } catch (Exception ignore) {
                    layer_gold.setVisibility(View.GONE);
                }
                try {
                    double reward_coin = Double.parseDouble(adModel.getCoin());
                    if (reward_coin > 0) {
                        layer_coin.setVisibility(View.VISIBLE);
                        tv_coin.setText(CommonUtil.setComma(reward_coin + "", true, false));
                    } else {
                        layer_coin.setVisibility(View.GONE);
                    }
                } catch (Exception ignore) {
                    layer_coin.setVisibility(View.GONE);
                }
            }else{
                layer_icon.setVisibility(View.GONE);
                layer_wide.setVisibility(View.VISIBLE);
                Picasso.with(context).load(adModel.getImage()).placeholder(R.drawable.ic_launcher).error(R.drawable.df_store).into(iv_wide);

                tv_wtitle.setText(adModel.getName());
                tv_wdesc.setText(adModel.getAdtxt());

                try {
                    double reward_gold = Double.parseDouble(adModel.getCash());
                    if (reward_gold > 0) {
                        layer_wgold.setVisibility(View.VISIBLE);
                        tv_wgold.setText(CommonUtil.setComma(reward_gold + "", true, false));
                    } else {
                        layer_wgold.setVisibility(View.GONE);
                    }
                } catch (Exception ignore) {
                    layer_wgold.setVisibility(View.GONE);
                }
                try {
                    double reward_coin = Double.parseDouble(adModel.getCoin());
                    if (reward_coin > 0) {
                        layer_wcoin.setVisibility(View.VISIBLE);
                        tv_wcoin.setText(CommonUtil.setComma(reward_coin + "", true, false));
                    } else {
                        layer_wcoin.setVisibility(View.GONE);
                    }
                } catch (Exception ignore) {
                    layer_wcoin.setVisibility(View.GONE);
                }
            }
            String descStr = "";

            if( adModel.getActionType().equals("2")){
                tv_sub.setVisibility(View.GONE);
                descStr = context.getResources().getString(R.string.cpi_confirm_desc2);
                descStr+= "\n"+context.getResources().getString(R.string.cpi_confirm_desc3);
                descStr+= "\n"+context.getResources().getString(R.string.cpi_confirm_desc4);
                descStr+= "\n"+context.getResources().getString(R.string.cpi_confirm_desc5);
            }else {
                tv_sub.setVisibility(View.VISIBLE);
                if( adModel.getTask().equals("2") || adModel.getTask().equals("3")) {
                    //install+run or install+action
                    tv_sub.setText(context.getResources().getString(R.string.cpi_run_sub));
                    descStr = context.getResources().getString(R.string.cpi_run_desc1);
                    descStr+= "\n"+context.getResources().getString(R.string.cpi_run_desc2);
                    descStr+= "\n"+context.getResources().getString(R.string.cpi_run_desc3);
                    descStr+= "\n"+context.getResources().getString(R.string.cpi_run_desc4);
                    descStr+= "\n"+context.getResources().getString(R.string.cpi_run_desc5);
                }else{
                    //install
                    tv_sub.setText(context.getResources().getString(R.string.cpi_confirm_sub));
                    descStr = context.getResources().getString(R.string.cpi_confirm_desc1);
                    descStr+= "\n"+context.getResources().getString(R.string.cpi_confirm_desc2);
                    descStr+= "\n"+context.getResources().getString(R.string.cpi_confirm_desc3);
                    descStr+= "\n"+context.getResources().getString(R.string.cpi_confirm_desc4);
                    descStr+= "\n"+context.getResources().getString(R.string.cpi_confirm_desc5);
                }
            }

            String[] descArr = descStr.split("\n");
            for(int i=0;i<descArr.length;i++){
                LinearLayout descItem = new LinearLayout(context);
                descItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                descItem.setGravity(Gravity.TOP|Gravity.LEFT);
                descItem.setOrientation(LinearLayout.HORIZONTAL);
                TextView tab = new TextView(context);
                tab.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tab.setTextColor(ContextCompat.getColor(context, R.color.text_desc));
                tab.setText("- ");
                descItem.addView(tab);
                TextView desc = new TextView(context);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                desc.setLayoutParams(params);
                desc.setTextColor(ContextCompat.getColor(context, R.color.text_desc));
                desc.setText(descArr[i]);
                descItem.addView(desc);
                desc_layer.addView(descItem);
            }

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cpiListener.start(adModel);
                }
            });

        }catch (Exception ignore){}
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        this.getWindow().setAttributes(params);
        this.show();
    }



}
