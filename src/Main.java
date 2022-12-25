import ConnectionComPort.ComPorts;
import Database.ConnectionDB;
import GUI.ChooseCounter;
import GUI.DatabaseWindow;
import GUI.LoginScreen;
import GUI.MainWindow;
import Settings.SettingsWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); //Windows Look and feel
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        ConnectionDB conn = new ConnectionDB();
        conn.initialize();

        new LoginScreen();

    }
}
