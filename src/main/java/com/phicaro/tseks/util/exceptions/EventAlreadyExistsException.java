/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.util.exceptions;

import com.phicaro.tseks.model.entities.Event;

/**
 *
 * @author Placc
 */
public class EventAlreadyExistsException extends Exception {
    private Event event;
    
    public EventAlreadyExistsException(Event event) {
        this.event = event;
    }
    
    public Event getEvent() {
        return event;
    }
}
