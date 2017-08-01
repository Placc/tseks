/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.entities;

import java.util.UUID;

/**
 *
 * @author Placc
 */
public class Table {
    
    private int seats;
    private String id;
    
    public Table(int seats) {
        this.id = UUID.randomUUID().toString();
        this.seats = seats;
    }
    
    public int getSeats() {
        return seats;
    }  

    public String getId() {
        return id;
    }
    
    
}
