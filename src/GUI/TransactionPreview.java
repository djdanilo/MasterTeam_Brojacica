package GUI;

import javax.swing.*;
import java.awt.*;

public class TransactionPreview {
    private final JFrame jFrame;
    private JLabel lb_generated;
    private JLabel lb_id;
    private JLabel lb_client;
    private JTable jt_denomination;
    private JTable jt_serialNumbers;
    private final String[] columnNames = {"Apoen - ", "Broj komada", "Vrednost"};
    private final String[][] denominationEmpty = {{"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}};
    private final String[] columnNamesSerial = {"Apoen", "Serijski broj", "Slika serijskog broja"};

    public TransactionPreview(JLabel lb_generated, JLabel lb_id, JLabel lb_client, JTable jt_denomination, JTable jt_serialNumbers){
        this.lb_generated = lb_generated;
        this.lb_id = lb_id;
        this.lb_client = lb_client;
        this.jt_denomination = jt_denomination;
        this.jt_serialNumbers = jt_serialNumbers;
        jFrame = new JFrame("Pregled transakcije");
        jFrame.setSize(500, 750);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setResizable(false);
        initComponents();
        jFrame.setVisible(true);
    }

    private void initComponents() {
        JPanel jPanel = new JPanel();
        SpringLayout sl = new SpringLayout();
        jPanel.setLayout(sl);

        JScrollPane jsp_panel = new JScrollPane(jPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp_panel.setPreferredSize(new Dimension(500,500));

        JLabel lb_report = new JLabel("Izveštaj o transakciji");
        lb_report.setFont(new Font("Arial", Font.BOLD, 14));
        sl.putConstraint(SpringLayout.WEST, lb_report, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, lb_report, 15, SpringLayout.NORTH, jPanel);

        lb_generated = new JLabel(this.lb_generated.getText());
        lb_generated.setFont(new Font("Arial", Font.BOLD, 12));
        sl.putConstraint(SpringLayout.WEST, lb_generated, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, lb_generated, 60, SpringLayout.NORTH, jPanel);

        lb_id = new JLabel(this.lb_id.getText());
        lb_id.setFont(new Font("Arial", Font.BOLD, 12));
        sl.putConstraint(SpringLayout.WEST, lb_id, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, lb_id, 110, SpringLayout.NORTH, jPanel);

        lb_client = new JLabel(this.lb_client.getText());
        lb_client.setFont(new Font("Arial", Font.BOLD, 12));
        sl.putConstraint(SpringLayout.WEST, lb_client, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, lb_client, 127, SpringLayout.NORTH, jPanel);

        JLabel lb_denomination = new JLabel("Apoenska struktura transakcije");
        lb_denomination.setFont(new Font("Arial", Font.BOLD, 12));
        sl.putConstraint(SpringLayout.WEST, lb_denomination, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, lb_denomination, 160, SpringLayout.NORTH, jPanel);

        jt_denomination = new JTable(denominationEmpty, columnNames);
        jt_denomination.setRowHeight(20);
        jt_denomination.getColumnModel().getColumn(0).setMinWidth(130);
        jt_denomination.getColumnModel().getColumn(1).setMinWidth(130);
        jt_denomination.getColumnModel().getColumn(2).setMinWidth(130);
        jt_denomination.setEnabled(false);
        JScrollPane jsp_denomination = new JScrollPane(jt_denomination);
        jsp_denomination.setPreferredSize(new Dimension(420, 230));
        sl.putConstraint(SpringLayout.WEST, jsp_denomination, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, jsp_denomination, 185, SpringLayout.NORTH, jPanel);

        JLabel lb_serial = new JLabel("Serijski brojevi");
        lb_serial.setFont(new Font("Arial", Font.BOLD, 12));
        sl.putConstraint(SpringLayout.WEST, lb_serial, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, lb_serial, 15, SpringLayout.SOUTH, jsp_denomination);

        jt_serialNumbers = new JTable(denominationEmpty, columnNamesSerial);
        jt_serialNumbers.setRowHeight(20);
        jt_serialNumbers.getColumnModel().getColumn(0).setMinWidth(130);
        jt_serialNumbers.getColumnModel().getColumn(1).setMinWidth(130);
        jt_serialNumbers.getColumnModel().getColumn(2).setMinWidth(130);
        jt_serialNumbers.setEnabled(false);
        JScrollPane jsp_serialNumbers = new JScrollPane(jt_serialNumbers);
        jsp_serialNumbers.setPreferredSize(new Dimension(420, 230));
        sl.putConstraint(SpringLayout.WEST, jsp_serialNumbers, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, jsp_serialNumbers, 15, SpringLayout.SOUTH, lb_serial);



        jPanel.add(lb_report);
        jPanel.add(lb_generated);
        jPanel.add(lb_id);
        jPanel.add(lb_client);
        jPanel.add(lb_denomination);
        jPanel.add(jsp_denomination);
        jPanel.add(lb_serial);
        jPanel.add(jsp_serialNumbers);
        //jFrame.add(jsp_panel);
        jFrame.setContentPane(jsp_panel);


    }
}
