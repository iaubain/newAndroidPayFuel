package com.oltranz.pf.n_payfuel.models.commonmodels;

/**
 * Created by Hp on 6/8/2017.
 */

public class CommonBranch {
    private String branchId;

    public CommonBranch() {
    }

    public CommonBranch(String branchId) {
        this.setBranchId(branchId);
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }
}
