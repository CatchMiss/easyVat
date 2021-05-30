package com.moptim.easyvat.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.moptim.easyvat.R;
import com.moptim.easyvat.utils.SharedPreferencesUtil;
import com.moptim.easyvat.utils.Sp;

public class HoleModeView extends LinearLayout {

    public HoleModeView(final Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_hole_mode, this);

        /*
         * 测试类型，单选，裸眼和矫正
         */
        final RelativeLayout bakeEye = view.findViewById(R.id.rl_bakeEye);
        final RelativeLayout correction = view.findViewById(R.id.rl_correction);

        int holeMode = (int) SharedPreferencesUtil.getParams(context, Sp.HOLE_MODE, 0);
        if(holeMode == 0){
            bakeEye.setBackgroundColor(getResources().getColor(R.color.c20));
            correction.setBackgroundColor(getResources().getColor(R.color.c30));
        }else{
            bakeEye.setBackgroundColor(getResources().getColor(R.color.c30));
            correction.setBackgroundColor(getResources().getColor(R.color.c20));
        }

        bakeEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesUtil.setParams(context, Sp.HOLE_MODE, 0);
                bakeEye.setBackgroundColor(getResources().getColor(R.color.c20));
                correction.setBackgroundColor(getResources().getColor(R.color.c30));
            }
        });

        correction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesUtil.setParams(context, Sp.HOLE_MODE, 1);
                bakeEye.setBackgroundColor(getResources().getColor(R.color.c30));
                correction.setBackgroundColor(getResources().getColor(R.color.c20));
            }
        });
    }

}
