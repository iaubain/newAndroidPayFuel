package com.oltranz.pf.n_payfuel.entities;

import com.orm.SugarRecord;
import com.orm.util.NamingHelper;

import java.util.List;

/**
 * Created by Hp on 6/7/2017.
 */

public class MPump extends SugarRecord {
    private String pumpId;
    private String pumpName;
    private String branchId;
    private String branchName;
    private String status;

    public MPump() {
    }

    public MPump(String pumpId, String pumpName, String branchId, String branchName, String status) {
        this.setPumpId(pumpId);
        this.setPumpName(pumpName);
        this.setBranchId(branchId);
        this.setBranchName(branchName);
        this.setStatus(status);
    }

    public List<MNozzle> getNozzles(){
        return find(MNozzle.class, NamingHelper.toSQLNameDefault("mPump")+" = ?", String.valueOf(this.getId()));
    }

    public String getPumpId() {
        return pumpId;
    }

    public void setPumpId(String pumpId) {
        this.pumpId = pumpId;
    }

    public String getPumpName() {
        return pumpName;
    }

    public void setPumpName(String pumpName) {
        this.pumpName = pumpName;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
