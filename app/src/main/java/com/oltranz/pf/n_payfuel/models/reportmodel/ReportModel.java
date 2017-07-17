package com.oltranz.pf.n_payfuel.models.reportmodel;

import com.oltranz.pf.n_payfuel.entities.MUser;

import java.util.LinkedHashMap;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/29/2017.
 */

public class ReportModel {
    private LinkedHashMap<String, Long> transactionCount;
    private LinkedHashMap<String, Double> paymentCount;
    private LinkedHashMap<String, Double> quantityCount;
    private Double totalSoldAmount;
    private long totalTransaction;
    private String date;
    private MUser user;

    public ReportModel() {
    }

    public ReportModel(LinkedHashMap<String, Long> transactionCount, LinkedHashMap<String, Double> paymentCount, LinkedHashMap<String, Double> quantityCount, Double totalSoldAmount, long totalTransaction, String date, MUser user) {
        this.setTransactionCount(transactionCount);
        this.setPaymentCount(paymentCount);
        this.setQuantityCount(quantityCount);
        this.setTotalSoldAmount(totalSoldAmount);
        this.setTotalTransaction(totalTransaction);
        this.setDate(date);
        this.setUser(user);
    }


    public LinkedHashMap<String, Long> getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(LinkedHashMap<String, Long> transactionCount) {
        this.transactionCount = transactionCount;
    }

    public LinkedHashMap<String, Double> getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(LinkedHashMap<String, Double> paymentCount) {
        this.paymentCount = paymentCount;
    }

    public LinkedHashMap<String, Double> getQuantityCount() {
        return quantityCount;
    }

    public void setQuantityCount(LinkedHashMap<String, Double> quantityCount) {
        this.quantityCount = quantityCount;
    }

    public Double getTotalSoldAmount() {
        return totalSoldAmount;
    }

    public void setTotalSoldAmount(Double totalSoldAmount) {
        this.totalSoldAmount = totalSoldAmount;
    }

    public long getTotalTransaction() {
        return totalTransaction;
    }

    public void setTotalTransaction(long totalTransaction) {
        this.totalTransaction = totalTransaction;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public MUser getUser() {
        return user;
    }

    public void setUser(MUser user) {
        this.user = user;
    }
}
