package kr.co.gubed.habit2good.gpoint.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import kr.co.gubed.habit2good.R;


public class GuideStep1 extends Fragment {

    private ImageView iv_step;

    public static GuideStep1 newInstance(){
        GuideStep1 fragment = new GuideStep1();
        Bundle args = new Bundle();
        args.putInt("p",1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_guide_step1, container, false);
        iv_step = (ImageView)view.findViewById(R.id.iv_step);
        Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.guide1);
        iv_step.setImageDrawable(d);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        iv_step.setImageDrawable(null);
    }
}
