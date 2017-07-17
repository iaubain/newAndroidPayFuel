package com.oltranz.pf.n_payfuel.models.reportmodel;

import com.oltranz.pf.n_payfuel.entities.MUser;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/29/2017.
 */

public class ReportRequest {
    private MUser mUser;
    private String startDate;
    private String endDate;

    public ReportRequest() {
    }

    public ReportRequest(MUser mUser, String startDate, String endDate) {
        this.setmUser(mUser);
        this.setStartDate(startDate);
        this.setEndDate(endDate);
    }

    public MUser getmUser() {
        return mUser;
    }

    public void setmUser(MUser mUser) {
        this.mUser = mUser;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
