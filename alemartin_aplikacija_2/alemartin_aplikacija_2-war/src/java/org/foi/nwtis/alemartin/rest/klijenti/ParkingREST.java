/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.rest.klijenti;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 *
 * @author TOSHIBA
 */
public class ParkingREST {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/alemartin_aplikacija_1/webresources";

    public ParkingREST() {
        client = ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("parkiralista");
    }

    public ParkingREST(String username, String pass) {
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(username, pass);
        client = javax.ws.rs.client.ClientBuilder.newClient();
        client.register(feature);
        webTarget = client.target(BASE_URI).path("parkiralista");
    }

    public String getParking(String id) {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public String addParking(Object requestEntity) {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }

    public String getParkingStatus(String id) {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/stanje", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public String getParkingVehicles(String id) {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/vozila", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public String activateParking(String id) {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/aktiviraj", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public String getAllParkings() {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public String blockParking(String id) {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/blokiraj", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public String updateParking(Object requestEntity, String id){
        return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }

    public String deleteParking(String id) {
        return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request().delete(String.class);
    }

    public void close() {
        client.close();
    }
}
