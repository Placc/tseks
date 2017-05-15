/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.entities;

import com.phicaro.tseks.util.Resources;
import java.util.*;

/**
 *
 * @author Placc
 */
public class Event {
    
    private static final int DESCRIPTION_LINES = 3;
    
    private Date date;
    private List<Table> tables;
    private String[] description;
    private String name;
    
    public Event() {
        
    }
    
    public Event(Date date, String name) {
        this.description = new String[DESCRIPTION_LINES];
        this.tables = new ArrayList(); 
        setDate(date);
        setName(name);
    }
    
    public Event(Date date, String name, String[] description) {
        this(date, name);
        setDescription(description);
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String[] description) throws ArrayIndexOutOfBoundsException {
        if(description.length > this.description.length) {
            throw new ArrayIndexOutOfBoundsException(Resources.getString("MSG_OnlyThreeDescriptionLinesAllowed"));
        }
        System.arraycopy(description, 0, this.description, 0, description.length);
    }
  
    public void setDate(Date date) {
        this.date = date;
    }
    
    public void addTable(Table table) {
        if(!this.tables.contains(table)) {
            this.tables.add(table);
        }
    }
    
    public void removeTable(Table table) {
        this.tables.remove(table);
    }
    
    public Optional<Table> findTable(int seats, PriceCategory category) {
        return this.tables.stream().filter(table -> table.getSeats() == seats && table.getCategory().equals(category)).findAny();
    }
    
    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Event && ((Event) o).date.equals(date) && ((Event) o).name.equals(name);
    }
}
