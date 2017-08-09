package com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels;

/**
 * Created by Hp on 4/25/2017.
 */

public class MBattery {
    private String scale;
    private String level;
    private String percentage;
    private String state;
    private String accessDate;

    public MBattery() {

    }

    public MBattery(String scale, String level, String percentage, String state, String accessDate) {
        this.setScale(scale);
        this.setLevel(level);
        this.setPercentage(percentage);
        this.setState(state);
        this.setAccessDate(accessDate);
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}
