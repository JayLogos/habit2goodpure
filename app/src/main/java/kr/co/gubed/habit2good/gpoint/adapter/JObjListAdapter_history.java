package kr.co.gubed.habit2good.gpoint.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.gubed.habit2good.R;
import kr.co.gubed.habit2good.gpoint.util.Applications;
import kr.co.gubed.habit2good.gpoint.util.CommonUtil;

public class JObjListAdapter_history extends ArrayAdapter<JSONObject> {

    private LayoutInflater inflater;
    private Context context;
    private int resourceID;
    private ArrayList<JSONObject> objList;

    public JObjListAdapter_history(Context context, int resourceID, ArrayList<JSONObject> objList) {
        super(context, resourceID, objList);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.resourceID = resourceID;
        this.objList = objList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = inflater.inflate(resourceID, null);
            holder = new ViewHolder();
            holder.root = (LinearLayout)convertView.findViewById(R.id.root);

            holder.tv_event = (TextView)convertView.findViewById(R.id.tv_event);
            holder.tv_date = (TextView)convertView.findViewById(R.id.tv_date);
            holder.tv_comment = (TextView)convertView.findViewById(R.id.tv_comment);
//            holder.tv_action = (TextView)convertView.findViewById(R.id.tv_action);
            holder.tv_cash = (TextView)convertView.findViewById(R.id.tv_cash);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject jobj = getItem(position);
        if( jobj != null){
            try {
                holder.tv_comment.setVisibility(View.GONE);
                if( jobj.getString("n") != null && !jobj.getString("n").toLowerCase().equals("null") && !jobj.getString("n").equals("")){
                    if (jobj.getString("rk").startsWith("CPNM") || jobj.getString("rk").startsWith("JE")) {
                        String partnerInfo = jobj.getString("ak") + " " + jobj.getString("n");
                        holder.tv_event.setText(partnerInfo);
                    } else {
                        holder.tv_event.setText(jobj.getString("n"));
                    }
                }else{
                    if( jobj.getString("d") != null && !jobj.getString("d").toLowerCase().equals("null") && !jobj.getString("d").equals("")) {
                        holder.tv_event.setText(context.getResources().getString(R.string.sponsor, jobj.getString("d")));
                    }
                }
                if( jobj.getString("t") != null && !jobj.getString("t").toLowerCase().equals("null") && !jobj.getString("t").equals("")){
                    if( jobj.getString("c") != null && jobj.getString("c").toLowerCase().equals("purchase")){
                        if( jobj.getString("o") != null && !jobj.getString("o").toLowerCase().equals("null") && jobj.getString("o").equals("5")){
                            if( Applications.getCountry(context).equals("KR")){
                                holder.tv_date.setText(CommonUtil.getDateTimeHistory(jobj.getString("t")));
                            }else{
                                holder.tv_date.setText(CommonUtil.getDateTime(jobj.getString("t")));
                            }
                        }else{
                            if (Applications.getCountry(context).equals("KR")) {
                                holder.tv_date.setText(context.getResources().getString(R.string.none) + " " + CommonUtil.getDateTimeHistory(jobj.getString("t")));
                            } else {
                                holder.tv_date.setText(context.getResources().getString(R.string.none) + " " + CommonUtil.getDateTime(jobj.getString("t")));
                            }
                        }
                    }else{
                        if( Applications.getCountry(context).equals("KR")){
                            holder.tv_date.setText(CommonUtil.getDateTimeHistory(jobj.getString("t")));
                        }else{
                            holder.tv_date.setText(CommonUtil.getDateTime(jobj.getString("t")));
                        }
                    }
                }else{
                    holder.tv_date.setText("");
                }
//                if( jobj.getString("c") != null && !jobj.getString("c").toLowerCase().equals("null") && !jobj.getString("c").equals("")){
//                    holder.tv_action.setText(jobj.getString("c"));
//                }else{
//                    if( jobj.getString("d") != null && !jobj.getString("d").toLowerCase().equals("null") && !jobj.getString("d").equals("")) {
//                        holder.tv_action.setText(jobj.getString("d"));
//                    }
//                }

                if( jobj.getString("c") != null && jobj.getString("c").toLowerCase().equals("purchase")){
                    holder.tv_comment.setVisibility(View.VISIBLE);
                    if( jobj.getString("o") != null && !jobj.getString("o").toLowerCase().equals("null") && jobj.getString("o").equals("1")){
                        if( jobj.getString("q") != null && !jobj.getString("q").toLowerCase().equals("null") && !jobj.getString("q").equals("")){
                            holder.tv_comment.setText(context.getResources().getString(R.string.done)+" "+CommonUtil.getDateTimeHistory(jobj.getString("m")));
                        }else{
                            holder.tv_comment.setText("");
                        }
                    }else{
                        if( jobj.getString("o") != null && !jobj.getString("o").toLowerCase().equals("null") && jobj.getString("o").equals("0") || jobj.getString("o").equals("3")) {
                            holder.tv_comment.setText(context.getResources().getString(R.string.next_month));
                        }else{
                            if( jobj.getString("o") != null && !jobj.getString("o").toLowerCase().equals("null") && jobj.getString("o").equals("4")){
                                holder.tv_comment.setText(context.getResources().getString(R.string.request_deny));
                            }else if( jobj.getString("o") != null && !jobj.getString("o").toLowerCase().equals("null") && jobj.getString("o").equals("2")) {
                                holder.tv_comment.setText(context.getResources().getString(R.string.request_fail));
                            }else if( jobj.getString("o") != null && !jobj.getString("o").toLowerCase().equals("null") && jobj.getString("o").equals("5")) {
                                holder.tv_comment.setText(context.getResources().getString(R.string.request_return));
                            }else{
                                holder.tv_comment.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }

                if( jobj.getString("q") != null && !jobj.getString("q").toLowerCase().equals("null") && !jobj.getString("q").equals("")){
                    if( CommonUtil.isPlus(jobj.getString("q")) && !jobj.getString("o").equals("5")){
                        holder.tv_cash.setText(CommonUtil.setComma(jobj.getString("q"), true, true)+" "+context.getResources().getString(R.string.gpoint));
                        holder.tv_cash.setTextColor(ContextCompat.getColor(context, R.color.text_default));
                    }else{
                        if( jobj.getString("c").toLowerCase().equals("purchase") || jobj.getString("c").toLowerCase().equals("lottery")){
                            holder.tv_cash.setText(CommonUtil.setComma(jobj.getString("ak"), true, true)+" "+context.getResources().getString(R.string.gpoint));
                            holder.tv_cash.setTextColor(ContextCompat.getColor(context, R.color.text_red));
                        }else{
                            holder.tv_cash.setText(CommonUtil.setComma(jobj.getString("q"), true, true)+" "+context.getResources().getString(R.string.gpoint));
                        }
                    }
                }else{
                    holder.tv_cash.setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }

    @Override
    public JSONObject getItem(int position) {
        return objList.get(position);
    }

    class ViewHolder {
        LinearLayout root;
        TextView tv_event;
        TextView tv_date;
        TextView tv_comment;
        //        TextView tv_action;
        TextView tv_cash;
    }

}