package com.jshsoft.inspectvehicleapp.moel;


import java.io.Serializable;
public class UserEntity implements Serializable {
    private Integer id;
    private String username;
    private String password;
    private Integer type;
    private Integer statue;
    private String bindingImei;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatue() {
        return statue;
    }

    public void setStatue(Integer statue) {
        this.statue = statue;
    }

    public String getBindingImei() {
        return bindingImei;
    }

    public void setBindingImei(String bindingImei) {
        this.bindingImei = bindingImei;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", type='" + type + '\'' +
                ", statue='" + statue + '\'' +
                ", bindingImei='" + bindingImei + '\'' +
                '}';
    }
}
