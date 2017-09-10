/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model;

import com.phicaro.tseks.database.EventAlreadyExistsException;
import com.phicaro.tseks.database.IDatabaseService;
import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.model.entities.Location;
import com.phicaro.tseks.model.entities.TableCategory;
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

    private void removeEvent(String id) {
        Optional<Event> toRemove = events.stream().filter(e -> e.getId().equals(id)).findAny();

        if (toRemove.isPresent()) {
            events.remove(toRemove.get());
            eventRemoved.onNext(toRemove.get());
        }
    }

    public Observable<Event> getEvents() {
        return Observable.fromIterable(events)
                .concatWith(
                        database.getSnapshot()
                                .doOnNext(event -> {
                                    Optional<Event> existing = events.stream().filter(e -> e.getId().equals(event.getId())).findAny();
                                    if (existing.isPresent() && !existing.get().equals(event)) {
                                        removeEvent(existing.get().getId());
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

    public Single<Event> createNewEvent(String name, String title, Date date, Location location, String description) {
        Event event = new Event(date, name, title, location);

        if (description != null) {
            event.setDescription(description);
        }

        if (events.contains(event)) {
            return Single.error(new EventAlreadyExistsException(event));
        }

        return database.createEvent(event)
                .toSingleDefault(event);
    }

    public Completable copyEvent(Event event) {
        int highestIdx = 1 + events.stream()
                .map(e -> e.getName().replace(" ", ""))
                .filter(name -> name.replace(event.getName(), "").matches("\\([0-9]+\\)$"))
                .map(e -> Integer.valueOf(e.replace(event.getName(), "").replaceAll("\\(|\\)", "")))
                .max(Comparator.naturalOrder())
                .orElse(0);

        Event copy = new Event(event.getDate(), event.getName() + " (" + highestIdx + ")", event.getTitle(), event.getLocation());
        copy.setDescription(event.getDescription());

        event.getTableCategories().forEach(group -> copy.addTableCategory(new TableCategory(group.getEvent(), group.getSeatsNumber(), group.getPrice(), group.getMinTableNumber(), group.getMaxTableNumber())));

        return database.createEvent(copy);
    }

    public Completable updateEvent(Event event) {
        if (events.stream().filter(e -> !e.getId().equals(event.getId()) && e.equals(event)).findAny().isPresent()) {
            return Completable.error(new EventAlreadyExistsException(event));
        }

        return database.updateEvent(event);
    }

    public Completable deleteEvent(Event event) {
        return database.deleteEvent(event);
    }
}
