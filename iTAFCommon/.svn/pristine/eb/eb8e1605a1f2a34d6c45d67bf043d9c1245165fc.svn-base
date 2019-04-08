package com.majesco.itaf.rest.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//Mandar
import com.google.gson.JsonPrimitive;
import com.majesco.itaf.main.ITAFWebDriver;

public class ExcelUtility {
	
	private static Logger log = Logger.getLogger(ExcelUtility.class); 
	
	//Changes by Suchit150286 Start
	private static String transactionId;
	private static String transactionName;
	private static Sheet sheet = null;
	private static Map<String,Integer> cellMap = null;
	private static CellStyle borderStyle;
	private static CellStyle dateStyle;
	private static String cycleDate;
	private static Date date = new Date();
	private static String eventName;
	private static Set<String> ignoreKeys;
	static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();
	
	public static void writeResultToExcel(String reqJson,String expectedJson, String responseJson, File restResultFile, String cycleDateLocal, String transId, String transName, String transType) throws IOException{
		
		webDriver.getReport().setStatus("PASS");
		date = new Date();
		DataFormat dataFormat = null;
		FileInputStream inputStream = new FileInputStream(restResultFile);
		Workbook workbook = new HSSFWorkbook(inputStream);
		Sheet worksheet = workbook.getSheet("Result");
		sheet = worksheet;
		Row transTypeRow = sheet.createRow(sheet.getLastRowNum()+1);
		transactionId = transId;
		transactionName = transName;
		cycleDate = cycleDateLocal;
		cellMap = getHeaders(worksheet);//new HashMap<String, Integer>();
		borderStyle = workbook.createCellStyle();
		String sourceSystemEntityCode = getSourceSystemEntityCode(reqJson);
		createCell(transTypeRow, cellMap.get("TransactionID"), transType.replace("RestServiceJSON_POST", "V"), borderStyle);
		createCell(transTypeRow, cellMap.get("TransactionID")+1, sourceSystemEntityCode, borderStyle);
		
		CellStyle fontStyle = null;
		
		dateStyle = null;
		
		ignoreKeys = new HashSet<String>();
		ignoreKeys.add("BillNo");
		ignoreKeys.add("BillNumber");
		ignoreKeys.add("UserId");
		ignoreKeys.add("SystemTransactionSeq");
		ignoreKeys.add("SourceSystemEntityCode");
		ignoreKeys.add("FormSequenceBillingSystem");
		
		dataFormat = workbook.createDataFormat();
		dateStyle = workbook.createCellStyle();
		dateStyle.setDataFormat(dataFormat.getFormat("d/m/yyyy hh:mm"));
		
		fontStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setColor(HSSFColor.RED.index);
		fontStyle.setFont(font);

		createCellBorder(borderStyle);
		createCellBorder(dateStyle);
		createCellBorder(fontStyle);
		
		//Changes Start by Yatin for Configuration of Rest output
		List<filedata> theFileDataNoWritelist = new ArrayList<ExcelUtility.filedata>();
		List<filedata> theFileDataNoComparelist = new ArrayList<ExcelUtility.filedata>();
		readConfigFile(theFileDataNoWritelist,"NoWrite");
		readConfigFile(theFileDataNoComparelist,"NoCompare");
		//Changes End by Yatin for Configuration of Rest output
		
		JsonParser parser = new JsonParser();
		JsonElement expected = parser.parse(expectedJson);
		JsonElement response = parser.parse(responseJson);
		if(expected.isJsonObject()){
			JsonObject eventObject = expected.getAsJsonObject();
			for(String key : eventObject.keySet()){
				eventName = key.toString();
				break;
			}
			if (eventName.equals("OutputTransaction")){
				JsonObject eventObject1 = expected.getAsJsonObject().get("OutputTransaction").getAsJsonObject();
				for(String key : eventObject1.keySet()){
					eventName = key.toString();
					break;
				}
			}
			
//			JsonObject eventObject = expected.getAsJsonObject().get("OutputTransaction").getAsJsonObject();
//			for(String key : eventObject.keySet()){
//				eventName = key.toString();
//				break;
//			}
			
			processJsonObjectOld(expected.getAsJsonObject(),response.getAsJsonObject(),theFileDataNoWritelist,theFileDataNoComparelist);
		}
		inputStream.close();
		//System.out.println("In Workbook --> "+ workbook.toString());
		System.out.println("In Workbook --> "+ restResultFile.getName());
		System.out.println("In Worksheet --> "+ worksheet.getSheetName());
		workbook.write(new FileOutputStream(restResultFile));
		workbook.close();
	}
	
