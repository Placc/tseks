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
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
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

        this.eventAdded = PublishSubject.create();
        this.eventRemoved = PublishSubject.create();
        
        database.eventAdded().subscribe(this::addEvent);
        database.eventRemoved().subscribe(this::removeEvent);
    }

    private void addEvent(Event event) {
        events.add(event);
        eventAdded.onNext(event);
    }
    
    private void removeEvent(Event event) {
        events.remove(event);
        eventRemoved.onNext(event);
    }
    
    public Observable<Event> getEvents() {
        return Observable.fromIterable(events)
                .concatWith(
                        database.getSnapshot()
                                .doOnNext(event -> {
                                    Optional<Event> existing = events.stream().filter(e -> e.getId().equals(event.getId())).findAny();
                                    if(existing.isPresent() && !existing.get().equals(event)) {
                                        removeEvent(existing.get());
                                        addEvent(event);
                                    } else if (!existing.isPresent()) {
                                        addEvent(event);
                                    }
                                })
                );
    }

    public Observable<Event> eventAdded() {
        return eventAdded;
    }

    public Observable<Event> eventRemoved() {
        return eventRemoved;
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
    
    public Completable copyEvent(Event event) {
        int highestIdx = 1 + events.stream()
                                .map(e -> e.getName().replace(" ", ""))
                                .filter(name -> name.replace(event.getName(), "").matches("\\([0-9]+\\)"))
                                .map(e -> Integer.valueOf(e.replace(event.getName(), "").replaceAll("\\(|\\)", "")))
                                .max(Comparator.naturalOrder())
                                .orElse(0);
        
        return createNewEvent(event.getName() + " (" + highestIdx + ")", event.getDate(), event.getLocation(), event.getDescription())
                .doOnSuccess(copy -> event.getTableGroups().forEach(group -> copy.addTableGroup(group)))
                .flatMapCompletable(this::updateEvent);
    }

    public Completable updateEvent(Event event) {
        System.out.println("Events:" + events.stream().filter(e -> e.equals(event)).count());
        
        if(events.stream().filter(e -> !e.getId().equals(event.getId()) && e.equals(event)).findAny().isPresent()) {
            return resetEventChanges(event)
                    .andThen(Completable.error(new EventAlreadyExistsException(event)));
        }
        
        return database.updateEvent(event)
                .onErrorResumeNext(error -> resetEventChanges(event)
                                                .andThen(Completable.error(error)));
    }
    
    public Completable deleteEvent(Event event) {
        return database.deleteEvent(event);
    }

    private Completable resetEventChanges(Event event) {
        return database.getSnapshot()
                        .filter(e -> e.getId().equals(event.getId()))
                        .firstOrError()
                        .doOnSuccess(e -> {
                            event.getTableGroups().clear();
                            event.getTableGroups().addAll(e.getTableGroups());
                            event.setName(e.getName());
                            event.setDate(e.getDate());
                            event.setLocation(e.getLocation());
                            event.setDescription(e.getDescription());
                        })
                        .toCompletable();
    }
}
