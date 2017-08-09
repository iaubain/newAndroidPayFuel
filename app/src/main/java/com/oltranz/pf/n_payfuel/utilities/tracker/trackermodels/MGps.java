package com.oltranz.pf.n_payfuel.utilities.tracker.trackermodels;

/**
 * Created by Hp on 4/25/2017.
 */

public class MGps {
    private String lon;
    private String lat;
    private String accessDate;

    public MGps() {
    }

    public MGps(String lon, String lat, String accessDate) {
        this.setLon(lon);
        this.setLat(lat);
        this.setAccessDate(accessDate);
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(String accessDate) {
        this.accessDate = accessDate;
    }
}
