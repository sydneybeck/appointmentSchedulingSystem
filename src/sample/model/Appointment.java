package sample.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.database.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * CLASS DESCRIPTION: This class is the model that encapsulates the Appointment items
 */
public class Appointment {
    private Integer appointmentID;
    private String appointmentTitle;
    private String appointmentDescription;
    private String appointmentLocation;
    private String appointmentType;
    private Timestamp appointmentStart;
    private Timestamp appointmentEnd;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    private Integer customerID;
    private Integer userID;
    private Integer contactID;
    private String contactName;
    private Integer typeCount;
    private String appointmentMonth;
    private Integer monthCount;

    public Appointment(int appointmentID, String appointmentTitle, String appointmentDescription,
                       String appointmentLocation, String appointmentType, Timestamp appointmentStart, Timestamp appointmentEnd,
                       Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy, int customerID,
                       int userID, int contactID, String contactName) {
        this.appointmentID = appointmentID;
        this.appointmentTitle = appointmentTitle;
        this.appointmentDescription = appointmentDescription;
        this.appointmentLocation = appointmentLocation;
        this.appointmentType = appointmentType;
        this.appointmentStart = appointmentStart;
        this.appointmentEnd = appointmentEnd;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
        this.contactName = contactName;
    }

    public Appointment(String appointmentType, Integer typeCount) {
        this.appointmentType = appointmentType;
        this.typeCount = typeCount;
    }

    public Appointment(Integer monthCount, String appointmentMonth) {
        this.monthCount = monthCount;
        this.appointmentMonth = appointmentMonth;
    }

    /**
     * METHOD DESCRIPTION: This method is the getter for the AppointmentID
     */
    public Integer getAppointmentID() {
        return appointmentID;
    }

    /**
     * METHOD DESCRIPTION: This method is the getter for the Appointment Title
     */
    public String getAppointmentTitle() {
        return appointmentTitle;
    }

    /**
     * METHOD DESCRIPTION: This method is the getter for the Appointment Description
     */
    public String getAppointmentDescription() {
        return appointmentDescription;
    }

    /**
     * METHOD DESCRIPTION: This method is the getter for the Appointment Location
     */
    public String getAppointmentLocation() {
        return appointmentLocation;
    }

    /**
     * METHOD DESCRIPTION: This method is the getter for the Appointment Type
     */
    public String getAppointmentType() {
        return appointmentType;
    }

    /**
     * METHOD DESCRIPTION: This method is the getter for the Appointment Start
     */
    public Timestamp getAppointmentStart() {
        return appointmentStart;
    }

    /**
     * METHOD DESCRIPTION: This method is the getter for the Appointment End
     */
    public Timestamp getAppointmentEnd() {
        return appointmentEnd;
    }

    /**
     * METHOD DESCRIPTION: This method is the getter for the Appointment CustomerID
     */
    public Integer getCustomerID() {
        return customerID;
    }

    /**
     * METHOD DESCRIPTION: This method is the getter for the Appointment UserID
     */
    public Integer getUserID() {
        return userID;
    }

