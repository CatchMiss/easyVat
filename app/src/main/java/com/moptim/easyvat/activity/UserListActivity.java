package com.moptim.easyvat.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.moptim.easyvat.R;
import com.moptim.easyvat.adapter.UserListAdapter;
import com.moptim.easyvat.mode.DataBean;
import com.moptim.easyvat.mode.UserListBean;
import com.moptim.easyvat.mode.UserBean;
import com.moptim.easyvat.utils.MptDBHelper;
import com.moptim.easyvat.view.UserDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class UserListActivity extends AppCompatActivity {

    private static final String TAG = UserListActivity.class.getSimpleName();

    private Context mContext;

    private int selectorMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        mContext = this;

        final ArrayList<UserListBean> data = new ArrayList<>();
        final MptDBHelper dbHelper = MptDBHelper.getInstance(mContext);

        ArrayList<UserBean> userBeans = dbHelper.queryUser(null, null, null);
        for (UserBean userBean : userBeans) {
            ArrayList<DataBean> dataBeans = dbHelper.queryData(null, MptDBHelper.NUMBER + "=?", new String[]{userBean.getNumber()});
            UserListBean userListBean = new UserListBean(userBean, dataBeans);
            data.add(userListBean);
        }

        final UserListAdapter adapter = new UserListAdapter(data);

        final ExpandableListView listView = findViewById(R.id.expend_list);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final UserListBean bean = data.get(i);
                final UserDialog dialog = new UserDialog(mContext);
                dialog.setContent(bean.user);
                dialog.setNoOnclickListener("取消", new UserDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        dialog.dismiss();
                    }
                });
                dialog.setYesOnclickListener("保存", new UserDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        bean.user = dialog.getContent();
                        dbHelper.saveUser(bean.user);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
        });

        //查询
        final LinearLayout lySelectorCondition = findViewById(R.id.ly_user_list_selector_condition);
        final EditText editTextSelector = findViewById(R.id.edit_user_list_select);

        Spinner spinner = findViewById(R.id.sp_user_list_selector);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String result = adapterView.getItemAtPosition(i).toString();
                Log.i(TAG, "onItemSelected: " + result);
                selectorMode = i;
                editTextSelector.setText("");

                switch (i){
                    case 0://全部
                        data.clear();
                        lySelectorCondition.setVisibility(View.INVISIBLE);
                        ArrayList<UserBean> userBeans = dbHelper.queryUser(null, null, null);
                        for (UserBean userBean : userBeans) {
                            ArrayList<DataBean> dataBeans = dbHelper.queryData(null, MptDBHelper.NUMBER + "=?", new String[]{userBean.getNumber()});
                            UserListBean userListBean = new UserListBean(userBean, dataBeans);
                            data.add(userListBean);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case 1://编号
                        lySelectorCondition.setVisibility(View.VISIBLE);
                        editTextSelector.setHint("请输入查询编号");
                        break;
                    case 2://姓名
                        lySelectorCondition.setVisibility(View.VISIBLE);
                        editTextSelector.setHint("请输入查询姓名");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mContext, "NothingSelected", Toast.LENGTH_SHORT).show();
            }
        });

        //编号，姓名查询
        editTextSelector.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(selectorMode == 1){
                    //number
                    if(charSequence.length() > 0){
                        data.clear();

                        String selector = charSequence.toString();

                        ArrayList<UserBean> userBeans = dbHelper.queryUser(null, MptDBHelper.NUMBER + " like ? ", new String[]{"%" + selector + "%"});
                        for (UserBean userBean : userBeans) {
                            ArrayList<DataBean> dataBeans = dbHelper.queryData(null, MptDBHelper.NUMBER + "=?", new String[]{userBean.getNumber()});
                            UserListBean userListBean = new UserListBean(userBean, dataBeans);
                            data.add(userListBean);
                        }

                        adapter.notifyDataSetChanged();
                    }
                } else if(selectorMode == 2){
                    //name
                    if(charSequence.length() > 0){
                        data.clear();

                        String selector = charSequence.toString();

                        ArrayList<UserBean> userBeans = dbHelper.queryUser(null, MptDBHelper.NAME + " like ? ", new String[]{"%" + selector + "%"});
                        for (UserBean userBean : userBeans) {
                            ArrayList<DataBean> dataBeans = dbHelper.queryData(null, MptDBHelper.NUMBER + "=?", new String[]{userBean.getNumber()});
                            UserListBean userListBean = new UserListBean(userBean, dataBeans);
                            data.add(userListBean);
                        }

                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}