	private static String getSourceSystemEntityCode(String reqJson){
		Map<String,String> attrMap = new HashMap<String,String>();
		JsonParser parser = new JsonParser();
		JsonElement request = parser.parse(reqJson);
		processRequestJsonObject(request.getAsJsonObject(),attrMap);
		String retVal;
		retVal = attrMap.get("SourceEntityNumber");
		if(retVal==null)
			retVal = attrMap.get("SourceSystemEntityCode");
		if(retVal==null)
			retVal = "Not Found";
		return retVal;
	}
	
	private static void processRequestJsonObject(JsonObject jsonObjReq,Map<String,String> attrMap){
		for(String key : jsonObjReq.keySet()){ 
			JsonElement ele = jsonObjReq.get(key);
			if(ele.isJsonObject())
				processRequestJsonObject(ele.getAsJsonObject(),attrMap);
			if(ele.isJsonPrimitive())
				processRequestJsonPrimitive(key,ele.getAsJsonPrimitive(),attrMap);
		}
	}
	
	private static void processRequestJsonPrimitive(String key,JsonPrimitive jsonPrimitiveReq,Map<String,String> attrMap){
		String value;
		if(jsonPrimitiveReq.isString())
			value = jsonPrimitiveReq.getAsString();	
		else if(jsonPrimitiveReq.isNumber())
			value = jsonPrimitiveReq.getAsString();	
		else
			value = jsonPrimitiveReq.toString();
		attrMap.put(key, value);
	}
	
	private static void processJsonObjectOld(JsonObject jsonObjExp,JsonObject jsonObjResp,List<filedata> theFileDataNoWritelist,List<filedata> theFileDataNoComparelist){
		if(jsonObjExp==null){
			for(String key : jsonObjResp.keySet()){ 
				JsonElement ele = jsonObjResp.get(key);
				if(ele.isJsonObject() || ele.isJsonArray())
					processJsonString(key,"Not Found","Composite Value");
				
				if(ele.isJsonPrimitive()){
						processJsonPrimitiveOld(key,null,jsonObjResp.get(key).getAsJsonPrimitive(),theFileDataNoWritelist,theFileDataNoComparelist);
				}
			}
		}else if(jsonObjResp==null){
			for(String key : jsonObjExp.keySet()){ 
				JsonElement ele = jsonObjExp.get(key);
				if(ele.isJsonObject() || ele.isJsonArray())
					processJsonString(key,"Composite Value","Not Found");
				
				if(ele.isJsonPrimitive()){
						processJsonPrimitiveOld(key,jsonObjExp.get(key).getAsJsonPrimitive(),null,theFileDataNoWritelist,theFileDataNoComparelist);
				}
			}
		}else{
		
		for(String key : jsonObjExp.keySet()){ 
			JsonElement ele = jsonObjExp.get(key);
			if(ele.isJsonObject())
				processJsonObjectOld(ele.getAsJsonObject(),jsonObjResp.get(key).getAsJsonObject(),theFileDataNoWritelist,theFileDataNoComparelist);
			if(ele.isJsonArray()){
			//	eventName = key.toString();
				processJsonArrayOld(ele.getAsJsonArray(),jsonObjResp.get(key).getAsJsonArray(),key,theFileDataNoWritelist,theFileDataNoComparelist);
			}
			if(ele.isJsonPrimitive())
				if(jsonObjResp.get(key)!=null)
					processJsonPrimitiveOld(key,ele.getAsJsonPrimitive(),jsonObjResp.get(key).getAsJsonPrimitive(),theFileDataNoWritelist,theFileDataNoComparelist);
				else
					processJsonPrimitiveOld(key,ele.getAsJsonPrimitive(),null,theFileDataNoWritelist,theFileDataNoComparelist);
		}
		Set<String> expSet = jsonObjExp.keySet();
		Set<String> respSet = jsonObjResp.keySet();
		respSet.removeAll(expSet);
		for(String key : respSet){
			JsonElement ele = jsonObjResp.get(key);
			if(ele.isJsonObject())
				processJsonObjectOld(null,ele.getAsJsonObject(),theFileDataNoWritelist,theFileDataNoComparelist);
			else if(ele.isJsonArray()){
			//	eventName = key.toString();
				processJsonString(key,"Not Found","Composite Array");
			}
			else
			processJsonPrimitiveOld(key,null,jsonObjResp.get(key).getAsJsonPrimitive(),theFileDataNoWritelist,theFileDataNoComparelist);
		}
		}
	}
	
