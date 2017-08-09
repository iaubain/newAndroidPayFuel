package com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels;

import java.util.List;

/**
 * Created by Hp on 4/26/2017.
 */

public class CommandList {
    private List<CommandBean> exec;
    private String reportId;

    public CommandList() {
    }

    public CommandList(List<CommandBean> exec, String reportId) {
        this.exec = exec;
        this.setReportId(reportId);
    }

    public List<CommandBean> getExec() {
        return exec;
    }

    public void setExec(List<CommandBean> exec) {
        this.exec = exec;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
}
