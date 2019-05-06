package com.majesco.itaf.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.majesco.compare.CompareUtil;
import com.majesco.compare.ResultBean.SummaryAttributes;
import com.majesco.compare.pdf.PDFResultBean;
import com.majesco.itaf.main.Config;
import com.majesco.itaf.main.MainController;
import com.majesco.itaf.main.ObjectFactory;

public class PDFComparisonUtil {
	public static String StringToIgnore = null;
	private static MainController controller = ObjectFactory
			.getMainController();

	public static void setStringToIgnore(String str) {
		if (str.isEmpty()) {
			StringToIgnore = "";
			System.out
					.println("No input is provided to ignore during PDF comparison");
		} else {
			StringToIgnore = str.trim();
			System.out
					.println("String to ignore during PDF comparison is set to '"
							+ StringToIgnore + "'");
		}
	}

	public static String[] PDFCompare(String logicalName, String controltype,
			String ctrlValue) throws Exception {

		long expectedFilePageCount = 0, actualFilePageCount = 0;

		// get path of files for comparison
		String[] temp=ctrlValue.split(";");
		String expFilePath = Config.expFilePath + "\\" + temp[1];
		String actualFilePath = Config.actualPdfDownloadPath + "\\" + temp[1];


		// create TC_Trasnaction folder if not present
		String folderPath = Config.pdfCompResultPath + "\\"
				+ controller.controllerTestCaseID + "\\"
				+ controller.controllerTransactionType;
		File createDir = new File(folderPath);
		if (!createDir.exists()) {
			createDir.mkdirs();
		}

		// create subfolder with timestamp inside folder created above
		String timeStampForFolder = new SimpleDateFormat("ddMMMMyyyy_HH_mm_ss")
				.format(new Date());
		String subFolderPathForOutput = folderPath + "\\" + timeStampForFolder;
		File subFolder = new File(subFolderPathForOutput);
		if (!subFolder.exists()) {
			subFolder.mkdirs();
		}

		String[] args = new String[6];

		// file comparison
		args[0] = "pdf";

		// Expected file path
		args[1] = expFilePath;

		// Actual file path
		args[2] = actualFilePath;

		// path for properties file. Not useful for now
		args[3] = "";

		// Results folder path
		args[4] = subFolderPathForOutput;

		// semicolon separated string to exclude
		// args[5] = "80-CP-002810335-8;06-12-18";
		args[5] = StringToIgnore;

		System.out.println("PDF comparison started at : "
				+ new SimpleDateFormat("ddMMMM HH:mm:ss").format(new Date()));
		System.out.println("PDF comparison is in progess");
		
		PDFResultBean pdfResultBean=new PDFResultBean();
		try{
			 pdfResultBean = CompareUtil.compareFiles(args);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		System.out.println("PDF comparison completed at: "
				+ new SimpleDateFormat("ddMMMM HH:mm:ss").format(new Date()));

		expectedFilePageCount = pdfResultBean.getExpectedFilePageCount();
		actualFilePageCount = pdfResultBean.getActualFilePageCount();

		String returnVal1 = "", returnVal2 = "";
		if (pdfResultBean.isError()) {
			returnVal1 = "Mismatch found in "
					+ pdfResultBean.getSummary().get(
							SummaryAttributes.NotMatched)
					+ " page(s) of PDF document.";
			returnVal2 = "\nExpected page Count: " + expectedFilePageCount
					+ ", ";
			returnVal2 = returnVal2 + "\nActual page Count: "
					+ actualFilePageCount + ", ";
			returnVal2 = returnVal2
					+ "\nTotal Pages Compared: "
					+ pdfResultBean.getSummary()
							.get(SummaryAttributes.Compared) + ", ";
			returnVal2 = returnVal2 + "\nTotal Pages Matched: "
					+ pdfResultBean.getSummary().get(SummaryAttributes.Matched)
					+ ", ";
			returnVal2 = returnVal2 + "\nResults saved at: "
					+ subFolderPathForOutput;
		} else {
			returnVal1 = "PDF Comparison is successful, NO mismatch found.";
			returnVal1 = returnVal1 + "\nExpected page Count: "
					+ expectedFilePageCount + ", ";
			returnVal1 = returnVal1 + "\nActual page Count: "
					+ actualFilePageCount + ", ";
			returnVal1 = returnVal1
					+ "\nTotal Pages Compared: "
					+ pdfResultBean.getSummary()
							.get(SummaryAttributes.Compared) + ", ";
			returnVal1 = returnVal1 + "\nTotal Pages Matched: "
					+ pdfResultBean.getSummary().get(SummaryAttributes.Matched)
					+ ", ";
			returnVal2 = returnVal1;
			if (subFolder.exists()) {
				subFolder.delete();
			}
		}

		String[] result = new String[3];
		result[0] = String.valueOf(pdfResultBean.isError());
		result[1] = returnVal2;
		result[2] = returnVal1;
		StringToIgnore = "";
		return result;
	}

}
