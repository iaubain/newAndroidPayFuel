package com.oltranz.pf.n_payfuel.models;

/**
 * Created by Hp on 6/7/2017.
 */

public class StatusModel {
    private int statusCode;
    private String message;

    public StatusModel() {
    }

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
