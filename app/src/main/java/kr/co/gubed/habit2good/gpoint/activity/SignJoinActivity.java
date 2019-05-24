package kr.co.gubed.habit2good.gpoint.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Pattern;

import kr.co.gubed.habit2good.MainActivity;
import kr.co.gubed.habit2good.gpoint.listener.AsyncTaskCompleteListener;
import kr.co.gubed.habit2good.gpoint.util.APICrypto;
import kr.co.gubed.habit2good.gpoint.util.Applications;
import kr.co.gubed.habit2good.gpoint.util.AsyncHTTPPost;
import kr.co.gubed.habit2good.gpoint.util.CommonUtil;
import kr.co.gubed.habit2good.gpoint.util.Preference;
import kr.co.gubed.habit2good.gpoint.view.CashPopDialog;
import kr.co.gubed.habit2good.gpoint.view.LoadingDialog;
import kr.co.gubed.habit2good.gpoint.view.NetworkDialog;
import kr.co.gubed.habit2good.gpoint.view.SupportDialog;
import kr.co.gubed.habit2good.R;

public class SignJoinActivity extends Activity implements View.OnClickListener, AsyncTaskCompleteListener<String> {

    private String TAG = this.getClass().toString();

    private EditText et_cpid;
    private EditText et_partner_code;
    private TextView tv_import_partner;

    private LinearLayout recommend_layer;
    private TextView recommend_id1;
    private TextView recommend_id2;
    private Button btn_confirm;
    private Button btn_back;
    private Button btn_info;

    private TextView tv_user;
    private TextView tv_privacy;

    private ToggleButton toggle_check_user;
    private TextView tv_user_agree;
    private ToggleButton toggle_check_privacy;
    private TextView tv_privacy_agree;
    private ToggleButton toggle_check_age14;
    private TextView tv_age14_agree;

    private Applications applications;
    private Tracker tracker;

    private LoadingDialog loadingDialog;
    private CashPopDialog cashPopDialog;
    private SupportDialog supportDialog;
    private NetworkDialog networkDialog;

