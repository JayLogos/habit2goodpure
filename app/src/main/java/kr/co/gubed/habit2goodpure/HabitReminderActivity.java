package kr.co.gubed.habit2goodpure;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HabitReminderActivity extends AppCompatActivity {
    private HabitReminderAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Integer habitid;
    //private ArrayList<HabitReminder> mHabitReminder;
    private ArrayList<HashMap<String, String>> habitReminderList;

    private static ItemTouchHelper touchHelper;
    private HabitDbAdapter dbAdapter;

    public HabitReminderActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbAdapter = new HabitDbAdapter(this);
        dbAdapter.open();

        setContentView(R.layout.activity_reminder_main);
        Intent intent = getIntent();

        habitid = Objects.requireNonNull(intent.getExtras()).getInt("habitid");

        //Toolbar toolbar;
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        habitReminderList = new ArrayList<HashMap<String, String>>();
        mAdapter = new HabitReminderAdapter(this, habitReminderList);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //mAdapter = new HabitReminderAdapter(habitReminderList);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallbackForReminder(mAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        //toolbar = findViewById(R.id.tb_reminder);
        //setSupportActionBar(toolbar);
        //Toolbar 의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.outline_keyboard_arrow_left_white_18);
        actionBar.setTitle("알람");



        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(HabitReminderActivity.this, AddHabitReminder.class);

            intent.putExtra("habitid", habitid);
            startActivity(intent);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean onHideToolBar = false;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (onHideToolBar) {
                    getSupportActionBar().hide();
                } else {
                    getSupportActionBar().show();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && !fab.isShown()) {
                    onHideToolBar = false;
                    fab.show();
                } else if (dy > 0 && fab.isShown()) {
                    onHideToolBar = true;
                    fab.hide();
                }
            }
        });

        makeListFromDB(habitid);
        mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            makeListFromDB(habitid);
            //mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
            mAdapter.notifyDataSetChanged();
            Log.i(getClass().getName(), "onResume...");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void makeListFromDB(Integer habitid) {
        List<HabitReminder> habitReminderLinkedList;
        habitReminderList.clear();

        habitReminderLinkedList = dbAdapter.getHabitReminderList(habitid, "alarm_time");

        for (int i = 0; i < habitReminderLinkedList.size(); i++) {
            HashMap<String, String> items = new HashMap<String, String>();
            HabitReminder habitReminder = habitReminderLinkedList.get(i);

            items.put("habitid", habitReminder.mHabitId.toString());
            items.put("alarm_time", habitReminder.mAlarmTime);
            items.put("alarm_name", habitReminder.mAlarmName);
            items.put("alarm_state", String.valueOf(habitReminder.mAlarmState));
            items.put("sunday", String.valueOf(habitReminder.mReSunday));
            items.put("monday", String.valueOf(habitReminder.mReMonday));
            items.put("tuesday", String.valueOf(habitReminder.mReTuesday));
            items.put("wednesday", String.valueOf(habitReminder.mReWednesday));
            items.put("thursday", String.valueOf(habitReminder.mReThursday));
            items.put("friday", String.valueOf(habitReminder.mReFriday));
            items.put("saturday", String.valueOf(habitReminder.mReSaturday));

            habitReminderList.add(items);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }
}
