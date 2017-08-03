/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.entities;

import com.phicaro.tseks.util.Logger;
import com.phicaro.tseks.util.exceptions.BadArgumentException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Placc
 */
public class TableGroup implements Cloneable {
    
    private PriceCategory priceCategory;
    private int seatsNumber;
    private List<Table> tables;
    
    public TableGroup(int seats, PriceCategory priceCategory) {
        this.tables = new ArrayList<>();
        
        this.priceCategory = priceCategory;
        this.seatsNumber = seats;
    }
    
    public void addTable(Table table) {
        if(table.getSeats() != this.seatsNumber) {
            throw new BadArgumentException();
        }
        
        if(!tables.contains(table)) {
            tables.add(table);
        }
    }
    
    public void removeTable(Table table) {
        tables.remove(table);
    }
    
    public void clearTables() {
        tables.clear();
    }
    
    public PriceCategory getCategory() {
        return priceCategory;
    }
    
    public List<Table> getTables() {
        return new ArrayList<>(tables);
    }
    
    public int getSeatsNumber() {
        return seatsNumber;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TableGroup && ((TableGroup) o).priceCategory.equals(priceCategory) && ((TableGroup) o).seatsNumber == seatsNumber && ((TableGroup) o).tables.equals(tables);
    }
    
    @Override
    public TableGroup clone() {
        try {
            TableGroup clone = (TableGroup) super.clone();
            clone.seatsNumber = seatsNumber;
            clone.priceCategory = priceCategory;
            clone.tables.addAll(tables);
            return clone;
        } catch (CloneNotSupportedException e) {
            Logger.error("table-group clone", e);
        }
        return null;
    }
}