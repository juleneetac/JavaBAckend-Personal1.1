package org.models;

import org.mindrot.jbcrypt.BCrypt;

public class Users {    //password en orden: 0000    1111
    String username;
    String email;
    String hash;
    String password;
    int enabled;
    String verification_code;



    public Users() {} //Empty constructor

    public Users(String name, String email, String hash){
        this.username = name;
        this.email = email;
        this.hash = hash;
    }




    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getEnabled() {
        return enabled;
    }
    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getVerification_code() {
        return verification_code;
    }

    public void setVerification_code(String verification_code) {
        this.verification_code = verification_code;
    }
}

