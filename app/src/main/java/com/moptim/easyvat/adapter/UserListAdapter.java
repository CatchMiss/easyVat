package com.moptim.easyvat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.moptim.easyvat.R;
import com.moptim.easyvat.mode.DataBean;
import com.moptim.easyvat.mode.UserListBean;
import com.moptim.easyvat.mode.UserBean;

import java.util.ArrayList;

public class UserListAdapter extends BaseExpandableListAdapter {

    /**
     *              data
     *          ---------------------
     *          |          ---------------
     *          |         |      |__data_
     *          | manager | USER |__data_
     *          |         |_____ |__data__
     *          |
     *          |          ---------------
     *          |         |      |__data_
     *          | manager | USER |__data_
     *          |         |_____ |__data__
     *
     * **/
    ArrayList<UserListBean> userListBeans;

    public UserListAdapter(ArrayList<UserListBean> data){
        this.userListBeans = data;
    }

    @Override
    public int getGroupCount() {
        return userListBeans.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return userListBeans.get(i).data.size();
    }

    @Override
    public Object getGroup(int i) {
        return userListBeans.get(i).user;
    }

    @Override
    public Object getChild(int i, int i1) {
        return userListBeans.get(i).data.get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentViewHolder parentViewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list_parent, parent, false);
            parentViewHolder = new ParentViewHolder();
            parentViewHolder.number = convertView.findViewById(R.id.item_user_list_number);
            parentViewHolder.name = convertView.findViewById(R.id.item_user_list_name);
            parentViewHolder.gender = convertView.findViewById(R.id.item_user_list_gender);
            parentViewHolder.age = convertView.findViewById(R.id.item_user_list_age);
            parentViewHolder.school = convertView.findViewById(R.id.item_user_list_school);
            parentViewHolder.grade = convertView.findViewById(R.id.item_user_list_grade);
            parentViewHolder.mClass = convertView.findViewById(R.id.item_user_list_class);

            convertView.setTag(parentViewHolder);
        }else {
            parentViewHolder = (ParentViewHolder) convertView.getTag();
        }

        UserBean userBean = userListBeans.get(i).user;
        parentViewHolder.number.setText(userBean.getNumber());
        parentViewHolder.name.setText(userBean.getName());
        parentViewHolder.gender.setText(userBean.getGender());
        parentViewHolder.age.setText(userBean.getAge() + "");
        parentViewHolder.school.setText(userBean.getSchool());
        parentViewHolder.grade.setText(userBean.getGrade());
        parentViewHolder.mClass.setText(userBean.getUserClass());

        return convertView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildViewHolder viewHolder;
        if (view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_list_child, viewGroup, false);

            viewHolder = new ChildViewHolder();
            viewHolder.date = view.findViewById(R.id.item_user_list_data);
            viewHolder.total = view.findViewById(R.id.item_user_list_total);
            viewHolder.correct = view.findViewById(R.id.item_user_list_correct);
            viewHolder.timeMode = view.findViewById(R.id.item_user_list_time_mode);
            viewHolder.holeMode = view.findViewById(R.id.item_user_list_hole_mode);
            viewHolder.eyeMode = view.findViewById(R.id.item_user_list_eye_mode);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) view.getTag();
        }

        DataBean dataBean = userListBeans.get(i).data.get(i1);
        viewHolder.date.setText(dataBean.getDate());
        viewHolder.total.setText(dataBean.getTotal() + "/2");
        viewHolder.correct.setText(dataBean.getPercent() + "%");
        viewHolder.timeMode.setText(dataBean.getModeTimeStr());
        viewHolder.holeMode.setText(dataBean.getModeHoleStr());
        viewHolder.eyeMode.setText(dataBean.getModeEyeStr());

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    static class ParentViewHolder {
        TextView number;
        TextView name;
        TextView gender;
        TextView age;
        TextView school;
        TextView grade;
        TextView mClass;
    }

    static class ChildViewHolder {
        TextView date;
        TextView total;
        TextView correct;
        TextView timeMode;
        TextView holeMode;
        TextView eyeMode;
    }
}
