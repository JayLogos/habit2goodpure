package kr.co.gubed.habit2good.gpoint.adapter;

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

import kr.co.gubed.habit2good.R;

public class StoreCategoryAdapter extends ArrayAdapter<JSONObject> {

    private LayoutInflater inflater;
    private Context context;
    private int resourceID;
    private ArrayList<JSONObject> objList;

    public StoreCategoryAdapter(Context context, int resourceID, ArrayList<JSONObject> objList) {
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
            holder.iv_category = (ImageView) convertView.findViewById(R.id.iv_category);
            holder.tv_category = (TextView)convertView.findViewById(R.id.tv_category);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject jobj = getItem(position);
        if( jobj != null){
            try {
                String products = jobj.getString("products");
                String category = jobj.getString("category");
                String image = jobj.getString("image");
                Picasso.with(context).load(image).placeholder(R.drawable.df_store).error(R.drawable.df_store).into(holder.iv_category);
                holder.iv_category.setTag(products);
                holder.tv_category.setText(category);
            } catch (Exception e) {
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
        ImageView iv_category;
        TextView tv_category;
    }

}