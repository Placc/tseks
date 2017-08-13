/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.print.settings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Placc
 */
public class PrintSettings {
    
    @JsonProperty("cardNumberScale")
    private double cardNumberScale = 0.6;
    
    @JsonProperty("positionScale")
    private double positionScale = 0.05;
    
    @JsonProperty("marginScale")
    private double marginScale = 0.04;
    
    @JsonProperty("fontScale")
    private double fontScale = 0.055;

    public PrintSettings() {
    }

    @JsonCreator
    public PrintSettings(@JsonProperty("cardNumberScale") double cardNumberScale, @JsonProperty("positionScale") double positionScale, @JsonProperty("marginScale") double marginScale, @JsonProperty("fontScale") double fontScale) {
        this.cardNumberScale = cardNumberScale;
        this.positionScale = positionScale;
        this.marginScale = marginScale;
        this.fontScale = fontScale;
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
