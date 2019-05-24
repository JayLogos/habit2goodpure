package kr.co.gubed.habit2good.gpoint.listener;

public interface AsyncTaskCompleteListener<T> {
	void onTaskComplete(T result);
	void onTaskError(String param, String action, String result);
}
