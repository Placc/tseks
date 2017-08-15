/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.overview;

import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.model.EventService;
import com.phicaro.tseks.ui.controller.INavigationController;
import com.phicaro.tseks.ui.controller.MainController;
import com.phicaro.tseks.ui.models.EventViewModel;
import com.phicaro.tseks.util.Resources;
import com.phicaro.tseks.ui.util.UiHelper;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 *
 * @author Placc
 */
public class OverviewController implements INavigationController, Initializable {

    @FXML
    private TableView<EventViewModel> eventTable;
    @FXML
    private Button addEventButton;
    @FXML
    private TableColumn<EventViewModel, HBox> eventTableNameColumn;
    @FXML
    private TableColumn<EventViewModel, HBox> eventTableDateColumn;
    @FXML
    private TableColumn<EventViewModel, HBox> eventTableLocationColumn;
    @FXML
    private TableColumn<EventViewModel, HBox> eventTableOptionsColumn;
    
    @FXML
    private AnchorPane eventInfo;
    @FXML
    private OverviewEventInfoController eventInfoController;

    //Model
    private EventService eventService;
    private ObservableList<EventViewModel> events;
    
    private Disposable addedDisposable;
    private Disposable removedDisposable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addEventButton.setText(Resources.getString("LAB_NewEvent"));
        addEventButton.setContentDisplay(ContentDisplay.LEFT);
        addEventButton.setGraphic(new ImageView(Resources.getImage("add.png", Resources.ImageSize.NORMAL)));
        addEventButton.setOnAction(e -> onAddEventClicked());

        //Table columns
        eventTableNameColumn.setText(Resources.getString("LAB_Event"));
        eventTableNameColumn.setCellValueFactory(event -> createLabelHBox(event.getValue().getName()));

        eventTableDateColumn.setText(Resources.getString("LAB_Date"));
        eventTableDateColumn.setCellValueFactory(event -> createLabelHBox(event.getValue().getDate()));

        eventTableLocationColumn.setText(Resources.getString("LAB_Location"));
        eventTableLocationColumn.setCellValueFactory(event -> createLabelHBox(event.getValue().getLocation()));

        eventTableOptionsColumn.setCellValueFactory(event -> createOptionsForEvent(event.getValue()));

        eventService = MainController.instance().getTseksApp().getEventService();
        events = FXCollections.observableArrayList();

        //Tables
        eventTable.setPlaceholder(new Label(Resources.getString("LAB_NoEventsAvailable")));
        eventTable.setItems(events);
        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, o, s) -> eventInfoController.setEvent(s));

        eventInfoController.setEvent(null);

        MainController.instance().toggleSpinner(true);
        eventService.getEvents()
                .doOnComplete(() -> {
                    MainController.instance().toggleSpinner(false);
                    addedDisposable = eventService.eventAdded().subscribe(this::addEvent);
                    removedDisposable = eventService.eventRemoved().subscribe(this::removeEvent);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(this::addEvent, e -> UiHelper.showException(Resources.getString("LAB_ErrorOccured"), e));
    }

    @Override
    public Single<Boolean> onNavigateAway() {
        addedDisposable.dispose();
        removedDisposable.dispose();
        
        return Single.just(true);
    }
    
    private void addEvent(Event event) {
        Platform.runLater(() -> {
            EventViewModel viewModel = new EventViewModel(event);

            if (!events.contains(viewModel)) {
                events.add(viewModel);
            }
        });
    }

    private void removeEvent(Event event) {
        Platform.runLater(() ->
            events.removeIf(viewModel -> viewModel.getModel() != null && viewModel.getModel().getId().equals(event.getId()))
        );
    }

    private ObjectProperty<HBox> createLabelHBox(String value) {
        Label label = new Label(value);
        
        HBox hbox = new HBox(label);
        hbox.alignmentProperty().setValue(Pos.CENTER);
        
        return new SimpleObjectProperty<>(hbox);
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

        copy.getStyleClass().add("back-btn");
        edit.getStyleClass().add("back-btn");
        delete.getStyleClass().add("back-btn");
        
        copy.setOnAction(e -> copyEventClicked(eventViewModel));
        edit.setOnAction(e -> editEventClicked(eventViewModel));
        delete.setOnAction(e -> deleteEventClicked(eventViewModel));

        return new SimpleObjectProperty<>(new HBox(20., copy, edit, delete));
    }

    private void onAddEventClicked() {
        this.onNavigateAway().subscribe(__ ->
            MainController.instance().switchToEdit(null)
        );
    }
    
    private void copyEventClicked(EventViewModel e) {
        MainController.instance().toggleSpinner(true);
        eventService.copyEvent(e.getModel())
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(() -> MainController.instance().toggleSpinner(false));
    }

    private void editEventClicked(EventViewModel e) {
        this.onNavigateAway().subscribe(__ ->
            MainController.instance().switchToEdit(e)
        );
    }

    private void deleteEventClicked(EventViewModel e) {
        if(UiHelper.showDeleteEventDialog().blockingFirst()) {
            MainController.instance().toggleSpinner(true);
            eventService.deleteEvent(e.getModel())
                    .subscribeOn(Schedulers.io())
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe(() -> MainController.instance().toggleSpinner(false));
        }
    }
}
