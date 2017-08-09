/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.database;

import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.model.entities.ITableCategory;
import com.phicaro.tseks.model.entities.Location;
import com.phicaro.tseks.model.entities.PriceCategory;
import com.phicaro.tseks.model.entities.Table;
import com.phicaro.tseks.model.entities.TableCategoryProxy;
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
import java.util.Date;

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
           
           s.onSuccess(result.next());
           
           prepared.close();
        });
    }

    @Override
    public Observable<Event> getSnapshot() {
        return Observable.create(s -> {
           String idQuery = "SELECT * FROM Events";
           
           String tablesQuery = "SELECT * FROM EventsTableCategory etc"
                   + "JOIN TableCategory tablecategory ON etc.categoryId = tablecategory.id "
                   + "ORDER BY tablecategory.id"
                   + "WHERE etc.eventId = ?";
           
           Statement statement = databaseConnection.createStatement();
           
           ResultSet result = statement.executeQuery(idQuery);
           
           while(result.next()) {  
               Event event = new Event(result.getString("id"), 
                                        new Date(result.getLong("date")), 
                                        result.getString("name"), 
                                        result.getString("title"), 
                                        new Location(result.getString("location")));
               
               PreparedStatement prepared = databaseConnection.prepareStatement(tablesQuery);
               
               prepared.setString(0, result.getString("id"));
               
               ResultSet tablesResult = prepared.executeQuery();
               
               while(tablesResult.next()) {
                    event.addTableCategory(
                            new TableCategoryProxy(tablesResult.getString("tablecategory.id"), 
                                                    tablesResult.getInt("tablecategory.tables"),
                                                    tablesResult.getInt("tablecategory.seats"), 
                                                    new PriceCategory(tablesResult.getDouble("tablecategory.price"))));
               }
               
               s.onNext(event);
               
               prepared.close();
           }
           
           statement.close();
                      
           s.onComplete();
        });
    }

    @Override
    public Completable saveEvent(Event event) {
        return Completable.create(s -> {
            String eventInsert = "INSERT INTO Events VALUES (?, ?, ?, ?, ?, ?)";
            String tableCategoryInsert = "INSERT INTO TableCategory VALUES (?, ?, ?, ?)";
            String etcInsert = "INSERT INTO EventsTableCategory VALUES (?, ?)";
            String tableInsert = "INSERT INTO Table VALUES (?, ?, ?)";
            String ttcInsert = "INSERT INTO TableTableCategory VALUES (?, ?)";
            
            PreparedStatement eventStmt = databaseConnection.prepareStatement(eventInsert);
            
            eventStmt.setString(0, event.getId());
            eventStmt.setString(1, event.getName());
            eventStmt.setString(2, event.getTitle());
            eventStmt.setLong(3, event.getDate().getTime());
            eventStmt.setString(4, event.getLocation().getLocationDescription());
            eventStmt.setString(5, event.getDescription());
            
            eventStmt.executeUpdate();
            eventStmt.close();
            
            for(ITableCategory category : event.getTableCategories()) {
                PreparedStatement tcStmt = databaseConnection.prepareStatement(tableCategoryInsert);
                
                tcStmt.setString(0, category.getId());
                tcStmt.setDouble(1, category.getPrice().getPrice());
                tcStmt.setInt(2, category.getSeatsNumber());
                tcStmt.setInt(3, category.getNumberOfTables());
                
                tcStmt.executeUpdate();
                tcStmt.close();
                
                PreparedStatement etcStmt = databaseConnection.prepareStatement(etcInsert);
                
                etcStmt.setString(0, event.getId());
                etcStmt.setString(1, category.getId());
                
                etcStmt.executeUpdate();
                etcStmt.close();
                
                for(Table table : category.getTables()) {
                    PreparedStatement tStmt = databaseConnection.prepareStatement(tableInsert);
                    
                    tStmt.setString(0, table.getId());
                    tStmt.setInt(1, table.getSeats());
                    tStmt.setInt(2, table.getTableNumber());
                    
                    tStmt.executeUpdate();
                    tStmt.close();
                    
                    PreparedStatement ttcStmt = databaseConnection.prepareStatement(ttcInsert);
                    
                    ttcStmt.setString(0, table.getId());
                    ttcStmt.setString(1, category.getId());
                    
                    ttcStmt.executeUpdate();
                    ttcStmt.close();
                }
            }
            
            s.onComplete();
        });
    }

    @Override
    public Completable updateEvent(Event event) {
        return Completable.create(s -> {
            String eventUpdate = "UPDATE Events SET name = ?, title = ?, date = ?, location = ?, description = ? WHERE id = ?";
            String tcUpdate = "INSERT OR REPLACE INTO TableCategory VALUES (?, ?, ?, ?)";
            String etcUpdate = "INSERT OR REPLACE INTO EventsTableCategory VALUES (?, ?)";
            String tableUpdate = "INSERT OR REPLACE INTO Table VALUES (?, ?, ?)";
            String ttcUpdate = "INSERT OR REPLACE INTO TableTableCategory VALUES (?, ?)";

            PreparedStatement eventStmt = databaseConnection.prepareStatement(eventUpdate);

            eventStmt.setString(0, event.getName());
            eventStmt.setString(1, event.getTitle());
            eventStmt.setLong(2, event.getDate().getTime());
            eventStmt.setString(3, event.getLocation().getLocationDescription());
            eventStmt.setString(4, event.getDescription());
            eventStmt.setString(5, event.getId());

            eventStmt.executeUpdate();
            eventStmt.close();
                        
            String cleanup = "DELETE FROM EventsTableCategory"
                         + "WHERE eventId = ? AND categoryId NOT IN " 
                         + createPlaceholder(event.getTableCategories().size());
            
            PreparedStatement cleanStmt = databaseConnection.prepareStatement(cleanup);
            
            cleanStmt.setString(0, event.getId());
            
            int categoryIdx = 1;
            for(ITableCategory category : event.getTableCategories()) {
                cleanStmt.setString(categoryIdx++, category.getId());
                
                PreparedStatement tcStmt = databaseConnection.prepareStatement(tcUpdate);
                
                tcStmt.setString(0, category.getId());
                tcStmt.setDouble(1, category.getPrice().getPrice());
                tcStmt.setInt(2, category.getSeatsNumber());
                tcStmt.setInt(3, category.getNumberOfTables());
                
                tcStmt.executeUpdate();
                tcStmt.close();
                
                PreparedStatement etcStmt = databaseConnection.prepareStatement(etcUpdate);
                
                etcStmt.setString(0, event.getId());
                etcStmt.setString(1, category.getId());
                
                etcStmt.executeUpdate();
                etcStmt.close();
                
                String tcleanup = "DELETE FROM TableTableCategory ttc "
                         + "JOIN EventsTableCategory etc ON ttc.categoryId = etc.categoryId "
                         + "WHERE etc.eventId = ? AND ttc.tableId NOT IN "
                         + createPlaceholder(category.getNumberOfTables());
                
                PreparedStatement tcleanStmt = databaseConnection.prepareStatement(tcleanup);
                
                tcleanStmt.setString(0, event.getId());
                
                int tableIdx = 1;
                for(Table table : category.getTables()) {
                    tcleanStmt.setString(tableIdx++, table.getId());
                    
                    PreparedStatement tStmt = databaseConnection.prepareStatement(tableUpdate);
                    
                    tStmt.setString(0, table.getId());
                    tStmt.setInt(1, table.getSeats());
                    tStmt.setInt(2, table.getTableNumber());
                    
                    tStmt.executeUpdate();
                    tStmt.close();
                    
                    PreparedStatement ttcStmt = databaseConnection.prepareStatement(ttcUpdate);
                    
                    ttcStmt.setString(0, table.getId());
                    ttcStmt.setString(1, category.getId());
                    
                    ttcStmt.executeUpdate();
                    ttcStmt.close();
                }
                
                tcleanStmt.executeUpdate();
                tcleanStmt.close();
            }

            cleanStmt.executeUpdate();
            cleanStmt.close();
            
            s.onComplete();
        });
    }
    
    private String createPlaceholder(int length) {
        StringBuilder sb = new StringBuilder();
        sb.append("(?");
        for (int i = 1; i < length; i++) {
            sb.append(",?");
        }
        return sb.append(")").toString();
    }

    @Override
    public Completable deleteEvent(Event event) {
        return Completable.create(s -> {
            String deleteEvent = "DELETE FROM Events WHERE id = ?";
            String deleteTablesAndCategories = "DELETE FROM Table t JOIN TableTableCategory ttc ON t.id = ttc.tableId JOIN TableCategory tc ON ttc.categoryId = tc.id JOIN EventsTableCategory etc ON tc.id = etc.categoryId WHERE etc.eventId = ?";

            PreparedStatement preparedDelete = databaseConnection.prepareStatement(deleteTablesAndCategories);

            preparedDelete.setString(0, event.getId());
        
            preparedDelete.executeUpdate();
            preparedDelete.close();
            
            preparedDelete = databaseConnection.prepareStatement(deleteEvent);
            
            preparedDelete.setString(0, event.getId());
            
            preparedDelete.executeUpdate();
            preparedDelete.close();
            
            s.onComplete();
        });
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
