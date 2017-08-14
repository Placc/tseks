/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.UUID;

/**
 *
 * @author Placc
 */
@DatabaseTable(tableName = Table.TABLE_NAME)
public class Table {
    public static final String TABLE_NAME = "Table";
    
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_SEATS = "seats";
    public static final String COLUMN_NUMBER = "number";
    
    @DatabaseField(columnName = COLUMN_ID, id = true) private String id;
    @DatabaseField(columnName = COLUMN_CATEGORY, foreign = true, foreignAutoRefresh = true) private TableCategory category;
    @DatabaseField(columnName = COLUMN_SEATS) private int seats;
    @DatabaseField(columnName = COLUMN_NUMBER) private int tableNumber;
    
    /*Database constructor*/
    Table() {
    }
    
    public Table(TableCategory category, int tableNumber, int seats) {
        this.id = UUID.randomUUID().toString();
        
        this.category = category;
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
    
    public TableCategory getTableCategory() {
        return category;
    }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof Table
                && ((Table) o).getSeats() == seats
                && ((Table) o).getTableNumber() == tableNumber;
    }
}
