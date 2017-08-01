/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.services;

import com.phicaro.tseks.entities.Event;
import com.phicaro.tseks.entities.Location;
import com.phicaro.tseks.util.exceptions.BadArgumentException;
import com.phicaro.tseks.util.exceptions.EventAlreadyExistsException;
import com.phicaro.tseks.util.exceptions.PersistenceException;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Placc
 */
public class DummyDatabaseService implements IDatabaseService {

    private List<Event> dummyEvents;

    private PublishSubject<Event> eventAdded;
    private PublishSubject<Event> eventRemoved;

    private BehaviorSubject<ConnectionState> connection;

    public DummyDatabaseService(String conn) {
        dummyEvents = new ArrayList<Event>();

        eventAdded = PublishSubject.create();
        eventRemoved = PublishSubject.create();

        connection = BehaviorSubject.createDefault(ConnectionState.CLOSED);

        long date = new Date().getTime() + 100000;

        dummyEvents.add(new Event(new Date(date), "Event1", new Location("here and there")));
        dummyEvents.add(new Event(new Date(date + 100000), "Event2", new Location("far away")));
        dummyEvents.add(new Event(new Date(date + 1000000), "Event3", new Location("even further")));
    }

    @Override
    public Completable connect() {
        return Completable.create(s -> {
            connection.onNext(ConnectionState.CONNECTING);
            connection.onNext(ConnectionState.CONNECTED);
            s.onComplete();
        }).delay(5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public Observable<Event> getSnapshot() {
        return Observable.fromIterable(dummyEvents);
    }

    @Override
    public Completable saveEvent(Event event) {
        return Completable.create(s -> {
            if (dummyEvents.contains(event)) {
                s.onError(new PersistenceException(new EventAlreadyExistsException(event)));
            }

            if (dummyEvents.add(event)) {
                eventAdded.onNext(event);
                s.onComplete();
            } else {
                s.onError(new PersistenceException());
            }
        });
    }
    
    @Override
    public Completable updateEvent(Event event) {
        return Completable.create(s -> {            
            Optional<Event> toRemove = dummyEvents.stream().filter(e -> e.getId().equals(event.getId())).findAny();
            
            if(!toRemove.isPresent()) {
                s.onError(new PersistenceException(new BadArgumentException()));
            }
            
            dummyEvents.set(dummyEvents.indexOf(toRemove.get()), event);
            
            eventRemoved.onNext(toRemove.get());
            eventAdded.onNext(event);
            
            s.onComplete();
        });
    }

    @Override
    public Completable deleteEvent(Event event) {
        return Completable.create(s -> {
            if (dummyEvents.remove(event)) {
                eventRemoved.onNext(event);
                s.onComplete();
            } else {
                s.onError(new PersistenceException());
            }
        });
    }

    @Override
    public Single<Boolean> containsEvent(Event event) {
        return Single.just(dummyEvents.contains(event));
    }

    @Override
    public Observable<Event> eventAdded() {
        return eventAdded;
    }

    @Override
    public Observable<Event> eventRemoved() {
        return eventRemoved;
    }

    @Override
    public Completable shutdown() {
        return Completable.create(s -> {
            connection.onNext(ConnectionState.CLOSING);
            connection.onNext(ConnectionState.CLOSED);
            s.onComplete();
        });
    }

    public Observable<ConnectionState> connection() {
        return connection;
    }
}
