/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.alemartin.ejb.sb.UserService;
import org.foi.nwtis.alemartin.web.utils.SessionUtils;

/**
 *
 * @author alemartin
 */
@Named(value = "login")
@SessionScoped
public class Login implements Serializable {

    @EJB
    UserService userService;
    
    private String username;
    private String password;

    /**
     * Creates a new instance of Login
     */
    public Login() {
    }

    public String login() {
        if (userService.authentication(username, password)) {
            return "index";
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Invalid Credentials",
                            "Enter correct username and password"));
            return "login";
        }
    }

    public String logout() {
        HttpSession session = SessionUtils.getSession();
        session.invalidate();
        return "login";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
