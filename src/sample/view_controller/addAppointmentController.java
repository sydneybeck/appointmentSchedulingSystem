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
import java.sql.Time;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

/**
 * CLASS DESCRIPTION: This is the class that controls the Add Appointment screen
 */

public class addAppointmentController implements Initializable {
    public Button clearButton;
    public Button saveButton;
    public Button backButton;
    public TextField newAppointmentIDTextBox;
    public TextField newAppointmentTitleTextBox;
    public TextField newAppointmentDescriptionTextBox;
    public TextField newAppointmentLocationTextBox;
    public TextField newAppointmentTypeTextBox;
    public ComboBox<Integer> customerIDComboBox;
    public ComboBox<Integer> userIDComboBox;
    public ComboBox<String> newAppointmentContactComboBox;
    public DatePicker newAppointmentStartDateBox;
    public DatePicker newAppointmentEndDateBox;
    public TextField startTimeTextBox;
    public TextField endTimeTextBox;

    Stage stage;
    Parent scene;


    /**
     * METHOD DESCRIPTION: This method initializes the CustomerID ComboBox with all of the CustomerIDs, the UserID
     * ComboBox with all of the UserIDs, the Contact ComboBox with all of the Contact names, and initializes the
     * start and end date Date Pickers with the disabled days.
     *
     * LAMBDA EXPRESSION: A lambda expression is used to disable the ability to pick days in the past from the
     * Date Pickers as dates for new appointments. For the end date Date Picker, it also does not allow the user
     * to pick an end date from before the start date.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            customerIDComboBox.setItems(Customer.getAllCustomerIDs());
            userIDComboBox.setItems(User.getAllUserID());
            newAppointmentContactComboBox.setItems(Contact.getAllContactNames());
        }
        catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }

        newAppointmentStartDateBox.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate newAppointmentStartDateBox, boolean empty) {
                super.updateItem(newAppointmentStartDateBox, empty);
                setDisable(empty || newAppointmentStartDateBox.isBefore(LocalDate.now()));
            }});

        newAppointmentEndDateBox.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate newAppointmentEndDateBox, boolean empty) {
                super.updateItem(newAppointmentEndDateBox, empty);
                setDisable(empty || newAppointmentEndDateBox.isBefore(LocalDate.now()) ||
                        newAppointmentEndDateBox.isBefore(newAppointmentStartDateBox.getValue()));
            }});
    }

    /**
     * METHOD DESCRIPTION: This method clears any values from the TextBoxes, ComboBoxes, and Date Pickers
     */
    public void OnClickClearButton(ActionEvent actionEvent) {
        newAppointmentIDTextBox.clear();
        newAppointmentTitleTextBox.clear();
        newAppointmentDescriptionTextBox.clear();
        newAppointmentLocationTextBox.clear();
        newAppointmentTypeTextBox.clear();
        customerIDComboBox.valueProperty().set(null);
        userIDComboBox.valueProperty().set(null);
        newAppointmentContactComboBox.valueProperty().set(null);
        newAppointmentStartDateBox.getEditor().clear();
        newAppointmentEndDateBox.getEditor().clear();
        startTimeTextBox.clear();
        endTimeTextBox.clear();
    }

    /**
     * METHOD DESCRIPTION: This method gathers all of the inputted values, performs time conversions and logic checks
     * that ensure the inputted times are within business hours and do not overlap with another appointment for the
     * same customer, and calls the addAppointment database method that inserts the new appointment values into the
     * database
     */
    public void OnClickSaveButton(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {
        Boolean checkBusinessHours = true;
        Boolean checkAppointmentOverlap = true;
        Boolean checkStartTime = true;
        Boolean checkEndTime = true;

        // Gathers inputted data
        String appointmentTitle = newAppointmentTitleTextBox.getText();
        String appointmentDescription = newAppointmentDescriptionTextBox.getText();
        String appointmentLocation = newAppointmentLocationTextBox.getText();
        String appointmentType = newAppointmentTypeTextBox.getText();
        Integer customerID = customerIDComboBox.getValue();
        Integer userID = userIDComboBox.getValue();
        String contactName = newAppointmentContactComboBox.getValue();
        LocalDate appointmentStartDate = newAppointmentStartDateBox.getValue();
        LocalDate appointmentEndDate = newAppointmentEndDateBox.getValue();
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
            startDateTime = LocalDateTime.of(newAppointmentStartDateBox.getValue(), LocalTime.parse(startTimeTextBox.getText(), formatter));
            checkStartTime = true;
        }
        catch (DateTimeParseException error) {
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
            endDateTime = LocalDateTime.of(newAppointmentEndDateBox.getValue(), LocalTime.parse(endTimeTextBox.getText(), formatter));
            checkEndTime = true;
        }
        catch (DateTimeParseException error) {
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
        checkAppointmentOverlap = checkAppointmentOverlap(customerID, startDateTime, endDateTime, appointmentStartDate);

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
            String currentUser = User.getCurrentUser().getUserName();

            zonedStart = zonedStart.withZoneSameInstant(ZoneOffset.UTC);
            zonedEnd = zonedEnd.withZoneSameInstant(ZoneOffset.UTC);

            Appointment.addAppointment(appointmentTitle, appointmentDescription, appointmentLocation, appointmentType,
                    zonedStart, zonedEnd, currentUser, currentUser, customerID, userID, contactID);

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
    public boolean checkBusinessHours (LocalDateTime startDateTime, LocalDateTime endDateTime,
                                       LocalDate appointmentStartDate) {
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
                                            LocalDate startDate) throws SQLException {

        /* Assigns appointmentID as zero because it is a new appointment, so there won't be an appointment with the same
        appointmentID to exclude from the Observable List
         */
        int appointmentID = 0;
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
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("appointmentPage.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }
}
