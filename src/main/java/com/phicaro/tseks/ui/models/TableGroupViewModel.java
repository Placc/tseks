/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.models;

import com.phicaro.tseks.entities.Event;
import com.phicaro.tseks.entities.TableGroup;
import com.phicaro.tseks.services.TableService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Placc
 */
public class TableGroupViewModel implements IViewModel<TableGroup> {
    
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
    
    public TableGroupViewModel(int startNumber, int numberOfTables, int seats, double price) {
        this.numberOfTables = new SimpleIntegerProperty(numberOfTables);
        this.startNumber = new SimpleIntegerProperty(startNumber); 
        this.endNumber = new SimpleIntegerProperty(startNumber + numberOfTables - 1);
        this.category = new SimpleDoubleProperty(price);
        this.seats = new SimpleIntegerProperty(seats);
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
    
    public TableGroup getModel() {
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
    public boolean matches(TableGroup g) {
        int minNumber = g.getTables().stream().map(table -> table.getTableNumber()).min(Comparator.naturalOrder()).orElse(1);
        int maxNumber = g.getTables().stream().map(table -> table.getTableNumber()).max(Comparator.naturalOrder()).orElse(1);
        
        return getStartNumber() == minNumber &&
                getEndNumber() == maxNumber &&
                getNumberOfTables() == g.getSeatsNumber() &&
                getPrice() == g.getCategory().getPrice();
    }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof TableGroupViewModel &&
                ((TableGroupViewModel) o).startNumber.get() == (startNumber.get()) &&
                ((TableGroupViewModel) o).seats.get() == (seats.get()) &&
                ((TableGroupViewModel) o).endNumber.get() == (endNumber.get()) &&
                ((TableGroupViewModel) o).numberOfTables.get() == (numberOfTables.get()) &&
                ((TableGroupViewModel) o).category.get() == (category.get())
                &&((tableGroup == null && ((TableGroupViewModel) o).tableGroup == null) || ((TableGroupViewModel) o).tableGroup.equals(tableGroup));
    }
}
