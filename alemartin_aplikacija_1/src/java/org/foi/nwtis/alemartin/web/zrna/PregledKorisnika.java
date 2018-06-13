/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.zrna;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.servlet.ServletContext;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.utils.BazaPodataka;
import org.foi.nwtis.alemartin.web.podaci.Korisnik;
import org.foi.nwtis.alemartin.web.slusaci.SlusacAplikacije;

/**
 *
 * @author alemartin
 */
@ManagedBean
@SessionScoped
public class PregledKorisnika {

    private List<Korisnik> listaKorisnika;
    private boolean prikaziGumbSljedeci;
    private boolean prikaziGumbPrethodni;
    private static int brojTrenutneStranice;
    private int zapisiPoStranici;
    private int brojStranica;

    /**
     * Creates a new instance of PregledKorisnika
     */
    public PregledKorisnika() {
        ServletContext servletContext = SlusacAplikacije.getServletContext();
        Konfiguracija konfig = (Konfiguracija) servletContext.getAttribute("konfiguracija_aplikacije");
        zapisiPoStranici = Integer.parseInt(konfig.dajPostavku("stranicenje.brojZapisa"));
        brojTrenutneStranice = 0;
        dohvatiPodatke();
    }

    private void dohvatiPodatke() {
        int pomak = brojTrenutneStranice * zapisiPoStranici;
        String sql = "SELECT *FROM korisnici LIMIT ?,?";
        listaKorisnika = izvrsiUpit(sql, pomak);
        azurirajPogled();
    }

    private void azurirajPogled() {
        if (brojTrenutneStranice > 0 && brojTrenutneStranice < brojStranica - 1) {
            prikaziGumbPrethodni = true;
            prikaziGumbSljedeci = true;
        } else if (brojStranica > 0 && !(brojTrenutneStranice < brojStranica - 1)) {
            prikaziGumbPrethodni = true;
            prikaziGumbSljedeci = false;
        } else if (!(brojTrenutneStranice > 0) && brojTrenutneStranice < brojStranica - 1) {
            prikaziGumbPrethodni = false;
            prikaziGumbSljedeci = true;
        } else {
        }
    }

    private List<Korisnik> izvrsiUpit(String sql, int pomak) {
        List<Korisnik> korisnici = new ArrayList<>();
        String izvrseniSQL = "";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pomak);
            ps.setInt(2, zapisiPoStranici);
            ResultSet rs = ps.executeQuery();
            izvrseniSQL = rs.getStatement().toString();
            while (rs.next()) {
                korisnici.add(kreirajObjekt(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        izracunajBrojStranica(izvrseniSQL);
        return korisnici;
    }

    private Korisnik kreirajObjekt(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String korIme = rs.getString("korisnicko_ime");
        String lozinka = rs.getString("lozinka");
        String prezime = rs.getString("prezime");
        String ime = rs.getString("ime");
        Korisnik korisnik = new Korisnik(id, korIme, lozinka, prezime, ime);
        return korisnik;
    }

    private void izracunajBrojStranica(String sql) {
        int brojZapisa = 0;
        brojStranica = 0;
        String pocetniTekst = ":";
        String zavrsniTekst = "LIMIT";
        int pocetniIndex = sql.indexOf(pocetniTekst);
        int zavrsniIndex = sql.indexOf(zavrsniTekst);
        String subSql = sql.substring(pocetniIndex + 1, zavrsniIndex - 1).replace("*", "").trim();
        String completeSql = subSql.substring(0, 6) + " COUNT(*) AS 'broj_zapisa' " + subSql.substring(6, subSql.length());
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(completeSql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                brojZapisa = rs.getInt("broj_zapisa");
            }
        } catch (SQLException ex) {
            Logger.getLogger(pregledDnevnika.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (brojZapisa > 0) {
            brojStranica++;
            brojZapisa -= zapisiPoStranici;
        }
    }

    public void sljedecaStranica() {
        brojTrenutneStranice++;
        dohvatiPodatke();
    }

    public void prethodnaStranica() {
        brojTrenutneStranice--;
        dohvatiPodatke();
    }

    public List<Korisnik> getListaKorisnika() {
        return listaKorisnika;
    }

    public void setListaKorisnika(List<Korisnik> listaKorisnika) {
        this.listaKorisnika = listaKorisnika;
    }

    public boolean isPrikaziGumbSljedeci() {
        return prikaziGumbSljedeci;
    }

    public void setPrikaziGumbSljedeci(boolean prikaziGumbSljedeci) {
        this.prikaziGumbSljedeci = prikaziGumbSljedeci;
    }

    public boolean isPrikaziGumbPrethodni() {
        return prikaziGumbPrethodni;
    }

    public void setPrikaziGumbPrethodni(boolean prikaziGumbPrethodni) {
        this.prikaziGumbPrethodni = prikaziGumbPrethodni;
    }

    /* Navigacija */
    public String pregledDnevnika() {
        return "pregledDnevnika";
    }
}
