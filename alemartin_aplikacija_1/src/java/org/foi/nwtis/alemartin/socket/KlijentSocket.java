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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.alemartin.web.baza.BazaPodataka;
import org.foi.nwtis.alemartin.web.utils.KomandaUtils;

/**
 *
 * @author TOSHIBA
 */
public class KlijentSocket implements Runnable {

    private final Socket klijetSocket;

    public KlijentSocket(Socket klijetSocket) {
        this.klijetSocket = klijetSocket;
    }

    @Override
    public void run() {
        try (
                BufferedReader odKlijenta = new BufferedReader(new InputStreamReader(klijetSocket.getInputStream(), "UTF-8"));
                DataOutputStream doKlijenta = new DataOutputStream(klijetSocket.getOutputStream());) {
            String komanda = odKlijenta.readLine();
            String odgovor = obradiKomandu(komanda);
            doKlijenta.write(odgovor.getBytes());
        } catch (Exception ex) {
            Logger.getLogger(KlijentSocket.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                klijetSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(KlijentSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String obradiKomandu(String komanda) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (!autentificirajKorisnika(komanda)) {
            return "ERR 11";    //Neuspjela autentifikacija
        }

        String podKomanda = KomandaUtils.INSTANCE.vratiPodkomandu(komanda, 4);
        if (podKomanda.isEmpty()) {
            return "OK 10";     //Komanda je sadržavala samo autorizacijske podatke
        }

        //TODO poslati email
        //TODO upisati u dnevnik komandu i tip zahtjeva
        String tipKomande = KomandaUtils.INSTANCE.odrediVrstuKomande(komanda);
        String[] komandaArray = KomandaUtils.INSTANCE.rastaviKomandu(podKomanda);
        String akcija = "";
        if (tipKomande.equals("posluzitelj")) {
            akcija = komandaArray[0].toLowerCase();
        } else if (tipKomande.equals("grupa")) {
            //TODO još provjerit da li se dobro spaja
            String tipAkcije = komandaArray[0].toLowerCase();
            akcija = komandaArray[1].toLowerCase();
            akcija = tipAkcije + akcija.substring(0, 1).toUpperCase() + akcija.substring(1);
        }
        Method metoda = this.getClass().getDeclaredMethod(akcija);
        metoda.setAccessible(true);
        String odgovor = (String) metoda.invoke(this);
        return odgovor;
    }

    private boolean autentificirajKorisnika(String komanda) {
        if (KomandaUtils.INSTANCE.provjeriKomanduAutorizacije(komanda)) {
            String[] komandaArray = KomandaUtils.INSTANCE.rastaviKomandu(komanda);
            String korisnickoIme = komandaArray[1];
            String lozinka = komandaArray[3];
            String sql = "SELECT *FROM korisnici WHERE korisnicko_ime = '" + korisnickoIme + "' AND lozinka = '" + lozinka + "';";
            try (
                    Connection conn = BazaPodataka.INSTANCE.getConnection();
                    Statement stmt = conn.createStatement();
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

    public String dodaj() {
       return "dodaj";
    }

    public String pauza() {
        if (PosluziteljSocketSlusac.isPauza()) {
            return "ERR 12;";
        } else {
            PosluziteljSocketSlusac.setPauza(true);
            return "OK 10;";
        }
    }

    private String kreni() {
        if (PosluziteljSocketSlusac.isPauza()) {
            PosluziteljSocketSlusac.setPauza(false);
            return "OK 10;";
        } else {
            return "ERR 13;";
        }
    }

    private String pasivno() {
        return "pasivno";
    }

    private String aktivno() {
        return "aktivno";
    }

    private String stani() {
        return "stani";
    }

    private String stanje() {
        return "stanje";
    }

    private String listaj() {
        return "listaj";
    }
}
