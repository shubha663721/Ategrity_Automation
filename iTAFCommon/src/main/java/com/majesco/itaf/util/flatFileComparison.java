package com.majesco.itaf.util;

import java.io.IOException;
import java.util.Scanner;



public class flatFileComparison {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException 
	{
		// TODO Auto-generated method stub
		
		 Scanner sc = new Scanner(System.in);
		 
		 System.out.println("Specify your OS Windows or Linux");
		 String osName = sc.nextLine();
		 
		 //LINUX
		 if(osName.equalsIgnoreCase("Linux"))
		 {
			 
			 //XML to FLAT
			 inboundFileTransfer cfr = new inboundFileTransfer();
			 //cfr.copyFilesToRemote(sc);   --Commented Meghna/Uncomment to execute
			 
			 //FLAT To XML
			 outboundTransferFiles cf = new outboundTransferFiles();
			 //cf.copyFiles(sc); --Commented Meghna/Uncomment to execute
			 
			 
			 //REPORT(.csv) To XML
			 reportToFlatConversion rfc = new reportToFlatConversion();
			 rfc.reportFileToXml(sc);
			 
			 sc.close();
			 System.out.println("Conversion Complete");
		 }
		 
		 //WINDOWS
		 else if (osName.equalsIgnoreCase("Windows"))
		 {
			 
			 //XML To FLAT
			 inboundFileTransferWin cfr = new inboundFileTransferWin();
			 cfr.copyFilesToRemoteWindows(sc);
			 
			//FLAT To XML
			 outboundTransferFileWin cf = new outboundTransferFileWin();
			 cf.copyFilesWindows(sc);
			 
			 
			//REPORT(.csv) To XML
			 reportToFlatConversion rfc = new reportToFlatConversion();
			 rfc.reportFileToXml(sc);
			 
			 sc.close();
			 System.out.println("Conversion Complete");
		 }
		 
		 
	}

}
