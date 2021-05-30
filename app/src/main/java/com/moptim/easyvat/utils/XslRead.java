package com.moptim.easyvat.utils;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class XslRead {

    private Workbook mWorkbook;
    private Sheet mSheet;

    public XslRead(String filePath){
        try {
            mWorkbook = Workbook.getWorkbook(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    public void openSheet(int sheet){
        if (mWorkbook != null){
            mSheet = mWorkbook.getSheet(sheet);
        }
    }

    //行
    public int getRows(){
        int row = 0;
        if(mWorkbook != null && mSheet != null){
            row = mSheet.getRows();
        }

        return row;
    }

    //列
    public int getColumns(){
        int columns = 0;
        if(mWorkbook != null && mSheet != null){
            columns = mSheet.getColumns();
        }
        return columns;
    }

    public String getCell(int row, int col){
        Cell cell = mSheet.getCell(col, row);
        return cell.getContents();
    }

    public void close() {
        if (mWorkbook != null) {
            try {
                mWorkbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