	private static void processJsonObject(JsonObject jsonObjExp,JsonObject jsonObjResp,List<filedata> theFileDataNoWritelist,List<filedata> theFileDataNoComparelist){
		if(jsonObjExp==null){
			for(String key : jsonObjResp.keySet()){ 
				JsonElement ele = jsonObjResp.get(key);
				if(ele.isJsonObject() || ele.isJsonArray())
					processJsonString(key,"Not Found","Composite Value");
				
				if(ele.isJsonPrimitive()){
						processJsonPrimitive(key,null,jsonObjResp.get(key).getAsJsonPrimitive(),theFileDataNoWritelist,theFileDataNoComparelist);
				}
			}
		}else if(jsonObjResp==null){
			for(String key : jsonObjExp.keySet()){ 
				JsonElement ele = jsonObjExp.get(key);
				if(ele.isJsonObject() || ele.isJsonArray())
					processJsonString(key,"Composite Value","Not Found");
				
				if(ele.isJsonPrimitive()){
						processJsonPrimitive(key,jsonObjExp.get(key).getAsJsonPrimitive(),null,theFileDataNoWritelist,theFileDataNoComparelist);
				}
			}
		}
		else{
			for(String key : jsonObjExp.keySet()){ 
				JsonElement ele = jsonObjExp.get(key);
				if(ele.isJsonObject())
					processJsonObject(ele.getAsJsonObject(),jsonObjResp.get(key).getAsJsonObject(),theFileDataNoWritelist,theFileDataNoComparelist);
				if(ele.isJsonArray()){
					
					processJsonArray(ele.getAsJsonArray(),jsonObjResp.get(key).getAsJsonArray(),key,theFileDataNoWritelist,theFileDataNoComparelist);
					
				}
				if(ele.isJsonPrimitive()){
					if(jsonObjResp.get(key)!=null)
						processJsonPrimitive(key,ele.getAsJsonPrimitive(),jsonObjResp.get(key).getAsJsonPrimitive(),theFileDataNoWritelist,theFileDataNoComparelist);
				}
			}
		}
	}
	
	private static void processJsonString(String key,String exp,String resp){
		Row row = sheet.createRow(sheet.getLastRowNum()+1);
		createCell(row, cellMap.get("TransactionID"), transactionId, borderStyle);
		createCell(row, cellMap.get("CycleDate"), cycleDate, borderStyle);
		createCell(row,cellMap.get("ExecutionDate"), date, dateStyle);
		createCell(row,cellMap.get("KeyName") , key, borderStyle);
		createCell(row,cellMap.get("TransactionName") , transactionName, borderStyle);
		createCell(row, cellMap.get("ExpectedValue"), exp, borderStyle);
		createCell(row, cellMap.get("ActualValue"), resp, borderStyle);
		createCell(row, cellMap.get("Status"),"FAILED", borderStyle);
	}
	
