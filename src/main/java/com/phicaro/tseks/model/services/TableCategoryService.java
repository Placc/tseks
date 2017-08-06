/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.model.services;

import com.phicaro.tseks.model.entities.PriceCategory;
import com.phicaro.tseks.model.entities.Table;
import com.phicaro.tseks.model.entities.TableCategory;
import com.phicaro.tseks.model.entities.TableCategoryProxy;
import java.util.UUID;

/**
 *
 * @author Placc
 */
public class TableCategoryService {
    
    public static TableCategory createTableCategory(int seats, double price) {
        return new TableCategory(UUID.randomUUID().toString(), seats, new PriceCategory(price));
    }
    
    public static void setTablesRange(TableCategory group, int from, int to) {
        for (int number = from; number <= to; number++) {
            group.addTable(new Table(UUID.randomUUID().toString(), number, group.getSeatsNumber()));
        }
    }

    public static TableCategory resolveTabeCategoryProxy(TableCategoryProxy aThis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
