package com.majesco.itaf.util;

import java.io.IOException;
import java.security.Permission;

import org.apache.log4j.Logger;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.unidex.xflat.flat2xml;

public class flatToXmlConversion {
	
	private final static Logger log = Logger.getLogger(flatToXmlConversion.class.getName());
	
	//Flat to xml converter
	public void convertFlatToXml(String copyTo, String fileName, String extn, String xflFile)
	{
		SystemExitControl.forbidSystemExitCall();
		try
		{
			SystemExitControl.forbidSystemExitCall();

			try {	 
				flat2xml flatToXml= new flat2xml();
				//String[] conData = {copyTo+"/"+xflFile, copyTo+"/"+fileName,copyTo+"/"+fileName.replaceAll(extn,".xml")};
				String[] conData = {xflFile, copyTo+"/"+fileName,copyTo+"/"+fileName.replaceAll(extn,".xml")};    //Meghna--To place xfl file in one place
				flatToXml.main(conData);
				}catch (SystemExitControl.ExitTrappedException e) {
					log.error(e.getMessage(), e);
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


class SystemExitControl {
	 
    public static class ExitTrappedException extends SecurityException {
    }
 
    public static void forbidSystemExitCall() {
        final SecurityManager securityManager = new SecurityManager() {
            @Override
            public void checkPermission(Permission permission) {
                if (permission.getName().contains("exitVM")) {
                    throw new ExitTrappedException();
                }
            }
        };
        System.setSecurityManager(securityManager);
    }
 
    public static void enableSystemExitCall() {
        System.setSecurityManager(null);
    }
}