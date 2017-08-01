/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.services;

import com.phicaro.tseks.entities.Event;
import com.phicaro.tseks.entities.Location;
import com.phicaro.tseks.util.exceptions.EventAlreadyExistsException;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Placc
 */
public class EventService {

    private IDatabaseService database;
    private Set<Event> events;

    private PublishSubject<Event> eventAdded;
    private PublishSubject<Event> eventRemoved;

    public EventService(IDatabaseService database) {
        this.events = new HashSet<>();
        this.database = database;

        database.eventAdded().subscribe(events::add);
        database.eventRemoved().subscribe(events::remove);
    }

    public Observable<Event> getEvents() {
        return Observable.fromIterable(events)
                .concatWith(
                        database.getSnapshot()
                                .filter(event -> !exists(event))
                                .doOnNext(events::add));
    }

    public Observable<Event> eventAdded() {
        return database.eventAdded();
    }

    public Observable<Event> eventRemoved() {
        return database.eventRemoved();
    }

    public Single<Event> createNewEvent(String name, Date date, Location location, String description) {
        Event event = new Event(date, name, location);

        if (description != null) {
            event.setDescription(description);
        }

        if (events.contains(event)) {
            return Single.error(new EventAlreadyExistsException(event));
        }

        return database.saveEvent(event)
                .toSingleDefault(event);
    }

    public Completable updateEvent(Event event) {
        return database.updateEvent(event);
    }
    
    public Completable deleteEvent(Event event) {
        return database.deleteEvent(event);
    }

    public boolean exists(Event event) {
        return events.contains(event);
    }

}
