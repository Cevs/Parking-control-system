/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.filter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.alemartin.ejb.eb.Dnevnik;
import org.foi.nwtis.alemartin.ejb.sb.DnevnikFacade;

/**
 *
 * @author TOSHIBA
 */
@WebFilter(urlPatterns = {"*.xhtml"})
public class LogFilter implements Filter {

    @EJB
    private DnevnikFacade dnevnikFacade;
    
    long start;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        start = System.currentTimeMillis();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        HttpServletRequest req = (HttpServletRequest) request;
        long elapsed = (System.currentTimeMillis() - start);
        String url = req.getRequestURL().toString().replace("/alemartin_aplikacija_2-war/faces","");
        int status = ((HttpServletResponse) response).getStatus();
        String ip = request.getRemoteAddr();
        if (ip.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ipAddress = inetAddress.getHostAddress();
            ip = ipAddress;
        }

        HttpSession session = req.getSession(false);
        String user = "Guest";
        if (session != null && session.getAttribute("username") != null) {
            user = (String) session.getAttribute("username");
        }

        Dnevnik d = new Dnevnik(0, user, url, ip, "HTTP", "", new Date(),(int) elapsed, status);
        dnevnikFacade.create(d);
    }

    @Override
    public void destroy() {
    }

}
