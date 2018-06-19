/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.podaci;

import java.util.Date;

/**
 *
 * @author TOSHIBA
 */
public class Dnevnik {
    private int id;
    private String korisnik;
    private String url;
    private String ip;
    private String vrstaZahtjeva;
    private String sadrzajZahtjeva;
    private Date vrijeme;
    private long trajanje;
    private String status;

    public Dnevnik(int id, String korisnik, String url, String ip, String vrstaZahtjeva, String sadrzajZahtjeva, Date vrijeme, long trajanje, String status) {
        this.id = id;
        this.korisnik = korisnik;
        this.url = url;
        this.ip = ip;
        this.vrstaZahtjeva = vrstaZahtjeva;
        this.sadrzajZahtjeva = sadrzajZahtjeva;
        this.vrijeme = vrijeme;
        this.trajanje = trajanje;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getKorisnik() {
        return korisnik;
    }
  
    public String getUrl() {
        return url;
    }

    public String getIp() {
        return ip;
    }

    public String getVrstaZahtjeva() {
        return vrstaZahtjeva;
    }

    public String getSadrzajZahtjeva() {
        return sadrzajZahtjeva;
    }

    public Date getVrijeme() {
        return vrijeme;
    }

    public long getTrajanje() {
        return trajanje;
    }

    public String getStatus() {
        return status;
    }
    
    
    
}