    /**
     * METHOD DESCRIPTION: This method is the getter for the Appointment Contact Name
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * METHOD DESCRIPTION: This method generates the Observable List data for all appointments in the database
     */
    public static ObservableList<Appointment> getAllAppointments() throws SQLException, ClassNotFoundException {
        ObservableList<Appointment> allAppointmentsList = FXCollections.observableArrayList();

        String sql = "SELECT * FROM appointments as a LEFT OUTER JOIN contacts as c ON a.Contact_ID = c.Contact_ID;";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Integer appointmentID = rs.getInt("Appointment_ID");
            String appointmentTitle = rs.getString("Title");
            String appointmentDescription = rs.getString("Description");
            String appointmentLocation = rs.getString("Location");
            String appointmentType = rs.getString("Type");
            Timestamp appointmentStart = rs.getTimestamp("Start");
            Timestamp appointmentEnd = rs.getTimestamp("End");
            Timestamp createdDate = rs.getTimestamp("Create_Date");
            String createdBy = rs.getString("Created_by");
            Timestamp lastUpdate = rs.getTimestamp("Last_Update");
            String lastUpdatedBy = rs.getString("Last_Updated_By");
            Integer customerID = rs.getInt("Customer_ID");
            Integer userID = rs.getInt("User_ID");
            Integer contactID = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");

            Appointment appointmentResult = new Appointment(appointmentID, appointmentTitle, appointmentDescription,
                    appointmentLocation, appointmentType, appointmentStart, appointmentEnd,
                    createdDate, createdBy, lastUpdate, lastUpdatedBy, customerID, userID, contactID, contactName);
            allAppointmentsList.add(appointmentResult);
        }
        ps.close();
        return allAppointmentsList;
    }

    /**
     * METHOD DESCRIPTION: This method is used to add a new appointment to the database
     */
    public static Boolean addAppointment(String appointmentTitle, String appointmentDescription, String appointmentLocation,
                                         String appointmentType, ZonedDateTime appointmentStart, ZonedDateTime appointmentEnd,
                                         String createdBy, String lastUpdatedBy, Integer customerID, Integer userID,
                                         Integer contactID) throws SQLException {
        String sql = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_date, Created_By, " +
                "Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        // DateTimeFormatter to format time into Timestamp pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Converts the appointment start and end times to the DateTimeFormatter pattern and turns them into strings
        String appointmentStartString = appointmentStart.format(formatter);
        String appointmentEndString = appointmentEnd.format(formatter);

        ps.setString(1, appointmentTitle);
        ps.setString(2, appointmentDescription);
        ps.setString(3, appointmentLocation);
        ps.setString(4, appointmentType);
        ps.setString(5, appointmentStartString);
        ps.setString(6, appointmentEndString);
        ps.setString(7, ZonedDateTime.now(ZoneOffset.UTC).format(formatter));
        ps.setString(8, createdBy);
        ps.setString(9, ZonedDateTime.now(ZoneOffset.UTC).format(formatter));
        ps.setString(10, lastUpdatedBy);
        ps.setInt(11, customerID);
        ps.setInt(12, userID);
        ps.setInt(13, contactID);

        try {
            ps.executeUpdate();
            ps.close();
            System.out.println("Add Appointment Successful");
            return true;
        }
        catch (SQLException throwable) {
            throwable.printStackTrace();
            System.out.println("Add Appointment Failed");
            return false;
        }
    }

    /**
     * METHOD DESCRIPTION: This method is used to edit an existing appointment in the database
     */
    public static void editAppointment(Integer appointmentID, String appointmentTitle, String appointmentDescription,
                                       String appointmentLocation, String appointmentType, ZonedDateTime appointmentStart,
                                       ZonedDateTime appointmentEnd, Integer customerID, Integer userID,
                                       Integer contactID) throws SQLException {

        String sql = "UPDATE appointments SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Last_Update=?," +
                "Last_Updated_By=?, Customer_ID=?, User_ID=?, Contact_ID=? WHERE Appointment_ID = ?";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        // DateTimeFormatter to format time into Timestamp pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Converts the appointment start and end times to the DateTimeFormatter pattern and turns them into strings
        String appointmentStartString = appointmentStart.format(formatter);
        String appointmentEndString = appointmentEnd.format(formatter);

        ps.setString(1, appointmentTitle);
        ps.setString(2, appointmentDescription);
        ps.setString(3, appointmentLocation);
        ps.setString(4, appointmentType);
        ps.setString(5, appointmentStartString);
        ps.setString(6, appointmentEndString);
        ps.setString(7, ZonedDateTime.now(ZoneOffset.UTC).format(formatter));
        ps.setString(8, User.getCurrentUser().getUserName());
        ps.setInt(9, customerID);
        ps.setInt(10, userID);
        ps.setInt(11, contactID);
        ps.setInt(12, appointmentID);

        try {
            ps.executeUpdate();
            ps.close();
            System.out.println("Edit Appointment Successful");
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("Edit Appointment Failed");
        }
    }

    /**
     * METHOD DESCRIPTION: This method is used to delete an appointment from the database
     */
    public static void deleteAppointment (Integer appointmentID) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Appointment_ID =?";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, appointmentID);

        try{
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * METHOD DESCRIPTION: This method is used to delete appointments associated with a customerID when that customer
     * is deleted from the database
     */
    public static void deleteCustomerAppointments (Integer customerID) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Customer_ID =?";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, customerID);

        try{
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * METHOD DESCRIPTION: This method generates the Observable List data for all appointments that start and end
     * between two times in the database
     */
    public static ObservableList<Appointment> getAppointmentsByTime(ZonedDateTime startTimeUTC, ZonedDateTime endTimeUTC)
            throws SQLException {
        ObservableList<Appointment> appointmentsByTimeList = FXCollections.observableArrayList();

        String sql = "SELECT * FROM appointments as a LEFT OUTER JOIN contacts as c ON a.Contact_ID = c.Contact_ID " +
                "WHERE Start between ? and ?";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        // Converts the start and end times from ZonedDateTime to LocalDateTime so they can be converted to timestamps
        LocalDateTime startTimeLocal = startTimeUTC.toLocalDateTime();
        LocalDateTime endTimeLocal = endTimeUTC.toLocalDateTime();

        // Converts the LocalDateTime values to timestamps
        Timestamp startTimestamp = Timestamp.valueOf(startTimeLocal);
        Timestamp endTimestamp = Timestamp.valueOf(endTimeLocal);

        ps.setTimestamp(1, startTimestamp);
        ps.setTimestamp(2, endTimestamp);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Integer appointmentID = rs.getInt("Appointment_ID");
            String appointmentTitle = rs.getString("Title");
            String appointmentDescription = rs.getString("Description");
            String appointmentLocation = rs.getString("Location");
            String appointmentType = rs.getString("Type");
            Timestamp appointmentStart = rs.getTimestamp("Start");
            Timestamp appointmentEnd = rs.getTimestamp("End");
            Timestamp createdDate = rs.getTimestamp("Create_Date");
            String createdBy = rs.getString("Created_by");
            Timestamp lastUpdate = rs.getTimestamp("Last_Update");
            String lastUpdatedBy = rs.getString("Last_Updated_By");
            Integer customerID = rs.getInt("Customer_ID");
            Integer userID = rs.getInt("User_ID");
            Integer contactID = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");

            Appointment appointmentResult = new Appointment(appointmentID, appointmentTitle, appointmentDescription,
                    appointmentLocation, appointmentType, appointmentStart, appointmentEnd,
                    createdDate, createdBy, lastUpdate, lastUpdatedBy, customerID, userID, contactID, contactName);
            appointmentsByTimeList.add(appointmentResult);
        }
        ps.close();
        return appointmentsByTimeList;
    }

    /**
     * METHOD DESCRIPTION: This method generates the Observable List data for all appointments that start on a certain
     * date and have a specific CustomerID associated with them
     */
    public static ObservableList<Appointment> getAppointmentsByDateAndCustomer(LocalDate appointmentDate, int customerID,
                                                                               int appointmentID)
            throws SQLException {
        ObservableList<Appointment> appointmentsByDateTimeList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments as a LEFT OUTER JOIN contacts as c ON a.Contact_ID = c.Contact_ID \n" +
                "WHERE DATE(Start) = ? AND Customer_ID = ? AND Appointment_ID != ?;";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ps.setString(1, appointmentDate.toString());
        ps.setInt(2, customerID);
        ps.setInt(3, appointmentID);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Integer appointmentID2 = rs.getInt("Appointment_ID");
            String appointmentTitle = rs.getString("Title");
            String appointmentDescription = rs.getString("Description");
            String appointmentLocation = rs.getString("Location");
            String appointmentType = rs.getString("Type");
            Timestamp appointmentStart = rs.getTimestamp("Start");
            Timestamp appointmentEnd = rs.getTimestamp("End");
            Timestamp createdDate = rs.getTimestamp("Create_Date");
            String createdBy = rs.getString("Created_by");
            Timestamp lastUpdate = rs.getTimestamp("Last_Update");
            String lastUpdatedBy = rs.getString("Last_Updated_By");
            Integer customerID2 = rs.getInt("Customer_ID");
            Integer userID = rs.getInt("User_ID");
            Integer contactID = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");

            Appointment appointmentResult = new Appointment(appointmentID2, appointmentTitle, appointmentDescription,
                    appointmentLocation, appointmentType, appointmentStart, appointmentEnd,
                    createdDate, createdBy, lastUpdate, lastUpdatedBy, customerID2, userID, contactID, contactName);
            appointmentsByDateTimeList.add(appointmentResult);
        }
        ps.close();
        return appointmentsByDateTimeList;
    }

    /**
     * METHOD DESCRIPTION: This method generates the Observable List data for all appointments that start within
     * 15 minutes of the current users time
     */
    public static ObservableList<Appointment> appointmentsWithin15Min() throws  SQLException {
        ObservableList<Appointment> allAppointmentsList = FXCollections.observableArrayList();

        // DateTimeFormatter to format time into Timestamp pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Gets the current time for the user, adds their time zone, converts that time to UTC and adds 15 minutes
        LocalDateTime localNow = LocalDateTime.now();
        ZonedDateTime zonedNow = localNow.atZone(User.getUserTimeZone());
        ZonedDateTime nowUTC = zonedNow.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime UTC15 = nowUTC.plusMinutes(15);

        // Formats the resulting UTC start and end times in timestamp format
        String startTime = nowUTC.format(formatter);
        String endTime = UTC15.format(formatter);

        String sql = "SELECT * FROM appointments as a LEFT OUTER JOIN contacts as c ON a.Contact_ID = c.Contact_ID " +
                "WHERE Start BETWEEN ? AND ?;";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ps.setString(1, startTime);
        ps.setString(2, endTime);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Integer appointmentID = rs.getInt("Appointment_ID");
            String appointmentTitle = rs.getString("Title");
            String appointmentDescription = rs.getString("Description");
            String appointmentLocation = rs.getString("Location");
            String appointmentType = rs.getString("Type");
            Timestamp appointmentStart = rs.getTimestamp("Start");
            Timestamp appointmentEnd = rs.getTimestamp("End");
            Timestamp createdDate = rs.getTimestamp("Create_Date");
            String createdBy = rs.getString("Created_by");
            Timestamp lastUpdate = rs.getTimestamp("Last_Update");
            String lastUpdatedBy = rs.getString("Last_Updated_By");
            Integer customerID = rs.getInt("Customer_ID");
            Integer userID = rs.getInt("User_ID");
            Integer contactID = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");

            Appointment appointmentResult = new Appointment(appointmentID, appointmentTitle, appointmentDescription,
                    appointmentLocation, appointmentType, appointmentStart, appointmentEnd,
                    createdDate, createdBy, lastUpdate, lastUpdatedBy, customerID, userID, contactID, contactName);
            allAppointmentsList.add(appointmentResult);
        }
        ps.close();
        return allAppointmentsList;
    }

    /**
     * METHOD DESCRIPTION: This method generates the Observable List data for all distinct appointment types and counts
     * how many instances of each type are in the database
     */
    public static ObservableList<Appointment> appointmentTypesAndTotals() throws SQLException {
        ObservableList<Appointment> allTypesList = FXCollections.observableArrayList();

        String sql = "SELECT Type, count(Type) FROM appointments GROUP by Type;";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            String appointmentType = rs.getString("Type");
            Integer typeCount = rs.getInt("count(Type)");

            Appointment appointmentResult = new Appointment(appointmentType, typeCount);
            allTypesList.add(appointmentResult);
        }
        ps.close();
        return allTypesList;
    }

    /**
     * METHOD DESCRIPTION: This method generates the Observable List data for all distinct appointment months and counts
     * how many instances of each month are in the database
     */
    public static ObservableList<Appointment> appointmentMonthsAndTotals() throws SQLException {
        ObservableList<Appointment> allMonthsList = FXCollections.observableArrayList();

        String sql = "SELECT MONTHNAME(Start), count(MONTHNAME(Start)) FROM appointments GROUP by MONTHNAME(Start);";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            String appointmentMonth = rs.getString("MONTHNAME(Start)");
            Integer monthCount = rs.getInt("count(MONTHNAME(Start))");

            Appointment appointmentResult = new Appointment(monthCount, appointmentMonth);
            allMonthsList.add(appointmentResult);
        }
        ps.close();
        return allMonthsList;
    }

}
