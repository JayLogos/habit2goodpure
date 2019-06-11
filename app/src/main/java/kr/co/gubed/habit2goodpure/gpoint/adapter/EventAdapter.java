package kr.co.gubed.habit2goodpure.gpoint.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kr.co.gubed.habit2goodpure.gpoint.activity.EventActivity;
import kr.co.gubed.habit2goodpure.gpoint.model.EventModel;
import kr.co.gubed.habit2goodpure.gpoint.util.CommonUtil;
import kr.co.gubed.habit2goodpure.R;

public class EventAdapter extends ArrayAdapter<EventModel> {

    private LayoutInflater inflater;
    private Context context;
    private int resourceID;
    private ArrayList<EventModel> eventList;

    public EventAdapter(Context context, int resourceID, ArrayList<EventModel> eventList) {
        super(context, resourceID, eventList);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.resourceID = resourceID;
        this.eventList = eventList;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final EventAdapter.ViewHolder holder;
        if(convertView == null) {
            convertView = inflater.inflate(resourceID, null);
            holder = new EventAdapter.ViewHolder();
            holder.type_banner = (LinearLayout)convertView.findViewById(R.id.type_banner);
            holder.iv_lottery = (ImageView)convertView.findViewById(R.id.iv_lottery);
            holder.d_iv_lottery = (ImageView)convertView.findViewById(R.id.d_iv_lottery);
            holder.tv_label = (TextView)convertView.findViewById(R.id.tv_label);
            holder.tv_product = (TextView)convertView.findViewById(R.id.tv_product);
            holder.tv_today = (TextView)convertView.findViewById(R.id.tv_today);
            holder.icon_gold = (ImageView)convertView.findViewById(R.id.icon_gold);
            holder.tv_gold = (TextView)convertView.findViewById(R.id.tv_gold);
            holder.tv_coin = (TextView)convertView.findViewById(R.id.tv_coin);
            convertView.setTag(holder);
        }else{
            holder = (EventAdapter.ViewHolder) convertView.getTag();
        }
        EventModel eventModel = getItem(position);
        if( eventModel != null){
            holder.type_banner.post(new Runnable() {
                @Override
                public void run() {
                }
            });
            try {
                Picasso.with(context).load(eventModel.getImage()).placeholder(R.drawable.df_event).error(R.drawable.df_event).into(holder.iv_lottery);
            }catch (Exception ignore){}
            try {
                Picasso.with(context).load(eventModel.getImage()).placeholder(R.drawable.df_event).error(R.drawable.df_event).into(holder.d_iv_lottery);
            }catch (Exception ignore){}
            holder.tv_label.setVisibility(View.GONE);
            if( eventModel.getLabelEnable().equals("Y")){
                holder.tv_label.setVisibility(View.VISIBLE);
                GradientDrawable gd = new GradientDrawable();
                try {
                    gd.setColor(Color.parseColor(eventModel.getLbackColor()));
                }catch (Exception ignore){
                    ignore.printStackTrace();
                }
                gd.setCornerRadius(15);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.tv_label.setBackground(gd);
                } else {
                    holder.tv_label.setBackgroundDrawable(gd);
                }
                holder.tv_label.setText(eventModel.getLtext());
                try {
                    holder.tv_label.setTextColor(Color.parseColor(eventModel.getLtextColor()));
                }catch (Exception ignore){
                    ignore.printStackTrace();
                }
            }

            holder.tv_product.setText(eventModel.getTitle());
            if( eventModel.getLimitType().equals("2")) {
                holder.tv_today.setText(context.getResources().getString(R.string.lottery_today, eventModel.getProcCnt() + "/" + eventModel.getDayCnt()));

            }else{
                if( eventModel.getExpire() > 0){
                    long currentTime = System.currentTimeMillis()/1000L;
                    long pTime = currentTime - ((EventActivity)context).getRequestTimeStamp()/1000L;
                    long expireTime = eventModel.getExpire();
                    long lastExpireTime = expireTime-pTime;
                    if( lastExpireTime > 0){
                        String msg = "";
                        String hour = "";
                        String min = "";
                        if( Math.floor(lastExpireTime/3600) > 0) {
                            hour = context.getResources().getString(R.string.lottery_hour, (int)Math.floor(lastExpireTime / 3600)+"");
                        }
                        if( Math.floor((lastExpireTime/60)%60) > 0){
                            min = context.getResources().getString(R.string.lottery_min, (int)Math.floor((lastExpireTime/60)%60)+"");
                        }
                        msg = context.getResources().getString(R.string.lottery_msg, hour+min);
                        holder.tv_today.setText(msg);
                    }else{
                        holder.tv_today.setText(context.getResources().getString(R.string.lottery_s));
                    }
                }else{
                    holder.tv_today.setText(context.getResources().getString(R.string.lottery_s));
                }
            }
            if( eventModel.getGold() > 0){
                holder.icon_gold.setVisibility(View.VISIBLE);
                holder.tv_gold.setVisibility(View.VISIBLE);
                holder.tv_gold.setText(CommonUtil.setComma(-eventModel.getGold()+"", true, false));
            }else{
                holder.icon_gold.setVisibility(View.GONE);
                holder.tv_gold.setVisibility(View.GONE);
            }
            if( eventModel.getCoin() > 0){
                holder.tv_coin.setVisibility(View.VISIBLE);
                holder.tv_coin.setText(CommonUtil.setComma(-eventModel.getCoin()+"", false, false));
            }else{
                holder.tv_coin.setVisibility(View.GONE);
            }

        }
        return convertView;
    }

    @Override
    public EventModel getItem(int position) {
        return eventList.get(position);
    }

    class ViewHolder {
        LinearLayout type_banner;
        ImageView iv_lottery;
        ImageView d_iv_lottery;
        TextView tv_label;
        TextView tv_product;
        TextView tv_today;
        ImageView icon_gold;
        TextView tv_gold;
        TextView tv_coin;
    }

}