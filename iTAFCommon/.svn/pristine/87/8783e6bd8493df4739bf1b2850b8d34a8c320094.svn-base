package com.majesco.itaf.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.openqa.selenium.os.WindowsUtils;

import org.apache.log4j.Logger;


public  class CalendarSnippet {
	
	private final static Logger log = Logger.getLogger(CalendarSnippet.class.getName());
	private static final String TASKLIST = "tasklist";
	private static String KILL = "";

	/**
	 * This method returns month name for month number e.g. January for 1, February for 2 and so on
	 * 
	 * @param monthInt i.e. integer number of the month for which month name is required
	 * @return monthName i.e. January, February etc.
	 */
	public static String getMonthForInt(int monthInt) {
		String monthName = "invalid";
		monthInt = monthInt-1;
		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] months = dfs.getMonths();
		if (monthInt >= 0 && monthInt <= 11 ) {
			monthName = months[monthInt];
		}
		return monthName;
	}
	
	/**
	 * This method returns short month name for month number i.e. Jan for 1, Feb for 2 and so on
	 * 
	 * @param monthInt - month number 1 for Jan, 2 for Feb and so on
	 * @return monthName - Jan for 1, Feb for 2 and so on
	 */
	public static String getShortMonthForInt(int monthInt) {
		String monthName = "invalid";
		monthInt = monthInt-1;
		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] months = dfs.getShortMonths();
		if (monthInt >= 0 && monthInt <= 11 ) {
			monthName = months[monthInt];
		}
		return monthName;
	}	
	
	/**
	 * This method returns month number as 01, 02.....12 for month name i.e. Jan or January, Feb or February
	 * 
	 * @param monthName [Jan or January, Feb or February]
	 * @return monthInt [01 for Jan, 02 for Feb]
	 * @throws ParseException 
	 */
	
	public static String getMonthForString(String monthName) throws ParseException{
		Calendar cal = Calendar.getInstance();
		cal.setTime(new SimpleDateFormat("MMM").parse("december"));
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(2);
		String monthInt = nf.format(cal.get(Calendar.MONTH) + 1);
		
		return monthInt;
	}

	//TM: commented the following method and wrote a same new method which uses Java's inbuilt classes for the purpose
/*	public static String getMonthForString(String mname1) {
        String monthnumber = "invalid" ;
        ControlTypeEnum mname  = ControlTypeEnum.valueOf(mname1.toString());
       
        switch(mname){
          case January:
                monthnumber = "01";
                break;                  
          case February:
                monthnumber = "02";
                break;
          case March:
                monthnumber = "03";
                break;
          case April:
                monthnumber = "04";
                break;
          case May:
                monthnumber = "05";
                break;
          case June:
                monthnumber = "06";
                break;
          case July:
                monthnumber = "07";
                break;
          case August:
                monthnumber = "08";
                break;
          case September:
                monthnumber = "09";
                break;
          case October:
                monthnumber = "10";
                break;
          case November:
                monthnumber = "11";
                break;
          case December:
                monthnumber = "12";
                break;
		default:
			break;
 }

        return monthnumber;
    }
*/

	public static boolean isProcessRunning(String serviceName) throws Exception {

		Process p = Runtime.getRuntime().exec(TASKLIST);
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader( p.getInputStream()));
		}
		catch(Exception e)
		{
			log.error("Failed to read process list <-|-> LocalizeMessage " + e.getLocalizedMessage() +" <-|-> Message "+ e.getMessage() +" <-|-> Cause "+ e.getCause(),e);
			throw new Exception("Failed to read process list  <-|-> LocalizeMessage " + e.getLocalizedMessage() +" <-|-> Message"+ e.getMessage() +" <-|-> Cause "+ e.getCause());
		}
		String line;
		while ((line = reader.readLine()) != null) {
			//log.info(line); // commnented for log clearing
			if (line.contains(serviceName)) {
				return true;
			}
		}
		return false;
	}

	public static void killProcess(String serviceName) throws Exception
	{
		KILL = "\\System32\\taskkill /F /IM ";
		KILL =System.getenv("SystemRoot") +  KILL;
		try
		{
		Runtime.getRuntime().exec(KILL + serviceName);
		}
		catch(Exception e)
		{
			log.error("Failed to kill process <-|-> LocalizeMessage " + e.getLocalizedMessage() +" <-|-> Message "+ e.getMessage() +" <-|-> Cause "+ e.getCause(),e);
			throw new Exception("Failed to kill process <-|-> LocalizeMessage " + e.getLocalizedMessage() +" <-|-> Message "+ e.getMessage() +" <-|-> Cause "+ e.getCause());
		}
		//if(serviceName == "IEDriverServer.exe")
		//{Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");}
	}
	
	public static void KillDriverProcess(String driverName) throws Exception
	{
		WindowsUtils.killByName(driverName);
	}
	
}

