/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.print;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.util.List;

/**
 *
 * @author Placc
 */
public class Page implements IPrintable {

    private List<Card> cards;
    private PageFormat format;
    
    public Page(List<Card> cards, PageFormat format) {
        this.cards = cards;
        this.format = format;
    }
    
    public PageFormat getFormat() {
        return format;
    }
    
    public List<Card> getCards() {
        return cards;
    }
    
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        return 0; //TODO
    }
    
}
