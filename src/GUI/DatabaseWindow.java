package GUI;

import Database.ConnectionDB;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import com.itextpdf.text.pdf.PdfPTable;
import net.proteanit.sql.DbUtils;

public class DatabaseWindow extends JFrame {

    private JPanel panel;
    private JButton btn_back;
    private JLabel lb_time;
    final DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
    private JCheckBox jcb_exportPDF;
    private JCheckBox jcb_exportXLSX;
    private JButton btn_export;
    private JTextField tf_search;
    public static JTable jt_transactions;
    private Vector originalTableModel;
    public static int row;

    public DatabaseWindow() {
        super();
        this.setSize(900, 750);
        this.setTitle("Transakcije");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        initComponents();
        initListeners();
        this.setVisible(true);
    }

    private void initComponents() {

        Border b = BorderFactory.createEtchedBorder(1);

        SpringLayout sl = new SpringLayout();
        GridLayout gl = new GridLayout(2,1);

        panel = new JPanel();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now2 = LocalDateTime.now();
        Calendar now = Calendar.getInstance();

        JPanel panel1 = new JPanel();
        //panel1.setLayout(gl);
        panel1.setPreferredSize(new Dimension(285, 150));
        panel1.setBorder(b);

        btn_back = new JButton("Nazad");
        btn_back.setPreferredSize(new Dimension(120,50));

        JButton btn_exit = new JButton("Izlaz");
        btn_exit.setPreferredSize(new Dimension(120,50));

        lb_time = new JLabel(dateFormat.format(now.getTime()));
        lb_time.setPreferredSize(new Dimension(240,100));
        lb_time.setFont(new Font("Arial", 2, 22));

        new Timer(1000, e -> {
            Calendar now1 = Calendar.getInstance();
            lb_time.setText(dateFormat.format(now1.getTime()));
        }).start();

        panel1.add(btn_back);
        panel1.add(btn_exit);
        panel1.add(lb_time);

        JPanel panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(285,150));
        panel2.setLayout(sl);
        panel2.setBorder(b);

        JLabel lb_export = new JLabel("Eksportuj odabranu transakciju:");
        lb_export.setFont(new Font("Arial", Font.BOLD, 14));

        ButtonGroup buttonGroup = new ButtonGroup();

        jcb_exportPDF = new JCheckBox("PDF");
        jcb_exportXLSX = new JCheckBox("XLSX");
        jcb_exportPDF.setSelected(true);

        buttonGroup.add(jcb_exportPDF);
        buttonGroup.add(jcb_exportXLSX);

        btn_export = new JButton("EXPORT");
        btn_export.setPreferredSize(new Dimension(120,30));

        sl.putConstraint(SpringLayout.WEST, lb_export, 30, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, lb_export, 10, SpringLayout.NORTH, panel2);

        sl.putConstraint(SpringLayout.WEST, jcb_exportPDF, 80, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, jcb_exportPDF, 50, SpringLayout.NORTH, panel2);

        sl.putConstraint(SpringLayout.WEST, jcb_exportXLSX, 150, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, jcb_exportXLSX, 50, SpringLayout.NORTH, panel2);

        sl.putConstraint(SpringLayout.WEST, btn_export, 80, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, btn_export, 110, SpringLayout.NORTH, panel2);


        panel2.add(lb_export);
        panel2.add(jcb_exportPDF);
        panel2.add(jcb_exportXLSX);
        panel2.add(btn_export);


        JPanel panel3 = new JPanel();
        panel3.setPreferredSize(new Dimension(285,150));
        panel3.setLayout(sl);
        panel3.setBorder(b);

        JLabel lb_search = new JLabel("Pretraga:");
        lb_search.setPreferredSize(new Dimension(150, 30));
        lb_search.setFont(new Font("Arial", Font.BOLD, 14));

        tf_search = new JTextField();

        tf_search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchTableContents(tf_search.getText());
            }
        });

        tf_search.setPreferredSize(new Dimension(180, 40));

        sl.putConstraint(SpringLayout.WEST, lb_search, 105, SpringLayout.WEST, panel3);
        sl.putConstraint(SpringLayout.NORTH, lb_search, 30, SpringLayout.NORTH, panel3);

        sl.putConstraint(SpringLayout.WEST, tf_search, 50, SpringLayout.WEST, panel3);
        sl.putConstraint(SpringLayout.NORTH, tf_search, 75, SpringLayout.NORTH, panel3);

        panel3.add(lb_search);
        panel3.add(tf_search);


        JPanel panel4 = new JPanel();
        panel4.setPreferredSize(new Dimension(885, 600));
        panel4.setBorder(b);

        jt_transactions = new JTable();
        jt_transactions.setDefaultEditor(Object.class, null);
        jt_transactions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane jsp_transactions = new JScrollPane(jt_transactions);
        jsp_transactions.setPreferredSize(new Dimension(870, 540));
        UpdateTable();

        originalTableModel = (Vector) ((DefaultTableModel) jt_transactions.getModel()).getDataVector().clone();

        panel4.add(jsp_transactions);

        panel.add(panel1);
        panel.add(panel2);
        panel.add(panel3);
        panel.add(panel4);

        setContentPane(panel);
    }

    private void UpdateTable() {
        try {
            String sql = "SELECT * FROM transactions";

            PreparedStatement pst = ConnectionDB.conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            jt_transactions.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void searchTableContents(String searchString) {

        DefaultTableModel currtableModel = (DefaultTableModel) jt_transactions.getModel();
        //To empty the table before search
        currtableModel.setRowCount(0);
        //To search for contents from original table content
        for (Object rows : originalTableModel) {
            Vector rowVector = (Vector) rows;
            for (Object column : rowVector) {
                if (column.toString().contains(searchString)) {
                    //content found so adding to table
                    currtableModel.addRow(rowVector);
                    break;
                }
            }
        }
    }

    private void initListeners() {
        btn_back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                dispose();
            }
        });

        btn_export.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                row = jt_transactions.getSelectedRow();

                if (row < 0){
                    JOptionPane.showMessageDialog(null, "Niste odabrali transakciju!", "Greška!", JOptionPane.ERROR_MESSAGE);
                }


                String Id = jt_transactions.getValueAt(row, 0).toString();
                String user = LoginScreen.getUser();
                String client = jt_transactions.getValueAt(row, 1).toString();
                String filePath = "";
                String denominationString = jt_transactions.getValueAt(row, 3).toString();
                String[] denomination = denominationString.split(", ");
                String serialOcrString = jt_transactions.getValueAt(row, 4).toString();
                String[] serialOcr = serialOcrString.split(", ");
                String serialImageString = jt_transactions.getValueAt(row, 5).toString();
                String[] serialImage = serialImageString.split(", ");


                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Odaberite mesto za snimanje fajla");


                int userSelection = jFileChooser.showSaveDialog(panel);

                if (userSelection == JFileChooser.APPROVE_OPTION){
                    File file = jFileChooser.getSelectedFile();
                    filePath = file.getAbsolutePath();
                }

                if (jcb_exportPDF.isSelected()){
                    if (!filePath.endsWith(".pdf"))
                        filePath += ".pdf";
                    PDFExportDatabase.createPdfExport(Id, user, client, filePath, denomination, serialOcr, serialImage);
                }
                else if (jcb_exportXLSX.isSelected()){
                    if (!filePath.endsWith(".xls"))
                        filePath += ".xls";
                    ExcelExport.createExcelExport(Id, user, client, filePath, denomination, serialOcr, serialImage);
                }

            }
        });
    }
}