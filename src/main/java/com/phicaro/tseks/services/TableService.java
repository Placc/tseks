/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.services;

import com.phicaro.tseks.entities.PriceCategory;
import com.phicaro.tseks.entities.Table;
import com.phicaro.tseks.entities.TableGroup;
import java.util.List;

/**
 *
 * @author Placc
 */
public class TableService {
    
    public static TableGroup createTableGroup(int seats, double price) {
        return new TableGroup(seats, new PriceCategory(price));
    }
    
    public static Table createTable(int tableNumber, int seats) {
        return new Table(tableNumber, seats);
    }
    
    public static void setTablesRange(TableGroup group, int from, int to) {
        for (int number = from; number <= to; number++) {
            group.addTable(createTable(number, group.getSeatsNumber()));
        }
    }
}
