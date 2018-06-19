/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.ejb.eb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author TOSHIBA
 */
@Entity
@Table(name = "MQTT_PORUKE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MqttPoruke.findAll", query = "SELECT m FROM MqttPoruke m")
    , @NamedQuery(name = "MqttPoruke.findById", query = "SELECT m FROM MqttPoruke m WHERE m.id = :id")
    , @NamedQuery(name = "MqttPoruke.findByKorisnik", query = "SELECT m FROM MqttPoruke m WHERE m.korisnik = :korisnik")
    , @NamedQuery(name = "MqttPoruke.findByAkcija", query = "SELECT m FROM MqttPoruke m WHERE m.akcija = :akcija")
    , @NamedQuery(name = "MqttPoruke.findByRegistracija", query = "SELECT m FROM MqttPoruke m WHERE m.registracija = :registracija")
    , @NamedQuery(name = "MqttPoruke.findByVrijeme", query = "SELECT m FROM MqttPoruke m WHERE m.vrijeme = :vrijeme")
    , @NamedQuery(name = "MqttPoruke.findByIdparkiraliste", query = "SELECT m FROM MqttPoruke m WHERE m.idparkiraliste = :idparkiraliste")})
public class MqttPoruke implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "KORISNIK")
    private String korisnik;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "AKCIJA")
    private String akcija;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "REGISTRACIJA")
    private String registracija;
    @Column(name = "VRIJEME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vrijeme;
    @Basic(optional = false)
    @NotNull
    @Column(name = "IDPARKIRALISTE")
    private int idparkiraliste;

    public MqttPoruke() {
    }

    public MqttPoruke(Integer id) {
        this.id = id;
    }

    public MqttPoruke(Integer id, String korisnik, String akcija, String registracija, int idparkiraliste) {
        this.id = id;
        this.korisnik = korisnik;
        this.akcija = akcija;
        this.registracija = registracija;
        this.idparkiraliste = idparkiraliste;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public String getAkcija() {
        return akcija;
    }

    public void setAkcija(String akcija) {
        this.akcija = akcija;
    }

    public String getRegistracija() {
        return registracija;
    }

    public void setRegistracija(String registracija) {
        this.registracija = registracija;
    }

    public Date getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(Date vrijeme) {
        this.vrijeme = vrijeme;
    }

    public int getIdparkiraliste() {
        return idparkiraliste;
    }

    public void setIdparkiraliste(int idparkiraliste) {
        this.idparkiraliste = idparkiraliste;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MqttPoruke)) {
            return false;
        }
        MqttPoruke other = (MqttPoruke) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.alemartin.ejb.eb.MqttPoruke[ id=" + id + " ]";
    }
    
}
