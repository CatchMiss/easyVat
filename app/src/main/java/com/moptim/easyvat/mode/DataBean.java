package com.moptim.easyvat.mode;

import com.moptim.easyvat.utils.Constant;

import java.io.Serializable;

public class DataBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String number;
    private int correct;    //正确数
    private int total;      //总数
    private int modeTime;   //测试速度模式
    private int modeHole;   //裸眼，矫正
    private int modeEye;    //左，右，双眼
    private String date;    //时间

    public DataBean(){
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPercent(){
        float percent = correct / (float)total;
        return (int) (percent * 100);
    }

    public int getModeTime() {
        return modeTime;
    }

    public String getModeTimeStr() {
        String ret = "";
        switch (modeTime){
            case Constant.MODE_TIME_FAST:
                ret = "快速";
                break;
            case Constant.MODE_TIME_NORMAL:
                ret = "普通";
                break;
            case Constant.MODE_TIME_STANDARD:
                ret = "标准";
                break;

        }
        return ret;
    }

    public int getModeTimeData(){
        int ret = 0;
        switch (modeTime){
            case Constant.MODE_TIME_FAST:
                ret = 30;
                break;
            case Constant.MODE_TIME_NORMAL:
                ret = 60;
                break;
            case Constant.MODE_TIME_STANDARD:
                ret = 3 * 60;
                break;

        }
        return ret;
    }

    public void setModeTime(int modeTime) {
        this.modeTime = modeTime;
    }

    public int getModeHole() {
        return modeHole;
    }

    public String getModeHoleStr() {
        String ret = "";
        switch (modeHole){
            case Constant.MODE_HOLE_BAKE:
                ret = "裸眼";
                break;
            case Constant.MODE_HOLE_CORRECTION:
                ret = "矫正";
                break;
        }
        return ret;
    }

    public void setModeHole(int modeHole) {
        this.modeHole = modeHole;
    }

    public int getModeEye() {
        return modeEye;
    }

    public String getModeEyeStr() {
        String ret = "";
        switch (modeEye){
            case Constant.MODE_OD:
                ret = "OD";
                break;
            case Constant.MODE_OS:
                ret = "OS";
                break;
            case Constant.MODE_OU:
                ret = "OU";
                break;

        }
        return ret;
    }
    public String getModeEyeStr2() {
        String ret = "";
        switch (modeEye){
            case Constant.MODE_OD:
                ret = "右眼";
                break;
            case Constant.MODE_OS:
                ret = "左眼";
                break;
            case Constant.MODE_OU:
                ret = "双眼";
                break;

        }
        return ret;
    }

    public void setModeEye(int modeEye) {
        this.modeEye = modeEye;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Number: " + number + "\n" +
                "Percent: " + getPercent() + "\n" +
                "ModeTime: " + getModeTimeStr() + "\n" +
                "Data: " + date;
    }
}
