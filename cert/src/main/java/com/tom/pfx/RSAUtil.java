package com.tom.pfx;

import java.io.FileInputStream;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class RSAUtil {
    public static RSAPublicKey publicKey;
    public static RSAPublicKey tlPublicKey;
    public static RSAPrivateKey privateKey;

    public static void configure(RSAConfig config) {
        try {
            RSAUtil.publicKey = (RSAPublicKey) loadPublicKey(config.getCerPath());
            RSAUtil.privateKey = (RSAPrivateKey) loadPrivateKey(config.getAlias(), config.getPath(), config.getPwd());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 私钥
     * 
     * @param source
     * @return
     */
    public static String doSignBySHA1withRSA(String source) {
        byte[] sourceData = source.getBytes();
        String result = null;
        try {
            // 获得私钥并签名
            Signature sign = Signature.getInstance("SHA1WithRSA", new BouncyCastleProvider());
            sign.initSign(privateKey);
            sign.update(sourceData);

            byte[] resultData = sign.sign();
            result = Base64.encodeBase64String(resultData);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static PrivateKey loadPrivateKey(String alias, String path, String pwd) {
        PrivateKey privateKey = null;
        try {
            // 获得公钥

            KeyPair keyPair = null;

            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            keyStore.load(new FileInputStream(path), pwd.toCharArray());
            Key key = keyStore.getKey(alias, pwd.toCharArray());

            if (key instanceof PrivateKey) {
                Certificate cert = keyStore.getCertificate(alias);

                keyPair = new KeyPair(cert.getPublicKey(), (PrivateKey) key);

            }
            privateKey = keyPair.getPrivate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return privateKey;

    }

    public static PublicKey loadPublicKey(String cerPath) {
        PublicKey publicKey = null;
        try {
            // 获得公钥
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            FileInputStream in = new FileInputStream(cerPath);

            // 生成一个证书对象并使用从输入流 inStream 中读取的数据对它进行初始化。
            Certificate c = cf.generateCertificate(in);
            publicKey = c.getPublicKey();
         
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicKey;

    }

    /**
     * 公钥验签
     * 
     * @param sign
     * @param source
     * @return
     */
    public static boolean doVerifyBySHA1withRSA(String sign, String source) {
        boolean result = false;
        try {
            // 获得公钥
            byte[] sourceData = source.getBytes();

            Signature verify = Signature.getInstance("SHA1withRSA");
            verify.initVerify(publicKey);
            verify.update(sourceData);

            byte[] decoded = Base64.decodeBase64(sign);
            result = verify.verify(decoded);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 公钥加密
     * 
     * @param plainText
     * @param pkcs8_rsa_public_key
     * @return
     */
    public static String doEncryptByRSA(String plainText) {
        byte[] sourceData = plainText.getBytes();
        String result = null;

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] resultData = cipher.doFinal(sourceData);
            result = Base64.encodeBase64String(resultData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String doDecryptByRSA(String encryptedText) {
        byte[] sourceData = Base64.decodeBase64(encryptedText);
        String result = null;

        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] resultData = cipher.doFinal(sourceData);
            result = new String(resultData);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}