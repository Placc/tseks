/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.database.impl;

import com.phicaro.tseks.database.IDatabaseInitializer;
import com.phicaro.tseks.database.IDatabaseService;
import io.reactivex.Single;

/**
 *
 * @author Placc
 */
public class PostgresDatabaseInitializer implements IDatabaseInitializer {

    @Override
    public Single<IDatabaseService> initializeDatabase() {
        return Single.error(new Exception());
    }
    
}