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
public class Parking {
    private int id;
    private String name;
    private String address;
    private Location geoLoc;
    private int parkingSlotCount;
    private int takenParkingSlotCount;
    private String user;

    public Parking() {
    }

    public Parking(int id, String name, String address, Location geoLoc, int parkingSlotCount, int takenParkingSlotCount, String user) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.geoLoc = geoLoc;
        this.parkingSlotCount = parkingSlotCount;
        this.takenParkingSlotCount = takenParkingSlotCount;
        this.user = user;
    }

 
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getGeoLoc() {
        return geoLoc;
    }

    public void setGeoLoc(Location geoLoc) {
        this.geoLoc = geoLoc;
    }

    public int getParkingSlotCount() {
        return parkingSlotCount;
    }

    public void setParkingSlotCount(int parkingSlotCount) {
        this.parkingSlotCount = parkingSlotCount;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getTakenParkingSlotCount() {
        return takenParkingSlotCount;
    }

    public void setTakenParkingSlotCount(int takenParkingSlotCount) {
        this.takenParkingSlotCount = takenParkingSlotCount;
    }

    @Override
    public String toString() {
        return "Parking{" + "id=" + id + ", name=" + name + ", address=" + address + ", geoLoc=" + geoLoc + ", parkingSlotCount=" + parkingSlotCount + ", takenParkingSlotCount=" + takenParkingSlotCount + ", user=" + user + '}';
    }
}
