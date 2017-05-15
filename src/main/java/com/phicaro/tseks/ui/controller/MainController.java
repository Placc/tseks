package com.phicaro.tseks.ui.controller;

import com.phicaro.tseks.util.Logger;
import com.phicaro.tseks.util.Resources;
import com.phicaro.tseks.util.UiHelper;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;


public class MainController implements Initializable {
  
    @FXML
    private BorderPane contentPane;
    
    @FXML
    private ToolBar toolbar;
    
    @FXML
    private Button eventOverviewButton;
    
    @FXML
    public void handleEventOverviewButtonClicked(ActionEvent event) { 
        try {
            contentPane.setCenter(FXMLLoader.load(getClass().getResource("/fxml/Overview.fxml")));
        } catch (IOException e) {
            Logger.error("main-controller handle-event-overview-button-clicked", e);
            UiHelper.showError(Resources.getString("LAB_Error"), Resources.getString("MSG_CouldNotLoadResource"));
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        eventOverviewButton.setText(Resources.getString("LAB_EventOverview"));
    }    
}
