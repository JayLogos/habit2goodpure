package kr.co.gubed.habit2goodpure.gpoint.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Pattern;

import kr.co.gubed.habit2goodpure.MainActivity;
import kr.co.gubed.habit2goodpure.gpoint.listener.AsyncTaskCompleteListener;
import kr.co.gubed.habit2goodpure.gpoint.util.APICrypto;
import kr.co.gubed.habit2goodpure.gpoint.util.Applications;
import kr.co.gubed.habit2goodpure.gpoint.util.AsyncHTTPPost;
import kr.co.gubed.habit2goodpure.gpoint.util.CommonUtil;
import kr.co.gubed.habit2goodpure.gpoint.util.Preference;
import kr.co.gubed.habit2goodpure.gpoint.view.CashPopDialog;
import kr.co.gubed.habit2goodpure.gpoint.view.LoadingDialog;
import kr.co.gubed.habit2goodpure.gpoint.view.NetworkDialog;
import kr.co.gubed.habit2goodpure.gpoint.view.SupportDialog;
import kr.co.gubed.habit2goodpure.R;

public class SignLoginActivity extends Activity implements View.OnClickListener, AsyncTaskCompleteListener<String> {

    private String TAG = this.getClass().toString();

    private EditText et_cpid;
    private TextView tv_transfer;
    private EditText et_transfer;
    private TextView tv_transfer_check;
    private Button btn_login;
    private Button btn_back;
    private Button btn_info;
    private String requestId;

    private Applications applications;
    private Tracker tracker;

    private CashPopDialog cashPopDialog;
    private LoadingDialog loadingDialog;
    private SupportDialog supportDialog;
    private NetworkDialog networkDialog;

