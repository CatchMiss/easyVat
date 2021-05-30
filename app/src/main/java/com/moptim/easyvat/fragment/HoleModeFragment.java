package com.moptim.easyvat.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.moptim.easyvat.R;
import com.moptim.easyvat.utils.SharedPreferencesUtil;
import com.moptim.easyvat.utils.Sp;

public class HoleModeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,   Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hole_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
         * 测试类型，单选，裸眼和矫正
         */
        final RelativeLayout bakeEye = view.findViewById(R.id.rl_bakeEye);
        final RelativeLayout correction = view.findViewById(R.id.rl_correction);

        int holeMode = (int) SharedPreferencesUtil.getParams(getActivity(), Sp.HOLE_MODE, 0);
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
                SharedPreferencesUtil.setParams(getActivity(), Sp.HOLE_MODE, 0);
                bakeEye.setBackgroundColor(getResources().getColor(R.color.c20));
                correction.setBackgroundColor(getResources().getColor(R.color.c30));
            }
        });

        correction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesUtil.setParams(getActivity(), Sp.HOLE_MODE, 1);
                bakeEye.setBackgroundColor(getResources().getColor(R.color.c30));
                correction.setBackgroundColor(getResources().getColor(R.color.c20));
            }
        });
    }
}
