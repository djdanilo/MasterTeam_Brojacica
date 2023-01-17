package Database;

import java.sql.*;

public class ConnectionDB {

    public static Connection conn = null;
    public Statement st = null;

    public void initialize(){
        initializeDB();
    }

    private void initializeDB() {
        final String JDBC_DRIVER = "org.sqlite.JDBC";
        final String DB_URL = "jdbc:sqlite:MoneyCounter_App.db";

        final String user = "";
        final String password = "";

        System.out.println("Attempting to connect to database.");
        try {
            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL);

            st = conn.createStatement();

            st.executeUpdate(sqlQuery);
            st.executeUpdate(sqlQuery1);
            st.executeUpdate(sqlQuery2);
            st.executeUpdate(sqlQuery3);

            System.out.println("Succesfully connected to database!");

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    String sqlQuery = "CREATE TABLE IF NOT EXISTS users(\n" +
            "    Id integer primary key autoincrement not null,\n" +
            "    userName text not null,\n" +
            "    password text not null\n" +
            ");";
    String sqlQuery1 = "CREATE TABLE IF NOT EXISTS transactions(\n" +
            "    Id INTEGER primary key autoincrement not null,\n" +
            "    Client text not null,\n" +
            "    Timestamp datetime not null,\n" +
            "    Denomination text not null,\n" +
            "    SerialNumberOCR text,\n" +
            "    SerialNumberImage text\n" +
            ")";
    String sqlQuery2 = "CREATE TABLE IF NOT EXISTS machines(\n" +
            "    machine text not null\n" +
            ")";
    String sqlQuery3 = "INSERT INTO users values (null, 'admin', 'admin');";

}
