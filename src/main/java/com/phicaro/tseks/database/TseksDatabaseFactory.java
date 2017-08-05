/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.database;

import com.phicaro.tseks.util.Resources;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 *
 * @author Placc
 */
public class TseksDatabaseFactory {
    
    private static final String LOCAL_DB_NAME = "tseks.db";
    
    public static Single<SQLiteDatabaseService> getLocalDatabaseService() {
        return Single.create(s -> {
            File currentDir = new File(Paths.get(".").toAbsolutePath().normalize().toString());
            File dbDir = new File(currentDir, "db");
            File db = new File(dbDir, LOCAL_DB_NAME);

            if(!dbDir.exists()) {
                dbDir.mkdirs();
            }

            if(!db.exists()) {
                InputStream stream = Resources.getResourceAsStream("/database/tseks.db");
                Files.copy(stream, db.toPath());
            
                Class.forName("org.sqlite.JDBC");
                Connection connection = DriverManager.getConnection("jdbc:sqlite:" + db.getAbsolutePath());

                BufferedReader reader = new BufferedReader(new InputStreamReader(Resources.getResourceAsStream("/database/dbinit")));

                while(reader.ready()) {
                    String sql = reader.readLine();
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate(sql);
                    stmt.close();
                }

                reader.close();
                connection.close();
            }
            
            s.onSuccess(new SQLiteDatabaseService(db.getAbsolutePath()));
        });
    }
}
