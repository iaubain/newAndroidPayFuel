package com.oltranz.pf.n_payfuel.models.logout;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oltranz.pf.n_payfuel.entities.MUser;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/17/2017.
 */

public class LogoutResponse {
    @JsonProperty("LogoutOpModel")
    private MUser mUser;
    private String message;
    private int statusCode;

    public LogoutResponse() {
    }

    public LogoutResponse(MUser mUser, String message, int statusCode) {
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