    private String analiticsCategory = "/sign_up";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_join);

        applications = (Applications)this.getApplication();
        tracker = applications.getDefaultTracker();
        tracker.setScreenName(analiticsCategory);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        btn_info = (Button)findViewById(R.id.btn_info);
        btn_info.setOnClickListener(this);

        et_cpid = (EditText)findViewById(R.id.et_cpid);
        et_cpid.setOnClickListener(this);
        et_cpid.setFilters(new InputFilter[]{CommonUtil.spaceFilter});
        et_partner_code = (EditText)findViewById(R.id.et_partner_code);
        et_partner_code.setOnClickListener(this);
        et_partner_code.setFilters(new InputFilter[]{CommonUtil.spaceFilter});

        tv_import_partner = (TextView)findViewById(R.id.tv_import_partner);
        String import_partner_txt = this.getResources().getString(R.string.import_partner);
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            tv_import_partner.setText(Html.fromHtml(import_partner_txt.replaceAll("\n", "<br>"), Html.FROM_HTML_MODE_LEGACY));
        }else{
            tv_import_partner.setText(Html.fromHtml(import_partner_txt.replaceAll("\n", "<br>")));
        }

        recommend_layer = (LinearLayout)findViewById(R.id.recommend_layer);
        recommend_id1 = (TextView)findViewById(R.id.recommend_id1);
        recommend_id1.setOnClickListener(this);
        recommend_id2 = (TextView)findViewById(R.id.recommend_id2);
        recommend_id2.setOnClickListener(this);

        btn_confirm = (Button)findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        btn_confirm.setEnabled(false);
        btn_confirm.setTextColor(ContextCompat.getColor(this,R.color.text_btn_dis));

        tv_user = (TextView)findViewById(R.id.tv_user);
        tv_user.setOnClickListener(this);
        tv_privacy = (TextView)findViewById(R.id.tv_privacy);
        tv_privacy.setOnClickListener(this);

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            tv_user.setText(Html.fromHtml("<u>"+this.getResources().getString(R.string.user).replaceAll("\n", "<br>")+"</u>", Html.FROM_HTML_MODE_LEGACY));
            tv_privacy.setText(Html.fromHtml("<u>"+this.getResources().getString(R.string.privacy).replaceAll("\n", "<br>")+"</u>", Html.FROM_HTML_MODE_LEGACY));
        }else{
            tv_user.setText(Html.fromHtml("<u>"+this.getResources().getString(R.string.user).replaceAll("\n", "<br>")+"</u>"));
            tv_privacy.setText(Html.fromHtml("<u>"+this.getResources().getString(R.string.privacy).replaceAll("\n", "<br>")+"</u>"));
        }

        toggle_check_user = (ToggleButton)findViewById(R.id.toggle_check_user);
        toggle_check_user.setOnClickListener(this);
        tv_user_agree = (TextView)findViewById(R.id.tv_user_agree);
        tv_user_agree.setOnClickListener(this);
        toggle_check_privacy = (ToggleButton)findViewById(R.id.toggle_check_privacy);
        toggle_check_privacy.setOnClickListener(this);
        tv_privacy_agree = (TextView)findViewById(R.id.tv_privacy_agree);
        tv_privacy_agree.setOnClickListener(this);

        toggle_check_age14 = (ToggleButton)findViewById(R.id.toggle_check_age14);
        toggle_check_age14.setOnClickListener(this);
        tv_age14_agree = (TextView)findViewById(R.id.tv_age14_agree);
        tv_age14_agree.setOnClickListener(this);

        loadingDialog = new LoadingDialog(this);
        cashPopDialog = new CashPopDialog(this);
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

    public void joinProcess(){
        if( et_cpid.getText().toString().equals("")){
            et_cpid.setError(getResources().getString(R.string.make_id));
            return;
        }

        if( et_cpid.getText().toString().length() < 4 || !Pattern.matches("^[a-zA-Z0-9]*$", et_cpid.getText().toString()) || et_cpid.getText().toString().length() > 15){
            et_cpid.setError(getResources().getString(R.string.error_id));
            return;
        }

        if( et_cpid.getText().toString().equals(et_partner_code.getText().toString())){
            et_partner_code.setError(this.getResources().getString(R.string.no_partner));
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(CommonUtil.KEY_CPID, et_cpid.getText().toString());
        if( !et_partner_code.getText().toString().equals("")) {
            map.put(CommonUtil.KEY_PARTNERCODE, et_partner_code.getText().toString());
        }
        map.put(CommonUtil.KEY_ADID, Applications.preference.getValue(Preference.AD_ID, ""));
        map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_JOIN);
        String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
        requestAsyncTask(param, CommonUtil.ACTION_JOIN);
        ShowLoadingProgress();
    }

    @Override
    public void onTaskComplete(String result) {
        if( result == null){
            HideLoadingProgress();
            //internal error
            cashPopDialog = new CashPopDialog(this);
            cashPopDialog.setCpTitle(getResources().getString(R.string.signup_error));
            cashPopDialog.setCpDesc(getResources().getString(R.string.signup_error_desc));
            cashPopDialog.setCpCancelButton(getResources().getString(R.string.try_again), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    joinProcess();
                    cashPopDialog.dismiss();
                }
            });
            cashPopDialog.setCpNeutralityButton(getResources().getString(R.string.start_again), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cashPopDialog.dismiss();
                }
            });
            cashPopDialog.show();
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
                    if( action.equals(CommonUtil.ACTION_JOIN)){
                        String uid = jo.getString(CommonUtil.RESULT_USERID);
                        String cpid = jo.getString(CommonUtil.RESULT_CPID);
                        Applications.preference.put(Preference.USER_ID, uid);
                        Applications.preference.put(Preference.CPID, cpid);
                        cashPopDialog = new CashPopDialog(this);
                        cashPopDialog.setCpTitle(getResources().getString(R.string.congratulation));
                        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            cashPopDialog.setCpDesc(Html.fromHtml(getResources().getString(R.string.congratulation_desc, "<font color=\"#00c853\">"+cpid+"</font>").replaceAll("\n", "<br>"), Html.FROM_HTML_MODE_LEGACY));
                        }else{
                            cashPopDialog.setCpDesc(Html.fromHtml(getResources().getString(R.string.congratulation_desc, "<font color=\"#00c853\">"+cpid+"</font>").replaceAll("\n", "<br>")));
                        }
                        cashPopDialog.setCpOkButton(getResources().getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cashPopDialog.dismiss();
                                GoMain();
                            }
                        });
                        cashPopDialog.setCpCancel(false);
                        cashPopDialog.show();
                    }
                }else if( error != null && error.equals(CommonUtil.ERROR_EXIST_USER)){
                    et_cpid.setError(getResources().getString(R.string.already_id));
                    recommend_layer.setVisibility(View.VISIBLE);
                    String recommend1 = jo.getString(CommonUtil.RESULT_RECOMMEND_1);
                    String recommend2 = jo.getString(CommonUtil.RESULT_RECOMMEND_2);
                    recommend_id1.setText(recommend1);
                    recommend_id2.setText(recommend2);
                }else if( error != null && error.equals(CommonUtil.ERROR_NO_PARTNER)){
                    et_partner_code.setError(this.getResources().getString(R.string.no_partner));
                }else if( error != null && error.equals(CommonUtil.ERROR_NO_MINE)){
                    et_partner_code.setError(this.getResources().getString(R.string.nomine));
                }else if( error != null && error.equals(CommonUtil.ERROR_EXIST_DEVICE)){
                    cashPopDialog = new CashPopDialog(this);
                    cashPopDialog.setCpDesc(getResources().getString(R.string.exist_device));
                    cashPopDialog.setCpOkButton(getResources().getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cashPopDialog.dismiss();
                        }
                    });
                    cashPopDialog.setCpCancel(false);
                    cashPopDialog.show();
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
            if( action.equals(CommonUtil.ACTION_JOIN)){
                showErrorNetwork(param, action);
            }
        }catch (Exception ignore){
            ignore.printStackTrace();
        }
    }

    public void showErrorNetwork(final String param, final String action){
        if( networkDialog == null){
            networkDialog = new NetworkDialog(this);
        }
        if( !networkDialog.isShowing()) {
            networkDialog.setCancelClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HideLoadingProgress();
                    networkDialog.dismiss();
                    ActivityCompat.finishAffinity(SignJoinActivity.this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_info:
                cashPopDialog = new CashPopDialog(this);
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
                        CommonUtil.showSupport(SignJoinActivity.this, false);
                    }
                });
                cashPopDialog.show();
                HideLoadingProgress();
                break;
            case R.id.btn_confirm:
                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/confirm_click").build());
                et_cpid.setCursorVisible(false);
                et_partner_code.setCursorVisible(false);
                joinProcess();
                break;
            case R.id.recommend_id1:
                et_cpid.setText(recommend_id1.getText().toString());
                et_cpid.setError(null);
                recommend_layer.setVisibility(View.INVISIBLE);
                recommend_id1.setText("");
                recommend_id2.setText("");
                break;
            case R.id.recommend_id2:
                et_cpid.setText(recommend_id2.getText().toString());
                et_cpid.setError(null);
                recommend_layer.setVisibility(View.INVISIBLE);
                recommend_id1.setText("");
                recommend_id2.setText("");
                break;
            case R.id.tv_user:
                //terms
                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/terms_Terms_of_Use_click").build());
                startActivity(new Intent(SignJoinActivity.this, TermsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_slide_out_left);
                break;
            case R.id.tv_privacy:
                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/terms_Privacy_Policy_click").build());
                startActivity(new Intent(SignJoinActivity.this, PrivateActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_slide_out_left);
                break;
            case R.id.et_cpid:
                et_cpid.setCursorVisible(true);
                break;
            case R.id.et_partner_code:
                et_partner_code.setCursorVisible(true);
                break;
            case R.id.toggle_check_user:
                if( toggle_check_user.isChecked()){
                    tv_user_agree.setTextColor(ContextCompat.getColor(this,R.color.text_default));
                    if( toggle_check_privacy.isChecked() && toggle_check_age14.isChecked()){
                        btn_confirm.setEnabled(true);
                        btn_confirm.setTextColor(ContextCompat.getColor(this,android.R.color.white));
                    }
                }else{
                    tv_user_agree.setTextColor(ContextCompat.getColor(this,R.color.text_hint));
                    btn_confirm.setEnabled(false);
                    btn_confirm.setTextColor(ContextCompat.getColor(this,R.color.text_btn_dis));
                }
                break;
            case R.id.tv_user_agree:
                toggle_check_user.setChecked(!toggle_check_user.isChecked());
                if( toggle_check_user.isChecked()){
                    tv_user_agree.setTextColor(ContextCompat.getColor(this,R.color.text_default));
                    if( toggle_check_privacy.isChecked() && toggle_check_age14.isChecked()){
                        btn_confirm.setEnabled(true);
                        btn_confirm.setTextColor(ContextCompat.getColor(this,android.R.color.white));
                    }
                }else{
                    tv_user_agree.setTextColor(ContextCompat.getColor(this,R.color.text_hint));
                    btn_confirm.setEnabled(false);
                    btn_confirm.setTextColor(ContextCompat.getColor(this,R.color.text_btn_dis));
                }
                break;
            case R.id.toggle_check_privacy:
                if( toggle_check_privacy.isChecked()){
                    tv_privacy_agree.setTextColor(ContextCompat.getColor(this,R.color.text_default));
                    if( toggle_check_user.isChecked() && toggle_check_age14.isChecked()){
                        btn_confirm.setEnabled(true);
                        btn_confirm.setTextColor(ContextCompat.getColor(this,android.R.color.white));
                    }
                }else{
                    tv_privacy_agree.setTextColor(ContextCompat.getColor(this,R.color.text_hint));
                    btn_confirm.setEnabled(false);
                    btn_confirm.setTextColor(ContextCompat.getColor(this,R.color.text_btn_dis));
                }
                break;
            case R.id.tv_privacy_agree:
                toggle_check_privacy.setChecked(!toggle_check_privacy.isChecked());
                if( toggle_check_privacy.isChecked()){
                    tv_privacy_agree.setTextColor(ContextCompat.getColor(this,R.color.text_default));
                    if( toggle_check_user.isChecked() && toggle_check_age14.isChecked()){
                        btn_confirm.setEnabled(true);
                        btn_confirm.setTextColor(ContextCompat.getColor(this,android.R.color.white));
                    }
                }else{
                    tv_privacy_agree.setTextColor(ContextCompat.getColor(this,R.color.text_hint));
                    btn_confirm.setEnabled(false);
                    btn_confirm.setTextColor(ContextCompat.getColor(this,R.color.text_btn_dis));
                }
                break;
            case R.id.toggle_check_age14:
                if( toggle_check_age14.isChecked()){
                    tv_age14_agree.setTextColor(ContextCompat.getColor(this,R.color.text_default));
                    if( toggle_check_user.isChecked() && toggle_check_privacy.isChecked()){
                        btn_confirm.setEnabled(true);
                        btn_confirm.setTextColor(ContextCompat.getColor(this,android.R.color.white));
                    }
                }else{
                    tv_age14_agree.setTextColor(ContextCompat.getColor(this,R.color.text_hint));
                    btn_confirm.setEnabled(false);
                    btn_confirm.setTextColor(ContextCompat.getColor(this,R.color.text_btn_dis));
                }
                break;
            case R.id.tv_age14_agree:
                toggle_check_age14.setChecked(!toggle_check_age14.isChecked());
                if( toggle_check_age14.isChecked()){
                    tv_age14_agree.setTextColor(ContextCompat.getColor(this,R.color.text_default));
                    if( toggle_check_user.isChecked() && toggle_check_privacy.isChecked()){
                        btn_confirm.setEnabled(true);
                        btn_confirm.setTextColor(ContextCompat.getColor(this,android.R.color.white));
                    }
                }else{
                    tv_age14_agree.setTextColor(ContextCompat.getColor(this,R.color.text_hint));
                    btn_confirm.setEnabled(false);
                    btn_confirm.setTextColor(ContextCompat.getColor(this,R.color.text_btn_dis));
                }
                break;
        }
    }

    public void GoMain() {
        Intent intent = new Intent(SignJoinActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.anim_slide_out_left);
        finish();
    }

    public void ShowLoadingProgress() {
        //show loading
        try {
            if( loadingDialog == null){
                loadingDialog = new LoadingDialog(SignJoinActivity.this);
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
