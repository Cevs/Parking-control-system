/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.podaci;

/**
 *
 * @author TOSHIBA
 */
public class Location {
    private String latitude;
    private String longitude;

    public Location() {
    }

    public Location(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }     

    @Override
    public String toString() {
        return "Location{" + "latitude=" + latitude + ", longitude=" + longitude + '}';
    }
     
}
