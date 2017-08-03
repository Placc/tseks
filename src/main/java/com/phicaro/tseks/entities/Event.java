/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.entities;

import com.phicaro.tseks.util.Resources;
import java.util.*;

/**
 *
 * @author Placc
 */
public class Event implements Cloneable {
    
    private static final int DESCRIPTION_LINES = 2;
    
    private String id;
    private Date date;
    private Location location;
    private List<TableGroup> tableGroups;
    private String description;
    private String name;
    
    public Event() {
        id = UUID.randomUUID().toString();
    }

    public Date getDate() {
        return date;
    }

    public Location getLocation() {
        return location;
    }

    public List<TableGroup> getTableGroups() {
        return tableGroups;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
    
    public Event(Date date, String name, Location location) {
        this();
        this.description = "";
        this.tableGroups = new ArrayList(); 
        setLocation(location);
        setDate(date);
        setName(name);
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
  
    public void setDate(Date date) {
        this.date = date;
    }
    
    public void addTableGroup(TableGroup table) {
        if(!this.tableGroups.contains(table)) {
            this.tableGroups.add(table);
        }
    }
    
    public void clearTableGroups() {
        this.tableGroups.clear();
    }
    
    public void removeTableGroup(TableGroup table) {
        this.tableGroups.remove(table);
    }
    
    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Event && ((Event) o).date.getTime() / 1000l == date.getTime() / 1000l && ((Event) o).name.equals(name) && ((Event) o).location.equals(location);
    }

    public String getId() {
        return id;
    }
    
    @Deprecated
    @Override
    public Event clone() throws CloneNotSupportedException {
        Event clone = (Event) super.clone();
        clone.date = date;
        clone.description = description;
        clone.location = location;
        clone.name = name;
        clone.id = id;
        
        for(TableGroup g : tableGroups) {
            clone.tableGroups.add(g.clone());
        }
        
        return clone;
    }
}
