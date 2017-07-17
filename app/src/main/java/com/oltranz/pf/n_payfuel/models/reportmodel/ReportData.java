package com.oltranz.pf.n_payfuel.models.reportmodel;

import com.oltranz.pf.n_payfuel.entities.MNozzle;
import com.oltranz.pf.n_payfuel.entities.MPayment;
import com.oltranz.pf.n_payfuel.entities.MSales;

import java.util.List;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/29/2017.
 */

public class ReportData {
    private List<MPayment> mPayments;
    private List<MNozzle> mNozzles;
    private List<MSales> mSalesList;

    public ReportData() {
    }

    public ReportData(List<MPayment> mPayments, List<MNozzle> mNozzles, List<MSales> mSalesList) {
        this.setmPayments(mPayments);
        this.setmNozzles(mNozzles);
        this.setmSalesList(mSalesList);
    }

    public List<MPayment> getmPayments() {
        return mPayments;
    }

    public void setmPayments(List<MPayment> mPayments) {
        this.mPayments = mPayments;
    }

    public List<MNozzle> getmNozzles() {
        return mNozzles;
    }

    public void setmNozzles(List<MNozzle> mNozzles) {
        this.mNozzles = mNozzles;
    }

    public List<MSales> getmSalesList() {
        return mSalesList;
    }

    public void setmSalesList(List<MSales> mSalesList) {
        this.mSalesList = mSalesList;
    }
}
