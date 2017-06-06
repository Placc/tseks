/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.models;

import com.phicaro.tseks.entities.Event;
import com.phicaro.tseks.util.Resources;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Placc
 */
public class EventViewModel {
    
    private final SimpleDateFormat formatter = new SimpleDateFormat(Resources.getConfig("CFG_DateFormat"));
    
    private Event event;
    
    //Overview
    private final SimpleStringProperty name;
    private final SimpleStringProperty location;
    private final SimpleStringProperty date;
    
    //Edit
    private final SimpleListProperty<String> description;
    
    public EventViewModel(Event event) {       
        this.event = event;
        
        this.name = new SimpleStringProperty(event.getName());
        this.location = new SimpleStringProperty(event.getLocation().toString());
        this.date = new SimpleStringProperty(formatter.format(event.getDate()));
        
        this.description = new SimpleListProperty<>(FXCollections.observableArrayList(event.getDescription()));
    }

        //Values
    
    public String getName() {
        return name.get();
    }
    
    public void setName(String name) {
        this.name.set(name);
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(location);
    }
    
    public String getDate() {
        return date.get();
    }

    public void setDate(Date date) {
        this.date.set(formatter.format(date));
    }
    
    public List<String> getDescription() {
        return description.get();
    }

    public void setDescription(String[] description) {
        this.description.set(FXCollections.observableArrayList(description));
    }
    
    public Event getEvent() {
        return event;
    }
    
    //Properties
    
    public SimpleStringProperty getNameProperty() {
        return name;
    }

    public SimpleStringProperty getLocationProperty() {
        return location;
    }

    public SimpleStringProperty getDateProperty() {
        return date;
    }

    public SimpleListProperty<String> getDescriptionProperty() {
        return description;
    }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof EventViewModel &&
                ((EventViewModel) o).event.equals(event) &&
                ((EventViewModel) o).date.get().equals(date.get()) &&
                ((EventViewModel) o).name.get().equals(name.get()) &&
                ((EventViewModel) o).location.get().equals(location.get());
    }
}
