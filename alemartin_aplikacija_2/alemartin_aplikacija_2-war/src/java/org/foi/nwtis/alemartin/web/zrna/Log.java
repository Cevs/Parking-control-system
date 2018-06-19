/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.zrna;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.alemartin.ejb.eb.Dnevnik;
import org.foi.nwtis.alemartin.ejb.sb.DnevnikFacade;

import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.web.utils.SessionUtils;

/**
 *
 * @author TOSHIBA
 */
@Named(value = "log")
@RequestScoped
public class Log {

    @EJB
    private DnevnikFacade dnevnikFacade;

    private int numberOfRecords;
    private int pageIndex;
    private int lastPageIndex;

    private int recordsPerPage;
    private List<Dnevnik> logList = new ArrayList<>();

    private boolean showButtonNext;
    private boolean showButtonPrevious;

    private HttpSession session;

    
    public Log() {
    }

    @PostConstruct
    public void init() {
        ServletContext sc = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        Konfiguracija config = (Konfiguracija) sc.getAttribute("konfiguracija_aplikacije");
        recordsPerPage = Integer.parseInt(config.dajPostavku("stranicenje.brojZapisa"));
        session = SessionUtils.getSession();
        getSessionData();      
        getLogRecords(); 
        refreshView();
    }

    private void getSessionData() {
        if (session.getAttribute("pageIndexLog") == null) {
            pageIndex = 0;
            session.setAttribute("pageIndexLog", pageIndex);
        } else {
            pageIndex = (int) session.getAttribute("pageIndexLog");
        }                   
    }

    private void getLogRecords() {
        int offset = pageIndex * recordsPerPage;
        int[] range = {offset, (offset + recordsPerPage - 1)};
        logList.clear();
        logList = dnevnikFacade.findRange(range);
        numberOfRecords = dnevnikFacade.findAll().size();
        lastPageIndex = (int) Math.ceil((double) numberOfRecords / recordsPerPage);
    }

    private void refreshView() {
        if (pageIndex > 0 && pageIndex < lastPageIndex - 1) {
            showButtonPrevious = true;
            showButtonNext = true;
        } else if (lastPageIndex - 1 > 0 && !(pageIndex < lastPageIndex - 1)) {
            showButtonPrevious = true;
            showButtonNext = false;
        } else if (!(pageIndex > 0) && pageIndex < lastPageIndex - 1) {
            showButtonPrevious = false;
            showButtonNext = true;
        } else {
            showButtonPrevious = false;
            showButtonNext = false;
        }
    }

   

    public void nextPage() {
        session.setAttribute("pageIndexLog", ++pageIndex);
        refreshView();
    }

    public void previousPage() {
        session.setAttribute("pageIndexLog", --pageIndex);
        refreshView();
    }

    public List<Dnevnik> getLogList() {
        return logList;
    }

    public void setLogList(List<Dnevnik> logList) {
        this.logList = logList;
    }

    public boolean isShowButtonNext() {
        return showButtonNext;
    }

    public void setShowButtonNext(boolean showButtonNext) {
        this.showButtonNext = showButtonNext;
    }

    public boolean isShowButtonPrevious() {
        return showButtonPrevious;
    }

    public void setShowButtonPrevious(boolean showButtonPrevious) {
        this.showButtonPrevious = showButtonPrevious;
    }

    /* Navigation */
    public String home() {
        return "home";
    }

    public String profile() {
        return "profile";
    }

    public String status() {
        return "status";
    }

    public String inbox() {
        return "inbox";
    }
}
