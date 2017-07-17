package com.oltranz.pf.n_payfuel.models.sales;


/**
 * Created by Hp on 6/7/2017.
 */

public class SalesRequest {
    private String deviceTransactionId;
    private String deviceTransactionTime;
    private String branchId;
    private String userId;
    private String deviceId;
    private String pumpId;
    private String nozzleId;
    private String productId;
    private String paymentModeId;
    private String amount;
    private String quantity;
    private String plateNumber;
    private String name;
    private String telephone;
    private String tin;
    private String voucherNumber;
    private String authenticationCode;
    private String authorisationCode;
    private String status;

    public SalesRequest() {
    }

    public SalesRequest(String deviceTransactionId, String deviceTransactionTime, String branchId, String userId, String deviceId, String pumpId, String nozzleId, String productId, String paymentModeId, String amount, String quantity, String plateNumber, String name, String telephone, String tin, String voucherNumber, String authenticationCode, String authorisationCode, String status) {
        this.setDeviceTransactionId(deviceTransactionId);
        this.setDeviceTransactionTime(deviceTransactionTime);
        this.setBranchId(branchId);
        this.setUserId(userId);
        this.setDeviceId(deviceId);
        this.setPumpId(pumpId);
        this.setNozzleId(nozzleId);
        this.setProductId(productId);
        this.setPaymentModeId(paymentModeId);
        this.setAmount(amount);
        this.setQuantity(quantity);
        this.setPlateNumber(plateNumber);
        this.setName(name);
        this.setTelephone(telephone);
        this.setTin(tin);
        this.setVoucherNumber(voucherNumber);
        this.setAuthenticationCode(authenticationCode);
        this.setAuthorisationCode(authorisationCode);
        this.setStatus(status);
    }

    public String getDeviceTransactionId() {
        return deviceTransactionId;
    }

    public void setDeviceTransactionId(String deviceTransactionId) {
        this.deviceTransactionId = deviceTransactionId;
    }

    public String getDeviceTransactionTime() {
        return deviceTransactionTime;
    }

    public void setDeviceTransactionTime(String deviceTransactionTime) {
        this.deviceTransactionTime = deviceTransactionTime;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPumpId() {
        return pumpId;
    }

    public void setPumpId(String pumpId) {
        this.pumpId = pumpId;
    }

    public String getNozzleId() {
        return nozzleId;
    }

    public void setNozzleId(String nozzleId) {
        this.nozzleId = nozzleId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPaymentModeId() {
        return paymentModeId;
    }

    public void setPaymentModeId(String paymentModeId) {
        this.paymentModeId = paymentModeId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public String getAuthenticationCode() {
        return authenticationCode;
    }

    public void setAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    public String getAuthorisationCode() {
        return authorisationCode;
    }

    public void setAuthorisationCode(String authorisationCode) {
        this.authorisationCode = authorisationCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
