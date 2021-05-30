package com.moptim.easyvat.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Environment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Hashtable;

public class CollectionUtils {
    /**
     * 判断集合是否为null或者0个元素
     *
     * @param c
     * @return
     */
    public static boolean isNullOrEmpty(Collection c) {
        if (null == c || c.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 从assets中读取图片
     */
    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 从assets中读取文本
     */
    private String getStringFromAssetsFile(Context context, String fileName) {
        String rel = null;
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(fileName);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int len = -1;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            rel = byteArrayOutputStream.toString();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rel;
    }

    /**
     * 从assets中读取音乐
     */
    private MediaPlayer getMusicFromAssetsFile(Context context, String fileName) {
        AssetManager am = context.getAssets();
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor is = am.openFd(fileName);
            mediaPlayer.setDataSource(is.getFileDescriptor(), is.getStartOffset(), is.getLength());
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaPlayer;
    }

    /**
     * 从assets复制到SD
     */
    private void copyAssetsToSd(Context context, String assets, String sd){
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getAssets().open(assets);

            File newFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), sd);
            fos = new FileOutputStream(newFile);

            int len;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 用字符串生成二维码
     *
     * @param str
     * @return
     * @throws WriterException
     */
    public static Bitmap Create2DCode(String str) throws WriterException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //设置QR二维码的纠错级别——这里选择最高H级别
        //如果二维码地址比较短，推荐H,因为容错率高，容错率越高，越容易被快速扫描
        //如果二维码地址比较长，生成的二维码比较密，推荐L,因为这样生成的二维码比较稀疏，容易扫描
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 400, 400, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    //这个else要加上去，否者保存的二维码全黑
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }


}
