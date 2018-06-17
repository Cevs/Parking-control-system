/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.ejb.sb;

import java.io.StringReader;
import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.LocalBean;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.foi.nwtis.alemartin.ejb.services.UserService;
import org.foi.nwtis.alemartin.rest.klijenti.Application3REST;
import org.foi.nwtis.alemartin.web.podaci.User;

/**
 *
 * @author TOSHIBA
 */
@Stateful
@LocalBean
public class UserSessionBean implements UserService {
   
    private String username;
    private String password;
    
    @PostConstruct
    private void init(){
    }
    
    @Override
    public boolean authentication(User user) {
        Application3REST app3REST = new Application3REST(user.getUsername(), user.getPassworod());
        String response = app3REST.authentication();
        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject responseObject = jsonReader.readObject();
        String code = responseObject.getString("status");
        if (code.contains("OK")) {    
            username = user.getUsername();
            password = user.getPassworod();
            return true;
        } else {
            return false;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
