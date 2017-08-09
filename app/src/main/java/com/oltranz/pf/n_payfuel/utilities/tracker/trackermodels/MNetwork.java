package com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels;

/**
 * Created by Hp on 4/25/2017.
 */

public class MNetwork {
    private String simId;
    private String imei;
    private String countryCode;
    private String network;
    private String networkType;
    private String carrierName;
    private String connectivity;
    private String accessDate;

    public MNetwork() {
    }

    public MNetwork(String simId, String imei, String countryCode, String network, String networkType, String carrierName, String connectivity, String accessDate) {
        this.simId = simId;
        this.imei = imei;
        this.countryCode = countryCode;
        this.network = network;
        this.networkType = networkType;
        this.carrierName = carrierName;
        this.connectivity = connectivity;
        this.accessDate = accessDate;
    }

    public String getSimId() {
        return simId;
    }

    public void setSimId(String simId) {
        this.simId = simId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getConnectivity() {
        return connectivity;
    }

    public void setConnectivity(String connectivity) {
        this.connectivity = connectivity;
    }

    public String getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }
}
