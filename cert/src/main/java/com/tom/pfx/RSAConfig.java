package com.tom.pfx;

public class RSAConfig {
      private String pwd;
      private String alias;
      private String path;
      private String cerPath;
    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getCerPath() {
        return cerPath;
    }
    public void setCerPath(String cerPath) {
        this.cerPath = cerPath;
    }
    public RSAConfig(String pwd, String alias, String path, String cerPath) {
        super();
        this.pwd = pwd;
        this.alias = alias;
        this.path = path;
        this.cerPath = cerPath;
    }
}