/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.database;

import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.model.entities.Location;
import com.phicaro.tseks.model.entities.PriceCategory;
import com.phicaro.tseks.model.entities.Table;
import com.phicaro.tseks.model.entities.TableCategory;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Placc
 */
public class SQLiteDatabaseService implements IDatabaseService {

    private PublishSubject<Event> eventAdded;
    private PublishSubject<Event> eventRemoved;

    private BehaviorSubject<ConnectionState> connection;
    
    private String dbPath;
    private Connection databaseConnection;
    
    public SQLiteDatabaseService(String dbPath) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        
        this.dbPath = dbPath;
        
        eventAdded = PublishSubject.create();
        eventRemoved = PublishSubject.create();

        connection = BehaviorSubject.createDefault(ConnectionState.CLOSED);
    }
    
    @Override
    public Single<Boolean> containsEvent(Event event) {
        return Single.create(s -> {
           String query = "SELECT * FROM Events WHERE name = ? AND location = ? AND date = ?";
           PreparedStatement prepared = databaseConnection.prepareStatement(query);
           
           prepared.setString(0, event.getName());
           prepared.setString(1, event.getLocation().getLocationDescription());
           prepared.setLong(2, event.getDate().getTime());
           
           ResultSet result = prepared.executeQuery();
           prepared.close();
           
           s.onSuccess(result.next());
        });
    }

    @Override
    public Observable<Event> getSnapshot() {
        return Observable.create(s -> {
           String idQuery = "SELECT * FROM Events";
           
           String tablesQuery = "SELECT * FROM EventsTableCategory etc"
                   + "JOIN TableCategory tablecategory ON etc.categoryId = tablecategory.id "
                   + "JOIN TableTableCategory ttc ON tablecategory.id = ttc.categoryId "
                   + "JOIN Table table ON ttc.tableId = table.id "
                   + "ORDER BY tablecategory.id"
                   + "WHERE etc.eventId = ?";
           
           Statement statement = databaseConnection.createStatement();
           
           ResultSet result = statement.executeQuery(idQuery);
           statement.close();
           
           while(result.next()) {  
               Event event = new Event(result.getString("id"), new Date(result.getLong("date")), result.getString("name"), result.getString("title"), new Location(result.getString("location")));
               
               PreparedStatement prepared = databaseConnection.prepareStatement(tablesQuery);
               
               prepared.setString(0, result.getString("id"));
               
               ResultSet tablesResult = prepared.executeQuery();
               
               TableCategory current = null;
               
               while(tablesResult.next()) {
                   if(current == null || !current.getId().equals(tablesResult.getString("tablecategory.id"))) {
                       current = new TableCategory(tablesResult.getString("tablecategory.id"), tablesResult.getInt("tablecategory.seats"), new PriceCategory(tablesResult.getDouble("tablecategory.price")));
                       event.addTableGroup(current);
                   }
                   
                   Table table = new Table(tablesResult.getInt("table.number"), tablesResult.getInt("table.seats"));
                   current.addTable(table);
               }
               
               s.onNext(event);
           }
                      
           s.onComplete();
        });
    }

    @Override
    public Completable saveEvent(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Completable updateEvent(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Completable deleteEvent(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public Observable<ConnectionState> connection() {
        return connection;
    }

    @Override
    public Completable connect() {
        return Completable.create(s -> {
            connection.onNext(ConnectionState.CONNECTING);
            databaseConnection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            connection.onNext(ConnectionState.CONNECTED);
            s.onComplete();
        });
    }

    @Override
    public Completable shutdown() {
        return Completable.create(s -> {
            connection.onNext(ConnectionState.CLOSING);
            databaseConnection.close();
            connection.onNext(ConnectionState.CLOSED);
            s.onComplete();
        });
    }
    
}
