package com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels;

/**
 * Created by Hp on 4/25/2017.
 */

public class MHardware {
    private String serialNumber;
    private String osVersion;
    private String osRelease;
    private String brand;
    private String manufacturer;
    private String accessDate;

    public MHardware() {
    }

    public MHardware(String serialNumber, String osVersion, String osRelease, String brand, String manufacturer, String accessDate) {
        this.serialNumber = serialNumber;
        this.osVersion = osVersion;
        this.osRelease = osRelease;
        this.setBrand(brand);
        this.setManufacturer(manufacturer);
        this.accessDate = accessDate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsRelease() {
        return osRelease;
    }

    public void setOsRelease(String osRelease) {
        this.osRelease = osRelease;
    }

    public String getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}
