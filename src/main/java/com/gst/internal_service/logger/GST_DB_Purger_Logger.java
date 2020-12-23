package com.gst.internal_service.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class GST_DB_Purger_Logger {

	public static PrintStream get_err_printstream(){
		
		try{
			PrintStream ps;
			String log_file = "/project/GST_DB_Handler/log/";
			
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
			Date date = new Date();
			
			String cur_time;
			StringTokenizer st = new StringTokenizer(dateFormat.format(date), " ");				
			log_file = log_file + st.nextToken() + ".err";
			cur_time = st.nextToken();

			FileWriter fw = new FileWriter(log_file, true);
			BufferedWriter bw = new BufferedWriter(fw);
		    bw.write("\n" + cur_time + "\n");
		    bw.newLine();
		    bw.close();
		    fw.close();
			
			FileOutputStream file = new FileOutputStream(log_file, true);
			ps = new PrintStream(file);
			
			return ps;
			
		} catch(Exception e){
			e.printStackTrace();
			
			return null;
		}
	}
	
	@SuppressWarnings("resource")
	public static void write_log(String log){
		
		String log_file = "/project/GST_DB_Handler/log/";
		FileWriter fw = null;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		Date date = new Date();
		
		try{
			String cur_time;
			StringTokenizer st = new StringTokenizer(dateFormat.format(date));				
			log_file = log_file + st.nextToken() + ".log";
			
			cur_time = st.nextToken();

			fw = new FileWriter(log_file, true);
		
			fw.write(cur_time + ": " + log + "\r\n");
			fw.close();
			
		} catch(Exception e){
			try{
				if(fw !=null) fw.close();
			} catch(Exception ie){
				ie.printStackTrace();
			}
			
			System.err.println("Error in log: " + e);
		}
	}

}
