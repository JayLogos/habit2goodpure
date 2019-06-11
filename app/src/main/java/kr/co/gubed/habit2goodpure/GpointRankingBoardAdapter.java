package kr.co.gubed.habit2goodpure;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.gubed.habit2goodpure.gpoint.util.Applications;
import kr.co.gubed.habit2goodpure.gpoint.util.CommonUtil;
import kr.co.gubed.habit2goodpure.gpoint.util.Preference;

public class GpointRankingBoardAdapter extends RecyclerView.Adapter<GpointRankingBoardAdapter.ViewHolder> {
        //implements ItemTouchHelperAdapter{
    private final ArrayList<JSONObject> gpointRankingList;
    private ViewGroup parent;
    private final Context context;

    static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperIndicator {
        private final TextView mTvRanking;
        private final TextView mTvUserId;
        private final TextView mTvGpoint;
        private final TextView mTvTrophy;
        private final LinearLayout mLlItemView;

        ViewHolder(View itemView) {
            super(itemView);

            mTvRanking = itemView.findViewById(R.id.tv_ranking);
            mTvUserId = itemView.findViewById(R.id.tv_userid);
            mTvGpoint = itemView.findViewById(R.id.tv_gpoint);
            mTvTrophy = itemView.findViewById(R.id.tv_trophy);
            mLlItemView = itemView.findViewById(R.id.ll_item_view);
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {

        }
    }

    public GpointRankingBoardAdapter(Context context, ArrayList<JSONObject> gpointRankingList) {
        this.context = context;
        this.gpointRankingList = gpointRankingList;
    }

    @NonNull
    @Override
    public GpointRankingBoardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_board_item_view, parent, false);
        this.parent = parent;

        return new ViewHolder(v);
    }

    /*
     * 이 코드가 없으면 recycler view 가 정상적으로 동작하지 않음. 특히 스크롤!
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public void onBindViewHolder(@NonNull final GpointRankingBoardAdapter.ViewHolder holder, int position) {
        final JSONObject gpointRankingItem = gpointRankingList.get(position);

        try {
            Integer mRanking = gpointRankingItem.getInt("r");
            String mCpId = gpointRankingItem.getString("c");
            Integer mGpoint = gpointRankingItem.getInt("g");
            Integer mTrophy = gpointRankingItem.getInt("t");
            Log.i(getClass().getName(), mRanking+", "+mCpId+", "+mGpoint+", "+mTrophy);

            holder.mTvRanking.setText(CommonUtil.setComma(mRanking.toString(), true, false));
            holder.mTvUserId.setText(mCpId);
            holder.mTvGpoint.setText(CommonUtil.setComma(mGpoint.toString(), true, false));
            holder.mTvTrophy.setText(CommonUtil.setComma(mTrophy.toString(), true, false));

            if (mCpId.equals(Applications.preference.getValue(Preference.CPID, ""))) {
                holder.mLlItemView.setBackgroundResource(R.color.reply_orange_500);
            } else {
                holder.mLlItemView.setBackgroundResource(R.color.md_white_1000);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return this.gpointRankingList.size();
    }

/*
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
    }
*/
}
