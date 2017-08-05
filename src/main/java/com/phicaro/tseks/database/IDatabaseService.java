/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.database;

import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.util.exceptions.DatabaseConnectionException;
import com.phicaro.tseks.util.exceptions.PersistenceException;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.List;

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
    Completable saveEvent(Event event);

    Completable updateEvent(Event event);
    
    Completable deleteEvent(Event event);

    //TODO
    //Notification
    Observable<Event> eventAdded();

    Observable<Event> eventRemoved();

    Observable<ConnectionState> connection();

    //Lifecycle
    Completable connect();

    Completable shutdown();
}
