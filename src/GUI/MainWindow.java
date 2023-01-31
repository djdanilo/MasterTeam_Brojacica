package GUI;

import ConnectionComPort.ComPorts;
import Database.ConnectionDB;
import Settings.SettingsWindow;
import com.fazecast.jSerialComm.SerialPort;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;


public class MainWindow {
    public static Logger log = Logger.getLogger(MainWindow.class.getName());
    public static JFrame frame;
    ButtonListeners buttonListeners = new ButtonListeners();
    public static JPanel panel;
    public static JLabel lb_timeDate;
    final DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
    private DateTimeFormatter dtf;
    private LocalDateTime now2;
    private Calendar now1;
    public static ArrayList<String> ocrDenomination = new ArrayList<>();
    public static JTabbedPane jtp_serialNumber;
    public static JTextArea jt_logs;
    String pass = "627862";
    private JButton btn_changeSettings;
    private JButton btn_printSave;
    public static JList<String> jList_ocrText;
    public static JList<ImageIcon> jList_serialImage;
    private JButton btn_save;
    private JButton btn_database;
    private JButton btn_logout;
    public static JTable jt_denom;
    public static JLabel lb_currency = new JLabel(" ");
    public static JScrollPane jsp_denom;
    public static JTextArea jt_serialBinary;
    public static DefaultListModel<String> model_ocrText;
    public static DefaultListModel<ImageIcon> model_serialImage;
    private JButton btn_serialNumberCopy;
    private JButton btn_serialNumberClear;
    private StringSelection stringSelection;
    private Clipboard clipboard;
    private JButton btn_settings;
    private final String[] columnNames = {"Apoen - " + lb_currency.getText(), "Broj komada", "Vrednost"};
    private final String[][] denominationEmpty = {{"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}};

    public MainWindow() {
        frame = new JFrame();
        frame.setSize(900, 750);
        frame.setTitle("Money Counter");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        initComponents();
        initListeners();
        frame.setVisible(true);
    }

    private void initComponents() {

        panel = new JPanel();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();
        JPanel panel5 = new JPanel();
        JPanel panel6 = new JPanel();

        dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        now2 = LocalDateTime.now();

        Border b = BorderFactory.createEtchedBorder(1);
        Font f = new Font("Arial", Font.BOLD, 20);
        Font f1 = new Font("Arial", Font.ITALIC, 20);
        Font f2 = new Font("Arial", Font.PLAIN, 16);

        SpringLayout sl = new SpringLayout();
        GridLayout gl = new GridLayout(4, 1);

        //panel1 setting
        panel1.setBorder(b);
        panel1.setPreferredSize(new Dimension(285, 200));
        JLabel lb_user = new JLabel();

        Calendar now = Calendar.getInstance();
        lb_timeDate = new JLabel(dateFormat.format(now.getTime()));
        lb_timeDate.setBounds(100, 100, 125, 125);

        int interval = 1000;
        new Timer(interval, e -> {
            now1 = Calendar.getInstance();
            lb_timeDate.setText(dateFormat.format(now1.getTime()));
        }).start();
        lb_timeDate.setFont(f1);
        JLabel lb_login = new JLabel("Korisnik: ");
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
        panel2.setPreferredSize(new Dimension(285, 200));
        JLabel lb_settings = new JLabel("Trenutna podešavanja:");
        lb_settings.setFont(f2);
        lb_settings.setBorder(b);

        JLabel lb_counter = new JLabel("Brojač novca: ");
        JLabel lb_counterData = new JLabel(ChooseCounter.getMachine());

        JLabel lb_port = new JLabel("Port: ");
        JLabel lb_portData = new JLabel(ChooseCounter.getPort());

        JLabel lb_baudRate = new JLabel("Baudrate: ");
        JLabel lb_baudRateData = new JLabel(ChooseCounter.getBaudRate());

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
        panel3.setPreferredSize(new Dimension(285, 200));
        btn_printSave = new JButton("Štampaj transakciju");
        btn_save = new JButton("Snimi transakciju");
        btn_database = new JButton("Transakcije");
        btn_logout = new JButton("Odjava");

        panel3.add(btn_printSave);
        panel3.add(btn_save);
        panel3.add(btn_database);
        panel3.add(btn_logout);

        //PANEL 4 (DENOMINATION COUNTING RESULT AND VALUE)
        panel4.setBorder(b);
        panel4.setPreferredSize(new Dimension(375, 355));

        lb_currency.setVisible(false);

        jt_denom = new JTable(denominationEmpty, columnNames);
        jsp_denom = new JScrollPane(jt_denom);
        jsp_denom.setPreferredSize(new Dimension(371, 350));

        jt_denom.setRowHeight(32);
        jt_denom.setFont(f2);
        jt_denom.setEnabled(false);

        panel4.add(lb_currency);
        panel4.add(jsp_denom);

        //PANEL 5(SERIAL NUMBERS)
        panel5.setBorder(b);
        panel5.setPreferredSize(new Dimension(485, 355));

        jtp_serialNumber = new JTabbedPane();

        jt_serialBinary = new JTextArea();
        jt_serialBinary.setVisible(false);

        model_ocrText = new DefaultListModel<>();
        model_serialImage = new DefaultListModel<>();
        jList_ocrText = new JList<>();
        jList_serialImage = new JList<>();

        JScrollPane jsp_serialNumber = new JScrollPane(jList_ocrText);
        JScrollPane jsp_serialNumberImage = new JScrollPane(jList_serialImage);
        JScrollPane jsp_serialNumberBinary = new JScrollPane(jt_serialBinary);
        jsp_serialNumberBinary.setVisible(false);

        jsp_serialNumber.setPreferredSize(new Dimension(480, 280));
        jsp_serialNumberImage.setPreferredSize(new Dimension(480, 280));
        jsp_serialNumberBinary.setPreferredSize(new Dimension(480, 280));

        jtp_serialNumber.add("Serijski brojevi", jsp_serialNumber);
        jtp_serialNumber.add("Slike serijskih brojeva", jsp_serialNumberImage);
        jtp_serialNumber.add("Binary Serial Number", jsp_serialNumberBinary);
        jtp_serialNumber.removeTabAt(2);

        btn_serialNumberClear = new JButton("Očisti");
        btn_serialNumberCopy = new JButton("Kopiraj");
        btn_serialNumberCopy.setPreferredSize(new Dimension(130, 35));
        btn_serialNumberClear.setPreferredSize(new Dimension(130, 35));

        sl.putConstraint(SpringLayout.WEST, jsp_serialNumber, 1, SpringLayout.WEST, panel5);
        sl.putConstraint(SpringLayout.NORTH, jsp_serialNumber, 17, SpringLayout.NORTH, panel5);

        sl.putConstraint(SpringLayout.WEST, btn_serialNumberCopy, 80, SpringLayout.WEST, panel5);
        sl.putConstraint(SpringLayout.NORTH, btn_serialNumberCopy, 313, SpringLayout.NORTH, panel5);

        sl.putConstraint(SpringLayout.WEST, btn_serialNumberClear, 255, SpringLayout.WEST, panel5);
        sl.putConstraint(SpringLayout.NORTH, btn_serialNumberClear, 313, SpringLayout.NORTH, panel5);

        //panel5.add(jsp_serialNumber);
        panel5.add(btn_serialNumberCopy);
        panel5.add(btn_serialNumberClear);
        panel5.add(jtp_serialNumber);

        panel6.setBorder(b);
        panel6.setPreferredSize(new Dimension(670, 140));
        panel6.setLayout(sl);

        JLabel lb_logs = new JLabel("Logovi aplikacije");
        lb_logs.setFont(f2);


        jt_logs = new JTextArea();
        jt_logs.setEditable(false);
        JScrollPane jsp_logs = new JScrollPane(jt_logs);
        jsp_logs.setPreferredSize(new Dimension(500, 110));

        jt_logs.append(lb_timeDate.getText() + "     Otvoren port " + ChooseCounter.getPort() + "\n");
        jt_logs.append(lb_timeDate.getText() + "     Povezana brojačica " + ChooseCounter.getMachine() + "\n");

        btn_settings = new JButton("Podešavanja");
        btn_settings.setPreferredSize(new Dimension(150, 40));

        sl.putConstraint(SpringLayout.WEST, lb_logs, 185, SpringLayout.WEST, panel6);
        sl.putConstraint(SpringLayout.NORTH, lb_logs, 3, SpringLayout.NORTH, panel6);

        sl.putConstraint(SpringLayout.WEST, jsp_logs, 5, SpringLayout.WEST, panel6);
        sl.putConstraint(SpringLayout.NORTH, jsp_logs, 23, SpringLayout.NORTH, panel6);

        sl.putConstraint(SpringLayout.WEST, btn_settings, 510, SpringLayout.WEST, panel6);
        sl.putConstraint(SpringLayout.NORTH, btn_settings, 50, SpringLayout.NORTH, panel6);

        panel6.add(lb_logs);
        panel6.add(jsp_logs);
        panel6.add(btn_settings);

        panel.add(panel1);
        panel.add(panel2);
        panel.add(panel3);
        panel.add(panel4);
        panel.add(panel5);
        panel.add(panel6);

        frame.add(panel);

        frame.setContentPane(panel);
    }

    private void initListeners() {
        btn_changeSettings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    ComPorts.closePort(SerialPort.getCommPort(ChooseCounter.getPort()));
                    log.info("ChangeSettings pressed. Closing port: " + ChooseCounter.getPort() + " and showing ChooseCounter window");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    log.error(ex.getMessage());
                }
                frame.dispose();
                new ChooseCounter();
            }
        });
        btn_serialNumberClear.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model_ocrText.removeAllElements();
                model_serialImage.removeAllElements();
                log.info("Clearing OCR and Serial Image data.");
                jt_logs.append(lb_timeDate.getText() + "     Brišem polja sa serijskim brojevima\n");
            }
        });
        btn_serialNumberCopy.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selection = "";
                for (int i = 0; i < model_ocrText.size(); i++) {
                    selection += model_ocrText.get(i) + "\n";
                }
                stringSelection = new StringSelection(selection);
                clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                log.info("Serial numbers copied to clipboard.");
                jt_logs.append(lb_timeDate.getText() + "     Serijski brojevi kopirani\n");
            }
        });
        btn_logout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Object[] options = {"Da", "Ne"};
                int n = JOptionPane.showOptionDialog(null, "Da li ste sigurni da želite da se odjavite?", "Pažnja", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                if (n == 0) {
                    try {
                        ComPorts.closePort(SerialPort.getCommPort(ChooseCounter.port));
                        log.info("Logout pressed. User log off and closing port " + ChooseCounter.port + ".");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        log.error(ex.getMessage());
                    }
                    frame.dispose();
                    new LoginScreen();
                    log.info("Showing LoginScreen");
                }
            }
        });
        btn_database.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //dispose();
                new DatabaseWindow();
                log.info("Opening Database window");
                jt_logs.append(lb_timeDate.getText() + "     Otvaram transakcije\n");
            }
        });
        btn_save.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (jt_denom.getValueAt(1, 1).equals("")) {
                    JOptionPane.showMessageDialog(null, "Nema podataka u tabeli", "Greška!", JOptionPane.ERROR_MESSAGE);
                } else {
                    //array with denomination and total amount.
                    ArrayList<String> denomData = new ArrayList<>();
                    //adding the first element to denomData, so we know which currency is counted
                    denomData.add(lb_currency.getText());
                    //getting denomination data from JTable as array of Strings
                    for (int i = 0; i < jt_denom.getRowCount() - 1; i++) {
                        if ((jt_denom.getValueAt(i, 1) == null) || (jt_denom.getValueAt(i, 1).equals(""))) {
                            continue;
                        }
                        denomData.add(jt_denom.getValueAt(i, 1).toString());
                    }
                    //getting the total amount as the last element of the array
                    denomData.add(jt_denom.getValueAt(9, 2).toString());
                    //converting denomData array to a string delimited with comma, so it can be saved to SQLite
                    String denomData2 = String.join(", ", denomData);


                    //array for OCR value of serial numbers
                    ArrayList<String> ocrData = new ArrayList<>();
                    //getting OCR value of serial numbers from a table in GUI and adding it to the array
                    for (int i = 0; i < model_ocrText.size(); i++) {
                        ocrData.add(model_ocrText.getElementAt(i).replace(" ", ", "));
                    }
                    //converting ocrData array to a string delimited with comma, so it can be saved to SQLite
                    String ocrData2 = String.join(", ", ocrData);

                    //getting binary data of images from GUI and saving as String, so it can be saved to SQLite
                    String imageData = jt_serialBinary.getText();

                    //getting the name of the operator
                    String operator = LoginScreen.getUser();

                    //asking user to enter the name of client when saving the transaction
                    String client = JOptionPane.showInputDialog(null, "Unesite ime klijenta", "Unos podataka", JOptionPane.INFORMATION_MESSAGE);

                    String statement = "INSERT INTO transactions(Client, Timestamp, Denomination, SerialNumberOCR, SerialNumberImage, Operator) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";

                    try {
                        PreparedStatement pst = ConnectionDB.conn.prepareStatement(statement);

                        pst.setString(1, client);
                        pst.setString(2, dtf.format(now2));
                        pst.setString(3, denomData2);
                        pst.setString(4, ocrData2);
                        pst.setString(5, imageData);
                        pst.setString(6, operator);

                        pst.execute();
                        pst.close();

                        System.out.println("Transaction saved successfully");
                        jt_logs.append(lb_timeDate.getText() + "     Transakcija uspešno snimljena za klijenta " + client + "\n");
                        log.info("Transaction saved successfully for client " + client + " with counted data [" + denomData2 + "].");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        log.error(ex.getMessage());
                    }
                }
            }

        });
        btn_printSave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (jt_denom.getValueAt(1, 1).equals("")) {
                    JOptionPane.showMessageDialog(null, "Nema podataka u tabeli", "Greška!", JOptionPane.ERROR_MESSAGE);
                } else {
                    //array with denomination and total amount.
                    ArrayList<String> denomData = new ArrayList<>();
                    //adding the first element to denomData, so we know which currency is counted
                    denomData.add(lb_currency.getText());
                    //getting denomination data from JTable as array of Strings
                    for (int i = 0; i < jt_denom.getRowCount() - 1; i++) {
                        if ((jt_denom.getValueAt(i, 1) == null) || (jt_denom.getValueAt(i, 1).equals(""))) {
                            continue;
                        }
                        denomData.add(jt_denom.getValueAt(i, 1).toString());
                    }
                    //getting the total amount as the last element of the array
                    denomData.add(jt_denom.getValueAt(9, 2).toString());
                    //converting denomData array to a string delimited with comma, so it can be saved to SQLite
                    String denomData2 = String.join(", ", denomData);


                    //array for OCR value of serial numbers
                    ArrayList<String> ocrData = new ArrayList<>();
                    //getting OCR value of serial numbers from a table in GUI and adding it to the array
                    for (int i = 0; i < model_ocrText.size(); i++) {
                        ocrData.add(model_ocrText.getElementAt(i).replace(" ", ", "));
                    }
                    //converting ocrData array to a string delimited with comma, so it can be saved to SQLite
                    String ocrData2 = String.join(", ", ocrData);


                    //array for serial number images
                    ArrayList<Image> serialImagePrint = new ArrayList<>();
                    //getting all images from table in GUI for printing in PDF
                    for (int i = 0; i < model_serialImage.size(); i++) {
                        serialImagePrint.add(model_serialImage.getElementAt(i).getImage());
                    }
                    //getting binary data of images from GUI and saving as String, so it can be saved to SQLite
                    String imageData = jt_serialBinary.getText();

                    //getting the name of the operator
                    String operator = LoginScreen.getUser();

                    //asking user to enter the name of client when saving the transaction
                    String client = JOptionPane.showInputDialog(null, "Unesite ime klijenta", "Unos podataka", JOptionPane.INFORMATION_MESSAGE);

                    String statement = "INSERT INTO transactions(Client, Timestamp, Denomination, SerialNumberOCR, SerialNumberImage, Operator) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";

                    try {
                        PreparedStatement pst = ConnectionDB.conn.prepareStatement(statement);

                        pst.setString(1, client);
                        pst.setString(2, dtf.format(now2));
                        pst.setString(3, denomData2);
                        pst.setString(4, ocrData2);
                        pst.setString(5, imageData);
                        pst.setString(6, operator);
                        pst.execute();
                        pst.close();

                        System.out.println("Transaction saved successfully");
                        jt_logs.append(lb_timeDate.getText() + "     Transakcija uspešno snimljena za klijenta " + client + "\n");
                        log.info("Transaction saved successfully for client " + client + " with counted data [" + denomData2 + "].");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        log.error(ex.getMessage());
                    }

                    String Id = " ";
                    String user = LoginScreen.getUser();
                    String[] denomination = denomData2.split(", ");
                    String[] serialOcr = ocrData2.split(", ");

                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setDialogTitle("Odaberite mesto za snimanje fajla");

                    try {
                        File tempFile = File.createTempFile("temp1", ".pdf");
                        String filePath = tempFile.getAbsolutePath();
                        PdfExport.createPdfExport(Id, lb_timeDate.getText(), user, client, filePath, denomination, serialOcr, serialImagePrint);
                        buttonListeners.PDFPrinter(tempFile);
                        jt_logs.append(lb_timeDate.getText() + "     Štampam transakciju\n");
                        log.info("PDF file sucessfully printed");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        log.error(e1.getMessage());
                    }
                }
            }
        });
        btn_settings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                Box box = Box.createHorizontalBox();
                JLabel jl = new JLabel("Unesite lozinku: ");
                box.add(jl);
                JPasswordField jpf = new JPasswordField(24);
                box.add(jpf);

                int button = JOptionPane.showConfirmDialog(null, box, "Servisna podešavanja", JOptionPane.OK_CANCEL_OPTION);

                if (button == JOptionPane.OK_OPTION) {
                    String password = String.valueOf(jpf.getPassword());
                    if (password.equals(pass)) {
                        new SettingsWindow();
                        log.info("Opening SettingWindow. Password enter successfully.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Pogrešna lozinka", "Greška!", JOptionPane.ERROR_MESSAGE);
                        log.error("Wrong password entered for SettingWindow");
                    }
                }
            }
        });
    }
}
