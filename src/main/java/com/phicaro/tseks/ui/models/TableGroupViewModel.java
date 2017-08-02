/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.models;

import com.phicaro.tseks.entities.Event;
import com.phicaro.tseks.entities.TableGroup;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Placc
 */
public class TableGroupViewModel {
    
    private SimpleIntegerProperty numberOfTables;
    private SimpleIntegerProperty startNumber;
    private SimpleIntegerProperty endNumber;
    private SimpleDoubleProperty category;
    private SimpleIntegerProperty seats;
    
    private TableGroup tableGroup;
    
    public static List<TableGroupViewModel> fromEvent(Event event) {
        List<TableGroupViewModel> result = new ArrayList<>();
        
        event.getTableGroups().forEach(group -> result.add(new TableGroupViewModel(group)));
        
        return result;
    }
    
    public TableGroupViewModel() {
        this.numberOfTables = new SimpleIntegerProperty(0);
        this.startNumber = new SimpleIntegerProperty(0); //TODO
        this.endNumber = new SimpleIntegerProperty(getStartNumber());
        this.category = new SimpleDoubleProperty(0);
        this.seats = new SimpleIntegerProperty(1);
    }
    
    public TableGroupViewModel(TableGroup tableGroup) {
        this.tableGroup = tableGroup;
        
        this.startNumber = new SimpleIntegerProperty(tableGroup.getTables()
                                                        .stream()
                                                        .map(table -> table.getTableNumber())
                                                        .min(Comparator.naturalOrder())
                                                        .orElse(1)
                            );
        this.numberOfTables = new SimpleIntegerProperty(tableGroup.getTables().size());
        this.endNumber = new SimpleIntegerProperty(Math.max(getStartNumber(), getStartNumber() + getNumberOfTables() - 1));
        this.category = new SimpleDoubleProperty(tableGroup.getCategory().getPrice());
        this.seats = new SimpleIntegerProperty(tableGroup.getSeatsNumber());
    }
    
    public TableGroup getTableGroup() {
        return tableGroup;
    }
    
    public void setTableNumber(int number) {
        numberOfTables.set(Math.max(0, number));
    }
    
    public void setStartNumber(int startNumber) {
        this.startNumber.set(startNumber); //TODO
    }
    
    public void setEndNumber(int endNumber) {
        this.endNumber.set(Math.max(getStartNumber(), endNumber));
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
    
    public int getStartNumber() {
        return startNumber.get();
    }
    
    public int getEndNumber() {
        return endNumber.get();
    }
    
    public SimpleIntegerProperty getStartNumberProperty() {
        return startNumber;
    }
    
    public SimpleIntegerProperty getEndNumberProperty() {
        return endNumber;
    }
    
    public SimpleDoubleProperty getPriceProperty() {
        return category;
    }
    
    public SimpleIntegerProperty getSeatsProperty() {
        return seats;
    }
    
    public SimpleIntegerProperty getNumberOfTablesProperty() {
        return numberOfTables;
    }
}