    private String analiticsCategory = "/login";
    private boolean isTransferCode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_login);

        applications = (Applications)this.getApplication();
        tracker = applications.getDefaultTracker();
        tracker.setScreenName(analiticsCategory);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        requestId = "";
        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_info = (Button)findViewById(R.id.btn_info);
        btn_info.setOnClickListener(this);
        et_cpid = (EditText)findViewById(R.id.et_cpid);
        et_cpid.setFilters(new InputFilter[]{CommonUtil.spaceFilter});
        tv_transfer = (TextView)findViewById(R.id.tv_transfer);
        et_transfer = (EditText)findViewById(R.id.et_transfer);
        et_transfer.setFilters(new InputFilter[]{CommonUtil.spaceFilter});
        tv_transfer_check = (TextView)findViewById(R.id.tv_transfer_check);
        et_cpid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if( !requestId.equals(charSequence.toString())){
                    tv_transfer_check.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        et_transfer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                et_transfer.setError(null);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        tv_transfer_check.setVisibility(View.GONE);
        btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        cashPopDialog = new CashPopDialog(this);
        loadingDialog = new LoadingDialog(this);
        supportDialog = new SupportDialog(this);
        networkDialog = new NetworkDialog(this);
    }

    @Override
    public void onBackPressed() {
        try{
            if( loadingDialog != null && loadingDialog.isShowing()){
                loadingDialog.dismiss();
                loadingDialog = null;
            }
            if( cashPopDialog != null && cashPopDialog.isShowing()){
                cashPopDialog.dismiss();
                cashPopDialog = null;
            }
            if( supportDialog != null && supportDialog.isShowing()){
                supportDialog.dismiss();
                supportDialog = null;
            }
            if( networkDialog != null && networkDialog.isShowing()){
                networkDialog.dismiss();
                networkDialog = null;
            }
        }catch (Exception ignore){
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        try{
            if( loadingDialog != null && loadingDialog.isShowing()){
                loadingDialog.dismiss();
                loadingDialog = null;
            }
            if( cashPopDialog != null && cashPopDialog.isShowing()){
                cashPopDialog.dismiss();
                cashPopDialog = null;
            }
            if( supportDialog != null && supportDialog.isShowing()){
                supportDialog.dismiss();
                supportDialog = null;
            }
            if( networkDialog != null && networkDialog.isShowing()){
                networkDialog.dismiss();
                networkDialog = null;
            }
        }catch (Exception ignore){
        }
        super.onDestroy();
    }

    public void loginProccess(){
        if( et_cpid.getText().toString().equals("")){
            et_cpid.setError(getResources().getString(R.string.input_id));
            return;
        }
        if( et_cpid.getText().toString().length() < 4 || !Pattern.matches("^[a-zA-Z0-9]*$", et_cpid.getText().toString())){
            et_cpid.setError(getResources().getString(R.string.error_id));
            return;
        }
        if( isTransferCode && et_transfer.getText().toString().equals("")){
            et_transfer.setError(getResources().getString(R.string.input_transfer_code));
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        requestId = et_cpid.getText().toString();
        map.put(CommonUtil.KEY_EMAIL, requestId);
        map.put(CommonUtil.KEY_TRANSFER_CODE, et_transfer.getText().toString());
        map.put(CommonUtil.KEY_ADID, Applications.preference.getValue(Preference.AD_ID, ""));
        map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_LOGIN);
        String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
        requestAsyncTask(param, CommonUtil.ACTION_LOGIN);
        ShowLoadingProgress();
    }

    @Override
    public void onTaskComplete(String result) {
        if( result == null){
            cashPopDialog.setCpTitle(getResources().getString(R.string.login_error));
            cashPopDialog.setCpDesc(getResources().getString(R.string.login_error_desc));
            cashPopDialog.setCpOkButton(getResources().getString(R.string.ok), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cashPopDialog.dismiss();
                }
            });
            cashPopDialog.show();
            HideLoadingProgress();
        }else{
            String rst;
            try {
                rst = APICrypto.decrypt(CommonUtil.SHARED_KEY, result);
            } catch (Exception e) {
                rst = result;
            }
            try {
                JSONObject jo = new JSONObject(rst);
                String error = jo.getString(CommonUtil.RESULT_ERROR);
                String action = jo.getString(CommonUtil.RESULT_ACTION);
                if( error != null && error.isEmpty() && action != null && !action.isEmpty()){
                    if( action.equals(CommonUtil.ACTION_LOGIN)){
                        String uid = jo.getString(CommonUtil.RESULT_USERID);
                        String cpid = jo.getString(CommonUtil.RESULT_CPID);
                        String iv = jo.getString(CommonUtil.RESULT_INVITE);
                        Applications.preference.put(Preference.USER_ID, uid);
                        Applications.preference.put(Preference.CPID, cpid);
                        Applications.preference.put(Preference.INVITE, iv);
                        GoMain();
                    }
                }else if( error != null && error.equals(CommonUtil.ERROR_INPURT_TRANSFER_CODE)){
                    et_transfer.setHint(getResources().getString(R.string.input_transfer_code));
                    tv_transfer_check.setVisibility(View.VISIBLE);
                    isTransferCode = true;
                }else if( error != null && error.equals(CommonUtil.ERROR_TRANSFER_CODE)){
                    et_transfer.setError(getResources().getString(R.string.invalid_transfer_code));
                }else if( error != null && error.equals(CommonUtil.ERROR_NO_USER)){
                    Toast toast = Toast.makeText(this, getResources().getString(R.string.no_user), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                    toast.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                HideLoadingProgress();
            }
        }
    }

    @Override
    public void onTaskError(String param, String action, String result) {
        HideLoadingProgress();
        try {
            Log.e(TAG, action);
            if( action.equals(CommonUtil.ACTION_LOGIN)){
                showErrorNetwork(param, action);
            }
        }catch (Exception ignore){
            ignore.printStackTrace();
        }
    }

    public void showErrorNetwork(final String param, final String action){
        if( networkDialog == null){
            networkDialog = new NetworkDialog(SignLoginActivity.this);
        }
        if( !networkDialog.isShowing()) {
            networkDialog.setCancelClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HideLoadingProgress();
                    networkDialog.dismiss();
                    ActivityCompat.finishAffinity(SignLoginActivity.this);
                }
            });
            networkDialog.setOkClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowLoadingProgress();
                    requestAsyncTask(param, action);
                    networkDialog.dismiss();
                }
            });
            networkDialog.show();
        }
    }

    public void GoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                loginProccess();
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_info:
                cashPopDialog.setCpTitle(getResources().getString(R.string.login_info));
                cashPopDialog.setCpDesc(getResources().getString(R.string.login_info_desc));
                cashPopDialog.setCpOkButton(getResources().getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cashPopDialog.dismiss();
                    }
                });
                cashPopDialog.setCpCancelButton(getResources().getString(R.string.support), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cashPopDialog.dismiss();
                        CommonUtil.showSupport(SignLoginActivity.this, false);
                    }
                });
                cashPopDialog.show();
                HideLoadingProgress();
                break;
        }
    }

    public void ShowLoadingProgress() {
        //show loading
        try {
            if( loadingDialog == null){
                loadingDialog = new LoadingDialog(this);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    try{
                        loadingDialog.show();
                    }catch (Exception ignore){}
                }
            });
        } catch (Exception ignore) {

        }
    }

    public void HideLoadingProgress() {
        //hide loading
        try {
            loadingDialog.dismiss();
        } catch (Exception ignore) {

        }
    }

    public void requestAsyncTask(String param, String action){
        if( Applications.getCountry(this).equals("KR") && !Applications.isRoaming(this)) {
            new AsyncHTTPPost(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, CommonUtil.SERVER_URL, param, action);
        }else{
            new AsyncHTTPPost(this).execute(CommonUtil.SERVER_URL, param, action);
        }
    }

}
