package kr.co.gubed.habit2goodpure.gpoint.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import kr.co.gubed.habit2goodpure.R;

public class LoadingDialog extends Dialog{
	private Context context;
	private ImageView loadingIv;
	private AnimationDrawable loadingViewAnim;
	
	public LoadingDialog(Context context) {
		super(context, R.style.DialogTheme);
		this.context = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCancelable(false);
        this.init();
	}

	private void init(){
		setContentView(R.layout.dialog_loading);
		loadingIv = (ImageView)findViewById(R.id.loadingIv);
		loadingViewAnim = (AnimationDrawable)loadingIv.getBackground();
	}
	
	@Override
	public void show() {
		try {
			super.show();
			loadingViewAnim.start();
		}catch (Exception ignore){
			ignore.printStackTrace();
		}
	}

	@Override
	public void dismiss() {
		try {
			loadingViewAnim.stop();

		}catch (Exception ignore){

		}
		try {
			super.dismiss();
		}catch (Exception ignore){

		}
	}
	
}
