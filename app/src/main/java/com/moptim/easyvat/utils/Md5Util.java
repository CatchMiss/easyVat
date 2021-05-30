package com.moptim.easyvat.utils;

import java.security.MessageDigest;

public class Md5Util {
    /**
     * 加盐MD5加密
     */
    public static String getSaltMD5(String primaryKey) {
        String Salt = "YJX1LOVE453H663O";
        primaryKey = md5Hex(primaryKey + Salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            //在结果中的每三位用中间位保存salt值
            cs[i] = primaryKey.charAt(i / 3 * 2);
            cs[i + 1] = Salt.charAt(i / 3);
            cs[i + 2] = primaryKey.charAt(i / 3 * 2 + 1);
        }
        return String.valueOf(cs);
    }

    private static String md5Hex(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(str.getBytes());
            return HexEncode(digest);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 字节流转成十六进制表示
     */
    public static String HexEncode(byte[] src) {
        String strHex;
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < src.length; n++) {
            strHex = Integer.toHexString(src[n] & 0xFF);
            // 每个字节由两个字符表示，位数不够，高位补0
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex);
        }
        return sb.toString().trim();
    }
}
