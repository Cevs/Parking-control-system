/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.rest.serveri;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.rest.klijenti.GMKlijent;
import org.foi.nwtis.alemartin.rest.klijenti.Lokacija;
import org.foi.nwtis.alemartin.utils.BazaPodataka;
import org.foi.nwtis.alemartin.web.podaci.Parkiraliste;
import org.foi.nwtis.alemartin.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.alemartin.ws.klijenti.ParkiranjeWSKlijenti;
import org.foi.nwtis.alemartin.ws.klijenti.Vozilo;
import sun.misc.BASE64Decoder;

/**
 * REST Web Service
 *
 * @author alemartin
 */
@Path("application1")
public class Application1REST {

    private String nwtisKorIme;
    private String nwtisLozinka;
    private String openWeatherApiKey;
    private String googleMapsApiKey;

    /**
     * Creates a new instance of ParkiralisteREST
     */
    public Application1REST() {
        ServletContext servletContext = SlusacAplikacije.getServletContext();
        Konfiguracija konfig = (Konfiguracija) servletContext.getAttribute("konfiguracija_aplikacije");
        nwtisKorIme = konfig.dajPostavku("nwtis.korisnickoIme");
        nwtisLozinka = konfig.dajPostavku("nwtis.lozinka");
        openWeatherApiKey = konfig.dajPostavku("apikey");
        googleMapsApiKey = konfig.dajPostavku("gmapikey");
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.alemartin.rest.serveri.ParkiralisteREST
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String dajSvaParkiralista(@HeaderParam("authorization") String auth) {
        try {
            String userAuth = getUserAuthentication(auth);
            String username = userAuth.split(":")[0];
            String password = userAuth.split(":")[1];

            if (!BazaPodataka.autentificirajKorisnika(username, password)) {
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Neuspjela autorizacija");
            }
            BazaPodataka.UpisDnevnika(username, "REST", "Preuzmi sva parkirališta");
            ArrayList<Parkiraliste> parkiralista = new ArrayList<>();
            String sql = "SELECT *FROM parkiralista";
            try (Connection conn = BazaPodataka.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
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
                Logger.getLogger(Application1REST.class.getName()).log(Level.SEVERE, null, ex);
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Pogreska kod dohvata parkiralista iz baze");
            }
            //Za provjeru
            //Evenutano napravi sinkronizaciju (tako da se se provjere popisi parkiralista)
            ArrayList<org.foi.nwtis.alemartin.ws.klijenti.Parkiraliste> parkiralista2 = (ArrayList<org.foi.nwtis.alemartin.ws.klijenti.Parkiraliste>) ParkiranjeWSKlijenti.dajSvaParkiralistaGrupe(nwtisKorIme, nwtisLozinka);
            return kreirajJSONParkiraliste(parkiralista, false, "");
        } catch (Exception ex) {
            return kreirajJSONParkiraliste(new ArrayList<>(), true, "Pogreška zahtjeva");
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String dajPojedinacnoParkiraliste(@HeaderParam("authorization") String auth, @PathParam("id") int id) {
        try {
            String userAuth = getUserAuthentication(auth);
            String username = userAuth.split(":")[0];
            String password = userAuth.split(":")[1];
            if (!BazaPodataka.autentificirajKorisnika(username, password)) {
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Neuspjela autorizacija");
            }
            BazaPodataka.UpisDnevnika(username, "REST", "Preuzmi određeno parkirališta");
            ArrayList<Parkiraliste> parkiralista = new ArrayList<>();
            String sql = "SELECT *FROM parkiralista WHERE id = ?";
            try (Connection conn = BazaPodataka.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String naziv = rs.getString("naziv");
                    String adresa = rs.getString("adresa");
                    String latitude = rs.getString("latitude");
                    String longitude = rs.getString("longitude");
                    Parkiraliste parkiraliste = new Parkiraliste(id, naziv, adresa, new Lokacija(latitude, longitude));
                    parkiralista.add(parkiraliste);
                } else {
                    return kreirajJSONParkiraliste(new ArrayList<>(), true, "Nepostoji parkiralsite s id: " + id);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Application1REST.class.getName()).log(Level.SEVERE, null, ex);
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Pogreska kod dohvacanja parkiralista");
            }
            return kreirajJSONParkiraliste(parkiralista, false, "");
        } catch (Exception ex) {
            return kreirajJSONParkiraliste(new ArrayList<>(), true, "Pogreška zahtjeva");
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String dodajParkiraliste(@HeaderParam("authorization") String auth, String sadrzaj) {
        String naziv;
        String adresa;
        int ukupno;
        int zauzeto;
        try {
            String userAuth = getUserAuthentication(auth);
            String username = userAuth.split(":")[0];
            String password = userAuth.split(":")[1];
            if (!BazaPodataka.autentificirajKorisnika(username, password)) {
                return kreirajJSONParkiraliste(new ArrayList<Parkiraliste>(), true, "Neuspjela autentifikacija");
            }
            BazaPodataka.UpisDnevnika(username, "REST", "Dodaj novo parkiralište");
            JsonReader jsonReader = Json.createReader(new StringReader(sadrzaj));
            JsonObject jsonObject = jsonReader.readObject();
            try {
                naziv = jsonObject.getString("naziv").trim();
                adresa = jsonObject.getString("adresa").trim();
                ukupno = Integer.parseInt(jsonObject.getString("ukupnoMjesta").trim());
                zauzeto = Integer.parseInt(jsonObject.getString("zauzetaMjesta").trim());
            } catch (NumberFormatException ex) {
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Neispravni tipovi podataka");
            } catch (Exception ex) {
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Kriva struktura json-a");
            }

            if (naziv.isEmpty() || adresa.isEmpty()) {
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Nedostaje naziv i/ili adresa parkirališta");
            }

            if (parkiralistePostoji(adresa)) {
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Parkiraliste vec postoji");
            }
            int id = -1;
            try {
                id = dodajParkiraliste(naziv, adresa, ukupno, zauzeto);
                ParkiranjeWSKlijenti.dodajNovoParkiralisteGrupi(nwtisKorIme, nwtisLozinka, id, naziv, adresa, ukupno);
            } catch (Exception ex) {
                //Sinkronizacija parkiralista s bazom podataka, u slucaju da ParkiranjeWS rezultira greškom
                if (id != -1) {
                    try {
                        obrisiParkiraliste(id);
                    } catch (Exception ex2) {
                    }
                }
                return kreirajJSONParkiraliste(new ArrayList<>(), true, ex.getMessage());
            }
            return kreirajJSONParkiraliste(new ArrayList<>(), false, "");
        } catch (Exception ex) {
            return kreirajJSONParkiraliste(new ArrayList<>(), true, "Pogreška zahtjeva");
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String dodajParkiraliste(@HeaderParam("authorization") String auth, @PathParam("id") int id, String sadrzaj) {
        return kreirajJSONParkiraliste(new ArrayList<>(), true, "Nije dozvoljeno");
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String azurirajParkiraliste(@HeaderParam("authorization") String auth, @PathParam("id") int id, String sadrzaj) {
        String naziv;
        String adresa;
        int ukupno;
        int zauzeto;
        try {
            String userAuth = getUserAuthentication(auth);
            String username = userAuth.split(":")[0];
            String password = userAuth.split(":")[1];
            if (!BazaPodataka.autentificirajKorisnika(username, password)) {
                return kreirajJSONParkiraliste(new ArrayList<Parkiraliste>(), true, "Neuspjela autentifikacija");
            }
            BazaPodataka.UpisDnevnika(username, "REST", "Ažuriraj parkiralište");
            JsonReader jsonReader = Json.createReader(new StringReader(sadrzaj));
            JsonObject jsonObject = jsonReader.readObject();
            try {
                naziv = jsonObject.getString("naziv").trim();
                adresa = jsonObject.getString("adresa").trim();
                ukupno = Integer.parseInt(jsonObject.getString("ukupnoMjesta").trim());
                zauzeto = Integer.parseInt(jsonObject.getString("zauzetaMjesta").trim());
            } catch (NumberFormatException ex) {
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Neispravni tipovi podataka");
            } catch (Exception ex) {
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Kriva struktura json-a");
            }

            if (naziv.isEmpty() || adresa.isEmpty()) {
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Nedostaje naziv i/ili adresa parkirališta");
            }

            if (!parkiralistePostoji(id)) {
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Parkiraliste ne postoji");
            }

            //TODO: Vidjeti kako ovo ispravno sinkronizirati u slučaju greške
            try {
                ParkiranjeWSKlijenti.obrisiParkiralisteGrupe(nwtisKorIme, nwtisLozinka, id);
                ParkiranjeWSKlijenti.dodajNovoParkiralisteGrupi(nwtisKorIme, nwtisLozinka, id, naziv, adresa, ukupno);
                azurirajParkiraliste(naziv, adresa, ukupno, zauzeto, id);
            } catch (Exception ex) {
                return kreirajJSONParkiraliste(new ArrayList<>(), true, ex.getMessage());
            }
            return kreirajJSONParkiraliste(new ArrayList<>(), false, "");
        } catch (Exception ex) {
            return kreirajJSONParkiraliste(new ArrayList<>(), true, "Pogreška zahtjeva");
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String azurirajParkiralsite(@HeaderParam("authorization") String auth, String sadrzaj) {
        return kreirajJSONParkiraliste(new ArrayList<>(), true, "Nije dozvoljeno");
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String obrisiParkiraslite(@HeaderParam("authorization") String auth, @PathParam("id") int id) {
        try {
            String userAuth = getUserAuthentication(auth);
            String username = userAuth.split(":")[0];
            String password = userAuth.split(":")[1];
            if (!BazaPodataka.autentificirajKorisnika(username, password)) {
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Neuspjela autentifikacija");
            }
            BazaPodataka.UpisDnevnika(username, "REST", "Obriši parkiralište");
            try {
                obrisiParkiraliste(id);
                ParkiranjeWSKlijenti.obrisiParkiralisteGrupe(nwtisKorIme, nwtisLozinka, id);
            } catch (Exception ex) {
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Neuspjelo brisanje parkirališta" + ex.getMessage());
            }
            return kreirajJSONParkiraliste(new ArrayList<>(), false, "OK");
        } catch (Exception ex) {
            return kreirajJSONParkiraliste(new ArrayList<>(), true, "Pogreška zahtjeva");
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String obrisiParkiraliste(@HeaderParam("authorization") String auth) {
        return kreirajJSONParkiraliste(new ArrayList<>(), true, "Nije dozvoljeno");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/vozila/")
    public String dajVozilaParkiralista(@HeaderParam("authorization") String auth, @PathParam("id") int id) {
        try {
            String userAuth = getUserAuthentication(auth);
            String username = userAuth.split(":")[0];
            String password = userAuth.split(":")[1];
            if (!BazaPodataka.autentificirajKorisnika(username, password)) {
                return kreirajJSONParkiraliste(new ArrayList<>(), true, "Neuspjela autentifikacija");
            }

            if (!parkiralistePostoji(id)) {
                return kreirajJSONVozila(new ArrayList<>(), true, "Parkiraliste ne postoji");
            }

            BazaPodataka.UpisDnevnika(username, "REST", "Dohvati vozila parkirališta");
            ArrayList<Vozilo> vozila = (ArrayList) ParkiranjeWSKlijenti.dajSvaVozilaParkiralistaGrupe(nwtisKorIme, nwtisLozinka, id);
            if (vozila.isEmpty()) {
                return kreirajJSONVozila(vozila, true, "Parkiraliste ne postoji");
            }
            return kreirajJSONVozila(vozila, false, "OK");
        } catch (Exception ex) {
            return kreirajJSONParkiraliste(new ArrayList<>(), true, "Pogreška zahtjeva");
        }
    }

    private int dodajParkiraliste(String naziv, String adresa, int ukupno, int zauzeto) throws Exception {
        float latitude;
        float longitude;
        try {
            GMKlijent gmk = new GMKlijent(googleMapsApiKey);
            Lokacija lok = gmk.getGeoLocation(adresa);
            latitude = Float.parseFloat(lok.getLatitude());
            longitude = Float.parseFloat(lok.getLongitude());
        } catch (Exception ex) {
            throw new Exception("Nepostojeca adresa");
        }

        String sql = "INSERT INTO parkiralista (id, naziv, adresa, latitude, longitude, "
                + "ukupno_mjesta, zauzeta_mjesta) VALUES  (DEFAULT, ?,?,?,?,?,?);";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, naziv);
            ps.setString(2, adresa);
            ps.setFloat(3, latitude);
            ps.setFloat(4, longitude);
            ps.setInt(5, ukupno);
            ps.setInt(6, zauzeto);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            return id;
        } catch (SQLException ex) {
            throw new Exception("Neuspjelo dodavanje" + ex.getMessage());
        }
    }

    private void azurirajParkiraliste(String naziv, String adresa, int ukupno, int zauzeto, int id) throws Exception {
        float latitude;
        float longitude;
        try {
            GMKlijent gmk = new GMKlijent(googleMapsApiKey);
            Lokacija lok = gmk.getGeoLocation(adresa);
            latitude = Float.parseFloat(lok.getLatitude());
            longitude = Float.parseFloat(lok.getLongitude());
        } catch (Exception ex) {
            throw new Exception("Nepostojeca adresa");
        }

        String sql = "UPDATE parkiralista SET naziv = ?, adresa = ?, latitude = ?, "
                + "longitude = ?, ukupno_mjesta = ?, zauzeta_mjesta = ? WHERE id = ?";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareCall(sql)) {
            ps.setString(1, naziv);
            ps.setString(2, adresa);
            ps.setFloat(3, latitude);
            ps.setFloat(4, longitude);
            ps.setInt(5, ukupno);
            ps.setInt(6, zauzeto);
            ps.setInt(7, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Application1REST.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Neuspjelo ažuriranje" + ex.getMessage());
        }
    }

    private boolean parkiralistePostoji(String naziv) {
        String sql = "SELECT *FROM parkiralista WHERE adresa =  ?";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, naziv);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(Application1REST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean parkiralistePostoji(int id) {
        String sql = "SELECT *FROM parkiralista WHERE id =  ?";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(Application1REST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void obrisiParkiraliste(int id) throws Exception {
        String sql = "DELETE FROM parkiralista WHERE id = ?";
        try (Connection conn = BazaPodataka.getConnection();
                PreparedStatement ps = conn.prepareCall(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new Exception("Nuspjelo brisanje parkirališta");
        }
    }

    private String kreirajJSONParkiraliste(ArrayList<Parkiraliste> parkiralista, boolean greska, String poruka) {
        JsonObjectBuilder json = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayParkiralista = Json.createArrayBuilder();
        for (Parkiraliste p : parkiralista) {
            JsonObjectBuilder jsonParkiraliste = Json.createObjectBuilder();
            jsonParkiraliste.add("id", p.getId());
            jsonParkiraliste.add("naziv", p.getNaziv());
            jsonParkiraliste.add("adresa", p.getAdresa());
            jsonParkiraliste.add("latitude", p.getGeoloc().getLatitude());
            jsonParkiraliste.add("longitude", p.getGeoloc().getLongitude());

            jsonArrayParkiralista.add(jsonParkiraliste);
        }
        json.add("odgovor", jsonArrayParkiralista);
        if (greska) {
            json.add("status", "ERR");
            json.add("poruka", poruka);
        } else {
            json.add("status", "OK");
        }

        return json.build().toString();
    }

    private String kreirajJSONVozila(ArrayList<Vozilo> vozila, boolean greska, String poruka) {
        JsonObjectBuilder json = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayVozila = Json.createArrayBuilder();
        for (Vozilo v : vozila) {
            JsonObjectBuilder jsonVozilo = Json.createObjectBuilder();
            jsonVozilo.add("idParkiraliste", v.getParkiraliste());
            jsonVozilo.add("akcija", v.getAkcija().value().toLowerCase());
            jsonVozilo.add("naziv", v.getRegistracija());
            jsonArrayVozila.add(jsonVozilo);
        }
        json.add("odgovor", jsonArrayVozila);
        if (greska) {
            json.add("status", "ERR");
            json.add("poruka", poruka);
        } else {
            json.add("status", "OK");
        }

        return json.build().toString();
    }

    private String getUserAuthentication(String authString) {
        String decodedAuth = "";
        String[] authParts = authString.split("\\s+");
        String authInfo = authParts[1];
        //Decode the data back to original string
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(authInfo);
        } catch (IOException ex) {
            Logger.getLogger(Application1REST.class.getName()).log(Level.SEVERE, null, ex);
        }
        decodedAuth = new String(bytes);
        return decodedAuth;
    }
}
