/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.database;

import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.util.Resources;

/**
 *
 * @author Placc
 */
public class EventAlreadyExistsException extends Exception {
    private Event event;
    
    public EventAlreadyExistsException(Event event) {
        super(Resources.getString("MSG_EventAlreadyExists"));
        this.event = event;
    }
    
    public Event getEvent() {
        return event;
    }
}
