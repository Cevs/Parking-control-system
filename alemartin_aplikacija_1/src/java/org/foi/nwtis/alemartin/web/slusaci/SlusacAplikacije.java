/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.slusaci;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.alemartin.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.alemartin.konfiguracije.NemaKonfiguracije;

/**
 * Web application lifecycle listener.
 *
 * @author TOSHIBA
 */
public class SlusacAplikacije implements ServletContextListener {

    private PreuzmiMeteoPodatke preuzmiMeteoPodatke = null;
    private static ServletContext servletContext;
    @Override
    public void contextInitialized(ServletContextEvent sce) {      
        try {
            servletContext = sce.getServletContext();
            String putanja = servletContext.getRealPath("/WEB-INF") + java.io.File.separator;
            String datoteka = servletContext.getInitParameter("konfiguracija");
            Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(putanja + datoteka);
            servletContext.setAttribute("konfiguracija", konfig);
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
              
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
