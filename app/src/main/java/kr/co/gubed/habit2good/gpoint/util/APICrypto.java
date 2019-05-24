package kr.co.gubed.habit2good.gpoint.util;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.TreeMap;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class APICrypto {
    private final static String TAG = APICrypto.class.toString();

    private final static String HEX = "0123456789ABCDEF";
    public final static String KEY = "aac7584f5525c081";
    public static String encrypt(String seed, String cleartext) throws Exception {
        if (cleartext == null || cleartext.equals("")) return null;
        byte[] result = encrypt(seed.getBytes(), cleartext.getBytes());
        return Base64.encodeToString(result, 0);
    }

    public static String decrypt(String seed, String encrypted) throws Exception {
        if (encrypted == null || encrypted.equals("")) return null;
        byte[] enc = Base64.decode(encrypted, 0);
        byte[] result = decrypt(seed.getBytes(), enc);
        return new String(result);
    }


    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/OFB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec("aac7584f5525c081".getBytes()));
        return cipher.doFinal(clear);
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/OFB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec("aac7584f5525c081".getBytes()));
        return cipher.doFinal(encrypted);
    }

    private static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (byte aBuf : buf) {
            appendHex(result, aBuf);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    private static String hmacMD5(String value, String key) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String type = "HmacMD5";
        SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
        Mac mac = Mac.getInstance(type);
        mac.init(secret);
        byte[] bytes = mac.doFinal(value.getBytes());
        return toHex(bytes).toLowerCase();
    }

    private static JSONObject getJson(Context context, HashMap<String, String> map, String shared_key){
        Long tsLong = System.currentTimeMillis() / 1000;
        map.put(CommonUtil.KEY_TIMESTAMP, tsLong.toString());
        map.put(CommonUtil.KEY_VERSION, CommonUtil.getVersion(context)+"");
        map.put(CommonUtil.KEY_LOCALE, Applications.getCountry(context));
        StringBuilder sb = new StringBuilder();
        TreeMap<String, String> tm = new TreeMap<>(map);
        for (String key : tm.keySet()) {
            sb.append(tm.get(key));
        }
        String hmac = "";
        try {
             hmac = hmacMD5(sb.toString(), shared_key);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        map.put(CommonUtil.KEY_SIGN, hmac);
        Log.e(TAG,map.toString());
        return new JSONObject(map);
    }

    public static String getParam(Context context, HashMap<String, String> map, String shared_key){
        String param = "";
        JSONObject job = getJson(context, map, shared_key);
        try {
            String encryptedData = encrypt(shared_key, job.toString());
            param = "x=" + URLEncoder.encode(encryptedData, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }

    public static String MD5(String str){
        String MD5;
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte byteData[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }
            MD5 = sb.toString();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            MD5 = null;
        }
        return MD5;
    }

}
