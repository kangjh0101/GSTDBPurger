package com.gst.internal_service.purge_messages;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.gst.internal_service.DB_Handler_Glob_Vars;
import com.gst.internal_service.logger.GST_DB_Purger_Logger;


public class PurgeMessages implements Runnable{

	Thread purge_messages;
	
	private static String maria_url = "jdbc:mysql://" + DB_Handler_Glob_Vars.DB_IP + ":" + DB_Handler_Glob_Vars.DB_PORT + "/" + DB_Handler_Glob_Vars.DB_NAME;
	
	public PurgeMessages(){
		
		purge_messages = new Thread(this);
		purge_messages.start();
	}
	
	public void run(){
		
		PreparedStatement preparedStatement;
		ResultSet resultSet;
		Connection conn = null;
	
		while(true) {
			try {
				Timestamp purge_timestamp;
				Date purge_date_tmp;
				
				Date date = new Date();
				Calendar cal = Calendar.getInstance();       // get calendar instance
				cal.setTime(date); 

				cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
				cal.set(Calendar.MINUTE, 0);                 // set minute in hour
				cal.set(Calendar.SECOND, 0);                 // set second in minute
				cal.set(Calendar.MILLISECOND, 0);
				cal.add(Calendar.DAY_OF_WEEK, -7);
				purge_timestamp = new Timestamp(cal.getTimeInMillis());
//System.out.println(date + "  /  " + purge_timestamp.toString());				
				conn = DriverManager.getConnection(maria_url, DB_Handler_Glob_Vars.DB_USER, DB_Handler_Glob_Vars.DB_PW);
		
/*preparedStatement = conn.prepareStatement("select * from messages where msg_time < ? order by idx desc");
preparedStatement.setTimestamp(1, purge_timestamp);
resultSet = preparedStatement.executeQuery();

while(resultSet.next()) {
	long idx = resultSet.getLong("idx");
	String msg_confirmed = resultSet.getString("msg_confirmed");
	Timestamp msg_time = resultSet.getTimestamp("msg_time");
	String message = resultSet.getString("message");
	
	System.out.println(idx + " / " + msg_confirmed + " / " + msg_time + " / " + message);
}*/
				preparedStatement = conn.prepareStatement("delete from messages where msg_time < ?");
				preparedStatement.setTimestamp(1, purge_timestamp);
				preparedStatement.executeUpdate();
				
				String log_string = "message before " + purge_timestamp.toString() + " has been deleted";
				GST_DB_Purger_Logger.write_log(log_string);
				
				
				cal.add(Calendar.DAY_OF_WEEK, -93);
				purge_date_tmp = cal.getTime();  
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//System.out.println(format1.format(purge_date_tmp));				
				java.sql.Date purge_date = new java.sql.Date(cal.getTimeInMillis());
/*preparedStatement = conn.prepareStatement("select * from wanted_ad where date_to_close < ? and active = 'n' order by idx desc");
preparedStatement.setDate(1, purge_date);
resultSet = preparedStatement.executeQuery();
				
while(resultSet.next()) {
	long idx = resultSet.getLong("idx");
	String user_id = resultSet.getString("user_id");
	String op_ntrp = resultSet.getString("op_ntrp");
	String op_howlong = resultSet.getString("op_howlong");
	java.sql.Date date_to_close = resultSet.getDate("date_to_close");
	String active = resultSet.getString("active");
	
	System.out.println(date_to_close + " / " + idx + " / " + user_id + " / " + op_ntrp + " / " + op_howlong + " / " + active);
}*/
				preparedStatement = conn.prepareStatement("delete from wanted_ad where date_to_close < ? and active='n'");
				preparedStatement.setDate(1, purge_date);
				preparedStatement.executeUpdate();
				
				log_string = "wanted_ad before " + purge_date.toString() + " has been deleted";
				GST_DB_Purger_Logger.write_log(log_string);

				conn.close();
				Thread.sleep(1000 * 60 * 60 * 6);
				
			} catch(Exception e) {
				
				PrintStream ps = GST_DB_Purger_Logger.get_err_printstream();
				e.printStackTrace(ps);
				ps.close();
				
				if(conn != null) {
					try {
						conn.close();
					} catch(Exception ie) {
						PrintStream ps2 = GST_DB_Purger_Logger.get_err_printstream();
						ie.printStackTrace(ps2);
						ps2.close();
					}
				}
			}
		}
	
	}
	
	
	
	
	
}


































