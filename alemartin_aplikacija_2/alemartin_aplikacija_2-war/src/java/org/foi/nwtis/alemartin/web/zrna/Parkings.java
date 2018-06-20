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
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import org.foi.nwtis.alemartin.rest.klijenti.ParkingREST;
import org.foi.nwtis.alemartin.web.podaci.Location;
import org.foi.nwtis.alemartin.web.podaci.Parking;
import org.foi.nwtis.alemartin.web.podaci.Vehicle;
import org.foi.nwtis.alemartin.web.utils.SessionUtils;
import org.foi.nwtis.alemartin.ws.klijenti.MeteoPodaci;
import org.foi.nwtis.alemartin.ws.klijenti.MeteoWS;

/**
 *
 * @author TOSHIBA
 */
@Named(value = "parkings")
@SessionScoped
public class Parkings implements Serializable {

    private int parkingId;
    private String newParkingName;
    private String newParkingAddress;
    private String newParkingSlots;
    private String parkingName;
    private String parkingAddress;
    private String parkingSlots;

    private ParkingREST parkingREST;
    private List<Parking> parkingList = new ArrayList<>();
    private String selectedParking;

    private List<Vehicle> vehicleList = new ArrayList<>();
    private List<MeteoPodaci> meteoList = new ArrayList<>();

    private String messageSuccess;
    private String messageError;
    private String messageAddError;
    private String messageAddSuccess;
    private String messageInfo;

    public Parkings() {
        parkingREST = new ParkingREST(SessionUtils.getUsername(), SessionUtils.getPassword());
        fetchParkings();
    }

    private void fetchParkings() {
        parkingList.clear();
        String response = parkingREST.getAllParkings();

        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject jsonObject = jsonReader.readObject();
        JsonArray array = jsonObject.getJsonArray("odgovor");
        for (int i = 0; i < array.size(); i++) {
            JsonObject parkingObject = array.getJsonObject(i);
            Parking p = new Parking();
            p.setId(parkingObject.getInt("id"));
            p.setName(parkingObject.getString("naziv"));
            p.setAddress(parkingObject.getString("adresa"));
            p.setGeoLoc(new Location(parkingObject.getString("latitude"), parkingObject.getString("longitude")));
            p.setParkingSlotCount(parkingObject.getInt("brojParkirnihMjesta"));
            parkingList.add(p);
        }
    }

    public void addNewParking() {
        unsetMessages();
        if (newParkingName.isEmpty() || newParkingAddress.isEmpty() || newParkingSlots.isEmpty()) {
            messageAddError = "Fields must not be empty";
            return;
        }

        JsonObjectBuilder jsonObjectResponse = Json.createObjectBuilder();
        jsonObjectResponse.add("naziv", newParkingName);
        jsonObjectResponse.add("adresa", newParkingAddress);
        jsonObjectResponse.add("ukupnoMjesta", newParkingSlots);
        String request = jsonObjectResponse.build().toString();
        String response = parkingREST.addParking(request);
        String status = getResponseValue(response, "status");
        if (status.contains("OK")) {
            newParkingName = "";
            newParkingAddress = "";
            newParkingSlots = "";
            messageAddSuccess = "Parking successfully added";
            fetchParkings();
        } else {
            messageAddError = "Parking lot already exist";
        }

    }

    public void fetchVehicles() {
        unsetMessages();
        vehicleList.clear();
        String response = parkingREST.getParkingVehicles(String.valueOf(parkingId));
        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject jsonObject = jsonReader.readObject();
        String status = jsonObject.getString("status");

        if (status.contains("OK")) {
            JsonArray arr = jsonObject.getJsonArray("odgovor");
            for (int i = 0; i < arr.size(); i++) {
                JsonObject vehicleObject = arr.getJsonObject(i);
                Vehicle v = new Vehicle(vehicleObject.getString("naziv"), vehicleObject.getString("akcija"));
                vehicleList.add(v);
            }
        } else {
            messageError = "Parking slot does not have vehicles";
        }
    }

    public void validMeteo() {
        meteoList.clear();
        MeteoPodaci mp = MeteoWS.dajVazeceMeteoPodatke(SessionUtils.getUsername(), SessionUtils.getPassword(), parkingId);
        if (mp != null) {
            meteoList.add(mp);
        }
    }

    public void lastMeteo() {
        meteoList.clear();
        MeteoPodaci mp = MeteoWS.dajZadnjePreuzeteMeteoPodatke(SessionUtils.getUsername(), SessionUtils.getPassword(), parkingId);
        if (mp != null) {
            meteoList.add(mp);
        }
    }

    public void updateParking() {
        unsetMessages();
        if (parkingName.isEmpty() || parkingAddress.isEmpty() || parkingSlots.isEmpty()) {
            messageError = "Fields must not be empty";
            return;
        }

        JsonObjectBuilder jsonObjectResponse = Json.createObjectBuilder();
        jsonObjectResponse.add("naziv", parkingName);
        jsonObjectResponse.add("adresa", parkingAddress);
        jsonObjectResponse.add("ukupnoMjesta", parkingSlots);
        String request = jsonObjectResponse.build().toString();
        String response = parkingREST.updateParking(request, String.valueOf(parkingId));
        String status = getResponseValue(response, "status");
        if (status.contains("OK")) {
            messageSuccess = "Parking slot successful update";
            fetchParkings();;
        } else {
            messageError = "Error with updating parking slot";
        }
    }

