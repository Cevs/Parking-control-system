/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.socket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.slusaci.SlusacAplikacije;


/**
 *
 * @author alemartin
 */
public class SocketClient {

    private String ipAdress;
    private int port;

    public SocketClient() {
        Konfiguracija konfiguracija
                = (Konfiguracija) SlusacAplikacije.servletContext.getAttribute("konfiguracija_aplikacije");
        ipAdress = konfiguracija.dajPostavku("server.ip");
        port = Integer.parseInt(konfiguracija.dajPostavku("server.socket.port"));
    }

    public String sendRequest(String request) {

        try (Socket clientSocket = new Socket(ipAdress, port);
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                OutputStream outToServer = clientSocket.getOutputStream()) {  
            outToServer.write(request.getBytes(Charset.forName("UTF-8")));
            clientSocket.shutdownOutput();          
            String clientResponse = inFromServer.readLine();
            return clientResponse;
        } catch (IOException ex) {
            Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
