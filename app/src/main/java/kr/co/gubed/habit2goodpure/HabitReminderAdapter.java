package kr.co.gubed.habit2goodpure;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class HabitReminderAdapter extends RecyclerView.Adapter<HabitReminderAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {
    private final Context context;
    private ArrayList<HashMap<String, String>> habitReminderList;
    private HabitDbAdapter dbAdapter;


    public HabitReminderAdapter(Context context, ArrayList<HashMap<String, String>> habitReminderList) {
        this.context = context;
        this.habitReminderList = habitReminderList;
        dbAdapter = new HabitDbAdapter(context);
        dbAdapter.open();
    }

    public HabitReminderAdapter (Context context) {
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)


    @NonNull
    @Override
    public HabitReminderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_reminder_item, null);
        return new ViewHolder(view);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        Integer habitId;
        String alarmTime;

        habitId = Integer.valueOf(Objects.requireNonNull(habitReminderList.get(position).get("habitid")));
        alarmTime = habitReminderList.get(position).get("alarm_time");

        dbAdapter.deleteHabitReminderItem(habitId, alarmTime);
        setAlarmReminderItem(habitId, alarmTime, null, false);
        habitReminderList.remove(position);
        notifyItemRemoved(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperIndicator {
        Integer habitid;
        final CardView cardView;
        final TextView tv_alarmTime;
        final TextView tv_alarmName;
        final TextView tv_sunday;
        final TextView tv_monday;
        final TextView tv_tuesday;
        final TextView tv_wednesday;
        final TextView tv_thursday;
        final TextView tv_friday;
        final TextView tv_saturday;
        final Switch sw_alarmOnOff;

        ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            tv_alarmTime = itemView.findViewById(R.id.tv_alarmTime);
            tv_alarmName = itemView.findViewById(R.id.tv_alarmName);
            tv_sunday = itemView.findViewById(R.id.tv_sunday);
            tv_monday = itemView.findViewById(R.id.tv_monday);
            tv_tuesday = itemView.findViewById(R.id.tv_tuesday);
            tv_wednesday = itemView.findViewById(R.id.tv_wednesday);
            tv_thursday = itemView.findViewById(R.id.tv_thursday);
            tv_friday = itemView.findViewById(R.id.tv_friday);
            tv_saturday = itemView.findViewById(R.id.tv_saturday);
            sw_alarmOnOff = itemView.findViewById(R.id.sw_alarmOnOff);
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final HabitReminderAdapter.ViewHolder holder, int mPosition) {
        final int position = mPosition;
        final HashMap<String,String> habitReminderItem = habitReminderList.get(position);

        holder.habitid = Integer.parseInt(Objects.requireNonNull(habitReminderItem.get("habitid")));

        holder.sw_alarmOnOff.setChecked(Objects.equals(habitReminderItem.get("alarm_state"), "true"));

        holder.tv_alarmTime.setText(habitReminderItem.get("alarm_time"));

        Log.i(getClass().getName(), "onBindViewHolder time="+habitReminderItem.get("alarm_time")+" alarm_state="+habitReminderItem.get("alarm_state"));

        if (Objects.requireNonNull(habitReminderItem.get("alarm_name")).equals("")) {
            holder.tv_alarmName.setVisibility(View.GONE);
            holder.tv_alarmName.setText("");
        } else {
            holder.tv_alarmName.setVisibility(View.VISIBLE);
            holder.tv_alarmName.setText(habitReminderItem.get("alarm_name"));
        }
        if (holder.sw_alarmOnOff.isChecked()) {
            holder.tv_alarmTime.setTextColor(Color.DKGRAY);
            holder.tv_alarmName.setTextColor(Color.DKGRAY);
        } else {
            holder.tv_alarmTime.setTextColor(Color.LTGRAY);
            holder.tv_alarmName.setTextColor(Color.LTGRAY);
        }
        holder.tv_alarmTime.setText(habitReminderItem.get("alarm_time"));

        if (Objects.requireNonNull(habitReminderItem.get("alarm_name")).equals("")) {
            holder.tv_alarmName.setVisibility(View.GONE);
        } else {
            holder.tv_alarmName.setVisibility(View.VISIBLE);
            holder.tv_alarmName.setText(habitReminderItem.get("alarm_name"));
        }

        if (Objects.requireNonNull(habitReminderItem.get("sunday")).equals("true")) {
            holder.tv_sunday.setTextColor(Color.BLUE);
        } else {
            holder.tv_sunday.setTextColor(Color.GRAY);
        }
        if (Objects.requireNonNull(habitReminderItem.get("monday")).equals("true")) {
            holder.tv_monday.setTextColor(Color.BLUE);
        } else {
            holder.tv_monday.setTextColor(Color.GRAY);
        }
        if (Objects.requireNonNull(habitReminderItem.get("tuesday")).equals("true")) {
            holder.tv_tuesday.setTextColor(Color.BLUE);
        } else {
            holder.tv_tuesday.setTextColor(Color.GRAY);
        }
        if (Objects.requireNonNull(habitReminderItem.get("wednesday")).equals("true")) {
            holder.tv_wednesday.setTextColor(Color.BLUE);
        } else {
            holder.tv_wednesday.setTextColor(Color.GRAY);
        }
        if (Objects.requireNonNull(habitReminderItem.get("thursday")).equals("true")) {
            holder.tv_thursday.setTextColor(Color.BLUE);
        } else {
            holder.tv_thursday.setTextColor(Color.GRAY);
        }
        if (Objects.requireNonNull(habitReminderItem.get("friday")).equals("true")) {
            holder.tv_friday.setTextColor(Color.BLUE);
        } else {
            holder.tv_friday.setTextColor(Color.GRAY);
        }
        if (Objects.requireNonNull(habitReminderItem.get("saturday")).equals("true")) {
            holder.tv_saturday.setTextColor(Color.BLUE);
        } else {
            holder.tv_saturday.setTextColor(Color.GRAY);
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateReminderItem(v, habitReminderItem, position);
                notifyItemChanged(position);
            }
        });

        holder.sw_alarmOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setReminderItem(holder, isChecked);
            }
        });
    }

    private void setReminderItem(HabitReminderAdapter.ViewHolder holder, boolean isChecked) {
        Integer habitid = holder.habitid;
        String alarmTime = holder.tv_alarmTime.getText().toString();
        String alarmName = holder.tv_alarmName.getText().toString();
        if (isChecked) {
            holder.tv_alarmTime.setTextColor(ContextCompat.getColor(context, R.color.md_grey_800)); // default gray 보다 진함, color tuning 은 추후에...
            holder.tv_alarmName.setTextColor(ContextCompat.getColor(context, R.color.md_grey_800));
        } else {
            holder.tv_alarmTime.setTextColor(ContextCompat.getColor(context, R.color.md_grey_400));
            holder.tv_alarmName.setTextColor(ContextCompat.getColor(context, R.color.md_grey_400));
        }
        dbAdapter.setHabitReminderItemState(habitid, alarmTime, isChecked);
        setAlarmReminderItem(habitid, alarmTime, alarmName, isChecked);
    }

    @Override
    public int getItemCount() {
        return habitReminderList.size();
    }

    private void updateReminderItem(View v, HashMap<String,String> habitReminderItem, int position) {
        Intent intent = new Intent(v.getContext(), UpdateHabitReminder.class);

        intent.putExtra("habitid", habitReminderItem.get("habitid"));
        intent.putExtra("alarm_time", habitReminderItem.get("alarm_time"));
        intent.putExtra("alarm_name", habitReminderItem.get("alarm_name"));
        intent.putExtra("alarm_state", habitReminderItem.get("alarm_state"));
        intent.putExtra("sunday", habitReminderItem.get("sunday"));
        intent.putExtra("monday", habitReminderItem.get("monday"));
        intent.putExtra("tuesday", habitReminderItem.get("tuesday"));
        intent.putExtra("wednesday", habitReminderItem.get("wednesday"));
        intent.putExtra("thursday", habitReminderItem.get("thursday"));
        intent.putExtra("friday", habitReminderItem.get("friday"));
        intent.putExtra("saturday", habitReminderItem.get("saturday"));

        v.getContext().startActivity(intent);
        //overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
        Log.i(getClass().getName(), "habitid="+habitReminderItem.get("habitid")+" time="+habitReminderItem.get("alarm_time"));
    }
