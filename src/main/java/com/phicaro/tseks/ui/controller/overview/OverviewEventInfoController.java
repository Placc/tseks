/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.overview;

import com.phicaro.tseks.ui.models.EventViewModel;
import com.phicaro.tseks.ui.models.TableGroupViewModel;
import com.phicaro.tseks.util.Resources;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

/**
 *
 * @author Placc
 */
public class OverviewEventInfoController implements Initializable {
    @FXML
    private Label infoEventTitle;
    @FXML
    private Label infoEventDescription;
    @FXML
    private Label infoEventName;
    @FXML
    private Label infoEventTableDesc;
    @FXML
    private TableView<TableGroupViewModel> infoEventTable;
    @FXML
    private Button printButton;
    @FXML
    private TableColumn<TableGroupViewModel, Integer> infoTableCountColumn;
    @FXML
    private TableColumn<TableGroupViewModel, Integer> infoTableSeatsColumn;
    @FXML
    private TableColumn<TableGroupViewModel, Double> infoTablePriceColumn;

    //Model
    private EventViewModel eventViewModel;

    public void setEvent(EventViewModel eventViewModel) {
        this.eventViewModel = eventViewModel;
        
        infoEventTable.getItems().clear();
        
        if (eventViewModel != null) {
            infoEventName.setText(eventViewModel.getName());
            infoEventTitle.setText(Resources.getString("LAB_EventTitle") + ": " + eventViewModel.getTitle());
            infoEventDescription.setText(Resources.getString("LAB_Description") + ": " + eventViewModel.getDescription());
            
            int sumTables = eventViewModel.getModel().getTableGroups().stream().map(group -> group.getTables().size()).reduce(0, (a, b) -> a + b);
            infoEventTableDesc.setText(Resources.getString("LAB_XTablesOverall", sumTables));
            
            infoEventTable.getItems().addAll(TableGroupViewModel.fromEvent(eventViewModel.getModel()));

            infoEventTable.setDisable(false);
            printButton.setDisable(eventViewModel.getModel().getTableGroups().size() > 0);
        } else {
            infoEventName.setText(Resources.getString("LAB_NoEventSelected"));
            
            infoEventTitle.setText("");
            infoEventDescription.setText("");
            infoEventTableDesc.setText("");

            infoEventTable.setDisable(true);
            printButton.setDisable(true);
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        printButton.setText(Resources.getString("LAB_PrintTickets"));
        printButton.setContentDisplay(ContentDisplay.LEFT);
        printButton.setGraphic(new ImageView(Resources.getImage("print.png", Resources.ImageSize.NORMAL)));
        printButton.setOnAction(e -> onPrintClicked());

        infoTableCountColumn.setText(Resources.getString("LAB_Number"));
        infoTableCountColumn.setCellValueFactory(group -> group.getValue().getNumberOfTablesProperty().asObject());

        infoTablePriceColumn.setText(Resources.getString("LAB_Price"));
        infoTablePriceColumn.setCellValueFactory(group -> group.getValue().getPriceProperty().asObject());

        infoTableSeatsColumn.setText(Resources.getString("LAB_Seats"));
        infoTableSeatsColumn.setCellValueFactory(group -> group.getValue().getSeatsProperty().asObject());

        infoEventTable.setPlaceholder(new Label(Resources.getString("LAB_NoTablesAvailable")));
        infoEventTable.setItems(FXCollections.observableArrayList());
        infoEventTable.getSelectionModel().setCellSelectionEnabled(false);
    }
    
    private void onPrintClicked() {
        //TODO
    }
}
