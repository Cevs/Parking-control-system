/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.ejb.services;

import javax.ejb.Local;
import org.foi.nwtis.alemartin.web.podaci.User;

/**
 *
 * @author TOSHIBA
 */
@Local
public interface UserService {
    public boolean authentication(User user);
}
