package kr.co.gubed.habit2goodpure.gpoint.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import kr.co.gubed.habit2goodpure.R;
import kr.co.gubed.habit2goodpure.gpoint.util.Applications;
import kr.co.gubed.habit2goodpure.gpoint.util.CommonUtil;
import kr.co.gubed.habit2goodpure.gpoint.util.Preference;

public class InviteActivity extends Activity implements View.OnClickListener {

    private String TAG = this.getClass().toString();

    private TextView tv_title;
    private Button btn_back;
    private Button btn_info;

    private TextView tv_partner_cnt;
    private TextView tv_partner_gpoint;

    private ImageView iv_partner;

    private Button btn_kakao;
    private Button btn_kakaostory;
    private Button btn_sms;
    private Button btn_facebook;
    private Button btn_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        this.init();

    }

    public void init(){
        tv_title = (TextView)findViewById(R.id.tv_title);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        btn_info = (Button) findViewById(R.id.btn_info);
        btn_info.setOnClickListener(this);

        Log.i(TAG,"numberOfpartner="+Applications.preference.getValue(Preference.PARTNERS, "0")+
                " sumPartner="+Applications.preference.getValue(Preference.PARTNER_GPOINT, "0"));

        tv_partner_cnt = (TextView)findViewById(R.id.tv_partner_cnt);
        tv_partner_cnt.setText(getResources().getString(R.string.partner_cnt, CommonUtil.setComma(Applications.preference.getValue(Preference.PARTNERS, "0"), false, false)));

        tv_partner_gpoint = (TextView)findViewById(R.id.tv_partner_gpoint);
        tv_partner_gpoint.setText(CommonUtil.setComma(Applications.preference.getValue(Preference.PARTNER_GPOINT, "0"), false, false));

        iv_partner = (ImageView)findViewById(R.id.iv_partner);
        Picasso.with(this).load(R.drawable.guide3).error(R.drawable.guide3).into(iv_partner);

        btn_kakao = (Button)findViewById(R.id.btn_kakao);
        btn_kakao.setOnClickListener(this);

        btn_kakaostory = (Button)findViewById(R.id.btn_kakaostory);
        btn_kakaostory.setOnClickListener(this);

        btn_facebook = (Button)findViewById(R.id.btn_facebook);
        btn_facebook.setOnClickListener(this);

        btn_link = (Button)findViewById(R.id.btn_link);
        btn_link.setOnClickListener(this);

        btn_sms = (Button)findViewById(R.id.btn_sms);
        btn_sms.setOnClickListener(this);

        btn_facebook = (Button)findViewById(R.id.btn_facebook);
        btn_facebook.setOnClickListener(this);

        btn_link = (Button)findViewById(R.id.btn_link);
        btn_link.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_info:
                CommonUtil.showSupport(InviteActivity.this, true);
                break;
            case R.id.btn_kakao:
                try {
                    String msg = InviteActivity.this.getResources().getString(R.string.invite_msg, Applications.preference.getValue(Preference.CPID, ""), Applications.preference.getValue(Preference.CPID, ""));
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, msg);
                    shareIntent.setPackage("com.kakao.talk");

                    startActivity(Intent.createChooser(shareIntent, ""));
                }catch (Exception ignore){

                }
                break;
            case R.id.btn_kakaostory:
                try {
                    String msg = InviteActivity.this.getResources().getString(R.string.invite_msg, Applications.preference.getValue(Preference.CPID, ""), Applications.preference.getValue(Preference.CPID, ""));
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, msg);
                    shareIntent.setPackage("com.kakao.story");

                    startActivity(Intent.createChooser(shareIntent, ""));
                }catch (Exception ignore){
                    ignore.printStackTrace();
                }
                break;
            case R.id.btn_sms:
                try {
                    String smsmsg = InviteActivity.this.getResources().getString(R.string.invite_msg, Applications.preference.getValue(Preference.CPID, ""), Applications.preference.getValue(Preference.CPID, ""));
                    Intent smsintent = new Intent(Intent.ACTION_SENDTO);
                    smsintent.putExtra("sms_body", smsmsg);
                    smsintent.setData(Uri.parse("smsto:"));
                    startActivity(smsintent);
                }catch (Exception ignore){
                    Toast toast = Toast.makeText(InviteActivity.this, getResources().getString(R.string.ist_not_supported), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                    toast.show();
                }
                break;
            case R.id.btn_facebook:
                try{
                    Intent fbintent = new Intent(Intent.ACTION_SEND);
                    fbintent.setType("text/plain");
                    fbintent.putExtra(Intent.EXTRA_TEXT, "http://a.habit2good.com/share/"+Applications.preference.getValue(Preference.USER_ID, ""));
                    fbintent.setPackage("com.facebook.katana");
                    startActivity(fbintent);
                }catch (Exception ignore){
                    //https://www.facebook.com/sharer/sharer.php?u=http%3A%2F%2Fa.cashpop.net%2Fshare%2F1001
                    if( !Applications.preference.getValue(Preference.USER_ID, "").equals("")){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/sharer/sharer.php?u=http%3A%2F%2Fa.habit2good.com%2Fshare%2F"+Applications.preference.getValue(Preference.USER_ID, ""))));
                    }else{
                        Toast toast = Toast.makeText(InviteActivity.this, getResources().getString(R.string.ist_not_supported), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                        toast.show();
                    }
                }
                break;
            case R.id.btn_link:
                try{
                    String msg = InviteActivity.this.getResources().getString(R.string.invite_msg, Applications.preference.getValue(Preference.CPID, ""), Applications.preference.getValue(Preference.CPID, ""));
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, msg);
                    Intent chooser = Intent.createChooser(intent, getResources().getString(R.string.invite_partners));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(chooser);
                    }else{
                        startActivity(intent);
                    }
                }catch (Exception ignore){
                    Toast toast = Toast.makeText(InviteActivity.this, getResources().getString(R.string.ist_not_supported), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, CommonUtil.TOAST_YOFFSET);
                    toast.show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
