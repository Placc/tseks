/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.database.local;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.phicaro.tseks.model.database.IDatabaseService;
import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.model.entities.Table;
import com.phicaro.tseks.model.entities.TableCategory;
import com.phicaro.tseks.util.Logger;
import com.phicaro.tseks.util.exceptions.EventAlreadyExistsException;
import com.phicaro.tseks.util.exceptions.PersistenceException;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Placc
 */
public class LocalDatabaseService implements IDatabaseService {
    
    private Dao<Event, String> eventDao;
    private Dao<TableCategory, String> categoryDao;
    private Dao<Table, String> tableDao;
    
    private final String connectionString;
    private ConnectionSource connectionSource;
    
    private final BehaviorSubject<ConnectionState> connectionState;
    private final PublishSubject<Event> eventAdded;
    private final PublishSubject<String> eventRemoved;
    
    public LocalDatabaseService(String connectionString) {
        this.connectionString = connectionString;
        
        eventAdded = PublishSubject.create();
        eventRemoved = PublishSubject.create();

        connectionState = BehaviorSubject.createDefault(ConnectionState.CLOSED);
    }

    @Override
    public Single<Boolean> containsEvent(Event event) {
        return Single.create(s -> 
                s.onSuccess(
                    eventDao.queryBuilder()
                    .where()
                    .eq(Event.COLUMN_NAME, event.getName()).and()
                    .eq(Event.COLUMN_DATE, event.getDate()).and()
                    .eq(Event.COLUMN_LOCATION, event.getLocation())
                    .queryForFirst() != null
                )
        );
    }

    @Override
    public Observable<Event> getSnapshot() {
        return Observable.create(s -> {
           eventDao.queryForAll().forEach(s::onNext);
           s.onComplete();
        });
    }

    @Override
    public Completable createEvent(Event event) {
        return Completable.create(s -> {
            if(eventDao.queryForId(event.getId()) != null || this.containsEvent(event).blockingGet()) {
                s.onError(new PersistenceException(new EventAlreadyExistsException(event)));
            }
            
            createOrUpdateTablesAndCategories(event);
            
            if(eventDao.createOrUpdate(event).isCreated()) {
                eventAdded.onNext(event);
                s.onComplete();
            } else {
                s.onError(new PersistenceException("Could not create event"));
            }
        });
    }
    
    private void createOrUpdateTablesAndCategories(Event event) throws SQLException {
        deleteTablesAndCategories(event, true);
        
        for(TableCategory category : event.getTableCategories()) {
                for(Table table : category.getTables()) {
                    if(tableDao.createOrUpdate(table).getNumLinesChanged() != 1) {
                        Logger.error("local-database-service cu-tac table invariant");
                    }
                }
                
                if(categoryDao.createOrUpdate(category).getNumLinesChanged() != 1) {
                        Logger.error("local-database-service cu-tac category table invariant");
                }
            }
    }
    
    @Override
    public Completable updateEvent(Event event) {
        return Completable.create(s -> {
            boolean duplicate = eventDao.queryBuilder()
                    .where()
                    .ne(Event.COLUMN_ID, event.getId()).and()
                    .eq(Event.COLUMN_NAME, event.getName()).and()
                    .eq(Event.COLUMN_DATE, event.getDate()).and()
                    .eq(Event.COLUMN_LOCATION, event.getLocation())
                    .queryForFirst() != null;
            
            if(duplicate) {
                s.onError(new PersistenceException(new EventAlreadyExistsException(event)));
                return;
            }
            
            Event old = eventDao.queryForSameId(event);
            
            if(old == null) {
                s.onError(new PersistenceException("Event does not exist!"));
                return;
            }
            
            createOrUpdateTablesAndCategories(event);
            
            if(eventDao.createOrUpdate(event).isUpdated()) {
                eventRemoved.onNext(event.getId());
                
                eventAdded.onNext(event);
                s.onComplete();
            } else {
                s.onError(new PersistenceException("Could not update event"));
            }
        });
    }

    private void deleteTablesAndCategories(Event event, boolean retainExisting) throws SQLException {
         List<TableCategory> existingCategories = categoryDao.queryBuilder().where()
                .eq(TableCategory.COLUMN_EVENT, event)
                .query();
        
        for(TableCategory category : existingCategories) {
            if(!retainExisting || !event.getTableCategories().contains(category)) {
                if(categoryDao.delete(category) != 1) {
                    Logger.error("local-database-service cu-tac nonexistent category invariant");
                }
            }
        }
        
        for(TableCategory category : event.getTableCategories()) {
            List<Table> existingTables = tableDao.queryBuilder().where()
                    .eq(Table.COLUMN_CATEGORY, category)
                    .query();

            for(Table table : existingTables) {
                if(!retainExisting || !category.getTables().contains(table)) {
                    if(tableDao.delete(table) != 1) {
                        Logger.error("local-database-service cu-tac nonexistent table invariant");
                    }
                }
            }
        }
    }
    
    @Override
    public Completable deleteEvent(Event event) {
        return Completable.create(s -> {
            if(!this.containsEvent(event).blockingGet()) {
                s.onError(new PersistenceException("Event does not exist"));
                return;
            }
            
            deleteTablesAndCategories(event, false);
            
            Event old = eventDao.queryForSameId(event);
            
            if(eventDao.deleteById(event.getId()) == 1) {
                eventRemoved.onNext(event.getId());
                
                s.onComplete();
            } else {
                s.onError(new PersistenceException("Deletion did not affect one row"));
            }
        });
    }

    @Override
    public Observable<Event> eventAdded() {
        return eventAdded;
    }

    @Override
    public Observable<String> eventRemoved() {
        return eventRemoved;
    }

    @Override
    public Observable<ConnectionState> connection() {
        return connectionState;
    }

    @Override
    public Completable connect() {
        if(connectionSource != null && connectionSource.isOpen(Event.TABLE_NAME)) {
            return Completable.complete();
        }
        
        return Completable.create(s -> {
                    connectionState.onNext(ConnectionState.CONNECTING);

                    connectionSource = new JdbcConnectionSource(connectionString);

                    eventDao = DaoManager.createDao(connectionSource, Event.class);
                    categoryDao = DaoManager.createDao(connectionSource, TableCategory.class);
                    tableDao = DaoManager.createDao(connectionSource, Table.class);

                    connectionState.onNext(ConnectionState.CONNECTED);

                    s.onComplete();
                })
                .doOnError(__ -> connectionState.onNext(ConnectionState.CLOSED));
    }

    @Override
    public Completable shutdown() {
        if(connectionSource == null || connectionState.blockingSingle() != ConnectionState.CONNECTED) {
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
