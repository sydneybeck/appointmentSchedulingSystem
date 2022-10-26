package sample.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String databaseName = "client_schedule";
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//localhost:3306/";
    private static final String DB_URL = protocol + vendorName + ipAddress + databaseName + "?connectionTimeZone=SERVER";
    private static final String MYSQLJBCDriver = "com.mysql.cj.jdbc.Driver";
    private static final String username = "sqlUser";
    private static final String password = "Passw0rd!";
    public static Connection connection = null;

    public static Connection startConnection() {
        try {
            Class.forName(MYSQLJBCDriver);
            connection = DriverManager.getConnection(DB_URL, username, password);
        }
        catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try {
            connection.close();
        }
        catch (Exception e){
        }
    }
}
