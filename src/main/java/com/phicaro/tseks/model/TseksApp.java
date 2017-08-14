/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model;

import com.phicaro.tseks.model.database.DatabaseType;
import com.phicaro.tseks.model.services.EventService;
import com.phicaro.tseks.model.database.IDatabaseService;
import com.phicaro.tseks.model.database.IDatabaseService.ConnectionState;
import com.phicaro.tseks.model.database.TseksDatabaseFactory;
import com.phicaro.tseks.settings.DatabaseSettings;
import com.phicaro.tseks.settings.PrintSettings;
import com.phicaro.tseks.settings.SettingsService;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.io.IOException;

/**
 *
 * @author Placc
 */
public class TseksApp {

    private IDatabaseService database;
    private EventService eventService;
    private SettingsService settingsService;

    private static TseksApp instance;

    private TseksApp() {
        settingsService = new SettingsService();
    }

    public static Single<TseksApp> startApp() {
        Single<TseksApp> application;
        
        if (instance == null) {
            application = Single.just(new TseksApp())
                    .doOnSuccess(app -> app.settingsService.loadSettings())
                    .flatMap(app -> TseksDatabaseFactory.getDatabase(app.settingsService.getDatabaseSettings().getDatabaseType())
                                    .initializeDatabase()
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
                .flatMap(app -> app.reconnect()
                                    .toSingleDefault(app));
    }

    public SettingsService getSettingsService() {
        return settingsService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public Completable stopApp() {
        return database.shutdown();
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
