package com.moptim.easyvat.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.moptim.easyvat.mode.DataBean;
import com.moptim.easyvat.mode.UserBean;

import java.util.ArrayList;

public class MptDBHelper extends SQLiteOpenHelper {

    private static final String TAG = MptDBHelper.class.getSimpleName();

    // 单例
    private static MptDBHelper dbHelper = null;
    public static MptDBHelper getInstance(Context context) {
        if (dbHelper == null) {
            synchronized (MptDBHelper.class){
                if (dbHelper == null) {
                    dbHelper = new MptDBHelper(context);
                }
            }
        }
        return dbHelper;
    }

    //数据库名
    private static final String DB_NAME = "easyVat.db";
    //个人信息表明
    private static final String TABLE_USER = "vatUser";
    //测试信息表名
    private static final String TABLE_DATA = "vatData";

    //自增id
    private static final String ID = "_id";

    //编号
    public static final String NUMBER = "number";

    //个人信息
    public static final String NAME = "name";
    public static final String AGE = "age";
    public static final String ID_CARD = "idCard";
    public static final String GENDER = "gender";
    public static final String SCHOOL = "school";
    public static final String GRADE = "grade";
    public static final String U_CLASS = "userClass";

    //测试信息
    public static final String CORRECT = "correct";     //正确数
    public static final String TOTAL = "total";         //总数
    public static final String MODE_TIME = "modeTime";  //快速模式，普通模式，标准模式
    public static final String MODE_HOLE = "modeHole";  //裸眼 or 矫正
    public static final String MODE_EYE = "modeEye";    //左，右，双眼
    public static final String DATE = "date";

    // 创建表
    private MptDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建个人信息表
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER
                        + "("
                        + ID + " integer PRIMARY KEY AUTOINCREMENT,"
                        + NUMBER + " text NOT NULL UNIQUE,"
                        + NAME + " text,"
                        + AGE + " integer,"
                        + ID_CARD + " text,"
                        + GENDER + " text,"
                        + SCHOOL + " text,"
                        + GRADE + " text,"
                        + U_CLASS + " text"
                        + ")";
        db.execSQL(CREATE_USER_TABLE);

        //创建测试数据表
        String CREATE_DATA_TABLE = "CREATE TABLE " + TABLE_DATA
                + "("
                + ID + " integer PRIMARY KEY AUTOINCREMENT,"
                + NUMBER + " text NOT NULL,"
                + CORRECT + " integer,"
                + TOTAL + " integer,"
                + MODE_TIME + " integer,"
                + MODE_HOLE + " integer,"
                + MODE_EYE + " integer,"
                + DATE + " text"
                + ")";
        db.execSQL(CREATE_DATA_TABLE);

