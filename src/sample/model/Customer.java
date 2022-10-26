package sample.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.database.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * CLASS DESCRIPTION: This class is the model that encapsulates the Customer items
 */
public class Customer {

    private int customerID;
    private String customerName;
    private String customerAddress;
    private String customerPostalCode;
    private String customerPhoneNumber;
    private LocalDateTime createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    private int divisionID;
    private String customerDivision;
    private String customerCountry;

    public Customer(Integer customerID, String customerName, String customerAddress, String customerPostalCode,
                    String customerPhoneNumber, LocalDateTime createDate, String createdBy, Timestamp lastUpdate,
                    String lastUpdatedBy, Integer divisionID) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPostalCode = customerPostalCode;
        this.customerPhoneNumber = customerPhoneNumber;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.divisionID = divisionID;
    }

    public Customer(Integer customerID, String customerName, String customerAddress, String customerPostalCode,
                    String customerPhoneNumber, String customerDivision, String customerCountry) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPostalCode = customerPostalCode;
        this.customerPhoneNumber = customerPhoneNumber;
        this.customerDivision = customerDivision;
        this.customerCountry = customerCountry;
    }

    /**
     * METHOD DESCRIPTION: This method is a getter for the customerDivision
     */
    public String getCustomerDivision() {
        return customerDivision;
    }

    /**
     * METHOD DESCRIPTION: This method is a getter for the customerCountry
     */
    public String getCustomerCountry() {
        return customerCountry;
    }

    /**
     * METHOD DESCRIPTION: This method is a getter for the customerID
     */
    public Integer getCustomerID() {
        return customerID;
    }

    /**
     * METHOD DESCRIPTION: This method is a getter for the customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * METHOD DESCRIPTION: This method is a getter for the customerAddress
     */
    public String getCustomerAddress() {
        return customerAddress;
    }

    /**
     * METHOD DESCRIPTION: This method is a getter for the customerPostalCode
     */
    public String getCustomerPostalCode() {
        return customerPostalCode;
    }

    /**
     * METHOD DESCRIPTION: This method is a getter for the customerPhoneNumber
     */
    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    /**
     * METHOD DESCRIPTION: This method generates an Observable List that contains all customers in the database and
     * generates what country and state/province they are in, instead of the divisionID
     */
    public static ObservableList<Customer> getAllCustomers() throws SQLException {
        ObservableList<Customer> allCustomersList = FXCollections.observableArrayList();

        String sql = "SELECT cx.Customer_ID, cx.Customer_Name, cx.Address, cx.Postal_Code, cx.Phone, f.Division, \n" +
                "co.Country FROM customers as cx INNER JOIN first_level_divisions as f on cx.Division_ID = \n" +
                "f.Division_ID INNER JOIN countries as co ON f.COUNTRY_ID = co.Country_ID";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int customerID = rs.getInt("Customer_ID");
            String customerName = rs.getString("Customer_Name");
            String customerAddress = rs.getString("Address");
            String customerPostalCode = rs.getString("Postal_Code");
            String customerPhoneNumber = rs.getString("Phone");
            String customerDivision = rs.getString("Division");
            String customerCountry = rs.getString("Country");
            Customer customerResult = new Customer(customerID, customerName, customerAddress, customerPostalCode,
                    customerPhoneNumber, customerDivision, customerCountry);
            allCustomersList.add(customerResult);
        }
        ps.close();
        return allCustomersList;
    }

    /**
     * METHOD DESCRIPTION: This method generates an Observable List that contains all customerIDs in the database
     */
    public static ObservableList<Integer> getAllCustomerIDs() throws SQLException, ClassNotFoundException {

        ObservableList<Integer> allCustomerIDs = FXCollections.observableArrayList();
        String sql = "SELECT DISTINCT Customer_ID" +
                " FROM customers;";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            allCustomerIDs.add(rs.getInt("Customer_ID"));
        }
        return allCustomerIDs;
    }

    /**
     * METHOD DESCRIPTION: This method deletes a customer based on the passed in customerID
     */
    public static void deleteCustomer(Integer customerID) throws SQLException {
        String sql = "DELETE FROM customers WHERE Customer_ID =?";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, customerID);

        try {
            ps.executeUpdate();
            ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * METHOD DESCRIPTION: This method edits an existing customer in the database
     */
    public static void editCustomer(Integer customerID, String customerName, String customerAddress,
                                    String customerPostalCode, String customerPhoneNumber, Integer divisionID)
            throws SQLException {

        String sql = "UPDATE customers SET Customer_Name=?, Address=?, Postal_Code=?, Phone=?, " +
                "Last_Update=?, Last_Updated_By=?, Division_ID=? WHERE Customer_ID=?";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ps.setString(1, customerName);
        ps.setString(2, customerAddress);
        ps.setString(3, customerPostalCode);
        ps.setString(4, customerPhoneNumber);
        ps.setString(5, ZonedDateTime.now(ZoneOffset.UTC).format(formatter));
        ps.setString(6, User.getCurrentUser().getUserName());
        ps.setInt(7, divisionID);
        ps.setInt(8, customerID);

        try {
            ps.executeUpdate();
            ps.close();
            System.out.println("Edit Customer Successful");
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            System.out.println("Edit Customer Failed");
        }
    }

    /**
     * METHOD DESCRIPTION: This method adds a new customer to the database
     */
    public static void addCustomer(String customerName, String customerAddress, String customerPostalCode,
                                   String customerPhoneNumber, Integer divisionID) throws SQLException {
        // DateTimeFormatter to format time into Timestamp pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String sql = "INSERT INTO customers SET Customer_Name=?, Address=?, Postal_Code=?, Phone=?, " +
                "Create_Date=?, Created_By=?, Last_Update=?, Last_Updated_By=?, Division_ID=?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ps.setString(1, customerName);
        ps.setString(2, customerAddress);
        ps.setString(3, customerPostalCode);
        ps.setString(4, customerPhoneNumber);
        ps.setString(5, ZonedDateTime.now(ZoneOffset.UTC).format(formatter));
        ps.setString(6, User.getCurrentUser().getUserName());
        ps.setString(7, ZonedDateTime.now(ZoneOffset.UTC).format(formatter));
        ps.setString(8, User.getCurrentUser().getUserName());
        ps.setInt(9, divisionID);

        try {
            ps.executeUpdate();
            ps.close();
            System.out.println("Add Customer Successful");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("Add Customer Failed");
        }
    }
}


