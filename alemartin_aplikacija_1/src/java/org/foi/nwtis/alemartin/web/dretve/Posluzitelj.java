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
import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.web.baza.BazaPodataka;
import org.foi.nwtis.alemartin.web.slusaci.SlusacAplikacije;

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
                    ObradaKomande obradaKomande = new ObradaKomande(komanda, null); //umjesto null stavit socket 
                    Thread dretva = new Thread(obradaKomande);
                    dretva.start();
                } else {
                    //output.write(odg.getBytes());
                    //output.flush();
                    //socket.shutdownOutput();
                    System.out.println("Neuspjela autentifikacija");
                }
                sleep(1000 * 20); //Spjavaj 20 sekundi
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private String izvrsiKomandu(String komanda) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String kosturKomande = "KORISNIK [aA-zZ0-9_\\\\-]*; LOZINKA [aA-zZ0-9_\\\\-]*; "
                + "(DODAJ (\"|')[aA-zZ]*(\"|') (\"|')[aA-zZ]*(\"|')|PAUZA|KRENI|PASIVNO|AKTIVNO|STANI|STANJE|LISTAJ);";
        Pattern uzorak = Pattern.compile(kosturKomande);
        Matcher matcher = uzorak.matcher(komanda);
        if (!matcher.matches()) {
            return "Komanda nije ispravna";
        }
        String akcija = komanda.split(" ")[4].trim().toLowerCase();
        Method metoda = this.getClass().getDeclaredMethod(akcija);
        metoda.setAccessible(true);
        return ((String)metoda.invoke(this));
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

    private boolean autentificirajKorisnika(String poruka) {
        String[] komandaArray = poruka.split(" "); //Splitaj po razmaku
        String korisnickoIme = komandaArray[1].trim().replace(";", "");
        String lozinka = komandaArray[3].trim().replace(";", "");
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
        return false;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

}
