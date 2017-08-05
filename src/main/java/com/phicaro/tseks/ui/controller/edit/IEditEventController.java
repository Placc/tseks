/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.edit;

import com.phicaro.tseks.ui.controller.IEventController;
import java.util.List;

/**
 *
 * @author Placc
 */
public interface IEditEventController extends IEventController {
    public List<String> errors();
    public List<String> warnings();
}
