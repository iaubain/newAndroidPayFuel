package com.oltranz.pf.n_payfuel.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orm.SugarRecord;

/**
 * Created by Hp on 6/7/2017.
 */

public class MNozzle extends SugarRecord {
    private String nozzleId;
    private String nozzleName;
    private String indexCount;
    private String lastUsed;
    private String productId;
    private String productName;
    private String unitPrice;
    private String userName;
    private String status;
    @JsonIgnore
    private MPump mPump;

    public MNozzle() {
    }

    public MNozzle(String nozzleId, String nozzleName, String indexCount, String lastUsed, String productId, String productName, String unitPrice, String userName, String status, MPump mPump) {
        this.setNozzleId(nozzleId);
        this.setNozzleName(nozzleName);
        this.setIndexCount(indexCount);
        this.setLastUsed(lastUsed);
        this.setProductId(productId);
        this.setProductName(productName);
        this.setUnitPrice(unitPrice);
        this.setUserName(userName);
        this.setStatus(status);
        this.setmPump(mPump);
    }

    public String getNozzleId() {
        return nozzleId;
    }

    public void setNozzleId(String nozzleId) {
        this.nozzleId = nozzleId;
    }

    public String getNozzleName() {
        return nozzleName;
    }

    public void setNozzleName(String nozzleName) {
        this.nozzleName = nozzleName;
    }

    public String getIndexCount() {
        return indexCount;
    }

    public void setIndexCount(String indexCount) {
        this.indexCount = indexCount;
    }

    public String getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(String lastUsed) {
        this.lastUsed = lastUsed;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MPump getmPump() {
        return mPump;
    }

    public void setmPump(MPump mPump) {
        this.mPump = mPump;
    }
}
