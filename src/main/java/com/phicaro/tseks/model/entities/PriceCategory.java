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
public class PriceCategory {
    
    private double price;
    
    public PriceCategory(double price) {
        this.price = price;
    }
    
    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof PriceCategory && ((PriceCategory) o).price == price;
    }
    
    public double getPrice() {
        return price;
    }
}
