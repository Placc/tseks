/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.edit;

import com.phicaro.tseks.ui.models.EventViewModel;
import com.phicaro.tseks.ui.models.TableGroupViewModel;
import com.phicaro.tseks.util.Resources;
import com.phicaro.tseks.util.UiHelper;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.converter.NumberStringConverter;

/**
 *
 * @author Placc
 */
public class EditEventTableGroupController implements IEditEventController {
    
    @FXML
    private TableView<TableGroupViewModel> eventTableView;
    @FXML
    private TableColumn<TableGroupViewModel, HBox> tableNumberColumn;
    @FXML
    private TableColumn<TableGroupViewModel, HBox> seatsColumn;
    @FXML
    private TableColumn<TableGroupViewModel, HBox> categoryColumn;
    @FXML
    private TableColumn<TableGroupViewModel, HBox> optionsColumn;
    @FXML
    private Label tableGroupLabel;
    @FXML
    private Button addTableGroupButton;
    
    //Model
    private EventViewModel eventViewModel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
        tableGroupLabel.setText(Resources.getString("LAB_TableGroups"));
        
        addTableGroupButton.setText(Resources.getString("LAB_NewTableGroup"));
        addTableGroupButton.setGraphic(new ImageView(Resources.getImage("add_outline.png", Resources.ImageSize.NORMAL)));
        addTableGroupButton.setOnAction(e -> onAddTableGroupClicked());
        
        eventTableView.setPlaceholder(new Label(Resources.getString("LAB_NoTablesAvailable")));
        
