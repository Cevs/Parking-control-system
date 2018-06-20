/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.foi.nwtis.alemartin.web.podaci;

import org.foi.nwtis.alemartin.rest.klijenti.Lokacija;

/**
 *
 * @author alemartin
 */
public class Parkiraliste {
    private int id;
    private String naziv;
    private String adresa;
    private Lokacija geoloc;
    private int brojParkirnihMjesta;
    private int brojZauzetihMjesta;
    private int korisnikId;

    public Parkiraliste() {
    }

    public Parkiraliste(int id, String naziv, String adresa, Lokacija geoloc) {
        this.id = id;
        this.naziv = naziv;
        this.adresa = adresa;
        this.geoloc = geoloc;
    }
    
    public Parkiraliste(int id, String naziv, String adresa, Lokacija geoloc, int brojParkirnihMjesta, int brojZauzetihMjesta, int korisnikId) {
        this.id = id;
        this.naziv = naziv;
        this.adresa = adresa;
        this.geoloc = geoloc;
        this.brojParkirnihMjesta = brojParkirnihMjesta;
        this.brojZauzetihMjesta = brojZauzetihMjesta;
        this.korisnikId = korisnikId;
    }

    public Lokacija getGeoloc() {
        return geoloc;
    }

    public void setGeoloc(Lokacija geoloc) {
        this.geoloc = geoloc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }      
	
    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }        

    public int getBrojParkirnihMjesta() {
        return brojParkirnihMjesta;
    }

    public void setBrojParkirnihMjesta(int brojParkirnihMjesta) {
        this.brojParkirnihMjesta = brojParkirnihMjesta;
    }

    public int getBrojZauzetihMjesta() {
        return brojZauzetihMjesta;
    }

    public void setBrojZauzetihMjesta(int brojZauzetihMjesta) {
        this.brojZauzetihMjesta = brojZauzetihMjesta;
    }

    public int getKorisnikId() {
        return korisnikId;
    }

    public void setKorisnikId(int korisnikId) {
        this.korisnikId = korisnikId;
    }
    
    
}
