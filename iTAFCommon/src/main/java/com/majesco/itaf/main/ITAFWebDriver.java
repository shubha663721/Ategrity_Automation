package com.majesco.itaf.main;

import java.io.IOException;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import atu.testrecorder.ATUTestRecorder;
import atu.testrecorder.exceptions.ATUTestRecorderException;

import com.majesco.itaf.vo.Reporter;

public abstract class ITAFWebDriver {
	
	private final static Logger log = Logger.getLogger(ITAFWebDriver.class);

	
	
	
	
	Reporter report = new Reporter();
	JFrame frame = new JFrame("iTAF FRAMEWORK");
	String email = "";
	
	private static final String BILLING_APPLICATION_NAME = "Billing";
	private static final String CLAIMS_APPLICATION_NAME = "Claims";
	private static final String PAS_APPLICATION_NAME = "PAS";

	private static String applicationName = null;

	public static ITAFWebDriver getInstance() {
		return ObjectFactory.getWebDriver();
	}

	public static void main(String args[]) throws Exception {
		String filePathFileName = args[0];
		
	
		
		Config.initializeConfigMap(filePathFileName);
		
		if(args.length > 1){
			applicationName = args[1];
		} else {
			applicationName = Config.applicationName;
		}
		
		ObjectFactory.initialize();
		
		
	}

	protected abstract void init() throws IOException;

	public void DataInput(String filePath, String testcaseID,
			String transactionType, String transactionCode, String operationType)
			throws Exception {
		
	}

	public void DataInput(String filePath, String testcaseID,
			String transactionType, String transactionCode,
			String operationType, String cycleDate) throws Exception {
	}

	public void DataInput(String structurePath, String filePath,
			String testcaseID, String transactionType, String transactionCode,
			String operationType, String cycleDate) throws Exception{
	}

	public Reporter getReport() {
		return report;
	}

	public void setReport(Reporter report) {
		this.report = report;
	}

	public JFrame getFrame() {
		return frame;
	}

	public String getEmail() {
		return email;
	}

	public static boolean isBillingApplication() {
		return BILLING_APPLICATION_NAME.equalsIgnoreCase(applicationName);
	}

	public static boolean isPASApplication() {
		return PAS_APPLICATION_NAME.equalsIgnoreCase(applicationName);
	}

	public static boolean isClaimsApplication() {
		return CLAIMS_APPLICATION_NAME.equalsIgnoreCase(applicationName);
	}
}
