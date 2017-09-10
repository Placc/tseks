/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.edit;

import com.phicaro.tseks.model.EventService;
import com.phicaro.tseks.model.entities.Event;
import com.phicaro.tseks.model.entities.Location;
import com.phicaro.tseks.model.entities.PriceCategory;
import com.phicaro.tseks.model.entities.TableCategory;
import com.phicaro.tseks.ui.controller.INavigationController;
import com.phicaro.tseks.ui.controller.MainController;
import com.phicaro.tseks.ui.controller.components.PreviewController;
import com.phicaro.tseks.ui.controller.exception.LifecycleException;
import com.phicaro.tseks.ui.models.EventViewModel;
import com.phicaro.tseks.ui.models.TableCategoryViewModel;
import com.phicaro.tseks.ui.util.UiHelper;
import com.phicaro.tseks.util.Resources;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
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
    private PreviewController previewController;
    @FXML
    private EditEventInfoController eventInfoController;
    @FXML
    private EditEventTableCategoryController tableGroupsController;

    //Model
    private EventViewModel eventViewModel;

    private InvalidationListener eventChanged = o -> {
        enableButtons(hasChanges());
    };

    private InvalidationListener categoryChanged = o -> {
        previewController.onChanged();
        eventChanged.invalidated(o);
    };

    //Subscription
    private Disposable addedDisposable;
    private Disposable removedDisposable;

    public void setEvent(EventViewModel eventViewModel) {
        enableButtons(false);

        this.eventViewModel = eventViewModel;

        eventViewModel.getNameProperty().addListener(eventChanged);
        eventViewModel.getTitleProperty().addListener(eventChanged);
        eventViewModel.getDateProperty().addListener(eventChanged);
        eventViewModel.getDescriptionProperty().addListener(eventChanged);
        eventViewModel.getLocationProperty().addListener(eventChanged);
        eventViewModel.getTableGroupsProperty().addListener(categoryChanged);
        eventViewModel.getTableGroupsProperty().addListener((ListChangeListener.Change<? extends TableCategoryViewModel> c) -> {
            while (c.next()) {
                c.getAddedSubList().forEach(group -> bindTableGroupViewModel(group));
            }
        });
        eventViewModel.getTableGroups().forEach(this::bindTableGroupViewModel);

        invalidateControllers();
    }

    private void bindTableGroupViewModel(TableCategoryViewModel viewModel) {
        viewModel.getStartNumberProperty().addListener(categoryChanged);
        viewModel.getEndNumberProperty().addListener(categoryChanged);
        viewModel.getSeatsProperty().addListener(categoryChanged);
        viewModel.getPriceProperty().addListener(categoryChanged);
        viewModel.getNumberOfTablesProperty().addListener(categoryChanged);
    }

    private void invalidateControllers() {
        previewController.setEvent(eventViewModel);
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

        setEvent(new EventViewModel());

        EventService eventService = MainController.instance().getTseksApp().getEventService();
        addedDisposable = eventService.eventAdded().subscribe(e -> handleEventChanges(e, true));
        removedDisposable = eventService.eventRemoved().subscribe(e -> handleEventChanges(e, false));
    }

    private void handleEventChanges(Event event, boolean added) {
        if ((added ^ hasChanges()) && eventViewModel.getModel() != null && event.getId().equals(eventViewModel.getModel().getId()) && !eventViewModel.getModel().equals(event)) {
            MainController.instance().navigateAwayFrom(EditEventController.this);
            UiHelper.showException(Resources.getString("LAB_LifecycleError"), new LifecycleException());
        }
    }

    private boolean hasChanges() {
        if (eventViewModel.getModel() == null) {
            return !eventViewModel.getName().trim().isEmpty()
                    || !eventViewModel.getLocation().trim().isEmpty()
                    || !eventViewModel.getDate().trim().isEmpty()
                    || !eventViewModel.getDescription().trim().isEmpty()
                    || !eventViewModel.getTitle().trim().isEmpty()
                    || !eventViewModel.getTableGroups().isEmpty();
        } else {
            return !eventViewModel.matches(eventViewModel.getModel());
        }
    }

    @Override
    public Single<Boolean> onNavigateAway() {
        if (hasChanges() && !UiHelper.showDiscardChangesDialog().blockingFirst()) {
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
        if (UiHelper.showDiscardChangesDialog().blockingFirst()) {
            if (eventViewModel.getModel() != null) {
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
        if (!hasChanges()) {
            return Completable.complete();
        }

        List<String> invalidChanges = errors();

        if (!invalidChanges.isEmpty()) {
            return Completable.error(new Exception(invalidChanges.stream().reduce("", (s1, s2) -> s1 + "\n" + s2)));
        }

        List<String> warnings = warnings();

        if (!warnings.isEmpty() && !UiHelper.showSaveWarningDialog(warnings).blockingFirst()) {
            return Completable.complete();
        }

        EventService eventService = MainController.instance().getTseksApp().getEventService();
        Single<Event> event;

        if (eventViewModel.getModel() != null) {
            event = Single.just(eventViewModel.getModel())
                    .doOnSuccess(e -> {
                        e.setName(eventViewModel.getName());
                        e.setTitle(eventViewModel.getTitle());
                        e.setLocation(new Location(eventViewModel.getLocation()));
                        e.setDescription(eventViewModel.getDescription());
                        e.setDate(UiHelper.parse(eventViewModel.getDate()));
                    });
        } else {
            event = eventService.createNewEvent(eventViewModel.getName(), eventViewModel.getTitle(), UiHelper.parse(eventViewModel.getDate()), new Location(eventViewModel.getLocation()), eventViewModel.getDescription());
        }

        return event
                .doOnSuccess(e -> {
                    List<TableCategory> categories = e.getTableCategories();
                    categories.stream()
                            .filter(category -> !eventViewModel.getTableGroups().stream()
                            .anyMatch(model -> model.getModel() != null && model.getModel().getId().equals(category.getId())))
                            .forEach(category -> e.removeTableCategory(category));

                    eventViewModel.getTableGroups().stream()
                            .forEach(model -> {
                                if (model.getModel() == null || e.getTableCategories().stream().noneMatch(category -> category.getId().equals(model.getModel().getId()))) {
                                    TableCategory tableGroup = new TableCategory(e, model.getSeats(), new PriceCategory(model.getPrice()), model.getStartNumber(), model.getEndNumber());
                                    e.addTableCategory(tableGroup);
                                    return;
                                }

                                Optional<TableCategory> optional = e.getTableCategories().stream().filter(category -> category.getId().equals(model.getModel().getId())).findAny();

                                if (optional.isPresent()) {
                                    TableCategory tableGroup = optional.get();
                                    tableGroup.setMaxTableNumber(model.getEndNumber());
                                    tableGroup.setMinTableNumber(model.getStartNumber());
                                    tableGroup.setSeatsNumber(model.getSeats());
                                    tableGroup.setPrice(new PriceCategory(model.getPrice()));
                                }
                            });

                    setEvent(new EventViewModel(e));
                })
                .flatMapCompletable(e -> eventService.updateEvent(e));
    }
}