	private static void processJsonArrayOld(JsonArray jsonArrExp,JsonArray jsonArrResp,String key, List<filedata> theFileDataNoWritelist,List<filedata> theFileDataNoComparelist){
		if(jsonArrExp.size()!=jsonArrResp.size()){
			Row row = sheet.createRow(sheet.getLastRowNum()+1);
			createCell(row, cellMap.get("TransactionID"), transactionId, borderStyle);
			createCell(row, cellMap.get("CycleDate"), cycleDate, borderStyle);
			createCell(row,cellMap.get("ExecutionDate"), date, dateStyle);
			createCell(row,cellMap.get("KeyName") , key, borderStyle);
			createCell(row,cellMap.get("TransactionName") , transactionName, borderStyle);
			createCell(row, cellMap.get("ExpectedValue"), "Count" + jsonArrExp.size(), borderStyle);
			createCell(row, cellMap.get("ActualValue"), "Count" + jsonArrResp.size(), borderStyle);
			createCell(row, cellMap.get("Status"),"FAILED", borderStyle);
			webDriver.getReport().setStatus("FAIL");
		}
		else {
			for(int i=0;i<jsonArrExp.size();i++){
				JsonElement ele = jsonArrExp.get(i);
				if(ele.isJsonObject()){
					if(jsonArrResp.size()>i)
					processJsonObjectOld(ele.getAsJsonObject(),jsonArrResp.get(i).getAsJsonObject(),theFileDataNoWritelist,theFileDataNoComparelist);
				}
				if(ele.isJsonArray())
					processJsonArrayOld(ele.getAsJsonArray(),jsonArrResp.get(i).getAsJsonArray(),key,theFileDataNoWritelist,theFileDataNoComparelist);
				//if(ele.isJsonPrimitive())
					//processJsonPrimitive(ele.getAsJsonPrimitive(),jsonArrResp.get(i).getAsJsonPrimitive());
			}
		}
	}
	
	private static void processJsonArray(JsonArray jsonArrExp,JsonArray jsonArrResp,String key,List<filedata> theFileDataNoWritelist,List<filedata> theFileDataNoComparelist){
		
		List<JsonElement> expList = new ArrayList<JsonElement>();
		for(int i=0;i<jsonArrExp.size();i++){
			expList.add(jsonArrExp.get(i));
		}
		//jsonArrExp.forEach(expList::add);
		
		List<JsonElement> respList = new ArrayList<JsonElement>();
		for(int i=0;i<jsonArrResp.size();i++){
			respList.add(jsonArrResp.get(i));
		}
		//jsonArrResp.forEach(respList::add);
		
		if(expList.size()!=respList.size()){
			Row row = sheet.createRow(sheet.getLastRowNum()+1);
			createCell(row, cellMap.get("TransactionID"), transactionId, borderStyle);
			createCell(row, cellMap.get("CycleDate"), cycleDate, borderStyle);
			createCell(row,cellMap.get("ExecutionDate"), date, dateStyle);
			createCell(row,cellMap.get("KeyName") , key, borderStyle);
			createCell(row,cellMap.get("TransactionName") , transactionName, borderStyle);
			createCell(row, cellMap.get("ExpectedValue"), "Count" + expList.size(), borderStyle);
			createCell(row, cellMap.get("ActualValue"), "Count" + respList.size(), borderStyle);
			createCell(row, cellMap.get("Status"),"FAILED", borderStyle);
			webDriver.getReport().setStatus("FAIL");
		}else{
			List<Integer> expIdx = new ArrayList<Integer>();
			for(int i=0;i<expList.size();i++)	expIdx.add(i);
			List<Integer> respIdx = new ArrayList<Integer>();
			for(int i=0;i<respList.size();i++)	respIdx.add(i);
			
			for(int i=0;i<expList.size();i++){
				JsonElement ele = expList.get(i);
				
				if(ele.isJsonObject()){
					int idx;
					//int idx = respList.indexOf(ele);
					//if(idx<0)
						idx = jsonIndexOf(ele,respList); //respList.indexOf(ele);
					
					if(idx >= 0){
						expIdx.remove((Object)i);
						respIdx.remove((Object)idx);
						processJsonObject(ele.getAsJsonObject(),respList.get(idx).getAsJsonObject(),theFileDataNoWritelist,theFileDataNoComparelist);
					}else if(expList.size()==respList.size() && expList.size()==1){
						expIdx.remove((Object)i);
						respIdx.remove((Object)i);
						processJsonObject(ele.getAsJsonObject(),respList.get(i).getAsJsonObject(),theFileDataNoWritelist,theFileDataNoComparelist);
					}
					System.out.println(transactionId);
					System.out.println(i);
					System.out.println(ele.getAsJsonObject().equals(jsonArrResp.get(i).getAsJsonObject()));
				}
				if(ele.isJsonArray())
					processJsonArray(ele.getAsJsonArray(),jsonArrResp.get(i).getAsJsonArray(),key,theFileDataNoWritelist,theFileDataNoComparelist);
				//if(ele.isJsonPrimitive())
					//processJsonPrimitive(ele.getAsJsonPrimitive(),jsonArrResp.get(i).getAsJsonPrimitive());
			}
			for(int i : expIdx){
				processJsonObject(expList.get(i).getAsJsonObject(),null,theFileDataNoWritelist,theFileDataNoComparelist);
			}
			for(int i : respIdx){
				processJsonObject(null,respList.get(i).getAsJsonObject(),theFileDataNoWritelist,theFileDataNoComparelist);
			}
		}
	}
	
