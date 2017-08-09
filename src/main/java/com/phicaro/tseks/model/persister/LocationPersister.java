/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.persister;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import com.phicaro.tseks.model.entities.Location;

/**
 *
 * @author Placc
 */
public class LocationPersister extends StringType {

    private static LocationPersister singleton = new LocationPersister();
    
    private LocationPersister() {
        super(SqlType.STRING, new Class<?>[] { Location.class });
    }
    
    public static LocationPersister getSingleton() {
        return singleton;
    }
    
    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        if(javaObject == null) {
            return null;
        }
        return ((Location) javaObject).getLocationDescription();
    }
    
    
    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
       if(sqlArg == null) {
           return null;
       }
       return new Location((String) sqlArg);
    }
    
}
