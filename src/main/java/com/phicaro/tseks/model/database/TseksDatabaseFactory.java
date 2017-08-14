/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.database;

import com.phicaro.tseks.model.database.impl.PostgresDatabaseInitializer;
import com.phicaro.tseks.model.database.impl.SqliteDatabaseInitializer;
import com.phicaro.tseks.util.exceptions.BadArgumentException;

/**
 *
 * @author Placc
 */
public class TseksDatabaseFactory {
    
    public static IDatabaseInitializer getDatabase(DatabaseType type) {
       switch(type) {
           case SQLite: return new SqliteDatabaseInitializer();
           case PostgreSQL: return new PostgresDatabaseInitializer();
           default: throw new BadArgumentException();
       }
    }
}
