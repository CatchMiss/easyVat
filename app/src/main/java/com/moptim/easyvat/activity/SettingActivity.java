package com.moptim.easyvat.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.WriterException;
import com.moptim.easyvat.R;
import com.moptim.easyvat.mode.UserBean;
import com.moptim.easyvat.utils.CollectionUtils;
import com.moptim.easyvat.utils.FileUtils;
import com.moptim.easyvat.utils.MptDBHelper;
import com.moptim.easyvat.utils.SharedPreferencesUtil;
import com.moptim.easyvat.utils.Sp;
import com.moptim.easyvat.utils.XlsUtil;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mContext = this;
        
        //裸眼和矫正
        initHoleMode();

        //左，右，双
        initEyeMode();

        //视标大小
        initVisualSize();

        //开始模式
        initStartMode();

        //数据
        iniXlsData();
    }

    private void initStartMode() {
        final RelativeLayout qrCode = findViewById(R.id.rl_start_qrCode);
        final RelativeLayout list = findViewById(R.id.rl_start_list);

        int mode = (int) SharedPreferencesUtil.getParams(mContext, Sp.START_MODE, 0);
        if(mode == 0){
            qrCode.setBackground(getDrawable(R.drawable.shape_n9));
            list.setBackground(getDrawable(R.drawable.shape_n_02));
        } else {
            qrCode.setBackground(getDrawable(R.drawable.shape_n_02));
            list.setBackground(getDrawable(R.drawable.shape_n7));
        }

        qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesUtil.setParams(mContext, Sp.START_MODE, 0);
                qrCode.setBackground(getDrawable(R.drawable.shape_n9));
                list.setBackground(getDrawable(R.drawable.shape_n_02));
            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesUtil.setParams(mContext, Sp.START_MODE, 1);
                qrCode.setBackground(getDrawable(R.drawable.shape_n_02));
                list.setBackground(getDrawable(R.drawable.shape_n7));
            }
        });
    }

    private void iniXlsData() {
        //读取
        findViewById(R.id.rl_read_xls).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XlsUtil.read(mContext);
                Toast.makeText(mContext, "导入完成", Toast.LENGTH_SHORT).show();
            }
        });

        //写入
        findViewById(R.id.rl_write_xls).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XlsUtil.write(mContext);
                Toast.makeText(mContext, "导出完成", Toast.LENGTH_SHORT).show();
            }
        });

        //二维码
        findViewById(R.id.rl_write_qrCode).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                MptDBHelper dbHelper = MptDBHelper.getInstance(mContext);
                final ArrayList<UserBean> userBeans = dbHelper.queryUser(null, null, null);
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

                final ProgressDialog progressDialog = new ProgressDialog(mContext);

                new AsyncTask<Void, Integer, Void>(){

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();

                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.setMax(userBeans.size());
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            int progress = 0;
                            for(UserBean user : userBeans) {
                                Bitmap bitmap = CollectionUtils.Create2DCode(user.getNumber());
                                String fileName = user.getName() + "_" + user.getNumber() + ".PNG";
                                FileUtils.saveBitmap(bitmap, fileName);
                                publishProgress(progress++);
                                Thread.sleep(50);
                            }
                        } catch (WriterException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        super.onProgressUpdate(values);
                        progressDialog.setProgress(values[0]);
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        progressDialog.dismiss();
                        Toast.makeText(mContext, "成功生成二维码", Toast.LENGTH_SHORT).show();
                    }

                }.execute();
            }
        });

        findViewById(R.id.bt_delete_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("确定清空历史记录？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XlsUtil.clear(mContext, "HistoryBack");
                        Toast.makeText(mContext, "已清空历史数据", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
    }

    private void initVisualSize() {
        final TextView tvSize = findViewById(R.id.tv_size);
        tvSize.setText(String.format("%smm", SharedPreferencesUtil.getParams(mContext, Sp.IMAGE_W, 1.05f)));

        findViewById(R.id.rl_size).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(mContext);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("请输入管理员密码");
                builder.setView(editText);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String trim = editText.getText().toString().trim();
                        if("easyAdmin".equals(trim)){
                            final EditText editText = new EditText(mContext);
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("请输入视标大小");
                            builder.setView(editText);
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String trim = editText.getText().toString().trim();
                                    if(trim.length() > 0){
                                        try {
                                            float size = Float.valueOf(trim);
                                            SharedPreferencesUtil.setParams(mContext, Sp.IMAGE_W, size);
                                            tvSize.setText(String.format("%smm", size));
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void initHoleMode() {
        /*
          * 测试类型，单选，裸眼和矫正
          */
        final RelativeLayout bakeEye = findViewById(R.id.rl_bakeEye);
        final RelativeLayout correction = findViewById(R.id.rl_correction);

        final Drawable resCheck = getDrawable(R.drawable.shape_n4);
        final Drawable resUnCheck = getDrawable(R.drawable.shape_n_02);

        int holeMode = (int) SharedPreferencesUtil.getParams(mContext, Sp.HOLE_MODE, 0);
        if(holeMode == 0){
            bakeEye.setBackground(resCheck);
            correction.setBackground(resUnCheck);
        }else{
            bakeEye.setBackground(resUnCheck);
            correction.setBackground(resCheck);
        }

        bakeEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesUtil.setParams(mContext, Sp.HOLE_MODE, 0);
                bakeEye.setBackground(resCheck);
                correction.setBackground(resUnCheck);
            }
        });

        correction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesUtil.setParams(mContext, Sp.HOLE_MODE, 1);
                bakeEye.setBackground(resUnCheck);
                correction.setBackground(resCheck);
            }
        });

    }

    private void initEyeMode() {
        /*
         *  双眼类型多选
         * */
        int od = (int) SharedPreferencesUtil.getParams(mContext, Sp.OD, 0);
        int os = (int) SharedPreferencesUtil.getParams(mContext, Sp.OS, 0);
        int ou = (int) SharedPreferencesUtil.getParams(mContext, Sp.OU, 0);
        if(od == 0 && os == 0 && ou == 0){
            od = 1;
            SharedPreferencesUtil.setParams(mContext, Sp.OD, od);
        }

        final RelativeLayout rlOd = findViewById(R.id.rl_od);
        final RelativeLayout rlOs = findViewById(R.id.rl_os);
        final RelativeLayout rlOu = findViewById(R.id.rl_ou);

        final Drawable resUnCheck = getDrawable(R.drawable.shape_n_02);
        final Drawable resOd = getDrawable(R.drawable.shape_n7);
        final Drawable resOs = getDrawable(R.drawable.shape_n9);
        final Drawable resOu = getDrawable(R.drawable.shape_n5);

        if(od == 0){
            rlOd.setBackground(resUnCheck);
        }else{
            rlOd.setBackground(resOd);
        }

        if(os == 0){
            rlOs.setBackground(resUnCheck);
        }else{
            rlOs.setBackground(resOs);
        }

        if(ou == 0){
            rlOu.setBackground(resUnCheck);
        }else{
            rlOu.setBackground(resOu);
        }

        rlOd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int od = (int) SharedPreferencesUtil.getParams(mContext, Sp.OD, 0);
                int os = (int) SharedPreferencesUtil.getParams(mContext, Sp.OS, 0);
                int ou = (int) SharedPreferencesUtil.getParams(mContext, Sp.OU, 0);

                od = (od == 0) ? 1 : 0;

                if(od == 0 && os == 0 && ou == 0){
                    Toast.makeText(mContext, "至少选择一种模式", Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferencesUtil.setParams(mContext, Sp.OD, od);
                    if(od == 0){
                        rlOd.setBackground(resUnCheck);
                    }else{
                        rlOd.setBackground(resOd);
                    }
                }

            }
        });

        rlOs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int od = (int) SharedPreferencesUtil.getParams(mContext, Sp.OD, 0);
                int os = (int) SharedPreferencesUtil.getParams(mContext, Sp.OS, 0);
                int ou = (int) SharedPreferencesUtil.getParams(mContext, Sp.OU, 0);

                os = (os == 0) ? 1 : 0;

                if(od == 0 && os == 0 && ou == 0){
                    Toast.makeText(mContext, "至少选择一种模式", Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferencesUtil.setParams(mContext, Sp.OS, os);
                    if(os == 0){
                        rlOs.setBackground(resUnCheck);
                    }else{
                        rlOs.setBackground(resOs);
                    }
                }

            }
        });

        rlOu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int od = (int) SharedPreferencesUtil.getParams(mContext, Sp.OD, 0);
                int os = (int) SharedPreferencesUtil.getParams(mContext, Sp.OS, 0);
                int ou = (int) SharedPreferencesUtil.getParams(mContext, Sp.OU, 0);

                ou = (ou == 0) ? 1 : 0;

                if(od == 0 && os == 0 && ou == 0){
                    Toast.makeText(mContext, "至少选择一种模式", Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferencesUtil.setParams(mContext, Sp.OU, ou);
                    if(ou == 0){
                        rlOu.setBackground(resUnCheck);
                    }else{
                        rlOu.setBackground(resOu);
                    }
                }

            }
        });
    }
}
