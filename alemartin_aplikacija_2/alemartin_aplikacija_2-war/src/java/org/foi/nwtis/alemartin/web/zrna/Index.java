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
@Named(value = "index")
@SessionScoped
public class Index implements Serializable {

    private String selectedLanguage;
    private Locale locale;
   
    public Index() {
    }
      
    /* Navigation */
    
    public String profile(){
        return "profile";
    }
    
    public String status(){
        return "status";
    }
    
    public String inbox(){
        return "inbox";
    }
    
    public String log(){
        return "log";
    }
    
    public String parkings(){
        return "parkings";
    }
    
    public String mqtt(){
        return "mqtt";
    }
}
