/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.phicaro.tseks.model.persister.PricePersister;
import com.phicaro.tseks.util.exceptions.BadArgumentException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Placc
 */
@DatabaseTable(tableName = TableCategory.TABLE_NAME)
public class TableCategory {
    public static final String TABLE_NAME = "TableCategory";
    
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EVENT = "event";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_SEATS = "seats";
    
    @DatabaseField(columnName = COLUMN_ID, id = true) private String id;
    @DatabaseField(columnName = COLUMN_EVENT, foreign = true, foreignAutoRefresh = true) private Event event;
    @DatabaseField(columnName = COLUMN_PRICE, persisterClass = PricePersister.class) private PriceCategory priceCategory;
    @DatabaseField(columnName = COLUMN_SEATS) private int seatsNumber;
    @ForeignCollectionField(eager = true) private Collection<Table> tables;
    
    /*Database constructor*/
    TableCategory() {
    }
    
    public TableCategory(Event event, int seats, PriceCategory priceCategory) {
        this.id = UUID.randomUUID().toString();
        
        this.tables = new ArrayList<>();
        
        this.event = event;
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
        return o instanceof TableCategory 
                && ((TableCategory) o).getEvent().equals(event)
                && ((TableCategory) o).getPrice().equals(priceCategory) 
                && ((TableCategory) o).getSeatsNumber() == seatsNumber 
                && ((TableCategory) o).getNumberOfTables() == tables.size()
                && ((TableCategory) o).getTables().equals(tables);
    }
    
    public int getNumberOfTables() {
        return tables.size();
    }
    
    public Event getEvent() {
        return event;
    }
}