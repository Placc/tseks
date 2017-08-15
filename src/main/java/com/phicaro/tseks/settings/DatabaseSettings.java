/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.settings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.phicaro.tseks.database.DatabaseType;

/**
 *
 * @author Placc
 */
public class DatabaseSettings implements ISettings {
    
    @JsonProperty("connection") 
    private String connection = "";
    @JsonProperty("user") 
    private String user = "";
    @JsonProperty("password")
    private String password = "";
    @JsonProperty("type")
    private DatabaseType type = DatabaseType.SQLite;
    
    public DatabaseSettings() {
    }

    @JsonCreator
    public DatabaseSettings(@JsonProperty("connection") String connection, @JsonProperty("user") String user, @JsonProperty("password") String password, @JsonProperty("type") DatabaseType type) {
        this.connection = connection;
        this.user = user;
        this.password = password;
        this.type = type;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DatabaseType getDatabaseType() {
        return type;
    }

    public void setDatabaseType(DatabaseType type) {
        this.type = type;
    }
}
