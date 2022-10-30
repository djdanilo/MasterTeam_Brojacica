package GUI;

import javax.swing.*;

public class ButtonListeners {

    public static void tableTotalAmountRows(JTable table) {

        try {
            for (int i = 0; i < table.getRowCount() - 1; i++) {
                String denom = table.getValueAt(i, 0).toString();
                int denom2 = Integer.parseInt(denom);
                String totalPcs = table.getValueAt(i, 1).toString();
                int totalPcs2 = Integer.parseInt(totalPcs);
                int total = totalPcs2 * denom2;
                table.setValueAt(String.valueOf(total), i, 2);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static void tableTotalAmountColumns(JTable table){
        int totalPcs2 = 0;
        int denom2 = 0;
        try{
            for (int i = 0; i < table.getRowCount(); i++){
                String totalPcs = table.getValueAt(i, 1).toString();
                totalPcs2 = totalPcs2 + Integer.parseInt(totalPcs);
                String denom = table.getValueAt(i, 2).toString();
                denom2 = denom2 + Integer.parseInt(denom);
            }
            table.setValueAt(String.valueOf(totalPcs2), 9,1);
            table.setValueAt(String.valueOf(denom2), 9,2);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
    }

}
