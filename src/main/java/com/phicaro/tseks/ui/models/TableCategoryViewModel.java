/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.models;

import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.model.entities.ITableCategory;
import com.phicaro.tseks.model.entities.TableCategory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Placc
 */
public class TableCategoryViewModel implements IViewModel<ITableCategory> {
    
    private SimpleIntegerProperty numberOfTables;
    private SimpleIntegerProperty startNumber;
    private SimpleIntegerProperty endNumber;
    private SimpleDoubleProperty category;
    private SimpleIntegerProperty seats;
    
    private ITableCategory tableGroup;
    
    public static List<TableCategoryViewModel> fromEvent(Event event) {
        List<TableCategoryViewModel> result = new ArrayList<>();
        
        event.getTableCategories().forEach(group -> result.add(new TableCategoryViewModel(group)));
        
        return result;
    }
    
    public TableCategoryViewModel(int startNumber, int numberOfTables, int seats, double price) {
        this.numberOfTables = new SimpleIntegerProperty(numberOfTables);
        this.startNumber = new SimpleIntegerProperty(startNumber); 
        this.endNumber = new SimpleIntegerProperty(startNumber + numberOfTables - 1);
        this.category = new SimpleDoubleProperty(price);
        this.seats = new SimpleIntegerProperty(seats);
    }
    
    public TableCategoryViewModel(ITableCategory tableGroup) {
        this.tableGroup = tableGroup;
        
        this.startNumber = new SimpleIntegerProperty(computeStartNumber(tableGroup));
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

    private int computeStartNumber(ITableCategory g) {
        if(g instanceof TableCategory) {
            return g.getTables().stream().map(table -> table.getTableNumber()).min(Comparator.naturalOrder()).orElse(1);
        }
        return 1;
    }
    
    private int computeEndNumber(ITableCategory g) {
        if(g instanceof TableCategory) {
            return g.getTables().stream().map(table -> table.getTableNumber()).max(Comparator.naturalOrder()).orElse(1);
        }
        return 1;
    }
    
    @Override
    public boolean matches(ITableCategory g) {
        return getStartNumber() == computeStartNumber(g) &&
                getEndNumber() == computeEndNumber(g) &&
                getNumberOfTables() == g.getNumberOfTables() &&
                getSeats() == g.getSeatsNumber() &&
                getPrice() == g.getPrice().getPrice();
    }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof TableCategoryViewModel &&
                ((TableCategoryViewModel) o).startNumber.get() == (startNumber.get()) &&
                ((TableCategoryViewModel) o).seats.get() == (seats.get()) &&
                ((TableCategoryViewModel) o).endNumber.get() == (endNumber.get()) &&
                ((TableCategoryViewModel) o).numberOfTables.get() == (numberOfTables.get()) &&
                ((TableCategoryViewModel) o).category.get() == (category.get())
                &&((tableGroup == null && ((TableCategoryViewModel) o).tableGroup == null) || 
                (tableGroup != null && ((TableCategoryViewModel) o).tableGroup != null &&
                ((TableCategoryViewModel) o).tableGroup.equals(tableGroup)));
    }
}
