package kr.co.gubed.habit2goodpure.gpoint.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import kr.co.gubed.habit2goodpure.R;


public class NetworkDialog extends Dialog {

    private Button btn_cancel;
    private View.OnClickListener cancelClickListener;
    private Button btn_ok;
    private View.OnClickListener okClickListener;

    public NetworkDialog(Context context) {
        super(context, R.style.CPAlertDialog);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setCancelClickListener(View.OnClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
    }

    public void setOkClickListener(View.OnClickListener okClickListener) {
        this.okClickListener = okClickListener;
    }

    @Override
    public void show() {
        try {
            setContentView(R.layout.dialog_network);
            btn_cancel = (Button) findViewById(R.id.btn_cancel);
            btn_ok = (Button) findViewById(R.id.btn_ok);
            btn_cancel.setOnClickListener(cancelClickListener);
            btn_ok.setOnClickListener(okClickListener);
            this.setCancelable(false);
            super.show();
        }catch (Exception ignore){}
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}
