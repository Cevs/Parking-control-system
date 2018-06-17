/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author TOSHIBA
 */
@Named(value = "index")
@SessionScoped
public class Index implements Serializable {

    /**
     * Creates a new instance of Index
     */
    public Index() {
    }
    
    /* Navigation */
    
    public String profile(){
        return "profile";
    }
    
}
