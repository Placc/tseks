/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller;

import com.phicaro.tseks.entities.Event;
import com.phicaro.tseks.services.EventService;
import com.phicaro.tseks.ui.models.EventViewModel;
import com.phicaro.tseks.ui.models.TableGroup;
import com.phicaro.tseks.util.Resources;
import com.phicaro.tseks.util.UiHelper;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Placc
 */
public class OverviewController implements Initializable {

    @FXML
    private BorderPane overviewContentPane;
    @FXML
    private TableView<EventViewModel> eventTable;
    @FXML
    private Button addEventButton;
    @FXML
    private Label infoEventTitle;
    @FXML
    private Label infoEventDesc;
    @FXML
    private TableView<TableGroup> infoEventTable;
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
    private TableColumn<EventViewModel, HBox> eventTableOptionsColumn;
    @FXML
    private TableColumn<TableGroup, Integer> infoTableCountColumn;
    @FXML
    private TableColumn<TableGroup, Integer> infoTableSeatsColumn;
    @FXML
    private TableColumn<TableGroup, Double> infoTablePriceColumn;

    //Model
    private EventService eventService;
    private ObservableList<EventViewModel> events;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        eventTableOptionsColumn.setCellValueFactory(event -> createOptionsForEvent(event.getValue()));

        infoTableCountColumn.setText(Resources.getString("LAB_Number"));
        infoTableCountColumn.setCellValueFactory(group -> group.getValue().getNumberOfTablesProperty().asObject());

        infoTablePriceColumn.setText(Resources.getString("LAB_Price"));
        infoTablePriceColumn.setCellValueFactory(group -> group.getValue().getPriceProperty().asObject());

        infoTableSeatsColumn.setText(Resources.getString("LAB_Seats"));
        infoTableSeatsColumn.setCellValueFactory(group -> group.getValue().getSeatsProperty().asObject());

        eventService = MainController.instance().getTseksApp().getEventService();
        events = FXCollections.observableArrayList();

        //Tables
        eventTable.setPlaceholder(new Label(Resources.getString("LAB_NoEventsAvailable")));
        eventTable.setItems(events);
        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, o, s) -> onSelected(s));

        infoEventTable.setPlaceholder(new Label(Resources.getString("LAB_NoTablesAvailable")));
        infoEventTable.setItems(FXCollections.observableArrayList());
        infoEventTable.getSelectionModel().setCellSelectionEnabled(false);

        onSelected(null);

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

    private ObjectProperty<HBox> createOptionsForEvent(EventViewModel eventViewModel) {
        Image copyImage = Resources.getImage("copy.png", Resources.ImageSize.NORMAL);
        Image editImage = Resources.getImage("create.png", Resources.ImageSize.NORMAL);
        Image deleteImage = Resources.getImage("delete.png", Resources.ImageSize.NORMAL);

        ColorAdjust colorAdjust = UiHelper.getColorAdjust(Color.STEELBLUE);

        ImageView copyView = new ImageView(copyImage);
        ImageView editView = new ImageView(editImage);
        ImageView deleteView = new ImageView(deleteImage);
        
        copyView.setEffect(colorAdjust);
        editView.setEffect(colorAdjust);
        deleteView.setEffect(colorAdjust);

        Button copy = new Button("", copyView);
        Button edit = new Button("", editView);
        Button delete = new Button("", deleteView);

        copy.getStyleClass().add("tool-bar");
        edit.getStyleClass().add("tool-bar");
        delete.getStyleClass().add("tool-bar");
        
        copy.setOnAction(e -> copyEventClicked(eventViewModel.getEvent()));
        edit.setOnAction(e -> editEventClicked(eventViewModel.getEvent()));
        delete.setOnAction(e -> deleteEventClicked(eventViewModel.getEvent()));

        return new SimpleObjectProperty<>(new HBox(20., copy, edit, delete));
    }

    private void copyEventClicked(Event e) {
    }

    private void editEventClicked(Event e) {
    }

    private void deleteEventClicked(Event e) {
    }

    private void onSelected(EventViewModel selection) {
        infoEventTable.getItems().clear();

        if (selection != null) {
            infoEventTitle.setText(selection.getName());
            infoEventDesc.setText(Resources.getString("LAB_XTablesOverall", selection.getEvent().getTables().size()));
            infoEventTable.getItems().addAll(TableGroup.fromEvent(selection.getEvent()));

            infoEventTable.setDisable(false);
            printButton.setDisable(selection.getEvent().getTables().size() > 0);
        } else {
            infoEventTitle.setText(Resources.getString("LAB_NoEventSelected"));
            infoEventDesc.setText("");

            infoEventTable.setDisable(true);
            printButton.setDisable(true);
        }
    }
}
