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
import java.util.Date;

/**
 *
 * @author Placc
 */
public class EventService {

    private IDatabaseService database;

    public EventService(IDatabaseService database) {
        this.database = database;
    }

    public Observable<Event> getEvents() {
        return database.getSnapshot();
    }

    public Single<Event> createNewEvent(String name, String title, Date date, Location location, String description) {
        Event event = new Event(date, name, title, location);

        if (description != null) {
            event.setDescription(description);
        }

        return database.createEvent(event)
                .toSingleDefault(event);
    }

    public Single<Event> copyEvent(Event event) {
        int highestIdx = 1 + database.getSnapshot()
                .map(e -> e.getName().replace(" ", ""))
                .filter(name -> name.replace(event.getName(), "").matches("\\([0-9]+\\)$"))
                .map(e -> Integer.valueOf(e.replace(event.getName(), "").replaceAll("\\(|\\)", "")))
                .sorted()
                .lastElement()
                .blockingGet(0);

        Event copy = new Event(event.getDate(), event.getName() + " (" + highestIdx + ")", event.getTitle(), event.getLocation());
        copy.setDescription(event.getDescription());

        event.getTableCategories().forEach(group -> copy.addTableCategory(new TableCategory(copy, group.getSeatsNumber(), group.getPrice(), group.getMinTableNumber(), group.getMaxTableNumber())));

        return database.createEvent(copy)
                .toSingleDefault(copy);
    }

    public Completable updateEvent(Event event) {
        if (!database.getSnapshot()
                .filter(e -> !e.getId().equals(event.getId()) && e.equals(event))
                .firstElement()
                .isEmpty()
                .blockingGet()) {
            return Completable.error(new EventAlreadyExistsException(event));
        }

        return database.updateEvent(event);
    }

    public Completable deleteEvent(Event event) {
        return database.deleteEvent(event);
    }
}
