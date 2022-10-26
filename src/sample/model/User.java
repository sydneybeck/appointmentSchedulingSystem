package sample.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.database.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * CLASS DESCRIPTION: This class is the model that encapsulates the User items
 */
public class User {
    private int userID;
    private String userName;
    private String password;
    private LocalDateTime createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    private String logonTimestamp;
    private Integer logonUserID;
    private String logonUserName;
    private String logonSuccess;

    public User (String logonTimestamp, int logonUserID, String logonUserName, String logonSuccess) {
        this.logonTimestamp = logonTimestamp;
        this.logonUserID = logonUserID;
        this.logonUserName = logonUserName;
        this.logonSuccess = logonSuccess;
    }

    public User(int userID, String userName, String password, LocalDateTime createDate, String createdBy,
                Timestamp lastUpdate, String lastUpdatedBy) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public User(int userID, String userName, String password) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
    }

    public User(String userName, int userID) {
        this.userName = userName;
        this.userID = userID;
    }

    public User(int userID) {
        this.userID = userID;
    }

    /**
     * METHOD DESCRIPTION: This method is a getter for the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * METHOD DESCRIPTION: This method is a getter for the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * METHOD DESCRIPTION: This method is a getter for the currentUser
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * METHOD DESCRIPTION: This method is a getter for the userTimeZone
     */
    public static ZoneId getUserTimeZone() {
        return userTimeZone;
    }

    private static User currentUser;
    private static Locale locale;
    private static ZoneId userTimeZone;

    /**
     * METHOD DESCRIPTION: This method generates an Observable List that contains all the UserIDs
     */
    public static ObservableList<Integer> getAllUserID() throws SQLException {
        ObservableList<Integer> allUserID = FXCollections.observableArrayList();
        String sql = "SELECT DISTINCT User_ID" +
                " FROM users;";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            allUserID.add(rs.getInt("User_ID"));
        }
        return allUserID;
    }

    /**
     * METHOD DESCRIPTION: This method returns the userID associated with a passed in userName
     */
    public static Integer matchUserID(String userName) throws SQLException {
        Integer userID = -1;
        String sql = "SELECT User_ID, User_Name " + "FROM users WHERE User_Name = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, userName);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            userID = rs.getInt("User_ID");
        }
        return userID;
    }

    /**
     * METHOD DESCRIPTION: This method creates a table to hold logon data, then adds logon data to it
     */
    public static void addToLogonRecord(Boolean logon, String userName) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");

        Integer userID = User.matchUserID(userName);
        String timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(formatter);
        String logonString = String.valueOf(logon);

        String sql = "CREATE TABLE IF NOT EXISTS logons (Logon_Time VARCHAR(30), User_ID INT, User_Name VARCHAR(30), " +
                "Login_Successful VARCHAR(30))";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);

        String sql2 = "INSERT INTO logons (Logon_Time, User_ID, User_Name, Login_Successful) VALUES (?,?,?,?)";

        PreparedStatement ps2 = DBConnection.getConnection().prepareStatement(sql2);

        ps2.setString(1, timestamp);
        ps2.setInt(2, userID);
        ps2.setString(3, userName);
        ps2.setString(4, logonString);

        ps.executeUpdate();
        ps2.executeUpdate();
    }

    /**
     * METHOD DESCRIPTION: This method creates an Observable List that contains all of the logon data
     */
    public static ObservableList<User> getAllLogonData() throws SQLException, ClassNotFoundException {
        ObservableList<User> allLoginsList = FXCollections.observableArrayList();

        String sql = "SELECT * FROM logons;";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String logonTimestamp = rs.getString("Logon_Time");
            Integer logonUserID = rs.getInt("User_ID");
            String logonUserName = rs.getString("User_Name");
            String logonSuccess = rs.getString("Login_Successful");

            User userResult = new User(logonTimestamp, logonUserID, logonUserName, logonSuccess);
            allLoginsList.add(userResult);
        }
        ps.close();
        return allLoginsList;
    }

    /**
     * METHOD DESCRIPTION: This method returns a boolean based on if a passed in userName and password combination
     * can be found in the database
     */
    public static boolean loginValidation(String userName, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE User_Name = ? AND Password = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, userName);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            ps.close();
            return false;
        }
        else {
            currentUser = new User(rs.getString("User_Name"), rs.getInt("User_ID"));
            locale = Locale.getDefault();
            userTimeZone = ZoneId.systemDefault();
            System.out.println("User: " + currentUser.toString());
            ps.close();
            return true;
        }
    }
}

