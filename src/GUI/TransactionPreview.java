package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static GUI.DatabaseWindow.jt_transactions;

public class TransactionPreview {
    private final JFrame jFrame;
    private JTable jt_denomination;
    private JButton btn_print;
    private JTable jt_serialNumbers;
    private final String generated;
    public static JLabel lb_currency = new JLabel(" ");
    private final String Id;
    private final String client;
    private final String[] serialOcr;
    private final String[] serialImage;
    private final String[] denomination;
    private final String time;
    ButtonListeners buttonListeners = new ButtonListeners();
    private final String[] columnNames = {"Apoen - " + lb_currency.getText(), "Broj komada", "Vrednost"};
    private final String[][] denominationEmpty = {{"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}, {"", "", ""}};

    public TransactionPreview(String generated, String Id, String time, String client, String[] denomination, String[] serialOcr, String[] serialImage) throws IOException {
        this.generated = generated;
        this.Id = Id;
        this.client = client;
        this.denomination = denomination;
        this.serialOcr = serialOcr;
        this.serialImage = serialImage;
        this.time = time;
        jFrame = new JFrame("Pregled transakcije");
        jFrame.setSize(500, 800);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setResizable(false);
        initComponents();
        initListeners();
        insertDenomination();
        ButtonListeners.tableTotalAmountRows(jt_denomination);
        tableTotalAmountColumns(jt_denomination);
        jFrame.setVisible(true);
    }

    private void initComponents() throws IOException {
        JPanel jPanel = new JPanel();
        SpringLayout sl = new SpringLayout();
        jPanel.setLayout(sl);

        JLabel lb_report = new JLabel("Izveštaj o transakciji");
        lb_report.setFont(new Font("Arial", Font.BOLD, 14));
        sl.putConstraint(SpringLayout.WEST, lb_report, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, lb_report, 15, SpringLayout.NORTH, jPanel);

        JLabel lb_generated = new JLabel(this.generated);
        lb_generated.setFont(new Font("Arial", Font.BOLD, 12));
        sl.putConstraint(SpringLayout.WEST, lb_generated, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, lb_generated, 60, SpringLayout.NORTH, jPanel);

        JLabel lb_id = new JLabel("Id transakcije: " + this.Id);
        lb_id.setFont(new Font("Arial", Font.BOLD, 12));
        sl.putConstraint(SpringLayout.WEST, lb_id, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, lb_id, 93, SpringLayout.NORTH, jPanel);

        JLabel lb_transactionTime = new JLabel("Vreme transakcije: " + this.time);
        lb_transactionTime.setFont(new Font("Arial", Font.BOLD, 12));
        sl.putConstraint(SpringLayout.WEST, lb_transactionTime, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, lb_transactionTime, 110, SpringLayout.NORTH, jPanel);

        JLabel lb_client = new JLabel(this.client);
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
        jt_denomination.getColumnModel().getColumn(0).setCellRenderer(new BoldEnlargedTableCellRenderer(9, 4));
        jt_denomination.getColumnModel().getColumn(1).setCellRenderer(new BoldEnlargedTableCellRenderer(9, 4));
        jt_denomination.getColumnModel().getColumn(2).setCellRenderer(new BoldEnlargedTableCellRenderer(9, 4));
        jt_denomination.setShowHorizontalLines(true);
        jt_denomination.setShowVerticalLines(true);
        JScrollPane jsp_denomination = new JScrollPane(jt_denomination);
        jsp_denomination.setPreferredSize(new Dimension(420, 228));
        sl.putConstraint(SpringLayout.WEST, jsp_denomination, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, jsp_denomination, 185, SpringLayout.NORTH, jPanel);

        JLabel lb_serial = new JLabel("Serijski brojevi");
        lb_serial.setFont(new Font("Arial", Font.BOLD, 12));
        sl.putConstraint(SpringLayout.WEST, lb_serial, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, lb_serial, 15, SpringLayout.SOUTH, jsp_denomination);


        jt_serialNumbers = new JTable();
        jt_serialNumbers.setRowHeight(20);
        jt_serialNumbers.setEnabled(false);
        JScrollPane jsp_serialNumbers = new JScrollPane(jt_serialNumbers, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp_serialNumbers.setPreferredSize(new Dimension(420, 280));
        sl.putConstraint(SpringLayout.WEST, jsp_serialNumbers, 30, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, jsp_serialNumbers, 15, SpringLayout.SOUTH, lb_serial);
        insertSerialNumbers(jt_serialNumbers);

        btn_print = new JButton("Print");
        btn_print.setPreferredSize(new Dimension(100, 30));
        sl.putConstraint(SpringLayout.WEST, btn_print, 350, SpringLayout.WEST, jPanel);
        sl.putConstraint(SpringLayout.NORTH, btn_print, 10, SpringLayout.NORTH, jPanel);

        jPanel.add(lb_report);
        jPanel.add(lb_generated);
        jPanel.add(lb_id);
        jPanel.add(lb_client);
        jPanel.add(btn_print);
        jPanel.add(lb_transactionTime);
        jPanel.add(lb_denomination);
        jPanel.add(jsp_denomination);
        jPanel.add(lb_serial);
        jPanel.add(jsp_serialNumbers);
        jFrame.setContentPane(jPanel);
    }

    public void initListeners() {
        btn_print.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int row = getRowWithValue(jt_transactions, Id);
                String Id = jt_transactions.getValueAt(row, 0).toString();
                String user = jt_transactions.getValueAt(row, 6).toString();
                String client = jt_transactions.getValueAt(row, 1).toString();
                String transactionTime = jt_transactions.getValueAt(row, 2).toString();
                String denominationString = jt_transactions.getValueAt(row, 3).toString();
                String[] denomination = denominationString.split(", ");
                String serialOcrString = jt_transactions.getValueAt(row, 4).toString();
                String[] serialOcr = serialOcrString.split(", ");
                String serialImageString = jt_transactions.getValueAt(row, 5).toString();
                String[] serialImage = serialImageString.split(", ");

                try {
                    File tempFile = File.createTempFile("temp", ".pdf");
                    String filePath = tempFile.getAbsolutePath();
                    PDFExportDatabase.createPdfExport(Id, transactionTime, user, client, filePath, denomination, serialOcr, serialImage);
                    buttonListeners.PDFPrinter(tempFile);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void insertDenomination() {
        if (this.denomination[0].equals("RSD")) {

            jt_denomination.setValueAt("10", 0, 0);
            jt_denomination.setValueAt(denomination[1], 0, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[1]) * 10), 0, 2);
            jt_denomination.setValueAt("20", 1, 0);
            jt_denomination.setValueAt(denomination[2], 1, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[2]) * 20), 1, 2);
            jt_denomination.setValueAt("50", 2, 0);
            jt_denomination.setValueAt(denomination[3], 2, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[3]) * 50), 2, 2);
            jt_denomination.setValueAt("100", 3, 0);
            jt_denomination.setValueAt(denomination[4], 3, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[4]) * 100), 3, 2);
            jt_denomination.setValueAt("200", 4, 0);
            jt_denomination.setValueAt(denomination[5], 4, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[5]) * 200), 4, 2);
            jt_denomination.setValueAt("500", 5, 0);
            jt_denomination.setValueAt(denomination[6], 5, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[6]) * 500), 5, 2);
            jt_denomination.setValueAt("1000", 6, 0);
            jt_denomination.setValueAt(denomination[7], 6, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[7]) * 1000), 6, 2);
            jt_denomination.setValueAt("2000", 7, 0);
            jt_denomination.setValueAt(denomination[8], 7, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[8]) * 2000), 7, 2);
            jt_denomination.setValueAt("5000", 8, 0);
            jt_denomination.setValueAt(denomination[9], 8, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[9]) * 5000), 8, 2);
            jt_denomination.setValueAt("Ukupno:", 9, 0);
        }
        if (this.denomination[0].equals("USD")) {
            jt_denomination.setValueAt("1", 0, 0);
            jt_denomination.setValueAt(denomination[1], 0, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[1])), 0, 2);
            jt_denomination.setValueAt("2", 1, 0);
            jt_denomination.setValueAt(denomination[2], 1, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[2]) * 2), 1, 2);
            jt_denomination.setValueAt("5", 2, 0);
            jt_denomination.setValueAt(denomination[3], 2, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[3]) * 5), 2, 2);
            jt_denomination.setValueAt("10", 3, 0);
            jt_denomination.setValueAt(denomination[4], 3, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[4]) * 10), 3, 2);
            jt_denomination.setValueAt("20", 4, 0);
            jt_denomination.setValueAt(denomination[5], 4, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[5]) * 20), 4, 2);
            jt_denomination.setValueAt("50", 5, 0);
            jt_denomination.setValueAt(denomination[6], 5, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[6]) * 50), 5, 2);
            jt_denomination.setValueAt("100", 6, 0);
            jt_denomination.setValueAt(denomination[7], 6, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[7]) * 100), 6, 2);
            jt_denomination.setValueAt("", 7, 0);
            jt_denomination.setValueAt("", 7, 1);
            jt_denomination.setValueAt("", 7, 2);
            jt_denomination.setValueAt("", 8, 0);
            jt_denomination.setValueAt("", 8, 1);
            jt_denomination.setValueAt("", 8, 2);
            jt_denomination.setValueAt("Ukupno:", 9, 0);
        }
        if (this.denomination[0].equals("EUR")) {
            jt_denomination.setValueAt("5", 0, 0);
            jt_denomination.setValueAt(denomination[1], 0, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[1]) * 5), 0, 2);
            jt_denomination.setValueAt("10", 1, 0);
            jt_denomination.setValueAt(denomination[2], 1, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[2]) * 10), 1, 2);
            jt_denomination.setValueAt("20", 2, 0);
            jt_denomination.setValueAt(denomination[3], 2, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[3]) * 20), 2, 2);
            jt_denomination.setValueAt("50", 3, 0);
            jt_denomination.setValueAt(denomination[4], 3, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[4]) * 50), 3, 2);
            jt_denomination.setValueAt("100", 4, 0);
            jt_denomination.setValueAt(denomination[5], 4, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[5]) * 100), 4, 2);
            jt_denomination.setValueAt("200", 5, 0);
            jt_denomination.setValueAt(denomination[6], 5, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[6]) * 200), 5, 2);
            jt_denomination.setValueAt("500", 6, 0);
            jt_denomination.setValueAt(denomination[7], 6, 1);
            jt_denomination.setValueAt(String.valueOf(Integer.parseInt(denomination[7]) * 500), 6, 2);
            jt_denomination.setValueAt("", 7, 0);
            jt_denomination.setValueAt("", 7, 1);
            jt_denomination.setValueAt("", 7, 2);
            jt_denomination.setValueAt("", 8, 0);
            jt_denomination.setValueAt("", 8, 1);
            jt_denomination.setValueAt("", 8, 2);
            jt_denomination.setValueAt("Ukupno:", 9, 0);
        }
    }

    public void insertSerialNumbers(JTable jTable) throws IOException {

        DefaultTableModel model = new DefaultTableModel();
        // Add columns to the model
        model.addColumn("Apoen");
        model.addColumn("Serijski broj");
        model.addColumn("Slika serijskog broja");
        jTable.setModel(model);
        jTable.getColumnModel().getColumn(0).setMaxWidth(70);
        jTable.getColumnModel().getColumn(1).setMaxWidth(140);
        jTable.getColumnModel().getColumn(2).setMaxWidth(210);

        String newLine2 = "01100000110110010001101100010001101000011100001000101";
        int i = 0;
        int j = 1;
        int row = 0;

        while (row < (serialOcr.length + 1) / 2) {
            if (serialImage.length > 1) {
                BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
                Graphics2D g2d = img.createGraphics();
                java.awt.Font font = new java.awt.Font("Arial", java.awt.Font.PLAIN, 2);
                g2d.setFont(font);
                int height = g2d.getFontMetrics().getHeight();
                g2d.dispose();

                img = new BufferedImage(384, 40, BufferedImage.TYPE_INT_RGB);
                g2d = img.createGraphics();

                g2d.setFont(font);
                g2d.setColor(Color.WHITE);
                int fontSize = 1;

                for (String line2 : serialImage[j].split(newLine2)) {
                    g2d.drawString(line2, 0, height);
                    height += fontSize;
                }

                File file = new File("images\\TextDB" + j + ".png");
                ImageIO.write(img, "png", file);
                g2d.dispose();

                model.addRow(new Object[]{serialOcr[i], serialOcr[i + 1], file.getAbsolutePath()});
                jt_serialNumbers.getColumnModel().getColumn(2).setCellRenderer(new ImageRenderer());

            } else {
                model.addRow(new Object[]{serialOcr[i], serialOcr[i + 1], "/"});
            }
            jt_serialNumbers.setModel(model);
            i += 2;
            j++;
            row++;
        }

    }

    static class ImageRenderer extends DefaultTableCellRenderer {
        JLabel lbl = new JLabel();
        int width = 200;
        int height = 25;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            if (value instanceof String) {
                ImageIcon icon = new ImageIcon((String) value);
                Image img = icon.getImage();
                Image newImg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
                lbl.setIcon(new ImageIcon(newImg));
            }
            return lbl;
        }
    }

    static class BoldEnlargedTableCellRenderer extends DefaultTableCellRenderer {
        private final int row;
        private final int fontSize;

        BoldEnlargedTableCellRenderer(int row, int fontSize) {
            this.row = row;
            this.fontSize = fontSize;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (row == this.row) {
                Font currentFont = c.getFont();
                c.setFont(currentFont.deriveFont(currentFont.getStyle() | Font.BOLD, currentFont.getSize() + fontSize));
            }
            return c;
        }

    }

    public static void tableTotalAmountColumns(JTable table) {
        int totalPcs2 = 0;
        int denom2 = 0;
        try {

            for (int i = 0; i < table.getRowCount(); i++) {
                if ((table.getValueAt(i, 1) == null) || table.getValueAt(i, 1).equals("") ||
                        (table.getValueAt(i, 2) == null) || table.getValueAt(i, 2).equals("")) {
                    continue;
                }
                String totalPcs = table.getValueAt(i, 1).toString();
                totalPcs2 += Integer.parseInt(totalPcs);
                String denom = table.getValueAt(i, 2).toString();
                denom2 += Integer.parseInt(denom);
            }

            table.setValueAt(String.valueOf(totalPcs2) + " komada", 9, 1);
            table.setValueAt(String.valueOf(denom2) + " " + lb_currency.getText(), 9, 2);

        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public int getRowWithValue(JTable table, Object value) {
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getColumnCount(); col++) {
                if (table.getValueAt(row, col).toString().equals(value)) {
                    return row;
                }
            }
        }
        return -1;
    }

}