	private static int jsonIndexOf(JsonElement ele,List<JsonElement> respList){
		for(int i=0;i<respList.size();i++){
			if(jsonEquals(ele.getAsJsonObject(),respList.get(i).getAsJsonObject()))
				return i;
		}
		return -1;
	}
	
	private static boolean jsonEquals(JsonObject jsonObjExp,JsonObject jsonObjResp){
		boolean retVal = true;
		if(jsonObjExp.equals(jsonObjResp))
			return true;
		else{
			for(String key : jsonObjExp.keySet()){ 
				JsonElement ele = jsonObjExp.get(key);
				if(ele.isJsonPrimitive()){
					retVal = ele.equals(jsonObjResp.get(key));
					if(!retVal){
						if(ignoreKeys.contains(key)) retVal = true;
						else break;
					}
				}else if(ele.isJsonArray()){
					if(!jsonObjResp.get(key).isJsonArray()){
						retVal = false;	break;
					}else if(ele.getAsJsonArray().size()!=jsonObjResp.get(key).getAsJsonArray().size()){
						retVal = false; break;
					}
				}else{
					retVal = jsonEquals(ele.getAsJsonObject(),jsonObjResp.get(key).getAsJsonObject());
					if(!retVal) break;
				}
				
			}
		}
		return retVal;
	}
	
	private static void processJsonPrimitiveOld(String key,JsonPrimitive jsonPrimitiveExp,JsonPrimitive jsonPrimitiveResp,
					List<filedata> theFileDataNoWritelist,List<filedata> theFileDataNoComparelist){
		String objExp;
		String objResp;
		Boolean printExcel = true;
		Boolean compareData = true;
		
		if(jsonPrimitiveExp==null){
			objExp = "Not Found";
			objResp = jsonPrimitiveResp.getAsString();
		}else if(jsonPrimitiveResp==null){
			objExp = jsonPrimitiveExp.getAsString();
			objResp = "Not Found";
		}
		else{
			if(jsonPrimitiveExp.isString()){
				objExp = jsonPrimitiveExp.getAsString();
				objResp = jsonPrimitiveResp.getAsString();
			}else if(jsonPrimitiveExp.isNumber()){
				objExp = jsonPrimitiveExp.getAsString();
				objResp = jsonPrimitiveResp.getAsString();
			}else{
				objExp = jsonPrimitiveExp.toString();
				objResp = jsonPrimitiveResp.toString();
			}
		}
		
		for (filedata filedata : theFileDataNoWritelist)
		{
			if(filedata.Event.equals(eventName) && (filedata.Attribute.equals(key)))
			{
				printExcel = false;
			}
		}
		for	(filedata filedata : theFileDataNoComparelist)
		{
			if(filedata.Event.equals(eventName) && (filedata.Attribute.equals(key)))
			{
				compareData = false;
			}
		}
		
		if(printExcel){
			Row row = sheet.createRow(sheet.getLastRowNum()+1);
			createCell(row, cellMap.get("TransactionID"), transactionId, borderStyle);
			createCell(row, cellMap.get("CycleDate"), cycleDate, borderStyle);
			createCell(row,cellMap.get("ExecutionDate"), date, dateStyle);
			createCell(row,cellMap.get("KeyName") , key, borderStyle);
			createCell(row,cellMap.get("TransactionName") , transactionName, borderStyle);
			createCell(row, cellMap.get("ExpectedValue"), objExp, borderStyle);
			createCell(row, cellMap.get("ActualValue"), objResp, borderStyle);
			if(compareData){
				int cmpResult = CommonUtils.compare(objExp, objResp);
				if(cmpResult == 0){
					createCell(row, cellMap.get("Status"),"PASS", borderStyle);
				}else{
					createCell(row, cellMap.get("Status"),"FAILED", borderStyle);
					webDriver.getReport().setStatus("FAIL");
				}
				
			}
		}
	}
	
