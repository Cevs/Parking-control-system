/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.ejb.sb;

import java.io.StringReader;
import javax.ejb.Stateful;
import javax.ejb.LocalBean;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.alemartin.rest.klijenti.Application3REST;
import org.foi.nwtis.alemartin.web.utils.SessionUtils;

/**
 *
 * @author TOSHIBA
 */
@Stateful
@LocalBean
public class UserService {

    public boolean authentication(String username, String password) {
        Application3REST app3REST = new Application3REST(username, password);
        String response = app3REST.authentication();

        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject responseObject = jsonReader.readObject();
        String code = responseObject.getString("status");
        if (code.contains("OK")) {
            HttpSession session = SessionUtils.getSession();
            session.setAttribute("username", username);
            session.setAttribute("password", password);
            return true;
        } else {
            return false;
        }
    }
}
