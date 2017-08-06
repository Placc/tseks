/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.entities;

import java.util.List;

/**
 *
 * @author Placc
 */
public interface ITableCategory extends Cloneable {
    PriceCategory getPrice();
    String getId();
    int getSeatsNumber();
    int getNumberOfTables();
    List<Table> getTables();
    void addTable(Table table);
    void removeTable(Table table);
    void clearTables();
    ITableCategory clone();
}
