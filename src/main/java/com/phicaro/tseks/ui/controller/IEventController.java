/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller;

import com.phicaro.tseks.ui.models.EventViewModel;
import javafx.fxml.Initializable;

/**
 *
 * @author Placc
 */
public interface IEventController extends Initializable {
    public void setEvent(EventViewModel event);
}
