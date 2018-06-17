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
public class User {

    private String username;
    private String passworod;

    public User(String username, String passworod) {
        this.username = username;
        this.passworod = passworod;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassworod() {
        return passworod;
    }

    public void setPassworod(String passworod) {
        this.passworod = passworod;
    }
}
