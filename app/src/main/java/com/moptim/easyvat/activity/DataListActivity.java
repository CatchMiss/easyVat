package com.moptim.easyvat.activity;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.moptim.easyvat.R;
import com.moptim.easyvat.adapter.DataListAdapter;
import com.moptim.easyvat.mode.DataBean;
import com.moptim.easyvat.mode.DataListBean;
import com.moptim.easyvat.mode.UserBean;
import com.moptim.easyvat.utils.MptDBHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class DataListActivity extends AppCompatActivity {

    private static final String TAG = DataListActivity.class.getSimpleName();

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_list);
        mContext = this;

        final ArrayList<DataListBean> dataListBeans = new ArrayList<>();
        final MptDBHelper dbHelper = MptDBHelper.getInstance(mContext);

        ArrayList<DataBean> dataBeans = dbHelper.queryData(null, null, null);
        for(DataBean bean : dataBeans){
            UserBean userBean = dbHelper.getUser(bean);
            DataListBean dataListBean = new DataListBean(userBean.getName(), bean);
            dataListBeans.add(dataListBean);
        }

        final DataListAdapter listAdapter = new DataListAdapter(dataListBeans);
        final ListView listView = findViewById(R.id.lv_data_list);
        listView.setAdapter(listAdapter);

        //查询
        final LinearLayout lySelectorCondition = findViewById(R.id.ly_data_list_selector_condition);
        final EditText editTextSelector = findViewById(R.id.edit_data_list_select);
        final LinearLayout lyTimeSelector = findViewById(R.id.ly_data_list_time_selector);

        Spinner spinner = findViewById(R.id.sp_data_list_selector);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String result = adapterView.getItemAtPosition(i).toString();
                Log.i(TAG, "onItemSelected: " + result);
                editTextSelector.setText("");

                switch (i){
                    case 0://全部
                        lySelectorCondition.setVisibility(View.INVISIBLE);

                        dataListBeans.clear();
                        ArrayList<DataBean> dataBeans = dbHelper.queryData(null, null, null);
                        for(DataBean bean : dataBeans){
                            UserBean userBean = dbHelper.getUser(bean);
                            DataListBean dataListBean = new DataListBean(userBean.getName(), bean);
                            dataListBeans.add(dataListBean);
                        }
                        listAdapter.notifyDataSetChanged();
                        break;
                    case 1://姓名
                        lySelectorCondition.setVisibility(View.VISIBLE);

                        lyTimeSelector.setVisibility(View.GONE);
                        editTextSelector.setVisibility(View.VISIBLE);
                        editTextSelector.setHint("请输入查询姓名");

                        break;
                    case 2://时间
                        lySelectorCondition.setVisibility(View.VISIBLE);

                        lyTimeSelector.setVisibility(View.VISIBLE);
                        editTextSelector.setVisibility(View.GONE);

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mContext, "NothingSelected", Toast.LENGTH_SHORT).show();
            }
        });

        //姓名查询
        editTextSelector.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0){
                    dataListBeans.clear();

                    String selector = charSequence.toString();

                    ArrayList<UserBean> userBeans = dbHelper.queryUser(null, MptDBHelper.NAME + " like ? ", new String[]{"%" + selector + "%"});
                    for (UserBean bean : userBeans){
                        ArrayList<DataBean> dataBeans = dbHelper.queryData(null, MptDBHelper.NUMBER + "=?", new String[]{bean.getNumber()});
                        for(DataBean data : dataBeans){
                            DataListBean dataListBean = new DataListBean(bean.getName(), data);
                            dataListBeans.add(dataListBean);
                        }
                    }

                    listAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //时间查询
        final TextView tvStart = findViewById(R.id.tv_data_list_start_time);
        final TextView tvEnd = findViewById(R.id.tv_data_list_end_time);
        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPick((TextView) view);
            }
        });
        tvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPick((TextView) view);
            }
        });

        findViewById(R.id.bt_data_list_query_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String start = tvStart.getText().toString().trim();
                String end = tvEnd.getText().toString().trim();
                String selection = MptDBHelper.DATE + " > ? AND " + MptDBHelper.DATE + " < ? ";

                dataListBeans.clear();

                ArrayList<DataBean> dataBeans = dbHelper.queryData(null, selection, new String[]{start, end});
                Log.i(TAG, "query: " + selection + " [ " + start + " -- " + end + " ] size:" + dataBeans.size());

                for(DataBean bean : dataBeans){
                    UserBean userBean = dbHelper.getUser(bean);
                    DataListBean dataListBean = new DataListBean(userBean.getName(), bean);
                    dataListBeans.add(dataListBean);
                }

                listAdapter.notifyDataSetChanged();

            }
        });
    }

    private void showDialogPick(final TextView timeText) {
        final StringBuffer time = new StringBuffer();

        //获取Calendar对象，用于获取当前时间
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        final TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            //选择完时间后会调用该回调函数
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String timeData = String.format(" %02d:%02d:00", hourOfDay, minute);
                time.append(timeData);
                timeText.setText(time);
            }
        }, hour, minute, true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            //选择完日期后会调用该回调函数
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //因为monthOfYear会比实际月份少一月所以这边要加1
                String yearData = String.format("%d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
                time.append(yearData);
                //选择完日期后弹出选择时间对话框
                timePickerDialog.show();
            }
        }, year, month, day);

        //弹出选择日期对话框
        datePickerDialog.show();
    }
}
