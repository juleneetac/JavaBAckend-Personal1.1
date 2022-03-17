package org.utils;

import org.UserDAO.UserImpl;
import org.apache.log4j.Logger;

import org.glassfish.grizzly.http.server.Request;
import javax.servlet.http.HttpServletRequest;

public class Utility {
    final static Logger log = Logger.getLogger(Utility.class.getName());
    public static String getSiteURL(Request request) {
        String siteURL = request.getRequestURL().toString();

        System.out.println(String.format(siteURL));
        siteURL = eliminarUltimaPalabra(siteURL);
        System.out.println(String.format(siteURL));
        //log.info(siteURL);

        return siteURL; //.replace(request.getServletPath(), "");
    }

    public static String eliminarUltimaPalabra(String s) {
        int pos;
        s = s.trim();
        pos = s.lastIndexOf("/");
        if (pos != -1) {
            s = s.substring(0, pos);
        } else {
            s = "";
        }
        return s;
    }
}
