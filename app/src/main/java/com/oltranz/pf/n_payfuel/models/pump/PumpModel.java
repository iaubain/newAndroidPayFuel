package com.oltranz.pf.n_payfuel.models.pump;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Hp on 6/8/2017.
 */

public class PumpModel {
    private String pumpId;
    private String pumpName;
    private String branchId;
    private String branchName;
    private String status;
    @JsonProperty("nozzleDash")
    private List<NozzleModel> nozzleList;

    public PumpModel() {
    }

    public PumpModel(String pumpId, String pumpName, String branchId, String branchName, String status, List<NozzleModel> nozzleList) {
        this.pumpId = pumpId;
        this.pumpName = pumpName;
        this.branchId = branchId;
        this.branchName = branchName;
        this.status = status;
        this.nozzleList = nozzleList;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PumpModel)) return false;

        PumpModel pumpModel = (PumpModel) object;

        if (!getPumpId().equals(pumpModel.getPumpId())) return false;
        if (!getPumpName().equals(pumpModel.getPumpName())) return false;
        if (!getBranchId().equals(pumpModel.getBranchId())) return false;
        if (!getBranchName().equals(pumpModel.getBranchName())) return false;
        if (!getStatus().equals(pumpModel.getStatus())) return false;
        return getNozzleList().equals(pumpModel.getNozzleList());

    }

    @Override
    public int hashCode() {
        int result = getPumpId().hashCode();
        result = 31 * result + getPumpName().hashCode();
        result = 31 * result + getBranchId().hashCode();
        result = 31 * result + getBranchName().hashCode();
        result = 31 * result + getStatus().hashCode();
        result = 31 * result + getNozzleList().hashCode();
        return result;
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

    public List<NozzleModel> getNozzleList() {
        return nozzleList;
    }

    public void setNozzleList(List<NozzleModel> nozzleList) {
        this.nozzleList = nozzleList;
    }
}
