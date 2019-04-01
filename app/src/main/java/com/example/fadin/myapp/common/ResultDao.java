package com.example.fadin.myapp.common;

import java.util.List;

/**
 * Created by root on 19-3-2.
 */

public class ResultDao {
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String code;
    private String msg;
    private List<da> data;

    public List<da> getData() {
        return data;
    }

    public void setData(List<da> data) {
        this.data = data;
    }

    public static class da{
        public String email;
        public String username;
        public String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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
    }
}
