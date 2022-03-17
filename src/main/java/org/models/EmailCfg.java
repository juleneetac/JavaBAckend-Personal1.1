package org.models;

import org.UserDAO.UserDAO;
import org.UserDAO.UserImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class EmailCfg {
    @Value("${spring.mail.host}")   //para que pueda cogerlo del archivo application.properties
    String host;

    @Value("${spring.mail.port}")
    int port;

    @Value("${spring.mail.username}")
    String username;

    @Value("${spring.mail.password}")
    String password;

    private static EmailCfg instance;
    public static EmailCfg getInstance() {
        if (instance == null) {
            instance = new EmailCfg();
        }
        return instance;
    }

    public EmailCfg() {} //Empty constructor

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
