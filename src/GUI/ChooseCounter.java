package GUI;

import ConnectionComPort.ComPorts;
import Database.ConnectionDB;
import MoneyCounters.K2;
import MoneyCounters.MIB9;
import MoneyCounters.ML2F;
import MoneyCounters.SB9;
import com.fazecast.jSerialComm.SerialPort;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class ChooseCounter extends JFrame {
    public static Logger log = Logger.getLogger(ChooseCounter.class.getName());
    private static JComboBox<String> cb_chooseMachine;
    public static JComboBox<String> cb_chooseComPort;
    private static JComboBox<String> cb_baudrate;
    private JButton btn_confirm;
    private JButton btn_reset;
    private JButton btn_refreshPort;
    public static String port;


    public ChooseCounter() {
        super();
        this.setSize(400, 350);
        this.setTitle("Odabir mašine za brojanje novca");
        this.setLocationRelativeTo(LoginScreen.jFrame);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        initComponents();
        initListeners();
        this.setVisible(true);
    }

    private void initComponents() {

        //setting a layout for the window
        JPanel panel = new JPanel();
        SpringLayout sl = new SpringLayout();
        panel.setLayout(sl);
        Border b = BorderFactory.createEtchedBorder(1);

        Font f = new Font("Arial", Font.BOLD, 12);

        //initializing components
        JLabel lb_title = new JLabel("PODEŠAVANJA BROJAČICE");
        lb_title.setFont(new Font("Arial", Font.BOLD, 14));
        lb_title.setBorder(b);

        JLabel lb_chooseMachine = new JLabel("Izaberite brojač novca:");
        lb_chooseMachine.setFont(f);

        JLabel lb_chooseComPort = new JLabel("Izaberite port:");
        lb_chooseComPort.setFont(f);

        JLabel lb_baudrate = new JLabel("Izaberite brzinu:");
        lb_baudrate.setFont(f);

        cb_chooseMachine = new JComboBox<>();

        //adding data to cb_chooseMachine
        try {

            String statement = "SELECT * FROM machines;";

            Statement stmt = ConnectionDB.conn.createStatement();
            ResultSet rs = stmt.executeQuery(statement);

            while (rs.next()) {
                String machine = rs.getString(1);
                cb_chooseMachine.addItem(machine);
                log.info("Adding machines " + machine + " to be available");
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

        cb_chooseComPort = new JComboBox<>();

        ComPorts.listSerials(cb_chooseComPort);

        cb_baudrate = new JComboBox<>();

        //adding data for baudrate
        cb_baudrate.addItem("9600");
        cb_baudrate.addItem("14400");
        cb_baudrate.addItem("19200");
        cb_baudrate.addItem("38400");
        cb_baudrate.addItem("57600");
        cb_baudrate.addItem("115200");
        cb_baudrate.setSelectedItem("115200");

        btn_confirm = new JButton("POTVRDI");
        btn_confirm.setPreferredSize(new Dimension(100, 50));

        btn_reset = new JButton("ODJAVA");
        btn_reset.setPreferredSize(new Dimension(100, 50));

        btn_refreshPort = new JButton("Osveži");
        btn_refreshPort.setPreferredSize(new Dimension(70, 26));
        btn_refreshPort.setMargin(new Insets(1, 0, 1, 0));

        //setting the placement of components
        sl.putConstraint(SpringLayout.WEST, lb_title, 100, SpringLayout.WEST, panel);
        sl.putConstraint(SpringLayout.NORTH, lb_title, 25, SpringLayout.NORTH, panel);

        sl.putConstraint(SpringLayout.WEST, lb_chooseMachine, 60, SpringLayout.WEST, panel);
        sl.putConstraint(SpringLayout.NORTH, lb_chooseMachine, 100, SpringLayout.NORTH, panel);

        sl.putConstraint(SpringLayout.WEST, cb_chooseMachine, 200, SpringLayout.WEST, panel);
        sl.putConstraint(SpringLayout.NORTH, cb_chooseMachine, 94, SpringLayout.NORTH, panel);

        sl.putConstraint(SpringLayout.WEST, lb_chooseComPort, 60, SpringLayout.WEST, panel);
        sl.putConstraint(SpringLayout.NORTH, lb_chooseComPort, 130, SpringLayout.NORTH, panel);

        sl.putConstraint(SpringLayout.WEST, cb_chooseComPort, 200, SpringLayout.WEST, panel);
        sl.putConstraint(SpringLayout.NORTH, cb_chooseComPort, 124, SpringLayout.NORTH, panel);

        sl.putConstraint(SpringLayout.WEST, btn_refreshPort, 272, SpringLayout.WEST, panel);
        sl.putConstraint(SpringLayout.NORTH, btn_refreshPort, 124, SpringLayout.NORTH, panel);

        sl.putConstraint(SpringLayout.WEST, lb_baudrate, 60, SpringLayout.WEST, panel);
        sl.putConstraint(SpringLayout.NORTH, lb_baudrate, 160, SpringLayout.NORTH, panel);

        sl.putConstraint(SpringLayout.WEST, cb_baudrate, 200, SpringLayout.WEST, panel);
        sl.putConstraint(SpringLayout.NORTH, cb_baudrate, 154, SpringLayout.NORTH, panel);

        sl.putConstraint(SpringLayout.WEST, btn_confirm, 90, SpringLayout.WEST, panel);
        sl.putConstraint(SpringLayout.NORTH, btn_confirm, 225, SpringLayout.NORTH, panel);

        sl.putConstraint(SpringLayout.WEST, btn_reset, 210, SpringLayout.WEST, panel);
        sl.putConstraint(SpringLayout.NORTH, btn_reset, 225, SpringLayout.NORTH, panel);

        panel.add(lb_title);
        panel.add(lb_chooseMachine);
        panel.add(lb_chooseComPort);
        panel.add(lb_baudrate);
        panel.add(cb_chooseMachine);
        panel.add(cb_chooseComPort);
        panel.add(cb_baudrate);
        panel.add(btn_confirm);
        panel.add(btn_reset);
        panel.add(btn_refreshPort);

        setContentPane(panel);
    }

    public static String getMachine() {
        return (String) cb_chooseMachine.getSelectedItem();
    }

    public static String getPort() {
        return String.valueOf(cb_chooseComPort.getSelectedItem());
    }

    public static String getBaudRate() {
        return (String) cb_baudrate.getSelectedItem();
    }


    private void initListeners() {
        btn_confirm.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cb_chooseComPort.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(null, "Niste odabrali com port!", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
                    log.error("Port wasn't chosen.");
                } else {
                    ComPorts comPorts = new ComPorts();
                    port = Objects.requireNonNull(cb_chooseComPort.getSelectedItem()).toString();

                    try {
                        SerialPort serialPort = comPorts.openSerialPort(SerialPort.getCommPort(port), 0, (String) cb_baudrate.getSelectedItem());

                        if (serialPort.isOpen()) {
                            JOptionPane.showMessageDialog(null, "Uspešno ste se povezali na " + serialPort.getSystemPortName() + ".", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
                            log.info("Connected to port " + serialPort);
                        } else {
                            JOptionPane.showMessageDialog(null, "Odabrani port " + serialPort.getSystemPortName() + " je već otvoren!", "Greška", JOptionPane.ERROR_MESSAGE);
                            log.error("Error while opening the port.");
                        }

                        if (Objects.equals(cb_chooseMachine.getSelectedItem(), "SB-9")) {
                            SB9.readingData(serialPort);
                            log.info("Opening port with settings for SB-9");
                        } else if (Objects.equals(cb_chooseMachine.getSelectedItem(), "ML-2F")
                                || Objects.equals(cb_chooseMachine.getSelectedItem(), "ML-2FS")) {
                            ML2F.readingData(serialPort);
                            log.info("Opening port with settings for ML-2F/ML-2FS");
                        } else if (Objects.equals(cb_chooseMachine.getSelectedItem(), "MIB-9")) {
                            MIB9.readingData(serialPort);
                            log.info("Opening port with settings for MIB-9");
                        }else if (Objects.equals(cb_chooseMachine.getSelectedItem(), "K2")) {
                            log.info("Opening port with settings for K2");
                            K2.readingData(serialPort);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        log.error(e1.getMessage());
                    }
                    dispose();
                    new MainWindow();
                    log.info("Showing MainWindow, with following settings: " + cb_chooseMachine.getSelectedItem() + ", " + cb_chooseComPort.getSelectedItem()
                            + ", " + cb_baudrate.getSelectedItem() + ".");
                }

            }
        });
        btn_refreshPort.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cb_chooseComPort.getSelectedItem() == null) {
                    ComPorts.listSerials(cb_chooseComPort);
                    log.info("Refreshing ports.");
                } else {
                    cb_chooseComPort.removeAllItems();
                }
            }
        });
        btn_reset.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginScreen();
                log.info("Logging out. Showing LoginScreen.");
            }
        });
    }
}
