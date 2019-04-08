package com.majesco.itaf.util;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.LibraryLoader;
import com.jacob.com.Variant;
import com.majesco.itaf.main.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;


public class Jacob {

	private final static Logger log = Logger.getLogger(Jacob.class.getName());
	public static void main(String filepath, String macroname) {
		// TODO Auto-generated method stub

        String libFile = System.getProperty("os.arch").equals("amd64") ? System.getProperty("user.dir")+"\\libs\\Jacob\\jacob-1.18-x64.dll" : System.getProperty("user.dir")+"\\libs\\Jacob\\jacob-1.18-x86.dll";
       System.out.println("libFile : "+libFile);
        try {
            /* Read DLL file*/
        	 InputStream inputStream = new FileInputStream(libFile);
            /**
             *  Step 1: Create temporary file under <%user.home%>\AppData\Local\Temp\jacob.dll 
             *  Step 2: Write contents of `inputStream` to that temporary file.
             */
            File temporaryDll = File.createTempFile("jacob", ".dll");
            FileOutputStream outputStream = new FileOutputStream(temporaryDll);
            byte[] array = new byte[8192];
            for (int i = inputStream.read(array); i != -1; i = inputStream.read(array)) {
                outputStream.write(array, 0, i);
            }
            outputStream.close();
            /**
             * `System.setProperty(LibraryLoader.JACOB_DLL_PATH, temporaryDll.getAbsolutePath());`
             * Set System property same like setting java home path in system.
             * 
             * `LibraryLoader.loadJacobLibrary();`
             * Load JACOB library in current System.
             */
            System.setProperty(LibraryLoader.JACOB_DLL_PATH, temporaryDll.getAbsolutePath());
            LibraryLoader.loadJacobLibrary();

            /**
             * Create ActiveXComponent using CLSID. You can also use program id here.
             * Next line(commented line/compProgramID) shows you how you can create ActiveXComponent using ProgramID.
             */
            ActiveXComponent compCLSID = new ActiveXComponent("clsid:{00024500-0000-0000-C000-000000000046}");
            /*ActiveXComponent compProgramID = new ActiveXComponent("Excel.Application");*/

            System.out.println("The Library been loaded, and an activeX component been created");                       
          //  File file = new File("C:\\Users\\Mohit12517\\Documents\\Book1.xlsm");
            File file = new File(filepath);
            //String macroName = "D:\\Book1.xlsm!TestFunction1_Click";
            String macroName = macroname;
            callExcelMacro(file, macroName);

            
            /* Temporary file will be removed after terminating-closing-ending the application-program */
            temporaryDll.deleteOnExit();
            inputStream.close();
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
            e.printStackTrace();
        }
	}
	
	public static void callExcelMacro(File file, String macroName) {
        ComThread.InitSTA(true);
        final ActiveXComponent excel = new ActiveXComponent("Excel.Application");
        try{
            excel.setProperty("EnableEvents", new Variant(false));

            Dispatch workbooks = excel.getProperty("Workbooks")
                    .toDispatch();

            Dispatch workBook = Dispatch.call(workbooks, "Open",
                    file.getAbsolutePath()).toDispatch();

            // Calls the macro
            Variant V1 = new Variant( file.getName() + macroName);
            if(Config.projectName.equals("DistributionManagement")){//Minaakshi : 03-10-2018
            	Variant result1 = Dispatch.call(excel, "Run", V1);
            	log.info(result1.getString());
            }else{
            	Variant result = Dispatch.call(excel, "Run", macroName);
            	log.info(result.getString());
            }            
            // Saves and closes
            Dispatch.call(workBook, "Save");

            com.jacob.com.Variant f = new com.jacob.com.Variant(true);
            Dispatch.call(workBook, "Close", f);

        } catch (Exception e) {
        	log.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            excel.invoke("Quit", new Variant[0]);
            ComThread.Release();
        }
    }
	
}
