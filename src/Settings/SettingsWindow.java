package Settings;

import Database.ConnectionDB;
import GUI.LoginScreen;
import GUI.MainWindow;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SettingsWindow extends JFrame {

    private JPanel panel;

    private JPanel panel2;
    private JLabel lb_machines;
    private JCheckBox jcb_sb9;
    private JCheckBox jcb_mib9;
    private JCheckBox jcb_ml2f;
    private JCheckBox jcb_ml2fs;
    private JCheckBox jcb_k2;
    private JCheckBox jcb_bc55;
    private JButton btn_confirm;

    private JPanel panel3;
    private JLabel lb_addUser;
    private JTextField tf_username;
    private JPasswordField pf_password;
    private JPasswordField pf_password2;
    private JButton btn_addUser;


    public SettingsWindow(){
        super();
        this.setSize(400, 350);
        this.setTitle("Servisna podešavanja");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        initComponents();
        initListeners();
        this.setVisible(true);
    }


    private void initComponents() {

        panel = new JPanel();
        SpringLayout sl = new SpringLayout();

        Border b = BorderFactory.createEtchedBorder(1);
        Font f = new Font("Arial", 1, 12);

        panel2 = new JPanel();
        panel2.setLayout(sl);
        panel2.setBorder(b);
        panel2.setPreferredSize(new Dimension(380,150));

        lb_machines = new JLabel("Aktivne mašine");
        lb_machines.setFont(f);

        sl.putConstraint(SpringLayout.WEST, lb_machines, 150, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, lb_machines, 5, SpringLayout.NORTH, panel2);

        jcb_sb9 = new JCheckBox("SB-9");
        jcb_mib9 = new JCheckBox("MIB-9");
        jcb_ml2f = new JCheckBox("ML-2F");
        jcb_ml2fs = new JCheckBox("ML-2FS");
        jcb_k2 = new JCheckBox("K2");
        jcb_bc55 = new JCheckBox("BC-55");



        sl.putConstraint(SpringLayout.WEST, jcb_sb9, 25, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, jcb_sb9, 50, SpringLayout.NORTH, panel2);

        sl.putConstraint(SpringLayout.WEST, jcb_mib9, 120, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, jcb_mib9, 50, SpringLayout.NORTH, panel2);

        sl.putConstraint(SpringLayout.WEST, jcb_ml2f, 25, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, jcb_ml2f, 80, SpringLayout.NORTH, panel2);

        sl.putConstraint(SpringLayout.WEST, jcb_ml2fs, 120, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, jcb_ml2fs, 80, SpringLayout.NORTH, panel2);

        sl.putConstraint(SpringLayout.WEST, jcb_k2, 25, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, jcb_k2, 110, SpringLayout.NORTH, panel2);

        sl.putConstraint(SpringLayout.WEST, jcb_bc55, 120, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, jcb_bc55, 110, SpringLayout.NORTH, panel2);

        btn_confirm = new JButton("POTVRDI");
        btn_confirm.setPreferredSize(new Dimension(150,50));

        sl.putConstraint(SpringLayout.WEST, btn_confirm, 210, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, btn_confirm, 60, SpringLayout.NORTH, panel2);

        try {

            String statement = "SELECT * FROM machines;";

            Statement stmt = ConnectionDB.conn.createStatement();
            ResultSet rs = stmt.executeQuery(statement);

            while (rs.next()){
                String machine = rs.getString(1);
                JCheckBox[] checkBoxes = {jcb_sb9, jcb_mib9, jcb_ml2f, jcb_ml2fs, jcb_k2, jcb_bc55};
                for (int i = 0; i < checkBoxes.length; i++){
                    if (checkBoxes[i].getText().equals(machine)){
                        checkBoxes[i].setSelected(true);
                    }
                }
            }

            rs.close();
            stmt.close();

        }catch (SQLException e){
            e.printStackTrace();
        }


        panel2.add(lb_machines);
        panel2.add(jcb_sb9);
        panel2.add(jcb_mib9);
        panel2.add(jcb_ml2f);
        panel2.add(jcb_ml2fs);
        panel2.add(jcb_k2);
        panel2.add(jcb_bc55);
        panel2.add(btn_confirm);



        panel3 = new JPanel();
        panel3.setLayout(sl);
        panel3.setBorder(b);
        panel3.setPreferredSize(new Dimension(380,150));


        panel.add(panel2);
        panel.add(panel3);

        setContentPane(panel);
    }

    private void initListeners() {

        btn_confirm.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JCheckBox[] checkBoxes = {jcb_sb9, jcb_mib9, jcb_ml2f, jcb_ml2fs, jcb_k2, jcb_bc55};
                ArrayList<String> machines = new ArrayList();

                for (int i = 0; i < checkBoxes.length; i++){
                    if (checkBoxes[i].isSelected()){
                        String machine = checkBoxes[i].getText();
                        machines.add(machine);
                    }
                }

                System.out.println(machines);

                String deleteStatement = "DELETE FROM machines;";
                String statement = "INSERT INTO machines(machine) " +
                        "VALUES (?)";

                try {
                    PreparedStatement pst1 = ConnectionDB.conn.prepareStatement(deleteStatement);
                    PreparedStatement pst2 = ConnectionDB.conn.prepareStatement(statement);

                    pst1.execute();
                    pst1.close();

                    for (int i = 0; i < machines.size(); i++) {
                        pst2.setString(1, machines.get(i));
                        pst2.execute();
                    }


                    pst2.close();
                }catch (SQLException e1){
                    e1.printStackTrace();
                }

                JOptionPane.showMessageDialog(null, "Uspešno ste izvršili podešavanja. \n Ponovo pokrenite program nakon potvrde.", "Pažnja!", JOptionPane.OK_OPTION);
                System.exit(0);

            }
        });

    }


}











