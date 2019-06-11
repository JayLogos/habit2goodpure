package kr.co.gubed.habit2goodpure.gpoint.listener;

public interface AsyncTaskCompleteListener<T> {
	void onTaskComplete(T result);
	void onTaskError(String param, String action, String result);
}
