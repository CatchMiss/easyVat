package com.moptim.easyvat.utils;

import android.content.Context;
import android.util.Log;

import com.moptim.easyvat.mode.DataBean;
import com.moptim.easyvat.mode.UserBean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class XlsUtil {

    private static final String TAG = XlsUtil.class.getSimpleName();

    public static void write(Context context){

        String fileName = new SimpleDateFormat("导出数据_yyyyMMddHHmmss", Locale.getDefault()).format(new Date()) + ".xls";
        String filePath = FileUtils.app_path + fileName;
        XslWrite xls = new XslWrite(filePath);

        xls.addCell(0, 0, "时间");
        xls.addCell(0, 1, "姓名");
        xls.addCell(0, 2, "编号");
        xls.addCell(0, 3, "总数");
        xls.addCell(0, 4, "正确率");
        xls.addCell(0, 5, "模式");
        xls.addCell(0, 6, "眼别");
        xls.addCell(0, 7, "裸眼/矫正");

        MptDBHelper dbHelper = MptDBHelper.getInstance(context);
        ArrayList<UserBean> userBeans = dbHelper.queryUser(null, null, null);

        int row = 1;//行数
        for(int i = 0; i < userBeans.size(); i++){
            UserBean userBean = userBeans.get(i);
            ArrayList<DataBean> dataBeans = dbHelper.queryData(null, MptDBHelper.NUMBER + "=?", new String[]{userBean.getNumber()});
            //一行数据
            for (int j = 0; j < dataBeans.size(); j++){
                DataBean dataBean = dataBeans.get(j);
                Log.i(TAG, "write: " + dataBean.toString());

                xls.addCell(row, 0, dataBean.getDate());
                xls.addCell(row, 1, userBean.getName());
                xls.addCell(row, 2, userBean.getNumber());
                xls.addCell(row, 3, dataBean.getTotal() + "/2");
                xls.addCell(row, 4, dataBean.getPercent() + "%");
                xls.addCell(row, 5, dataBean.getModeTimeStr());
                xls.addCell(row, 6, dataBean.getModeEyeStr());
                xls.addCell(row, 7, dataBean.getModeHoleStr());
                row++;
            }
        }

        xls.write();
        xls.close();
    }

    public static void clear(Context context, String path){
        File file = new File(FileUtils.app_path + path);
        if(!file.exists()){
            boolean mkdir = file.mkdir();
            Log.i(TAG, "clear make dir: " + mkdir);
        }

        String fileName = new SimpleDateFormat("备份数据_yyyyMMddHHmmss", Locale.getDefault()).format(new Date()) + ".xls";
        String filePath = FileUtils.app_path + path + "/" + fileName;
        XslWrite xls = new XslWrite(filePath);

        xls.addCell(0, 0, "编号");
        xls.addCell(0, 1, "姓名");
        xls.addCell(0, 2, "时间");
        xls.addCell(0, 3, "总数");
        xls.addCell(0, 4, "正确率");
        xls.addCell(0, 5, "模式");
        xls.addCell(0, 6, "眼别");
        xls.addCell(0, 7, "裸眼/矫正");

        MptDBHelper dbHelper = MptDBHelper.getInstance(context);
        ArrayList<UserBean> userBeans = dbHelper.queryUser(null, null, null);

        int row = 1;
        for(int i = 0; i < userBeans.size(); i++){
            UserBean userBean = userBeans.get(i);
            xls.addCell(row, 0, userBean.getNumber());
            xls.addCell(row, 1, userBean.getName());

            ArrayList<DataBean> dataBeans = dbHelper.queryData(null, MptDBHelper.NUMBER + "=?", new String[]{userBean.getNumber()});
            for (int j = 0; j < dataBeans.size(); j++){
                DataBean dataBean = dataBeans.get(j);
                Log.i(TAG, "write: " + dataBean.toString());
                xls.addCell(row, 2, dataBean.getDate());
                xls.addCell(row, 3, dataBean.getTotal() + "/2");
                xls.addCell(row, 4, dataBean.getPercent() + "%");
                xls.addCell(row, 5, dataBean.getModeTimeStr());
                xls.addCell(row, 6, dataBean.getModeEyeStr());
                xls.addCell(row, 7, dataBean.getModeHoleStr());

                row++;
                dbHelper.deleteData(dataBean);
            }

            row++;
            dbHelper.deleteUser(userBean);
        }

        xls.write();
        xls.close();
    }

    public static void read(Context context){
        MptDBHelper dbHelper = MptDBHelper.getInstance(context);

        String filePath = FileUtils.app_path + "数据导入.xls";
        XslRead xls = new XslRead(filePath);
        xls.openSheet(0);

        int rows = xls.getRows();
        int cols = xls.getColumns();
        for(int i = 1; i < rows; i++){
            UserBean userBean = new UserBean();
            for (int j = 0; j < cols; j++){
                String value = xls.getCell(i, j);
                Log.i(TAG, "read: i=" + i + ", j=" + j + ", Value:" + value);
                switch (j){
                    case 0 :
                        userBean.setNumber(value);
                        break;
                    case 1:
                        userBean.setName(value);
                        break;
                    case 2:
                        if(value.trim().length() > 0){
                            try {
                                int age = Integer.parseInt(value.trim());
                                userBean.setAge(age);
                            } catch (NumberFormatException e){
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 3:
                        userBean.setIdCard(value);
                        break;
                    case 4:
                        userBean.setGender(value);
                        break;
                    case 5:
                        userBean.setSchool(value);
                        break;
                    case 6:
                        userBean.setGrade(value);
                        break;
                    case 7:
                        userBean.setUserClass(value);
                        break;
                }
            }
            dbHelper.saveUser(userBean);
        }

        xls.close();
    }
}
