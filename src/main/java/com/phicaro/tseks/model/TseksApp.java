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

        /*TseksDatabaseFactory.getLocalDatabaseService()
                .subscribe(db -> database = db,
                        e -> Logger.error("tseks-app c",e));*/
        
        this.database = new DummyDatabaseService(DB_CONNECTION);
        
        eventService = new EventService(database);
    }

    public static Single<TseksApp> startApp() {
        if (instance == null) {
            instance = new TseksApp();
        }

        return instance.reconnect()
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
