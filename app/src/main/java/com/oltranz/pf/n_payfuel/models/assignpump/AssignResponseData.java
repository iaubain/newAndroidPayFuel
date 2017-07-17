package com.oltranz.pf.n_payfuel.models.assignpump;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oltranz.pf.n_payfuel.models.commonmodels.StatusModel;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/15/2017.
 */

public class AssignResponseData {
    @JsonProperty("assignUserPumpNozzle")
    private AssignPumpRequest assignPumpRequest;
    @JsonProperty("status")
    private StatusModel statusModel;

    public AssignResponseData() {
    }

    public AssignResponseData(AssignPumpRequest assignPumpRequest, StatusModel statusModel) {
        this.setAssignPumpRequest(assignPumpRequest);
        this.setStatusModel(statusModel);
    }

    public AssignPumpRequest getAssignPumpRequest() {
        return assignPumpRequest;
    }

    public void setAssignPumpRequest(AssignPumpRequest assignPumpRequest) {
        this.assignPumpRequest = assignPumpRequest;
    }

    public StatusModel getStatusModel() {
        return statusModel;
    }

    public void setStatusModel(StatusModel statusModel) {
        this.statusModel = statusModel;
    }
}
