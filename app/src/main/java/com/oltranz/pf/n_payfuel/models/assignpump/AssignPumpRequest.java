package com.oltranz.pf.n_payfuel.models.assignpump;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/15/2017.
 */

public class AssignPumpRequest {
    private String userId;
    private String pumpId;
    private String nozzleId;

    public AssignPumpRequest() {
    }

    public AssignPumpRequest(String userId, String pumpId, String nozzleId) {
        this.setUserId(userId);
        this.setPumpId(pumpId);
        this.setNozzleId(nozzleId);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
