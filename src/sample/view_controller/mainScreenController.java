package sample.view_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * CLASS DESCRIPTION: This is the class that controls the Main Screen
 */
public class mainScreenController implements Initializable {
    public Button customerButton;
    public Button schedulingButton;
    public Button logoutButton;
    public Button reportsButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * METHOD DESCRIPTION: This method takes the user to the Customer Page when the click the "Customers" button
     */
    public void OnCustomerButtonClick(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("customerPage.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * METHOD DESCRIPTION: This method takes the user to the Appointments Page when the click the "Appointments" button
     */
    public void OnSchedulingButtonClick(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("appointmentPage.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * METHOD DESCRIPTION: This method takes the user to the Login Page when the click the "Log Out" button
     */
    public void OnLogoutButtonClick(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("loginPage.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * METHOD DESCRIPTION: This method takes the user to the Reports Page when the click the "Reports" button
     */
    public void OnClickReportsButton(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("reportsPage.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}
