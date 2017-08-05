/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.services;

import com.phicaro.tseks.model.entities.PriceCategory;
import com.phicaro.tseks.model.entities.Table;
import com.phicaro.tseks.model.entities.TableCategory;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Placc
 */
public class TableService {
    
    public static TableCategory createTableGroup(int seats, double price) {
        return new TableCategory(UUID.randomUUID().toString(), seats, new PriceCategory(price));
    }
    
    public static Table createTable(int tableNumber, int seats) {
        return new Table(tableNumber, seats);
    }
    
    public static void setTablesRange(TableCategory group, int from, int to) {
        for (int number = from; number <= to; number++) {
            group.addTable(createTable(number, group.getSeatsNumber()));
        }
    }
}
