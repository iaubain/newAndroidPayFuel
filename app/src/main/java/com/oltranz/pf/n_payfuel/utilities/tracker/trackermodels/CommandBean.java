package com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels;

/**
 * Created by Hp on 4/26/2017.
 */

public class CommandBean {
    private String cmd;
    private String value;

    public CommandBean() {
    }

    public CommandBean(String cmd, String value) {
        this.setCmd(cmd);
        this.setValue(value);
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
