package com.moptim.easyvat.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.moptim.easyvat.R;
import com.moptim.easyvat.ble.BLEModel;
import com.moptim.easyvat.ble.EventUtil;
import com.moptim.easyvat.ble.EventWhat;
import com.moptim.easyvat.mode.DataBean;
import com.moptim.easyvat.mode.KeyDao;
import com.moptim.easyvat.mode.UserBean;
import com.moptim.easyvat.utils.CollectionUtils;
import com.moptim.easyvat.utils.Constant;
import com.moptim.easyvat.utils.MptDBHelper;
import com.moptim.easyvat.utils.SharedPreferencesUtil;
import com.moptim.easyvat.utils.SoundManager;
import com.moptim.easyvat.utils.Sp;
import com.moptim.easyvat.view.SimpleResultDialog;
import com.moptim.easyvat.view.TipsDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class VisualActivity extends AppCompatActivity {

    private static final String TAG = VisualActivity.class.getSimpleName();

    private Context mContext;

    private ImageView imageViewE;

    //上下左右视标及对应方向
    private List<Pair<Integer,Bitmap>> imageList = new ArrayList<>(4);
    private int imageIndex = -1;

    //测试数据
    private List<DataBean> dataBeans;
    private int dataBeanIndex = 0;

    private TextView tvTimes;
    private int curMsgTime;

    private static final int MSG_UPDATE = 0x40;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE) {
                mHandler.removeMessages(MSG_UPDATE);
                if (curMsgTime > 0) {
                    curMsgTime--;
                    tvTimes.setText(String.format(Locale.getDefault(),"%d", curMsgTime));
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE, 1000);
                } else {
                    curMsgTime = 0;
                    dataBeanIndex++;
                    startVisual();
                }
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventUtil eventUtil) {
        Log.i(TAG, "onEventMainThread: " + eventUtil.what + ", " + eventUtil.obj.deviceName);
        BLEModel model = eventUtil.obj;
        if(eventUtil.what == EventWhat.STATUS_READ_DATA && model != null){
            if(dataBeanIndex < dataBeans.size()){
                checkResult(Integer.parseInt(model.readData));
            }
        } else if(eventUtil.what == EventWhat.STATUS_DISCONNECT){
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual);
        mContext = this;

        EventBus.getDefault().register(this);

        imageViewE = findViewById(R.id.image_e);
        tvTimes = findViewById(R.id.textView_title);
        TextView tvTips = findViewById(R.id.textView_tips);

        //
        dataBeans = (List<DataBean>) getIntent().getSerializableExtra(Constant.DATA_BEAN);

        if(dataBeans.size() > 0){
            UserBean userBean = MptDBHelper.getInstance(mContext).getUser(dataBeans.get(0));
            tvTips.setText("No." + userBean.getNumber() + ", Name:" + userBean.getName());
        }

        //
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.i(TAG, "onCreate: " + dm.widthPixels + ", " + dm.heightPixels);

        //initTipDialog(dm.widthPixels, dm.heightPixels);

        float realSize = (float) SharedPreferencesUtil.getParams(mContext, Sp.IMAGE_W, 1.05f);
        float screenSize = (float) SharedPreferencesUtil.getParams(mContext, Sp.SCREEN_REAL_W, 86.0f);
        double scl = realSize / screenSize;
        //视标大小
        int imagePix = (int) Math.round(dm.widthPixels * scl);

        //
        RelativeLayout.LayoutParams imgLayout = (android.widget.RelativeLayout.LayoutParams) imageViewE.getLayoutParams();
        imgLayout.width = imagePix;
        imgLayout.height = imagePix;
        imageViewE.setLayoutParams(imgLayout);

        initImageList();

        changeImageView();

        TipsDialog dialog = new TipsDialog(mContext);
        dialog.setDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                DataBean dataBean = dataBeans.get(dataBeanIndex);

                String data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                dataBean.setDate(data);

                curMsgTime = dataBean.getModeTimeData();
                mHandler.sendEmptyMessage(MSG_UPDATE);
            }
        });
        if (dataBeans.get(dataBeanIndex).getModeEye() == Constant.MODE_OD) {
            dialog.showTipDialog("右眼");
            SoundManager.getInstance().playSound(R.raw.od);
        } else if (dataBeans.get(dataBeanIndex).getModeEye() == Constant.MODE_OS) {
            dialog.showTipDialog("左眼");
            SoundManager.getInstance().playSound(R.raw.os);
        } else if (dataBeans.get(dataBeanIndex).getModeEye() == Constant.MODE_OU) {
            dialog.showTipDialog("双眼");
            SoundManager.getInstance().playSound(R.raw.ou);
        }
    }

    private void startVisual(){
        if(dataBeanIndex >= dataBeans.size()){
            SoundManager.getInstance().playSound(R.raw.result_show);

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);

            SimpleResultDialog dialog = new SimpleResultDialog(mContext);
            dialog.setTitle("视力得分");
            dialog.setResult(dataBeans);
            dialog.setWid(dm.widthPixels - 2 * 60);
            dialog.setOnCloseListener(new SimpleResultDialog.OnCloseClicked() {
                @Override
                public void OnClicked() {
                    Log.i(TAG, "ResultDialog close---");
                    finish();
                }
            });
            dialog.show();

            MptDBHelper dbHelper = MptDBHelper.getInstance(mContext);
            for(DataBean dataBean : dataBeans){
                dbHelper.saveData(dataBean);
            }

        } else {
            changeImageView();

            TipsDialog dialog = new TipsDialog(mContext);
            dialog.setDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    DataBean dataBean = dataBeans.get(dataBeanIndex);

                    String data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    dataBean.setDate(data);

                    curMsgTime = dataBean.getModeTimeData();
                    mHandler.sendEmptyMessage(MSG_UPDATE);
                }
            });
            if (dataBeans.get(dataBeanIndex).getModeEye() == Constant.MODE_OD) {
                dialog.showTipDialog("右眼");
                SoundManager.getInstance().playSound(R.raw.od);
            } else if (dataBeans.get(dataBeanIndex).getModeEye() == Constant.MODE_OS) {
                dialog.showTipDialog("左眼");
                SoundManager.getInstance().playSound(R.raw.os);
            } else if (dataBeans.get(dataBeanIndex).getModeEye() == Constant.MODE_OU) {
                dialog.showTipDialog("双眼");
                SoundManager.getInstance().playSound(R.raw.ou);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mHandler.removeMessages(MSG_UPDATE);
        Log.i(TAG, "onDestroy");
    }


    /**
     * 切换视标图片
     */
    private void changeImageView() {
        int rand = new Random().nextInt(4);
        while (rand == imageIndex) {
            rand = new Random().nextInt(4);
        }
        imageIndex = rand;

        imageViewE.setImageBitmap(imageList.get(imageIndex).second);
    }

    private void initImageList(){
        Bitmap down = CollectionUtils.getImageFromAssetsFile(mContext, Constant.E_DOWN);
        Pair dPair = new Pair(KeyDao.DOWN, down);
        imageList.add(dPair);

        Bitmap left = CollectionUtils.getImageFromAssetsFile(mContext, Constant.E_LEFT);
        Pair lPair = new Pair(KeyDao.LEFT, left);
        imageList.add(lPair);

        Bitmap right = CollectionUtils.getImageFromAssetsFile(mContext, Constant.E_RIGHT);
        Pair rPair = new Pair(KeyDao.RIGHT, right);
        imageList.add(rPair);

        Bitmap up = CollectionUtils.getImageFromAssetsFile(mContext, Constant.E_UP);
        Pair uPair = new Pair(KeyDao.UP, up);
        imageList.add(uPair);
    }

    private void checkResult(int keyCode){
        if (keyCode != KeyDao.DOWN
                && keyCode != KeyDao.UP
                && keyCode != KeyDao.LEFT
                && keyCode != KeyDao.RIGHT
                && keyCode != KeyDao.CONFIRM
                && keyCode != KeyDao.CANCEL) {
            return;
        }

        if(keyCode == KeyDao.UP){
            SoundManager.getInstance().playSound(R.raw.up);
        } else if(keyCode == KeyDao.DOWN){
            SoundManager.getInstance().playSound(R.raw.down);
        }else if(keyCode == KeyDao.LEFT){
            SoundManager.getInstance().playSound(R.raw.left);
        }else if(keyCode == KeyDao.RIGHT){
            SoundManager.getInstance().playSound(R.raw.right);
        }

        DataBean bean = dataBeans.get(dataBeanIndex);

        // 判断对错
        int correct = bean.getCorrect();
        int total = bean.getTotal();
        if (imageList.get(imageIndex).first == keyCode){
            //正确
            correct++;
        }
        total++;

        // 统计对错
        bean.setCorrect(correct);
        bean.setTotal(total);

        //切换视标
        changeImageView();
    }
}
