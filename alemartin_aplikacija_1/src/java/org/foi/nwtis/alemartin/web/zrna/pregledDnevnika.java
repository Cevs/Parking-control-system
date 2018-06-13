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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.servlet.ServletContext;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.utils.BazaPodataka;
import org.foi.nwtis.alemartin.web.podaci.Dnevnik;
import org.foi.nwtis.alemartin.web.slusaci.SlusacAplikacije;

/**
 *
 * @author alemartin
 */
@ManagedBean
@SessionScoped
public class pregledDnevnika {

    private List<Dnevnik> listaDnevnika;
    private boolean prikaziGumbSljedeci;
    private boolean prikaziGumbPrethodni;
    public int brojTrenutneStranice;
    private String vrstaZapisa = "";
    private String vrijemeOd = "";
    private String vrijemeDo = "";
    private int zapisiPoStranici;
    private int brojStranica;

    //Filteri
    private Date vrijemeOdDate = null;
    private Date vrijemeDoDate = null;

    /**
     * Creates a new instance of pregledDnevnika
     */
    public pregledDnevnika() {
        ServletContext servletContext = SlusacAplikacije.getServletContext();
        Konfiguracija konfig = (Konfiguracija) servletContext.getAttribute("konfiguracija_aplikacije");
        zapisiPoStranici = Integer.parseInt(konfig.dajPostavku("stranicenje.brojZapisa"));
        brojTrenutneStranice = 0;
        dohvatiPodatke();
    }

    public void postaviFiltere() throws ParseException {
        brojTrenutneStranice = 0;
        if (!vrijemeOd.isEmpty()) {
            vrijemeOd = BazaPodataka.pretvoriDatum(vrijemeOd);
        } else {
            vrijemeOd = "";
        }
        if (!vrijemeDo.isEmpty()) {
            vrijemeDo = BazaPodataka.pretvoriDatum(vrijemeDo);
        } else {
            vrijemeDo = "";
        }
        dohvatiPodatke();
    }

    private void dohvatiPodatke() {
        //listaDnevnika = new ArrayList<>();
        int pomak = brojTrenutneStranice * zapisiPoStranici;
        String sql = "";
        if (!vrijemeOd.isEmpty() && !vrijemeDo.isEmpty() && !vrstaZapisa.isEmpty()) {
            sql = "SELECT *FROM dnevnik WHERE vrsta_zapisa = ? AND vrijeme BETWEEN ? AND ? LIMIT ?,?";
            listaDnevnika = izvrsiUpitSve(sql, vrijemeOd, vrijemeDo, vrstaZapisa, pomak);
        } else if (!vrijemeOd.isEmpty() && !vrijemeDo.isEmpty() && vrstaZapisa.isEmpty()) {
            sql = "SELECT *FROM dnevnik WHERE vrijeme BETWEEN ? AND ? LIMIT ?,?";
            listaDnevnika = izvrsiUpitInterval(sql, vrijemeOd, vrijemeDo, pomak);
        } else if (vrijemeOd.isEmpty() && vrijemeDo.isEmpty() && !vrstaZapisa.isEmpty()) {
            sql = "SELECT *FROM dnevnik WHERE vrsta_zahtjeva  = ? LIMIT ?,?";
            listaDnevnika = izvrsiUpitVrsta(sql, vrstaZapisa, pomak);
        } else if (!vrijemeOd.isEmpty() && vrijemeDo.isEmpty() && !vrstaZapisa.isEmpty()) {
            sql = "SELECT *FROM dnevnik WHERE vrsta_zahtjeva = ? AND vrijeme >= ? LIMIT ?,?";
            listaDnevnika = izvrsiUpitVrijemeZapis(sql, vrstaZapisa, vrijemeOd, pomak);
        } else if (!vrijemeOd.isEmpty() && vrijemeDo.isEmpty() && vrstaZapisa.isEmpty()) {
            sql = "SELECT *FROM dnevnik WHERE vrijeme >= ? LIMIT ?,?";
            listaDnevnika = izvrsiUpitVrijeme(sql, vrijemeOd, pomak);
        } else if (vrijemeOd.isEmpty() && !vrijemeDo.isEmpty() && !vrstaZapisa.isEmpty()) {
            sql = "SELECT *FROM dnevnik WHERE vrsta_zahtjeva = ? AND vrijeme <= ? LIMIT ?,?";
            listaDnevnika = izvrsiUpitVrijemeZapis(sql, vrstaZapisa, vrijemeDo, pomak);
        } else if (vrijemeOd.isEmpty() && !vrijemeDo.isEmpty() && vrstaZapisa.isEmpty()) {
            sql = "SELECT  *FROM dnevnik WHERE vrijeme <= ? LIMIT ?,?";
            listaDnevnika = izvrsiUpitVrijeme(sql, vrijemeDo, pomak);
        } else {
            sql = "SELECT *FROM dnevnik LIMIT ?,?;";
            listaDnevnika = izvrsiUpit(sql, pomak);
        }
        azurirajPogled();
    }

    private void azurirajPogled() {
        if (brojTrenutneStranice > 0 && brojTrenutneStranice < brojStranica - 1) {
            prikaziGumbPrethodni = true;
            prikaziGumbSljedeci = true;
        } else if (brojStranica - 1 > 0 && !(brojTrenutneStranice < brojStranica - 1)) {
            prikaziGumbPrethodni = true;
            prikaziGumbSljedeci = false;
        } else if (!(brojTrenutneStranice > 0) && brojTrenutneStranice < brojStranica - 1) {
            prikaziGumbPrethodni = false;
            prikaziGumbSljedeci = true;
        } else {
            prikaziGumbPrethodni = false;
            prikaziGumbSljedeci = false;
        }
    }

