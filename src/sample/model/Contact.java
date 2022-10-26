package sample.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.database.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * CLASS DESCRIPTION: This class is the model that encapsulates the Contact items
 */
public class Contact {
    private int contactID;
    private String contactName;
    private String email;

    public Contact (int contactID, String contactName, String email) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.email = email;
    }

    /**
     * METHOD DESCRIPTION: This method generates an Observable List with all the distinct contact names in the database
     */
    public static ObservableList<String> getAllContactNames() throws SQLException, ClassNotFoundException {
        ObservableList<String> allContactNames = FXCollections.observableArrayList();
        String sql = "SELECT DISTINCT Contact_Name" +
                " FROM contacts;";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            allContactNames.add(rs.getString("Contact_Name"));
        }
        return allContactNames;
    }

    /**
     * METHOD DESCRIPTION: This method returns the matching contactID for a passed in contact name
     */
    public static Integer matchContactID(String contactName) throws SQLException, ClassNotFoundException {
        Integer contactID = -1;
        String sql = "SELECT Contact_ID, Contact_Name " + "FROM contacts WHERE Contact_Name = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, contactName);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            contactID = rs.getInt("Contact_ID");
        }
        return contactID;
    }
}
