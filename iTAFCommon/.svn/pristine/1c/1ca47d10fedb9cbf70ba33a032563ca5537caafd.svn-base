package com.majesco.itaf.util;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.unidex.xflat.xml2flat;

public class xmlToFlatConversion 
{
	final static Logger log = Logger.getLogger(xmlToFlatConversion.class.getName());

	//Linux Server
	//Meghna- Changed method to static - FlatFile
	public static void convertXmlToFlat(String copyFromLocal,String copyToRemote, String xmlFileName, String extn, String xflFile)
	{
		SystemExitControl.forbidSystemExitCall();
		try
		{
			SystemExitControl.forbidSystemExitCall();

			try {	 
				xml2flat xmlToFlat= new xml2flat();
				//String[] conData = {copyFromLocal+"/"+xflFile, copyFromLocal+"/"+xmlFileName,copyFromLocal+"/"+xmlFileName.replaceAll(".xml",extn)};
				String[] conData = {xflFile, copyFromLocal+"/"+xmlFileName,copyFromLocal+"/"+xmlFileName.replaceAll(".xml",extn)};
				xmlToFlat.main(conData);
				
				}
			catch (SystemExitControl.ExitTrappedException e) {
		            System.out.println("Forbidding call to System.exit");
		        }
			SystemExitControl.enableSystemExitCall();
		}
		catch (IOException e)
		{
        	log.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

}


