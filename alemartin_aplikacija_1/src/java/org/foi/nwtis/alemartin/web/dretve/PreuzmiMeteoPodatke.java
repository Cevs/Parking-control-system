/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.dretve;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.rest.klijenti.Lokacija;
import org.foi.nwtis.alemartin.rest.klijenti.MeteoPodaci;
import org.foi.nwtis.alemartin.rest.klijenti.OWMKlijent;
import org.foi.nwtis.alemartin.utils.BazaPodataka;
import org.foi.nwtis.alemartin.web.podaci.Parkiraliste;
import org.foi.nwtis.alemartin.web.slusaci.SlusacAplikacije;

/**
 *
 * @author TOSHIBA
 */
public class PreuzmiMeteoPodatke extends Thread {

    private static Konfiguracija konfig;
    private static int interval;
    private String openWeatherApiKey;
    private static boolean radi;

    @Override
    public synchronized void start() {
        ServletContext servletContext = SlusacAplikacije.getServletContext();
        konfig = (Konfiguracija) servletContext.getAttribute("konfiguracija_aplikacije");
        interval = Integer.parseInt(konfig.dajPostavku("intervalDretveZaMeteoPodatke"));
        openWeatherApiKey = konfig.dajPostavku("apikey");
        radi = true;
        super.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                //Preuzimaj meteopodatke dok je zastavica podignuta
                if (radi) {
                    List<Parkiraliste> parkiralista = dohvatiParkiralista();
                    dohvatiSpremiMeteoPodatke(parkiralista);
                }
                sleep(interval * 1000);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    /**
     * Dohvaća sva parkirališta iz baze podataka. Vraća listu objekata
     * parkirališta.
     *
     * @return List<Parkiraliste>
     */
    private List<Parkiraliste> dohvatiParkiralista() {
        List<Parkiraliste> parkiralista = new ArrayList<>();
        String sql = "SELECT *FROM parkiralista";
        try (Connection con = BazaPodataka.getConnection();
                Statement stat = con.createStatement();
                ResultSet rs = stat.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String naziv = rs.getString("naziv");
                String adresa = rs.getString("adresa");
                String latitude = rs.getString("latitude");
                String longitude = rs.getString("longitude");
                Parkiraliste parkiraliste = new Parkiraliste(id, naziv, adresa, new Lokacija(latitude, longitude));
                parkiralista.add(parkiraliste);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreuzmiMeteoPodatke.class.getName()).log(Level.SEVERE, null, ex);
        }
        return parkiralista;
    }

    /**
     * Za argument prima listu objekata parkirališta. Vrši se iteracija nad
     * listom i za svako parkiralište dohvaćaju se meteorološki podaci sa
     * servisa https://home.openweathermap.org/api. Dohvaćene meteorološke
     * podatke sprema u bazu podataka.
     *
     * @param List<Parkiraliste> spremljenaParkiralista
     */
    private void dohvatiSpremiMeteoPodatke(List<Parkiraliste> spremljenaParkiralista) {     
        try(Connection conn = BazaPodataka.getConnection();
                Statement stat = conn.createStatement()){
            for(Parkiraliste p: spremljenaParkiralista){
                OWMKlijent owmk = new OWMKlijent(openWeatherApiKey);
                MeteoPodaci mp = owmk.getRealTimeWeather(p.getGeoloc().getLatitude(), p.getGeoloc().getLongitude());
                
                if(mp!=null){
                    String sql = kreirajSQLInsert(mp, p);
                    stat.execute(sql);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreuzmiMeteoPodatke.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    /**
     * Za argumente prima objekte MeteoPodaci i Parkiralište. Na temelju
     * atributa prosljeđenih objekata kreira potrebnu SQL instrukciju za upis u
     * bazu podataka u tablicu meteo.
     *
     * @param MeteoPodaci mp
     * @param Parkiralište p
     * @return
     */
    private String kreirajSQLInsert(MeteoPodaci mp, Parkiraliste p) {

        String sql = "INSERT INTO meteo (id, adresastanice, latitude, longitude, vrijeme, "
                + "vrijemeopis, temp, tempmin, tempmax, vlaga, tlak, vjetar, vjetarsmjer) "
                + "VALUES (" + p.getId()+", ";
        sql += (p.getAdresa() != null) ? "'"+p.getAdresa() + "', " : "DEFAULT, ";
        sql += (p.getGeoloc().getLatitude() != null) ? "'"+p.getGeoloc().getLatitude() + "', " : "DEFAULT, ";
        sql += (p.getGeoloc().getLatitude() != null) ? "'"+p.getGeoloc().getLongitude()+ "', " : "DEFAULT, ";
        sql += (mp.getWeatherValue() != null) ? "'"+mp.getWeatherValue() + "', " : "DEFAULT, ";
        sql += (mp.getWeatherIcon() != null) ? "'"+mp.getWeatherIcon() + "', " : "DEFAULT, ";
        sql += (mp.getTemperatureValue() != null) ? "'"+mp.getTemperatureValue() + "', " : "DEFAULT, ";
        sql += (mp.getTemperatureMin() != null) ? "'"+mp.getTemperatureMin() + "', " : "DEFAULT, ";
        sql += (mp.getTemperatureMax() != null) ? "'"+mp.getTemperatureMax() + "', " : "DEFAULT, ";
        sql += (mp.getHumidityValue() != null) ? "'"+mp.getHumidityValue() + "', " : "DEFAULT, ";
        sql += (mp.getPressureValue() != null) ? "'"+mp.getPressureValue() + "', " : "DEFAULT, ";
        sql += (mp.getWindSpeedValue() != null) ? "'"+mp.getWindSpeedValue() + "', " : "DEFAULT, ";
        sql += (mp.getWindDirectionValue() != null) ? "'"+mp.getWindDirectionValue() + "'" : "DEFAULT";
        sql += ")";     
        return sql;
    }

    public synchronized static boolean isRadi() {
        return radi;
    }

    public synchronized static void setRadi(boolean radi) {
        PreuzmiMeteoPodatke.radi = radi;
    }

}
