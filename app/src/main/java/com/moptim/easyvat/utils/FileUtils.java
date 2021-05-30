package com.moptim.easyvat.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtils {

    static String app_path = Environment.getExternalStorageDirectory() + File.separator + "easyVat/";

    public static void saveFile(String str, String fileName, boolean append) {
        // 创建String对象保存文件名路径
        try {
            // 创建指定路径的文件
            File file = new File(app_path + fileName);
            //创建上级目录
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            // 如果文件不存在
            if (!file.exists()) {
                file.createNewFile();
            }
            // 获取文件的输出流对象
            FileOutputStream outStream = new FileOutputStream(file, append);
            // 获取字符串对象的byte数组并写入文件流
            outStream.write(str.getBytes());
            // 最后关闭文件输出流
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String fileName) {
        try {
            // 创建文件
            File file = new File(app_path,fileName);
            // 创建FileInputStream对象
            FileInputStream fis = new FileInputStream(file);
            // 创建字节数组 每次缓冲1M
            byte[] b = new byte[1024];
            int len = 0;// 一次读取1024字节大小，没有数据后返回-1.
            // 创建ByteArrayOutputStream对象
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 一次读取1024个字节，然后往字符输出流中写读取的字节数
            while ((len = fis.read(b)) != -1) {
                baos.write(b, 0, len);
            }
            // 将读取的字节总数生成字节数组
            byte[] data = baos.toByteArray();
            // 关闭字节输出流
            baos.close();
            // 关闭文件输入流
            fis.close();
            // 返回字符串对象
            return new String(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> readLine(String fileName, int len){
        ArrayList<String> result = new ArrayList<>();
        File file = new File(app_path, fileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            int count = 0;
            while ((line = br.readLine()) != null && count < len) {
                result.add(line);
                count++;
            }

            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }

    public static void saveBitmap(Bitmap bitmap, String name) {
        try {
            File filePic = new File(app_path + "Image/" + name);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("FileUtils", "saveBitmap: " + e.getMessage());
        }
    }
}
