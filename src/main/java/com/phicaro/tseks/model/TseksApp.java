/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model;

import com.phicaro.tseks.database.DummyDatabaseService;
import com.phicaro.tseks.model.services.EventService;
import com.phicaro.tseks.database.IDatabaseService;
import com.phicaro.tseks.database.IDatabaseService.ConnectionState;
import com.phicaro.tseks.database.TseksDatabaseFactory;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.prefs.Preferences;

/**
 *
 * @author Placc
 */
public class TseksApp {

    private static final String DB_CONNECTION = "DB_CONNECTION";

    private IDatabaseService database;
    private EventService eventService;
    private Preferences prefs;

    private static TseksApp instance;

    private TseksApp() {
        loadSettings();
    }

    public static Single<TseksApp> startApp() {
        Single<TseksApp> application;
        
        if (instance == null) {
            application = Single.just(new TseksApp())
                    .flatMap(app -> TseksDatabaseFactory.getLocalDatabaseService()
                                    .doOnSuccess(database -> app.database = database)
                                    .map(database -> new EventService(database))
                                    .doOnSuccess(service -> app.eventService = service)
                                    .map(__ -> app));
        } 
        else {
            application = Single.just(instance);
        }

        return application
                .doOnSuccess(app -> instance = app)
                .flatMapCompletable(app -> app.reconnect())
                .toSingleDefault(instance);
    }

    private void loadSettings() {
        //TODO
        prefs = Preferences.userRoot();
    }

    private void saveSettings() {
        //TODO
    }

    public EventService getEventService() {
        return eventService;
    }

    public Completable stopApp() {
        return database.shutdown().doOnComplete(() -> {
            saveSettings();
        });
    }

    public Completable reconnect() {
        return instance.connectionState()
                .firstOrError()
                .flatMapCompletable(state -> {
                    if (state.equals(ConnectionState.CLOSED)) {
                        return database.connect();
                    }
                    return Completable.complete();
                });
    }

    public Observable<IDatabaseService.ConnectionState> connectionState() {
        return database.connection();
    }

}
