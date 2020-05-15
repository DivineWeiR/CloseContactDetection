package com.seucpss.contact_detection.Bean;

import java.io.Serializable;

public class Position implements Serializable {
    private String Pname;
    private long starttime;
    private long endtime;
    private double Plat;
    private double Plon;
    private int id;

    private static final long serialVersionUID = 8633299996744734593L;

    public Position(String pname , double lat, double lon, long start, long end) {
        Pname = pname;
        Plat= lat;
        Plon=lon;
        starttime=start;
        endtime=end;


    }

    public String getPname() {
        return Pname;
    }

    public void setPname(String pname) {
        Pname = pname;
    }

    public double getLat() {
        return Plat;
    }

    public void setLat(double lat) {
        this.Plat = lat;
    }

    public double getLon() {
        return Plon;
    }

    public void setLon(double lon) {
        this.Plon = lon;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public int getId(){return id;}

    public void setId(int id) {
        this.id = id;
    }
}