        tableNumberColumn.setText(Resources.getString("LAB_TableNumbers"));
        seatsColumn.setText(Resources.getString("LAB_Seats"));
        categoryColumn.setText(Resources.getString("LAB_Price"));
    }    

    public void setEvent(EventViewModel event) {       
        eventViewModel = event;
        
        eventTableView.itemsProperty().bindBidirectional(event.getTableGroupsProperty());
        
        tableNumberColumn.setCellValueFactory(group -> createTableNumbersHbox(group.getValue()));
        seatsColumn.setCellValueFactory(group -> createSeatsHbox(group.getValue()));
        categoryColumn.setCellValueFactory(group -> createCategoryHBox(group.getValue()));
        optionsColumn.setCellValueFactory(group -> createOptionsHbox(group.getValue()));
    }

    public List<String> errors() {
        List<String> invalids = new ArrayList<>();
        
        if(eventViewModel.getTableGroups().stream().anyMatch(group -> group.getEndNumber() < group.getStartNumber())) {
            invalids.add(Resources.getString("DESC_EndnumberBeforeStartnumber"));
        }
        if(eventViewModel.getTableGroups().stream().anyMatch(group -> group.getSeats() <= 0)) {
            invalids.add(Resources.getString("DESC_SeatsZero"));
        }
        if(eventViewModel.getTableGroups().stream().anyMatch(group -> 
                eventViewModel.getTableGroups().stream().filter(g -> !g.equals(group)).anyMatch(g -> 
                        UiHelper.isIntersection(g.getStartNumber(), g.getEndNumber(), group.getStartNumber(), group.getEndNumber())))) {
            invalids.add(Resources.getString("DESC_GroupIntersections"));
        }

        return invalids;
    }
    
    public List<String> warnings() {
        List<String> warnings = new ArrayList<>();
        
        //Missing table numbers
        List<String> missingTables = getMissingTables();
        
        if(!missingTables.isEmpty()) {            
            String message = Resources.getString("DESC_TableNumbersXNotPresent", UiHelper.combine(missingTables));
            warnings.add(message);
        }
        
        //Price 0
        if(eventViewModel.getTableGroups().stream().anyMatch(group -> group.getPrice() <= 0)) {
            warnings.add(Resources.getString("DESC_PriceZero"));
        }
        
        return warnings;
    }

    private List<String> getMissingTables() {
        List<String> missingTables = new ArrayList<>();
        int max = eventViewModel.getTableGroups().stream().map(group -> group.getEndNumber()).max(Comparator.naturalOrder()).orElse(1);
        int interval = 0;
        
        for(int number = 1; number <= max; number++) {
            final int n = number;
            boolean found = eventViewModel.getTableGroups().stream().anyMatch(group -> group.getStartNumber() <= n && n <= group.getEndNumber());
        
            if(!found) {
                if(interval <= 0) {
                    interval = n;
                }
            } 
            else if (interval > 0) {
                missingTables.add(interval + " - " + Math.max(interval, n - 1));
                interval = 0;
            }
        }
        
        if(interval > 0) {
            missingTables.add(interval + " - " + interval);
        }
        
        return missingTables;
    }
    
    private void onAddTableGroupClicked() {
        List<TableGroupViewModel> tableGroups = eventViewModel.getTableGroups();
        
        int seats = 1;
        double price = 0.0; 
        int numberOfTables = 1;
        
        if(!tableGroups.isEmpty()) {
            TableGroupViewModel lastGroup = tableGroups.get(tableGroups.size() - 1);
            seats = lastGroup.getSeats();
            price = lastGroup.getPrice();
            numberOfTables = lastGroup.getNumberOfTables();
        }
        
        int startTableNumber = 1 + tableGroups.stream().map(group -> group.getEndNumber()).max(Comparator.naturalOrder()).orElse(0);
        
        TableGroupViewModel viewModel = new TableGroupViewModel(startTableNumber, numberOfTables, seats, price);
        
        eventViewModel.getTableGroups().add(viewModel);
    }
    
    private ObservableValue<HBox> createTableNumbersHbox(TableGroupViewModel group) {
        Label minus = new Label(" - ");
        
        TextField from = new TextField();
        
        from.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                from.setText(newValue.replaceAll("[^\\d]", ""));
            } 
        });
        
        from.textProperty().bindBidirectional(group.getStartNumberProperty(), new NumberStringConverter());
        
        TextField to = new TextField();
        
        to.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                to.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
                
        to.textProperty().bindBidirectional(group.getEndNumberProperty(), new NumberStringConverter());
        
        HBox result = new HBox(from, minus, to);
        result.alignmentProperty().setValue(Pos.CENTER);
        
        return new SimpleObjectProperty<>(result);
    }

    private ObservableValue<HBox> createCategoryHBox(TableGroupViewModel group) {
        Label currency = new Label(Resources.getString("LAB_Currency"));
        
        TextField amount = new TextField();
        
        amount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]+((\\,|\\.)[0-9]{0,2})?")) {
                String newVal = newValue.replaceAll("[^\\d\\.\\,]", "");
                int pointIdx = -1;
                
                if(newVal.contains(",") && newVal.contains(".")) {
                    pointIdx = Math.min(newVal.indexOf("."), newValue.indexOf(","));
                } 
                else if(newVal.contains(",")) {
                    pointIdx = newVal.indexOf(",");
                } 
                else if(newVal.contains(".")) {
                    pointIdx = newVal.indexOf(".");
                }
                
                if(pointIdx == 0) {
                    newVal = "0" + newVal;
                    pointIdx++;
                }
                
                int endIdx = pointIdx + 1;
                
                while(endIdx < Math.min(pointIdx + 3, newVal.length()) && endIdx > 0) {
                    if(!String.valueOf(newVal.charAt(endIdx)).matches("[0-9]")) {
                        break;
                    }
                    endIdx++;
                }
                
                amount.setText(newVal.substring(0, endIdx));
            }
        });
        
        amount.textProperty().bindBidirectional(group.getPriceProperty(), new NumberStringConverter());
        
        HBox result = new HBox(amount, currency);
        result.alignmentProperty().setValue(Pos.CENTER);
        
        return new SimpleObjectProperty<>(result);
    }
    
    private ObservableValue<HBox> createSeatsHbox(TableGroupViewModel group) {
        TextField seats = new TextField();
        
        seats.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                seats.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        seats.textProperty().bindBidirectional(group.getSeatsProperty(), new NumberStringConverter());
        
        HBox result = new HBox(seats);
        result.alignmentProperty().setValue(Pos.CENTER);
        
        return new SimpleObjectProperty<>(result);
    }

    private ObservableValue<HBox> createOptionsHbox(TableGroupViewModel group) {
         Image deleteImage = Resources.getImage("delete.png", Resources.ImageSize.NORMAL);
         
         ColorAdjust colorAdjust = UiHelper.getColorAdjust(Color.STEELBLUE);

         ImageView deleteView = new ImageView(deleteImage);
         deleteView.setEffect(colorAdjust);

         Button delete = new Button("", deleteView);
         delete.getStyleClass().add("back-btn");
        
         delete.setOnAction(e -> deleteTableGroupClicked(group));
         
         return new SimpleObjectProperty<>(new HBox(delete));
    }
    
    private void deleteTableGroupClicked(TableGroupViewModel tableGroup) {
        eventViewModel.getTableGroups().remove(tableGroup);
    }    
}
