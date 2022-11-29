package GUI;

import ConnectionComPort.ComPorts;
import Database.ConnectionDB;
import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MainWindow extends JFrame {

    ButtonListeners buttonListeners = new ButtonListeners();

    private JPanel panel;
    private JPanel panel1;
    private JLabel lb_login;
    private JLabel lb_user;
    private JLabel lb_timeDate;
    final DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
    private int interval = 1000;
    private Calendar now;
    private DateTimeFormatter dtf;
    private LocalDateTime now2;


    private JPanel panel2;
    private JLabel lb_settings;
    private JLabel lb_counter;
    private JLabel lb_port;
    private JLabel lb_baudRate;
    private JButton btn_changeSettings;
    private JLabel lb_counterData;
    private JLabel lb_portData;
    private JLabel lb_baudRateData;

    private JPanel panel3;
    private JButton btn_printSave;
    private JButton btn_save;
    private JButton btn_database;
    private JButton btn_logout;

    private JPanel panel4;
    private JTable jt_denom;
    private JLabel lb_currency;
    private JScrollPane jsp_denom;
    private JButton btn_clear;

    private JPanel panel5;
    private JLabel lb_serialNumber;
    private JTextArea jta_serialNumber;
    private JScrollPane jsp_serialNumber;
    private JButton btn_serialNumberCopy;
    private JButton btn_serialNumberClear;
    private StringSelection stringSelection;
    private Clipboard clipboard;

    private JPanel panel6;

    String[] columnNamesEmpty = {"", "", ""};
    String[] columnNamesRSD = {"Apoen - RSD", "Broj komada", "Vrednost"};
    String[] columnNamesUSD = {"Apoen - USD", "Broj komada", "Vrednost"};
    String[] columnNamesEUR = {"Apoen - EUR", "Broj komada", "Vrednost"};
    String[][] denominationEmpty = {{"","",""},{"","",""},{"","",""},{"","",""},{"","",""},{"","",""},{"","",""},{"","",""},{"","",""},{"","",""}};
    String[][] denominationRSD = {{"10","0","0"},{"20","4","0"},{"50","5","0"},{"100","4","0"},{"200","3","0"},{"500","0","0"},{"1000","0","0"},{"2000","0","0"},{"5000","0","0"},{"Ukupno:","0","0"}};
    String[][] denominationUSD = {{"1","0","0"},{"2","0","0"},{"5","0","0"},{"10","0","0"},{"20","0","0"},{"50","0","0"},{"100","0","0"},{null,null,null},{null,null,null}, {"Ukupno:","0","0"}};
    String[][] denominationEUR = {{"5","0","0"},{"10","0","0"},{"20","0","0"},{"50","0","0"},{"100","0","0"},{"200","0","0"},{"500","0",""},{null,null,null},{null,null,null}, {"Ukupno:","0","0"}};

    public MainWindow(){
        super();
        this.setSize(900, 750);
        this.setTitle("Money Counter");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        initComponents();
        initListeners();
        this.setVisible(true);
    }

    public static void clearTable(final JTable table){
        for (int i = 0; i < table.getRowCount(); i++) {
            for(int j = 1; j < table.getColumnCount(); j=2) {
                table.setValueAt("", i, j);
            }
        }
    }

    private void initComponents() {

        panel = new JPanel();
        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();
        panel4 = new JPanel();
        panel5 = new JPanel();
        panel6 = new JPanel();

        dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        now2 = LocalDateTime.now();
        //System.out.println(dtf.format(now2));

        Border b = BorderFactory.createEtchedBorder(1);
        Font f = new Font("Arial", 1, 20);
        Font f1 = new Font("Arial", 2, 20);
        Font f2 = new Font("Arial", 0, 16);

        SpringLayout sl = new SpringLayout();
        GridLayout gl = new GridLayout(4,1);

        //panel1 setting
        panel1.setBorder(b);
        panel1.setPreferredSize(new Dimension(285,200));
        lb_user = new JLabel();

        now = Calendar.getInstance();
        lb_timeDate = new JLabel(dateFormat.format(now.getTime()));
        lb_timeDate.setBounds(100, 100, 125, 125);

        new Timer(interval, e -> {
            Calendar now1 = Calendar.getInstance();
            lb_timeDate.setText(dateFormat.format(now1.getTime()));
        }).start();
        lb_timeDate.setFont(f1);
        lb_login = new JLabel("Korisnik: ");
        lb_login.setFont(f1);
        lb_user = new JLabel(LoginScreen.getUser());
        lb_user.setFont(f);
        lb_user.setBorder(b);

        //setting layouts for panels
        panel1.setLayout(sl);
        panel2.setLayout(sl);
        panel3.setLayout(gl);
        panel4.setLayout(sl);
        panel5.setLayout(sl);

        panel1.add(lb_timeDate);
        panel1.add(lb_user);
        panel1.add(lb_login);

        sl.putConstraint(SpringLayout.WEST, lb_login, 20, SpringLayout.WEST, panel1);
        sl.putConstraint(SpringLayout.NORTH, lb_login, 25, SpringLayout.NORTH, panel1);

        sl.putConstraint(SpringLayout.WEST, lb_user, 19, SpringLayout.WEST, panel1);
        sl.putConstraint(SpringLayout.NORTH, lb_user, 80, SpringLayout.NORTH, panel1);

        sl.putConstraint(SpringLayout.WEST, lb_timeDate, 10, SpringLayout.WEST, panel1);
        sl.putConstraint(SpringLayout.NORTH, lb_timeDate, 150, SpringLayout.NORTH, panel1);


        panel2.setBorder(b);
        panel2.setPreferredSize(new Dimension(285,200));
        lb_settings = new JLabel("Trenutna podešavanja:");
        lb_settings.setFont(f2);
        lb_settings.setBorder(b);

        lb_counter = new JLabel("Brojač novca: ");
        lb_counterData = new JLabel(ChooseCounter.getMachine());

        lb_port = new JLabel("Port: ");
        lb_portData = new JLabel(ChooseCounter.getPort());

        lb_baudRate = new JLabel("Baudrate: ");
        lb_baudRateData = new JLabel(ChooseCounter.getBaudRate());

        btn_changeSettings = new JButton("Izmeni podešavanja");

        sl.putConstraint(SpringLayout.WEST, lb_settings, 30, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, lb_settings, 15, SpringLayout.NORTH, panel2);

        sl.putConstraint(SpringLayout.WEST, lb_counter, 30, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, lb_counter, 55, SpringLayout.NORTH, panel2);
        sl.putConstraint(SpringLayout.WEST, lb_counterData, 109, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, lb_counterData, 55, SpringLayout.NORTH, panel2);

        sl.putConstraint(SpringLayout.WEST, lb_port, 30, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, lb_port, 85, SpringLayout.NORTH, panel2);
        sl.putConstraint(SpringLayout.WEST, lb_portData, 60, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, lb_portData, 85, SpringLayout.NORTH, panel2);

        sl.putConstraint(SpringLayout.WEST, lb_baudRate, 30, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, lb_baudRate, 115, SpringLayout.NORTH, panel2);
        sl.putConstraint(SpringLayout.WEST, lb_baudRateData, 89, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, lb_baudRateData, 115, SpringLayout.NORTH, panel2);

        sl.putConstraint(SpringLayout.WEST, btn_changeSettings, 65, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, btn_changeSettings, 155, SpringLayout.NORTH, panel2);

        panel2.add(lb_settings);
        panel2.add(lb_counter);
        panel2.add(lb_port);
        panel2.add(lb_baudRate);
        panel2.add(btn_changeSettings);
        panel2.add(lb_counterData);
        panel2.add(lb_portData);
        panel2.add(lb_baudRateData);



        panel3.setBorder(b);
        panel3.setPreferredSize(new Dimension(285,200));
        btn_printSave = new JButton("Print i snimi");
        btn_save = new JButton("Snimi");
        btn_database = new JButton("Transakcije");
        btn_logout = new JButton("Odjava");

        panel3.add(btn_printSave);
        panel3.add(btn_save);
        panel3.add(btn_database);
        panel3.add(btn_logout);


        //PANEL 4 (DENOMINATION COUNTING RESULT AND VALUE)
        panel4.setBorder(b);
        panel4.setPreferredSize(new Dimension(375,355));

        lb_currency = new JLabel("RSD");
        lb_currency.setVisible(false);



        if (lb_currency.getText() == null){
            jt_denom = new JTable(denominationEmpty, columnNamesEmpty);
            jsp_denom = new JScrollPane(jt_denom);
            jsp_denom.setPreferredSize(new Dimension(371,350));
        }
        else if (lb_currency.getText().equals("RSD")){
            jt_denom = new JTable(denominationRSD, columnNamesRSD);
            jsp_denom = new JScrollPane(jt_denom);
            jsp_denom.setPreferredSize(new Dimension(371,350));

        }
        else if (lb_currency.getText().equals("USD")){
            jt_denom = new JTable(denominationUSD, columnNamesUSD);
            jsp_denom = new JScrollPane(jt_denom);
            jsp_denom.setPreferredSize(new Dimension(371,350));
        }
        else if (lb_currency.getText().equals("EUR")){
            jt_denom = new JTable(denominationEUR, columnNamesEUR);
            jsp_denom = new JScrollPane(jt_denom);
            jsp_denom.setPreferredSize(new Dimension(371,350));
        }
        else{
            JOptionPane.showMessageDialog(null, "Odabrana valuta nije podržana!", "Greška", JOptionPane.ERROR_MESSAGE);
        }



        jt_denom.setRowHeight(32);
        jt_denom.setFont(f2);

        panel4.add(lb_currency);
        panel4.add(jsp_denom);


        //PANEL 5(SERIAL NUMBERS)
        panel5.setBorder(b);
        panel5.setPreferredSize(new Dimension(485,355));
        lb_serialNumber = new JLabel("Serijski brojevi");
        jta_serialNumber = new JTextArea();
        jsp_serialNumber = new JScrollPane(jta_serialNumber);
        jsp_serialNumber.setPreferredSize(new Dimension(480,300));
        btn_serialNumberClear = new JButton("Očisti");
        btn_serialNumberCopy = new JButton("Kopiraj");
        btn_serialNumberCopy.setPreferredSize(new Dimension(130,35));
        btn_serialNumberClear.setPreferredSize(new Dimension(130,35));

        sl.putConstraint(SpringLayout.WEST, lb_serialNumber, 185, SpringLayout.WEST, panel5);
        sl.putConstraint(SpringLayout.NORTH, lb_serialNumber, 3, SpringLayout.NORTH, panel5);

        sl.putConstraint(SpringLayout.WEST, jsp_serialNumber, 1, SpringLayout.WEST, panel5);
        sl.putConstraint(SpringLayout.NORTH, jsp_serialNumber, 17, SpringLayout.NORTH, panel5);

        sl.putConstraint(SpringLayout.WEST, btn_serialNumberCopy, 80, SpringLayout.WEST, panel5);
        sl.putConstraint(SpringLayout.NORTH, btn_serialNumberCopy, 313, SpringLayout.NORTH, panel5);

        sl.putConstraint(SpringLayout.WEST, btn_serialNumberClear, 255, SpringLayout.WEST, panel5);
        sl.putConstraint(SpringLayout.NORTH, btn_serialNumberClear, 313, SpringLayout.NORTH, panel5);

        panel5.add(lb_serialNumber);
        panel5.add(jsp_serialNumber);
        panel5.add(btn_serialNumberCopy);
        panel5.add(btn_serialNumberClear);


        panel6.setBorder(b);
        panel6.setPreferredSize(new Dimension(670,140));

        panel.add(panel1);
        panel.add(panel2);
        panel.add(panel3);
        panel.add(panel4);
        panel.add(panel5);
        panel.add(panel6);


        setContentPane(panel);

    }

    private void initListeners() {
        btn_changeSettings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    ComPorts.closePort(SerialPort.getCommPort(ChooseCounter.port));

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                dispose();
                new ChooseCounter();

            }
        });

        btn_serialNumberClear.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jta_serialNumber.setText("");
            }
        });

        btn_serialNumberCopy.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                stringSelection = new StringSelection (jta_serialNumber.getText());
                clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard ();
                clipboard.setContents (stringSelection, null);
            }
        });

        btn_logout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                Object[] options = {"Da", "Ne"};

                int n = JOptionPane.showOptionDialog(null, "Da li ste sigurni da ćelite da se odjavite?", "Pažnja", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                System.out.println(n);


                if (n == 0) {
                    try {
                        ComPorts.closePort(SerialPort.getCommPort(ChooseCounter.port));

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    dispose();
                    new LoginScreen();
                }
            }
        });

        btn_database.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //dispose();
                new DatabaseWindow();
            }
        });

        btn_save.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {


                    ArrayList<String> denomData = new ArrayList<>();

                    denomData.add(lb_currency.getText());


                    ButtonListeners.tableTotalAmountRows(jt_denom);
                    ButtonListeners.tableTotalAmountColumns(jt_denom);


                    //getting denomination data from JTable as array of Strings
                    for (int i = 0; i < jt_denom.getRowCount() - 1; i++) {
                        denomData.add(jt_denom.getValueAt(i, 1).toString());
                    }

                    denomData.add(jt_denom.getValueAt(9, 2).toString());

                    String denomData2 = String.join(", ", denomData);


                    System.out.println(denomData2);


                    String client = JOptionPane.showInputDialog(null, "Unesite ime klijenta", "Unos podataka", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println(client);

                    String statement = "INSERT INTO transactions(Client, Timestamp, Denomination, SerialNumberOCR, SerialNumberImage) " +
                            "VALUES (?, ?, ?, '1$, AB12345678', '1110001111')";

                    try {
                        PreparedStatement pst = ConnectionDB.conn.prepareStatement(statement);


                        pst.setString(1, client);
                        pst.setString(2, dtf.format(now2));
                        pst.setString(3, denomData2);

                        pst.execute();
                        pst.close();

                        System.out.println("Transaction saved successfully");


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

        });

        btn_printSave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int row = DatabaseWindow.jt_transactions.getRowCount();
                System.out.println(row);

                ArrayList<String> denomData = new ArrayList<>();

                denomData.add(lb_currency.getText());


                ButtonListeners.tableTotalAmountRows(jt_denom);
                ButtonListeners.tableTotalAmountColumns(jt_denom);


                //getting denomination data from JTable as array of Strings
                for (int i = 0; i < jt_denom.getRowCount() - 1; i++) {
                    denomData.add(jt_denom.getValueAt(i, 1).toString());
                }

                denomData.add(jt_denom.getValueAt(9, 2).toString());

                String denomData2 = String.join(", ", denomData);


                System.out.println(denomData2);


                String client = JOptionPane.showInputDialog(null, "Unesite ime klijenta", "Unos podataka", JOptionPane.INFORMATION_MESSAGE);
                System.out.println(client);

                String statement = "INSERT INTO transactions(Client, Timestamp, Denomination, SerialNumberOCR, SerialNumberImage) " +
                        "VALUES (?, ?, ?, '1$, AB12345678', '1110001111')";

                try {
                    PreparedStatement pst = ConnectionDB.conn.prepareStatement(statement);


                    pst.setString(1, client);
                    pst.setString(2, dtf.format(now2));
                    pst.setString(3, denomData2);

                    pst.execute();
                    pst.close();

                    System.out.println("Transaction saved successfully");


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                int row1 = DatabaseWindow.jt_transactions.getRowCount() + 1;

                String Id = DatabaseWindow.jt_transactions.getValueAt(row1, 0).toString();
                String user = LoginScreen.getUser();
                String client2 = DatabaseWindow.jt_transactions.getValueAt(row1, 1).toString();
                String file = "test.pdf";
                String denominationString = DatabaseWindow.jt_transactions.getValueAt(row1, 3).toString();
                String[] denomination = denominationString.split(", ");
                String serialOcrString = DatabaseWindow.jt_transactions.getValueAt(row1, 4).toString();
                String[] serialOcr = serialOcrString.split(", ");
                String serialImageString = DatabaseWindow.jt_transactions.getValueAt(row1, 5).toString();
                String[] serialImage = serialImageString.split(", ");

                buttonListeners.PDFPrinter(PdfExport.createPdfFile(Id, user, client2, file, denomination, serialOcr, serialImage));

            }
        });
    }



}
