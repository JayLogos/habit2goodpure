package kr.co.gubed.habit2good;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import kr.co.gubed.habit2good.gpoint.util.CommonUtil;

public class HabitMemoAdapter extends RecyclerView.Adapter<HabitMemoAdapter.ViewHolder> implements ItemTouchHelperAdapter{
    private final ArrayList<HashMap<String, String>> habitMemoList;
    private final HabitDbAdapter dbAdapter;
    private ViewGroup parent;
    private final Context context;

    private final int RESULT_DONE=0;
    private final int RESULT_FAIL=1;
    private final int RESULT_SKIP=2;

    static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperIndicator {
        private LinearLayout mMemoLayout;
        private final TextView mTvDate;
        private final TextView mTvMemo;
        private Integer habitid;
        private String mSelectedDay;
        private String mMemo;

        ViewHolder(View itemView) {
            super(itemView);

            mMemoLayout = itemView.findViewById(R.id.memo_item);
            mTvDate = itemView.findViewById(R.id.tv_date);
            mTvMemo = itemView.findViewById(R.id.tv_memo);
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {

        }
    }

    public HabitMemoAdapter(Context context, ArrayList<HashMap<String, String>> habitMemoList, HabitDbAdapter dbAdapter) {
        this.context = context;
        this.habitMemoList = habitMemoList;
        this.dbAdapter = dbAdapter;
    }

    @NonNull
    @Override
    public HabitMemoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_item_view, parent, false);
        this.parent = parent;

        return new ViewHolder(v);
    }

    /*
     * 이 코드가 없으면 recycler view 가 정상적으로 동작하지 않음. 특히 스크롤!
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(@NonNull final HabitMemoAdapter.ViewHolder holder, int position) {
        final HashMap<String, String> habitMemoItem = habitMemoList.get(position);
        int result;

        holder.habitid = Integer.parseInt(Objects.requireNonNull(habitMemoItem.get("habitid")));
        holder.mSelectedDay = habitMemoItem.get("selectedday");
        holder.mMemo = habitMemoItem.get("memo");

        result = dbAdapter.getExecResult(holder.habitid, holder.mSelectedDay);
        Log.i(getClass().getName(), "position="+position+" mSelectedDay="+holder.mSelectedDay+" memo="+holder.mMemo);

        holder.mTvDate.setText(holder.mSelectedDay);
        switch (result) {
            case RESULT_DONE:
                holder.mTvDate.setBackgroundResource(R.drawable.rectangle_green_solid_background);
                holder.mTvDate.setTextColor(Color.WHITE);
                break;
            case RESULT_FAIL:
                holder.mTvDate.setBackgroundResource(R.drawable.rectangle_red_solid_background);
                holder.mTvDate.setTextColor(Color.WHITE);
                break;
            case RESULT_SKIP:
                holder.mTvDate.setBackgroundResource(R.drawable.rectangle_blue_solid_background);
                holder.mTvDate.setTextColor(Color.WHITE);
                break;
            default:
                holder.mTvDate.setBackgroundResource(R.drawable.rectangle_gray_solid_background);
                holder.mTvDate.setTextColor(Color.WHITE);
                break;
        }

        holder.mTvMemo.setText(holder.mMemo);

        holder.mMemoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*holder.mTvMemo.setSelected(true);
                * text를 scroll하는 것에서 edit text로 변경
                * */
                Intent intent = new Intent(context, EditMemoActivity.class);

                intent.putExtra("habitid", holder.habitid);
                intent.putExtra("date", holder.mSelectedDay);
                intent.putExtra("memo", holder.mMemo);

                ((HabitMemoActivity)context).startActivityForResult(intent, CommonUtil.REQUEST_CODE_UPDATE_MEMO);
                ((HabitMemoActivity)context).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);


            }
        });
    }

    @Override
    public int getItemCount() {
        return this.habitMemoList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        Integer habitid;
        String selectedDay;

        habitid = Integer.valueOf(Objects.requireNonNull(habitMemoList.get(position).get("habitid")));
        selectedDay = habitMemoList.get(position).get("selectedday");

        dbAdapter.deleteMemo(habitid, selectedDay);
        habitMemoList.remove(position);
        notifyItemRemoved(position);
    }
}
