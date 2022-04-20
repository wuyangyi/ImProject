package net.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AesServerUtil {
    private static final String key = "s379e8b551be4ed0"; //s379e8b551be4ed0b19169e4d93665dc
    private static final String iv = "2002038505065588";

    private  static final String CBC_PKCS5_PADDING = "AES/ECB/PKCS5Padding";//AES是加密方式 ECB是工作模式 PKCS5Padding是填充模式
    private  static final String AES = "AES";//AES 加密

    // 加密
    public static String encrypt(String sSrc) {
        try {
            byte[] raw = key.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
            Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);//"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
            String s = Base64.getEncoder().encodeToString(encrypted);
            s = s.replaceAll("\r|\n", "");
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 解密
    public static String decrypt(String sSrc) {
        try {
            byte[] raw = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
            Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            try {
//            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
                byte[] encrypted1 = Base64.getDecoder().decode(sSrc.getBytes(StandardCharsets.UTF_8));
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, StandardCharsets.UTF_8);
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}