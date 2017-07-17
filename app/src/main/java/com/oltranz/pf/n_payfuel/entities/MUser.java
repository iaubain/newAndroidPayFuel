package com.oltranz.pf.n_payfuel.entities;

import com.orm.SugarRecord;

/**
 * Created by Hp on 6/7/2017.
 */

public class MUser extends SugarRecord {
    private String userPin;
    private String userId;
    private String name;
    private String permission;
    private String branchId;
    private String branchName;
    private String sessionStatus;

    public MUser() {

    }

    public MUser(String userPin, String userId, String name, String permission, String branchId, String branchName, String sessionStatus) {
        this.setUserPin(userPin);
        this.setUserId(userId);
        this.setName(name);
        this.setPermission(permission);
        this.setBranchId(branchId);
        this.setBranchName(branchName);
        this.setSessionStatus(sessionStatus);
    }


    public String getUserPin() {
        return userPin;
    }

    public void setUserPin(String userPin) {
        this.userPin = userPin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
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

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }
}
