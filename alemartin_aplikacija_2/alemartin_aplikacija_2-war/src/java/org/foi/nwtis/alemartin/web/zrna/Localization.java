/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import org.foi.nwtis.alemartin.slusaci.SlusacAplikacije;

/**
 *
 * @author TOSHIBA
 */
@Named(value = "localization")
@SessionScoped
public class Localization implements Serializable {

    private String selectedLanguage;
    private Locale locale;

    public Localization() {
    }

    @PostConstruct
    public void init() {
        locale = FacesContext.getCurrentInstance().getApplication().getDefaultLocale();
        SlusacAplikacije.servletContext.setAttribute("lokalizacija", locale);
    }

    public Object selectLanguage(String language) {
        locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
        SlusacAplikacije.servletContext.setAttribute("lokalizacija", locale);
        return "";
    }

    public Locale getLocale() {
        return locale;
    }

    public String getSelectedLanguage() {
        UIViewRoot uivr = FacesContext.getCurrentInstance().getViewRoot();
        if (uivr != null) {
            selectedLanguage = FacesContext.getCurrentInstance().getViewRoot().getLocale().getLanguage();
        }
        return selectedLanguage;
    }
}
