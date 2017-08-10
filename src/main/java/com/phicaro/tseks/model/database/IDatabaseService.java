/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.database;

import com.phicaro.tseks.model.entities.Event;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 *
 * @author Placc
 */
public interface IDatabaseService {

    public enum ConnectionState {
        CONNECTED, CLOSED, CONNECTING, CLOSING
    }

    //State
    Single<Boolean> containsEvent(Event event);
    
    Observable<Event> getSnapshot();

    //Manipulation
    Completable createEvent(Event event);

    Completable updateEvent(Event event);
    
    Completable deleteEvent(Event event);

    //TODO
    //Notification
    Observable<Event> eventAdded();

    Observable<String> eventRemoved();

    Observable<ConnectionState> connection();

    //Lifecycle
    Completable connect();

    Completable shutdown();
}
