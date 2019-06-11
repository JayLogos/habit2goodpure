package kr.co.gubed.habit2goodpure.gpoint.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import kr.co.gubed.habit2goodpure.AddHabit;
import kr.co.gubed.habit2goodpure.R;
import kr.co.gubed.habit2goodpure.UploadFile;
import kr.co.gubed.habit2goodpure.gpoint.listener.AsyncTaskCompleteListener;
import kr.co.gubed.habit2goodpure.gpoint.util.APICrypto;
import kr.co.gubed.habit2goodpure.gpoint.util.Applications;
import kr.co.gubed.habit2goodpure.gpoint.util.AsyncHTTPPost;
import kr.co.gubed.habit2goodpure.gpoint.util.CommonUtil;
import kr.co.gubed.habit2goodpure.gpoint.util.Preference;
import kr.co.gubed.habit2goodpure.gpoint.view.LoadingDialog;
import kr.co.gubed.habit2goodpure.gpoint.view.NetworkDialog;

public class ProfileActivity extends Activity implements View.OnClickListener, AsyncTaskCompleteListener<String>, AdapterView.OnItemSelectedListener {

    private String TAG = this.getClass().toString();

    private Button btn_back;
    private Button btn_info;
    private ImageView iv_profile;
    private TextView tv_cpid;
    private EditText et_nickname;
    private Button btn_check_nickname;
    private Spinner spinner_year;
    private Spinner spinner_location;
    private TextView tv_gender_1;
    private RadioButton rb_gender_1;
    private TextView tv_gender_2;
    private RadioButton rb_gender_2;
    private TextView tv_marriage_1;
    private RadioButton rb_marriage_1;
    private TextView tv_marriage_2;
    private RadioButton rb_marriage_2;
    private TextView tv_partner_code;
    private EditText et_partner_code;
    private TextView tv_import_partner;
    private Button btn_confirm;

    private LoadingDialog loadingDialog;
    private NetworkDialog networkDialog;

    private Applications applications;
    private Tracker tracker;

    private String analiticsCategory = "/my_profile";
    private ArrayList<Integer> yearList;
    private String[] locationList;
    private int defaultYear;
    private int defaultYearIdx;
    private int defaultLocationIdx;
    private String nickname = "";
    private String year = "";
    private String location = "";
    private String gender = "";
    private String marriage = "";
    private String partnerCode = "";
    private String alarm = "";

    String mCurrentPhotoPath;
    File goalImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        applications = (Applications) getApplication();
        tracker = applications.getDefaultTracker();
        tracker.setScreenName(analiticsCategory);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        btn_info = (Button) findViewById(R.id.btn_info);
        btn_info.setOnClickListener(this);

        iv_profile = findViewById(R.id.circle_image);
        mCurrentPhotoPath = Applications.preference.getValue(Preference.PROFILE_IMAGE, "");
        String profileImageUrl = CommonUtil.PROFILE_SERVER_IMAGE_URL + Applications.preference.getValue(Preference.USER_ID, "") + "/my_profile.jpg";
        if (mCurrentPhotoPath.equals("default") /*|| mCurrentPhotoPath.equals("")*/) {
            Glide.with(this).load(profileImageUrl)
                    .apply(new RequestOptions()
                            .error(R.drawable.profile_default_image)
                            .circleCrop())
                    .into(iv_profile);
        } else {
            File file = new File(mCurrentPhotoPath);
            if (!file.exists()) {
                Log.i(getClass().getName(), "profileImageUrl="+profileImageUrl);

                Glide.with(this).load(profileImageUrl)
                        .apply(new RequestOptions()
                                .error(R.drawable.profile_default_image)
                                .circleCrop())
                        .into(iv_profile);
            } else {
                Glide.with(this).load(mCurrentPhotoPath)
                        .apply(new RequestOptions()
                                .error(R.drawable.profile_default_image)
                                .circleCrop())
                        .into(iv_profile);
            }
        }
        iv_profile.setOnClickListener(onProfileImageSelectedListener);

