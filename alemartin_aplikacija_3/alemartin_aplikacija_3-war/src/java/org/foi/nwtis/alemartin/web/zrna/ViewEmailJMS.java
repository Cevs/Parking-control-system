/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import org.foi.nwtis.alemartin.ejb.sb.JMSMessages;
import org.foi.nwtis.alemartin.ejb.sb.UserAction;
import org.foi.nwtis.alemartin.web.podaci.EmailJMS;

/**
 *
 * @author alemartin
 */
@Named(value = "viewEmailJMS")
@SessionScoped
public class ViewEmailJMS implements Serializable {


    @EJB
    JMSMessages jmsMessages;

    private List<EmailJMS> jmsEmails = new ArrayList<>();

    private String idDelete;

    /**
     * Creates a new instance of ViewEmailJMS
     */
    public ViewEmailJMS() {
    }

    public void deleteEmailJMS() {
        jmsMessages.deleteEmail(idDelete);
    }

    public List<EmailJMS> getJmsEmails() {
        jmsEmails = jmsMessages.getEmailMessages();
        return jmsEmails;
    }

    public void setJmsEmails(List<EmailJMS> jmsEmails) {
        this.jmsEmails = jmsEmails;
    }

    public String getIdDelete() {
        return idDelete;
    }

    public void setIdDelete(String idDelete) {
        this.idDelete = idDelete;
    }

    /* Navigation */
    public String home() {
        return "home";
    }

    
}
