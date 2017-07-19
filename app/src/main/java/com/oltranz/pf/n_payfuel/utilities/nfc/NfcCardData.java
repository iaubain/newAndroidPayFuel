package com.oltranz.pf.n_payfuel.utilities.nfc;

/**
 * Created by ISHIMWE Aubain Consolateur. email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: +250 785 534 672 / +250 736 864 662 on 6/30/2017.
 */

public class NfcCardData {
    private String serialNumber;
    private String payload;
    private String msisdn;

    public NfcCardData() {
    }

    public NfcCardData(String serialNumber, String payload, String msisdn) {
        this.setSerialNumber(serialNumber);
        this.setPayload(payload);
        this.setMsisdn(msisdn);
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "NfcCardData{" +
                "serialNumber='" + getSerialNumber() + '\'' +
                ", payload='" + getPayload() + '\'' +
                '}';
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
}
