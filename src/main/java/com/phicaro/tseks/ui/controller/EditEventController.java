/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller;

import com.phicaro.tseks.entities.Event;
import com.phicaro.tseks.entities.Location;
import com.phicaro.tseks.entities.TableGroup;
import com.phicaro.tseks.services.EventService;
import com.phicaro.tseks.ui.models.EventViewModel;
import com.phicaro.tseks.ui.models.TableGroupViewModel;
import com.phicaro.tseks.util.Resources;
import com.phicaro.tseks.util.TimeTextField;
import com.phicaro.tseks.util.UiHelper;
import com.phicaro.tseks.util.exceptions.LifecycleException;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
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
 * FXML Controller class
 *
 * @author Placc
 */
public class EditEventController implements Initializable, INavigationController {

    @FXML
    private Label eventNameLabel;
    @FXML
    private Label eventDescLabel;
    @FXML
    private Label eventDateLabel;
    @FXML
    private Label eventLocationLabel;
    @FXML
    private TextField eventNameEditText;
    @FXML
    private TextField eventDescEditText;
    @FXML
    private TextField eventLocationEditText;
    @FXML
    private DatePicker eventDatePicker;
    @FXML
    private HBox eventDateHBox;     
    @FXML
    private TableView<TableGroupViewModel> eventTableView;
    @FXML
    private TableColumn<TableGroupViewModel, HBox> tableNumberColumn;
    @FXML
    private TableColumn<TableGroupViewModel, Integer> seatsColumn;
    @FXML
    private TableColumn<TableGroupViewModel, HBox> categoryColumn;
    @FXML
    private TableColumn<TableGroupViewModel, HBox> optionsColumn;
    @FXML
    private Label previewLabel;
    @FXML
    private ImageView previewImageView;
    @FXML
    private Button saveButton;
    @FXML
    private Button discardButton;
    @FXML
    private Label tableGroupLabel;
    @FXML
    private Button addTableGroupButton;
    
    private TimeTextField timeTextField;

    //Model
    private EventViewModel eventViewModel = new EventViewModel();
    
    //Subscription
    private Disposable addedDisposable;
    private Disposable removedDisposable;
    
    public void setExistingEvent(Event event) {
        this.eventViewModel = new EventViewModel(event);
        initializeWithEvent(this.eventViewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        timeTextField = new TimeTextField();        
        eventDateHBox.getChildren().add(timeTextField);
        eventDateHBox.setMargin(timeTextField, new Insets(0, 10, 0, 10));
        
        eventNameLabel.setText(Resources.getString("LAB_EventName"));
        eventDescLabel.setText(Resources.getString("LAB_Description"));
        eventDateLabel.setText(Resources.getString("LAB_DateTime"));
        eventLocationLabel.setText(Resources.getString("LAB_Location"));
        previewLabel.setText(Resources.getString("LAB_Preview"));
        
        saveButton.setText(Resources.getString("LAB_Save"));
        saveButton.setGraphic(new ImageView(Resources.getImage("save.png", Resources.ImageSize.NORMAL)));
        saveButton.setOnAction(e -> onSaveClicked());
        
        discardButton.setText(Resources.getString("LAB_Discard"));
        discardButton.setGraphic(new ImageView(Resources.getImage("clear.png", Resources.ImageSize.NORMAL)));
        discardButton.setOnAction(e -> onDiscardClicked());
        
        tableGroupLabel.setText(Resources.getString("LAB_TableGroups"));
        
        addTableGroupButton.setText(Resources.getString("LAB_NewTableGroup"));
        addTableGroupButton.setGraphic(new ImageView(Resources.getImage("add_outline.png", Resources.ImageSize.NORMAL)));
        addTableGroupButton.setOnAction(e -> onAddTableGroupClicked());
        
        eventTableView.setPlaceholder(new Label(Resources.getString("LAB_NoTablesAvailable")));
        
        tableNumberColumn.setText(Resources.getString("LAB_TableNumbers"));
        seatsColumn.setText(Resources.getString("LAB_Seats"));
        categoryColumn.setText(Resources.getString("LAB_Price"));
        
        EventService eventService = MainController.instance().getTseksApp().getEventService();
        addedDisposable = eventService.eventAdded().subscribe(e -> handleEventChanges(e, true));
        removedDisposable = eventService.eventRemoved().subscribe(e -> handleEventChanges(e, false));
        
        initializeWithEvent(this.eventViewModel);        
    }    

    private void handleEventChanges(Event event, boolean added) {
        if((added ^ hasChanges()) && eventViewModel.getEvent() != null && event.getId().equals(eventViewModel.getEvent().getId()) && !eventViewModel.getEvent().equals(event)) {
            MainController.instance().navigateBack(EditEventController.this);
            UiHelper.showException(Resources.getString("LAB_LifecycleError"), new LifecycleException());
        }
    }
    
    private void initializeWithEvent(EventViewModel event) {        
        eventNameEditText.textProperty().bindBidirectional(event.getNameProperty());
        eventNameEditText.textProperty().addListener((observable, oldValue, newValue) -> enableButtons(hasChanges()));
        
        eventDescEditText.textProperty().bindBidirectional(event.getDescriptionProperty());
        eventDescEditText.textProperty().addListener((observable, oldValue, newValue) -> enableButtons(hasChanges()));
        
        eventLocationEditText.textProperty().bindBidirectional(event.getLocationProperty());
        eventLocationEditText.textProperty().addListener((observable, oldValue, newValue) -> enableButtons(hasChanges()));
        
        Date eventDate = UiHelper.parse(event.getDate());
        
        eventDatePicker.setValue(UiHelper.asLocalDate(eventDate));
        eventDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            Date oldDate = UiHelper.parse(eventViewModel.getDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(oldDate);
            
            calendar.set(newValue.getYear(), newValue.getMonthValue() - 1, newValue.getDayOfMonth());
            
            eventViewModel.setDate(calendar.getTime());
            enableButtons(hasChanges());
        });
        
        timeTextField.setText(new SimpleDateFormat(Resources.getConfig("CFG_TimeFormat")).format(eventDate));
        timeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Date oldDate = UiHelper.parse(eventViewModel.getDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(oldDate);
            
            calendar.set(Calendar.HOUR_OF_DAY, timeTextField.getHours());
            calendar.set(Calendar.MINUTE, timeTextField.getMinutes());
            calendar.set(Calendar.SECOND, timeTextField.getSeconds());
            
            eventViewModel.setDate(calendar.getTime());
            enableButtons(hasChanges());
        });
        
