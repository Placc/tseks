/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.database.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.phicaro.tseks.database.EventAlreadyExistsException;
import com.phicaro.tseks.database.IDatabaseService;
import com.phicaro.tseks.database.exception.PersistenceException;
import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.model.entities.TableCategory;
import com.phicaro.tseks.util.Logger;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Placc
 */
public class JdbcDatabaseService implements IDatabaseService {

    private Dao<Event, String> eventDao;
    private Dao<TableCategory, String> categoryDao;

    private final String connectionString;
    private ConnectionSource connectionSource;

    private final BehaviorSubject<ConnectionState> connectionState;

    private static final Object LOCK = new Object();

    public JdbcDatabaseService(String connectionString) {
        this.connectionString = connectionString;

        connectionState = BehaviorSubject.createDefault(ConnectionState.CLOSED);
    }

    @Override
    public Single<Boolean> containsEvent(Event event) {
        return Single.create(s
                -> {
            synchronized (LOCK) {
                s.onSuccess(
                        eventDao.queryBuilder()
                                .where()
                                .eq(Event.COLUMN_NAME, event.getName()).and()
                                .eq(Event.COLUMN_DATE, event.getDate()).and()
                                .eq(Event.COLUMN_LOCATION, event.getLocation())
                                .queryForFirst() != null
                );
            }
        }
        );
    }

    @Override
    public Observable<Event> getSnapshot() {
        return Observable.create(s -> {
            synchronized (LOCK) {
                eventDao.queryForAll().forEach(s::onNext);
                s.onComplete();
            }
        });
    }

    @Override
    public Completable createEvent(Event event) {
        return Completable.create(s -> {
            synchronized (LOCK) {
                Logger.info("jdbc-database-service create-event " + event.toString());

                if (eventDao.queryForId(event.getId()) != null || this.containsEvent(event).blockingGet()) {
                    s.onError(new PersistenceException(new EventAlreadyExistsException(event)));
                }

                createOrUpdateTablesAndCategories(event);

                if (eventDao.createOrUpdate(event).isCreated()) {
                    s.onComplete();
                } else {
                    s.onError(new PersistenceException("Could not create event"));
                }
            }
        });
    }

    private void createOrUpdateTablesAndCategories(Event event) throws SQLException {
        synchronized (LOCK) {
            deleteTablesAndCategories(event, true);

            for (TableCategory category : event.getTableCategories()) {
                Logger.info("jdbc-database-service cu-tac category " + category.toString());

                if (categoryDao.createOrUpdate(category).getNumLinesChanged() != 1) {
                    Logger.error("local-database-service cu-tac category table invariant");
                }
            }
        }
    }

    @Override
    public Completable updateEvent(Event event) {
        return Completable.create(s -> {
            synchronized (LOCK) {
                Logger.info("jdbc-database-service update-event " + event.toString());

                boolean duplicate = eventDao.queryBuilder()
                        .where()
                        .ne(Event.COLUMN_ID, event.getId()).and()
                        .eq(Event.COLUMN_NAME, event.getName()).and()
                        .eq(Event.COLUMN_DATE, event.getDate()).and()
                        .eq(Event.COLUMN_LOCATION, event.getLocation())
                        .queryForFirst() != null;

                if (duplicate) {
                    s.onError(new PersistenceException(new EventAlreadyExistsException(event)));
                    return;
                }

                Event old = eventDao.queryForSameId(event);

                if (old == null) {
                    s.onError(new PersistenceException("Event does not exist!"));
                    return;
                }

                createOrUpdateTablesAndCategories(event);

                if (eventDao.createOrUpdate(event).isUpdated()) {
                    s.onComplete();
                } else {
                    s.onError(new PersistenceException("Could not update event"));
                }
            }
        });
    }

    private void deleteTablesAndCategories(Event event, boolean retainExisting) throws SQLException {
        synchronized (LOCK) {
            List<TableCategory> existingCategories = categoryDao.queryBuilder().where()
                    .eq(TableCategory.COLUMN_EVENT, event)
                    .query();

            for (TableCategory category : existingCategories) {
                if (!retainExisting || !event.getTableCategories().contains(category)) {
                    Logger.info("jdbc-database-service delete-category " + category.toString());

                    if (categoryDao.delete(category) != 1) {
                        Logger.error("local-database-service cu-tac nonexistent category invariant");
                    }
                }
            }
        }
    }

    @Override
    public Completable deleteEvent(Event event) {
        return Completable.create(s -> {
            synchronized (LOCK) {
                Logger.info("jdbc-database-service delete-event " + event.toString());

                if (!this.containsEvent(event).blockingGet()) {
                    s.onError(new PersistenceException("Event does not exist"));
                    return;
                }

                deleteTablesAndCategories(event, false);

                Event old = eventDao.queryForSameId(event);

                if (eventDao.deleteById(event.getId()) == 1) {
                    s.onComplete();
                } else {
                    s.onError(new PersistenceException("Deletion did not affect one row"));
                }
            }
        });
    }

    @Override
    public Observable<ConnectionState> connection() {
        return connectionState;
    }

    @Override
    public Completable connect() {
        if (connectionSource != null && connectionSource.isOpen(Event.TABLE_NAME)) {
            return Completable.complete();
        }

        return Completable.create(s -> {
            connectionState.onNext(ConnectionState.CONNECTING);

            connectionSource = new JdbcConnectionSource(connectionString);

            eventDao = DaoManager.createDao(connectionSource, Event.class);
            categoryDao = DaoManager.createDao(connectionSource, TableCategory.class);

            connectionState.onNext(ConnectionState.CONNECTED);

            s.onComplete();
        })
                .doOnError(__ -> connectionState.onNext(ConnectionState.CLOSED));
    }

    @Override
    public Completable shutdown() {
        if (connectionSource == null || connectionState.blockingSingle() != ConnectionState.CONNECTED) {
            return Completable.complete();
        }

        return Completable.create(s -> {
            connectionState.onNext(ConnectionState.CLOSING);

            connectionSource.closeQuietly();

            connectionState.onNext(ConnectionState.CLOSED);

            s.onComplete();
        }).doOnError(__ -> connectionState.onNext(ConnectionState.CLOSED));
    }
}
