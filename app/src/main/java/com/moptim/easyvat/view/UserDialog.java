package com.moptim.easyvat.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.moptim.easyvat.R;
import com.moptim.easyvat.mode.UserBean;

public class UserDialog extends Dialog {

    private Button confirm;//确定按钮
    private Button cancel;//取消按钮
    private String yesStr, noStr;

    private EditText etNumber;
    private EditText etName;
    private EditText etGender;
    private EditText etAge;
    private EditText etSchool;
    private EditText etGrade;
    private EditText etClass;
    private UserBean userBean;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    public interface onYesOnclickListener {
        void onYesClick();
    }

    public interface onNoOnclickListener {
        void onNoClick();
    }

    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    public void setContent(UserBean bean){
        this.userBean = bean;
    }

    public UserBean getContent(){
        return userBean;
    }

    public UserDialog(Context context) {
        super(context, R.style.Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_user);
        setCanceledOnTouchOutside(false);

        initUI();

        initData();
    }

    private void initData() {
        if(noStr != null){
            cancel.setText(noStr);
        }

        if(yesStr != null){
            confirm.setText(yesStr);
        }

        if(userBean != null){
            etNumber.setText(userBean.getNumber());
            etName.setText(userBean.getName());
            etGender.setText(userBean.getGender());
            etAge.setText(userBean.getAge() + "");
            etSchool.setText(userBean.getSchool());
            etGrade.setText(userBean.getGrade());
            etClass.setText(userBean.getUserClass());
        }
    }

    private void initUI() {
        confirm = findViewById(R.id.confirm);
        cancel = findViewById(R.id.cancel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userBean.setName(etName.getText().toString().trim());
                userBean.setGender(etGender.getText().toString().trim());
                if(etAge.getText().toString().trim().length() > 0){
                    try {
                        userBean.setAge(Integer.parseInt(etAge.getText().toString().trim()));
                    } catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                }
                userBean.setSchool(etSchool.getText().toString().trim());
                userBean.setGrade(etGrade.getText().toString().trim());
                userBean.setUserClass(etClass.getText().toString().trim());

                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });

        //设置取消按钮被点击后，向外界提供监听
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });

        etNumber = findViewById(R.id.et_user_dialog_number);
        etName = findViewById(R.id.et_user_dialog_name);
        etGender = findViewById(R.id.et_user_dialog_gender);
        etAge = findViewById(R.id.et_user_dialog_age);
        etSchool = findViewById(R.id.et_user_dialog_school);
        etGrade = findViewById(R.id.et_user_dialog_grade);
        etClass = findViewById(R.id.et_user_dialog_class);
    }
}
