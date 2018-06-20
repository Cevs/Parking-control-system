/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.ws.serveri;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.rest.klijenti.Lokacija;
import org.foi.nwtis.alemartin.rest.klijenti.MeteoPodaci;
import org.foi.nwtis.alemartin.rest.klijenti.OWMKlijent;
import org.foi.nwtis.alemartin.utils.BazaPodataka;
import org.foi.nwtis.alemartin.web.slusaci.SlusacAplikacije;

/**
 *
 * @author alemartin
 */
@WebService(serviceName = "GeoMeteoWS")
public class GeoMeteoWS {

    @Resource
    private WebServiceContext wsContext;

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajZadnjePreuzeteMeteoPodatke")
    public MeteoPodaci dajZadnjePreuzeteMeteoPodatke(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "parkiralisteId") int idParkiraliste) {

        if (BazaPodataka.autentificirajKorisnika(korisnickoIme, lozinka) ==-1) {
            return null;
        }

        String sql = "SELECT *FROM meteo WHERE id = ? ORDER BY preuzeto DESC LIMIT 1";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idParkiraliste);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                MeteoPodaci mp
                        = new MeteoPodaci(new Date(), new Date(), rs.getFloat("temp"), rs.getFloat("tempMin"),
                                rs.getFloat("tempMax"), "celsiuses", rs.getFloat("vlaga"), "%", rs.getFloat("tlak"), "hPa",
                                rs.getFloat("vjetar"), "ms", rs.getFloat("vjetarSmjer"), "", "", -1, "", "", 0.0f,
                                "", "", -1, rs.getString("vrijeme"), "", rs.getTimestamp("preuzeto"));
                return mp;
            }
        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajMeteoPodatke")
    public List<MeteoPodaci> dajMeteoPodatke(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "parkiralisteId") int idParkiraliste,
            @WebParam(name = "brojZapisa") int brojZapisa) {

        if (BazaPodataka.autentificirajKorisnika(korisnickoIme, lozinka) == -1) {
            return null;
        }
        List<MeteoPodaci> mpList = new ArrayList<>();
        String sql = "SELECT  *FROM meteo WHERE id = ? ORDER BY preuzeto DESC LIMIT ?";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idParkiraliste);
            ps.setInt(2, brojZapisa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MeteoPodaci mp
                        = new MeteoPodaci(new Date(), new Date(), rs.getFloat("temp"), rs.getFloat("tempMin"),
                                rs.getFloat("tempMax"), "celsiuses", rs.getFloat("vlaga"), "%", rs.getFloat("tlak"), "hPa",
                                rs.getFloat("vjetar"), "ms", rs.getFloat("vjetarSmjer"), "", "", -1, "", "", 0.0f,
                                "", "", -1, "", "", rs.getTimestamp("preuzeto"));
                mpList.add(mp);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mpList;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajMeteoPodatkeInterval")
    public List<MeteoPodaci> dajMeteoPodatkeInterval(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "idParkiraliste") int idParkiraliste,
            @WebParam(name = "datumOd") long datumOd, @WebParam(name = "datumDo") long datumDo) {

        if (BazaPodataka.autentificirajKorisnika(korisnickoIme, lozinka) == -1) {
            return null;
        }

        List<MeteoPodaci> mpList = new ArrayList<>();
        Timestamp tpoc = new Timestamp(datumOd);
        Timestamp tkraj = new Timestamp(datumDo);

        String sql = "SELECT * FROM meteo WHERE id = ? AND preuzeto BETWEEN ? AND ?";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idParkiraliste);
            ps.setTimestamp(2, tpoc);
            ps.setTimestamp(3, tkraj);
            ResultSet rs = ps.executeQuery();
            System.out.println("ResukltSET: " + rs);
            while (rs.next()) {
                System.out.println("Podatak");
                MeteoPodaci mp
                        = new MeteoPodaci(new Date(), new Date(), rs.getFloat("temp"), rs.getFloat("tempMin"),
                                rs.getFloat("tempMax"), "celsiuses", rs.getFloat("vlaga"), "%", rs.getFloat("tlak"), "hPa",
                                rs.getFloat("vjetar"), "ms", rs.getFloat("vjetarSmjer"), "", "", -1, "", "", 0.0f,
                                "", "", -1, "", "", rs.getTimestamp("preuzeto"));
                mpList.add(mp);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mpList;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajVazeceMeteoPodatke")
    public MeteoPodaci dajVazeceMeteoPodatke(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "idParkiraliste") int idParkiraliste) {

        if (BazaPodataka.autentificirajKorisnika(korisnickoIme, lozinka) == -1) {
            return null;
        }

        List<MeteoPodaci> mpList = new ArrayList<>();
        Lokacija geoLokacija = null;
        String sql = "SELECT *FROM parkiralista WHERE id = ?";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idParkiraliste);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                geoLokacija = new Lokacija(rs.getString("latitude"), rs.getString("longitude"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(GeoMeteoWS.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (geoLokacija != null) {
            ServletContext sc = SlusacAplikacije.getServletContext();
            Konfiguracija konfig = (Konfiguracija) sc.getAttribute("konfiguracija_aplikacije");
            OWMKlijent owmk = new OWMKlijent(konfig.dajPostavku("apikey"));
            MeteoPodaci mp = owmk.getRealTimeWeather(geoLokacija.getLatitude(), geoLokacija.getLongitude());

            return mp;

        }
        return null;
    }

}
