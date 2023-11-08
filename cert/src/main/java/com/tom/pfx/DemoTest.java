package com.tom.pfx;



public class DemoTest {
    public static void main(String[] args) throws Exception {
        String path="/RSA/other/openssl.pfx";
        String pwd="秘钥的秘钥（生成秘钥时记得存好）";
        String alias="1";
        String cerPath="/RSA/other/openssl.cer";
        
        RSAConfig config = new RSAConfig(pwd, alias, path, cerPath);//读取配置文件的秘钥配置
        RSAUtil.configure(config);
        String id = "123456789";
        String encodeId = RSAUtil.doEncryptByRSA(id);
        String decodeId = RSAUtil.doDecryptByRSA(encodeId);
        System.out.println("encodeId="+encodeId);
        System.out.println("decodeId="+decodeId);
        
        String sign = RSAUtil.doSignBySHA1withRSA(id);
        System.out.println(sign);
        boolean verify = RSAUtil.doVerifyBySHA1withRSA(sign, id);
        System.out.println("verify result="+verify);
    }

}