package kr.co.gubed.habit2goodpure.gpoint.util;

import android.content.Context;
import android.os.AsyncTask;

import kr.co.gubed.habit2goodpure.gpoint.listener.GiftBoxListener;
import kr.co.gubed.habit2goodpure.gpoint.model.GiftBoxModel;

import java.util.ArrayList;

public class GiftBoxAsyncTask extends AsyncTask<String, Void, ArrayList<GiftBoxModel>> {
    private Context context;
    private GiftBoxListener giftBoxListener;
    public GiftBoxAsyncTask(Context context, GiftBoxListener giftBoxListener){
        this.context = context;
        this.giftBoxListener = giftBoxListener;
    }

    @Override
    protected ArrayList<GiftBoxModel> doInBackground(String... strings){
        ArrayList<GiftBoxModel> packageHomeList = Applications.dbHelper.getGiftBoxList();
        return packageHomeList;
    }

    @Override
    protected void onPostExecute(ArrayList<GiftBoxModel> result){
        try{
            if( giftBoxListener != null) {
                giftBoxListener.giftBoxListener(result);
            }
        }catch (Exception ignore){
        }
    }
}
