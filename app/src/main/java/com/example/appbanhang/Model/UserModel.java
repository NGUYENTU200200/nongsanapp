package com.example.appbanhang.Model;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    boolean success;
    String message;
    List<User> result ;

    public UserModel(boolean success, String message, List<User> result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<User> getList() {
        return result;
    }

    public void setList(List<User> list) {
        this.result = list;
    }
}
