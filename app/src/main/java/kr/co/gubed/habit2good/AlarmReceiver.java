package kr.co.gubed.habit2good;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.Objects;

import kr.co.gubed.habit2good.gpoint.util.CommonUtil;

public class AlarmReceiver extends BroadcastReceiver{
    final private String channelName = CommonUtil.CHANNEL_NAME_HABIT;
    private HabitDbAdapter dbAdapter;

    @Override
    public void onReceive(Context context, Intent intent) {
        dbAdapter = new HabitDbAdapter(context);
        dbAdapter.open();
        Integer habitId;
        String alarmTime, alarmName;
        //HabitReminder habitReminder = new HabitReminder();
        HabitReminder habitReminder;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ringTonePreference = sharedPreferences.getString("key_pref_ringtone", "DEFAULT_SOUND");
        Uri soundUri = Uri.parse(ringTonePreference);


        String channelId = CommonUtil.CHANNEL_ID_HABIT;
        /*
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            //mChannel.setGroup(CommonUtil.CHANNEL_GROUP_HABIT);
            Objects.requireNonNull(notificationManager).createNotificationChannel(mChannel);
        }
        */

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), channelId);

        Intent notificationIntent = new Intent(context, HabitsActivity.class);
        notificationIntent.setAction(CommonUtil.CLICK_ACTION_HABIT);      // status bar 클릭 시 "습관" 탭으로 구동하기 위해서 전달
        habitId = Objects.requireNonNull(intent.getExtras()).getInt("habitid");
        alarmTime = intent.getExtras().getString("alarm_time");
        alarmName = intent.getExtras().getString("alarm_name");
        habitReminder = dbAdapter.getHabitReminder(habitId, alarmTime);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_80);
        Calendar calendar = Calendar.getInstance();
        if (((calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) && (habitReminder.mReSunday)) ||
            ((calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) && (habitReminder.mReMonday)) ||
            ((calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) && (habitReminder.mReTuesday)) ||
            ((calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) && (habitReminder.mReWednesday)) ||
            ((calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) && (habitReminder.mReThursday)) ||
            ((calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) && (habitReminder.mReFriday)) ||
            ((calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) && (habitReminder.mReSaturday))) {

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);

            int requestId = (int) System.currentTimeMillis();

            PendingIntent pendingIntent = PendingIntent.getActivity(context, requestId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentTitle(dbAdapter.getHabitNameFromDB(habitId))
                    .setContentText(alarmName)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    //.setNumber(999)
                    //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    //.setSound(soundUri)
                    .setSmallIcon(R.drawable.icon_80)
                    .setLargeIcon(bitmap)
                    //.setBadgeIconType(R.drawable.icon_16)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(alarmName));
            /*
            if (sharedPreferences.getBoolean("key_pref_vibrate", true)) {
                builder.setDefaults(Notification.DEFAULT_ALL);
            } else {
                builder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS);
            }
            Log.i(getClass().getName(), "setSound: "+soundUri);
            */
            Objects.requireNonNull(notificationManager).notify(0, builder.build());

            Log.i(getClass().getName(), "ALARM onReceive habitid="+intent.getExtras().getInt("habitid")+" alarm_name="+intent.getExtras().getString("alarm_name"));
        }
    }


}
