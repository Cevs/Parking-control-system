/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import org.foi.nwtis.alemartin.socket.SocketClient;
import org.foi.nwtis.alemartin.web.utils.SessionUtils;

/**
 *
 * @author TOSHIBA
 */
@Named(value = "status")
@SessionScoped
public class Status implements Serializable {

    private String serverStatus;
    private String groupStatus;

    private String username;
    private String password;

    private boolean buttonsDisabled;

    public Status() {
        buttonsDisabled = false;
        username = SessionUtils.getUsername();
        password = SessionUtils.getPassword();
        getCurrentServerStatus();
        getCurrentGroupStatus();
    }

    public void getCurrentServerStatus() {
        String request = createRequest("STANJE");
        SocketClient sc = new SocketClient();
        String status = sc.sendRequest(request);

        if (status.contains("OK 11")) {
            serverStatus = "Preuzima sve komande i preuzima meteo podatke";
        } else if (status.contains("OK 12")) {
            serverStatus = "Preuzima sve komande i ne preuzima meteo podatke";
        } else if (status.contains("OK 13")) {
            serverStatus = "Preuzima samo poslužiteljske komande i preuzima meteo podatke";
        } else if (status.contains("OK 14")) {
            serverStatus = "Preuzima samo poslužiteljske komande i ne preuzima meteo podatke";
        }
    }

    public void pauseServer() {
        String request = createRequest("PAUZA");
        sendRequestToServer(request);
        getCurrentServerStatus();
    }

    public void startServer() {
        String request = createRequest("KRENI");
        sendRequestToServer(request);
        getCurrentServerStatus();
    }

    public void passiveServer() {
        String request = createRequest("PASIVNO");
        sendRequestToServer(request);
        getCurrentServerStatus();
    }

    public void activeServer() {
        String request = createRequest("AKTIVNO");
        sendRequestToServer(request);
        getCurrentServerStatus();
    }

    public void stopServer() {
        String request = createRequest("STANI");
        sendRequestToServer(request);
        serverStatus = "Server ugašen";
        buttonsDisabled = true;
    }

    public void getCurrentGroupStatus() {
        String request = createRequest("GRUPA STANJE");
        SocketClient sc = new SocketClient();
        String status = sc.sendRequest(request);

        if (status.contains("OK 21")) {
            groupStatus = "Grupa je aktivna";
        } else if (status.contains("OK 22")) {
            groupStatus = "Grupa je blokirana";
        } else if (status.contains("OK 23")) {
            groupStatus = "Grupa je registrirana";
        } else if (status.contains("ERR 21")) {
            groupStatus = "Grupa ne postoji (deregistrirana)";
        }
    }

    public void registerGroup() {
        String request = createRequest("GRUPA DODAJ");
        sendRequestToServer(request);
        getCurrentGroupStatus();
    }

    public void unregisterGroup() {
        String request = createRequest("GRUPA PREKID");
        sendRequestToServer(request);
        getCurrentGroupStatus();;
    }

    public void activeGrup() {
        String request = createRequest("GRUPA KRENI");
        sendRequestToServer(request);
        getCurrentGroupStatus();
    }

    public void blockGroup() {
        String request = createRequest("GRUPA PAUZA");
        sendRequestToServer(request);
        getCurrentGroupStatus();
    }

    private void sendRequestToServer(String request) {
        SocketClient sc = new SocketClient();
        sc.sendRequest(request);
    }

    private String createRequest(String command) {
        return "KORISNIK " + username + "; LOZINKA " + password + "; " + command + ";";
    }

    public String getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }

    public String getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(String groupStatus) {
        this.groupStatus = groupStatus;
    }

    public boolean isButtonsDisabled() {
        return buttonsDisabled;
    }

    public void setButtonsDisabled(boolean buttonsDisabled) {
        this.buttonsDisabled = buttonsDisabled;
    }

    /* Navigation */
    public String profile() {
        return "profile";
    }

    public String home() {
        return "home";
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
