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
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletContext;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.web.baza.BazaPodataka;
import org.foi.nwtis.alemartin.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.alemartin.web.utils.KomandaUtils;

/**
 *
 * @author TOSHIBA
 */
public class Posluzitelj extends Thread {

    private ServerSocket serverSocket;
    private Socket socket;
    private static int port;
    private static Konfiguracija konfig;
    private boolean radi;

    @Override
    public synchronized void start() {
        ServletContext servletContext = SlusacAplikacije.getServletContext();
        konfig = (Konfiguracija) servletContext.getAttribute("konfiguracija_aplikacije");
        port = Integer.parseInt(konfig.dajPostavku("port"));
        radi = true;
        super.start();
    }

    @Override
    public void run() {
        try {
            //serverSocket = new ServerSocket(port);
            while (radi) {
                //socket = serverSocket.accept();
                //InputStream input = socket.getInputStream(); //Procitaj komandu sa socketa
                //OutputStream output = socket.getOutputStream(); //Vrati odgovor
                //String poruka = procitajSocket(input);
                String komanda = "KORISNIK alenmartincevic; LOZINKA martincevicalen; DODAJ 'martincevic' 'alen';";         
                if (autentificirajKorisnika(komanda)) {
                    String podKomanda  = KomandaUtils.INSTANCE.vratiPodkomandu(komanda, 4);
                    if (podKomanda.length() != 0) { //Ako novo dobiveno polje komandi ima jos elementa posaljji na obradu
                        ObradaKomande obradaKomande = new ObradaKomande(podKomanda, null); //umjesto null stavit socket 
                        Thread dretva = new Thread(obradaKomande);
                        dretva.start();
                    } else {
                        //Komanda ne sadrzi nista osim parametara za autentifikaciju
                        String odg = "OK 10;";
                        //output.write(odg.getBytes());
                        //output.flush();
                        //socket.shutdownOutput();
                    }

                } else {
                    String odg = "ERR 11; Neuspjela autentifikacija";
                    //output.write(odg.getBytes());
                    //output.flush();
                    //socket.shutdownOutput();
                    System.out.println(odg);
                }
                sleep(1000 * 20); //Spjavaj 20 sekundi
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private String procitajSocket(InputStream input) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        String readLine;
        String poruka = "";
        while ((readLine = in.readLine()) != null) {
            poruka += readLine;
        }
        return poruka;
    }

    private boolean autentificirajKorisnika(String komanda) {   
        if (KomandaUtils.INSTANCE.provjeriKomanduAutorizacije(komanda)) {
            String[] komandaArray = KomandaUtils.INSTANCE.rastaviKomandu(komanda);
            String korisnickoIme = komandaArray[1];
            String lozinka = komandaArray[3];
            String sql = "SELECT *FROM korisnici WHERE korisnicko_ime = '" + korisnickoIme + "' AND lozinka = '" + lozinka + "';";
            try (
                    Connection con = BazaPodataka.INSTANCE.getConnection();
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return true;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return false;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

}
