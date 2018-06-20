/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.alemartin.ejb.eb.MqttPoruke;
import org.foi.nwtis.alemartin.ejb.sb.MqttPorukeFacade;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.web.utils.SessionUtils;

/**
 *
 * @author TOSHIBA
 */
@Named(value = "mqtt")
@RequestScoped
public class Mqtt implements Serializable {

    @EJB
    private MqttPorukeFacade mqttPorukeFacade;

    private int numberOfRecords;
    private int pageIndex;
    private int lastPageIndex;

    private int recordsPerPage;
    private List<MqttPoruke> listMqtt = new ArrayList<>();

    private boolean showButtonNext;
    private boolean showButtonPrevious;

    private HttpSession session;

    public Mqtt() {
    }

    @PostConstruct
    private void init() {
        ServletContext sc = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        Konfiguracija config = (Konfiguracija) sc.getAttribute("konfiguracija_aplikacije");
        recordsPerPage = Integer.parseInt(config.dajPostavku("stranicenje.brojZapisa"));
        session = SessionUtils.getSession();
        fetchMqttMessages();
        refreshView();
    }

    private void getSessionData() {
        if (session.getAttribute("pageIndexMqtt") == null) {
            pageIndex = 0;
            session.setAttribute("pageIndexMqtt", pageIndex);
        } else {
            pageIndex = (int) session.getAttribute("pageIndexMqtt");
        }
    }

    private void fetchMqttMessages() {
        int offset = pageIndex * recordsPerPage;
        int[] range = {offset, (offset + recordsPerPage - 1)};
        listMqtt.clear();
        listMqtt = mqttPorukeFacade.findRangeByUser(range, SessionUtils.getUsername());
        refreshView();
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

    public void deelteRecords() {
        List<MqttPoruke> mqttMessages = mqttPorukeFacade.findAll();
        for (MqttPoruke mqtt : mqttMessages) {
            mqttPorukeFacade.remove(mqtt);
        }
    }

    public void nextPage() {
        session.setAttribute("pageIndexLog", ++pageIndex);
        fetchMqttMessages();
        refreshView();
    }

    public void previousPage() {
        session.setAttribute("pageIndexLog", --pageIndex);
        fetchMqttMessages();
        refreshView();
    }

    public List<MqttPoruke> getListMqtt() {
        return listMqtt;
    }

    public void setListMqtt(List<MqttPoruke> listMqtt) {
        this.listMqtt = listMqtt;
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

    public String log() {
        return "log";
    }

    public String parkings() {
        return "parkings";
    }
}
