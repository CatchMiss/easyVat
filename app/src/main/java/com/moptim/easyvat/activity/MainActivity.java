package com.moptim.easyvat.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.moptim.easyvat.R;
import com.moptim.easyvat.ble.BLEModel;
import com.moptim.easyvat.ble.BluetoothLeService;
import com.moptim.easyvat.ble.EventUtil;
import com.moptim.easyvat.ble.EventWhat;
import com.moptim.easyvat.mode.DataBean;
import com.moptim.easyvat.mode.KeyDao;
import com.moptim.easyvat.mode.UserBean;
import com.moptim.easyvat.utils.Constant;
import com.moptim.easyvat.utils.FileUtils;
import com.moptim.easyvat.utils.Md5Util;
import com.moptim.easyvat.utils.MptDBHelper;
import com.moptim.easyvat.utils.SharedPreferencesUtil;
import com.moptim.easyvat.utils.Sp;
import com.moptim.easyvat.zxing.activity.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Context mContext;

    private TextView bleStatus;
    private RelativeLayout bleMode;

    private List<DataBean> dataBeans = new ArrayList<>();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final EventUtil eventUtil) {
        Log.i(TAG, "onEventMainThread: " + eventUtil.what);
        final BLEModel model = eventUtil.obj;
        switch (eventUtil.what) {
            case STATUS_DISCOVER:
                if(model != null && Constant.BLE_NAME.equals(model.deviceName)){
                    //找到设备
                    bleStatus.setText("发现蓝牙手柄");
                    bleMode.setBackground(getDrawable(R.drawable.shape_n8));

                    eventUtil.what = EventWhat.S_STOP_SCAN;
                    EventBus.getDefault().post(eventUtil);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.autoConnect = 5;
                            eventUtil.what = EventWhat.S_CONNECT;
                            EventBus.getDefault().post(eventUtil);
                        }
                    }, 1000);
                }
                break;
            case STATUS_CONNECT:
                //设备已连接
                bleStatus.setText("手柄已连接");
                bleMode.setBackground(getDrawable(R.drawable.shape_n4));
                break;
            case STATUS_DISCONNECT:
                if(model != null){
                    //设备断开连接
                    bleStatus.setText("未连接");
                    bleMode.setBackground(getDrawable(R.drawable.shape_n8));

                    eventUtil.what = EventWhat.S_AUTO_CONNECT;
                    EventBus.getDefault().post(eventUtil);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        EventBus.getDefault().register(this);

        initKey();

        initView();

        startService(new Intent(MainActivity.this, BluetoothLeService.class));

        checkKey();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        stopService(new Intent(MainActivity.this, BluetoothLeService.class));
        super.onDestroy();
    }

    private void checkKey() {
        String read = FileUtils.readFile("key.txt");
        final String ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        String md5 = Md5Util.getSaltMD5(ANDROID_ID);
        if(!md5.equals(read)){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("警告");
            builder.setMessage("软件未注册，ID:" + ANDROID_ID);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FileUtils.saveFile(ANDROID_ID, "register.txt", false);
                    dialogInterface.dismiss();
                    System.exit(0);
                }
            });
            AlertDialog dialog = builder.create();

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();
        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventUtil eventUtil = new EventUtil();
                    eventUtil.what = EventWhat.S_START_SCAN;
                    EventBus.getDefault().post(eventUtil);
                }
            }, 500);
        }
    }

    private void initKey() {
        KeyDao.UP = (int) SharedPreferencesUtil.getParams(mContext, Sp.KEY_UP, 1);
        KeyDao.DOWN = (int) SharedPreferencesUtil.getParams(mContext, Sp.KEY_DOWN, 2);
        KeyDao.LEFT = (int) SharedPreferencesUtil.getParams(mContext, Sp.KEY_LEFT, 3);
        KeyDao.RIGHT = (int) SharedPreferencesUtil.getParams(mContext, Sp.KEY_RIGHT, 4);
    }

    private void initView() {
        findViewById(R.id.rl_fast_mode).setOnClickListener(this);
        findViewById(R.id.rl_normal_mode).setOnClickListener(this);
        findViewById(R.id.rl_standard_mode).setOnClickListener(this);
        findViewById(R.id.rl_user).setOnClickListener(this);
        findViewById(R.id.rl_set).setOnClickListener(this);
        findViewById(R.id.rl_about).setOnClickListener(this);
        findViewById(R.id.rl_data).setOnClickListener(this);
        findViewById(R.id.ble_mode).setOnClickListener(this);

        bleStatus = findViewById(R.id.ble_status);
        bleStatus.setText("未发现蓝牙手柄");

        bleMode = findViewById(R.id.ble_mode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_user:
                startActivity(new Intent(mContext, UserListActivity.class));
                break;
            case R.id.rl_about:
                startActivity(new Intent(mContext, AboutActivity.class));
                break;
            case R.id.rl_fast_mode:
                initBeans(Constant.MODE_TIME_FAST);
                startVisual();
                break;
            case R.id.rl_normal_mode:
                initBeans(Constant.MODE_TIME_NORMAL);
                startVisual();
                break;
            case R.id.rl_standard_mode:
                initBeans(Constant.MODE_TIME_STANDARD);
                startVisual();
                break;
            case R.id.rl_set:
                startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.rl_data:
                startActivity(new Intent(mContext, DataListActivity.class));
                break;
        }
    }

    private void startVisual() {
        int startMode = (int) SharedPreferencesUtil.getParams(mContext, Sp.START_MODE, 0);

        if (startMode == 0){
            startActivityForResult(new Intent(mContext, CaptureActivity.class), RESULT_CODE_USER_NUM);
        } else {
            MptDBHelper dbHelper = MptDBHelper.getInstance(mContext);
            ArrayList<UserBean> userBeans = dbHelper.queryUser(null, null, null);
            if(userBeans == null || userBeans.size() <= 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("请先导入数据");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
                return;
            }

            final String[] numbers = new String[userBeans.size()];
            String[] titles = new String[userBeans.size()];
            for (int i = 0; i < userBeans.size(); i++){
                numbers[i] = userBeans.get(i).getNumber();
                titles[i] = "姓名：" + userBeans.get(i).getName() + " 编号：" + userBeans.get(i).getNumber();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("请选择");
            builder.setItems(titles, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String result = numbers[i];
                    for(DataBean bean : dataBeans){
                        bean.setNumber(result);
                    }
                    Intent intent = new Intent(mContext, VisualActivity.class);
                    intent.putExtra(Constant.DATA_BEAN, (Serializable) dataBeans);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
    }

    private void initBeans(int modeTime){
        dataBeans.clear();

        //裸眼 or 矫正
        int hole = (int) SharedPreferencesUtil.getParams(mContext, Sp.HOLE_MODE, 0);

        //右眼
        int od = (int) SharedPreferencesUtil.getParams(mContext, Sp.OD, 0);
        if(od != 0){
            DataBean dataBean = new DataBean();
            dataBean.setModeTime(modeTime);
            dataBean.setModeHole(hole);
            dataBean.setModeEye(Constant.MODE_OD);
            dataBeans.add(dataBean);
        }

        //左眼
        int os = (int) SharedPreferencesUtil.getParams(mContext, Sp.OS, 0);
        if(os != 0){
            DataBean dataBean = new DataBean();
            dataBean.setModeTime(modeTime);
            dataBean.setModeHole(hole);
            dataBean.setModeEye(Constant.MODE_OS);
            dataBeans.add(dataBean);
        }

        //双眼
        int ou = (int) SharedPreferencesUtil.getParams(mContext, Sp.OU, 0);
        if(ou != 0){
            DataBean dataBean = new DataBean();
            dataBean.setModeTime(modeTime);
            dataBean.setModeHole(hole);
            dataBean.setModeEye(Constant.MODE_OU);
            dataBeans.add(dataBean);
        }
    }

    private static final int RESULT_CODE_USER_NUM = 1001;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE_USER_NUM){
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (null != extras) {
                    String result = extras.getString("result");

                    MptDBHelper dbHelper = MptDBHelper.getInstance(mContext);
                    ArrayList<UserBean> userBeans = dbHelper.queryUser(null, MptDBHelper.NUMBER + "=?", new String[]{result});
                    if(userBeans == null || userBeans.size() <= 0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("请先导入：" + result + " 信息");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                        return;
                    }

                    for(DataBean bean : dataBeans){
                        bean.setNumber(result);
                    }
                    Intent intent = new Intent(mContext, VisualActivity.class);
                    intent.putExtra(Constant.DATA_BEAN, (Serializable) dataBeans);
                    startActivity(intent);
                }
            }
        }

    }
}
