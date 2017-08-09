/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.persister;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.DoubleType;
import com.phicaro.tseks.model.entities.Location;
import com.phicaro.tseks.model.entities.PriceCategory;

/**
 *
 * @author Placc
 */
public class PricePersister extends DoubleType {
    
    private static PricePersister singleton = new PricePersister();
    
    private PricePersister() {
        super(SqlType.DOUBLE, new Class<?>[] { PriceCategory.class });
    }
    
    public static PricePersister getSingleton() {
        return singleton;
    }
    
    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        if(javaObject == null) {
            return null;
        }
        return ((PriceCategory) javaObject).getPrice();
    }
    
    
    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
       if(sqlArg == null) {
           return null;
       }
       return new PriceCategory((double) sqlArg);
    }
}
