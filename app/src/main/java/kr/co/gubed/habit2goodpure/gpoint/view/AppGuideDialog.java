package kr.co.gubed.habit2goodpure.gpoint.view;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import kr.co.gubed.habit2goodpure.gpoint.fragment.GuideStep1;
import kr.co.gubed.habit2goodpure.gpoint.fragment.GuideStep2;
import kr.co.gubed.habit2goodpure.gpoint.fragment.GuideStep3;
import kr.co.gubed.habit2goodpure.gpoint.listener.GuideListener;
import kr.co.gubed.habit2goodpure.R;


public class AppGuideDialog extends DialogFragment {

    private Button btn_skip;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    private Button page1;
    private Button page2;
    private Button page3;

    private Button btn_start;

    private GuideListener guideListener;

    private static boolean isShow = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    public void setGuideListener(GuideListener guideListener){
        this.guideListener = guideListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_guide, container);
        btn_skip = (Button)view.findViewById(R.id.btn_skip);
        if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
            btn_skip.setText(Html.fromHtml("<u>"+getResources().getString(R.string.skip).replaceAll("\n", "<br>")+"</u>"));
        }else{
            btn_skip.setText(Html.fromHtml("<u>"+getResources().getString(R.string.skip).replaceAll("\n", "<br>")+"</u>"));
        }
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( guideListener != null){
                    guideListener.dialogDismiss();
                }
            }
        });
        pager = (ViewPager)view.findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getChildFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(3);
        page1 = (Button)view.findViewById(R.id.page1);
        page2 = (Button)view.findViewById(R.id.page2);
        page3 = (Button)view.findViewById(R.id.page3);
        page1.setSelected(true);
        page2.setSelected(false);
        page3.setSelected(false);
        btn_start = (Button)view.findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("pager.getCurrentItem()", pager.getCurrentItem()+"");
                if( pager.getCurrentItem() == 2) {
                    if (guideListener != null) {
                        guideListener.dialogDismiss();
                    }
                }else{
                    pager.setCurrentItem(pager.getCurrentItem()+1);
                }
            }
        });

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                page1.setSelected(false);
                page2.setSelected(false);
                page3.setSelected(false);
                btn_skip.setVisibility(View.VISIBLE);
                //btn_start.setVisibility(View.GONE);
                switch (position){
                    case 0:
                        page1.setSelected(true);
                        btn_start.setText(getResources().getString(R.string.next));
                        break;
                    case 1:
                        page2.setSelected(true);
                        btn_start.setText(getResources().getString(R.string.next));
                        break;
                    case 2:
                        page3.setSelected(true);
                        btn_skip.setVisibility(View.GONE);
                        btn_start.setText(getResources().getString(R.string.start));
                        //btn_start.setVisibility(View.VISIBLE);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return GuideStep1.newInstance();
                case 1:
                    return GuideStep2.newInstance();
                case 2:
                    return GuideStep3.newInstance();
            }
            return GuideStep1.newInstance();
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        isShow = false;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if( isShow) return;
        super.show(manager, tag);
        isShow = true;
    }
}
