package com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels;

/**
 * Created by Hp on 4/26/2017.
 */

public class PingRequest {
    private MBattery battery;
    private MHardware hardware;
    private MSoftware software;
    private MGps gps;
    private MNetwork network;

    public PingRequest(MBattery battery, MHardware hardware, MSoftware software, MGps gps, MNetwork network) {
        this.setBattery(battery);
        this.setHardware(hardware);
        this.setSoftware(software);
        this.setGps(gps);
        this.setNetwork(network);
    }

    public PingRequest() {

    }

    public MBattery getBattery() {
        return battery;
    }

    public void setBattery(MBattery battery) {
        this.battery = battery;
    }

    public MHardware getHardware() {
        return hardware;
    }

    public void setHardware(MHardware hardware) {
        this.hardware = hardware;
    }

    public MSoftware getSoftware() {
        return software;
    }

    public void setSoftware(MSoftware software) {
        this.software = software;
    }

    public MGps getGps() {
        return gps;
    }

    public void setGps(MGps gps) {
        this.gps = gps;
    }

    public MNetwork getNetwork() {
        return network;
    }

    public void setNetwork(MNetwork network) {
        this.network = network;
    }
}
