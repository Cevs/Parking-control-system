/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.rest.klijenti;

import java.text.MessageFormat;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 *
 * @author alemartin
 */
public class Application3REST {
    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8080/alemartin_aplikacija_3-war/webresources";

    public Application3REST() {
        client = ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("korisnici");
    }
    
    public Application3REST(String username, String password){
        HttpAuthenticationFeature httpAuthFeature = HttpAuthenticationFeature.basic(username, password);
        client = ClientBuilder.newClient();
        client.register(httpAuthFeature);
        webTarget = client.target(BASE_URI).path("korisnici");       
    }
    
    public String authentication(){
        WebTarget resource = webTarget;
        resource = resource.path("autentifikacija");
        return resource.request(MediaType.APPLICATION_JSON).get(String.class); // ili .toString() ? 
    }
        
    public String addUser(Object requestEntity){
        return webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(requestEntity, MediaType.APPLICATION_JSON), String.class);
    }
    
    public String getAllUsers(){
        return webTarget.request(MediaType.APPLICATION_JSON).get(String.class);
    }
    
    public String updateUser(Object requestEntity){
        return webTarget.request(MediaType.APPLICATION_JSON).put(Entity.entity(requestEntity, "application/json; charset=UTF-8"),String.class);
    }
}
