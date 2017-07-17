package com.oltranz.pf.n_payfuel.models.register;

/**
 * Created by Hp on 6/7/2017.
 */

public class RegisterRequest {
    private String email;
    private String password;
    private String deviceId;
    private String serialNumber;

    public RegisterRequest(String email, String password, String deviceId, String serialNumber) {
        this.email = email;
        this.password = password;
        this.deviceId = deviceId;
        this.serialNumber = serialNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
