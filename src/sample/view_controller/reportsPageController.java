package sample.view_controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sample.model.Appointment;
import sample.model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * CLASS DESCRIPTION: This is the class that controls the Reports Page
 */
public class reportsPageController implements Initializable {
    public Tab appointmentMetricsTab;
    public Tab contactsScheduleTab;
    public TableView<Appointment> contactScheduleTableView;
    public TableColumn<Appointment, String> contactNameColumn;
    public TableColumn<Appointment, Integer> customerIDColumn;
    public TableColumn<Appointment, String> titleColumn;
    public TableColumn<Appointment, String> typeColumn;
    public TableColumn<Appointment, ZonedDateTime> startColumn;
    public TableColumn<Appointment, ZonedDateTime> endColumn;
    public Tab loginRecordsTab;
    public TableView<User> loginRecordsTableView;
    public TableColumn<User, Integer> userIDColumn;
    public TableColumn<User, String> userNameColumn;
    public TableColumn<User, String> timestampColumn;
    public TableColumn<User, String> successfulColumn;
    public Button backButton;
    public TableView<Appointment> appointmentByTypeTableView;
    public TableColumn<Appointment, String> appointmentTypeColumn;
    public TableColumn<Appointment, Integer> typeTotalColumn;
    public TableView<Appointment> appointmentByMonthTableView;
    public TableColumn<Appointment, String> appointmentMonthColumn;
    public TableColumn<Appointment, Integer> monthTotalColumn;

    /**
     * METHOD DESCRIPTION: This method initializes and populates the Contacts Table using the populateContactsTable
     * method, populates the Appointment Type table using the populateAppointmentTypeTable method, populates the
     * Appointment Month table using the populateAppointmentMonthTable method, and populates the Login Record table
     * using the populateLoginRecordTable method
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            populateContactsTable(Appointment.getAllAppointments());
            populateAppointmentTypeTable(Appointment.appointmentTypesAndTotals());
            populateAppointmentMonthTable(Appointment.appointmentMonthsAndTotals());
            populateLoginRecordTable(User.getAllLogonData());
        }
        catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * METHOD DESCRIPTION: This method populates the Login Records Table by using an Observable List from the User model
     */
    public void populateLoginRecordTable(ObservableList<User> allLoginsList) throws SQLException {
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("logonUserID"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("logonUserName"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("logonTimestamp"));
        successfulColumn.setCellValueFactory(new PropertyValueFactory<>("logonSuccess"));

        loginRecordsTableView.setItems(allLoginsList);
    }

    /**
     * METHOD DESCRIPTION: This method populates the Contacts Table by using an Observable List from the Appointment
     * model
     */
    public void populateContactsTable(ObservableList<Appointment> allAppointmentList) throws SQLException, ClassNotFoundException {
        contactNameColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentStart"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentEnd"));

        contactScheduleTableView.setItems(allAppointmentList);
    }

    /**
     * METHOD DESCRIPTION: This method populates the Appointment Type Table by using an Observable List from the
     * Appointment model
     */
    public void populateAppointmentTypeTable(ObservableList<Appointment> allTypesList) throws SQLException {
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        typeTotalColumn.setCellValueFactory(new PropertyValueFactory<>("typeCount"));

        appointmentByTypeTableView.setItems(allTypesList);
    }

    /**
     * METHOD DESCRIPTION: This method populates the Appointment Month Table by using an Observable List from the
     * Appointment model
     */
    public void populateAppointmentMonthTable(ObservableList<Appointment> allMonthsList) throws SQLException {
        appointmentMonthColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentMonth"));
        monthTotalColumn.setCellValueFactory(new PropertyValueFactory<>("monthCount"));

        appointmentByMonthTableView.setItems(allMonthsList);
    }

    /**
     * METHOD DESCRIPTION: This method returns the user to the Main Screen when they click the "Back" button
     */
    public void OnBackButtonClick(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}