	private static void processJsonPrimitive(String key,JsonPrimitive jsonPrimitiveExp,JsonPrimitive jsonPrimitiveResp,List<filedata> theFileDataNoWritelist,List<filedata> theFileDataNoComparelist){
		
			String objExp;
			String objResp;
			Boolean printExcel = true;
			Boolean compareData = true;
			if(jsonPrimitiveExp==null){
				objExp = "Not Found";
				objResp = jsonPrimitiveResp.getAsString();
			}else if(jsonPrimitiveResp==null){
				objExp = jsonPrimitiveExp.getAsString();
				objResp = "Not Found";
			}else{
				if(jsonPrimitiveExp.isString()){
					objExp = jsonPrimitiveExp.getAsString();
					objResp = jsonPrimitiveResp.getAsString();
				}else if(jsonPrimitiveExp.isNumber()){
					objExp = jsonPrimitiveExp.getAsString();
					objResp = jsonPrimitiveResp.getAsString();
				}else{
					objExp = jsonPrimitiveExp.toString();
					objResp = jsonPrimitiveResp.toString();
				}
			}
			
			for (filedata filedata : theFileDataNoWritelist)
			{
				if(filedata.Event.equals(eventName) && (filedata.Attribute.equals(key)))
				{
					printExcel = false;
				}
			}
			for	(filedata filedata : theFileDataNoComparelist)
			{
				if(filedata.Event.equals(eventName) && (filedata.Attribute.equals(key)))
				{
					compareData = false;
				}
			}
			if(printExcel){
				Row row = sheet.createRow(sheet.getLastRowNum()+1);
				createCell(row, cellMap.get("TransactionID"), transactionId, borderStyle);
				createCell(row, cellMap.get("CycleDate"), cycleDate, borderStyle);
				createCell(row,cellMap.get("ExecutionDate"), date, dateStyle);
				createCell(row,cellMap.get("KeyName") , key, borderStyle);
				createCell(row,cellMap.get("TransactionName") , transactionName, borderStyle);
				createCell(row, cellMap.get("ExpectedValue"), objExp, borderStyle);
				createCell(row, cellMap.get("ActualValue"), objResp, borderStyle);
				if(compareData){
					int cmpResult = CommonUtils.compare(objExp, objResp);
					if(cmpResult == 0){
						createCell(row, cellMap.get("Status"),"PASS", borderStyle);
					}else{
						createCell(row, cellMap.get("Status"),"FAILED", borderStyle);
						webDriver.getReport().setStatus("FAIL");
					}
					
				}
				
				//createCell(row, cellMap.get("Status"), CommonUtils.compare(objExp, objResp) == 0 ? "PASS" : "FAILED", borderStyle);
			}
	}
	//Changes by Suchit150286 End
	

	public static void writeResultToExcel(Map<String, Map<String, String>> result, File file, String cycleDate,
			String transactionId, String transactionName)throws IOException{
		Date date = new Date();
		DataFormat dataFormat = null;
		FileInputStream inputStream = new FileInputStream(file);
		HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
		HSSFSheet worksheet = workbook.getSheet("Result");
		Map<String, Integer> cellMap = getHeaders(worksheet);//new HashMap<String, Integer>();
		CellStyle fontStyle = null;
		CellStyle borderStyle = workbook.createCellStyle();
		CellStyle dateStyle = null;
		
		dataFormat = workbook.createDataFormat();
		dateStyle = workbook.createCellStyle();
		dateStyle.setDataFormat(dataFormat.getFormat("d/m/yyyy hh:mm"));
		
		fontStyle = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.RED.index);
		fontStyle.setFont(font);

		
		
