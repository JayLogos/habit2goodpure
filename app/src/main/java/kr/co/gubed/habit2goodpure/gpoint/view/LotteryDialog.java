package kr.co.gubed.habit2goodpure.gpoint.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import kr.co.gubed.habit2goodpure.gpoint.listener.LotteryListener;
import kr.co.gubed.habit2goodpure.R;

public class LotteryDialog extends Dialog {

    private ImageView iv_effect_cash;
    private ImageView iv_prize;
    private RelativeLayout back_white;
    private ImageView iv_boom;
    private RelativeLayout front_layer;

    private Context context;
    private LotteryListener lotteryListener;
    private String prizeImg;

    private boolean isClick = false;
    private CashTask cashTask;
    private BoomTask boomTask;
    private int cashDelay1 = 200;
    private int cashDelay3 = 70;
    private int cashDelay4 = 30;
    private int boomDelay = 5;

    private String rank;

    Animation animation;
    public LotteryDialog(Context context) {
        super(context, R.style.full_screen_dialog);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.context = context;
    }

    public void setPrizeImg(String prizeImg){
        this.prizeImg = prizeImg;
    }

    public void setRankStr(String rank) {
        this.rank = rank;
    }

    private void KeepStatusBar(){
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeepStatusBar();
    }

    @Override
    public void show() {
        setContentView(R.layout.dialog_lottery);

        iv_effect_cash = (ImageView)findViewById(R.id.iv_effect_cash);
        iv_prize = (ImageView)findViewById(R.id.iv_prize);
        iv_boom = (ImageView)findViewById(R.id.iv_boom);
        back_white = (RelativeLayout)findViewById(R.id.back_white);
        front_layer = (RelativeLayout)findViewById(R.id.front_layer);

        Picasso.with(context).load(prizeImg).placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher).into(iv_prize);

        iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_1));
        iv_effect_cash.setVisibility(View.VISIBLE);
        iv_prize.setVisibility(View.INVISIBLE);
        back_white.setVisibility(View.GONE);
        iv_boom.setVisibility(View.GONE);
        front_layer.setVisibility(View.VISIBLE);

        front_layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( isClick) {
                    lotteryListener.procList();
                    dismiss();
                }
            }
        });

        if( cashTask == null){
            cashTask = new CashTask();
        }
        if( boomTask == null){
            boomTask = new BoomTask();
        }

        animation = AnimationUtils.loadAnimation(context, R.anim.scale_show);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                iv_prize.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        cashTask.sendEmptyMessageDelayed(0, cashDelay1);

        super.show();
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
            cashTask.removeCallbacksAndMessages(null);
            boomTask.removeCallbacksAndMessages(null);
        }catch (Exception ignore){}
    }

    public void setLotteryListener(LotteryListener lotteryListener){
        this.lotteryListener = lotteryListener;
    }

    private class CashTask extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch( msg.what){
                case 0:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_2));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay3);
                    break;
                case 1:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_3));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay3);
                    break;
                case 2:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_4));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay3);
                    break;
                case 3:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_5));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay3);
                    break;
                case 4:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_6));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay4);
                    break;
                case 5:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_1));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay1);
                    break;
                case 6:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_2));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay3);
                    break;
                case 7:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_3));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay3);
                    break;
                case 8:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_4));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay3);
                    break;
                case 9:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_5));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay3);
                    break;
                case 10:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_6));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay4);
                    break;
                case 11:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_1));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay1);
                    break;
                case 12:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_2));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay3);
                    break;
                case 13:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_3));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay3);
                    break;
                case 14:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_4));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay3);
                    break;
                case 15:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_5));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay3);
                    break;
                case 16:
                    iv_effect_cash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eventeffect1_6));
                    back_white.setVisibility(View.VISIBLE);
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay4);
                    break;
                case 17:
                    back_white.setBackgroundColor(ContextCompat.getColor(context, R.color.trans_w_20));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay4);
                    break;
                case 18:
                    back_white.setBackgroundColor(ContextCompat.getColor(context, R.color.trans_w_40));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay4);
                    break;
                case 19:
                    back_white.setBackgroundColor(ContextCompat.getColor(context, R.color.trans_w_60));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay4);
                    break;
                case 20:
                    back_white.setBackgroundColor(ContextCompat.getColor(context, R.color.trans_w_80));
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay4);
                    break;
                case 21:
                    back_white.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                    iv_effect_cash.setVisibility(View.GONE);
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay4);
                    break;
                case 22:
                    back_white.setBackgroundColor(ContextCompat.getColor(context, R.color.trans_w_60));
                    if( rank.equals("1")) {
                        iv_boom.setVisibility(View.VISIBLE);
                        iv_boom.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.boom_start));
                    }
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay4);
                    break;
                case 23:
                    back_white.setBackgroundColor(ContextCompat.getColor(context, R.color.trans_w_30));
                    if( rank.equals("1")) {
                        iv_boom.setVisibility(View.GONE);
                    }
                    cashTask.sendEmptyMessageDelayed(msg.what+1, cashDelay4);
                    break;
                case 24:
                    back_white.setBackgroundColor(ContextCompat.getColor(context, R.color.trans_w_10));
//                    iv_prize.setVisibility(View.VISIBLE);
                    iv_prize.startAnimation(animation);
                    if( rank.equals("1")) {
                        boomTask.sendEmptyMessageDelayed(0, boomDelay);
                    }else{
                        if( !isClick){
                            isClick = true;
                        }
                    }
                    break;
            }
        }
    }

    private class BoomTask extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    iv_boom.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.boom_1));
                    iv_boom.setVisibility(View.VISIBLE);
                    boomTask.sendEmptyMessageDelayed(1, boomDelay);
                    break;
                case 1:
                    iv_boom.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.boom_2));
                    boomTask.sendEmptyMessageDelayed(2, boomDelay);
                    break;
                case 2:
                    iv_boom.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.boom_3));
                    boomTask.sendEmptyMessageDelayed(3, boomDelay);
                    break;
                case 3:
                    iv_boom.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.boom_4));
                    boomTask.sendEmptyMessageDelayed(4, boomDelay);
                    break;
                case 4:
                    iv_boom.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.boom_5));
                    boomTask.sendEmptyMessageDelayed(5, boomDelay);
                    break;
                case 5:
                    iv_boom.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.boom_6));
                    boomTask.sendEmptyMessageDelayed(6, boomDelay);
                    break;
                case 6:
                    iv_boom.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.boom_7));
                    if( !isClick){
                        isClick = true;
                    }
                    boomTask.sendEmptyMessageDelayed(0, boomDelay);
                    break;
            }
        }
    }

}
