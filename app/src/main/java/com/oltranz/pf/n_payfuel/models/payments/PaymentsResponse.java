package com.oltranz.pf.n_payfuel.models.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oltranz.pf.n_payfuel.entities.MPayment;

import java.util.List;

/**
 * Created by Hp on 6/8/2017.
 */

public class PaymentsResponse {
    @JsonProperty("PaymentMode")
    private
    List<MPayment> mPayments;
    private String message;
    private int statusCode;

    public PaymentsResponse() {
    }

    public PaymentsResponse(List<MPayment> mPayments, String message, int statusCode) {
        this.setmPayments(mPayments);
        this.setMessage(message);
        this.setStatusCode(statusCode);
    }

    public List<MPayment> getmPayments() {
        return mPayments;
    }

    public void setmPayments(List<MPayment> mPayments) {
        this.mPayments = mPayments;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
