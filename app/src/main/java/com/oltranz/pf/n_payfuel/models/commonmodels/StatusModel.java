package com.oltranz.pf.n_payfuel.models.commonmodels;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/15/2017.
 */

public class StatusModel {
    private int statusCode;
    private String message;

    public StatusModel(int statusCode, String message) {
        this.setStatusCode(statusCode);
        this.setMessage(message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
