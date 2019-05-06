package com.majesco.itaf.main;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import atu.testrecorder.ATUTestRecorder;


import atu.testrecorder.exceptions.ATUTestRecorderException;

import com.majesco.itaf.util.ExcelUtility;
import com.majesco.itaf.verification.WebVerification;

import org.apache.commons.lang.StringUtils;

public class ITAFWebDriverPAS extends ITAFWebDriver{

	private final static Logger log = Logger.getLogger(ITAFWebDriverPAS.class.getName());
	private MainController controller;
	
	static ATUTestRecorder recorder;
	ITAFWebDriverPAS(){
	}

	protected void init() throws IOException{
		
		String reportsheet = "";
		String detailreportsheet = "";
		String uniquenumbersheet= "";//Mrinmayee
		String nodetailreportsheet = "";
		String FailedScreen = "";
		String body = "";
		String strCompleteData = "";
		String subject = "";
		
		try {
			// Unlockscreen("wscript D:/unlockScreen.vbs");
			Automation.setUp();	
			
			controller = ObjectFactory.getMainController();

			// bhaskar
			subject = Config.projectName
					+ " - Automated Test Run Results";
			email = Config.emailId;
			reportsheet = Config.resultOutput;
			detailreportsheet = Config
					.verificationResultPath;
			
			if(StringUtils.equalsIgnoreCase(Config.emailTransactionInfo,"true"))
				uniquenumbersheet = Config.transactionInfo;
			else
				uniquenumbersheet="";
			// bhaskar
			log.info("formula or string:"
					+ Config.controllerFilePath);
			controller.ControllerData(Config
					.controllerFilePath);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			report.setStatus("FAIL");
			report.setTestcaseId(controller.controllerTestCaseID
					.toString());
			report.setTrasactionType(controller.controllerTransactionType
					.toString());
			try {
				controller.pauseFun("d: "
						+ controller.controllerTestCaseID
						+ ", Tranasction: "
						+ controller.controllerTransactionType
						+ ", Error: " + e.getMessage());
			} catch (Exception e1) {
				System.out.println("finally block e1 executed");
				log.error(e1.getMessage(), e1);
				controller.pauseFun("File Not Found");
			}
		} finally {
			// TM: 16-01-2015
			System.out.println("finally block executed");
			
			if(WebHelperPAS.recorder != null)
					{
					try {
						WebHelperPAS.recorder.stop();
						System.out.println("recorder stop executed");
					} catch (ATUTestRecorderException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
			frame.setLocationRelativeTo(null);
			JOptionPane.setRootFrame(frame);
			JOptionPane.showMessageDialog(frame, "Execution Completed");
			frame.dispose();
			// bhaskar
			File tempfile = new File(detailreportsheet);
			boolean exists = tempfile.exists();
			if (exists == true) {
				// body = "PFA the Reports";
				int TotalVerificationPoints = WebHelperPAS.TotalpassCount
						+ WebHelperPAS.TotalfailCount;

				body = "Total Verification Points :" + TotalVerificationPoints
						+ "$" + "Total Passed:" + WebHelperPAS.TotalpassCount
						+ "$" + "Total Failed:" + WebHelperPAS.TotalfailCount
						+ "$" + "Please see attached reports for details.";

				System.out.println(body);
				strCompleteData = email + "#" + subject + "#" + body + "#"
						+ reportsheet + "#" + detailreportsheet + "#"
						+ FailedScreen + "#" + uniquenumbersheet;
				
				Runtime.getRuntime().exec(
						"wscript SendMail.vbs " + (char) 34 + strCompleteData
								+ (char) 34);
			} else {
				// int
				// TotalVerificationPoints=WebHelper.TotalpassCount+WebHelper.TotalfailCount;

				body = "PFA the Report";
				subject = "Execution Failed";
				strCompleteData = email + "#" + subject + "#" + body + "#"
						+ reportsheet + "#" + FailedScreen + "#"
						+ nodetailreportsheet + "#" + uniquenumbersheet;
				
				Runtime.getRuntime().exec(
						"wscript SendMail.vbs " + (char) 34 + strCompleteData
								+ (char) 34);
			}
			
			//System.exit(1);
			// bhaskar
		}
	}

	public void DataInput(String filePath, String testcaseID,
			String transactionType, String transactionCode, String operationType)
			throws Exception {
		System.out.println("filePath is:" + filePath);
		if (transactionCode == null) {
			transactionCode = transactionType;
		}
		System.out.println(transactionCode);

		if (operationType.equalsIgnoreCase("InputandVerfiy")
				&& !operationType.isEmpty()) {
			ExcelUtility.GetDataFromValues(filePath, testcaseID.toString(),
					transactionType.toString());
			WebVerification.performVerification(transactionType, testcaseID);
		} else if (!operationType.equalsIgnoreCase("Verify")
				&& !operationType.isEmpty()) {
			ExcelUtility.GetDataFromValues(filePath, testcaseID.toString(),
					transactionType.toString());
		} else if (!operationType.equalsIgnoreCase("Input")
				&& !operationType.isEmpty()) {
			System.out
					.println("------------------------------------------------------");
			System.out
					.println("-------------------------INPUT-----------------------------");
			System.out
					.println("------------------------------------------------------");
			WebVerification.performVerification(transactionType, testcaseID);
		}
	}

	public static void Unlockscreen(String filepath) {

		try {
			Runtime.getRuntime().exec(filepath);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			System.out.println("Unlock file is not available");

		}

	}
	
}
