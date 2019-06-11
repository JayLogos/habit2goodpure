package kr.co.gubed.habit2goodpure.gpoint.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.gubed.habit2goodpure.gpoint.util.CommonUtil;
import kr.co.gubed.habit2goodpure.R;

public class StoreAdapter extends ArrayAdapter<JSONObject> {

    private LayoutInflater inflater;
    private Context context;
    private int resourceID;
    private ArrayList<JSONObject> objList;
    private HashMap<Integer, JSONObject> template;

    public StoreAdapter(Context context, int resourceID, ArrayList<JSONObject> objList, final HashMap<Integer, JSONObject> template) {
        super(context, resourceID, objList);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.resourceID = resourceID;
        this.objList = objList;
        this.template = template;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = inflater.inflate(resourceID, null);
            holder = new ViewHolder();
            holder.iv_product = (ImageView)convertView.findViewById(R.id.iv_product);
            holder.tv_mileage = (TextView)convertView.findViewById(R.id.tv_mileage);
            holder.tv_product = (TextView)convertView.findViewById(R.id.tv_product);
            holder.tv_gold = (TextView)convertView.findViewById(R.id.tv_gold);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject jobj = getItem(position);
        if( jobj != null){
            try {
                String product = jobj.getString("product");
                double gold = Double.parseDouble(jobj.getString("gold"));
                int linkedBackPer = jobj.getInt("linkedBackPer");
                if( linkedBackPer > 0){
                    holder.tv_mileage.setVisibility(View.VISIBLE);
                    holder.tv_mileage.setText(linkedBackPer +"% "+context.getResources().getString(R.string.mileage));
                }else{
                    holder.tv_mileage.setVisibility(View.GONE);
                }
                String image = jobj.getString("image");
                Picasso.with(context).load(image).placeholder(R.drawable.df_store).error(R.drawable.df_store).into(holder.iv_product);
                holder.tv_product.setText(product);
                holder.tv_gold.setText(CommonUtil.setComma(-gold+"", true, false));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }

    @Override
    public JSONObject getItem(int position) {
        try {
            return objList.get(position);
        }catch (Exception ignore){
            ignore.printStackTrace();
            return null;
        }
    }

    class ViewHolder {
        ImageView iv_product;
        TextView tv_mileage;
        TextView tv_product;
        TextView tv_gold;
    }

}