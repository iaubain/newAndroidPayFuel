package com.oltranz.pf.n_payfuel.entities;

import com.orm.SugarRecord;

/**
 * Created by Hp on 6/7/2017.
 */

public class MPayment extends SugarRecord {
    private String paymentModeId;
    private String name;
    private String descr;
    private String status;

    public MPayment() {
    }

    public MPayment(String paymentModeId, String name, String descr, String status) {
        this.setPaymentModeId(paymentModeId);
        this.setName(name);
        this.setDescr(descr);
        this.setStatus(status);
    }

    public String getPaymentModeId() {
        return paymentModeId;
    }

    public void setPaymentModeId(String paymentModeId) {
        this.paymentModeId = paymentModeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
