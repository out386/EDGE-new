package com.edge2.registration.models;

import androidx.annotation.NonNull;

public class RegisterResp {
    private boolean status;
    private String message;
    private User user;

    @Override
    public String toString() {
        return "RegisterResp{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", user=" + user.toString() +
                '}';
    }
}
