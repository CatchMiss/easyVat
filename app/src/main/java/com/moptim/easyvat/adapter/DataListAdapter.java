package com.moptim.easyvat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.moptim.easyvat.R;
import com.moptim.easyvat.mode.DataBean;
import com.moptim.easyvat.mode.DataListBean;
import com.moptim.easyvat.mode.UserBean;
import com.moptim.easyvat.mode.UserListBean;

import java.util.ArrayList;
import java.util.List;

public class DataListAdapter extends BaseAdapter {

    List<DataListBean> dataListBeans;

    public DataListAdapter(ArrayList<DataListBean> beans){
        dataListBeans = beans;
    }

    @Override
    public int getCount() {
        return dataListBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_list, viewGroup, false);

            viewHolder = new ViewHolder();
            viewHolder.date = view.findViewById(R.id.item_data_data);
            viewHolder.name = view.findViewById(R.id.item_data_name);
            viewHolder.total = view.findViewById(R.id.item_data_total);
            viewHolder.correct = view.findViewById(R.id.item_data_correct);
            viewHolder.timeMode = view.findViewById(R.id.item_data_time_mode);
            viewHolder.holeMode = view.findViewById(R.id.item_data_hole_mode);
            viewHolder.eyeMode = view.findViewById(R.id.item_data_eye_mode);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        DataListBean bean = dataListBeans.get(i);
        viewHolder.date.setText(bean.dataBean.getDate());
        viewHolder.name.setText(bean.name);
        viewHolder.total.setText(bean.dataBean.getTotal() + "/2");
        viewHolder.correct.setText(bean.dataBean.getPercent() + "%");
        viewHolder.timeMode.setText(bean.dataBean.getModeTimeStr());
        viewHolder.holeMode.setText(bean.dataBean.getModeHoleStr());
        viewHolder.eyeMode.setText(bean.dataBean.getModeEyeStr());

        return view;
    }

    static class ViewHolder {
        TextView date;
        TextView name;
        TextView total;
        TextView correct;
        TextView timeMode;
        TextView holeMode;
        TextView eyeMode;
    }
}
