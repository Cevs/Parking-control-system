/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.web.slusaci.SlusacAplikacije;

/**
 *
 * @author TOSHIBA
 */
public class PosluziteljSocketSlusac extends Thread {

    private ServerSocket serverSocket;
    private ExecutorService bazenKlijenata;
    private Socket klijentSocket;
    private static int port;
    private static Konfiguracija konfig;
    private boolean radi;

    public static boolean pauza = false;

    @Override
    public synchronized void start() {
        ServletContext servletContext = SlusacAplikacije.getServletContext();
        konfig = (Konfiguracija) servletContext.getAttribute("konfiguracija_aplikacije");
        int brojKlijenata = Integer.parseInt(konfig.dajPostavku("maks.broj.zahtjeva.cekanje"));
        bazenKlijenata = Executors.newFixedThreadPool(brojKlijenata);
        port = Integer.parseInt(konfig.dajPostavku("port"));
        radi = true;
        nasilnoGasenjeServera();
        super.start();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (!bazenKlijenata.isShutdown() && radi) {
                klijentSocket = serverSocket.accept();
                bazenKlijenata.submit(new KlijentSocket(klijentSocket));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void interrupt() {
        radi = false;
        super.interrupt();
    }

    static boolean isPauza() {
        return pauza;
    }

    public static void setPauza(boolean pauza) {
        PosluziteljSocketSlusac.pauza = pauza;
    }

    private void nasilnoGasenjeServera() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                close();
            }
        });
    }
    
    public void close(){
        if(bazenKlijenata != null){
            bazenKlijenata.shutdown();
        }
        
        if(serverSocket != null){
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(PosluziteljSocketSlusac.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
