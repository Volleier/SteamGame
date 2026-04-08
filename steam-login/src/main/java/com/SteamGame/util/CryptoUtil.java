package com.SteamGame.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12; // 96 bits recommended for GCM
    private static final int TAG_LENGTH_BIT = 128;

    public static class EncryptResult {
        public final String cipherTextBase64;
        public final String ivBase64;

        public EncryptResult(String cipherTextBase64, String ivBase64) {
            this.cipherTextBase64 = cipherTextBase64;
            this.ivBase64 = ivBase64;
        }
    }

    public static EncryptResult encrypt(String plainText, String base64Key) throws Exception {
        byte[] key = Base64.getDecoder().decode(base64Key);
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);

        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

        byte[] cipherBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        String cipherTextBase64 = Base64.getEncoder().encodeToString(cipherBytes);
        String ivBase64 = Base64.getEncoder().encodeToString(iv);
        return new EncryptResult(cipherTextBase64, ivBase64);
    }

    public static String decrypt(String cipherTextBase64, String ivBase64, String base64Key) throws Exception {
        byte[] key = Base64.getDecoder().decode(base64Key);
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);

        byte[] iv = Base64.getDecoder().decode(ivBase64);
        byte[] cipherBytes = Base64.getDecoder().decode(cipherTextBase64);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        byte[] plain = cipher.doFinal(cipherBytes);
        return new String(plain, "UTF-8");
    }
}
