/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.edit;

import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.model.entities.Location;
import com.phicaro.tseks.model.entities.TableGroup;
import com.phicaro.tseks.model.services.EventService;
import com.phicaro.tseks.model.services.TableService;
import com.phicaro.tseks.ui.controller.INavigationController;
import com.phicaro.tseks.ui.controller.MainController;
import com.phicaro.tseks.ui.models.EventViewModel;
import com.phicaro.tseks.ui.models.TableGroupViewModel;
import com.phicaro.tseks.util.Resources;
import com.phicaro.tseks.util.UiHelper;
import com.phicaro.tseks.util.exceptions.LifecycleException;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author Placc
 */
public class EditEventController implements IEditEventController, INavigationController {

    @FXML
    private Button saveButton;
    @FXML
    private Button discardButton;
    @FXML
    private BorderPane eventInfo;
    @FXML 
    private BorderPane tableGroups;
    
    @FXML
    private EditEventInfoController eventInfoController;
    @FXML
    private EditEventTableGroupController tableGroupsController;
    
    //Model
    private EventViewModel eventViewModel = new EventViewModel();
    private InvalidationListener changeListener = o -> enableButtons(hasChanges());
    
    //Subscription
    private Disposable addedDisposable;
    private Disposable removedDisposable;
    
    public void setEvent(EventViewModel eventViewModel) {
        enableButtons(false);
        
        this.eventViewModel = eventViewModel;
        
        eventViewModel.getNameProperty().addListener(changeListener);
        eventViewModel.getDateProperty().addListener(changeListener);
        eventViewModel.getDescriptionProperty().addListener(changeListener);
        eventViewModel.getLocationProperty().addListener(changeListener);
        eventViewModel.getTableGroupsProperty().addListener(changeListener);
        eventViewModel.getTableGroupsProperty().addListener(
                (ListChangeListener.Change<? extends TableGroupViewModel> c) -> {
                    while(c.next()) {
                        c.getAddedSubList().forEach(group -> bindTableGroupViewModel(group));
                    }
                });
        eventViewModel.getTableGroups().forEach(this::bindTableGroupViewModel);
        
        invalidateControllers();
    }
    
    private void bindTableGroupViewModel(TableGroupViewModel viewModel) {
        viewModel.getStartNumberProperty().addListener(changeListener);
        viewModel.getEndNumberProperty().addListener(changeListener);
        viewModel.getSeatsProperty().addListener(changeListener);
        viewModel.getPriceProperty().addListener(changeListener);
        viewModel.getNumberOfTablesProperty().addListener(changeListener);
    }
    
    private void invalidateControllers() {
        eventInfoController.setEvent(eventViewModel);
        tableGroupsController.setEvent(eventViewModel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
        saveButton.setText(Resources.getString("LAB_Save"));
        saveButton.setGraphic(new ImageView(Resources.getImage("save.png", Resources.ImageSize.NORMAL)));
        saveButton.setOnAction(e -> onSaveClicked());
        
        discardButton.setText(Resources.getString("LAB_Discard"));
        discardButton.setGraphic(new ImageView(Resources.getImage("clear.png", Resources.ImageSize.NORMAL)));
        discardButton.setOnAction(e -> onDiscardClicked());
        
        EventService eventService = MainController.instance().getTseksApp().getEventService();
        addedDisposable = eventService.eventAdded().subscribe(e -> handleEventChanges(e, true));
        removedDisposable = eventService.eventRemoved().subscribe(e -> handleEventChanges(e, false));
        
        invalidateControllers();
    }    

    private void handleEventChanges(Event event, boolean added) {
        if((added ^ hasChanges()) && eventViewModel.getModel() != null && event.getId().equals(eventViewModel.getModel().getId()) && !eventViewModel.getModel().equals(event)) {
            MainController.instance().navigateBack(EditEventController.this);
            UiHelper.showException(Resources.getString("LAB_LifecycleError"), new LifecycleException());
        }
    }
    
    private boolean hasChanges() {
        if(eventViewModel.getModel() == null) {
            return !eventViewModel.getName().trim().isEmpty() ||
                    !eventViewModel.getLocation().trim().isEmpty() ||
                    !eventViewModel.getDate().trim().isEmpty() || 
                    !eventViewModel.getDescription().trim().isEmpty() ||
                    !eventViewModel.getTitle().trim().isEmpty() ||
                    !eventViewModel.getTableGroups().isEmpty();
        } else {
            return !eventViewModel.matches(eventViewModel.getModel());
        }
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
        saveEvent()
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

    private void onDiscardClicked() {
        if(UiHelper.showDiscardChangesDialog().blockingFirst()) {
            if(eventViewModel.getModel() != null) {
                setEvent(new EventViewModel(eventViewModel.getModel()));
            } else {
                setEvent(new EventViewModel());
            }
        }
    }

    private void enableButtons(boolean enabled) {
        saveButton.setDisable(!enabled);
        discardButton.setDisable(!enabled);
    }

    @Override
    public List<String> errors() {
        List<String> invalidChanges = new ArrayList<>();
        
        invalidChanges.addAll(eventInfoController.errors());
        invalidChanges.addAll(tableGroupsController.errors());
        
        return invalidChanges;
    }
    
    @Override 
    public List<String> warnings() {
        List<String> warnings = new ArrayList<>();
        
        warnings.addAll(eventInfoController.warnings());
        warnings.addAll(tableGroupsController.warnings());
        
        return warnings;
    }
    
    private Completable saveEvent() {
        if(!hasChanges()) {
            return Completable.complete();
        }
        
        List<String> invalidChanges = errors();
            
        if(!invalidChanges.isEmpty()) {
            return Completable.error(new Exception(invalidChanges.stream().reduce("", (s1, s2) -> s1 + "\n" + s2)));
        }
        
        List<String> warnings = warnings();
        
        if(!warnings.isEmpty() && !UiHelper.showSaveWarningDialog(warnings).blockingFirst()) {
            return Completable.complete();
        }
        
        EventService eventService = MainController.instance().getTseksApp().getEventService();
        Single<Event> event;
        
        if(eventViewModel.getModel() != null) {
            event = Single.just(eventViewModel.getModel())
                        .doOnSuccess(e -> {
                            e.setName(eventViewModel.getName());
                            e.setTitle(eventViewModel.getTitle());
                            e.setLocation(new Location(eventViewModel.getLocation()));
                            e.setDescription(eventViewModel.getDescription());
                            e.setDate(UiHelper.parse(eventViewModel.getDate()));
                            
                            e.clearTableGroups();
                        });
        } else {
            event = eventService.createNewEvent(eventViewModel.getName(), eventViewModel.getTitle(), UiHelper.parse(eventViewModel.getDate()), new Location(eventViewModel.getLocation()), eventViewModel.getDescription());
        }

        return event
                .doOnSuccess(e -> {
                    eventViewModel.getTableGroups().stream()
                    .forEach(model -> {
                        TableGroup tableGroup = TableService.createTableGroup(model.getSeats(), model.getPrice());
                        TableService.setTablesRange(tableGroup, model.getStartNumber(), model.getEndNumber());
                        e.addTableGroup(tableGroup);
                    });
                    
                    setEvent(new EventViewModel(e));
                })
                .flatMapCompletable(e -> eventService.updateEvent(e));
    }
}
