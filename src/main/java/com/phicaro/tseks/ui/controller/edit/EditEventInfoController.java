/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phicaro.tseks.ui.controller.edit;

/**
 *
 * @author Placc
 */
import com.phicaro.tseks.ui.models.EventViewModel;
import com.phicaro.tseks.ui.util.UiHelper;
import com.phicaro.tseks.ui.util.views.TimeTextField;
import com.phicaro.tseks.util.Resources;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * FXML Controller class
 *
 * @author Placc
 */
public class EditEventInfoController implements IEditEventController, Initializable {

    @FXML
    private Label eventNameLabel;
    @FXML
    private Label eventDescLabel;
    @FXML
    private Label eventDateLabel;
    @FXML
    private Label eventLocationLabel;
    @FXML
    private Label eventTitleLabel;
    @FXML
    private TextField eventNameEditText;
    @FXML
    private TextField eventTitleEditText;
    @FXML
    private TextField eventDescEditText;
    @FXML
    private TextField eventLocationEditText;
    @FXML
    private DatePicker eventDatePicker;
    @FXML
    private HBox eventDateHBox;

    private TimeTextField timeTextField;

    private EventViewModel eventViewModel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        timeTextField = new TimeTextField();
        eventDateHBox.getChildren().add(timeTextField);
        HBox.setHgrow(timeTextField, Priority.ALWAYS);
        HBox.setMargin(timeTextField, new Insets(0, 10, 0, 10));

        eventNameLabel.setText(Resources.getString("LAB_EventName"));

        eventTitleLabel.setText(Resources.getString("LAB_EventTitle"));
        eventDescLabel.setText(Resources.getString("LAB_Description"));
        eventDateLabel.setText(Resources.getString("LAB_DateTime"));
        eventLocationLabel.setText(Resources.getString("LAB_Location"));
    }

    public void setEvent(EventViewModel event) {
        eventViewModel = event;

        eventNameEditText.textProperty().bindBidirectional(event.getNameProperty());
        eventTitleEditText.textProperty().bindBidirectional(event.getTitleProperty());
        eventDescEditText.textProperty().bindBidirectional(event.getDescriptionProperty());
        eventLocationEditText.textProperty().bindBidirectional(event.getLocationProperty());

        Date eventDate = UiHelper.parse(event.getDate());

        eventDatePicker.setValue(UiHelper.asLocalDate(eventDate));
        eventDatePicker.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Date oldDate = UiHelper.parse(event.getDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(oldDate);

            Date newDate = UiHelper.parse(newValue);

            if (newDate != null) {
                eventDatePicker.getEditor().setStyle("-fx-text-inner-color: black");

                LocalDate newLocal = UiHelper.asLocalDate(newDate);

                calendar.set(newLocal.getYear(), newLocal.getMonthValue() - 1, newLocal.getDayOfMonth());
                event.setDate(calendar.getTime());
            } else {
                eventDatePicker.getEditor().setStyle("-fx-text-inner-color: red");
            }
        });

        timeTextField.setText(new SimpleDateFormat(Resources.getConfig("CFG_TimeFormat")).format(eventDate));
        timeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Date oldDate = UiHelper.parse(event.getDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(oldDate);

            calendar.set(Calendar.HOUR_OF_DAY, timeTextField.getHours());
            calendar.set(Calendar.MINUTE, timeTextField.getMinutes());
            calendar.set(Calendar.SECOND, 0);

            event.setDate(calendar.getTime());
        });
    }

    public List<String> errors() {
        List<String> invalids = new ArrayList<>();

        if (eventNameEditText.getText().trim().isEmpty()) {
            invalids.add(Resources.getString("MSG_EmptyEventName"));
        }
        if (eventTitleEditText.getText().trim().isEmpty()) {
            invalids.add(Resources.getString("MSG_EmptyEventTitle"));
        }
        if (eventLocationEditText.getText().trim().isEmpty()) {
            invalids.add(Resources.getString("MSG_EmptyEventLocation"));
        }
        if (timeTextField.getText().trim().isEmpty()) {
            invalids.add(Resources.getString("MSG_EmptyEventTime"));
        }
        if (UiHelper.parse(eventDatePicker.getEditor().getText()) == null) {
            invalids.add(Resources.getString("MSG_InvalidEventDate"));
        }

        return invalids;
    }

    public List<String> warnings() {
        List<String> warnings = new ArrayList<>();

        if (UiHelper.parse(eventViewModel.getDate()).before(new Date())) {
            warnings.add(Resources.getString("MSG_EventInPast"));
        }

        return warnings;
    }
}