    public void selectParking() {
        vehicleList.clear();
        meteoList.clear();

        for (Parking p : parkingList) {
            if (p.getId() == Integer.parseInt(selectedParking)) {
                parkingId = Integer.parseInt(selectedParking);
                parkingName = p.getName();
                parkingAddress = p.getAddress();
                parkingSlots = String.valueOf(p.getParkingSlotCount());

                break;
            }
        }
    }

    public void activateParking() {
        unsetMessages();
        parkingREST.activateParking(String.valueOf(parkingId));
        messageInfo = "Parking slot activated";
    }

    public void blockParking() {
        unsetMessages();
        parkingREST.blockParking(String.valueOf(parkingId));
        messageInfo = "Parking slot blockete";
    }

    public void getStatusOfParking() {
        unsetMessages();
        String response = parkingREST.getParkingStatus(String.valueOf(parkingId));
        String status = getResponseValue(response, "odgovor");

        if (status.contains("Aktivan")) {
            messageSuccess = "Parkings status: ACTIVATED";
        } else if (status.contains("Pasivan")) {
            messageSuccess = "Parkings status: PASIVE";
        } else if (status.contains("Blokiran")) {
            messageSuccess = "Parkings status: BLOCKED";
        } else if (status.contains("Nepostoji")) {
            messageSuccess = "Parkings status: NOT EXISTING";
        } else {
            messageError = "Parkings status: UNKNOWN";
        }
    }

    public void deleteParking() {
        unsetMessages();
        String response = parkingREST.deleteParking(String.valueOf(parkingId));
        String status = getResponseValue(response, "status");
        if (status.contains("OK")) {
            messageSuccess = "Parking slot deleted";
            parkingName = "";
            parkingAddress = "";
            parkingSlots = "";
            meteoList.clear();
            vehicleList.clear();
            fetchParkings();;
        } else {
            messageError = "Parking slot cannot be deleted";
        }
    }

    private String getResponseValue(String response, String attributeName) {
        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject jsonObject = jsonReader.readObject();
        String status = jsonObject.getString(attributeName);
        return status;
    }

    private void unsetMessages() {
        messageAddError = "";
        messageAddSuccess = "";
        messageError = "";
        messageSuccess = "";
        messageInfo = "";
    }

    public String getNewParkingName() {
        return newParkingName;
    }

    public void setNewParkingName(String newParkingName) {
        this.newParkingName = newParkingName;
    }

    public String getNewParkingAddress() {
        return newParkingAddress;
    }

    public void setNewParkingAddress(String newParkingAddress) {
        this.newParkingAddress = newParkingAddress;
    }

    public String getNewParkingSlots() {
        return newParkingSlots;
    }

    public void setNewParkingSlots(String newParkingSlots) {
        this.newParkingSlots = newParkingSlots;
    }

    public List<Parking> getParkingList() {
        return parkingList;
    }

    public void setParkingList(List<Parking> parkingList) {
        this.parkingList = parkingList;
    }

    public String getSelectedParking() {
        return selectedParking;
    }

    public void setSelectedParking(String selectedParking) {
        this.selectedParking = selectedParking;
    }

    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    public List<MeteoPodaci> getMeteoList() {
        return meteoList;
    }

    public void setMeteoList(List<MeteoPodaci> meteoList) {
        this.meteoList = meteoList;
    }

    public String getParkingName() {
        return parkingName;
    }

    public int getParkingId() {
        return parkingId;
    }

    public void setParkingId(int parkingId) {
        this.parkingId = parkingId;
    }

    public void setParkingName(String parkingName) {
        this.parkingName = parkingName;
    }

    public String getParkingAddress() {
        return parkingAddress;
    }

    public void setParkingAddress(String parkingAddress) {
        this.parkingAddress = parkingAddress;
    }

    public String getParkingSlots() {
        return parkingSlots;
    }

    public void setParkingSlots(String parkingSlots) {
        this.parkingSlots = parkingSlots;
    }

    public String getMessageSuccess() {
        return messageSuccess;
    }

    public void setMessageSuccess(String messageSuccess) {
        this.messageSuccess = messageSuccess;
    }

    public String getMessageError() {
        return messageError;
    }

    public void setMessageError(String messageError) {
        this.messageError = messageError;
    }

    public String getMessageAddError() {
        return messageAddError;
    }

    public void setMessageAddError(String messageAddError) {
        this.messageAddError = messageAddError;
    }

    public String getMessageAddSuccess() {
        return messageAddSuccess;
    }

    public void setMessageAddSuccess(String messageAddSuccess) {
        this.messageAddSuccess = messageAddSuccess;
    }

    public String getMessageInfo() {
        return messageInfo;
    }

    public void setMessageInfo(String messageInfo) {
        this.messageInfo = messageInfo;
    }

    /* Navigation */
    public String home() {
        return "home";
    }

    public String profile() {
        return "profile";
    }

    public String status() {
        return "status";
    }

    public String inbox() {
        return "inbox";
    }

    public String log() {
        return "log";
    }
    
    public String mqtt(){
        return "mqtt";
    }

}
