import Database.ConnectionDB;
import GUI.DatabaseWindow;
import GUI.LoginScreen;
import GUI.TransactionPreview;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.xml.crypto.Data;

public class Main {
    public static Logger log = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        log.info("------------------------------Starting application------------------------------");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); //Windows Look and feel
            log.info("Setting LookAndFeel to NimbusLookAndFeel.");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        try {
            log.info("Attempting to connect to database.");
            ConnectionDB conn = new ConnectionDB();
            conn.initialize();
            log.info("Succesfully connected to database!");
        }catch (Exception e){
            log.fatal(e.getMessage());
        }
        //Showing login screen
        new LoginScreen();
        log.info("Showing login screen.");
    }
}
