package kr.co.gubed.habit2goodpure.gpoint.util;

import android.os.AsyncTask;
import android.util.Log;

import kr.co.gubed.habit2goodpure.gpoint.listener.AsyncTaskCompleteListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class AsyncHTTPPost extends AsyncTask<String, Void, String>{

	private AsyncTaskCompleteListener<String> callback;
	private final String TAG = AsyncHTTPPost.class.toString();

	private String REQUEST_URL;
//	private static HashMap<String,String>  cache = new HashMap<>();
	private String action;
	private String param;
	public AsyncHTTPPost(AsyncTaskCompleteListener<String> callback){
		this.callback = callback;
	}
	@Override
	protected String doInBackground(String... strings){
		param = strings[1];
		action = strings[2];
		Log.e("action : ",""+action);
		Log.e("param : ",""+param);
//        if( !action.equals(CommonUtil.ACTION_POP_LINKED) && Applications.isCashpopPopup) {
//            while (Applications.isCashpopPopup) {
//                if (!Applications.isCashpopPopup) {
//                    break;
//                }
//            }
//        }
		BufferedInputStream bis = null;
		String contentAsString = "";
		HttpURLConnection conn = null;
//		int retry = 0;
//		while(retry < 2) {
		REQUEST_URL = strings[0];
		URL url = null;
		try {
			url = new URL(REQUEST_URL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("error","1");
			try{
				conn = (HttpURLConnection) url.openConnection();
			}catch (Exception ex){
				ex.printStackTrace();
				Log.e("error","1");
				return null;
			}
		}
		if( conn != null) {
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(30000);
			try {
				conn.setRequestMethod("POST");
			} catch (ProtocolException e) {
				e.printStackTrace();
				Log.e("error","2");
				return null;
			}
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					conn.addRequestProperty("Connection", "keep-alive");
					conn.setDoInput(true);
					conn.setDoOutput(true);
			OutputStream out_stream = null;
			try{
				out_stream = conn.getOutputStream();
			}catch (Exception ignore){
				ignore.printStackTrace();
				Log.e("error","2-1");
				return null;
			}
			try {
				out_stream.write(strings[1].getBytes("UTF-8"));
				out_stream.flush();
				out_stream.close();
				conn.connect();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("error","3");
				return "";
			}
			int response = 0;
			try {
				response = conn.getResponseCode();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("error", "4");
				return "";
			}
			if( response == HttpURLConnection.HTTP_OK) {
				try {
					bis = new BufferedInputStream(conn.getInputStream());
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("error","5");
					return "";
				}
				byte[] buf = new byte[4096];
				int len;
				try {
					while ((len = bis.read(buf, 0, 4096)) > 0) {
                        contentAsString += (new String(buf, 0, len));
                    }
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("error","6");
					return "";
				}
				len = contentAsString.length();
				if( len > 0){
					try {
						bis.close();
						conn.disconnect();
					} catch (IOException ignored) {}
				}
			}
		}else{
			return null;
		}
		if( bis != null){
			try {
				bis.close();
			}catch (IOException ignored){}
		}
		if( conn != null){
			try {
				conn.disconnect();
			}catch (Exception ignored){}
		}
//			retry++;
//		}
//		if(contentAsString.length() < 2) {
//			contentAsString = cache.get(strings[1] + strings[0]);
//		}
		return contentAsString;
	}

	@Override
	protected void onPostExecute(String result){
		Log.e(TAG,"onPostExecute : "+action);
		try{
//			if( isCancelled()){
//				result = "";
//			}
			Log.e("result", ""+result);
			if( result == null || result.equals("")){
				Log.e("result", ""+result);
				callback.onTaskError(param, action, result);
			}else{
				callback.onTaskComplete(result);
			}

		}catch (Exception ignore){
			ignore.printStackTrace();
		}
	}
}