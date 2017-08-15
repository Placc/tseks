/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.overview;

import com.phicaro.tseks.print.PrintJob;
import com.phicaro.tseks.ui.controller.IEventController;
import com.phicaro.tseks.ui.controller.MainController;
import com.phicaro.tseks.ui.controller.components.PrintFromToDialogController;
import com.phicaro.tseks.ui.models.EventViewModel;
import com.phicaro.tseks.ui.models.TableCategoryViewModel;
import com.phicaro.tseks.util.Resources;
import java.net.URL;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

/**
 *
 * @author Placc
 */
public class OverviewEventInfoController implements IEventController {
    @FXML
    private Label infoEventTitle;
    @FXML
    private Label infoEventDescription;
    @FXML
    private Label infoEventName;
    @FXML
    private Label infoEventTableDesc;
    @FXML
    private TableView<TableCategoryViewModel> infoEventTable;
    @FXML
    private SplitMenuButton printButton;
    @FXML
    private TableColumn<TableCategoryViewModel, Integer> infoTableCountColumn;
    @FXML
    private TableColumn<TableCategoryViewModel, Integer> infoTableSeatsColumn;
    @FXML
    private TableColumn<TableCategoryViewModel, String> infoTablePriceColumn;

    //Model
    private EventViewModel eventViewModel;

    @Override
    public void setEvent(EventViewModel eventViewModel) {
        this.eventViewModel = eventViewModel;
        
        infoEventTable.getItems().clear();
        
        if (eventViewModel != null) {
            infoEventName.setText(eventViewModel.getName());
            infoEventTitle.setText(Resources.getString("LAB_EventTitle") + ": " + eventViewModel.getTitle());
            infoEventDescription.setText(Resources.getString("LAB_Description") + ": " + eventViewModel.getDescription());
            
            int sumTables = eventViewModel.getModel().getTableCategories().stream().map(group -> group.getNumberOfTables()).reduce(0, (a, b) -> a + b);
            infoEventTableDesc.setText(Resources.getString("LAB_XTablesOverall", sumTables));
            
            infoEventTable.getItems().addAll(TableCategoryViewModel.fromEvent(eventViewModel.getModel()));

            infoEventTable.setDisable(false);
            printButton.setDisable(eventViewModel.getTableGroups().isEmpty());
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
        
        MenuItem printCards = new MenuItem(Resources.getString("LAB_PrintFromTo"));
        printCards.setOnAction(e -> onPrintFromToClicked());
        
        printButton.getItems().add(printCards);

        infoTableCountColumn.setText(Resources.getString("LAB_Number"));
        infoTableCountColumn.setCellValueFactory(group -> group.getValue().getNumberOfTablesProperty().asObject());

        infoTablePriceColumn.setText(Resources.getString("LAB_Price"));
        infoTablePriceColumn.setCellValueFactory(group -> convertToCurrency(group.getValue().getPrice()));

        infoTableSeatsColumn.setText(Resources.getString("LAB_Seats"));
        infoTableSeatsColumn.setCellValueFactory(group -> group.getValue().getSeatsProperty().asObject());

        infoEventTable.setPlaceholder(new Label(Resources.getString("LAB_NoTablesAvailable")));
        infoEventTable.setItems(FXCollections.observableArrayList());
        infoEventTable.getSelectionModel().setCellSelectionEnabled(false);
    }
    
    private ObservableValue<String> convertToCurrency(double price) {
        String result = String.valueOf(price);
        
        int toAppend = 2;
        if(result.contains(".") || result.contains(",")) {
            int digitsAfter = result.substring(Math.max(result.indexOf("."), result.indexOf(","))).length() - 1;
            toAppend -= digitsAfter;
        } else {
            result += ".";
        }
        
        for(int cnt = 0; cnt < toAppend; cnt++) {
            result += "0";
        }
        
         result += Resources.getString("LAB_Currency");
         
         return new SimpleObjectProperty<>(result);
    }
    
    private void onPrintClicked() {
        MainController.instance().getTseksApp().getPrinterService().print(eventViewModel.getModel());
    }
    
    private void onPrintFromToClicked() {
        MainController.instance().toggleSpinner(true);
        
        int minCardNumber = eventViewModel.getTableGroups().stream().map(group -> group.getStartNumber()).min(Comparator.naturalOrder()).orElse(1);
        int maxCardNumber = eventViewModel.getTableGroups().stream().map(group -> group.getEndNumber()).max(Comparator.naturalOrder()).orElse(1);
        
        Optional<Pair<Integer, Integer>> resultOpt = new PrintFromToDialogController(minCardNumber, maxCardNumber).showAndWait();
        
        MainController.instance().toggleSpinner(false);
        
        if(resultOpt.isPresent()) {
            Pair<Integer, Integer> result = resultOpt.get();
            
            MainController.instance().getTseksApp().getPrinterService()
                    .print(eventViewModel.getModel(), result.getKey(), result.getValue());
        }
    }
}
