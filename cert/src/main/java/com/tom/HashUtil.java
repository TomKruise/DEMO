package com.tom;

import org.bouncycastle.crypto.digests.RIPEMD160Digest;

import java.security.MessageDigest;

public class HashUtil {
    public static String md5(byte[] data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(data);

        return bytes2HexStr(digest);
    }

    private static String bytes2HexStr(byte[] digest) {
        StringBuffer buffer = new StringBuffer();
        for (byte b : digest) {
            buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        return buffer.toString();
    }

    public static String sha1Bytes(byte[] data) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA");
        byte[] digest = sha.digest(data);
        return bytes2HexStr(digest);
    }

    public static String sha256Bytes(byte[] data) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] digest = sha.digest(data);
        return bytes2HexStr(digest);
    }

    public static String sha512Bytes(byte[] data) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-512");
        byte[] digest = sha.digest(data);
        return bytes2HexStr(digest);
    }

    public static String ripemd160Bytes(byte[] data) {
        RIPEMD160Digest ripemd160Digest = new RIPEMD160Digest();
        ripemd160Digest.update(data, 0, data.length);

        byte[] bytes = new byte[ripemd160Digest.getDigestSize()];
        ripemd160Digest.doFinal(bytes, 0);

        return bytes2HexStr(bytes);
    }

    public static byte[] hexStr2HexBytes(String hexStr) {
        if(null == hexStr || hexStr.length() == 0) return null;

        hexStr = (hexStr.length() == 1)? "0" + hexStr : hexStr;
        byte[]arr = new byte[hexStr.length()/2];
        byte[] strBytes = hexStr.getBytes();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = unitBytes(strBytes[i*2], strBytes[i*2+1]);
        }

        return arr;
    }

    public static byte unitBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte)(_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte)(_b0 ^ _b1);
        return ret;
    }
}
