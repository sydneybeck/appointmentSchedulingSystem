package sample.view_controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.database.DBConnection;
import sample.model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * CLASS DESCRIPTION: This is the class that controls the Edit Customer page
 */
public class editCustomerController implements Initializable {
    public TextField customerIDTextBox;
    public TextField customerNameTextBox;
    public TextField customerAddressTextBox;
    public TextField customerPostalCodeTextBox;
    public TextField customerPhoneTextBox;
    public ComboBox<String> customerCountryComboBox;
    public ComboBox<String> customerStateComboBox;
    public Button backButton;
    public Button saveButton;

    Stage stage;
    Parent scene;

    public void OnSaveButtonClick(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
        // Gathers inputted data
        Integer customerID = Integer.parseInt(customerIDTextBox.getText());
        String customerName = customerNameTextBox.getText();
        String customerAddress = customerAddressTextBox.getText();
        String customerPostalCode = customerPostalCodeTextBox.getText();
        String customerPhoneNumber = customerPhoneTextBox.getText();
        String country = customerCountryComboBox.getValue();
        String division = customerStateComboBox.getValue();

        Integer divisionID = FirstLevelDivisions.getDivisionID(division);

        // Checks for blank input fields and outputs an error if any are found
        if (customerName.isBlank() || customerAddress.isBlank() || customerPostalCode.isBlank()
                || customerPhoneNumber.isBlank() || country == null || division == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please enter a value into all of the fields");
            alert.showAndWait();
        }

        /** Calls the editCustomer method and passes in all of the inputted data to add the customer to the database,
         * then reloads the Customer Page
         */
        else {
            Customer.editCustomer(customerID, customerName, customerAddress, customerPostalCode, customerPhoneNumber,
                    divisionID);

            Parent parent = FXMLLoader.load(getClass().getResource("customerPage.fxml"));
            Scene scene = new Scene(parent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        }
    }

    /**
     * METHOD DESCRIPTION: This method returns the user to the Appointment Page when they click the "Back" button
     */
    public void OnBackButtonClick(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("customerPage.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * METHOD DESCRIPTION: This method takes the selected customer from the previous screen and populates the input
     * fields
     */
    public void populateCustomer(Customer selectedCustomer) throws SQLException {
        try {
            customerIDTextBox.setText(selectedCustomer.getCustomerID().toString());
            customerNameTextBox.setText(selectedCustomer.getCustomerName());
            customerAddressTextBox.setText(selectedCustomer.getCustomerAddress());
            customerPostalCodeTextBox.setText(selectedCustomer.getCustomerPostalCode());
            customerPhoneTextBox.setText(selectedCustomer.getCustomerPhoneNumber());

            customerCountryComboBox.setItems(Country.getAllCountries());
            customerCountryComboBox.getSelectionModel().select(selectedCustomer.getCustomerCountry());
            customerStateComboBox.setItems(FirstLevelDivisions.getStateFromCountryName(selectedCustomer.getCustomerCountry()));
            customerStateComboBox.getSelectionModel().select(selectedCustomer.getCustomerDivision());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * METHOD DESCRIPTION: This method listens for a selection in the Country ComboBox, then limits the states/provinces
     * that populate into the State ComboBox by using the getStateFromCountryName method in my FirstLevelDivisions model
     */
    public void OnSelectCountry() throws SQLException, ClassNotFoundException {
        customerStateComboBox.setItems(FirstLevelDivisions.getStateFromCountryName
                (customerCountryComboBox.getValue()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
