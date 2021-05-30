package com.moptim.easyvat.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.moptim.easyvat.R;
import com.moptim.easyvat.mode.DataBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleResultDialog extends Dialog {

    private Context mContext;

    private TextView tvTitle;
    private String titleStr;

    private TableLayout resultTableLayout = null;// 显示结果使用的主容器

    private List<DataBean> dataBeans;
    private int wid;

    public interface OnCloseClicked{
        void OnClicked();
    }
    private OnCloseClicked onCloseListener;

    public void setOnCloseListener(OnCloseClicked callback){
        onCloseListener = callback;
    }

    public SimpleResultDialog(Context context) {
        super(context, R.style.result_dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_result_simple);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        initView();

        initData();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.simple_dialog_title);
        resultTableLayout = (TableLayout) findViewById(R.id.result_table);

        findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if(onCloseListener != null){
                    onCloseListener.OnClicked();
                }
            }
        });
    }

    private void initData() {
        if(titleStr != null){
            tvTitle.setText(titleStr);
        }

        if (dataBeans != null) {

            resultTableLayout.removeAllViews();

            TableLayout.LayoutParams tRowParams = new TableLayout.LayoutParams(-1, -1);
            tRowParams.setMargins(0, 0, 0, 0);

            for (int i = 0; i < dataBeans.size(); ++i) {
                TableRow tRow = new TableRow(mContext);
                tRow.setLayoutParams(tRowParams);
                tRow.setBackgroundResource(R.drawable.table_frame_gray);
                resultTableLayout.addView(tRow);

                tRow.addView(getResultLine(dataBeans.get(i).getModeEyeStr2(), 0xff666666, 25, wid / 5, 90));
                tRow.addView(getResultLine("总数：" + dataBeans.get(i).getTotal(), 0xff03a64a, 25, wid * 2 / 5, 90));
                tRow.addView(getResultLine("正确率：" + dataBeans.get(i).getPercent() + "%", 0xff03a64a, 25, wid * 2 / 5, 90));
                tRow.setBackgroundColor(0xFFFFFF);
            }
        }
    }

    private View getResultLine(String title, int color, int size, int width, int height) {
        LinearLayout layout = new LinearLayout(mContext);
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(width, height);
        layout.setLayoutParams(trParams);
        layout.setPadding(0, 0, 0, 0);

        if (null != title) {
            TextView textView = new TextView(mContext);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            textView.setText(title);
            textView.setTextSize(size);
            textView.setBackgroundResource(R.drawable.table_frame_gray);
            textView.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            textView.setTextColor(color);
            layout.addView(textView);
        }

        return layout;
    }

    public void setTitle(String title){
        titleStr = title;
    }

    public void setWid(int w){
        this.wid = w;
    }

    /**
     * 内容，行，列
     * **/
    public void setResult(List<DataBean> dataBeans) {
        this.dataBeans = dataBeans;
    }

}
