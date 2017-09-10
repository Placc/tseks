/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.phicaro.tseks.model.persister.PricePersister;
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
    public static final String COLUMN_MINNUM = "min_num";
    public static final String COLUMN_MAXNUM = "max_num";

    @DatabaseField(columnName = COLUMN_ID, id = true)
    private String id;
    @DatabaseField(columnName = COLUMN_EVENT, foreign = true, foreignAutoRefresh = true)
    private Event event;
    @DatabaseField(columnName = COLUMN_PRICE, persisterClass = PricePersister.class)
    private PriceCategory priceCategory;
    @DatabaseField(columnName = COLUMN_SEATS)
    private int seatsNumber;
    @DatabaseField(columnName = COLUMN_MINNUM)
    private int minTableNumber;
    @DatabaseField(columnName = COLUMN_MAXNUM)
    private int maxTableNumber;

    /*Database constructor*/
    TableCategory() {
    }

    public TableCategory(Event event, int seats, PriceCategory priceCategory, int minTableNumber, int maxTableNumber) {
        this.id = UUID.randomUUID().toString();

        this.event = event;
        this.priceCategory = priceCategory;
        this.seatsNumber = seats;
        this.minTableNumber = minTableNumber;
        this.maxTableNumber = maxTableNumber;
    }

    public void setMinTableNumber(int minTableNumber) {
        if (minTableNumber >= 1) {
            this.minTableNumber = Math.min(minTableNumber, maxTableNumber);
        } else {
            this.minTableNumber = 1;
        }
    }

    public int getMinTableNumber() {
        return minTableNumber;
    }

    public int getMaxTableNumber() {
        return maxTableNumber;
    }

    public void setMaxTableNumber(int maxTableNumber) {
        this.maxTableNumber = Math.max(maxTableNumber, minTableNumber);
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

    public void setSeatsNumber(int seatsNumber) {
        this.seatsNumber = seatsNumber;
    }

    public void setPrice(PriceCategory priceCategory) {
        this.priceCategory = priceCategory;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TableCategory
                && ((TableCategory) o).getPrice().equals(priceCategory)
                && ((TableCategory) o).getSeatsNumber() == seatsNumber
                && ((TableCategory) o).getNumberOfTables() == getNumberOfTables()
                && ((TableCategory) o).getMinTableNumber() == minTableNumber
                && ((TableCategory) o).getMaxTableNumber() == maxTableNumber;
    }

    public int getNumberOfTables() {
        return maxTableNumber - minTableNumber + 1;
    }

    public Event getEvent() {
        return event;
    }
}
