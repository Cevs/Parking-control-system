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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import org.foi.nwtis.alemartin.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.alemartin.web.slusaci.SlusacAplikacije;

/**
 * Singleton Klasa za pojednostavljeni rad s bazom podataka
 *
 * @author alemartin
 */
public class BazaPodataka {

    @Resource
    private WebServiceContext context;

    private BP_Konfiguracija bpk;
    private String url;
    private String korisnik;
    private String lozinka;
    private String serverDatabase;

    public static final BazaPodataka INSTANCE = new BazaPodataka();

    /**
     * Privatni konstruktor. Dohvaća potrebnu konfiguraciju za bazu podataka i
     * učitava drajver za rad s bazom
     */
    private BazaPodataka() {
        ServletContext servletContext = SlusacAplikacije.getServletContext();
        bpk = (BP_Konfiguracija) servletContext.getAttribute("konfiguracija_baze");
        url = bpk.getServerDatabase() + bpk.getUserDatabase();
        serverDatabase = bpk.getServerDatabase();
        korisnik = bpk.getUserUsername();
        lozinka = bpk.getUserPassword();
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
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, korisnik, lozinka);
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
    public String pretvoriDatum(String timestamp) {
        if (serverDatabase.contains("mysql")) {
            try {
                String sDate = timestamp.toString();
                DateFormat originalFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = originalFormat.parse(sDate);
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
   
    public void UpisDnevnika(String korisnickoIme, String vrstaZahtjeva, String sadrzajZahtjeva) {    
        try (Connection conn = getConnection()) {
            String sql = "SELECT *FROM korisnici where korisnicko_ime = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, korisnickoIme);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int idKorisnik = rs.getInt("id");
            ps.close();
            String sqlInsert = "INSERT INTO dnevnik (korisnik, vrsta_zahtjeva, sadrzaj_zahtjeva) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sqlInsert);
            ps.setInt(1, idKorisnik);
            ps.setString(2, vrstaZahtjeva);
            ps.setString(3, sadrzajZahtjeva);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(BazaPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public String getLozinka() {
        return lozinka;
    }

}
