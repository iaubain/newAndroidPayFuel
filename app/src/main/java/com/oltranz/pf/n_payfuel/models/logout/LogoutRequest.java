package com.oltranz.pf.n_payfuel.models.logout;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/17/2017.
 */

public class LogoutRequest {
    private String deviceId;
    private String userId;

    public LogoutRequest() {
    }

    public LogoutRequest(String deviceId, String userId) {
        this.setDeviceId(deviceId);
        this.setUserId(userId);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
