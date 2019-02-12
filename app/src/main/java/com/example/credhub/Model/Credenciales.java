package com.example.credhub.Model;

import java.io.Serializable;

/**
 * Created by Jaime García on 08,febrero,2019
 */

public class Credenciales implements Serializable {

    private String id;
    private String username;
    private String password;

    public Credenciales() {
    }

    public Credenciales( String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