        tv_cpid = (TextView)findViewById(R.id.tv_cpid);
        et_nickname = findViewById(R.id.et_nickname);
        btn_check_nickname = findViewById(R.id.btn_check_nickname);
        btn_check_nickname.setOnClickListener(this);
        btn_check_nickname.setEnabled(false);
        et_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkNicknameBtn();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nickname = s.toString();
                checkNicknameBtn();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int yearSize = 100;
        int yearIndex = 14;
        String birth = Applications.preference.getValue(Preference.BIRTH, "");
        try{
            year = birth;
            defaultYear = Integer.parseInt(year);
        }catch (Exception ignore){
            defaultYear = currentYear - yearIndex;
        }
        spinner_year = (Spinner)findViewById(R.id.spinner_year);
        yearList = new ArrayList<>();
        int index = 0;
        for(int i=currentYear;i>(currentYear-yearSize);i--){
            yearList.add(i);
            if( i == defaultYear){
                yearIndex = index;
            }
            index++;
        }
        defaultYearIdx = yearIndex;
        ArrayAdapter yearAdapter = new ArrayAdapter(this, R.layout.row_spinner, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(yearAdapter);
        spinner_year.setSelection(yearIndex);
        spinner_year.setOnItemSelectedListener(this);

        location = Applications.preference.getValue(Preference.LOCATION, "");
        spinner_location = (Spinner)findViewById(R.id.spinner_location);
        locationList = getResources().getStringArray(R.array.locations_id);
        defaultLocationIdx = 0;
        int lindex = 0;
        for(int i=0;i<locationList.length;i++){
            if( location.equals(locationList[i])){
                defaultLocationIdx = i;
                lindex = i;
            }
        }
        ArrayAdapter locationAdapter = new ArrayAdapter(this, R.layout.row_spinner, locationList);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_location.setAdapter(locationAdapter);
        spinner_location.setSelection(lindex);
        spinner_location.setOnItemSelectedListener(this);

        tv_gender_1 = (TextView)findViewById(R.id.tv_gender_1);
        tv_gender_1.setOnClickListener(this);
        rb_gender_1 = (RadioButton)findViewById(R.id.rb_gender_1);
        rb_gender_1.setOnClickListener(this);
        tv_gender_2 = (TextView)findViewById(R.id.tv_gender_2);
        tv_gender_2.setOnClickListener(this);
        rb_gender_2 = (RadioButton)findViewById(R.id.rb_gender_2);
        rb_gender_2.setOnClickListener(this);

        tv_marriage_1 = (TextView)findViewById(R.id.tv_marriage_1);
        tv_marriage_1.setOnClickListener(this);
        rb_marriage_1 = (RadioButton)findViewById(R.id.rb_marriage_1);
        rb_marriage_1.setOnClickListener(this);
        tv_marriage_2 = (TextView)findViewById(R.id.tv_marriage_2);
        tv_marriage_2.setOnClickListener(this);
        rb_marriage_2 = (RadioButton)findViewById(R.id.rb_marriage_2);
        rb_marriage_2.setOnClickListener(this);

        tv_partner_code = (TextView)findViewById(R.id.tv_partner_code);
        et_partner_code = (EditText)findViewById(R.id.et_partner_code);
        et_partner_code.setFilters(new InputFilter[]{CommonUtil.spaceFilter});
        et_partner_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btn_confirm.setEnabled(true);
                btn_confirm.setTextColor(ContextCompat.getColor(ProfileActivity.this, android.R.color.white));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        tv_import_partner = (TextView)findViewById(R.id.tv_import_partner);
        String import_partner_txt = this.getResources().getString(R.string.import_partner);
        if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
            tv_import_partner.setText(Html.fromHtml(import_partner_txt.replaceAll("\n", "<br>"), Html.FROM_HTML_MODE_LEGACY));
        }else{
            tv_import_partner.setText(Html.fromHtml(import_partner_txt.replaceAll("\n", "<br>")));
        }

        btn_confirm = (Button)findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);

