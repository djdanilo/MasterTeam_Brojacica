package GUI;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.util.Date;

public class ExcelExport {


    public static void createExcelExport(String Id, String user, String client, String file, String[] denomination, String[] serialOcr, String[] serialImage) {

        try {


            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFSheet hssfSheet = hssfWorkbook.createSheet("Transkacija");
            HSSFRow row0 = hssfSheet.createRow(0);

            //creating cell by using the createCell() method and setting the values to the cell by using the setCellValue() method

            hssfSheet.addMergedRegion(new CellRangeAddress(0,0,0,1));
            hssfSheet.addMergedRegion(new CellRangeAddress(3,3,0,6));
            hssfSheet.addMergedRegion(new CellRangeAddress(6,6,0,1));
            hssfSheet.addMergedRegion(new CellRangeAddress(7,7,0,2));
            hssfSheet.addMergedRegion(new CellRangeAddress(10,10,0,2));
            hssfSheet.addMergedRegion(new CellRangeAddress(25,25,0,1));


            HSSFRow row3 = hssfSheet.createRow(3);
            HSSFRow row6 = hssfSheet.createRow(6);
            HSSFRow row7 = hssfSheet.createRow(7);
            HSSFRow row10 = hssfSheet.createRow(10);
            HSSFRow row12 = hssfSheet.createRow(12);
            HSSFRow row13 = hssfSheet.createRow(13);
            HSSFRow row14 = hssfSheet.createRow(14);
            HSSFRow row15 = hssfSheet.createRow(15);
            HSSFRow row16 = hssfSheet.createRow(16);
            HSSFRow row17 = hssfSheet.createRow(17);
            HSSFRow row18 = hssfSheet.createRow(18);
            HSSFRow row19 = hssfSheet.createRow(19);
            HSSFRow row20 = hssfSheet.createRow(20);
            HSSFRow row21 = hssfSheet.createRow(21);
            HSSFRow row22 = hssfSheet.createRow(22);
            HSSFRow row25 = hssfSheet.createRow(25);
            HSSFRow row28 = hssfSheet.createRow(28);

            row0.createCell(0).setCellValue("Izveštaj o transakciji");
            row3.createCell(0).setCellValue("Izveštaj generisao: " + user + ", " + new Date());
            row6.createCell(0).setCellValue("Id transakcije: " + Id);
            row7.createCell(0).setCellValue("Klijent: " + client);
            row10.createCell(0).setCellValue("Apoenska struktura transakcije");
            row12.createCell(0).setCellValue("Apoen - " + denomination[0]);
            row12.createCell(1).setCellValue("Broj komada");
            row12.createCell(2).setCellValue("Vrednost");

            if (denomination[0].equals("RSD")) {
                row13.createCell(0).setCellValue("10");
                row13.createCell(1).setCellValue(denomination[1]);
                row13.createCell(2).setCellValue(String.valueOf(10 * Integer.parseInt(denomination[1])));
                row14.createCell(0).setCellValue("20");
                row14.createCell(1).setCellValue(denomination[2]);
                row14.createCell(2).setCellValue(String.valueOf(20 * Integer.parseInt(denomination[2])));
                row15.createCell(0).setCellValue("50");
                row15.createCell(1).setCellValue(denomination[3]);
                row15.createCell(2).setCellValue(String.valueOf(50 * Integer.parseInt(denomination[3])));
                row16.createCell(0).setCellValue("100");
                row16.createCell(1).setCellValue(denomination[4]);
                row16.createCell(2).setCellValue(String.valueOf(100 * Integer.parseInt(denomination[4])));
                row17.createCell(0).setCellValue("200");
                row17.createCell(1).setCellValue(denomination[5]);
                row17.createCell(2).setCellValue(String.valueOf(200 * Integer.parseInt(denomination[5])));
                row18.createCell(0).setCellValue("500");
                row18.createCell(1).setCellValue(denomination[6]);
                row18.createCell(2).setCellValue(String.valueOf(500 * Integer.parseInt(denomination[6])));
                row19.createCell(0).setCellValue("1000");
                row19.createCell(1).setCellValue(denomination[7]);
                row19.createCell(2).setCellValue(String.valueOf(1000 * Integer.parseInt(denomination[7])));
                row20.createCell(0).setCellValue("2000");
                row20.createCell(1).setCellValue(denomination[8]);
                row20.createCell(2).setCellValue(String.valueOf(2000 * Integer.parseInt(denomination[8])));
                row21.createCell(0).setCellValue("5000");
                row21.createCell(1).setCellValue(denomination[9]);
                row21.createCell(2).setCellValue(String.valueOf(5000 * Integer.parseInt(denomination[9])));
                row22.createCell(0).setCellValue("Ukupno:");
                row22.createCell(1).setCellValue("16");
                row22.createCell(2).setCellValue(denomination[10]);
            }

            row25.createCell(0).setCellValue("Serijski brojevi");


            try {
                int i = 0;
                int j = 0;
                int row = 28;
                while (j < serialImage.length){
                    HSSFRow rowLoop = hssfSheet.createRow(row);
                    rowLoop.createCell(0).setCellValue(serialOcr[i]);
                    rowLoop.createCell(1).setCellValue(serialOcr[i+1]);
                    rowLoop.createCell(2).setCellValue(serialImage[j]);
                    i+=2;
                    j++;
                    row++;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }





            FileOutputStream fileOut = new FileOutputStream(file);
            hssfWorkbook.write(fileOut);
//closing the Stream
            fileOut.close();
//closing the workbook
            hssfWorkbook.close();
//prints the message on the console
            System.out.println("Excel file has been generated successfully.");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
