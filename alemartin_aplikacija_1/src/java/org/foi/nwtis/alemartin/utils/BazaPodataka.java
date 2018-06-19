/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.alemartin.web.podaci.Dnevnik;
import org.foi.nwtis.alemartin.web.podaci.Korisnik;
import org.foi.nwtis.alemartin.web.slusaci.SlusacAplikacije;

/**
 * Singleton Klasa za pojednostavljeni rad s bazom podataka
 *
 * @author alemartin
 */
public class BazaPodataka {

    @Resource
    private WebServiceContext context;

    private static BP_Konfiguracija bpk;
    private static Konfiguracija konfig;
    private static String url;
    private static String dbKorisnik;
    private static String dbLozinka;
    private static String serverDatabase;
    private static BazaPodataka instanca;

    /**
     * Privatni konstruktor. Dohvaća potrebnu konfiguraciju za bazu podataka i
     * učitava drajver za rad s bazom
     */
    private BazaPodataka() {
        ServletContext servletContext = SlusacAplikacije.getServletContext();
        bpk = (BP_Konfiguracija) servletContext.getAttribute("konfiguracija_baze");
        url = bpk.getServerDatabase() + bpk.getUserDatabase();
        serverDatabase = bpk.getServerDatabase();
        dbKorisnik = bpk.getUserUsername();
        dbLozinka = bpk.getUserPassword();
        try {
            Class.forName(bpk.getDriverDatabase()).newInstance();
        } catch (Exception ex) {
            Logger.getLogger(BazaPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Uspostavlja konekciju s bazom podataka i vraća ju kao objekt
     *
     * @return Connection
     */
    public static Connection getConnection() {
        try {
            if(instanca == null){
                instanca = new BazaPodataka();
            }
            return DriverManager.getConnection(url, dbKorisnik, dbLozinka);
        } catch (SQLException ex) {
            Logger.getLogger(BazaPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Proslijeđeni timestamp u obliku stringa pretvara u potrebni format kako
     * bi se mogao spremiti u određenu bazu podataka
     *
     * @param timestamp
     * @return
     */
    public static String pretvoriDatum(String timestamp) {
        if (serverDatabase.contains("mysql")) {
            try {
                DateFormat originalFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = originalFormat.parse(timestamp);
                String formatedDate = targetFormat.format(date);
                return formatedDate;
            } catch (ParseException ex) {
                Logger.getLogger(BazaPodataka.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            String sDate = timestamp.toString();
            return sDate;
        }
        return "";
    }

    public static void UpisDnevnika(String korisnickoIme, String url, String ip, String vrstaZahtjeva, String sadrzajZahtjeva, long trajanje, String status) {
        String sqlInsert = "INSERT INTO dnevnik (korisnik, url, ipadresa, vrsta_zahtjeva, sadrzaj_zahtjeva, vrijeme, trajanje, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";  
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);) {      
            ps.setString(1, korisnickoIme);
            ps.setString(2, url);
            ps.setString(3, ip);
            ps.setString(4, vrstaZahtjeva);
            ps.setString(5, sadrzajZahtjeva);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(new Date());
            ps.setString(6, date);
            ps.setLong(7, trajanje);
            ps.setString(8, status);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(BazaPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static int autentificirajKorisnika(String korisnickoIme, String lozinka) {
        String sql = "SELECT id FROM korisnici WHERE korisnicko_ime = ? AND lozinka = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, korisnickoIme);
            ps.setString(2, lozinka);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("id");
        } catch (SQLException ex) {
            Logger.getLogger(BazaPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public String getUrl() {
        return url;
    }
   
}
