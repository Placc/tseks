/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.phicaro.tseks.model.persister.LocationPersister;
import java.util.*;

/**
 *
 * @author Placc
 */

@DatabaseTable(tableName = Event.TABLE_NAME)
public class Event {
    public static final String TABLE_NAME = "Event";
    
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_NAME = "name";
    
    public static final String FOREIGN_COLLECTION_FIELD = "tableCategories";
    
    @DatabaseField(columnName = COLUMN_ID, id = true) private String id;
    @DatabaseField(columnName = COLUMN_DATE) private Date date;
    @DatabaseField(columnName = COLUMN_LOCATION, persisterClass = LocationPersister.class) private Location location;
    @DatabaseField(columnName = COLUMN_DESCRIPTION) private String description;
    @DatabaseField(columnName = COLUMN_TITLE) private String title;
    @DatabaseField(columnName = COLUMN_NAME) private String name;

    @ForeignCollectionField private Collection<TableCategory> tableCategories;
    
    /*Database constructor*/
    Event() {
    }
    
    public Event(Date date, String name, String title, Location location) {
        this.id = UUID.randomUUID().toString();
        
        this.description = "";
        this.tableCategories = new ArrayList<>();
        
        setLocation(location);
        setDate(date);
        setName(name);
        setTitle(title);
    }
    
    public Date getDate() {
        return date;
    }

    public Location getLocation() {
        return location;
    }

    public List<TableCategory> getTableCategories() {
        return new ArrayList<>(tableCategories);
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
    
    public void addTableCategory(TableCategory table) {
        if(!this.tableCategories.contains(table)) {
            this.tableCategories.add(table);
        }
    }
    
    public void clearTableCategories() {
        this.tableCategories.clear();
    }
    
    public void removeTableCategory(TableCategory table) {
        this.tableCategories.remove(table);
    }
    
    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Event && ((Event) o).date.getTime() / 1000l == date.getTime() / 1000l && ((Event) o).name.equals(name) && ((Event) o).location.equals(location);
    }

    public String getId() {
        return id;
    }
}
