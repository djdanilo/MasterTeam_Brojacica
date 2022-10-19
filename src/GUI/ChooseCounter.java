package GUI;

import ConnectionComPort.ComPorts;
import com.fazecast.jSerialComm.SerialPort;
import com.sun.tools.javac.Main;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class ChooseCounter extends JFrame {

    private JPanel panel;
    private JLabel lb_title;
    private JLabel lb_chooseMachine;
    private JLabel lb_chooseComPort;
    private JLabel lb_baudrate;
    private static JComboBox cb_chooseMachine;
    public static JComboBox cb_chooseComPort;
    private static JComboBox cb_baudrate;
    private JButton btn_confirm;
    private JButton btn_reset;
    private JButton btn_refreshPort;
    public static String port;


    public ChooseCounter() {
        super();
        this.setSize(400, 350);
        this.setTitle("Odabir mašine za brojanje novca");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        initComponents();
        initListeners();
        this.setVisible(true);

    }

    private void initComponents() {

        //setting a layout for the window
        panel = new JPanel();
        SpringLayout sl = new SpringLayout();
        panel.setLayout(sl);
        Border b = BorderFactory.createEtchedBorder(1);

        Font f = new Font("Arial", 1, 12);

        //initializing components
        lb_title = new JLabel("PODEŠAVANJA BROJAČICE");
        lb_title.setFont(new Font("Arial", 1, 14));
        lb_title.setBorder(b);

        lb_chooseMachine = new JLabel("Izaberite brojač novca:");
        lb_chooseMachine.setFont(f);

        lb_chooseComPort = new JLabel("Izaberite port:");
        lb_chooseComPort.setFont(f);

        lb_baudrate = new JLabel("Izaberite brzinu:");
        lb_baudrate.setFont(f);

        cb_chooseMachine = new JComboBox();

        //adding data to cb_chooseMachine
        cb_chooseMachine.addItem("Kisan K2");
        cb_chooseMachine.addItem("Hyundai MIB SB-9");
        cb_chooseMachine.addItem("Hyundai MIB MIB-9");
        cb_chooseMachine.addItem("Hyundai MIB MIB-11");
        cb_chooseMachine.addItem("Lidix ML-2F");
        cb_chooseMachine.addItem("Lidix ML-2FS");
        cb_chooseMachine.addItem("Ribao BC-55");
        cb_chooseMachine.addItem("Ribao BC-40");

        cb_chooseComPort = new JComboBox();

        cb_baudrate = new JComboBox();

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
        btn_refreshPort.setPreferredSize(new Dimension(70,26));
        btn_refreshPort.setMargin(new Insets(1,0,1,0));

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

    public static String getMachine(){
        return (String) cb_chooseMachine.getSelectedItem();
    }

    public static String getPort(){
        return String.valueOf(cb_chooseComPort.getSelectedItem());
    }

    public static String getBaudRate(){
        return (String) cb_baudrate.getSelectedItem();
    }

    private void initListeners() {
        btn_confirm.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cb_chooseComPort.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(null, "Niste odabrali com port!", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
                }else {
                    ComPorts comPorts = new ComPorts();
                    port = cb_chooseComPort.getSelectedItem().toString().replace("[","").replace("]","");

                    SerialPort serialPort = comPorts.openSerialPort(SerialPort.getCommPort(port), 10L, (String) cb_baudrate.getSelectedItem());

                    if (serialPort.isOpen()){
                        JOptionPane.showMessageDialog(null, "Uspešno ste se povezali na " + serialPort.getSystemPortName() + ".", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "Odabrani port " + serialPort.getSystemPortName() + " je već otvoren!", "Greška", JOptionPane.ERROR_MESSAGE);
                    }

                    dispose();
                    new MainWindow();
                }

            }
        });

        btn_refreshPort.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ComPorts comPorts = new ComPorts();
                if (cb_chooseComPort.getSelectedItem() == null) {
                    cb_chooseComPort.addItem(comPorts.listSerials());
                }else{
                    cb_chooseComPort.removeAllItems();
                }
            }
        });

        btn_reset.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginScreen();
            }
        });
    }




}
