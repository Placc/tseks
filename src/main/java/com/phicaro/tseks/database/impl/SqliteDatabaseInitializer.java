/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.database.impl;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.phicaro.tseks.database.IDatabaseInitializer;
import com.phicaro.tseks.database.IDatabaseService;
import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.model.entities.TableCategory;
import com.phicaro.tseks.util.Platform;
import com.phicaro.tseks.util.Resources;
import io.reactivex.Single;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

/**
 *
 * @author Placc
 */
public class SqliteDatabaseInitializer implements IDatabaseInitializer {

    private static final String LOCAL_DB_NAME = "tseks.db";

    @Override
    public File databaseFile() {
        return new File(Platform.getWorkingDirectory(), LOCAL_DB_NAME);
    }

    @Override
    public Single<IDatabaseService> initializeDatabase() {
        return Single.create(s -> {
            File db = databaseFile();

            String connectionString = "jdbc:sqlite:" + db.getAbsolutePath();

            if (!db.exists()) {
                try (InputStream stream = Resources.getResourceAsStream("/database/tseks.db")) {
                    Files.copy(stream, db.toPath());
                }

                ConnectionSource connection = new JdbcConnectionSource(connectionString);

                TableUtils.createTable(connection, Event.class);
                TableUtils.createTable(connection, TableCategory.class);

                connection.closeQuietly();
            }

            s.onSuccess(new JdbcDatabaseService(connectionString));
        });
    }

}
