/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.edit;

import com.phicaro.tseks.ui.models.EventViewModel;
import com.phicaro.tseks.ui.models.TableCategoryViewModel;
import com.phicaro.tseks.ui.util.NullTableViewSelectionModel;
import com.phicaro.tseks.ui.util.UiHelper;
import com.phicaro.tseks.util.Resources;
import com.sun.javafx.scene.traversal.Algorithm;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.scene.traversal.TraversalContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.converter.NumberStringConverter;

/**
 *
 * @author Placc
 */
public class EditEventTableCategoryController implements IEditEventController {

    private TableView<TableCategoryViewModel> eventTableView;
    private TableColumn<TableCategoryViewModel, HBox> tableNumberColumn;
    private TableColumn<TableCategoryViewModel, HBox> seatsColumn;
    private TableColumn<TableCategoryViewModel, HBox> categoryColumn;
    private TableColumn<TableCategoryViewModel, HBox> optionsColumn;

    //Model
    private EventViewModel eventViewModel;

    public EditEventTableCategoryController(TableView<TableCategoryViewModel> eventTableView, Button addTableGroupButton) {
        this.eventTableView = eventTableView;

        this.tableNumberColumn = (TableColumn<TableCategoryViewModel, HBox>) eventTableView.getColumns().get(0);
        this.seatsColumn = (TableColumn<TableCategoryViewModel, HBox>) eventTableView.getColumns().get(1);
        this.categoryColumn = (TableColumn<TableCategoryViewModel, HBox>) eventTableView.getColumns().get(2);
        this.optionsColumn = (TableColumn<TableCategoryViewModel, HBox>) eventTableView.getColumns().get(3);

        addTableGroupButton.setText(Resources.getString("LAB_NewTableGroup"));
        addTableGroupButton.setGraphic(new ImageView(Resources.getImage("add_outline.png", Resources.ImageSize.NORMAL)));
        addTableGroupButton.setOnAction(e -> onAddTableGroupClicked());

        eventTableView.setPlaceholder(new Label(Resources.getString("LAB_NoTablesAvailable")));
        eventTableView.setSelectionModel(new NullTableViewSelectionModel(eventTableView));
        eventTableView.getSortOrder().setAll(tableNumberColumn);

        tableNumberColumn.setText(Resources.getString("LAB_TableNumbers"));
        tableNumberColumn.comparatorProperty().set((HBox h1, HBox h2) -> {
            TextField t1 = (TextField) h1.getChildren().get(0);
            TextField t2 = (TextField) h2.getChildren().get(0);

            return Integer.parseInt(t2.getText()) - Integer.parseInt(t1.getText());
        });

        seatsColumn.setText(Resources.getString("LAB_Seats"));
        seatsColumn.setSortable(false);
        categoryColumn.setText(Resources.getString("LAB_Price"));
        categoryColumn.setSortable(false);
        optionsColumn.setSortable(false);
    }

    public void setEvent(EventViewModel event) {
        eventViewModel = event;

        eventTableView.setItems(event.getTableGroups());

        tableNumberColumn.setCellValueFactory(group -> createTableNumbersHbox(group.getValue()));
        seatsColumn.setCellValueFactory(group -> createSeatsHbox(group.getValue()));
        categoryColumn.setCellValueFactory(group -> createCategoryHBox(group.getValue()));
        optionsColumn.setCellValueFactory(group -> createOptionsHbox(group.getValue()));
    }

    public List<String> errors() {
        List<String> invalids = new ArrayList<>();

        if (eventViewModel.getTableGroups().stream().anyMatch(group -> group.getEndNumber() < group.getStartNumber())) {
            invalids.add(Resources.getString("MSG_EndnumberBeforeStartnumber"));
        }
        if (eventViewModel.getTableGroups().stream().anyMatch(group -> group.getSeats() <= 0)) {
            invalids.add(Resources.getString("MSG_SeatsZero"));
        }
        if (eventViewModel.getTableGroups().stream().anyMatch(group
                -> eventViewModel.getTableGroups().stream().filter(g -> !g.equals(group)).anyMatch(g
                        -> UiHelper.isIntersection(g.getStartNumber(), g.getEndNumber(), group.getStartNumber(), group.getEndNumber())))) {
            invalids.add(Resources.getString("MSG_GroupIntersections"));
        }

        return invalids;
    }

    public List<String> warnings() {
        List<String> warnings = new ArrayList<>();

        //Missing table numbers
        List<String> missingTables = getMissingTables();

        if (!missingTables.isEmpty()) {
            String message = Resources.getString("MSG_TableNumbersXNotPresent", UiHelper.combine(missingTables));
            warnings.add(message);
        }

        //Price 0
        if (eventViewModel.getTableGroups().stream().anyMatch(group -> group.getPrice() <= 0)) {
            warnings.add(Resources.getString("MSG_PriceZero"));
        }

        return warnings;
    }

