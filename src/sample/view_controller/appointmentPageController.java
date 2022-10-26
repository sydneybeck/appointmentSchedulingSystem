package sample.view_controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sample.model.Appointment;
import sample.model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.ResourceBundle;

/**
 * CLASS DESCRIPTION: This is the class that controls the Appointment Page
 */
public class appointmentPageController implements Initializable {
    public TextField appointmentSearchBox;
    @FXML
    private RadioButton allRadioButton;
    @FXML
    private TableColumn<Appointment, String> appointmentContactNameColumn;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIDColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentTitleColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentDescriptionColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentLocationColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentTypeColumn;
    @FXML
    private TableColumn<Appointment, ZonedDateTime> appointmentStartColumn;
    @FXML
    private TableColumn<Appointment, ZonedDateTime> appointmentEndColumn;
    @FXML
    private TableColumn<Appointment, Integer> customerIDColumn;
    @FXML
    private TableColumn<Appointment, Integer> userIDColumn;
    @FXML
    private RadioButton weekRadioButton;
    @FXML
    private ToggleGroup radioButtons;
    @FXML
    private RadioButton monthRadioButton;
    @FXML
    private TableView<Appointment> appointmentTableView;

    Stage stage;
    Parent scene;

    ZonedDateTime startTimeMarker;
    ZonedDateTime endTimeMarker;

    /**
     * METHOD DESCRIPTION: This method assigns the radio buttons to a toggle group so only one can be selected at a time
     */
    public void toggleGroup() {
        radioButtons = new ToggleGroup();

        allRadioButton.setToggleGroup(radioButtons);
        weekRadioButton.setToggleGroup(radioButtons);
        monthRadioButton.setToggleGroup(radioButtons);
    }

