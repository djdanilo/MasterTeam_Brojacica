package Settings;

import Database.ConnectionDB;
import GUI.MainWindow;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class SettingsWindow extends JFrame {
    public static Logger log = Logger.getLogger(SettingsWindow.class.getName());
    private JCheckBox jcb_sb9;
    private JCheckBox jcb_mib9;
    private JCheckBox jcb_ml2f;
    private JCheckBox jcb_ml2fs;
    private JCheckBox jcb_k2;
    private JCheckBox jcb_bc55;
    private JButton btn_confirm;
    private JTextField tf_username;
    private JPasswordField pf_password;
    private JPasswordField pf_password2;
    private JButton btn_addUser;

    public SettingsWindow() {
        super();
        this.setSize(400, 350);
        this.setTitle("Servisna podešavanja");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        initComponents();
        initListeners();
        this.setVisible(true);
    }

    private void initComponents() {

        JPanel panel = new JPanel();
        SpringLayout sl = new SpringLayout();
        GridLayout gl = new GridLayout(3, 2);

        Border b = BorderFactory.createEtchedBorder(1);
        Font f = new Font("Arial", 1, 12);

        JPanel panel2 = new JPanel();
        panel2.setLayout(sl);
        panel2.setBorder(b);
        panel2.setPreferredSize(new Dimension(380, 150));

        JLabel lb_machines = new JLabel("Aktivne mašine");
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
        btn_confirm.setPreferredSize(new Dimension(150, 50));

        sl.putConstraint(SpringLayout.WEST, btn_confirm, 210, SpringLayout.WEST, panel2);
        sl.putConstraint(SpringLayout.NORTH, btn_confirm, 60, SpringLayout.NORTH, panel2);

        try {
            String statement = "SELECT * FROM machines;";

            Statement stmt = ConnectionDB.conn.createStatement();
            ResultSet rs = stmt.executeQuery(statement);

            while (rs.next()) {
                String machine = rs.getString(1);
                JCheckBox[] checkBoxes = {jcb_sb9, jcb_mib9, jcb_ml2f, jcb_ml2fs, jcb_k2, jcb_bc55};
                for (int i = 0; i < checkBoxes.length; i++) {
                    if (checkBoxes[i].getText().equals(machine)) {
                        checkBoxes[i].setSelected(true);
                    }
                }
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        panel2.add(lb_machines);
        panel2.add(jcb_sb9);
        panel2.add(jcb_mib9);
        panel2.add(jcb_ml2f);
        panel2.add(jcb_ml2fs);
        panel2.add(jcb_k2);
        panel2.add(jcb_bc55);
        panel2.add(btn_confirm);

        JPanel panel3 = new JPanel();
        panel3.setLayout(gl);
        panel3.setPreferredSize(new Dimension(380, 100));

        JLabel lb_username = new JLabel("       Korisničko ime:");
        tf_username = new JTextField();

        JLabel lb_password = new JLabel("       Lozinka:");
        pf_password = new JPasswordField();

        JLabel lb_password2 = new JLabel("       Ponovi lozinku");
        pf_password2 = new JPasswordField();

        panel3.add(lb_username);
        panel3.add(tf_username);
        panel3.add(lb_password);
        panel3.add(pf_password);
        panel3.add(lb_password2);
        panel3.add(pf_password2);

        JPanel panel4 = new JPanel();

        btn_addUser = new JButton("Dodaj korisnika");

        panel4.add(btn_addUser);

        panel.add(panel2);
        panel.add(panel3);
        panel.add(panel4);

        setContentPane(panel);
    }

    private void initListeners() {

        btn_confirm.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JCheckBox[] checkBoxes = {jcb_sb9, jcb_mib9, jcb_ml2f, jcb_ml2fs, jcb_k2, jcb_bc55};
                ArrayList<String> machines = new ArrayList<>();

                for (int i = 0; i < checkBoxes.length; i++) {
                    if (checkBoxes[i].isSelected()) {
                        String machine = checkBoxes[i].getText();
                        machines.add(machine);
                        log.info("Adding machine: " + machine);
                    }
                }

                if (machines.size() == 0) {
                    JOptionPane.showMessageDialog(null, "Niste odabrali nijednu mašinu.", "Pažnja!", JOptionPane.ERROR_MESSAGE);
                    return;
                }

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
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                JOptionPane.showMessageDialog(null, "Uspešno ste izvršili podešavanja. \n Ponovo pokrenite program nakon potvrde.", "Pažnja!", JOptionPane.OK_OPTION);
                System.exit(0);

            }
        });

        btn_addUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (tf_username.getText().equals("") || pf_password.getPassword().length == 0 || pf_password2.getPassword().length == 0) {
                    JOptionPane.showMessageDialog(null, "Niste uneli sve podatke.", "Pažnja!", JOptionPane.ERROR_MESSAGE);
                } else if (!Arrays.equals(pf_password.getPassword(), pf_password2.getPassword())) {
                    JOptionPane.showMessageDialog(null, "Lozinke se ne podudaraju. \nPokušajte ponovo.", "Pažnja!", JOptionPane.ERROR_MESSAGE);
                } else {
                    String statement = "INSERT INTO users(userName, password) " +
                            "VALUES (?, ?)";
                    try {
                        PreparedStatement pst = ConnectionDB.conn.prepareStatement(statement);

                        pst.setString(1, tf_username.getText());
                        pst.setString(2, String.valueOf(pf_password.getPassword()));

                        pst.execute();
                        pst.close();

                        System.out.println("User successfully added");
                        log.info("Adding user [" + tf_username.getText() + "] with password " + Arrays.toString(pf_password.getPassword()));

                        JOptionPane.showMessageDialog(null, "Uspešno ste izvršili podešavanja. \n Ponovo pokrenite program nakon potvrde.", "Pažnja!", JOptionPane.OK_OPTION);
                        System.exit(0);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        log.error(ex.getMessage());
                    }
                }
            }
        });
    }
}











