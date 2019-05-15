package com.qhit.utils;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;

public class CommonUtil {

	public static Connection getConnection(){
		Connection conn=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		   //new oracle.jdbc.driver.OracleDriver();   
		    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ems?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC", "root", "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

	public static void exportExcel(String title, String[] name, ResultSet rs) throws Exception {
		HSSFWorkbook workBook = new HSSFWorkbook(); //创建excel文件
		HSSFSheet sheet = workBook.createSheet(); //创建工作表
		
		for(int i=0; i<name.length; i++){
			sheet.setColumnWidth(i, 20*256);
		}
		
		//标题样式
		HSSFCellStyle titleStyle = workBook.createCellStyle();
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); //水平居中
		titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont titleFont =  workBook.createFont(); //字体
		titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); //加粗
		titleFont.setFontHeightInPoints((short)14);  //字号
		titleStyle.setFont(titleFont);               //标题使用字体样式
		//正文样式
		HSSFCellStyle contentStyle = workBook.createCellStyle();
		HSSFFont contentFont = workBook.createFont(); //字体
		contentFont.setFontHeightInPoints((short)12);
		contentStyle.setFont(contentFont);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//边框
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); //居中
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		
		HSSFRow titleRow = sheet.createRow(0);    //创建标题行
		HSSFCell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(title);
		titleCell.setCellStyle(titleStyle);     //单元格样式
		CellRangeAddress address = new CellRangeAddress(0, 0, 0, name.length-1);
		sheet.addMergedRegion(address);
		titleRow.setHeightInPoints(30); //行高
		
		//给工作表设置行，列以及列的标题
		HSSFRow row2 = sheet.createRow(1);      
		row2.setHeightInPoints(20);
		//根据数据库的内容来创建列
		for(int i=0; i<name.length; i++){
			HSSFCell cell = row2.createCell(i);
			cell.setCellValue(name[i]);
			cell.setCellStyle(contentStyle);
		}
		
		int flag = 2;
		while(rs.next()){
			HSSFRow datarow = sheet.createRow(flag);
			datarow.setHeightInPoints(20);
			//根据数据库的结果集给列追加相应的值
			ResultSetMetaData metaData = rs.getMetaData();
			for(int i=0; i<metaData.getColumnCount(); i++){
				HSSFCell datacell = datarow.createCell(i);
				datacell.setCellValue(rs.getString(i+1));
				datacell.setCellStyle(contentStyle);
			}
			
			flag++;
		}
		//设置输出位置
		String path = "e:\\demo.xls";
//		将内容输出
		FileOutputStream out = new FileOutputStream(path);
		workBook.write(out);	//保存单元格
		out.close();
		
	}

	public static void main(String[] args) {
		String title = "流程表";
		String[] name = {"流程id","流程名称","斗轮机id","装载机id","皮带机id","企业id"};
		//获取数据
		Connection connection = CommonUtil.getConnection();
		Statement statement = null;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sql = "SELECT *  FROM base_flow";
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			CommonUtil.exportExcel(title,name,rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	}

