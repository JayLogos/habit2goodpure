package kr.co.gubed.habit2goodpure.gpoint.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tnkfactory.ad.NativeAdItem;

import java.util.ArrayList;

import kr.co.gubed.habit2goodpure.R;
import kr.co.gubed.habit2goodpure.gpoint.model.AdModel;
import kr.co.gubed.habit2goodpure.gpoint.model.LastModel;
import kr.co.gubed.habit2goodpure.gpoint.model.TitleModel;
import kr.co.gubed.habit2goodpure.gpoint.util.Applications;
import kr.co.gubed.habit2goodpure.gpoint.util.CommonUtil;
import kr.co.gubed.habit2goodpure.gpoint.util.Preference;

public class MissionAdapter extends RecyclerView.Adapter{

    public static final int VIEW_ICON = 1;
    public static final int VIEW_WIDE = 2;
    public static final int VIEW_ADMOB = 3;
    public static final int VIEW_TITLE = 4;
    public static final int VIEW_LAST = 5;

    private Context context;
    private ArrayList<Object> adList;

    public MissionAdapter(Context context, final ArrayList<Object> adList) {
        this.context = context;
        this.adList = adList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if( viewType == VIEW_ICON){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_offer, parent, false);
            viewHolder = new OfferViewHolder(itemView);
        }else if( viewType == VIEW_WIDE){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_advertise, parent, false);
            viewHolder = new AdViewHolder(itemView);
        } else if( viewType == VIEW_ADMOB){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gad, parent, false);
            viewHolder = new AdmobViewHolder(itemView);
        }else if( viewType == VIEW_TITLE){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_title, parent, false);
            viewHolder = new TitleViewHolder(itemView);
        }else if( viewType == VIEW_LAST){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_last, parent, false);
            viewHolder = new LastViewHolder(itemView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Object objItem = getItem(position);

        if( objItem instanceof AdModel) {
            final AdModel adModel = (AdModel) objItem;
            if( adModel != null){
                if( getItemViewType(position) == VIEW_ICON) {
                    ((OfferViewHolder) holder).root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
                    switch (adModel.getViewType()) {
                        case CommonUtil.AD_TYPE_TCASH:
                            ((OfferViewHolder) holder).layer_gold.setVisibility(View.VISIBLE);
                            ((OfferViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                            ((OfferViewHolder) holder).layer_trophy_plus.setVisibility(View.GONE);
                            ((OfferViewHolder) holder).layer_icon.setVisibility(View.VISIBLE);
                            Picasso.with(context).load(adModel.getImage()).error(R.drawable.logo2).into(((OfferViewHolder) holder).iv_offer);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                ((OfferViewHolder) holder).layer_type.setBackground(ContextCompat.getDrawable(context, R.drawable.offer_round_btn));
                            } else {
                                ((OfferViewHolder) holder).layer_type.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.offer_round_btn));
                            }
                            int iconDrawble = R.drawable.icon_down1;
                            switch (adModel.getTask()) {
                                case "1":
                                case "2":
                                case "3":
                                    iconDrawble = R.drawable.icon_down1;
                                    switch (adModel.getTask()) {
                                        case "1":
                                            ((OfferViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.install));
                                            break;
                                        case "2":
                                            ((OfferViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.install_run));
                                            break;
                                        case "3":
                                            ((OfferViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.install_action));
                                            break;
                                    }
                                    break;
                                case "4":
                                    //visit
                                    iconDrawble = R.drawable.icon_internet1;
                                    ((OfferViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.visit));
                                    break;
                                case "5":
                                case "6":
                                case "7":
                                    //like
                                    iconDrawble = R.drawable.icon_like1;
                                    ((OfferViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.facebook_like));
                                    break;
                            }
                            ((OfferViewHolder) holder).tv_icon.setSelected(true);
                            ((OfferViewHolder) holder).iv_icon.setImageDrawable(ContextCompat.getDrawable(context, iconDrawble));
                            if (adModel.getIsRun().equals("Y")) {
                                if (adModel.getRunStep().equals("-1")) {
                                    if (adModel.getActionType().equals("1")) {
                                        //cpi
                                        if (!Applications.dbHelper.chkCPIPackage(adModel.getPackage_name())) {
                                            if (adModel.getTask().equals("2") || adModel.getTask().equals("3")) {
                                                ((OfferViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.run_chk));
                                            } else {
                                                ((OfferViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.confirm_chk));
                                            }
                                            ((OfferViewHolder) holder).iv_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_now1));
                                        }
                                    }
                                    if (adModel.getIsAction().equals("N")) {
                                        ((OfferViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                        ((OfferViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                    } else {
                                        ((OfferViewHolder) holder).layer_gold.setVisibility(View.VISIBLE);
                                        ((OfferViewHolder) holder).layer_trophy.setVisibility(View.VISIBLE);
                                    }
                                    try {
                                        double reward_gold = Double.parseDouble(adModel.getCash());
                                        if (reward_gold > 0) {
                                            ((OfferViewHolder) holder).layer_gold.setVisibility(View.VISIBLE);
                                            ((OfferViewHolder) holder).tv_gold.setText(CommonUtil.setComma(reward_gold + "", true, false));
                                        } else {
                                            ((OfferViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                        }
                                    } catch (Exception ignore) {
                                        ((OfferViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                    }
                                    try {
                                        int reward_trophy = Integer.parseInt(adModel.getCoin());
                                        if (reward_trophy > 0) {
                                            ((OfferViewHolder) holder).layer_trophy.setVisibility(View.VISIBLE);
                                            ((OfferViewHolder) holder).tv_trophy.setText(CommonUtil.setComma(reward_trophy + "", false, false));
                                        } else {
                                            ((OfferViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                        }
                                    } catch (Exception ignore) {
                                        ((OfferViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                    }
                                    if (adModel.getIsAction().equals("N")) {
                                        ((OfferViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                        ((OfferViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                    }
                                } else {
                                    ((OfferViewHolder) holder).tv_icon.setText(context.getResources().getString(R.string.every_day_run_1) + "(" + adModel.getRunStep() + "/" + adModel.getRunCnt() + ")");
                                    if (adModel.getRunToday().equals("Y")) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            ((OfferViewHolder) holder).tv_icon.setText(Html.fromHtml("<s>" + context.getResources().getString(R.string.every_day_run_1) + "(" + adModel.getRunStep() + "/" + adModel.getRunCnt() + ")" + "</s>", Html.FROM_HTML_MODE_LEGACY));
                                        } else {
                                            ((OfferViewHolder) holder).tv_icon.setText(Html.fromHtml("<s>" + context.getResources().getString(R.string.every_day_run_1) + "(" + adModel.getRunStep() + "/" + adModel.getRunCnt() + ")" + "</s>"));
                                        }
                                    } else {
                                        ((OfferViewHolder) holder).tv_icon.setText(context.getResources().getString(R.string.every_day_run_1) + "(" + adModel.getRunStep() + "/" + adModel.getRunCnt() + ")");
                                    }
                                    ((OfferViewHolder) holder).tv_icon.setSelected(true);
                                    ((OfferViewHolder) holder).iv_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_run1));
                                    try {
                                        int run_reward_gold = Integer.parseInt(adModel.getRunReward());
                                        if (run_reward_gold > 0) {
                                            ((OfferViewHolder) holder).layer_gold.setVisibility(View.VISIBLE);
                                            ((OfferViewHolder) holder).tv_gold.setText(CommonUtil.setComma(run_reward_gold + "", true, false) + "/" + context.getResources().getString(R.string.time));
                                        } else {
                                            ((OfferViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                        }
                                    } catch (Exception ignore) {
                                        ((OfferViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                    }
                                    try {
                                        int run_reward_coin1 = Integer.parseInt(adModel.getRunCoin());
                                        if (run_reward_coin1 > 0) {
                                            ((OfferViewHolder) holder).layer_trophy.setVisibility(View.VISIBLE);
                                            ((OfferViewHolder) holder).tv_trophy.setText(CommonUtil.setComma(run_reward_coin1 + "", false, false) + "/" + context.getResources().getString(R.string.time));
                                        } else {
                                            ((OfferViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                        }
                                    } catch (Exception ignore) {
                                        ((OfferViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                if (adModel.getActionType().equals("1")) {
                                    //cpi
                                    if (!Applications.dbHelper.chkCPIPackage(adModel.getPackage_name())) {
                                        if (adModel.getTask().equals("2") || adModel.getTask().equals("3")) {
                                            ((OfferViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.run_chk));
                                        } else {
                                            ((OfferViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.confirm_chk));
                                        }
                                        ((OfferViewHolder) holder).iv_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_now1));
                                    }
                                }
                                if (adModel.getIsAction().equals("N")) {
                                    ((OfferViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                    ((OfferViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                } else {
                                    ((OfferViewHolder) holder).layer_gold.setVisibility(View.VISIBLE);
                                    ((OfferViewHolder) holder).layer_trophy.setVisibility(View.VISIBLE);
                                }
                                try {
                                    double reward_gold = Double.parseDouble(adModel.getCash());
                                    if (reward_gold > 0) {
                                        ((OfferViewHolder) holder).layer_gold.setVisibility(View.VISIBLE);
                                        ((OfferViewHolder) holder).tv_gold.setText(CommonUtil.setComma(reward_gold + "", true, false));
                                    } else {
                                        ((OfferViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                    }
                                } catch (Exception ignore) {
                                    ((OfferViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                }
                                try {
                                    int reward_trophy = Integer.parseInt(adModel.getCoin());
                                    if (reward_trophy > 0) {
                                        ((OfferViewHolder) holder).layer_trophy.setVisibility(View.VISIBLE);
                                        ((OfferViewHolder) holder).tv_trophy.setText(CommonUtil.setComma(reward_trophy + "", false, false));
                                    } else {
                                        ((OfferViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                    }
                                } catch (Exception ignore) {
                                    ((OfferViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                }
                                if (adModel.getIsAction().equals("N")) {
                                    ((OfferViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                    ((OfferViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                }
                            }

                            ((OfferViewHolder) holder).tv_offer.setText(adModel.getName());
                            ((OfferViewHolder) holder).tv_label.setVisibility(View.GONE);
                            break;
                        case CommonUtil.AD_TYPE_OFFER:
                            ((OfferViewHolder) holder).layer_gold.setVisibility(View.VISIBLE);
                            ((OfferViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                            ((OfferViewHolder) holder).layer_trophy_plus.setVisibility(View.VISIBLE);
                            ((OfferViewHolder) holder).layer_icon.setVisibility(View.GONE);
                            int thumbDrawble = R.drawable.ic_launcher;
                            if (position % 9 == 0) {
                                thumbDrawble = R.drawable.icon_ad1;
                            } else if (position % 9 == 1) {
                                thumbDrawble = R.drawable.icon_ad2;
                            } else if (position % 9 == 2) {
                                thumbDrawble = R.drawable.icon_ad3;
                            } else if (position % 9 == 3) {
                                thumbDrawble = R.drawable.icon_ad5;
                            } else if (position % 9 == 4) {
                                thumbDrawble = R.drawable.icon_ad6;
                            } else if (position % 9 == 5) {
                                thumbDrawble = R.drawable.icon_ad7;
                            } else if (position % 9 == 6) {
                                thumbDrawble = R.drawable.icon_ad8;
                            } else if (position % 9 == 7) {
                                thumbDrawble = R.drawable.icon_ad9;
                            } else if (position % 9 == 8) {
                                thumbDrawble = R.drawable.icon_ad10;
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                ((OfferViewHolder) holder).layer_type.setBackground(ContextCompat.getDrawable(context, R.drawable.offer_round_btn));
                            } else {
                                ((OfferViewHolder) holder).layer_type.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.offer_round_btn));
                            }
                            Drawable d = ContextCompat.getDrawable(context, thumbDrawble);
                            try {
                                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                                Drawable newD = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
                                ((OfferViewHolder) holder).iv_offer.setImageDrawable(newD);
                            } catch (Exception ignore) {
                                ((OfferViewHolder) holder).iv_offer.setImageDrawable(d);
                            }
                            ((OfferViewHolder) holder).tv_offer.setText(adModel.getName());
                            if (adModel.getCash() != null && !adModel.getCash().equals("")) {
                                ((OfferViewHolder) holder).tv_gold.setText(CommonUtil.setComma(adModel.getCash(), true, false)+"~");
                            } else {
                                ((OfferViewHolder) holder).tv_gold.setText("");
                            }
                            ((OfferViewHolder) holder).tv_label.setVisibility(View.GONE);
                            if( adModel.getLabelEnable().equals("Y")){
                                ((OfferViewHolder) holder).tv_label.setVisibility(View.VISIBLE);
                                GradientDrawable gd = new GradientDrawable();
                                try {
                                    gd.setColor(Color.parseColor(adModel.getBackColor()));
                                }catch (Exception ignore){
                                    ignore.printStackTrace();
                                }
                                gd.setCornerRadius(15);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    ((OfferViewHolder) holder).tv_label.setBackground(gd);
                                } else {
                                    ((OfferViewHolder) holder).tv_label.setBackgroundDrawable(gd);
                                }
                                ((OfferViewHolder) holder).tv_label.setText(adModel.getLabel());
                                try {
                                    ((OfferViewHolder) holder).tv_label.setTextColor(Color.parseColor(adModel.getTextColor()));
                                }catch (Exception ignore){
                                    ignore.printStackTrace();
                                }
                            }
                            break;
                    }
                }else if( getItemViewType(position) == VIEW_WIDE){
                    ((AdViewHolder) holder).tv_ad.setText(adModel.getName());
                    ((AdViewHolder) holder).root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    switch (adModel.getViewType()) {
                        case CommonUtil.AD_TYPE_ADNETWORK:
                            Picasso.with(context).load(adModel.getImage()).error(R.drawable.df_event).into(((AdViewHolder) holder).iv_ad);
                            Picasso.with(context).load(adModel.getImage()).error(R.drawable.df_event).into(((AdViewHolder) holder).iv_d_ad);
                            ((AdViewHolder) holder).layer_trophy.setVisibility(View.VISIBLE);
                            ((AdViewHolder) holder).layer_icon.setVisibility(View.VISIBLE);
                            ((AdViewHolder) holder).iv_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_down1));
                            ((AdViewHolder) holder).tv_icon.setText(adModel.getTask());
                            ((AdViewHolder) holder).tv_gold.setText(CommonUtil.setComma(adModel.getCash(), true, false));
                            ((AdViewHolder) holder).tv_gold.setVisibility(View.VISIBLE);
                            break;
                        case CommonUtil.AD_TYPE_TCASH:
                            Picasso.with(context).load(adModel.getImage()).error(R.drawable.df_event).into(((AdViewHolder) holder).iv_ad);
                            Picasso.with(context).load(adModel.getImage()).error(R.drawable.df_event).into(((AdViewHolder) holder).iv_d_ad);
                            ((AdViewHolder) holder).layer_trophy_plus.setVisibility(View.GONE);
                            ((AdViewHolder) holder).layer_icon.setVisibility(View.VISIBLE);
                            int iconDrawble = R.drawable.icon_down1;
                            switch (adModel.getTask()) {
                                case "1":
                                case "2":
                                case "3":
                                    iconDrawble = R.drawable.icon_down1;

                                    switch (adModel.getTask()) {
                                        case "1":
                                            ((AdViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.install));
                                            break;
                                        case "2":
                                            ((AdViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.install_run));
                                            break;
                                        case "3":
                                            ((AdViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.install_action));
                                            break;
                                    }
                                    break;
                                case "4":
                                    //visit
                                    iconDrawble = R.drawable.icon_internet1;
                                    ((AdViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.visit));
                                    break;
                                case "5":
                                case "6":
                                case "7":
                                    //like
                                    iconDrawble = R.drawable.icon_like1;
                                    ((AdViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.facebook_like));
                                    break;
                            }
                            ((AdViewHolder) holder).iv_icon.setImageDrawable(ContextCompat.getDrawable(context, iconDrawble));
                            if (adModel.getIsRun().equals("Y")) {
                                if (adModel.getRunStep().equals("-1")) {
                                    if (adModel.getActionType().equals("1")) {
                                        //cpi
                                        if (!Applications.dbHelper.chkCPIPackage(adModel.getPackage_name())) {
                                            if (adModel.getTask().equals("2") || adModel.getTask().equals("3")) {
                                                ((AdViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.run_chk));
                                            } else {
                                                ((AdViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.confirm_chk));
                                            }
                                            ((AdViewHolder) holder).iv_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_now1));
                                        }
                                    }
                                    if (adModel.getIsAction().equals("N")) {
                                        ((AdViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                        ((AdViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                    } else {
                                        ((AdViewHolder) holder).layer_gold.setVisibility(View.VISIBLE);
                                        ((AdViewHolder) holder).layer_trophy.setVisibility(View.VISIBLE);
                                    }
                                    try {
                                        double reward_gold = Double.parseDouble(adModel.getCash());
                                        if (reward_gold > 0) {
                                            ((AdViewHolder) holder).layer_gold.setVisibility(View.VISIBLE);
                                            ((AdViewHolder) holder).tv_gold.setText(CommonUtil.setComma(reward_gold + "", true, false));
                                        } else {
                                            ((AdViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                        }
                                    } catch (Exception ignore) {
                                        ((AdViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                    }
                                    try {
                                        int reward_trophy = Integer.parseInt(adModel.getCoin());
                                        if (reward_trophy > 0) {
                                            ((AdViewHolder) holder).layer_trophy.setVisibility(View.VISIBLE);
                                            ((AdViewHolder) holder).tv_trophy.setText(CommonUtil.setComma(reward_trophy + "", false, false));
                                        } else {
                                            ((AdViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                        }
                                    } catch (Exception ignore) {
                                        ((AdViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                    }
                                    if (adModel.getIsAction().equals("N")) {
                                        ((AdViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                        ((AdViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                    }
                                } else {
                                    ((AdViewHolder) holder).tv_icon.setText(context.getResources().getString(R.string.every_day_run_1) + "(" + adModel.getRunStep() + "/" + adModel.getRunCnt() + ")");
                                    if (adModel.getRunToday().equals("Y")) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            ((AdViewHolder) holder).tv_icon.setText(Html.fromHtml("<s>" + context.getResources().getString(R.string.every_day_run_1) + "(" + adModel.getRunStep() + "/" + adModel.getRunCnt() + ")" + "</s>", Html.FROM_HTML_MODE_LEGACY));
                                        } else {
                                            ((AdViewHolder) holder).tv_icon.setText(Html.fromHtml("<s>" + context.getResources().getString(R.string.every_day_run_1) + "(" + adModel.getRunStep() + "/" + adModel.getRunCnt() + ")" + "</s>"));
                                        }
                                    } else {
                                        ((AdViewHolder) holder).tv_icon.setText(context.getResources().getString(R.string.every_day_run_1) + "(" + adModel.getRunStep() + "/" + adModel.getRunCnt() + ")");
                                    }
                                    ((AdViewHolder) holder).tv_icon.setSelected(true);
                                    ((AdViewHolder) holder).iv_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_run1));
                                    try {
                                        int run_reward_gold = Integer.parseInt(adModel.getRunReward());
                                        if (run_reward_gold > 0) {
                                            ((AdViewHolder) holder).layer_gold.setVisibility(View.VISIBLE);
                                            ((AdViewHolder) holder).tv_gold.setText(CommonUtil.setComma(run_reward_gold + "", true, false) + "/" + context.getResources().getString(R.string.time));
                                        } else {
                                            ((AdViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                        }
                                    } catch (Exception ignore) {
                                        ((AdViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                    }
                                    try {
                                        int run_reward_coin1 = Integer.parseInt(adModel.getRunCoin());
                                        if (run_reward_coin1 > 0) {
                                            ((AdViewHolder) holder).layer_trophy.setVisibility(View.VISIBLE);
                                            ((AdViewHolder) holder).tv_trophy.setText(CommonUtil.setComma(run_reward_coin1 + "", false, false) + "/" + context.getResources().getString(R.string.time));
                                        } else {
                                            ((AdViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                        }
                                    } catch (Exception ignore) {
                                        ((AdViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                if (adModel.getActionType().equals("1")) {
                                    //cpi
                                    if (!Applications.dbHelper.chkCPIPackage(adModel.getPackage_name())) {
                                        if (adModel.getTask().equals("2") || adModel.getTask().equals("3")) {
                                            ((AdViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.run_chk));
                                        } else {
                                            ((AdViewHolder) holder).tv_icon.setText(context.getResources().getText(R.string.confirm_chk));
                                        }
                                        ((AdViewHolder) holder).iv_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_now1));
                                    }
                                }
                                if (adModel.getIsAction().equals("N")) {
                                    ((AdViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                    ((AdViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                } else {
                                    ((AdViewHolder) holder).layer_gold.setVisibility(View.VISIBLE);
                                    ((AdViewHolder) holder).layer_trophy.setVisibility(View.VISIBLE);
                                }
                                try {
                                    double reward_gold = Double.parseDouble(adModel.getCash());
                                    if (reward_gold > 0) {
                                        ((AdViewHolder) holder).layer_gold.setVisibility(View.VISIBLE);
                                        ((AdViewHolder) holder).tv_gold.setText(CommonUtil.setComma(reward_gold + "", true, false));
                                    } else {
                                        ((AdViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                    }
                                } catch (Exception ignore) {
                                    ((AdViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                }
                                try {
                                    int reward_trophy = Integer.parseInt(adModel.getCoin());
                                    if (reward_trophy > 0) {
                                        ((AdViewHolder) holder).layer_trophy.setVisibility(View.VISIBLE);
                                        ((AdViewHolder) holder).tv_trophy.setText(CommonUtil.setComma(reward_trophy + "", false, false));
                                    } else {
                                        ((AdViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                    }
                                } catch (Exception ignore) {
                                    ((AdViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                }
                                if (adModel.getIsAction().equals("N")) {
                                    ((AdViewHolder) holder).layer_gold.setVisibility(View.GONE);
                                    ((AdViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                }
                            }

                            ((AdViewHolder) holder).tv_ad.setText(adModel.getName());
                            if (adModel.getCash() != null && !adModel.getCash().equals("")) {
                                ((AdViewHolder) holder).tv_gold.setText(CommonUtil.setComma(adModel.getCash(), true, false));
                            } else {
                                ((AdViewHolder) holder).tv_gold.setText("");
                            }
                            /*
                            if( adModel.getCoin() != null && !adModel.getCoin().equals("")){
                                ((AdViewHolder) holder).layer_trophy.setVisibility(View.VISIBLE);
                            }else{
                                ((AdViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                            }
                            */
                            try {
                                int reward_trophy = Integer.parseInt(adModel.getCoin());
                                if (reward_trophy > 0) {
                                    ((AdViewHolder) holder).layer_trophy.setVisibility(View.VISIBLE);
                                    ((AdViewHolder) holder).tv_trophy.setText(CommonUtil.setComma(reward_trophy + "", false, false));
                                } else {
                                    ((AdViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                                }
                            } catch (Exception ignore) {
                                ((AdViewHolder) holder).layer_trophy.setVisibility(View.GONE);
                            }
                            break;
                    }
                }
            }
        }else if( objItem instanceof NativeAdItem){
            final NativeAdItem nativeAdItem = (NativeAdItem) objItem;
            //holder.tv_ad.setText(nativeAdItem.getTitle());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ((OfferViewHolder) holder).layer_type.setBackground(ContextCompat.getDrawable(context, R.drawable.offer_round_btn));
            } else {
                ((OfferViewHolder) holder).layer_type.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.offer_round_btn));
            }
            Picasso.with(context).load(nativeAdItem.getIconUrl()).error(R.drawable.logo2).into(((OfferViewHolder) holder).iv_offer);
            ((OfferViewHolder) holder).root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            ((OfferViewHolder) holder).tv_offer.setText(nativeAdItem.getTitle());
            ((OfferViewHolder) holder).tv_offer.setSelected(true);
            ((OfferViewHolder) holder).layer_icon.setVisibility(View.GONE);
            ((OfferViewHolder) holder).layer_gold.setVisibility(View.VISIBLE);
            ((OfferViewHolder) holder).layer_trophy.setVisibility(View.GONE);
            ((OfferViewHolder) holder).layer_trophy_plus.setVisibility(View.VISIBLE);
            //((OfferViewHolder) holder).iv_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_down1));
            //((OfferViewHolder) holder).tv_icon.setText(nativeAdItem.getActionText());
            ((OfferViewHolder) holder).tv_gold.setVisibility(View.VISIBLE);
            ((OfferViewHolder) holder).tv_gold.setText(CommonUtil.setComma(Long.toString(nativeAdItem.getRewardPoint()), true, false));
            ((OfferViewHolder) holder).root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            ((OfferViewHolder) holder).tv_label.setVisibility(View.GONE);
            //}else if( getItem(position) instanceof NativeExpressAdView){
       /* ADMOB }else if( objItem instanceof AdView){

            AdView adView = (AdView) objItem;
            if( ((AdmobViewHolder) holder).type_admob.getChildCount() == 0) {
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }
                ((AdmobViewHolder) holder).type_admob.addView(adView);
            }
            Log.e("admob","adView.resume");*/
        }else if( objItem instanceof TitleModel){
            if( Applications.preference.getValue(Preference.TROPHY_VIEW, true)){
                Applications.preference.put(Preference.TROPHY_VIEW, false);
                ((TitleViewHolder) holder).title_back.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
                ((TitleViewHolder) holder).tv_title.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                ((TitleViewHolder) holder).tv_title1.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                int bcolorFrom = ContextCompat.getColor(context, R.color.black);
                int bcolorTo = ContextCompat.getColor(context, android.R.color.transparent);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), bcolorFrom, bcolorTo);
                colorAnimation.setDuration(2000);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ((TitleViewHolder) holder).title_back.setBackgroundColor((int) valueAnimator.getAnimatedValue());
                    }
                });
                int tcolorFrom = ContextCompat.getColor(context, android.R.color.white);
                int tcolorTo = ContextCompat.getColor(context, R.color.text_default);
                ValueAnimator tcolorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), tcolorFrom, tcolorTo);
                tcolorAnimation.setDuration(2400);
                tcolorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ((TitleViewHolder) holder).tv_title.setTextColor((int) valueAnimator.getAnimatedValue());
                        ((TitleViewHolder) holder).tv_title1.setTextColor((int) valueAnimator.getAnimatedValue());
                    }
                });
                colorAnimation.start();
                tcolorAnimation.start();
            }
        }
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    @Override
    public int getItemViewType(final int position){
        if( getItem(position) instanceof NativeAdItem){
            return VIEW_ICON;
            //}else if( getItem(position) instanceof NativeExpressAdView){
        /* ADMOB }else if( getItem(position) instanceof AdView){
            return VIEW_ADMOB;*/
        }else if( getItem(position) instanceof TitleModel){
            return VIEW_TITLE;
        }else if( getItem(position) instanceof LastModel){
            return VIEW_LAST;
        }else if( getItem(position) instanceof AdModel){
            if( ((AdModel)getItem(position)).getViewType().equals(CommonUtil.AD_TYPE_TCASH)) {
                if (((AdModel)getItem(position)).getAdType().equals("1")) {
                    return VIEW_ICON;
                } else {
                    return VIEW_WIDE;
                }
            }else{
                return VIEW_ICON;
            }
        }else{
            return VIEW_WIDE;
        }
    }

    public Object getItem(final int position) {
        try {
            return adList.get(position);
        }catch (Exception ignore){
            return null;
        }
    }

    class TitleViewHolder extends RecyclerView.ViewHolder{

        protected LinearLayout title_back;
        protected TextView tv_title;
        protected TextView tv_title1;
        public TitleViewHolder(View itemView) {
            super(itemView);
            this.title_back = (LinearLayout)itemView.findViewById(R.id.title_back);
            this.tv_title = (TextView)itemView.findViewById(R.id.tv_title);
            this.tv_title1 = (TextView)itemView.findViewById(R.id.tv_title1);
            if( Applications.preference.getValue(Preference.TROPHY_VIEW, true)){
                title_back.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
                tv_title.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                tv_title1.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            }else{
                title_back.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                tv_title.setTextColor(ContextCompat.getColor(context, R.color.text_default));
                tv_title1.setTextColor(ContextCompat.getColor(context, R.color.text_default));
            }
        }
    }
    class AdmobViewHolder extends RecyclerView.ViewHolder{

        protected LinearLayout type_admob;
        public AdmobViewHolder(View itemView) {
            super(itemView);
            this.type_admob = (LinearLayout)itemView.findViewById(R.id.type_admob);
        }
    }
    class OfferViewHolder extends RecyclerView.ViewHolder{
        protected LinearLayout root;
        protected LinearLayout layer_type;
        protected TextView tv_label;
        protected ImageView iv_offer;
        protected TextView tv_offer;
        protected LinearLayout layer_icon;
        protected ImageView iv_icon;
        protected TextView tv_icon;
        protected LinearLayout layer_gold;
        protected TextView tv_gold;
        protected LinearLayout layer_trophy_plus;
        protected LinearLayout layer_trophy;
        protected TextView tv_trophy;
        public OfferViewHolder(View itemView) {
            super(itemView);
            this.root = (LinearLayout)itemView.findViewById(R.id.root);
            this.layer_type = (LinearLayout)itemView.findViewById(R.id.layer_type);
            this.tv_label = (TextView)itemView.findViewById(R.id.tv_label);
            this.iv_offer = (ImageView)itemView.findViewById(R.id.iv_offer);
            this.tv_offer = (TextView)itemView.findViewById(R.id.tv_offer);
            this.layer_icon = (LinearLayout)itemView.findViewById(R.id.layer_icon);
            this.iv_icon = (ImageView)itemView.findViewById(R.id.iv_icon);
            this.tv_icon = (TextView)itemView.findViewById(R.id.tv_icon);
            this.layer_gold = (LinearLayout)itemView.findViewById(R.id.layer_gold);
            this.tv_gold = (TextView)itemView.findViewById(R.id.tv_gold);
            this.layer_trophy_plus = (LinearLayout)itemView.findViewById(R.id.layer_trophy_plus);
            this.layer_trophy = (LinearLayout)itemView.findViewById(R.id.layer_trophy);
            this.tv_trophy = (TextView)itemView.findViewById(R.id.tv_trophy);
        }


    }
    class AdViewHolder extends RecyclerView.ViewHolder{
        protected RelativeLayout root;
        protected ImageView iv_ad;
        protected ImageView iv_d_ad;
        protected TextView tv_ad;
        protected LinearLayout layer_icon;
        protected ImageView iv_icon;
        protected TextView tv_icon;
        protected LinearLayout layer_gold;
        protected TextView tv_gold;
        protected LinearLayout layer_trophy_plus;
        protected LinearLayout layer_trophy;
        protected TextView tv_trophy;
        public AdViewHolder(View itemView) {
            super(itemView);
            this.root = (RelativeLayout)itemView.findViewById(R.id.root);
            this.iv_ad = (ImageView)itemView.findViewById(R.id.iv_ad);
            this.iv_d_ad = (ImageView)itemView.findViewById(R.id.iv_d_ad);
            this.tv_ad = (TextView)itemView.findViewById(R.id.tv_ad);
            this.layer_icon = (LinearLayout)itemView.findViewById(R.id.layer_icon);
            this.iv_icon = (ImageView)itemView.findViewById(R.id.iv_icon);
            this.tv_icon = (TextView)itemView.findViewById(R.id.tv_icon);
            this.layer_gold = (LinearLayout)itemView.findViewById(R.id.layer_gold);
            this.tv_gold = (TextView)itemView.findViewById(R.id.tv_gold);
            this.layer_trophy_plus = (LinearLayout)itemView.findViewById(R.id.layer_trophy_plus);
            this.layer_trophy = (LinearLayout)itemView.findViewById(R.id.layer_trophy);
            this.tv_trophy = (TextView)itemView.findViewById(R.id.tv_trophy);
        }
    }
    class LastViewHolder extends RecyclerView.ViewHolder{
        public LastViewHolder(View itemView) {
            super(itemView);
        }
    }

}