        Log.i(TAG, "onCreate: "+db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_USER);
        db.execSQL("drop table if exists " + TABLE_DATA);
    }

    private static ContentValues parseToContentValues(UserBean bean){
        ContentValues values = new ContentValues();

        values.put(NUMBER, bean.getNumber());
        values.put(NAME, bean.getName());
        values.put(AGE, bean.getAge());
        values.put(ID_CARD, bean.getIdCard());
        values.put(GENDER, bean.getGender());
        values.put(SCHOOL, bean.getSchool());
        values.put(GRADE, bean.getGrade());
        values.put(U_CLASS, bean.getUserClass());

        return values;
    }

    private static ContentValues parseToContentValues(DataBean bean){
        ContentValues values = new ContentValues();

        values.put(NUMBER, bean.getNumber());
        values.put(CORRECT, bean.getCorrect());
        values.put(TOTAL, bean.getTotal());
        values.put(MODE_TIME, bean.getModeTime());
        values.put(MODE_HOLE, bean.getModeHole());
        values.put(MODE_EYE, bean.getModeEye());
        values.put(DATE, bean.getDate());

        return values;
    }

    public ArrayList<UserBean> queryUser(String[] columns, String selection, String[] selectionArgs) {
        ArrayList<UserBean> userBeans = new ArrayList<>();
        try (SQLiteDatabase db = getReadableDatabase(); Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null)) {
            // 获取一个只读数据库
            // 执行查询方法，返回游标

            while (cursor != null && cursor.moveToNext()) {
                UserBean bean = new UserBean();

                bean.setNumber(cursor.getString(cursor.getColumnIndex(NUMBER)));
                bean.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                bean.setAge(cursor.getInt(cursor.getColumnIndex(AGE)));
                bean.setIdCard(cursor.getString(cursor.getColumnIndex(ID_CARD)));
                bean.setGender(cursor.getString(cursor.getColumnIndex(GENDER)));
                bean.setSchool(cursor.getString(cursor.getColumnIndex(SCHOOL)));
                bean.setGrade(cursor.getString(cursor.getColumnIndex(GRADE)));
                bean.setUserClass(cursor.getString(cursor.getColumnIndex(U_CLASS)));

                userBeans.add(bean);
            }
        } catch (Exception e) {
            Log.e(TAG, "query err " + e.getMessage());
        }
        return userBeans;
    }

    public ArrayList<DataBean> queryData(String[] columns, String selection, String[] selectionArgs) {
        ArrayList<DataBean> dataBeans = new ArrayList<>();
        try (SQLiteDatabase db = getReadableDatabase(); Cursor cursor = db.query(TABLE_DATA, columns, selection, selectionArgs, null, null, null)) {
            // 获取一个只读数据库
            // 执行查询方法，返回游标

            while (cursor != null && cursor.moveToNext()) {
                DataBean bean = new DataBean();

                bean.setNumber(cursor.getString(cursor.getColumnIndex(NUMBER)));
                bean.setCorrect(cursor.getInt(cursor.getColumnIndex(CORRECT)));
                bean.setTotal(cursor.getInt(cursor.getColumnIndex(TOTAL)));
                bean.setModeTime(cursor.getInt(cursor.getColumnIndex(MODE_TIME)));
                bean.setModeHole(cursor.getInt(cursor.getColumnIndex(MODE_HOLE)));
                bean.setModeEye(cursor.getInt(cursor.getColumnIndex(MODE_EYE)));
                bean.setDate(cursor.getString(cursor.getColumnIndex(DATE)));

                dataBeans.add(bean);
            }
        } catch (Exception e) {
            Log.e(TAG, "query err " + e.getMessage());
        }
        return dataBeans;
    }

    public void saveUser(UserBean bean) {
        try{
            Log.d(TAG, "save user " + bean.toString());
            ContentValues values = parseToContentValues(bean);
            SQLiteDatabase db = getWritableDatabase();
            db.insertWithOnConflict(TABLE_USER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
        } catch (Exception e){
            Log.e(TAG, "save User: " + e.getMessage());
        }
    }

    public void deleteUser(UserBean bean) {
        try {
            Log.d(TAG, "delete user " + bean.toString());
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DELETE FROM vatUser WHERE number = '" + bean.getNumber() + "';");
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "delete user err " + e.getMessage());
        }
    }

    public UserBean getUser(DataBean dataBean){
        UserBean bean = new UserBean();
        try (SQLiteDatabase db = getReadableDatabase(); Cursor cursor = db.query(TABLE_USER, null, MptDBHelper.NUMBER + "=?", new String[]{dataBean.getNumber()}, null, null, null)) {
            while (cursor != null && cursor.moveToNext()) {
                bean.setNumber(cursor.getString(cursor.getColumnIndex(NUMBER)));
                bean.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                bean.setAge(cursor.getInt(cursor.getColumnIndex(AGE)));
                bean.setIdCard(cursor.getString(cursor.getColumnIndex(ID_CARD)));
                bean.setGender(cursor.getString(cursor.getColumnIndex(GENDER)));
                bean.setSchool(cursor.getString(cursor.getColumnIndex(SCHOOL)));
                bean.setGrade(cursor.getString(cursor.getColumnIndex(GRADE)));
                bean.setUserClass(cursor.getString(cursor.getColumnIndex(U_CLASS)));
            }
        } catch (Exception e) {
            Log.e(TAG, "query err " + e.getMessage());
        }
        return bean;
    }

    public void saveData(DataBean bean) {
        try{
            Log.d(TAG, "save data " + bean.getNumber());
            ContentValues values = parseToContentValues(bean);
            SQLiteDatabase db = getWritableDatabase();
            db.insert(TABLE_DATA, null, values);
            db.close();
        } catch (Exception e){
            Log.e(TAG, "save data err " + e.getMessage());
        }
    }

    public void deleteData(DataBean bean) {
        try {
            Log.d(TAG, "delete data " + bean.getNumber());
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("DELETE FROM vatData WHERE number = '" + bean.getNumber() + "';");
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "delete data err "+e.getMessage());
        }
    }
}
