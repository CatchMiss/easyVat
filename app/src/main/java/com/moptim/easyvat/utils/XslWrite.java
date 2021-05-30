package com.moptim.easyvat.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class XslWrite {
    private static final int MAX_LINE = 5000;

    private WritableWorkbook book;
    private WritableSheet sheet;

    public XslWrite(String filePath) {
        try {
            book = Workbook.createWorkbook(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public XslWrite(OutputStream outStream) {
        try {
            book = Workbook.createWorkbook(outStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param row   行数
     * @param col
     * @param value
     * @return
     */
    public void addCell(int row, int col, String value) {
        if (book != null) {

            //当前页数
            int currentSheet = row / MAX_LINE;
            int currentRow = row % MAX_LINE;

            if(currentRow == 0 && col == 0){
                sheet = book.createSheet(" 第" + (currentSheet + 1) + "页 ", currentSheet);
            }

            Label label = new Label(col, currentRow, value);

            //  将定义好的单元格添加到工作表中
            try {
                sheet.addCell(label);
            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }

        }
    }

    public void write() {
        if (book != null) {
            try {
                book.write();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        if (book != null) {
            try {
                book.close();
            } catch (WriteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
