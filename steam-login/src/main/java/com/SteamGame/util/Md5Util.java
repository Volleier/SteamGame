package com.SteamGame.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

/**
 * Md5Util 已标记为废弃。
 *
 * 说明：MD5 不应被用于凭据或密钥存储/加密用途。本项目中不再将 Md5Util 用于敏感凭据处理。
 * 保留该工具仅供非敏感用途的兼容性检查。新代码应使用 `CryptoUtil`（AES-GCM）进行加解密。
 */
@Deprecated
public class Md5Util {

    /**
     * 将字符串进行MD5加密
     * 
     * @param input 需要加密的字符串
     * @return 加密后的32位MD5字符串
     */
    public static String encrypt(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}