package MoneyCounters;

import javax.swing.*;
import java.awt.*;

public class ProgressBarFrame{
    public static JFrame frame;
    private JPanel panel;
    public static JLabel label;

    public ProgressBarFrame(){
        frame = new JFrame("Pažnja!");
        panel = new JPanel();
        SpringLayout sl = new SpringLayout();
        panel.setLayout(sl);
        label = new JLabel("Preuzimam apoensku strukturu.");
        label.setFont(new Font("Arial",Font.BOLD, 12));
        frame.setSize(350, 100);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        frame.setVisible(true);
        sl.putConstraint(SpringLayout.WEST, label, 50, SpringLayout.WEST, panel);
        sl.putConstraint(SpringLayout.NORTH, label, 23, SpringLayout.NORTH, panel);
        panel.add(label);
        frame.add(panel);
        frame.setContentPane(panel);
    }

}
