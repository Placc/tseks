/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller;

import com.phicaro.tseks.entities.Event;
import com.phicaro.tseks.services.EventService;
import com.phicaro.tseks.ui.viewmodels.EventViewModel;
import com.phicaro.tseks.util.Resources;
import com.phicaro.tseks.util.UiHelper;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Placc
 */
public class OverviewController implements Initializable {

    @FXML
    private BorderPane overviewContentPane;
    @FXML
    private Label pageTitle;
    @FXML
    private MenuButton optionButton;
    @FXML
    private TableView<EventViewModel> eventTable;
    @FXML
    private Button addEventButton;
    @FXML
    private Label infoEventTitle;
    @FXML
    private TitledPane infoEventPane;
    @FXML
    private TableView<?> infoEventTable;
    @FXML
    private Button printButton;

    @FXML
    private VBox infoPane;
    @FXML
    private TableColumn<EventViewModel, String> eventTableNameColumn;
    @FXML
    private TableColumn<EventViewModel, String> eventTableDateColumn;
    @FXML
    private TableColumn<EventViewModel, String> eventTableLocationColumn;
    @FXML
    private TableColumn<EventViewModel, String> eventTableOptionsColumn;
    @FXML
    private TableColumn<?, ?> infoTableCountColumn;
    @FXML
    private TableColumn<?, ?> infoTableSeatsColumn;
    @FXML
    private TableColumn<?, ?> infoTablePriceColumn;

    //Model
    private EventService eventService;
    private ObservableList<EventViewModel> events;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Page views
        pageTitle.setText(Resources.getString("LAB_Events"));
        optionButton.setText(Resources.getString("LAB_Options"));

        addEventButton.setText(Resources.getString("LAB_NewEvent"));
        addEventButton.setContentDisplay(ContentDisplay.LEFT);
        addEventButton.setGraphic(new ImageView(Resources.getImage("add.png", Resources.ImageSize.NORMAL)));

        printButton.setText(Resources.getString("LAB_PrintTickets"));
        printButton.setContentDisplay(ContentDisplay.LEFT);
        printButton.setGraphic(new ImageView(Resources.getImage("print.png", Resources.ImageSize.NORMAL)));
        
        //Table columns
        eventTableNameColumn.setText(Resources.getString("LAB_Event"));
        eventTableNameColumn.setCellValueFactory(event -> event.getValue().getNameProperty());

        eventTableDateColumn.setText(Resources.getString("LAB_Date"));
        eventTableDateColumn.setCellValueFactory(event -> event.getValue().getDateProperty());

        eventTableLocationColumn.setText(Resources.getString("LAB_Location"));
        eventTableLocationColumn.setCellValueFactory(event -> event.getValue().getLocationProperty());

        infoTableCountColumn.setText(Resources.getString("LAB_Number"));
        infoTablePriceColumn.setText(Resources.getString("LAB_Price"));
        infoTableSeatsColumn.setText(Resources.getString("LAB_Seats"));

        infoPane.setVisible(false);

        eventService = MainController.instance().getTseksApp().getEventService();
        events = FXCollections.observableArrayList();

        //Tables
        eventTable.setItems(events);
        
        eventService.getEvents()
                .doOnComplete(() -> {
                    eventService.eventAdded().subscribe(this::addEvent);
                    eventService.eventRemoved().subscribe(this::removeEvent);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(this::addEvent, e -> UiHelper.showException(e));
    }

    private void addEvent(Event event) {
        EventViewModel viewModel = new EventViewModel(event);

        if (!events.contains(viewModel)) {
            events.add(viewModel);
        }
    }

    private void removeEvent(Event event) {
        EventViewModel viewModel = new EventViewModel(event);
        events.remove(viewModel);
    }
}
