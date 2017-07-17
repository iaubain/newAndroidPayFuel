package com.oltranz.pf.n_payfuel.models.login;

/**
 * Created by Hp on 6/7/2017.
 */

public class LoginRequest {
    private String deviceId;
    private String userPin;

    public LoginRequest(String deviceId, String userPin) {
        this.deviceId = deviceId;
        this.userPin = userPin;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserPin() {
        return userPin;
    }

    public void setUserPin(String userPin) {
        this.userPin = userPin;
    }
}