    private List<String> getMissingTables() {
        List<String> missingTables = new ArrayList<>();
        int max = eventViewModel.getTableGroups().stream().map(group -> group.getEndNumber()).max(Comparator.naturalOrder()).orElse(0);
        int interval = 0;

        for (int number = 1; number <= max; number++) {
            final int n = number;
            boolean found = eventViewModel.getTableGroups().stream().anyMatch(group -> group.getStartNumber() <= n && n <= group.getEndNumber());

            if (!found) {
                if (interval <= 0) {
                    interval = n;
                }
            } else if (interval > 0) {
                missingTables.add(interval + " - " + Math.max(interval, n - 1));
                interval = 0;
            }
        }

        if (interval > 0) {
            missingTables.add(interval + " - " + interval);
        }

        return missingTables;
    }

    private void onAddTableGroupClicked() {
        List<TableCategoryViewModel> tableGroups = eventViewModel.getTableGroups();

        int seats = 1;
        double price = 0.0;
        int numberOfTables = 1;

        if (!tableGroups.isEmpty()) {
            TableCategoryViewModel lastGroup = tableGroups.get(tableGroups.size() - 1);
            seats = lastGroup.getSeats();
            price = lastGroup.getPrice();
            numberOfTables = lastGroup.getNumberOfTables();
        }

        int startTableNumber = 1 + tableGroups.stream().map(group -> group.getEndNumber()).max(Comparator.naturalOrder()).orElse(0);

        TableCategoryViewModel viewModel = new TableCategoryViewModel(startTableNumber, numberOfTables, seats, price);

        eventViewModel.getTableGroups().add(viewModel);
    }

    private ObservableValue<HBox> createTableNumbersHbox(TableCategoryViewModel group) {
        Label minus = new Label(" - ");

        TextField from = new TextField();

        from.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                from.requestFocus();
                from.boundsInParentProperty().removeListener(this);
            }
        });

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

    private ObservableValue<HBox> createCategoryHBox(TableCategoryViewModel group) {
        Label currency = new Label(Resources.getString("LAB_Currency"));

        TextField amount = new TextField();

        amount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]+((\\,|\\.)[0-9]{0,2})?")) {
                String newVal = newValue.replaceAll("[^\\d\\.\\,]", "");
                int pointIdx = -1;

                if (newVal.contains(",") && newVal.contains(".")) {
                    pointIdx = Math.min(newVal.indexOf("."), newValue.indexOf(","));
                } else if (newVal.contains(",")) {
                    pointIdx = newVal.indexOf(",");
                } else if (newVal.contains(".")) {
                    pointIdx = newVal.indexOf(".");
                }

                if (pointIdx == 0) {
                    newVal = "0" + newVal;
                    pointIdx++;
                }

                int endIdx = pointIdx + 1;

                while (endIdx < Math.min(pointIdx + 3, newVal.length()) && endIdx > 0) {
                    if (!String.valueOf(newVal.charAt(endIdx)).matches("[0-9]")) {
                        break;
                    }
                    endIdx++;
                }

                amount.setText(newVal.substring(0, endIdx));
            }
        });

        amount.textProperty().bindBidirectional(group.getPriceProperty(), new NumberStringConverter());

        Algorithm algorithm = new Algorithm() {
            @Override
            public Node select(Node owner, Direction dir, TraversalContext context) {
                int groupIdx = eventViewModel.getTableGroups().indexOf(group);

                if (groupIdx < eventViewModel.getTableGroups().size() - 1) {
                    HBox data = (HBox) tableNumberColumn.getCellData(groupIdx + 1);
                    TextField from = (TextField) data.getChildren().get(0);
                    return from;
                }

                return null;
            }

            @Override
            public Node selectFirst(TraversalContext context) {
                return context.selectFirstInParent(context.getRoot());
            }

            @Override
            public Node selectLast(TraversalContext context) {
                return context.selectLastInParent(context.getRoot());
            }
        };

        ParentTraversalEngine engine = new ParentTraversalEngine(amount, algorithm);
        amount.setImpl_traversalEngine(engine);

        amount.setOnKeyPressed(key -> {
            int groupIdx = eventViewModel.getTableGroups().indexOf(group);

            if (key.getCode().equals(KeyCode.TAB) && groupIdx >= eventViewModel.getTableGroups().size() - 1) {
                key.consume();
                onAddTableGroupClicked();
            }
        });

        HBox result = new HBox(amount, currency);
        result.alignmentProperty().setValue(Pos.CENTER);

        return new SimpleObjectProperty<>(result);
    }

    private ObservableValue<HBox> createSeatsHbox(TableCategoryViewModel group) {
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

    private ObservableValue<HBox> createOptionsHbox(TableCategoryViewModel group) {
        Image deleteImage = Resources.getImage("delete.png", Resources.ImageSize.NORMAL);

        ColorAdjust colorAdjust = UiHelper.getColorAdjust(Color.STEELBLUE);

        ImageView deleteView = new ImageView(deleteImage);
        deleteView.setEffect(colorAdjust);

        Button delete = new Button("", deleteView);
        delete.getStyleClass().add("back-btn");
        delete.setFocusTraversable(false);
        delete.setTooltip(new Tooltip(Resources.getString("LAB_Delete")));

        delete.setOnAction(e -> deleteTableGroupClicked(group));

        return new SimpleObjectProperty<>(new HBox(delete));
    }

    private void deleteTableGroupClicked(TableCategoryViewModel tableGroup) {
        eventViewModel.getTableGroups().remove(tableGroup);
    }
}
