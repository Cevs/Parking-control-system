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
public class Vehicle {

    private String registrationPlate;
    private String action;

    public Vehicle(String registrationPlate, String action) {
        this.registrationPlate = registrationPlate;
        this.action = action;
    }

    public String getRegistrationPlate() {
        return registrationPlate;
    }

    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "Vehicle{" + "registrationPlate=" + registrationPlate + ", action=" + action + '}';
    }

}
