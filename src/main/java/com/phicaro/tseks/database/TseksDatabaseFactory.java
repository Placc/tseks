/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.database;

import com.phicaro.tseks.database.impl.PostgresDatabaseInitializer;
import com.phicaro.tseks.database.impl.SqliteDatabaseInitializer;

/**
 *
 * @author Placc
 */
public abstract class TseksDatabaseFactory {
    
    public static IDatabaseInitializer getDatabase(DatabaseType type) {
       switch(type) {
           case SQLite: return new SqliteDatabaseInitializer();
           case PostgreSQL: return new PostgresDatabaseInitializer();
           default: throw new NoClassDefFoundError();
       }
    }
}
