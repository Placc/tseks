/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.settings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.phicaro.tseks.print.PageSize;
import com.phicaro.tseks.util.exceptions.InvalidPageSizeException;

/**
 *
 * @author Placc
 */
public class PrintSettings implements ISettings {
    
    @JsonProperty("cardNumberScale")
    private double cardNumberScale = 0.6;
    
    @JsonProperty("positionScale")
    private double positionScale = 0.05;
    
    @JsonProperty("marginScale")
    private double marginScale = 0.04;
    
    @JsonProperty("fontScale")
    private double fontScale = 0.055;
    
    @JsonProperty("cardSize")
    private PageSize cardSize = PageSize.A7;
    
    @JsonProperty("pageSize")
    private PageSize pageSize = PageSize.A4;
    
    @JsonProperty("printer")
    private String printer = null;

    public PrintSettings() {
    }

    @JsonCreator
    public PrintSettings(@JsonProperty("cardNumberScale") double cardNumberScale, @JsonProperty("positionScale") double positionScale, @JsonProperty("marginScale") double marginScale, @JsonProperty("fontScale") double fontScale, @JsonProperty("cardSize") PageSize cardSize, @JsonProperty("pageSize") PageSize pageSize, @JsonProperty("printer") String printer) {
        this.cardNumberScale = cardNumberScale;
        this.positionScale = positionScale;
        this.marginScale = marginScale;
        this.fontScale = fontScale;
        this.cardSize = cardSize;
        this.pageSize = pageSize;
        this.printer = printer;
    }

    public PageSize getCardSize() {
        return cardSize;
    }

    public void setCardSize(PageSize cardSize) throws InvalidPageSizeException {
        if(!cardSize.smaller(pageSize)) {
            throw new InvalidPageSizeException();
        }
        this.cardSize = cardSize;
    }

    public PageSize getPageSize() {
        return pageSize;
    }

    public void setPageSize(PageSize pageSize) throws InvalidPageSizeException {
        if(!cardSize.smaller(pageSize)) {
            throw new InvalidPageSizeException();
        }
        this.pageSize = pageSize;
    }

    public String getPrinter() {
        return printer;
    }

    public void setPrinter(String printer) {
        this.printer = printer;
    }

    public double getCardNumberScale() {
        return cardNumberScale;
    }

    public void setCardNumberScale(double cardNumberScale) {
        this.cardNumberScale = cardNumberScale;
    }

    public double getPositionScale() {
        return positionScale;
    }

    public void setPositionScale(double positionScale) {
        this.positionScale = positionScale;
    }

    public double getMarginScale() {
        return marginScale;
    }

    public void setMarginScale(double marginScale) {
        this.marginScale = marginScale;
    }

    public double getFontScale() {
        return fontScale;
    }

    public void setFontScale(double fontScale) {
        this.fontScale = fontScale;
    }
}
