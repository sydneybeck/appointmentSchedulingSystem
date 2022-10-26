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
 * CLASS DESCRIPTION: This class is the model that encapsulates the Country items
 */
public class Country {
    private int countryID;
    private String countryName;
    private LocalDateTime createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;

    public Country(int countryID, String countryName, LocalDateTime createDate, String createdBy, Timestamp lastUpdate,
                   String lastUpdatedBy) {
        this.countryID = countryID;
        this.countryName = countryName;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Country(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
    }

    public Country(String countryName) {
        this.countryName = countryName;
    }

    /**
     * METHOD DESCRIPTION: This method generates an Observable List that contains all the country names in the database
     */
    public static ObservableList<String> getAllCountries() throws SQLException {
        ObservableList<String> allCountriesList = FXCollections.observableArrayList();

        String sql = "SELECT Country FROM countries";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            allCountriesList.add(rs.getString("Country"));
        }
        ps.close();
        return allCountriesList;
    }
}