		List<String> BillingDetailsInquiryExpectList = null;
		List<String> BillingDetailsInquiryActualList = null;
		Set<String> keys = result.keySet();
		createCellBorder(borderStyle);
		createCellBorder(dateStyle);
		createCellBorder(fontStyle);
		for(String key : keys){
			try{
				HSSFRow row = worksheet.createRow(worksheet.getLastRowNum()+1);
				Map<String, String> valueMap = result.get(key);
				Object expectedValue = valueMap.get("ExpectedValue");
				Object actualValue = valueMap.get("ActualValue");
				System.out.println("writeResultToExcel >>>>>>>>>>>>>>>> "+ expectedValue.toString()+ " >>>>>>>>>>>>>> And <<<<<<<<<<<<<<<<<<< "
						+ "  " + actualValue.toString());//aa 
				
				//added by aseem changes to billing deatils
				if(key.equals("BillingDetailsInquiry"))
				{
					
					String[] billdeatilsArray= valueMap.get("ExpectedValue").split(",");
					String[] billdeatilsArrayActual= valueMap.get("ActualValue").split(",");
					
					//System.out.println(" In Bill Details inquiry  "+ billdeatilsArray.toString()+ " >>>>>>>>>>>>>> And <<<<<<<<<<<<<<<<<<< "
					//		+ "  " + billdeatilsArrayActual.toString());//aa
					int num=0;
					BillingDetailsInquiryExpectList= Arrays.asList(billdeatilsArray);
					BillingDetailsInquiryActualList= Arrays.asList(billdeatilsArrayActual);
					
					//System.out.println(" In Billing Details List "+BillingDetailsInquiryExpectList.toString()+ " >>>>>>>>>>>>>> And <<<<<<<<<<<<<<<<<<< "
					//		+ "  " +BillingDetailsInquiryActualList.toString());//aa
					
				if(BillingDetailsInquiryExpectList.size()==BillingDetailsInquiryActualList.size())
					{											
					for (String expectedVal : BillingDetailsInquiryExpectList) {
						expectedVal=expectedVal.replaceAll("[{}\\[\\]]","");
						Object keyName  =(String)expectedVal.substring(0, expectedVal.indexOf("="));
						Object keyValue =(String)expectedVal.substring(expectedVal.indexOf("=")+1,expectedVal.length());
						String actualVal= BillingDetailsInquiryActualList.get(num);
						actualVal=actualVal.replaceAll("[{}\\[\\]]","");//aa
						Object KeyvalueActual=actualVal.substring(actualVal.indexOf("=")+1,actualVal.length());
						row = worksheet.createRow(worksheet.getLastRowNum()+1);
						createCell(row, cellMap.get("TransactionID"), transactionId, borderStyle);
						createCell(row, cellMap.get("CycleDate"), cycleDate, borderStyle);
						createCell(row, cellMap.get("ExecutionDate"), date, dateStyle);
						createCell(row, cellMap.get("KeyName") , key, borderStyle);
						createCell(row, cellMap.get("TransactionName") , transactionName, borderStyle);						
						createCell(row, cellMap.get("KeyName"),keyName, borderStyle);
						createCell(row, cellMap.get("ExpectedValue"),keyValue, borderStyle);
						createCell(row, cellMap.get("ActualValue"), KeyvalueActual, borderStyle);								
						createCell(row, cellMap.get("Status"), CommonUtils.compare(keyValue, KeyvalueActual) == 0 ? "PASS" : "FAILED", borderStyle);
						createCell(row, cellMap.get("Comments"), null, fontStyle);
						num++;
					}
				  }else
				  {
					  createCell(row, cellMap.get("Comments"), "Expected Array and Actual Array is not equal " , fontStyle);
					  //Need to throw exception
				  }					
					
				}
				//end added by aseem changes to billing deatils
				else if(!key.equals("BillingDetailsInquiry"))
				{
				createCell(row, cellMap.get("TransactionID"), transactionId, borderStyle);
				createCell(row, cellMap.get("CycleDate"), cycleDate, borderStyle);
				createCell(row,cellMap.get("ExecutionDate"), date, dateStyle);
				createCell(row,cellMap.get("KeyName") , key, borderStyle);
				createCell(row,cellMap.get("TransactionName") , transactionName, borderStyle);
				createCell(row, cellMap.get("ExpectedValue"), expectedValue, borderStyle);
				createCell(row, cellMap.get("ActualValue"), actualValue, borderStyle);
				createCell(row, cellMap.get("Status"), CommonUtils.compare(expectedValue, actualValue) == 0 ? "PASS" : "FAILED", borderStyle);
				}
				
				if(valueMap != null && valueMap.size() == 0){
					//key is missing
					createCell(row, cellMap.get("Comments"), "Key missing" , fontStyle);
					createCell(row, cellMap.get("Status"), "FAILED", borderStyle);
				}else{
					
					createCell(row, cellMap.get("Comments"), null, fontStyle);
					if(expectedValue == null && actualValue != null){
						createCell(row, cellMap.get("Status"), "WARNING", borderStyle);
					}else if(expectedValue != null && actualValue == null){
						createCell(row, cellMap.get("Status"), "FAILED", borderStyle);
					}
					//createCell(row, cellMap.get("Status"), CommonUtils.compare(expectedValue, actualValue) == 0 ? "PASS" : "FAILED", borderStyle);				
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		inputStream.close();
		System.out.println("In Workbook>>>>>>>>>>>>"+workbook.toString());
		System.out.println("In Worksheer>>>>>>>>>>>>"+worksheet.toString());
		workbook.write(new FileOutputStream(file));
		workbook.close();
		
	}
	
	
	
	public static Map<String,Integer> getHeaders(Sheet worksheet){
		Map<String,Integer> cellMap = new HashMap<String, Integer>();
		Row headerRow = worksheet.getRow(0);
		int cellCount = headerRow.getPhysicalNumberOfCells();
		for(int colIndex=0; colIndex < cellCount; colIndex++)
		{
			cellMap.put(headerRow.getCell(colIndex).getStringCellValue(), colIndex);
		}
		
		return cellMap;
	}
	
	private static void createCellBorder(CellStyle style){
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
	}
	
	private static Cell createCell(Row row, int columnNo, Object value, CellStyle style){
		Cell cell = row.createCell(columnNo);		
		if(value != null){
			if(value instanceof Date){
				cell.setCellValue((Date) value);			
			}else if(value instanceof String){
				cell.setCellValue((String) value);
			}
		}
		
		cell.setCellStyle(style);
		
		
		return cell;
	}
	
	
	//Changes Start by Yatin for Configuration of Rest output
	private static void readConfigFile(List<filedata> theFileDatalist,String eventType) throws FileNotFoundException, IOException {
	
		String readFilePath= "";
		String line = "";
		String[] parts;
		if (eventType.equalsIgnoreCase("NoWrite")){
			readFilePath = "D:\\WorkspaceFinal\\iTAFSeleniumWeb\\ExcelUtilityConfigurationForNoWrite.csv";
		}else if(eventType.equalsIgnoreCase("NoCompare")) {
			readFilePath = "D:\\WorkspaceFinal\\iTAFSeleniumWeb\\ExcelUtilityConfigurationForNoCompare.csv";
		}
		
		BufferedReader br = null;
		try{
			if(!readFilePath.isEmpty()){
				br = new BufferedReader(new FileReader(readFilePath)); 
				while((line = br.readLine()) != null){
					parts = line.split(",");
					String E=null;
					String A=null;
					E = parts[0].toString();
					A =  parts[1].toString();
					filedata theFileData= new filedata(E,A);
					theFileDatalist.add(theFileData);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			br.close();
		}
	}
	
	public static class filedata{
		String Event;
		String Attribute;
		
		public filedata (String E,String A){
			Event = E;
			Attribute = A;
		}
		
		public void getfiledata (String E,String A){
			E = Event.toString();
			A = Attribute.toString();
		}
		
	}

	//Changes End by Yatin for Configuration of Rest output
}
