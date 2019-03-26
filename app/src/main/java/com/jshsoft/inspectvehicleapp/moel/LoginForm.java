package com.jshsoft.inspectvehicleapp.moel;

public class LoginForm {
    private String username;
    private String password;
    private String captcha;
    private String uuid;

    public LoginForm(String username, String password, String captcha, String uuid) {
        this.username = username;
        this.password = password;
        this.captcha = captcha;
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "LoginForm{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", captcha='" + captcha + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
