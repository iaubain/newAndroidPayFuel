package com.oltranz.pf.n_payfuel.models.register;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Hp on 6/7/2017.
 */

public class RegisterResponse {
    @JsonProperty("DeviceRegistrationResponse")
    private EnrolData enrolData;
    private String message;
    private int statusCode;

    public RegisterResponse() {
    }

    public RegisterResponse(EnrolData enrolData, String message, int statusCode) {
        this.setEnrolData(enrolData);
        this.setMessage(message);
        this.setStatusCode(statusCode);
    }

    public EnrolData getEnrolData() {
        return enrolData;
    }

    public void setEnrolData(EnrolData enrolData) {
        this.enrolData = enrolData;
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