//        btn_confirm.setEnabled(false);
//        btn_confirm.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.text_btn_dis));

        tv_cpid.setText(Applications.preference.getValue(Preference.CPID,""));
        et_nickname.setText(Applications.preference.getValue(Preference.NICKNAME, ""));
        Log.i(getClass().getName(), "NICKNAME from preference repository is "+Applications.preference.getValue(Preference.NICKNAME, ""));

        loadingDialog = new LoadingDialog(this);
        networkDialog = new NetworkDialog(this);

        getUserInfo();

    }

    public void setInformation(){
        Log.e(TAG, "setInformation");
        tv_cpid.setText(Applications.preference.getValue(Preference.CPID, ""));
        nickname = Applications.preference.getValue(Preference.NICKNAME, "");
        year = Applications.preference.getValue(Preference.BIRTH, "");
        location = Applications.preference.getValue(Preference.LOCATION, "");
        gender = Applications.preference.getValue(Preference.GENDER, "");
        marriage = Applications.preference.getValue(Preference.MARRIAGE, "");
        partnerCode = Applications.preference.getValue(Preference.PARTNERCDOE, "");
        if( gender.equals("1") || gender.equals("")) {
            rb_gender_1.setChecked(true);
        }else{
            rb_gender_2.setChecked(true);
        }
        if( marriage.equals("1") || marriage.equals("")) {
            rb_marriage_1.setChecked(true);
        }else{
            rb_marriage_2.setChecked(true);
        }
        if( partnerCode != null && !partnerCode.equals("")) {
            et_partner_code.setEnabled(false);
            et_partner_code.setText(partnerCode);
            tv_partner_code.setText(partnerCode);
            tv_partner_code.setVisibility(View.VISIBLE);
            et_partner_code.setVisibility(View.GONE);
            tv_import_partner.setVisibility(View.GONE);
        }else{
            tv_partner_code.setVisibility(View.GONE);
            et_partner_code.setVisibility(View.VISIBLE);
            et_partner_code.setEnabled(true);
            tv_import_partner.setVisibility(View.VISIBLE);
        }
        chkConfirmBtn();
    }

    public void chkConfirmBtn(){
        boolean isEnabled = false;
        if( !Applications.preference.getValue(Preference.BIRTH, "").equals(year+"") ||
                !Applications.preference.getValue(Preference.LOCATION, "").equals(location) ||
                !Applications.preference.getValue(Preference.GENDER, "").equals(gender) ||
                !Applications.preference.getValue(Preference.MARRIAGE, "").equals(marriage) ||
                !Applications.preference.getValue(Preference.NICKNAME, "").equals(nickname)
                ){
            isEnabled = true;
        }
        btn_confirm.setEnabled(isEnabled);
        if( isEnabled){
            btn_confirm.setTextColor(ContextCompat.getColor(ProfileActivity.this, android.R.color.white));
        }else{
            btn_confirm.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.text_btn_dis));
        }
    }

    public void checkNicknameBtn(){
        boolean isEnabled = false;
        if(!Applications.preference.getValue(Preference.NICKNAME, "").equals(nickname)){
            isEnabled = true;
        }
        btn_check_nickname.setEnabled(isEnabled);
        if( isEnabled){
            btn_check_nickname.setTextColor(ContextCompat.getColor(ProfileActivity.this, android.R.color.holo_red_dark));
        }else{
            btn_check_nickname.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.text_btn_dis));
        }
    }

    @Override
    public void onBackPressed() {
        try{
            if( loadingDialog != null && loadingDialog.isShowing()){
                loadingDialog.dismiss();
                loadingDialog = null;
            }
            if( networkDialog != null && networkDialog.isShowing()){
                networkDialog.dismiss();
                networkDialog = null;
            }
        }catch (Exception ignore){
        }
        tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/back_click").build());
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
            if( networkDialog != null && networkDialog.isShowing()){
                networkDialog.dismiss();
                networkDialog = null;
            }
        }catch (Exception ignore){
        }
        super.onDestroy();
    }

    public void getUserInfo(){
        ShowLoadingProgress();
        HashMap<String, String> map = new HashMap<>();
        map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID,""));
        map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_GET_USER);
        String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
        requestAsyncTask(param, CommonUtil.ACTION_GET_USER);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_info:
                CommonUtil.showSupport(ProfileActivity.this, true);
                break;
            case R.id.tv_gender_1:
                rb_gender_1.setChecked(true);
                gender = "1";
                chkConfirmBtn();
                break;
            case R.id.rb_gender_1:
                gender = "1";
                chkConfirmBtn();
                break;
            case R.id.tv_gender_2:
                rb_gender_2.setChecked(true);
                gender = "2";
                chkConfirmBtn();
                break;
            case R.id.rb_gender_2:
                gender = "2";
                chkConfirmBtn();
                break;
            case R.id.tv_marriage_1:
                rb_marriage_1.setChecked(true);
                marriage = "1";
                chkConfirmBtn();
                break;
            case R.id.rb_marriage_1:
                marriage = "1";
                chkConfirmBtn();
                break;
            case R.id.tv_marriage_2:
                rb_marriage_2.setChecked(true);
                marriage = "2";
                chkConfirmBtn();
                break;
            case R.id.rb_marriage_2:
                marriage = "2";
                chkConfirmBtn();
                break;
            case R.id.btn_confirm:
                btn_confirm.setEnabled(false);
                btn_confirm.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.text_btn_dis));
                tracker.send(new HitBuilders.EventBuilder().setCategory(analiticsCategory).setAction("/confirm_click").build());
                updateUserInfo();
                break;
            case R.id.btn_check_nickname:
                checkNicknameRedundancy();
                break;
        }
    }

    public void updateUserInfo(){
        ShowLoadingProgress();
        HashMap<String, String> map = new HashMap<>();
        map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID,""));
        map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_USER_UPDATE);
        map.put(CommonUtil.KEY_NICKNAME, nickname);
        map.put(CommonUtil.KEY_YEAR, year);
        map.put(CommonUtil.KEY_LOCATION, location);
        map.put(CommonUtil.KEY_GENDER, gender);
        map.put(CommonUtil.KEY_MARRIAGE, marriage);

        if( !et_partner_code.getText().toString().equals("") && partnerCode.equals("") && et_partner_code.isEnabled()){
            map.put(CommonUtil.KEY_PARTNERCODE, et_partner_code.getText().toString());
            et_partner_code.setEnabled(false);
        }
        map.put(CommonUtil.KEY_MARRIAGE, marriage);

        Applications.preference.put(Preference.LOCATION, location);
        Applications.preference.put(Preference.GENDER, gender);
        Applications.preference.put(Preference.MARRIAGE, marriage);

        String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
        requestAsyncTask(param, CommonUtil.ACTION_USER_UPDATE);
    }

    public void checkNicknameRedundancy(){
        ShowLoadingProgress();
        HashMap<String, String> map = new HashMap<>();
        map.put(CommonUtil.KEY_USERID, Applications.preference.getValue(Preference.USER_ID,""));
        map.put(CommonUtil.KEY_ACTION, CommonUtil.ACTION_CHECK_NICKNAME_REDUNDANCY);
        map.put(CommonUtil.KEY_NICKNAME, et_nickname.getText().toString());

        String param = APICrypto.getParam(this, map, CommonUtil.SHARED_KEY);
        requestAsyncTask(param, CommonUtil.ACTION_CHECK_NICKNAME_REDUNDANCY);
    }

    public void ShowLoadingProgress() {
        //show loading
        try {
            if( loadingDialog == null){
                loadingDialog = new LoadingDialog(ProfileActivity.this);
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

    @Override
    public void onTaskComplete(String result) {
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
                switch (action) {
                    case CommonUtil.ACTION_GET_USER:
                        String yearStr = jo.getString(CommonUtil.RESULT_YEAR);
                        if( yearStr != null && !yearStr.equals("")) {
                            year = yearStr;
                        }else{
                            year = yearList.get(defaultYearIdx)+"";
                        }
                        String cpId = jo.getString("c");
                        location = jo.getString(CommonUtil.RESULT_LOCATION);
                        if( location == null || location.equals("")){
                            location = locationList[defaultLocationIdx];
                        }
                        nickname = jo.getString(CommonUtil.RESULT_NICKNAME);
                        gender = jo.getString(CommonUtil.RESULT_GENDER);
                        marriage = jo.getString(CommonUtil.RESULT_MARRIAGE);
                        partnerCode = jo.getString(CommonUtil.RESULT_PARTNERCODE);
                        alarm = jo.getString(CommonUtil.RESULT_ALARM);

                        Applications.preference.put(Preference.CPID, cpId);
                        Applications.preference.put(Preference.NICKNAME, nickname);

                        Applications.preference.put(Preference.BIRTH, year);
                        Applications.preference.put(Preference.LOCATION, location);
                        Applications.preference.put(Preference.GENDER, gender);
                        Applications.preference.put(Preference.MARRIAGE, marriage);
                        Applications.preference.put(Preference.PARTNERCDOE, partnerCode);

                        setInformation();
                        break;
                    case CommonUtil.ACTION_USER_UPDATE:
                        String su = jo.getString(CommonUtil.RESULT_RESULT);
                        if(su.equals("s")){
                            Toast toast = Toast.makeText(ProfileActivity.this, getResources().getString(R.string.update_success), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                            toast.show();
                            Applications.preference.put(Preference.NICKNAME, nickname);
                        }else{
                            Toast toast = Toast.makeText(ProfileActivity.this, getResources().getString(R.string.update_fail), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                            toast.show();

                            /*
                            nickname 중복 처리 루틴 추가 요!
                             */
                        }
                        Applications.isHomeRefresh = true;
                        Applications.isStoreRefresh = true;
                        chkConfirmBtn();
                        checkNicknameBtn();
                        break;
                    case CommonUtil.ACTION_CHECK_NICKNAME_REDUNDANCY:
                        String check_result = jo.getString(CommonUtil.RESULT_RESULT);
                        if(check_result.equals("available")){
                            Toast.makeText(this, "사용 가능한 닉네임입니다.", Toast.LENGTH_LONG).show();
                            nickname = jo.getString(CommonUtil.RESULT_NICKNAME);
                            Applications.isHomeRefresh = true;
                            Applications.isStoreRefresh = true;
                            chkConfirmBtn();
                        }else if(check_result.equals("duplicated")){
                            Toast.makeText(this, "이미 사용하고 있는 닉네임입니다.", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }else if( error != null && error.equals(CommonUtil.ERROR_NO_PARTNER)){
                et_partner_code.setEnabled(true);
                et_partner_code.setError(this.getResources().getString(R.string.no_partner));
            }else if( error != null && error.equals(CommonUtil.ERROR_NO_MINE)){
                et_partner_code.setEnabled(true);
                et_partner_code.setError(this.getResources().getString(R.string.nomine));
            }else if( error != null && error.equals(CommonUtil.ERROR_EXIST_PARTNER_CODE)){
                et_partner_code.setEnabled(true);
                et_partner_code.setError(this.getResources().getString(R.string.no_partner));
            }
        } catch (Exception e) {
            setInformation();
            e.printStackTrace();
        }
        HideLoadingProgress();
    }

    @Override
    public void onTaskError(String param, String action, String result) {
        try{
            Log.e(TAG, action);
            if( action.equals(CommonUtil.ACTION_GET_USER)){
                showErrorNetwork(param, action);
            }else if( action.equals(CommonUtil.ACTION_USER_UPDATE)){
                showErrorNetwork(param, action);
            }
        }catch (Exception ignore){
        }
    }

    public void showErrorNetwork(final String param, final String action){
        if( networkDialog == null){
            networkDialog = new NetworkDialog(ProfileActivity.this);
        }
        if( !networkDialog.isShowing()) {
            networkDialog.setCancelClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HideLoadingProgress();
                    networkDialog.dismiss();
                    ActivityCompat.finishAffinity(ProfileActivity.this);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.spinner_year:
                year = yearList.get(i).toString();
                try {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.text_default));
                }catch (Exception ignore){
                }
                Log.e(TAG,i+" | "+year+" : onItemSelected year");
                chkConfirmBtn();
                break;
            case R.id.spinner_location:
                location = locationList[i];
                try {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(this, R.color.text_default));
                }catch (Exception ignore){
                }
                Log.e(TAG,i+" | "+location+" : onItemSelected location");
                chkConfirmBtn();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void requestAsyncTask(String param, String action){
        if( Applications.getCountry(this).equals("KR") && !Applications.isRoaming(this)) {
            new AsyncHTTPPost(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, CommonUtil.SERVER_URL, param, action);
        }else{
            new AsyncHTTPPost(this).execute(CommonUtil.SERVER_URL, param, action);
        }
    }

    private final View.OnClickListener onProfileImageSelectedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // dialog 를 통해서 갤러리, 카메라, 기본 이미지 선택
            showDialogForProfileImage();
        }
    };

    private void showDialogForProfileImage() {
        final List<String> ListItems = new ArrayList<>();

        ListItems.add("앨범에서 사진 선택");
        ListItems.add("사진 촬영");
        ListItems.add("기본 사진으로 변경");
        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle(" 프로필 사진 등록");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:     // 앨범
                        getImageFromAlbum();
                        break;
                    case 1:     // 촬영
                        //takePicture();
                        getImageFromCamera();
                        break;
                    case 2:     // 기본 사진
                        setProfileImageToDefault();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case AddHabit.REQUEST_PICK_ALBUM:
                Uri dataUri = data.getData();
                if (dataUri != null) {
                    cropImageFromAlbum(dataUri);
                }
                break;
            case AddHabit.REQUEST_TAKE_PHOTO:
                cropImageFromCamera();
                break;
            case AddHabit.REQUEST_CROP_IMAGE:
                Applications.preference.put(Preference.PROFILE_IMAGE, mCurrentPhotoPath);
                Glide.with(this).load(mCurrentPhotoPath)
                        .apply(new RequestOptions().circleCrop()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true))
                        .into(iv_profile);

                galleryAddPic();
                uploadFile(mCurrentPhotoPath);
                break;
            default:
                break;
        }
    }

    private void getImageFromAlbum() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, AddHabit.REQUEST_PICK_ALBUM);
        } else {
            Toast.makeText(getApplicationContext(), "갤러리 사용 권한을 허용해야 합니다.", Toast.LENGTH_LONG).show();
        }
    }

    private void getImageFromCamera() {

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try{
                goalImage = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Uri uri = FileProvider.getUriForFile(this, "kr.co.gubed.habit2good", goalImage);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(takePictureIntent, AddHabit.REQUEST_TAKE_PHOTO);
        } else {
            Toast.makeText(getApplicationContext(), "사진 사용 권한을 허용해야 합니다.", Toast.LENGTH_LONG).show();
        }
    }

    private void cropImageFromCamera() {
        Uri uri = FileProvider.getUriForFile(this, "kr.co.gubed.habit2good", goalImage);
        Intent intent = getCropIntent(uri, uri);
        startActivityForResult(intent, AddHabit.REQUEST_CROP_IMAGE);
    }

    private void cropImageFromAlbum(Uri inputUri) {
        try {
            goalImage = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri outputUri = Uri.fromFile(goalImage);
        Intent intent = getCropIntent(inputUri, outputUri);
        startActivityForResult(intent, AddHabit.REQUEST_CROP_IMAGE);
    }

    private Intent getCropIntent(Uri inputUri, Uri outputUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(inputUri, "image/*");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        return intent;
    }

    private void setProfileImageToDefault() {
        Glide.with(this).load(R.drawable.ic_habit2good_512)
                .apply(new RequestOptions().circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                .into(iv_profile);
        mCurrentPhotoPath = "default";
    }

    private File createImageFile() throws IOException {
        /*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "h2g_" + timeStamp + ".jpg";*/
        String imageFileName = "my_profile.jpg";

        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+imageFileName);

        mCurrentPhotoPath = storageDir.getAbsolutePath();
        Log.i(getClass().getName(), "mCurrentPhotoPath="+mCurrentPhotoPath);
        return storageDir;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = FileProvider.getUriForFile(this, "kr.co.gubed.habit2good", f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private void checkManifestPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(getApplicationContext(), "접근 권한 요청을 허용하셨습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "접근 권한 요청을 거부하셨습니다.", Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("권한 요청을 거절하시면 서비스 사용이 불가합니다. \n\n[설정] 눌러서 [권한]을 변경해 주세요.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    private void uploadFile(String imagePath) {
        String url = CommonUtil.PROFILE_SERVER_URL;
        String uid = Applications.preference.getValue(Preference.USER_ID, "");

        Log.i(TAG, "PROFILE_SERVER_URL="+url+", uid="+uid);

        try {
            UploadFile uploadFile = new UploadFile(ProfileActivity.this);
            uploadFile.setPath(uid, imagePath);
            uploadFile.execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


