/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.io.StringReader;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import org.foi.nwtis.alemartin.rest.klijenti.Application3REST;

/**
 *
 * @author TOSHIBA
 */
@Named(value = "registration")
@SessionScoped
public class Registration implements Serializable {

    private String username;
    private String password;
    private String rePassword;
    private String firstname;
    private String lastname;

    Application3REST app3REST;
    String error;

    public Registration() {
        app3REST = new Application3REST();
    }

    public String registration() {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (registerUser()) {
            Flash flash = facesContext.getExternalContext().getFlash();
            flash.setKeepMessages(true);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Uspješna prijava", "Uspješna prijava"));
            return "login";
        }
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, error, error));
        return "";

    }

    private boolean registerUser() {
        if (username.isEmpty() || password.isEmpty() || rePassword.isEmpty()
                || firstname.isEmpty() || lastname.isEmpty()) {
            error = "Empty fields";
            return false;
        }

        if (!password.equals(rePassword)) {
            error = "Passwords do not match";
            return false;
        }
        String request = createJsonRequest(firstname, lastname, username, password);
        String response = app3REST.addUser(request);
        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject jsonResponse = jsonReader.readObject();
        String status = jsonResponse.getString("status");

        if (status.contains("OK")) {
            return true;
        }
        error = "User already exists";
        return false;

    }

    private String createJsonRequest(String firstname, String lastname, String username, String password) {
        JsonObjectBuilder jsonObjectRequest = Json.createObjectBuilder();
        jsonObjectRequest.add("ime", firstname);
        jsonObjectRequest.add("prezime", lastname);
        jsonObjectRequest.add("korisnickoIme", username);
        jsonObjectRequest.add("lozinka", password);
        return jsonObjectRequest.build().toString();
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

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String login() {
        return "login";
    }

}
