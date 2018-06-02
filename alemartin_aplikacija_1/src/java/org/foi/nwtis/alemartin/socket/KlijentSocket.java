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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import org.foi.nwtis.alemartin.web.baza.BazaPodataka;
import org.foi.nwtis.alemartin.web.dretve.PreuzmiMeteoPodatke;
import org.foi.nwtis.alemartin.web.utils.KomandaUtils;

/**
 *
 * @author TOSHIBA
 */
public class KlijentSocket implements Runnable {

    private final Socket klijetSocket;
    private String korisnickoIme;
    private String tipKomande;
    private String lozinka;
    private String akcija;
    private static final List<String> KORISNICKI_ZAHTJEVI = Arrays.asList("dodaj", "pasivno", "aktivno", "listaj");
    private static Method metoda;
    private static AtomicBoolean radi = new AtomicBoolean(true);

    public KlijentSocket(Socket klijetSocket) {
        this.klijetSocket = klijetSocket;
    }

    @Override
    public void run() {
        if (radi.get()) {
            try (
                    BufferedReader odKlijenta = new BufferedReader(new InputStreamReader(klijetSocket.getInputStream(), "UTF-8"));
                    OutputStream doKlijenta = klijetSocket.getOutputStream()) {
                String komanda = odKlijenta.readLine();
                String odgovor = obradiKomandu(komanda);
                doKlijenta.write(odgovor.getBytes(Charset.forName("UTF-8")));
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
    }

    private void dohvatiParametreKomande(String komanda) {
        korisnickoIme = korisnickoIme = KomandaUtils.INSTANCE.dohvatiKorisnickoIme(komanda);
        lozinka = KomandaUtils.INSTANCE.dohvatiLozinku(komanda);
        tipKomande = KomandaUtils.INSTANCE.odrediVrstuKomande(komanda);
        if (tipKomande.equals("posluzitelj")) {
            akcija = KomandaUtils.INSTANCE.dohvatiAkcijuPosluzitelja(komanda);
        } else {
            akcija = KomandaUtils.INSTANCE.dohvatiAkcijuGrupe(komanda);
        }
    }

    private String obradiKomandu(String komanda) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (!KomandaUtils.INSTANCE.provjeriKomandu(komanda)) {
            return "Neispravna komanda";
        }
        dohvatiParametreKomande(komanda);

        //Server je u pauzi pa ne prima korisnicke zahtjeve
        if (PosluziteljSocketSlusac.isPauza() && (KORISNICKI_ZAHTJEVI.contains(akcija) || tipKomande.equals("grupa"))) {
            return "";
        }

        String podKomanda = KomandaUtils.INSTANCE.vratiPodkomandu(komanda, 4);
        if (akcija.equals("dodaj")) {
            metoda = this.getClass().getDeclaredMethod(akcija, String.class);
            metoda.setAccessible(true);
            return (String) metoda.invoke(this, (String) podKomanda);
        }

        if (!autentificirajKorisnika(komanda)) {
            return "ERR 11";    //Neuspjela autentifikacija
        }

        if (podKomanda.isEmpty()) {
            return "OK 10";     //Komanda je sadržavala samo autorizacijske podatke
        }

        BazaPodataka.INSTANCE.UpisDnevnika(KomandaUtils.INSTANCE.dohvatiKorisnickoIme(komanda), "socket", komanda);
        //TODO poslati email

        metoda = this.getClass().getDeclaredMethod(akcija);
        metoda.setAccessible(true);
        return (String) metoda.invoke(this);

    }

    private boolean autentificirajKorisnika(String komanda) {
        String sql = "SELECT *FROM korisnici WHERE korisnicko_ime = ? AND lozinka = ?;";
        try (
                Connection conn = BazaPodataka.INSTANCE.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, korisnickoIme);
            ps.setString(2, lozinka);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public String dodaj(String podKomanda) {
        String[] podKomandaArray = KomandaUtils.INSTANCE.rastaviKomandu(podKomanda);
        String prezime = podKomandaArray[1];
        String ime = podKomandaArray[2];
        if (!korisnikPostoji(korisnickoIme)) {
            String sql = "INSERT INTO korisnici (korisnicko_ime, lozinka, prezime, ime) VALUES (?,?,?,?)";
            try (
                    Connection conn = BazaPodataka.INSTANCE.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, korisnickoIme);
                ps.setString(2, lozinka);
                ps.setString(3, prezime);
                ps.setString(4, ime);
                ps.executeUpdate();
                return "OK 10;";
            } catch (SQLException ex) {
                Logger.getLogger(KlijentSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "ERR 10;";
    }

    public boolean korisnikPostoji(String korisnickoIme) {
        String sql = "SELECT *FROM korisnici WHERE korisnicko_ime = ?";
        try (Connection conn = BazaPodataka.INSTANCE.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, korisnickoIme);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(KlijentSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
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
        if (PreuzmiMeteoPodatke.isRadi()) {
            PreuzmiMeteoPodatke.setRadi(false);
            return "OK 10;";
        } else {
            return "ERR 14";
        }
    }

    private String aktivno() {
        if (!PreuzmiMeteoPodatke.isRadi()) {
            PreuzmiMeteoPodatke.setRadi(true);
            return "OK 10;";
        } else {
            return "ERR 15;";
        }
    }

    private String stani() {
        if (!radi.get()) {
            return "ERR 16";
        }
        radi.set(false);
        PreuzmiMeteoPodatke.setRadi(false);
        PosluziteljSocketSlusac.ugasiAplikaciju();
        return "OK 10;";
    }

    private String stanje() {
        if(PosluziteljSocketSlusac.isPauza() && !PreuzmiMeteoPodatke.isRadi()){
            return "OK 14;";
        }
        if(PosluziteljSocketSlusac.isPauza() && PreuzmiMeteoPodatke.isRadi()){
            return "OK 13;";
        }
        if(!PosluziteljSocketSlusac.isPauza() && !PreuzmiMeteoPodatke.isRadi()){
            return "OK 12;";
        }
        return "OK 11;";
    }

    private String listaj() {
        String sql = "SELECT *FROM korisnici";
        JsonArrayBuilder jsonArrayKorisnici = Json.createArrayBuilder();
        try(Connection conn = BazaPodataka.INSTANCE.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql))
        {
            if(!rs.next()){
                return " ERR 17;";
            }
            do{
                JsonObjectBuilder jsonKorisnik = Json.createObjectBuilder();
                jsonKorisnik.add("id", rs.getInt("id"));
                jsonKorisnik.add("ki", rs.getString("korisnicko_ime"));
                jsonKorisnik.add("prezime", rs.getString("prezime"));
                jsonKorisnik.add("ime", rs.getString("ime"));              
                //Treba li lozinka ? 
                jsonArrayKorisnici.add(jsonKorisnik);
            }while(rs.next());
        } catch (SQLException ex) {
            Logger.getLogger(KlijentSocket.class.getName()).log(Level.SEVERE, null, ex);
            return " ERR 17;";
        }
        return "OK 10; "+jsonArrayKorisnici.build().toString();
    }
}