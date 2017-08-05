/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.models;

import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.model.entities.ITableCategory;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Placc
 */
public class TableGroupViewModel implements IViewModel<ITableCategory> {
    
    private SimpleIntegerProperty numberOfTables;
    private SimpleIntegerProperty startNumber;
    private SimpleIntegerProperty endNumber;
    private SimpleDoubleProperty category;
    private SimpleIntegerProperty seats;
    
    private ITableCategory tableGroup;
    
    public static List<TableGroupViewModel> fromEvent(Event event) {
        List<TableGroupViewModel> result = new ArrayList<>();
        
        event.getTableGroups().forEach(group -> result.add(new TableGroupViewModel(group)));
        
        return result;
    }
    
    public TableGroupViewModel(int startNumber, int numberOfTables, int seats, double price) {
        this.numberOfTables = new SimpleIntegerProperty(numberOfTables);
        this.startNumber = new SimpleIntegerProperty(startNumber); 
        this.endNumber = new SimpleIntegerProperty(startNumber + numberOfTables - 1);
        this.category = new SimpleDoubleProperty(price);
        this.seats = new SimpleIntegerProperty(seats);
    }
    
    public TableGroupViewModel(ITableCategory tableGroup) {
        this.tableGroup = tableGroup;
        
        this.startNumber = new SimpleIntegerProperty(tableGroup.getMinTableNumber());
        this.numberOfTables = new SimpleIntegerProperty(tableGroup.getNumberOfTables());
        this.endNumber = new SimpleIntegerProperty(Math.max(getStartNumber(), getStartNumber() + getNumberOfTables() - 1));
        this.category = new SimpleDoubleProperty(tableGroup.getPrice().getPrice());
        this.seats = new SimpleIntegerProperty(tableGroup.getSeatsNumber());
    }
    
    public ITableCategory getModel() {
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
    
    @Override
    public boolean matches(ITableCategory g) {
        return getStartNumber() == g.getMinTableNumber() &&
                getEndNumber() == g.getMaxTableNumber() &&
                getNumberOfTables() == g.getNumberOfTables() &&
                getSeats() == g.getSeatsNumber() &&
                getPrice() == g.getPrice().getPrice();
    }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof TableGroupViewModel &&
                ((TableGroupViewModel) o).startNumber.get() == (startNumber.get()) &&
                ((TableGroupViewModel) o).seats.get() == (seats.get()) &&
                ((TableGroupViewModel) o).endNumber.get() == (endNumber.get()) &&
                ((TableGroupViewModel) o).numberOfTables.get() == (numberOfTables.get()) &&
                ((TableGroupViewModel) o).category.get() == (category.get())
                &&((tableGroup == null && ((TableGroupViewModel) o).tableGroup == null) || 
                (tableGroup != null && ((TableGroupViewModel) o).tableGroup != null &&
                ((TableGroupViewModel) o).tableGroup.equals(tableGroup)));
    }
}
