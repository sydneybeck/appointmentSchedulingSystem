package sample.view_controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sample.model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class editAppointmentController implements Initializable {
    public TextField appointmentIDTextBox;
    public TextField appointmentTitleTextBox;
    public TextField appointmentDescriptionTextBox;
    public TextField appointmentTypeTextBox;
    public ComboBox<String> appointmentContactComboBox;
    public DatePicker appointmentStartDateBox;
    public DatePicker appointmentEndDateBox;
    public ComboBox<Integer> customerIDComboBox;
    public ComboBox<Integer> userIDComboBox;
    public TextField startTimeTextBox;
    public TextField endTimeTextBox;
    public TextField appointmentLocationTextBox;
    public Button clearButton;
    public Button saveButton;
    public Button backButton;

    /**
     * LAMBDA EXPRESSION: A lambda expression is used to disable the ability to pick days in the past from the
     * Date Pickers as dates for edited appointments. For the end date Date Picker, it also does not allow the user
     * to pick an end date from before the start date.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appointmentStartDateBox.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate newAppointmentStartDateBox, boolean empty) {
                super.updateItem(newAppointmentStartDateBox, empty);
                setDisable(empty || newAppointmentStartDateBox.isBefore(LocalDate.now()));
            }});

        appointmentEndDateBox.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate newAppointmentEndDateBox, boolean empty) {
                super.updateItem(newAppointmentEndDateBox, empty);
                setDisable(empty || newAppointmentEndDateBox.isBefore(LocalDate.now()) ||
                        newAppointmentEndDateBox.isBefore(appointmentStartDateBox.getValue()));
            }});
    }

    /**
     * METHOD DESCRIPTION: This method takes the selected appointment from the previous screen, performs time conversions
     * and formatting, and populates the input fields
     */
    public void populateAppointment(Appointment selectedAppointment) {
        try {
            /* Takes the stored start and end times, adds the UTC time zone, converts them to the user's time zone,
            extracts just the hours and minutes using the DateTimeFormatter, then converts that to a string
             */
            ZonedDateTime startUTC = selectedAppointment.getAppointmentStart().toInstant().atZone(ZoneOffset.UTC);
            ZonedDateTime endUTC = selectedAppointment.getAppointmentEnd().toInstant().atZone(ZoneOffset.UTC);
            ZonedDateTime localStart = startUTC.withZoneSameInstant(User.getUserTimeZone());
            ZonedDateTime localEnd = endUTC.withZoneSameInstant(User.getUserTimeZone());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String startString = localStart.format(formatter);
            String endString = localEnd.format(formatter);

            /* Populates the input fields. The Start and End Date Pickers are populated by converting the database time
            to local date time, then extracts the date with the toLocalDate() method
             */
            appointmentIDTextBox.setText(selectedAppointment.getAppointmentID().toString());
            appointmentTitleTextBox.setText(selectedAppointment.getAppointmentTitle());
            appointmentDescriptionTextBox.setText(selectedAppointment.getAppointmentDescription());
            appointmentLocationTextBox.setText(selectedAppointment.getAppointmentLocation());
            appointmentTypeTextBox.setText(selectedAppointment.getAppointmentType());
            appointmentStartDateBox.setValue(selectedAppointment.getAppointmentStart().toLocalDateTime().toLocalDate());
            appointmentEndDateBox.setValue(selectedAppointment.getAppointmentEnd().toLocalDateTime().toLocalDate());
            startTimeTextBox.setText(startString);
            endTimeTextBox.setText(endString);

            /* Populates the ComboBoxes with all available options, then selects the correct data associated with the
            selected appointment
             */
            appointmentContactComboBox.setItems(Contact.getAllContactNames());
            appointmentContactComboBox.getSelectionModel().select(selectedAppointment.getContactName());
            userIDComboBox.setItems(User.getAllUserID());
            userIDComboBox.getSelectionModel().select(selectedAppointment.getUserID());
            customerIDComboBox.setItems(Customer.getAllCustomerIDs());
            customerIDComboBox.getSelectionModel().select(selectedAppointment.getCustomerID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * METHOD DESCRIPTION: This method clears any values from the TextBoxes, ComboBoxes, and Date Pickers
     */
    public void OnClickClearButton() {
        appointmentTitleTextBox.clear();
        appointmentDescriptionTextBox.clear();
        appointmentLocationTextBox.clear();
        appointmentTypeTextBox.clear();
        startTimeTextBox.clear();
        endTimeTextBox.clear();
        appointmentContactComboBox.getSelectionModel().clearSelection();
        customerIDComboBox.getSelectionModel().clearSelection();
        userIDComboBox.getSelectionModel().clearSelection();
        appointmentStartDateBox.getEditor().clear();
        appointmentEndDateBox.getEditor().clear();
    }

    /**
     * METHOD DESCRIPTION: This method gathers all of the inputted values, performs time conversions and logic checks
     * that ensure the inputted times are within business hours and do not overlap with another appointment for the
     * same customer, and calls the editAppointment database method that updates the selected appointment values into the
     * database
     */
    public void OnClickSaveButton(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {
        Boolean checkBusinessHours = true;
        Boolean checkAppointmentOverlap = true;
        Boolean checkStartTime = true;
        Boolean checkEndTime = true;

        // Gathers inputted data
        Integer appointmentID = Integer.parseInt(appointmentIDTextBox.getText());
        String appointmentTitle = appointmentTitleTextBox.getText();
        String appointmentDescription = appointmentDescriptionTextBox.getText();
        String appointmentLocation = appointmentLocationTextBox.getText();
        String appointmentType = appointmentTypeTextBox.getText();
        Integer customerID = customerIDComboBox.getValue();
        Integer userID = userIDComboBox.getValue();
        String contactName = appointmentContactComboBox.getValue();
        LocalDate appointmentStartDate = appointmentStartDateBox.getValue();
        LocalDate appointmentEndDate = appointmentEndDateBox.getValue();
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        ZonedDateTime zonedStart = null;
        ZonedDateTime zonedEnd = null;

        /* Calls the database method matchContactID to match the inputted contact name to the corresponding contactID
        and assigns it to the contactID variable to be inserted into the database
         */
        Integer contactID = Contact.matchContactID(contactName);

        // Initializes the DateTimeFormatter for inputted start and end times
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        /* Combines the entered start date and start time and calls the checkStartTime method to ensure the start time
        was entered correctly, and outputs an error if it is not formatted correctly
        */
        try {
            startDateTime = LocalDateTime.of(appointmentStartDateBox.getValue(), LocalTime.parse(startTimeTextBox.getText(), formatter));
            checkStartTime = true;
        } catch (DateTimeParseException error) {
            checkStartTime = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please format your times in 24 hour time and HH:MM");
            alert.showAndWait();
        }

        /* Combines the entered end date and end time and calls the checkEndTime method to ensure the end time
        was entered correctly, and outputs an error if it is not formatted correctly
        */
        try {
            endDateTime = LocalDateTime.of(appointmentEndDateBox.getValue(), LocalTime.parse(endTimeTextBox.getText(), formatter));
            checkEndTime = true;
        } catch (DateTimeParseException error) {
            checkEndTime = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please format your times in 24 hour time and HH:MM");
            alert.showAndWait();
        }

        /* Calls the checkBusinessHours and checkAppointmentOverlap methods with the inputted and formatted data as
        parameters and returns a boolean for both methods
         */
        checkBusinessHours = checkBusinessHours(startDateTime, endDateTime, appointmentStartDate);
        checkAppointmentOverlap = checkAppointmentOverlap(customerID, startDateTime, endDateTime, appointmentStartDate,
                appointmentID);

        // Checks for blank input fields and outputs an error if any are found
        if (appointmentTitle.isBlank() || appointmentDescription.isBlank() || appointmentLocation.isBlank() ||
                appointmentType.isBlank() || customerID == null || userID == null || contactName == null ||
                appointmentStartDate == null || appointmentEndDate == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please enter a value into all of the fields");
            alert.showAndWait();

            return;
        }

        // Outputs an error if checkBusinessHours returns false
        if (!checkBusinessHours) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Appointment must be scheduled during the business hours of 08:00 - 22:00 EST");
            alert.showAndWait();
        }

        // Outputs an error if checkAppointmentOverlap returns false
        if (!checkAppointmentOverlap) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("There is already an appointment for that customer during that time");
            alert.showAndWait();
        }

        // Outputs an error if any logic checks are false in order to stop the method
        if (!checkAppointmentOverlap || !checkBusinessHours || !checkStartTime || !checkEndTime) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("There is an error somewhere");
            alert.showAndWait();
        }

        /* If all logic checks return true, the users time zone is added to the start and end dateTimes, then
        the zonedStart and zonedEnd times are converted from the users time zone to the databases UTC timezone

        Calls the addAppointment method and passes in all of the inputted and formatted data to add the appointment
        to the database, then reloads the Appointment Page
         */
        else {
            zonedStart = ZonedDateTime.of(startDateTime, User.getUserTimeZone());
            zonedEnd = ZonedDateTime.of(endDateTime, User.getUserTimeZone());

            zonedStart = zonedStart.withZoneSameInstant(ZoneOffset.UTC);
            zonedEnd = zonedEnd.withZoneSameInstant(ZoneOffset.UTC);

            Appointment.editAppointment(appointmentID, appointmentTitle, appointmentDescription, appointmentLocation,
                    appointmentType, zonedStart, zonedEnd, customerID, userID, contactID);

            Parent parent = FXMLLoader.load(getClass().getResource("appointmentPage.fxml"));
            Scene scene = new Scene(parent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        }
    }

    /**
     * METHOD DESCRIPTION: This method takes the inputted start and end dateTimes, converts the business hours from ETC
     * to the users time zone, checks if they overlap outside of the business hours, and returns a boolean that is
     * used to continue or generate an error in the OnClickSaveButton Method
     */
    public boolean checkBusinessHours (LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDate appointmentStartDate) {
        // Adds the users time zone to the inputted start and end times
        ZonedDateTime zonedStartTime = ZonedDateTime.of(startDateTime, User.getUserTimeZone());
        ZonedDateTime zonedEndTime = ZonedDateTime.of(endDateTime, User.getUserTimeZone());

        // Converts the business hours from UTC to the users time zone
        ZonedDateTime businessStartTime = ZonedDateTime.of(appointmentStartDate, LocalTime.of(8, 0),
                ZoneId.of("America/New_York"));
        ZonedDateTime businessEndTime = ZonedDateTime.of(appointmentStartDate, LocalTime.of(22, 0),
                ZoneId.of("America/New_York"));

        // Compares the start and end times for overlap and returns a boolean
        if (zonedStartTime.isBefore(businessStartTime) | zonedStartTime.isAfter(businessEndTime) |
                zonedEndTime.isBefore(businessStartTime) | zonedEndTime.isAfter(businessEndTime) |
                zonedStartTime.isAfter(zonedEndTime)) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * METHOD DESCRIPTION: This method takes the inputted start and end dateTimes and the customerID, generates an
     * Observable List of appointments that have the same start date and customerID, checks if the list is empty, and
     * if the list is empty, it checks for overlap between the appointment start and end times, and returns a boolean
     * that is used to continue or generate an error in the OnClickSaveButton Method
     */
    public Boolean checkAppointmentOverlap (Integer customerID, LocalDateTime appointmentStart, LocalDateTime appointmentEnd,
                                            LocalDate startDate, Integer appointmentID) throws SQLException {
        /* The appointmentID must be passed in so it can be excluded from the returned Observable List or else it will
        always return the original appointment data and compare it against the appointment being edited. This will cause
        this method to return false every time, unless the appointment is edited to have completely different start and
        end times
         */
        ObservableList<Appointment> sameDayAppointmentList = Appointment.getAppointmentsByDateAndCustomer(startDate,
                customerID, appointmentID);

        if(sameDayAppointmentList.isEmpty()) {
            return true;
        }

        /* Converts the appointment start and end times from the Observable List to LocalDateTime so the appointment
         * times can be compared
         */
        else {
            for (Appointment checkedAppointment : sameDayAppointmentList) {
                LocalDateTime checkingStart = checkedAppointment.getAppointmentStart().toLocalDateTime();
                LocalDateTime checkingEnd = checkedAppointment.getAppointmentEnd().toLocalDateTime();

                if(checkingStart.isBefore(appointmentStart) & checkingEnd.isAfter(appointmentEnd)) {
                    return false;
                }
                if (checkingStart.isBefore(appointmentEnd) & checkingStart.isAfter(appointmentStart)) {
                    return false;
                }
                if (checkingEnd.isBefore(appointmentEnd) & checkingEnd.isAfter(appointmentStart)) {
                    return false;
                }
                if(checkingStart.isEqual(appointmentStart)) {
                    return false;
                }
                if (checkingEnd.isEqual(appointmentEnd)) {
                    return false;
                }
                else {
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * METHOD DESCRIPTION: This method returns the user to the Appointment Page when they click the "Back" button
     */
    public void OnClickBackButton(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("appointmentPage.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}
