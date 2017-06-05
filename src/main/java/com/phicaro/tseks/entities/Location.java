/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.entities;

/**
 *
 * @author Placc
 */
public class Location {
    
    String locationDescription;
    
    public Location(String locationDescription) {
        this.locationDescription = locationDescription;
    }
    
    public String getLocationDescription() {
        return locationDescription;
    }    
    
    @Override
    public boolean equals(Object o) {
        return o instanceof Location && ((Location) o).locationDescription.equals(locationDescription);
    }
    
    @Override
    public String toString() {
        return getLocationDescription();
    }
}
