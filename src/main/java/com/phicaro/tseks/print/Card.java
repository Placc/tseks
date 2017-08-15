/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.print;

import com.phicaro.tseks.model.entities.Event;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.util.Date;

/**
 *
 * @author Placc
 */
public class Card implements IPrintable {

    private int cardNumber;
    private int tableNumber;
    private double price;
    
    private String title;
    private String description;
    private String location;
    private Date date;
    
    public Card(int cardNumber, Event event, int tableNumber, double price, PageSize cardSize) {
        this.cardNumber = cardNumber;
        this.tableNumber = tableNumber;
        this.price = price;
        
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.location = event.getLocation().getLocationDescription();
        this.date = event.getDate();
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public double getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
    public String getLocation() {
        return location;
    }

    public Date getDate() {
        return date;
    }
    
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        return 0; //TODO
    }
    
}
