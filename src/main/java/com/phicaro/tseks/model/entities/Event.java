/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.entities;

import java.util.*;

/**
 *
 * @author Placc
 */

public class Event implements Cloneable {
    
    private String id;
    private Date date;
    private Location location;
    private List<ITableCategory> tableGroups;
    private String description;
    private String title;
    private String name;

    public Date getDate() {
        return date;
    }

    public Location getLocation() {
        return location;
    }

    public List<ITableCategory> getTableCategories() {
        return tableGroups;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
    
    public String getTitle() {
        return title;
    }
    
    public Event(String id, Date date, String name, String title, Location location) {
        this.id = id;
        this.description = "";
        this.tableGroups = new ArrayList(); 
        setLocation(location);
        setDate(date);
        setName(name);
        setTitle(title);
    }
    
    public void setTitle(String title) {
        this.title = title;
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
    
    public void addTableCategory(ITableCategory table) {
        if(!this.tableGroups.contains(table)) {
            this.tableGroups.add(table);
        }
    }
    
    public void clearTableCategories() {
        this.tableGroups.clear();
    }
    
    public void removeTableCategory(ITableCategory table) {
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
        clone.title = title;
        clone.id = id;
        clone.tableGroups = new ArrayList<>();
        
        for(ITableCategory g : tableGroups) {
            clone.tableGroups.add(g.clone());
        }
        
        return clone;
    }
}
