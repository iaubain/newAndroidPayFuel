package com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels;

/**
 * Created by Hp on 4/26/2017.
 */

public class CommandReport {
    private String cmd;
    private String execStatus;
    private String execDesc;
    private VitalInfoReport vitalInfoReport;

    public CommandReport() {
    }

    public CommandReport(String cmd, String execStatus, String execDesc) {
        this.setCmd(cmd);
        this.setExecStatus(execStatus);
        this.setExecDesc(execDesc);
    }

    public CommandReport(String cmd, String execStatus, String execDesc, VitalInfoReport vitalInfoReport) {
        this.cmd = cmd;
        this.execStatus = execStatus;
        this.execDesc = execDesc;
        this.setVitalInfoReport(vitalInfoReport);
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getExecStatus() {
        return execStatus;
    }

    public void setExecStatus(String execStatus) {
        this.execStatus = execStatus;
    }

    public String getExecDesc() {
        return execDesc;
    }

    public void setExecDesc(String execDesc) {
        this.execDesc = execDesc;
    }

    public VitalInfoReport getVitalInfoReport() {
        return vitalInfoReport;
    }

    public void setVitalInfoReport(VitalInfoReport vitalInfoReport) {
        this.vitalInfoReport = vitalInfoReport;
    }
}
