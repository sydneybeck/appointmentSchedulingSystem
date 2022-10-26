package sample.view_controller;

import javafx.collections.ObservableList;
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
import sample.model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

/**
 * CLASS DESCRIPTION: This is the class that controls the Customer Page
 */
public class customerPageController implements Initializable {
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, String> customerIDColumn;
    @FXML
    private TableColumn<Customer, String> customerNameColumn;
    @FXML
    private TableColumn<Customer, String> customerAddressColumn;
    @FXML
    private TableColumn<Customer, String> customerPostalCodeColumn;
    @FXML
    private TableColumn<Customer, String> customerPhoneColumn;
    @FXML
    private TableColumn<Customer, String> customerDivisionColumn;
    @FXML
    private TableColumn<Customer, String> customerCountryColumn;

    /**
     * METHOD DESCRIPTION: This method initializes the Customers Table and populates it using the populateCustomersTable
     * method
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            populateCustomersTable(Customer.getAllCustomers());
        }
        catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * METHOD DESCRIPTION: This method takes the user to the Add Customer page when they click the "Add Customer" button
     */
    public void OnAddCustomerButtonClick(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("addCustomer.fxml"));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * METHOD DESCRIPTION: This method deletes a selected customer from the database by calling the deleteCustomer
     * method from my Customer model, but first generates a confirmation alert that makes the user confirm that they
     * want to delete that customer
     */
    public void OnDeleteCustomerButtonClick() throws SQLException {
        Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " +
                selectedCustomer.getCustomerName() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            Appointment.deleteCustomerAppointments(selectedCustomer.getCustomerID());
            Customer.deleteCustomer(selectedCustomer.getCustomerID());
            System.out.println("Customer Deleted");

            populateCustomersTable(Customer.getAllCustomers());
        }
    }

    /**
     * METHOD DESCRIPTION: This method takes the user to the Edit Customer page when they click the "Edit
     * Customer" button and populates the data from the selected customer into the input fields
     */
    public void OnEditCustomerButtonClick(ActionEvent event) throws IOException, SQLException {
        Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("editCustomer.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        editCustomerController ECController = loader.getController();
        ECController.populateCustomer(selectedCustomer);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
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

    /**
     * METHOD DESCRIPTION: This method populates the Customers Table from an Observable List
     */
    public void populateCustomersTable(ObservableList<Customer> allCustomersList) throws SQLException {
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("customerPhoneNumber"));
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        customerPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("customerPostalCode"));
        customerDivisionColumn.setCellValueFactory(new PropertyValueFactory<>("customerDivision"));
        customerCountryColumn.setCellValueFactory(new PropertyValueFactory<>("customerCountry"));

        customerTableView.setItems(allCustomersList);
    }
}
