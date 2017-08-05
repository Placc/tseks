/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.edit;

import com.phicaro.tseks.ui.models.EventViewModel;
import java.util.List;
import javafx.fxml.Initializable;

/**
 *
 * @author Placc
 */
public interface IEditEventController extends Initializable {
    public void setEvent(EventViewModel event);
    public List<String> errors();
    public List<String> warnings();
}
