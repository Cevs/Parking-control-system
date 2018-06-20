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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.ServletContext;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.rest.klijenti.Application3REST;
import org.foi.nwtis.alemartin.web.podaci.User;
import org.foi.nwtis.alemartin.web.utils.SessionUtils;

/**
 *
 * @author alemartin
 */
@Named(value = "profile")
@SessionScoped
public class Profile implements Serializable {

    private int userId;
    private String firstname;
    private String lastname;
    private String username;

    private List<User> users = new ArrayList<>();
    private List<User> usersToShow = new ArrayList<>();
    private int recordsPerPage;
    private int numberOfUsers;
    private int pageIndex;
    private int lastPageIndex;
    private boolean showButtonNext;
    private boolean showButtonPrevious;

    Application3REST app3REST;

    public Profile() {
        ServletContext sc = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        Konfiguracija config = (Konfiguracija) sc.getAttribute("konfiguracija_aplikacije");
        recordsPerPage = Integer.parseInt(config.dajPostavku("stranicenje.brojZapisa"));
        app3REST = new Application3REST(SessionUtils.getUsername(), SessionUtils.getPassword());
        fetchUsers();
    }

    private void fetchUsers() {
        users.clear();
        String response = app3REST.getAllUsers();
        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject jsonObject = jsonReader.readObject();
        JsonArray jsonArray = jsonObject.getJsonArray("odgovor");

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject userObject = jsonArray.getJsonObject(i);
            User user = new User(
                    userObject.getInt("id"),
                    userObject.getString("ki"),
                    userObject.getString("ime"),
                    userObject.getString("prezime")
            );
            users.add(user);

            if (user.getUsername().equals(SessionUtils.getUsername())) {
                userId = user.getId();
                username = user.getUsername();
                firstname = user.getFirstname();
                lastname = user.getLastname();
            }
        }
        numberOfUsers = users.size();
        pageIndex = 0;
        lastPageIndex = (int) Math.ceil((float) numberOfUsers / recordsPerPage) - 1;
        refreshView();
    }

    private void refreshView() {
        int start = pageIndex * recordsPerPage;
        int end = start + recordsPerPage;
        usersToShow.clear();

        for (int i = start; i < end; i++) {
            if (i >= numberOfUsers) {
                break;
            }
            usersToShow.add(users.get(i));
        }

        if (pageIndex > 0 && pageIndex <= lastPageIndex - 1) {
            showButtonPrevious = true;
            showButtonNext = true;
        } else if (lastPageIndex - 1 > 0 && !(pageIndex <= lastPageIndex - 1)) {
            showButtonPrevious = true;
            showButtonNext = false;
        } else if (!(pageIndex > 0) && pageIndex <= lastPageIndex - 1) {
            showButtonPrevious = false;
            showButtonNext = true;
        } else {
            showButtonPrevious = false;
            showButtonNext = false;
        }

    }

    public void updateProfileInfo() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (firstname.isEmpty() || lastname.isEmpty()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "First name and last name cannot be empty", ""));
            return;
        }

        JsonObjectBuilder jsonObject = Json.createObjectBuilder();
        jsonObject.add("prezime", lastname);
        jsonObject.add("ime", firstname);       
        String request = jsonObject.build().toString();

        String response = app3REST.updateUser(request);
        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject responseObject = jsonReader.readObject();
        String status = responseObject.getString("status");

        if (status.contains("OK")) {
            fetchUsers();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "User updated", ""));
        }else{
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Update failed", ""));
        }
    }

    public void nextPage() {
        pageIndex++;
        refreshView();
    }

    public void previousPage() {
        pageIndex--;
        refreshView();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<User> getUsersToShow() {
        return usersToShow;
    }

    public void setUsersToShow(List<User> usersToShow) {
        this.usersToShow = usersToShow;
    }

    public int getRecordsPerPage() {
        return recordsPerPage;
    }

    public void setRecordsPerPage(int recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getLastPageIndex() {
        return lastPageIndex;
    }

    public void setLastPageIndex(int lastPageIndex) {
        this.lastPageIndex = lastPageIndex;
    }

    public boolean isShowButtonNext() {
        return showButtonNext;
    }

    public void setShowButtonNext(boolean showButtonNext) {
        this.showButtonNext = showButtonNext;
    }

    public boolean isShowButtonPrevious() {
        return showButtonPrevious;
    }

    public void setShowButtonPrevious(boolean showButtonPrevious) {
        this.showButtonPrevious = showButtonPrevious;
    }

    /* Navigation */
    public String home() {
        return "home";
    }
    
    public String status(){
        return "status";
    }
    
    public String inbox(){
        return "inbox";
    }
    
    public String log(){
        return "log";
    }
    
    public String parkings(){
        return "parkings";
    }
}
