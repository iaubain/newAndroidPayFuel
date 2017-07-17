package com.oltranz.pf.n_payfuel.models.reserve;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/9/2017.
 */

public class ReserveResponse {
    @JsonProperty("AssignUserPumpNozzle")
    private List<ReserveModel> reserveModelList;
    private String message;
    private int statusCode;

    public ReserveResponse() {
    }

    public ReserveResponse(List<ReserveModel> reserveModelList, String message, int statusCode) {
        this.setReserveModelList(reserveModelList);
        this.setMessage(message);
        this.setStatusCode(statusCode);
    }

    public List<ReserveModel> getReserveModelList() {
        return reserveModelList;
    }

    public void setReserveModelList(List<ReserveModel> reserveModelList) {
        this.reserveModelList = reserveModelList;
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
