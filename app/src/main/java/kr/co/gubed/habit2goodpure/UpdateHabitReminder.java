package kr.co.gubed.habit2goodpure;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class UpdateHabitReminder extends AppCompatActivity {
    private Integer mHabitId;
    private String mOldAlarmTime;
    private Integer mMinute;
    private String mAlarmName;
    private boolean mAlarmState;
    private boolean mReSunday;
    private boolean mReMonday;
    private boolean mReTuesday;
    private boolean mReWednesday;
    private boolean mReThursday;
    private boolean mReFriday;
    private boolean mReSaturday;

    private Button btn_cancel, btn_save;
    private TimePicker tp_time;
    private TextView tv_alarmTitle, tv_alarmName;
    private ToggleButton tb_sunday, tb_monday, tb_tuesday, tb_wednesday, tb_thursday, tb_friday, tb_saturday;
    private CardView alarmNameView;


    private HabitReminder mHabitReminder;
    private HabitReminderAdapter reminderAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_reminder_settings);

        //Toolbar 의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(false);
        //actionBar.setHomeAsUpIndicator(R.mipmap.outline_keyboard_arrow_left_white_18);
        //actionBar.setTitle("알람 수정하기");


        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);

        tp_time = findViewById(R.id.timePicker);

        tb_sunday = findViewById(R.id.tb_sunday);
        tb_monday = findViewById(R.id.tb_monday);
        tb_tuesday = findViewById(R.id.tb_tuesday);
        tb_wednesday = findViewById(R.id.tb_wednesday);
        tb_thursday = findViewById(R.id.tb_thursday);
        tb_friday = findViewById(R.id.tb_friday);
        tb_saturday = findViewById(R.id.tb_saturday);

        tv_alarmName = findViewById(R.id.tv_alarmName);
        tv_alarmTitle = findViewById(R.id.tv_alarmTitle);

        //alarmNameView = findViewById(R.id.alarmNameView);

        mHabitId = Integer.valueOf(Objects.requireNonNull(Objects.requireNonNull(intent.getExtras()).getString("habitid")));
        mOldAlarmTime = intent.getExtras().getString("alarm_time");
        Log.i(getClass().getName(), "habitid="+mHabitId+" mAlarmTime="+intent.getExtras().getString("alarm_time"));
        Integer mHour = Integer.valueOf(mOldAlarmTime.substring(0, 2));
        mMinute = Integer.valueOf(mOldAlarmTime.substring(3));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tp_time.setHour(mHour);
            tp_time.setMinute(mMinute);
        } else {
            tp_time.setCurrentHour(mHour);
            tp_time.setCurrentMinute(mMinute);
        }


        tb_sunday.setChecked(Objects.equals(intent.getExtras().getString("sunday"), "true"));
        tb_monday.setChecked(Objects.equals(intent.getExtras().getString("monday"), "true"));
        tb_tuesday.setChecked(Objects.equals(intent.getExtras().getString("tuesday"), "true"));
        tb_wednesday.setChecked(Objects.equals(intent.getExtras().getString("wednesday"), "true"));
        tb_thursday.setChecked(Objects.equals(intent.getExtras().getString("thursday"), "true"));
        tb_friday.setChecked(Objects.equals(intent.getExtras().getString("friday"), "true"));
        tb_saturday.setChecked(Objects.equals(intent.getExtras().getString("saturday"), "true"));
        tv_alarmName.setText(intent.getExtras().getString("alarm_name"));

        btn_cancel.setOnClickListener(onClickListenerCancel);
        btn_save.setOnClickListener(onClickListenerSave);
        tv_alarmName.setOnClickListener(onClickListenerAlarmName);
        tv_alarmTitle.setOnClickListener(onClickListenerAlarmName);

    }

    private final View.OnClickListener onClickListenerCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private final View.OnClickListener onClickListenerSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveHabitReminder();
            finish();
        }
    };

    private final View.OnClickListener onClickListenerAlarmName = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            show();
        }
    };

    private void saveHabitReminder() {
        HabitDbAdapter dbAdapter = new HabitDbAdapter(this);
        dbAdapter.open();
        HabitReminder habitReminder = new HabitReminder();
        reminderAdapter = new HabitReminderAdapter(this);

        Format formatter;
        Calendar calendar = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            calendar.set(Calendar.HOUR_OF_DAY, tp_time.getHour());
            calendar.set(Calendar.MINUTE, tp_time.getMinute());
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, tp_time.getCurrentHour());
            calendar.set(Calendar.MINUTE, tp_time.getCurrentMinute());
        }

        formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

        habitReminder.mHabitId = mHabitId;
        habitReminder.mAlarmTime = formatter.format(calendar.getTime());
        habitReminder.mAlarmName = tv_alarmName.getText().toString();
        habitReminder.mReSunday = tb_sunday.isChecked();
        habitReminder.mReMonday = tb_monday.isChecked();
        habitReminder.mReTuesday = tb_tuesday.isChecked();
        habitReminder.mReWednesday = tb_wednesday.isChecked();
        habitReminder.mReThursday = tb_thursday.isChecked();
        habitReminder.mReFriday = tb_friday.isChecked();
        habitReminder.mReSaturday = tb_saturday.isChecked();
        habitReminder.mAlarmState = true;

        Log.i(getClass().getName(), "habitid="+habitReminder.mHabitId+" Time="+habitReminder.mAlarmTime+" Name="+habitReminder.mAlarmName);
        dbAdapter.updateHabitReminder(habitReminder, mOldAlarmTime);
        if (!habitReminder.mAlarmTime.equals(mOldAlarmTime)) { // 2018.09.10 알람 시간이 변경되면 이전 알람은 캔슬
            reminderAdapter.setAlarmReminderItem(habitReminder.mHabitId, mOldAlarmTime, habitReminder.mAlarmName, false);
        }
        reminderAdapter.setAlarmReminderItem(habitReminder.mHabitId, habitReminder.mAlarmTime, habitReminder.mAlarmName, true);
    }

    private void show() {
        final EditText editText = new EditText(this);
        editText.setText(tv_alarmName.getText());
        editText.setSelection(editText.length());

        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("알람 이름을 입력하세요.");
        builder.setView(editText);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_alarmName.setText(editText.getText().toString());
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }
}
