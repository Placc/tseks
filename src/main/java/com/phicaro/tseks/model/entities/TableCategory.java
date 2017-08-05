/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.entities;

import com.phicaro.tseks.util.Logger;
import com.phicaro.tseks.util.exceptions.BadArgumentException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Placc
 */
public class TableCategory implements ITableCategory {
    
    private String id;
    private PriceCategory priceCategory;
    private int seatsNumber;
    private List<Table> tables;
    
    public TableCategory(String id, int seats, PriceCategory priceCategory) {
        this.tables = new ArrayList<>();
        
        this.id = id;
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
    
    public List<Table> getTables() {
        return new ArrayList<>(tables);
    }
    
    public void clearTables() {
        tables.clear();
    }
    
    public PriceCategory getPrice() {
        return priceCategory;
    }
    
    public int getSeatsNumber() {
        return seatsNumber;
    }
    
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ITableCategory 
                && ((ITableCategory) o).getPrice().equals(priceCategory) 
                && ((ITableCategory) o).getSeatsNumber() == seatsNumber 
                && ((ITableCategory) o).getNumberOfTables() == tables.size()
                && ((ITableCategory) o).getMinTableNumber() == getMinTableNumber()
                && ((ITableCategory) o).getMaxTableNumber() == getMaxTableNumber()
                && (!(o instanceof TableCategory) || ((TableCategory) o).getTables().equals(tables));
    }
    
    @Override
    public ITableCategory clone() {
        try {
            TableCategory clone = (TableCategory) super.clone();
            clone.seatsNumber = seatsNumber;
            clone.priceCategory = priceCategory;
            clone.id = id;
            
            clone.tables = new ArrayList<>();
            clone.tables.addAll(tables);
            return clone;
        } catch (CloneNotSupportedException e) {
            Logger.error("table-group clone", e);
        }
        return null;
    }

    @Override
    public int getNumberOfTables() {
        return tables.size();
    }

    @Override
    public int getMinTableNumber() {
        return tables.stream().map(table -> table.getTableNumber()).min(Comparator.naturalOrder()).orElse(0);
    }

    @Override
    public int getMaxTableNumber() {
        return tables.stream().map(table -> table.getTableNumber()).max(Comparator.naturalOrder()).orElse(0);
    }
}
