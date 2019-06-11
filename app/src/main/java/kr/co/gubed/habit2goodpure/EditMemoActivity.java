package kr.co.gubed.habit2goodpure;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

public class EditMemoActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionbar;
    EditText etMemo;
    TextView tvMemo;

    Integer habitid;
    String date;
    String memo;

    private HabitDbAdapter dbAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        Objects.requireNonNull(actionbar).setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.mipmap.outline_keyboard_arrow_left_white_18);
        actionbar.setTitle(R.string.activity_edit_memo);

        dbAdapter = new HabitDbAdapter(this);
        dbAdapter.open();

        etMemo = findViewById(R.id.et_memo);
        tvMemo = findViewById(R.id.tv_memo);

        Intent intent = getIntent();
        habitid = Objects.requireNonNull(intent.getExtras()).getInt("habitid");
        date = Objects.requireNonNull(intent.getExtras()).getString("date");
        memo = Objects.requireNonNull(intent.getExtras()).getString("memo");

        etMemo.setText(memo);
        tvMemo.setText(memo);

        etMemo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String year = date.substring(0, 4);
                String month = date.substring(5, 7);
                String day = date.substring(8);
                HfDay date = new HfDay(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day));
                dbAdapter.setMemo(habitid, date, s.toString());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                return true;
            case R.id.action_edit:
                tvMemo.setVisibility(View.GONE);
                etMemo.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_edit_memo, menu);
        return true;
    }
}
