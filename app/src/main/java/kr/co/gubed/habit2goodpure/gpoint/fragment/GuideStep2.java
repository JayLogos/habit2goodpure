package kr.co.gubed.habit2goodpure.gpoint.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import kr.co.gubed.habit2goodpure.R;


public class GuideStep2 extends Fragment {

    private ImageView iv_step;

    public static GuideStep2 newInstance(){
        GuideStep2 fragment = new GuideStep2();
        Bundle args = new Bundle();
        args.putInt("p",2);
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
        View view = inflater.inflate(R.layout.fragment_guide_step2, container, false);
        iv_step = (ImageView)view.findViewById(R.id.iv_step);
        Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.guide2);
        iv_step.setImageDrawable(d);
        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        iv_step.setImageDrawable(null);
    }
}
