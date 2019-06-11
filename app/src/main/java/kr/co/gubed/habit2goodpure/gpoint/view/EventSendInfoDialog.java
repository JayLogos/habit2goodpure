package kr.co.gubed.habit2goodpure.gpoint.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import kr.co.gubed.habit2goodpure.gpoint.listener.EventSendInfoListener;
import kr.co.gubed.habit2goodpure.R;


public class EventSendInfoDialog extends Dialog {

    private TextView tv_title;
    private Button btn_back;

    private EditText et_input1;
    private EditText et_input2;
    private EditText et_input3;

    private Button btn_submit;
    private Context context;

    public EventSendInfoDialog(Context context){
        super(context, R.style.CPAlertDialog);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void open(final String title, final EventSendInfoListener listener){
        setContentView(R.layout.dialog_event_info_sender);
        try {
            tv_title = (TextView)findViewById(R.id.tv_title);
            btn_back = (Button)findViewById(R.id.btn_back);

            et_input1 = (EditText)findViewById(R.id.et_input1);

            et_input2 = (EditText)findViewById(R.id.et_input2);

            et_input3 = (EditText)findViewById(R.id.et_input3);

            btn_submit = (Button)findViewById(R.id.btn_submit);

            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        String str_input1 = et_input1.getEditableText().toString();
                        String str_input2 = et_input2.getEditableText().toString();
                        String str_input3 = et_input3.getEditableText().toString();
                        if( str_input1.equals("")) {
                            et_input1.setError(context.getResources().getString(R.string.g_error_h));
                            return;
                        }
                        if( str_input2.equals("")) {
                            et_input2.setError(context.getResources().getString(R.string.g_error_n));
                            return;
                        }
                        if( str_input3.equals("")) {
                            et_input3.setError(context.getResources().getString(R.string.g_error_b));
                            return;
                        }
                        listener.eventSender(str_input1, str_input2, str_input3);
                    }catch (Exception ignore){

                    }
                }
            });

            tv_title.setText(title);
            et_input1.setHint(context.getResources().getString(R.string.p_input_name));
            et_input1.setInputType(InputType.TYPE_CLASS_TEXT);

            et_input2.setHint(context.getResources().getString(R.string.email));
            et_input2.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);

            et_input3.setHint(context.getResources().getString(R.string.phone_number));
            et_input3.setInputType(InputType.TYPE_CLASS_PHONE);

        }catch (Exception ignore){
            ignore.printStackTrace();
        }
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        this.getWindow().setAttributes(params);
        this.show();
    }



}
