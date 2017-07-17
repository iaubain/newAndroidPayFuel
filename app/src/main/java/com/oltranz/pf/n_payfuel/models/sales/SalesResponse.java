package com.oltranz.pf.n_payfuel.models.sales;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oltranz.pf.n_payfuel.entities.MSales;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/21/2017.
 */

public class SalesResponse {
    @JsonProperty("SaleDetailsModel")
    private MSales mSales;
    private String message;
    private int statusCode;

    public SalesResponse() {
    }

    public SalesResponse(MSales mSales, String message, int statusCode) {
        this.setmSales(mSales);
        this.setMessage(message);
        this.setStatusCode(statusCode);
    }

    public MSales getmSales() {
        return mSales;
    }

    public void setmSales(MSales mSales) {
        this.mSales = mSales;
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
