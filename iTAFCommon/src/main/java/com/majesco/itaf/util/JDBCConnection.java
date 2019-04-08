package com.majesco.itaf.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.majesco.itaf.main.Config;
import com.majesco.itaf.main.ITAFWebDriver;
public class JDBCConnection {

	private final static Logger log = Logger.getLogger(JDBCConnection.class.getName());
	protected static ResultSet rs = null;
	//protected static Connection c = null;
	protected static Statement st = null;
	public static Boolean sqlexception = false;
	public static String description = null;
	

	private static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();

	

	public static Connection establishDBConn() throws SQLException, ClassNotFoundException, Exception
	{
		String dbURL = "jdbc:oracle:thin:@172.16.244.237:1521/stgngbid";
		String user = "stg_release";
        String pass =  "RELEASEDUJUE";
        
        return establishDBConn(dbURL, user, pass);
	}

	public static Connection establishPASDBConn() throws SQLException,Exception
	{
		String dbURL = null;
		String user = null;
        String pass =  null;

        if(Config.databaseType.equalsIgnoreCase("MsSQL"))
		{
			dbURL = "jdbc:sqlserver://" + Config.databaseHost
					+ ":" + Config.databasePort + ";databaseName=" + Config.databaseName.toString();
		}
		else if(Config.databaseType.equalsIgnoreCase("Oracle"))
		{
			dbURL = "jdbc:oracle:thin:@" + Config.databaseHost + ":"
					+ Config.databasePort + "/" + Config.databaseSID;
		}else
		{
			System.out.println("NO database selected, Please select Database type in confige sheet.....");
			return null;
		}	

        user = Config.applicationdatabaseusername.toString();
		pass = Config.applicationdatabasepassword.toString();

		/*System.out.println(dbURL);
		System.out.println("user " + user);
		System.out.println("pass " + pass);*/
		return establishDBConn(dbURL, user, pass);
	}
	
	
	public static Connection establishHTML5BillingDBConn() throws SQLException,Exception
	{
		String dbURL = null;
		String user = null;
        String pass =  null;

		if (Config.databaseType.equalsIgnoreCase("MsSQL")) {

			dbURL = "jdbc:sqlserver://" + Config.databaseHost + ":"
					+ Config.databasePort + ";databaseName="
					+ Config.databaseName.toString();
		}
		else if (Config.databaseType.equalsIgnoreCase("Oracle")) {
			dbURL = "jdbc:oracle:thin:@" + Config.databaseHost + ":" + Config.databasePort + "/" + Config.databaseSID;
		} else {
			log.info("NO database selected, Please select Database type in confige sheet.....");
			return null;
		}
		user = Config.applicationdatabaseusername.toString();
		pass = Config.applicationdatabasepassword.toString();

		/*System.out.println("dbURL" + dbURL);
		System.out.println("user " + user);
		System.out.println("pass " + pass);*/
		return establishDBConn(dbURL, user, pass);
	}
	
	public static Connection establishHTML5BillingCoreDBConn() throws SQLException, ClassNotFoundException, Exception
	{
		String dbURL = null;
		String user = null;
		String pass = null;
		if (Config.databaseType.equalsIgnoreCase("MsSQL")) {
			
			dbURL = "jdbc:sqlserver://" + Config.jbeamHost + ":" + Config.databasePort + ";databaseName="
					+ Config.jbeamdatabaseusername.toString();
			
		} else if (Config.databaseType.equalsIgnoreCase("Oracle")) {
			
			dbURL = "jdbc:oracle:thin:@" + Config.jbeamHost + ":" + Config.databasePort + "/" + Config.jbeamSID;
			
		} else {
			log.info("NO database selected, Please select Database type in confige sheet.....");
			return null;
		}
		user = Config.jbeamdatabaseusername.toString();
		pass = Config.jbeamdatabasepassword.toString();

		/*System.out.println("dbURL" + dbURL);
		System.out.println("user " + user);
		System.out.println("pass " + pass);*/
		return establishDBConn(dbURL, user, pass);
	}

	public static Connection establishDBConn(String dbURL, String user, String pass) throws SQLException, ClassNotFoundException, Exception
	{
		try
		{
			Connection conn = null;
			if(Config.databaseType.equalsIgnoreCase("MsSQL"))
			{
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				conn = DriverManager.getConnection(dbURL, user, pass);
			}
			else if(Config.databaseType.equalsIgnoreCase("Oracle"))
			{
				//Below line is commented as it creates issue in getting connection.
				//DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				Class.forName("oracle.jdbc.driver.OracleDriver"); 
				conn = DriverManager.getConnection(dbURL, user, pass);
				//st = c.createStatement();		
			}else
			{
				log.error("NO database selected, Please select Database type in confige sheet.....");
			}	
		return conn;
		}
		catch(SQLException se)
		{
			//log.error("Failed to connect establishDBConn <-|-> LocalizeMessage " + se.getLocalizedMessage() +" <-|-> Message "+ se.getMessage() +" <-|-> Cause "+ se.getCause());
			//sqlexception = true;
			//description = se.getMessage();
			log.error(se.getMessage(), se);
			webDriver.getFrame().setVisible(true);
			webDriver.getFrame().setAlwaysOnTop(true);
			webDriver.getFrame().setLocationRelativeTo(null);
			JOptionPane.setRootFrame(webDriver.getFrame());
			JOptionPane.showMessageDialog(webDriver.getFrame(), "Unable to Connect establishDBConn Database..."+se.getMessage());		
			webDriver.getFrame().dispose();
			se.printStackTrace();
			throw new SQLException("Failed to connect establishDBConn <-|-> LocalizeMessage " + se.getLocalizedMessage() +" <-|-> Message"+ se.getMessage() +" <-|-> Cause "+ se.getCause());	
		}
		catch(Exception e)
		{
			//log.error("Failed while connecting establishDBConn <-|-> LocalizeMessage " + e.getLocalizedMessage() +" <-|-> Message "+ e.getMessage() +" <-|-> Cause "+ e.getCause());				
			//sqlexception = true;
			//description = e.getMessage();
			log.error(e.getMessage(), e);
			webDriver.getFrame().setVisible(true);
			webDriver.getFrame().setAlwaysOnTop(true);
			webDriver.getFrame().setLocationRelativeTo(null);
			JOptionPane.setRootFrame(webDriver.getFrame());
			JOptionPane.showMessageDialog(webDriver.getFrame(), "Unable to Connect establishDBConnn Database..."+e.getMessage());		
			webDriver.getFrame().dispose();
			e.printStackTrace();
			throw new Exception("Failed while connecting establishDBConn <-|-> LocalizeMessage " + e.getLocalizedMessage() +" <-|-> Message"+ e.getMessage() +" <-|-> Cause "+ e.getCause());
		}
	}
	public static void closeConnection(Connection con){
		if(con != null){
			try{
				con.close();
			}catch(Exception e){
				log.info("Exception while closing JDBC Connection" + e.getMessage());
			}
		}
	}
		
}