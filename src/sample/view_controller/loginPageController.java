package sample.view_controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.model.Appointment;
import sample.model.User;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * CLASS DESCRIPTION: This is the class that controls the Login Page
 */
public class loginPageController implements Initializable {
    public Label userNameLabel;
    public Label passwordLabel;
    public TextField userNameTextField;
    public TextField passwordTextField;
    public Button loginButton;
    public Label timeZoneLabel;
    public Label zoneID;
    public Label loginTitleLabel;
    public Button cancelButton;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");
    private ZoneId timezone = ZoneId.systemDefault();

    /**
     * Initializes and sets the zoneID label with the users time zone, assigns the resource bundle, and sets the label
     * and button text based on users default locale
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        zoneID.setText(ZoneId.systemDefault().toString());

        ResourceBundle resource = ResourceBundle.getBundle("sample/language/language", Locale.getDefault());

        zoneID.setText(String.valueOf(timezone));

        loginTitleLabel.setText(resource.getString("Title"));
        userNameLabel.setText(resource.getString("Username"));
        passwordLabel.setText(resource.getString("Password"));
        timeZoneLabel.setText(resource.getString("TimeZone"));
        loginButton.setText(resource.getString("Login"));
        cancelButton.setText(resource.getString("Cancel"));
    }

    /**
     * METHOD DESCRIPTION: This method collects the inputted userName and password, passes it into the loginValidation
     * method in my User model, and returns a boolean based on if the userName password combo exists in the database.
     * Then, it passes the inputted userName and returned logon boolean to the loginRecord method. If logon returns
     * true, it passes the logon boolean and inputted username into the addToLogonRecord method in my User model,
     * and generates the appointmentsWithin15 ObservableList. If there is an appointment in the ObservableList, a
     * confirmation alert is shown that alerts the user of the appointment, and if not, a confirmation alert is shown
     * that alerts the user that they do not have any appointments within 15 minutes and loads the Main Screen
     */
    public void OnLoginButtonClick(ActionEvent event) throws Exception {
        // Gathers the inputted userName and password
        String userName = userNameTextField.getText();
        String password = passwordTextField.getText();

        // Passes the inputted userName and password into the loginValidation method in the User model
        boolean logon = User.loginValidation(userName, password);

        // Passes the inputted userName and the boolean output of the loginValidation method into the loginRecord method
        loginRecord(userName, logon);

        // Loads the resource bundle
        ResourceBundle resource = ResourceBundle.getBundle("sample/language/language", Locale.getDefault());

        if (logon) {
            // Passes the logon boolean and inputted userName into the addToLogonRecord method in the User model
            User.addToLogonRecord(logon, userName);
            // Loads the ObservableList containing the appointments that start within 15 minutes
            ObservableList<Appointment> appointmentsWithin15 = Appointment.appointmentsWithin15Min();

            // Generates a confirmation dialog if there is an appointment within 15 minutes
            if (!appointmentsWithin15.isEmpty()) {
                for (Appointment appointment : appointmentsWithin15) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle(resource.getString("Information"));
                    alert.setContentText("There is an upcoming appointment with the Appointment ID: " +
                            appointment.getAppointmentID() + " that starts at: " +
                            appointment.getAppointmentStart().toString());
                    alert.showAndWait();
                }
            }
            // Generates a confirmation dialog if there is no appointment within 15 minutes
            else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(resource.getString("Information"));
                alert.setContentText(resource.getString("NoAppt"));
                alert.showAndWait();
            }
            System.out.println("Logon Successful");

            Parent parent = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        }
        // Generates an error message if logon returns false
        else {
            User.addToLogonRecord(logon, userName);
            System.out.println("Logon Unsuccessful");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(resource.getString("Error"));
            alert.setContentText(resource.getString("Message"));
            alert.showAndWait();
        }
    }

    /**
     * METHOD DESCRIPTION: This method passes in the inputted userName and the result of the loginValidation method
     * and records the inputted username, if the login was successful, and a UTC timestamp whenever a login is attempted
     */
    public void loginRecord(String userName, Boolean logon) {
        try {
            String fileName = "login_activity.txt";
            BufferedWriter log = new BufferedWriter(new FileWriter(fileName, true));
            log.append(ZonedDateTime.now(ZoneOffset.UTC).format(formatter)).append(" Username: ").append(userName)
                    .append(" Login Successful?: ").append(logon.toString()).append("\n");
            System.out.println("New login recorded");
            log.flush();
            log.close();
        } catch (IOException throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * METHOD DESCRIPTION: This method exits the program when the user clicks the "Cancel" button
     */
    public void OnCancelButtonClick() {
        System.exit(0);
    }
}
