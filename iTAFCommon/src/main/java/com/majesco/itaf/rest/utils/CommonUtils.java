package com.majesco.itaf.rest.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.Scanner;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CommonUtils {
	public static Properties loadProperties(String file){
		InputStream inputStream = new CommonUtils().getClass().getResourceAsStream("/"+file);
		Properties properties = new Properties();
		try{
			properties.load(inputStream);
		}catch(IOException ex){
			return null;
		}
		return properties;
	}
	
	public static int compareStrings(final String str1, final String str2, final boolean nullIsLess) {
        if (str1 == str2) {
            return 0;
        }
        if (str1 == null) {
            return nullIsLess ? -1 : 1;
        }
        if (str2 == null) {
            return nullIsLess ? 1 : - 1;
        }
        return str1.compareTo(str2);
    }
	
	public static String readJsonFile(String file)throws IOException{
		File inputFile = new File(file);
		
		return readFile(inputFile);
	}
	
	public static String readJsonFile(File file)throws IOException{
		return readFile(file);
	}
	//Added by Suchit150286 Start
	public static String readJsonFile2(String file)throws IOException{
		File inputFile = new File(file);
		
		return readFile2(inputFile);
	}
	
	public static String readJsonFile2(File file)throws IOException{
		return readFile2(file);
	}
	
	private static String readFile2(File file)throws IOException{
		String str="";
		String retVal="";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		while((str=br.readLine())!=null)
			retVal = retVal + str;
		br.close();
		return retVal;
	}
	//Added by Suchit150286 End
	
	public static String writeToFile(String file, byte[] data)throws IOException{
		File inputFile = new File(file);
		
		return writeFile(inputFile, data);
	}
	
	public static String WriteToFile(File file, byte[] data)throws IOException{
		return writeFile(file, data);
	}
	
	
	public static String toPrettyFormat(String jsonString) 
	  {
	      JsonParser parser = new JsonParser();
	      JsonObject json=null;
	      try{
	      json = parser.parse(jsonString).getAsJsonObject();
	      }
	      catch(Exception e){
	    	  System.out.println(jsonString);
	      }

	      Gson gson = new GsonBuilder().setPrettyPrinting().create();
	      String prettyJson = gson.toJson(json);

	      return prettyJson;
	  }
	
	private static String readFile(File file)throws IOException{
		Scanner sc = null;
	 	StringBuilder json = new StringBuilder();		
		sc = new Scanner(file);
		while(sc.hasNext()){
			json.append(sc.next());
		}
		return json.toString();
	
	}
	
	private static String writeFile(File file, byte[] data)throws IOException{
		OutputStream os = null;

		os = new FileOutputStream(file);
		os.write(data);
		os.flush();
		os.close();
		return "success";

	}
	
	public static int compare(Object o1, Object o2){
		if( o1 == null && o2 != null){
			return -1;
		}
		if(o1 != null && o2 == null){
			return 1;
		}
		if(NumberUtils.isNumber(o1.toString())){
			if(NumberUtils.isNumber(o2.toString())){
				BigDecimal value1 = NumberUtils.createBigDecimal(o1.toString());
				BigDecimal value2 = NumberUtils.createBigDecimal(o2.toString());
				return value1.compareTo(value2);
			}else{
				return -1;
			}
		}else {
			return compareStrings(o1.toString(), o2.toString(), true);
		}
	}
	
	public static String readJsonFile3(String file)throws IOException{
        File inputFile = new File(file);
        
        return readFile3(inputFile);
 }


	 //**** 10/18/2018 to verify REST issue
	private static String readFile3(File file)throws IOException{
        
        StringBuilder json = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));        
        try {
               String lineStr = null;
               while ((lineStr = br.readLine()) != null) {
                     json.append(lineStr.trim());
               }
               return json.toString();
        } finally {
               br.close();
        }
        
 }
	 
	 //***

}
