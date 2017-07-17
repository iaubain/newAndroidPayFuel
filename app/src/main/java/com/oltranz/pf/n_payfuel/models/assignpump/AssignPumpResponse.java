package com.oltranz.pf.n_payfuel.models.assignpump;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/15/2017.
 */

public class AssignPumpResponse {
    @JsonProperty("AssignUserPumpNozzle")
    private
    List<AssignResponseData> responseDatas;
    private String message;
    private int statusCode;

    public AssignPumpResponse() {
    }

    public AssignPumpResponse(List<AssignResponseData> responseDatas, String message, int statusCode) {
        this.setResponseDatas(responseDatas);
        this.setMessage(message);
        this.setStatusCode(statusCode);
    }

    public List<AssignResponseData> getResponseDatas() {
        return responseDatas;
    }

    public void setResponseDatas(List<AssignResponseData> responseDatas) {
        this.responseDatas = responseDatas;
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