    private Dnevnik kreirajObjekt(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int idKorisnik = rs.getInt("korisnik");
        String url = rs.getString("url");
        String ip = rs.getString("ipadresa");
        String vrstaZahtjeva = rs.getString("vrsta_zahtjeva");
        String sadrzajZahtjeva = rs.getString("sadrzaj_zahtjeva");
        Date vrijeme = rs.getTimestamp("vrijeme");
        long trajanje = rs.getLong("trajanje");
        int status = rs.getInt("status");
        Dnevnik dnevnik = new Dnevnik(id, idKorisnik, url, ip, vrstaZahtjeva, sadrzajZahtjeva, vrijeme, trajanje, status);
        return dnevnik;
    }

    private List<Dnevnik> izvrsiUpit(String sql, int pomak) {
        List<Dnevnik> lista = new ArrayList<>();
        String izvrseniSQL = "";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pomak);
            ps.setInt(2, zapisiPoStranici);
            ResultSet rs = ps.executeQuery();
            izvrseniSQL = rs.getStatement().toString();
            while (rs.next()) {
                lista.add(kreirajObjekt(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pregledDnevnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        izracunajBrojStranica(izvrseniSQL);
        return lista;
    }

    private List<Dnevnik> izvrsiUpitVrsta(String sql, String vrstaZahtjeva, int pomak) {
        List<Dnevnik> lista = new ArrayList<>();
        String izvrseniSQL = "";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vrstaZahtjeva);
            ps.setInt(2, pomak);
            ps.setInt(3, zapisiPoStranici);
            ResultSet rs = ps.executeQuery();
            izvrseniSQL = rs.getStatement().toString();
            while (rs.next()) {
                lista.add(kreirajObjekt(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pregledDnevnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        izracunajBrojStranica(izvrseniSQL);
        return lista;
    }

    private List<Dnevnik> izvrsiUpitInterval(String sql, String vrijemeOd, String vrijemeDo, int pomak) {
        List<Dnevnik> lista = new ArrayList<>();
        String izvrseniSQL = "";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vrijemeOd);
            ps.setString(2, vrijemeDo);
            ps.setInt(3, pomak);
            ps.setInt(4, zapisiPoStranici);
            ResultSet rs = ps.executeQuery();
            izvrseniSQL = rs.getStatement().toString();
            while (rs.next()) {
                lista.add(kreirajObjekt(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pregledDnevnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        izracunajBrojStranica(izvrseniSQL);
        return lista;
    }

    private List<Dnevnik> izvrsiUpitSve(String sql, String vrstaZahtjeva, String vrijemeOd, String vrijemeDo, int pomak) {
        List<Dnevnik> lista = new ArrayList<>();
        String izvrseniSQL = "";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vrstaZahtjeva);
            ps.setString(2, vrijemeOd);
            ps.setString(3, vrijemeDo);
            ps.setInt(4, pomak);
            ps.setInt(5, zapisiPoStranici);
            ResultSet rs = ps.executeQuery();
            izvrseniSQL = rs.getStatement().toString();
            while (rs.next()) {
                lista.add(kreirajObjekt(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pregledDnevnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        izracunajBrojStranica(izvrseniSQL);
        return lista;
    }

    private List<Dnevnik> izvrsiUpitVrijeme(String sql, String vrijeme, int pomak) {
        List<Dnevnik> lista = new ArrayList<>();
        String izvrseniSQL = "";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vrijeme);
            ps.setInt(2, pomak);
            ps.setInt(3, zapisiPoStranici);
            ResultSet rs = ps.executeQuery();
            izvrseniSQL = rs.getStatement().toString();
            while (rs.next()) {
                lista.add(kreirajObjekt(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pregledDnevnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        izracunajBrojStranica(izvrseniSQL);
        return lista;
    }

    private List<Dnevnik> izvrsiUpitVrijemeZapis(String sql, String vrstaZahtjeva, String vrijeme, int pomak) {
        List<Dnevnik> lista = new ArrayList<>();
        String izvrseniSQL = "";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vrstaZahtjeva);
            ps.setString(2, vrijeme);
            ps.setInt(3, pomak);
            ps.setInt(4, zapisiPoStranici);
            ResultSet rs = ps.executeQuery();
            izvrseniSQL = rs.getStatement().toString();
            while (rs.next()) {
                lista.add(kreirajObjekt(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pregledDnevnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        izracunajBrojStranica(izvrseniSQL);
        return lista;
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
        azurirajPogled();
    }

    public void prethodnaStranica() {
        brojTrenutneStranice--;
        dohvatiPodatke();
        azurirajPogled();
    }

    public List<Dnevnik> getListaDnevnika() {
        return listaDnevnika;
    }

    public void setListaDnevnika(List<Dnevnik> listaDnevnika) {
        this.listaDnevnika = listaDnevnika;
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

    public String getVrstaZapisa() {
        return vrstaZapisa;
    }

    public void setVrstaZapisa(String vrstaZapisa) {
        this.vrstaZapisa = vrstaZapisa;
    }

    public String getVrijemeOd() {
        return vrijemeOd;
    }

    public void setVrijemeOd(String vrijemeOd) {
        this.vrijemeOd = vrijemeOd;
    }

    public String getVrijemeDo() {
        return vrijemeDo;
    }

    public void setVrijemeDo(String vrijemeDo) {
        this.vrijemeDo = vrijemeDo;
    }

    /* Navigacija */
    public String pregledKorisnika() {
        return "pregledKorisnika";
    }
}
