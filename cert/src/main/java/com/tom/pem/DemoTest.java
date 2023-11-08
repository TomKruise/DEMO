package com.tom.pem;




public class DemoTest {
    public static void main(String[] args) {

        String A_RSA_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDnPzYKf20JIzaEEkeQTnDAkM1s+nzPbRIA2vU/FOQ47XGIa16lFQ//mxb2ichRF/YGRrBseoK4qxs5zAdNJwXWxQE5nsLzQvSsnQetROczUAdrNVMA4p+rySycYbMF3WK3TFODh1XUZ6KPPZ41PIABFPuqRsK3gHOhAtSmx8LP4cvgnFbCNQl7n7lWmEaTrSOiv+Ld8XlBIIG+jbIzDh6pHCYVsQBkhhC16awxNsbMVKAuGxpDO7OqEqOcmpTODDUuI99YBIuWrxHSHEOGDyK4FlGn9Ryd0fBLMsPPPfxZkC/KiJHIXm/k3I2CtR7E0+iQz3M5nogAsGzBIdVmcod/AgMBAAECggEBAMg2vG1eYmM077BtuzRAFfND6/hc788P2jSPXyMczXRUcKXygGFh2RYvizQtmxhLLKHGdl2VvLRywQHLms676JxIuYTP5m6EHB+PXeQw8hRFSAcUhicQD7rGVS+Yj02Wni+hj/UjKbbbe62VZfMlzJYjOrn0xgXm2zYeo0s7TXp5mWkmhhy3SVbz7AGDOJ4uZo8Hh3ORm6SfDChBi5fOBqH+4gBASQUPgTUczWZuq/5MhBHTTFPjief5mY7uSD8algBdlPnXeqGO1NiCE7/3m/PddI+xX35JyfwvosaHq3GKWAoAEEOVMWyG8tsveSMNV2YsPCj14LPPrbo9umwfCvECgYEA+ybodq1Fmt82qQR+BAyjGrNUOVQe0MtghrGvuH7yjJf9dzoU0+nmF7qS64Rya6udD+nDE34pH+Wz7Y22pfX6GL2Qino+vLLVTjXVJ7oSFt17l3V8WiMBBFdsr0aCQTlP/rmlS+Gzx+DP9BzIf3EXmYxcZMVYBLN8lJbogMHF3wMCgYEA67XwshfsuY6yxVAFjQIs8VU2ROxzp8FO0zKPCk8xXm96ikyidQ5slWzJ7HfogFqCTB8ZOT2kedF5WZyxNSykcipyxPcgSFtkyxNhkp/bwC2KS0JxePftaDGzzb7fAM61SfWunKTDTa0CQJTKMnDJ13Fcv9Z8HzCBt7DBN/DZ/tUCgYEA2SAwBLmT3WpwRPq/Pxz1vVWf0Ngqs/O/hXMEKYqGgom79WFfND2YUJdaAQbGLNN2u5UqsyV0xEC/pvXHG/9lshHgbfd1WYl5412i4+93SBE+khhd40czz98M9RMN9PlpcRxqDQoZdQmkfrSLmbHZ50NzdSMvDxFk+MjmRLpBKKUCgYEAzMenkoI4lslDxSqNeAFA3HYEjQLERCUsf034eaNtp7bARnDn3zyl13fJQhi2tPRtKQcHmfRU9cSoYdNBHYpoYAtC5J4yvMoyGj//UCxz9VVbRaE3BjqXViOAK6q9AW2UkOnSRqLaTpyVTVg3BnV41iTVyJDmCw7QU69LXndwXPUCgYBAambp9pl16WnjXNq2FJ+hlkbDDzWxSfHx0MVDmnd7Ptko0mMPCDjXP/3k18N2I0fucsVMDnxUnzi+bopp/MPunWPFKvBhjCG6r2C6NP7GjU1YyQv7cYJ1/FVQ1W4Sb/HWH0d3OLHR1YddOwvJ0o2T1TFfJ5hRUAlGyr07z4SNDA==-----END PRIVATE KEY-----";
        String A_RSA_PUBLIC_KEY="-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5z82Cn9tCSM2hBJHkE5wwJDNbPp8z20SANr1PxTkOO1xiGtepRUP/5sW9onIURf2BkawbHqCuKsbOcwHTScF1sUBOZ7C80L0rJ0HrUTnM1AHazVTAOKfq8ksnGGzBd1it0xTg4dV1Geijz2eNTyAART7qkbCt4BzoQLUpsfCz+HL4JxWwjUJe5+5VphGk60jor/i3fF5QSCBvo2yMw4eqRwmFbEAZIYQtemsMTbGzFSgLhsaQzuzqhKjnJqUzgw1LiPfWASLlq8R0hxDhg8iuBZRp/UcndHwSzLDzz38WZAvyoiRyF5v5NyNgrUexNPokM9zOZ6IALBswSHVZnKHfwIDAQAB-----END PUBLIC KEY-----";

        String id = "123456789";
        PemConfig config = new PemConfig(A_RSA_PRIVATE_KEY, A_RSA_PUBLIC_KEY);//读取配置文件的秘钥
        RSAUtil.configure(config);
        String encodeId = RSAUtil.doEncryptByRSA(id);
        String decodeId = RSAUtil.doDecryptByRSA(encodeId);
        System.out.println("encodeId="+encodeId);
        System.out.println("decodeId="+decodeId);
        
        String sign = RSAUtil.doSignBySHA1withRSA(id);
        
        boolean verify = RSAUtil.doVerifyBySHA1withRSA(sign, id);
        System.out.println("verify result="+verify);
    }

}