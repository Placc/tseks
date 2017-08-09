/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.database;

import io.reactivex.Single;

/**
 *
 * @author Placc
 */
public interface IDatabaseInitializer {
    Single<IDatabaseService> initializeDatabase();
}
