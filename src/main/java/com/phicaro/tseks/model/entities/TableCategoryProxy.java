/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.entities;

import com.phicaro.tseks.model.services.TableCategoryService;
import com.phicaro.tseks.util.Logger;
import java.util.List;

/**
 *
 * @author Placc
 */
public class TableCategoryProxy implements ITableCategory {

    private PriceCategory price;
    private String id;
    private int seats;
    private int numberOfTables;
    private TableCategory original;
    
    public TableCategoryProxy(String id, int numberOfTables, int seats, PriceCategory price) {
        this.id = id;
        this.numberOfTables = numberOfTables;
        this.seats = seats;
        this.price = price;
    }
    
    @Override
    public PriceCategory getPrice() {
        return price;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getSeatsNumber() {
        return seats;
    }

    @Override
    public int getNumberOfTables() {
        if(original == null) {
            return numberOfTables;
        }
        return original.getNumberOfTables();
    }

    @Override
    public List<Table> getTables() {
        resolveOriginal();
        return original.getTables();
    }

    @Override
    public void addTable(Table table) {
        resolveOriginal();
        original.addTable(table);
    }

    @Override
    public void removeTable(Table table) {
        resolveOriginal();
        original.removeTable(table);
    }

    @Override
    public void clearTables() {
        resolveOriginal();
        original.clearTables();
    }

    @Override
    public ITableCategory clone() {
        try {
            TableCategoryProxy clone = (TableCategoryProxy) super.clone();
            
            clone.id = id;
            clone.numberOfTables = numberOfTables;
            clone.price = new PriceCategory(price.getPrice());
            clone.seats = seats;
            
            if(original != null) {
                clone.original = (TableCategory) original.clone();
            }
            
            return clone;
        } catch (CloneNotSupportedException e) {
            Logger.error("table-category-proxy clone", e);
        }
        
        return null;
    }
 
    private void resolveOriginal() {
        if(original == null) {
            original = TableCategoryService.resolveTabeCategoryProxy(this);
        }
    }
}
