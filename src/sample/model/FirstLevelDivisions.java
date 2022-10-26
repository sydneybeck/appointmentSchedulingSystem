package sample.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.database.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * CLASS DESCRIPTION: This class is the model that encapsulates the First Level Division items
 */
public class FirstLevelDivisions {
    private int divisionID;
    private String divisionName;
    private LocalDateTime createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    private int countryID;

    public FirstLevelDivisions(int divisionID, String divisionName, int countryID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.countryID = countryID;
    }

    /**
     * METHOD DESCRIPTION: This method generates an Observable List that outputs the divisions based on a passed in
     * country
     */
    public static ObservableList<String> getStateFromCountryName(String country) throws SQLException {
        ObservableList<String> states = FXCollections.observableArrayList();

        String sql =  "SELECT c.Country, c.Country_ID,  d.Division_ID, d.Division FROM countries as c RIGHT OUTER JOIN " +
                "first_level_divisions AS d ON c.Country_ID = d.Country_ID WHERE c.Country = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, country);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            states.add(rs.getString("Division"));
        }
        ps.close();
        return states;
    }

    /**
     * METHOD DESCRIPTION: This method returns the divisionID for a passed in division
     */
    public static Integer getDivisionID(String division) throws SQLException {
        Integer divisionID = 0;
        String sql = "SELECT Division, Division_ID FROM " +
                "first_level_divisions WHERE Division = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, division);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            divisionID = rs.getInt("Division_ID");
        }
        return divisionID;
    }
}
