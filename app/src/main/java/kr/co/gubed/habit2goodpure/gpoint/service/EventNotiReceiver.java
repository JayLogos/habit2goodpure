package kr.co.gubed.habit2goodpure.gpoint.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import kr.co.gubed.habit2goodpure.gpoint.util.Applications;
import kr.co.gubed.habit2goodpure.gpoint.util.Preference;
import kr.co.gubed.habit2goodpure.R;

public class EventNotiReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if( intent.getAction().equals("EVENTNOTI")) {
                Preference preference = new Preference(context);
                if( preference.getValue(Preference.CASH_POP_ALARM, true)) {

                    String message = intent.getStringExtra("msg");
                    String title = intent.getStringExtra("ltitle");
                    int notiid = intent.getIntExtra("notiid", 0);

                    Intent alIntent = new Intent("EVENTNOTI");
                    alIntent.putExtra("ltitle", title);
                    alIntent.putExtra("msg", message);
                    alIntent.putExtra("notiid", notiid);
                    PendingIntent alPendingIntent = PendingIntent.getBroadcast(context, notiid, alIntent, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(alPendingIntent);

                    Applications.dbHelper.delAlarmNoti(notiid + "");

                    Intent newIntent = new Intent();
                    newIntent.setAction("HABIT_EVENT_GO");
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, newIntent, PendingIntent.FLAG_ONE_SHOT);
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
                    } else {
                        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
                    }
                    notificationBuilder.setContentTitle(title);
                    notificationBuilder.setContentText(message);
                    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
                    notificationBuilder.setAutoCancel(true);
                    notificationBuilder.setSound(defaultSoundUri);
                    notificationBuilder.setContentIntent(pendingIntent);
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(notiid, notificationBuilder.build());
                }
            }
        }catch (Exception ignored){
            ignored.printStackTrace();
        }
    }
}
