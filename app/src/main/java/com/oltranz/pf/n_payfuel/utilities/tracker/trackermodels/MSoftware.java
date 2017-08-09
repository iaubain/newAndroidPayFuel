package com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels;

/**
 * Created by Hp on 4/25/2017.
 */

public class MSoftware {
    private String deviceName;
    private String loggedUser;
    private String lastActivityDate;
    private String softwareName;
    private String accessDate;

    public MSoftware() {
    }

    public MSoftware(String deviceName, String loggedUser, String lastActivityDate, String softwareName, String accessDate) {
        this.deviceName = deviceName;
        this.loggedUser = loggedUser;
        this.lastActivityDate = lastActivityDate;
        this.softwareName = softwareName;
        this.accessDate = accessDate;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(String loggedUser) {
        this.loggedUser = loggedUser;
    }

    public String getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
    }

    public String getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(String lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }
}
