package com.oltranz.pf.n_payfuel.models.pump;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Hp on 6/8/2017.
 */

public class PumpResponse {
    @JsonProperty("PumpDash")
    private List<PumpModel> pumpModel;
    private String message;
    private int statusCode;

    public PumpResponse() {
    }

    public PumpResponse(List<PumpModel> pumpModel, String message, int statusCode) {
        this.setPumpModel(pumpModel);
        this.setMessage(message);
        this.setStatusCode(statusCode);
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

    public List<PumpModel> getPumpModel() {
        return pumpModel;
    }

    public void setPumpModel(List<PumpModel> pumpModel) {
        this.pumpModel = pumpModel;
    }
}
