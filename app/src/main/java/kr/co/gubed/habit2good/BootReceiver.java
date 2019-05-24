package kr.co.gubed.habit2good;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    private HabitReminderAdapter reminderAdapter;

    @Override
    public void onReceive(Context context, Intent intent) {
        HabitDbAdapter dbAdapter = new HabitDbAdapter(context);
        List<HabitReminder> habitReminderLinkedList;
        reminderAdapter = new HabitReminderAdapter(context);

        dbAdapter.open();
        habitReminderLinkedList = dbAdapter.getHabitReminderList();

        for (int i = 0; i < habitReminderLinkedList.size(); i++) {
            HabitReminder habitReminder = habitReminderLinkedList.get(i);
            reminderAdapter.setAlarmReminderItem(habitReminder.mHabitId, habitReminder.mAlarmTime, habitReminder.mAlarmName, habitReminder.mAlarmState);
        }
        Log.i(getClass().getName(), "BootReceiver, reminder registration was completed");
    }
}
