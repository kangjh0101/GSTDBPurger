package com.gst.internal_service;

import java.io.PrintStream;

import com.gst.internal_service.purge_messages.PurgeMessages;

public class DB_Handler {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try{
			Class.forName("com.mysql.jdbc.Driver");	
		} catch(ClassNotFoundException e){
			e.printStackTrace();
		}

		new PurgeMessages();
	}

}
