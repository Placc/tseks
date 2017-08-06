/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.entities;

import java.util.UUID;

/**
 *
 * @author Placc
 */
public class Table {
    
    private String id;
    private int seats;
    private int tableNumber;
    
    public Table(String id, int tableNumber, int seats) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.seats = seats;
    }
    
    public int getSeats() {
        return seats;
    }  

    public int getTableNumber() {
        return tableNumber;
    }
    
    public String getId() {
        return id;
    }
}
