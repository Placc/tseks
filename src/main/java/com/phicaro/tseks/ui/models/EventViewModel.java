/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.models;

import com.phicaro.tseks.entities.Event;
import com.phicaro.tseks.entities.Location;
import com.phicaro.tseks.util.UiHelper;
import java.util.Date;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Placc
 */
public class EventViewModel implements IViewModel<Event> {
    
    private Event event;
    
    //Overview
    private final SimpleStringProperty name;
    private final SimpleStringProperty location;
    private final SimpleStringProperty date;
    private final SimpleListProperty<TableGroupViewModel> tableGroups;
    
    //Edit
    private final SimpleStringProperty description;
    
    public EventViewModel() {
        this.name = new SimpleStringProperty("");
        this.location = new SimpleStringProperty("");
        this.date = new SimpleStringProperty(UiHelper.format(new Date()));
        this.tableGroups = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.description = new SimpleStringProperty("");
    }
    
    public EventViewModel(Event event) {       
        this.event = event;
        
        this.name = new SimpleStringProperty(event.getName());
        this.location = new SimpleStringProperty(event.getLocation().toString());
        this.date = new SimpleStringProperty(UiHelper.format(event.getDate()));
        this.tableGroups = new SimpleListProperty<>(FXCollections.observableArrayList(TableGroupViewModel.fromEvent(event)));   
        this.description = new SimpleStringProperty(event.getDescription());
    }
    
    public ObservableList<TableGroupViewModel> getTableGroups() {
        return tableGroups.get();
    }
    
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
        this.date.set(UiHelper.format(date));
    }
    
    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
    
    public Event getModel() {
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

    public SimpleStringProperty getDescriptionProperty() {
        return description;
    }
    
    public SimpleListProperty<TableGroupViewModel> getTableGroupsProperty() {
        return tableGroups;
    }
    
    @Override
    public boolean matches(Event e) {
        boolean viewModelsInEvent = getTableGroups().stream().allMatch(model -> e.getTableGroups().stream().anyMatch(group -> model.matches(group)));
        boolean groupsInViewModels = e.getTableGroups().stream().allMatch(group -> getTableGroups().stream().anyMatch(model -> model.matches(group)));
        return name.get().equals(e.getName()) &&
                location.get().equals(e.getLocation().toString()) &&
                date.get().equals(UiHelper.format(e.getDate())) &&
                description.get().equals(e.getDescription())
                && viewModelsInEvent && groupsInViewModels;
    }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof EventViewModel &&
                ((EventViewModel) o).date.get().equals(date.get()) &&
                ((EventViewModel) o).name.get().equals(name.get()) &&
                ((EventViewModel) o).location.get().equals(location.get()) &&
                ((EventViewModel) o).description.get().equals(description.get()) &&
                ((EventViewModel) o).tableGroups.get().equals(tableGroups.get())
                &&((event == null && ((EventViewModel) o).event == null) || ((EventViewModel) o).event.equals(event));
    }
}
