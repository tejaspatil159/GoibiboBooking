package com.qa.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelFileReader {
	
	static File f;
	static FileInputStream fis;
	static XSSFWorkbook wb;
	static XSSFSheet sheet;
	
	public static String filePath="D:\\TejasSelOwn\\GoibiboBooking\\ExcelFile\\Goibibo.xlsx";
	
	public static Object[][] bookingData(String sheetName) throws IOException{
		f=new File(filePath);
		fis=new FileInputStream(f);
		wb=new XSSFWorkbook(fis);
		sheet=wb.getSheet(sheetName);
		
		Object [][]data=new Object[sheet.getLastRowNum()][sheet.getRow(0).getLastCellNum()];
		for(int i=0; i<sheet.getLastRowNum();i++)
		{
			for(int j=0; j<sheet.getRow(0).getLastCellNum();j++)
			{
				data[i][j]=sheet.getRow(i+1).getCell(j).toString();
			}
		}
		return data;
	}

}
