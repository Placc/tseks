/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.entities;

/**
 *
 * @author Placc
 */
public class Table {
    
    private int seats;
    private PriceCategory category;
    
    public Table(int seats) {
        this.seats = seats;
    }
    
    public Table(int seats, PriceCategory category) {
        this(seats);
        this.category = category;
    }
    
    public int getSeats() {
        return seats;
    }
    
    public PriceCategory getCategory() {
        return category;
    }    
}
