package GUI;

import Database.ConnectionDB;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.proteanit.sql.DbUtils;

public class DatabaseWindow extends JFrame {

    private JPanel panel;
    private JPanel panel1;
    private JPanel panel2;
    private JButton btn_back;
    private JLabel lb_export;
    private JCheckBox jcb_export;
    private JButton btn_export;
    private JLabel lb_search;
    private JTextField tf_search;
    private JTable jt_transactions;
    private JScrollPane jsp_transactions;
    
    public DatabaseWindow(){
        super();
        this.setSize(700, 750);
        this.setTitle("Transakcije");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        initComponents();
        initListeners();
        this.setVisible(true);
    }

    private void initListeners() {

        Border b = BorderFactory.createEtchedBorder(1);
        Font f = new Font("Arial", 1, 20);
        Font f1 = new Font("Arial", 2, 20);
        Font f2 = new Font("Arial", 0, 16);

        panel = new JPanel();

        panel1 = new JPanel();
        panel1.setPreferredSize(new Dimension(670,150));
        panel1.setBorder(b);

        panel2 = new JPanel();
        panel2.setPreferredSize(new Dimension(670,545));
        panel2.setBorder(b);



        jt_transactions = new JTable();
        jsp_transactions = new JScrollPane(jt_transactions);
        jsp_transactions.setPreferredSize(new Dimension(660,530));
        UpdateTable();


        panel2.add(jsp_transactions);

        panel.add(panel1);
        panel.add(panel2);

        setContentPane(panel);

    }

    private void UpdateTable(){
        try{
            String sql = "SELECT * FROM transactions";

            PreparedStatement pst = ConnectionDB.conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            jt_transactions.setModel(DbUtils.resultSetToTableModel(rs));


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
    }
}
