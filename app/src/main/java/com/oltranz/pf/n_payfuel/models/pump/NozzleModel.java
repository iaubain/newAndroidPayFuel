package com.oltranz.pf.n_payfuel.models.pump;

/**
 * Created by Hp on 6/8/2017.
 */

public class NozzleModel {
    private String nozzleId;
    private String nozzleName;
    private String indexCount;
    private String lastUsed;
    private String productId;
    private String productName;
    private String unitPrice;
    private String userName;
    private String status;

    public NozzleModel() {
    }

    public NozzleModel(String nozzleId, String nozzleName, String indexCount, String lastUsed, String productId, String productName, String unitPrice, String userName, String status) {
        this.setNozzleId(nozzleId);
        this.setNozzleName(nozzleName);
        this.setIndexCount(indexCount);
        this.setLastUsed(lastUsed);
        this.setProductId(productId);
        this.setProductName(productName);
        this.setUnitPrice(unitPrice);
        this.setUserName(userName);
        this.setStatus(status);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof NozzleModel)) return false;

        NozzleModel that = (NozzleModel) object;

        if (getNozzleId() != null ? !getNozzleId().equals(that.getNozzleId()) : that.getNozzleId() != null)
            return false;
        if (getNozzleName() != null ? !getNozzleName().equals(that.getNozzleName()) : that.getNozzleName() != null)
            return false;
        if (getIndexCount() != null ? !getIndexCount().equals(that.getIndexCount()) : that.getIndexCount() != null)
            return false;
        if (getProductId() != null ? !getProductId().equals(that.getProductId()) : that.getProductId() != null)
            return false;
        if (getProductName() != null ? !getProductName().equals(that.getProductName()) : that.getProductName() != null)
            return false;
        return getUnitPrice() != null ? getUnitPrice().equals(that.getUnitPrice()) : that.getUnitPrice() == null;

    }

    @Override
    public int hashCode() {
        int result = getNozzleId() != null ? getNozzleId().hashCode() : 0;
        result = 31 * result + (getNozzleName() != null ? getNozzleName().hashCode() : 0);
        result = 31 * result + (getIndexCount() != null ? getIndexCount().hashCode() : 0);
        result = 31 * result + (getProductId() != null ? getProductId().hashCode() : 0);
        result = 31 * result + (getProductName() != null ? getProductName().hashCode() : 0);
        result = 31 * result + (getUnitPrice() != null ? getUnitPrice().hashCode() : 0);
        return result;
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
}
