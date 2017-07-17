package com.oltranz.pf.n_payfuel.models.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oltranz.pf.n_payfuel.entities.MUser;

/**
 * Created by Hp on 6/7/2017.
 */

public class LoginResponse {
    @JsonProperty("LoginOpModel")
    private MUser mUser;
    private String message;
    private int statusCode;

    public LoginResponse() {
    }

    public LoginResponse(MUser mUser, String message, int statusCode) {
        this.setmUser(mUser);
        this.setMessage(message);
        this.setStatusCode(statusCode);
    }

    public MUser getmUser() {
        return mUser;
    }

    public void setmUser(MUser mUser) {
        this.mUser = mUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
