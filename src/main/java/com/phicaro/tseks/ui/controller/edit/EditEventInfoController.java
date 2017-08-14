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
import com.phicaro.tseks.util.Resources;
import com.phicaro.tseks.util.TimeTextField;
import com.phicaro.tseks.util.UiHelper;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author Placc
 */
public class EditEventInfoController implements IEditEventController {

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
        eventDateHBox.setMargin(timeTextField, new Insets(0, 10, 0, 10));
        
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
        eventDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            Date oldDate = UiHelper.parse(event.getDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(oldDate);
            
            calendar.set(newValue.getYear(), newValue.getMonthValue() - 1, newValue.getDayOfMonth());
            
            event.setDate(calendar.getTime());
        });
        
        timeTextField.setText(new SimpleDateFormat(Resources.getConfig("CFG_TimeFormat")).format(eventDate));
        timeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Date oldDate = UiHelper.parse(event.getDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(oldDate);
            
            calendar.set(Calendar.HOUR_OF_DAY, timeTextField.getHours());
            calendar.set(Calendar.MINUTE, timeTextField.getMinutes());
            calendar.set(Calendar.SECOND, timeTextField.getSeconds());
            
            event.setDate(calendar.getTime());
        });
    }
    
    public List<String> errors() {
        List<String> invalids = new ArrayList<>();
        
        if(eventNameEditText.getText().trim().isEmpty()) {
            invalids.add(Resources.getString("MSG_EmptyEventName"));
        }
        if(eventTitleEditText.getText().trim().isEmpty()) {
            invalids.add(Resources.getString("MSG_EmptyEventTitle"));
        }
        if(eventLocationEditText.getText().trim().isEmpty()) {
            invalids.add(Resources.getString("MSG_EmptyEventLocation"));
        }
        if(timeTextField.getText().trim().isEmpty()) {
            invalids.add(Resources.getString("MSG_EmptyEventTime"));
        }
        
        return invalids;
    }
    
    public List<String> warnings() {
        List<String> warnings = new ArrayList<>();
        
        if(UiHelper.parse(eventViewModel.getDate()).before(new Date())) {
            warnings.add(Resources.getString("MSG_EventInPast"));
        }
        
        return warnings;
    }
}
