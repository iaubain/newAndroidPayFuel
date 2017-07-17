package com.oltranz.pf.n_payfuel.entities;

import com.orm.SugarRecord;

/**
 * Created by Hp on 6/7/2017.
 */

public class MDevice extends SugarRecord {
    private String deviceName;
    private String deviceSN;
    private boolean registered;

    public MDevice() {
    }

    public MDevice(String deviceName, String deviceSN, boolean registered) {
        this.setDeviceName(deviceName);
        this.setDeviceSN(deviceSN);
        this.setRegistered(registered);
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
}