    /**
     * METHOD DESCRIPTION: This method initializes the Appointment Table and populates it using the
     * populateAppointmentTable method
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            populateAppointmentTable(Appointment.getAllAppointments());
        }
        catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * METHOD DESCRIPTION: This method populates the Appointment Table from an Observable List and codes search
     * functionality
     *
     * LAMBDA EXPRESSION: A lambda expression is used to make the search box functional by using a Filtered List
     * and a listener for the user inputted value. The user inputted value is then compared to all of the data
     * for appointments in the Appointments Observable List and the Appointments table is repopulated. There
     * is a bug with the lambda expression when using the radio buttons. Going from Week/Month to All breaks the
     * search functionality, but the search still works within Weeks/Months. The search functionality can be used
     * when All is toggled if the Week/Month is not toggled prior. You can reset it by backing out of the view
     * and going back in.
     */
    public void populateAppointmentTable (ObservableList<Appointment> allAppointmentsList) throws SQLException {
        appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        appointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));
        appointmentLocationColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentLocation"));
        appointmentContactNameColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentStart"));
        appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentEnd"));
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));

        appointmentTableView.setItems(allAppointmentsList);

        // Lambda expression for search box functionality
        FilteredList<Appointment> filteredAppointments = new FilteredList<>(allAppointmentsList, b -> true);
        appointmentSearchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredAppointments.setPredicate(Appointment -> {
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                String searchKeyword = newValue.toLowerCase();
                if (Appointment.getAppointmentTitle().toLowerCase().contains(searchKeyword)) {
                    return true;
                }
                else if(Appointment.getAppointmentDescription().toLowerCase().contains(searchKeyword)) {
                    return true;
                }
                else if(Appointment.getAppointmentID().toString().toLowerCase().contains(searchKeyword)) {
                    return true;
                }
                else if(Appointment.getAppointmentLocation().toLowerCase().contains(searchKeyword)) {
                    return true;
                }
                else if(Appointment.getContactName().toLowerCase().contains(searchKeyword)) {
                    return true;
                }
                else if(Appointment.getAppointmentType().toLowerCase().contains(searchKeyword)) {
                    return true;
                }
                else if(Appointment.getAppointmentStart().toString().toLowerCase().contains(searchKeyword)) {
                    return true;
                }
                else if(Appointment.getAppointmentEnd().toString().toLowerCase().contains(searchKeyword)) {
                    return true;
                }
                else if(Appointment.getCustomerID().toString().toLowerCase().contains(searchKeyword)) {
                    return true;
                }
                else if(Appointment.getUserID().toString().toLowerCase().contains(searchKeyword)) {
                    return true;
                }
                else {
                    return false;
                }
            });
        });
        SortedList<Appointment> sortedData = new SortedList<>(filteredAppointments);
        sortedData.comparatorProperty().bind(appointmentTableView.comparatorProperty());

        appointmentTableView.setItems(sortedData);
    }

    /**
     * METHOD DESCRIPTION: This method takes the user to the Add Appointment page when they click the "Add Appointment"
     * button
     */
    public void OnClickAddAppointmentButton(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("addAppointment.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * METHOD DESCRIPTION: This method takes the user to the Edit Appointment page when they click the "Edit
     * Appointment" button and populates the data from the selected appointment into the input fields
     */
    public void OnClickEditAppointmentButton(ActionEvent event) throws IOException, SQLException {
        Appointment selectedAppointment = appointmentTableView.getSelectionModel().getSelectedItem();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("editAppointment.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        editAppointmentController EAController = loader.getController();
        EAController.populateAppointment(selectedAppointment);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
    }

    /**
     * METHOD DESCRIPTION: This method deletes a selected appointment from the database by calling the deleteAppointment
     * method from my Appointment model, but first generates a confirmation alert that makes the user confirm that they
     * want to delete that appointment
     */
    public void OnClickDeleteAppointmentButton() throws SQLException, ClassNotFoundException {
        Appointment selectedAppointment = appointmentTableView.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel Appointment ID: " +
                selectedAppointment.getAppointmentID() + " Appointment Type: " +
                selectedAppointment.getAppointmentType() + "?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            Appointment.deleteAppointment(selectedAppointment.getAppointmentID());
            System.out.println("Appointment Deleted");

            populateAppointmentTable(Appointment.getAllAppointments());
        }
    }

    /**
     * METHOD DESCRIPTION: This method populates the table with appointments within a week from the customers current
     * time when the Week radio button is clicked. The users time zone is added to their current time, converted
     * to UTC, passed into the getAppointmentsByDate method in my Appointment controller, then the table is populated
     * with the resulting appointments
     */
    public void OnWeekToggleClick() throws SQLException {
        allRadioButton.setSelected(false);
        monthRadioButton.setSelected(false);

        ObservableList<Appointment> appointmentsByTimeList;

        startTimeMarker = ZonedDateTime.now(User.getUserTimeZone());
        endTimeMarker = startTimeMarker.plusWeeks(1);

        ZonedDateTime startTime = startTimeMarker.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime endTime = endTimeMarker.withZoneSameInstant(ZoneOffset.UTC);

        appointmentsByTimeList = Appointment.getAppointmentsByTime(startTime, endTime);

        populateAppointmentTable(appointmentsByTimeList);
    }

    /**
     * METHOD DESCRIPTION: This method populates the table with appointments within a month from the customers current
     * time when the Month radio button is clicked. The users time zone is added to their current time, converted
     * to UTC, passed into the getAppointmentsByDate method in my Appointment controller, then the table is populated
     * with the resulting appointments
     */
    public void OnMonthToggleClick() throws SQLException {
        ObservableList<Appointment> appointmentsByTimeList;

        startTimeMarker = ZonedDateTime.now(User.getUserTimeZone());
        endTimeMarker = startTimeMarker.plusMonths(1);

        ZonedDateTime startTime = startTimeMarker.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime endTime = endTimeMarker.withZoneSameInstant(ZoneOffset.UTC);

        appointmentsByTimeList = Appointment.getAppointmentsByTime(startTime, endTime);

        populateAppointmentTable(appointmentsByTimeList);
    }

    /**
     * METHOD DESCRIPTION: This method populates the table with all the appointments in the database when the "All
     * Appointments" radio button is clicked
     */
    public void OnAllToggleClick() throws SQLException, ClassNotFoundException {
        ObservableList<Appointment> allAppointmentsList = Appointment.getAllAppointments();
        appointmentTableView.setItems(allAppointmentsList);
        startTimeMarker = null;
    }

    /**
     * METHOD DESCRIPTION: This method returns the user to the Main Screen when they click the "Back" button
     */
    public void OnClickBackButton(ActionEvent  event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * METHOD DESCRIPTION: This method works in conjunction with the Week and Month radio buttons and populates the
     * table with appointments one month or week in the future when the user clicks the "Next" button. One week or
     * month is added to the startTimeMarker variable, converted to UTC, then the table is populated with appointments
     * that fall between the startTime and endTime variables passed into the getAppointmentsByDate method.
     *
     * There is a bug with this feature where if there are no appointments that fall within the startTime and endTime,
     * then the table just says "No content in table" until the next/previous buttons are pressed so that appointments
     * are found within that date range. A future improvement would be to skip populating the table if no appointments
     * were returned in the appointmentsByDateList, or an error that says that no appointments fall between the start
     * and end markers
     */
    public void OnNextButtonClick() throws SQLException {
        ObservableList<Appointment> appointmentsByTimeList;
            if (radioButtons.getSelectedToggle() == weekRadioButton) {
                // Adds week to the markers
                ZonedDateTime startTime = startTimeMarker.plusWeeks(1);
                ZonedDateTime endTime = endTimeMarker.plusWeeks(1);

                // Assigns the markers to the start and end time variables
                startTimeMarker = startTime;
                endTimeMarker = endTime;

                // Convert start and end times to UTC
                ZonedDateTime startTimeUTC = startTime.withZoneSameInstant(ZoneOffset.UTC);
                ZonedDateTime endTimeUTC = endTime.withZoneSameInstant(ZoneOffset.UTC);

                // Generate list of appointments within that time frame
                appointmentsByTimeList = Appointment.getAppointmentsByTime(startTimeUTC, endTimeUTC);

                // Populate table
                populateAppointmentTable(appointmentsByTimeList);
            }

            if (radioButtons.getSelectedToggle() == monthRadioButton) {
                // Adds month to the markers
                ZonedDateTime startTime = startTimeMarker.plusMonths(1);
                ZonedDateTime endTime = endTimeMarker.plusMonths(1);

                // Assigns the markers to the start and end time variables
                startTimeMarker = startTime;
                endTimeMarker = endTime;

                // Convert start and end times to UTC
                ZonedDateTime startTimeUTC = startTime.withZoneSameInstant(ZoneOffset.UTC);
                ZonedDateTime endTimeUTC = endTime.withZoneSameInstant(ZoneOffset.UTC);

                // Generate list of appointments within that time frame
                appointmentsByTimeList = Appointment.getAppointmentsByTime(startTimeUTC, endTimeUTC);

                // Populate table
                populateAppointmentTable(appointmentsByTimeList);
        }
    }

    /**
     * METHOD DESCRIPTION: This method works in conjunction with the Week and Month radio buttons and populates the
     * table with appointments one month or week in the past (relative to the start and end time markers) when the user
     * clicks the "Previous" button. One week or month is subtracted from the startTimeMarker variable, converted to UTC,
     * then the table is populated with appointments that fall between the startTime and endTime variables passed into
     * the getAppointmentsByDate method.
     *
     * There is a bug with this feature where if there are no appointments that fall within the startTime and endTime,
     * then the table just says "No content in table" until the next/previous buttons are pressed so that appointments
     * are found within that date range. A future improvement would be to skip populating the table if no appointments
     * were returned in the appointmentsByDateList, or an error that says that no appointments fall between the start
     * and end markers
     */
    public void OnPreviousButtonClick(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        ObservableList<Appointment> appointmentsByTimeList = FXCollections.observableArrayList();

        if (radioButtons.getSelectedToggle() == weekRadioButton) {
            // Subtracts week from the markers
            ZonedDateTime startTime = startTimeMarker.minusWeeks(1);
            ZonedDateTime endTime = endTimeMarker.minusWeeks(1);

            // Assigns the markers to the start and end time variables
            startTimeMarker = startTime;
            endTimeMarker = endTime;

            // Convert start and end times to UTC
            ZonedDateTime startTimeUTC = startTime.withZoneSameInstant(ZoneOffset.UTC);
            ZonedDateTime endTimeUTC = endTime.withZoneSameInstant(ZoneOffset.UTC);

            // Generate list of appointments within that time frame
            appointmentsByTimeList = Appointment.getAppointmentsByTime(startTimeUTC, endTimeUTC);

            // Populate table
            populateAppointmentTable(appointmentsByTimeList);
        }

        if (radioButtons.getSelectedToggle() == monthRadioButton) {
            // Subtracts month from the markers
            ZonedDateTime startTime = startTimeMarker.minusMonths(1);
            ZonedDateTime endTime = endTimeMarker.minusMonths(1);

            // Assigns the markers to the start and end time variables
            startTimeMarker = startTime;
            endTimeMarker = endTime;

            // Convert start and end times to UTC
            ZonedDateTime startTimeUTC = startTime.withZoneSameInstant(ZoneOffset.UTC);
            ZonedDateTime endTimeUTC = endTime.withZoneSameInstant(ZoneOffset.UTC);

            // Generate list of appointments within that time frame
            appointmentsByTimeList = Appointment.getAppointmentsByTime(startTimeUTC, endTimeUTC);

            // Populate table
            populateAppointmentTable(appointmentsByTimeList);
        }
    }
}