/*
    private void deleteReminderItem(HabitReminderAdapter.ViewHolder holder, HashMap<String,String> habitReminderItem, int position) {
        holder.tv_
    }
    */

    public void setAlarmReminderItem(Integer habitid, String alarmTime, String alarmName, boolean isOn) {
        String mAlarmCode = habitid+alarmTime.substring(0,2)+alarmTime.substring(3);
        Calendar calendar = Calendar.getInstance();
        Long currentTime = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(alarmTime.substring(0, 2)));
        calendar.set(Calendar.MINUTE, Integer.valueOf(alarmTime.substring(3)));
        calendar.set(Calendar.SECOND, 0);
        Long settingTime = calendar.getTimeInMillis();
        // 기 설정된 알람은 현재 시간보다 이 전이면 알람을 발생하고 있어,  설정된 시간을 하루 이 후로 설정해야 함
        if (currentTime >= settingTime) {
            calendar.add(Calendar.DATE, 1);
        }

        //Log.i(getClass().getName(), "calendar.HOUR_OF_DAY="+calendar.get(Calendar.HOUR_OF_DAY)+" calendar.MINUTE="+calendar.get(Calendar.MINUTE));
        //Log.i(getClass().getName(), "Gap between current and alarm_time="+(currentTime-settingTime)/1000);

        if (isOn) {
            AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("habitid", habitid);
            intent.putExtra("alarm_time", alarmTime);
            intent.putExtra("alarm_name", alarmName);

            PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, Integer.valueOf(mAlarmCode), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // setRepeating() 정확한 시간을 요하기 때문에 시스템 리소스를 더 사용할 수 있음.
            Objects.requireNonNull(mAlarmManager).setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mPendingIntent);

            Log.i(getClass().getName(), "alarm register, time="+alarmTime+" habitid="+habitid+" alarm_name="+alarmName+" mAlarmCode="+Integer.valueOf(mAlarmCode));
        } else {
            AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, Integer.valueOf(mAlarmCode), intent, PendingIntent.FLAG_CANCEL_CURRENT);

            Objects.requireNonNull(mAlarmManager).cancel(mPendingIntent);
            mPendingIntent.cancel();

            Log.i(getClass().getName(), "alarm cancel, time="+alarmTime+" mAlarmCode="+Integer.valueOf(mAlarmCode));
        }
    }
}

class HabitReminder {
    public Integer mHabitId;
    public String mAlarmTime;
    public String mAlarmName;
    public boolean mAlarmState;
    public boolean mReSunday;
    public boolean mReMonday;
    public boolean mReTuesday;
    public boolean mReWednesday;
    public boolean mReThursday;
    public boolean mReFriday;
    public boolean mReSaturday;

    public HabitReminder() {

    }

    public HabitReminder(int habitid, String alarmTime, String alarmName, boolean alarmState,
                         boolean reSunday, boolean reMonday, boolean reTuesday,
                         boolean reWednesday, boolean reThursday, boolean reFriday, boolean reSaturday) {
        this.mHabitId = habitid;
        this.mAlarmTime = alarmTime;
        this.mAlarmName = alarmName;
        this.mAlarmState = alarmState;
        this.mReSunday = reSunday;
        this.mReMonday = reMonday;
        this.mReTuesday = reTuesday;
        this.mReWednesday = reWednesday;
        this.mReThursday = reThursday;
        this.mReFriday = reFriday;
        this.mReSaturday = reSaturday;
    }
}
