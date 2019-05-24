package kr.co.gubed.habit2good;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getName(), "getContentViewId="+getContentViewId());

        setContentView(getContentViewId());

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    /*
     * overrridePendingTransition()을 통해 activity 전환 애니메이션을 구현하게 되면
     * BottomNavtigationView의 메뉴도 함께 애니메이션 효과를 받음
     * 일단 블럭
     * 다른 앱 들은 fragment로 구현한 것으로 보임
     * 추후 검토 필요
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        navigationView.postDelayed(() -> {
            int itemId = item.getItemId();
            if (itemId == R.id.btn_nav_plus1) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (itemId == R.id.btn_nav_habits) {
                startActivity(new Intent(this, HabitsActivity.class));
            } else if (itemId == R.id.btn_nav_point) {
                startActivity(new Intent(this, PointmallActivity.class));
            } else if (itemId == R.id.btn_nav_dashboard) {
                startActivity(new Intent(this, DashboardActivity.class));
            }
            finish();
        }, 0);
        return true;
    }



    private void updateNavigationBarState(){
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        MenuItem item = navigationView.getMenu().findItem(itemId);
        item.setChecked(true);
    }

    abstract int getContentViewId();

    abstract int getNavigationMenuItemId();

}
