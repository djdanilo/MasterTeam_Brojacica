package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConnectionDB {

    public static Connection conn = null;
    public Statement st = null;

    public void initialize(){
        initializeDB();
    }

    private void initializeDB(){
        final String JDBC_DRIVER = "org.sqlite.JDBC";
        final String DB_URL = "jdbc:sqlite:MoneyCounter_App.db";

        final String user = "";
        final String password = "";

        System.out.println("Attempting to connect to database.");
        try{
            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(DB_URL);

            st = conn.createStatement();

            System.out.println("Succesfully connected to database!");

        }catch (Exception e){
            e.printStackTrace();

        }
    }

}
