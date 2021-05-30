package com.moptim.easyvat.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moptim.easyvat.R;

public class TipsDialog extends AlertDialog {

    private Context mContext;
    private int delay = 2000;

    private TextView tipTXt = null; // 标题

    private OnDismissListener listener;
    public void setDismissListener(OnDismissListener listener) {
        this.listener = listener;
    }

    public TipsDialog(Context context) {
        super(context, R.style.fullDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tipTXt = new TextView(mContext);
        tipTXt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tipTXt.setTextColor(Color.BLACK);
        tipTXt.setTextSize(150);
        tipTXt.setGravity(Gravity.CENTER);
        setContentView(tipTXt);
    }

    public void setDelay(int delay){
        this.delay = delay;
    }

    public void showTipDialog(String str) {
        this.setCancelable(false);
        this.show();

        tipTXt.setText(str);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                dismiss();
                if(listener != null){
                    listener.onDismiss(TipsDialog.this);
                }
            }
        }, delay);
    }
}
