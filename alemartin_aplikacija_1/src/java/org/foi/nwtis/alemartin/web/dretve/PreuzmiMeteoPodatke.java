/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.dretve;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.web.baza.BazaPodataka;
import org.foi.nwtis.alemartin.web.slusaci.SlusacAplikacije;

/**
 *
 * @author TOSHIBA
 */
public class PreuzmiMeteoPodatke extends Thread {

    private static Konfiguracija konfig;
    private static int interval;
    private String openWeatherApiKey;
    private boolean radi;
    

    @Override
    public synchronized void start() {
        ServletContext servletContext = SlusacAplikacije.getServletContext();
        konfig = (Konfiguracija) servletContext.getAttribute("konfiguracija_aplikacije");
        interval = Integer.parseInt(konfig.dajPostavku("intervalDretveZaMeteoPodatke"));
        openWeatherApiKey = konfig.dajPostavku("apikey");
        radi = true;
        super.start();
    }

    @Override
    public void run() {
        try {
            
            while (radi) {
               sleep(1000*30);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } 

    }
    
    @Override
    public void interrupt() {
        super.interrupt();
    }

}
