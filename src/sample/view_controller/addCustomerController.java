package sample.view_controller;

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
import sample.model.Country;
import sample.model.Customer;
import sample.model.FirstLevelDivisions;
import sample.model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * CLASS DESCRIPTION: This is the class that controls the Add Customer controller
 */
public class addCustomerController implements Initializable {
    public TextField newCustomerNameTextBox;
    public TextField newCustomerAddressTextBox;
    public TextField newCustomerPostCodeTextBox;
    public TextField newCustomerPhoneTextBox;
    public ComboBox<String> newCustomerStateComboBox;
    public ComboBox<String> newCustomerCountryComboBox;
    public Button clearButton;
    public Button saveButton;
    public Button backButton;

    /**
     * METHOD DESCRIPTION: This method initializes the Country ComboBox with all available countries from the database
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            newCustomerCountryComboBox.setItems(Country.getAllCountries());
        }
        catch (SQLException throwable){
            throwable.printStackTrace();
        }
    }

    /**
     * METHOD DESCRIPTION: This method clears any values from the TextBoxes and ComboBoxes
     */
    public void OnClickClearButton(ActionEvent actionEvent) {
        newCustomerNameTextBox.clear();
        newCustomerAddressTextBox.clear();
        newCustomerPostCodeTextBox.clear();
        newCustomerPhoneTextBox.clear();
        newCustomerCountryComboBox.valueProperty().set(null);
        newCustomerStateComboBox.valueProperty().set(null);
    }

    /**
     * METHOD DESCRIPTION: This method gathers all of the inputted information, performs time formatting and conversions,
     * inputs the data into the database, and returns the user to the Customer Page. The time data is collected using
     * now(), converted to UTC, formatted using the DateTimeFormatter, then converted to a timestamp for the database.
     * The DivisionID is collected using a method in my FirstLevelDivisions model that matches the inputted state to its
     * DivisionID
     */
    public void OnClickSaveButton(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {
        // Gathers inputted data
        String customerName = newCustomerNameTextBox.getText();
        String customerAddress = newCustomerAddressTextBox.getText();
        String customerPostalCode = newCustomerPostCodeTextBox.getText();
        String customerPhoneNumber = newCustomerPhoneTextBox.getText();
        String country = newCustomerCountryComboBox.getValue();
        String division = newCustomerStateComboBox.getValue();

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
            Customer.addCustomer(customerName, customerAddress, customerPostalCode, customerPhoneNumber, divisionID);

            Parent parent = FXMLLoader.load(getClass().getResource("customerPage.fxml"));
            Scene scene = new Scene(parent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        }
    }

    /**
     * METHOD DESCRIPTION: This method returns the user to the Customer Page when they click the "Back" button
     */
    public void OnClickBackButton(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("customerPage.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * METHOD DESCRIPTION: This method listens for a selection in the Country ComboBox, then limits the states/provinces
     * that populate into the State ComboBox by using the getStateFromCountryName method in my FirstLevelDivisions model
     */
    public void OnSelectCountry() throws SQLException, ClassNotFoundException {
        newCustomerStateComboBox.setItems(FirstLevelDivisions.getStateFromCountryName
                (newCustomerCountryComboBox.getValue()));
    }
}
