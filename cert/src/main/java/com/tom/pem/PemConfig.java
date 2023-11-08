package com.tom.pem;

public class PemConfig {

    private String rsa_private_key;
    private String rsa_public_key;

    public String getRsa_private_key() {
        return rsa_private_key;
    }

    public void setRsa_private_key(String rsa_private_key) {
        this.rsa_private_key = rsa_private_key;
    }

    public String getRsa_public_key() {
        return rsa_public_key;
    }

    public void setRsa_public_key(String rsa_public_key) {
        this.rsa_public_key = rsa_public_key;
    }

    public PemConfig(String rsa_private_key, String rsa_public_key) {
        super();
        this.rsa_private_key = rsa_private_key;
        this.rsa_public_key = rsa_public_key;
    }

}