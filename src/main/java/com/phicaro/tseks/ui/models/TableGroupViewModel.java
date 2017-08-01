/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.models;

import com.phicaro.tseks.entities.Event;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Placc
 */
public class TableGroupViewModel {
    
    private IntegerProperty numberOfTables;
    private DoubleProperty category;
    private IntegerProperty seats;
    
    public static List<TableGroupViewModel> fromEvent(Event event) {
        List<TableGroupViewModel> result = new ArrayList<>();
        
        event.getTableGroups().forEach(group -> 
                result.add(new TableGroupViewModel(group.getCategory().getPrice(), group.getSeatsNumber())));
        
        return result;
    }
    
    public TableGroupViewModel(double categoryIdentifier, int seatsIdentifier) {
        this.numberOfTables = new SimpleIntegerProperty(0);
        this.category = new SimpleDoubleProperty(categoryIdentifier);
        this.seats = new SimpleIntegerProperty(seatsIdentifier);
    }
    
    public void setTableNumber(int number) {
        numberOfTables.set(Math.max(0, number));
    }
    
    public void increaseTableNumber() {
        numberOfTables.set(numberOfTables.get() + 1);
    }
    
    public void decreaseTableNumber() {
        numberOfTables.set(Math.max(1, numberOfTables.get() - 1));
    }
    
    public void setPrice(double price) {
        category.set(Math.max(0., price));
    }
    
    public void setSeats(int seats) {
        this.seats.set(Math.max(1, seats));
    }
    
    public double getPrice() {
        return category.get();
    }
    
    public int getSeats() {
        return seats.get();
    }
    
    public int getNumberOfTables() {
        return numberOfTables.get();
    }
    
    public DoubleProperty getPriceProperty() {
        return category;
    }
    
    public IntegerProperty getSeatsProperty() {
        return seats;
    }
    
    public IntegerProperty getNumberOfTablesProperty() {
        return numberOfTables;
    }
}