        eventTableView.itemsProperty().bindBidirectional(this.eventViewModel.getTableGroupsProperty());
        
        tableNumberColumn.setCellValueFactory(group -> createTableNumbersHbox(group.getValue()));
        seatsColumn.setCellValueFactory(group -> group.getValue().getSeatsProperty().asObject());
        categoryColumn.setCellValueFactory(group -> createCategoryHBox(group.getValue()));
        optionsColumn.setCellValueFactory(group -> createOptionsHbox(group.getValue()));
        
        enableButtons(false);
    }

    private boolean hasChanges() {
        if(eventViewModel.getEvent() == null) {
            return !eventViewModel.getName().trim().isEmpty() ||
                    !eventViewModel.getLocation().trim().isEmpty() ||
                    !eventViewModel.getDate().trim().isEmpty() || 
                    !eventViewModel.getDescription().trim().isEmpty() ||
                    !eventViewModel.getTableGroups().isEmpty();
        } else {
            return !eventViewModel.matches(eventViewModel.getEvent());
        }
    }
    
    private List<String> invalidChanges() {
        List<String> invalids = new ArrayList<>();
        
        if(eventNameEditText.getText().trim().isEmpty()) {
            invalids.add(Resources.getString("DESC_EmptyEventName"));
        }
        if(eventLocationEditText.getText().trim().isEmpty()) {
            invalids.add(Resources.getString("DESC_EmptyEventLocation"));
        }
        if(timeTextField.getText().trim().isEmpty()) {
            invalids.add(Resources.getString("DESC_EmptyEventTime"));
        }
        
        return invalids;
    }

    private Completable saveChanges() {
        if(hasChanges()) {
            List<String> invalidChanges = invalidChanges();
            
            if(invalidChanges.isEmpty()) {
                EventService eventService =  MainController.instance().getTseksApp().getEventService();

                if(eventViewModel.getEvent() != null) {
                    eventViewModel.updateEvent();
                    return eventService.updateEvent(eventViewModel.getEvent())
                            .doOnComplete(() -> setExistingEvent(eventViewModel.getEvent()));
                } else {
                    return eventService.createNewEvent(eventViewModel.getName(), UiHelper.parse(eventViewModel.getDate()), new Location(eventViewModel.getLocation()), eventViewModel.getDescription())         
                        .doOnSuccess(event -> setExistingEvent(event))
                        .toCompletable();
                }
            } else {
                return Completable.error(new Exception(invalidChanges.stream().reduce("", (s1, s2) -> s1 + "\n" + s2)));
            }
        }
        return Completable.complete();
    }
    
    @Override
    public Single<Boolean> onNavigateBack() {
        if(hasChanges() && !UiHelper.showDiscardChangesDialog().blockingFirst()) {
            return Single.just(false);
        }
        
        addedDisposable.dispose();
        removedDisposable.dispose();
        
        return Single.just(true);
    }

    private void onSaveClicked() {
        MainController.instance().toggleSpinner(true);
        saveChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(() -> {
                        MainController.instance().toggleSpinner(false);
                        MainController.instance().showSuccessMessage(Resources.getString("LAB_SaveSuccessful"));
                }, 
                e -> {
                    MainController.instance().toggleSpinner(false);
                    MainController.instance().showErrorMessage(Resources.getString("LAB_CouldNotSaveEvent"));
                    UiHelper.showException(Resources.getString("LAB_CouldNotSaveEvent"), e);
                });
    }

    private void onAddTableGroupClicked() {
        eventViewModel.getTableGroups().add(new TableGroupViewModel());
        enableButtons(hasChanges());
    }
    
    private void onDiscardClicked() {
        if(UiHelper.showDiscardChangesDialog().blockingFirst()) {
            setExistingEvent(eventViewModel.getEvent());
        }
    }

    private void enableButtons(boolean enabled) {
        saveButton.setDisable(!enabled);
        discardButton.setDisable(!enabled);
    }

    private ObservableValue<HBox> createTableNumbersHbox(TableGroupViewModel group) {
        Label minus = new Label(" - ");
        
        TextField from = new TextField();
        
        from.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                from.setText(newValue.replaceAll("[^\\d]", ""));
            } 
            if(from.getText().trim().isEmpty()) {
                from.setText(String.valueOf(group.getStartNumber()));
            }
        });
        
        from.textProperty().bindBidirectional(group.getStartNumberProperty(), new NumberStringConverter());
        
        TextField to = new TextField();
        
        to.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                to.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if(to.getText().trim().isEmpty()) {
                to.setText(String.valueOf(group.getEndNumber()));
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
            if (!newValue.matches("\\d*")) {
                amount.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if(amount.getText().trim().isEmpty()) {
                amount.setText(String.valueOf(group.getEndNumber()));
            }
        });
        
        amount.textProperty().bindBidirectional(group.getPriceProperty(), new NumberStringConverter());
        
        HBox result = new HBox(amount, currency);
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
        enableButtons(hasChanges());
    }
    
}
