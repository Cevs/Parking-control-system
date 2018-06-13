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
    private int idKorisnik;
    private String url;
    private String ip;
    private String vrstaZahtjeva;
    private String sadrzajZahtjeva;
    private Date vrijeme;
    private long trajanje;
    private int status;

    public Dnevnik(int id, int idKorisnik, String url, String ip, String vrstaZahtjeva, String sadrzajZahtjeva, Date vrijeme, long trajanje, int status) {
        this.id = id;
        this.idKorisnik = idKorisnik;
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

    public int getIdKorisnik() {
        return idKorisnik;
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

    public int getStatus() {
        return status;
    }
    
    
    
}
