/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.slusaci;

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
 *
 * @author TOSHIBA
 */
public class SlusacAplikacije implements ServletContextListener {

    public static ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        servletContext = sce.getServletContext();
        String datoteka = servletContext.getInitParameter("konfiguracija_aplikacije");
        String putanja = servletContext.getRealPath("/WEB-INF") + java.io.File.separator;
        String punaPutanja = putanja + datoteka;

        try {
            Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(punaPutanja);
            servletContext.setAttribute("konfiguracija_aplikacije", konfig);
         
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        servletContext = sce.getServletContext();
        servletContext.removeAttribute("konfiguracija_aplikacije");
    }

}
