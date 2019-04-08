package com.majesco.itaf.rest.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.majesco.itaf.main.WebHelperUtil;

public class JsonUtility {
	final static Logger log = Logger.getLogger(JsonUtility.class.getName());

	@SuppressWarnings("unchecked")
	private static void compareJson(Map<String, Object> holdingMap, Map<String, Object> clientMap, 
			StringBuilder log, Map<String, Map<String, String>> resultMap){
		Set<String> holdingMapKeySet = holdingMap.keySet();
		Set<String> clientMapKeySet = null;
		for(String holdingMapKey : holdingMapKeySet){
			Object holdingMapKeyObject = holdingMap.get(holdingMapKey); 
			if( holdingMapKeyObject != null && holdingMapKeyObject instanceof Map){
				if(clientMap.containsKey(holdingMapKey)){
					compareJson((Map<String, Object>)holdingMapKeyObject, 
							(Map<String, Object>)clientMap.get(holdingMapKey), log, resultMap);
				}else{
					//put all holding map keys in log
					Set<String> childMapKeys = ((Map<String, Object>) holdingMapKeyObject).keySet();
					for(String childMapKey : childMapKeys){
						log.append("Missing key "+childMapKey+"\n");
						resultMap.put(childMapKey, null);
					}
				}
			}else{
				//compare keys
				Map<String,String> diffMap = new HashMap<String, String>();
				if(holdingMapKeyObject instanceof String){
					
					// ***For GB - below given change is to handle the double quotes issues in the actual/expected json data ***//
					String act = (String)holdingMapKeyObject.toString().replaceAll("\"", "");//08/06/2018 - FOR GB - Mandar
					String  exp = clientMap.get(holdingMapKey).toString().replaceAll("\"", "");//08/06/2018 - FOR GB
					//***END***//
					
					//if(CommonUtils.compareStrings((String)holdingMapKeyObject, clientMap.get(holdingMapKey).toString(),true) != 0)/08/06/2018 - FOR GB{
					if(CommonUtils.compareStrings(act, exp,true) != 0){
						log.append("Key: "+holdingMapKey+"\texpected value: "+holdingMapKeyObject+
								"\tactual value: "+clientMap.get(holdingMapKey)+"\n");
						
					}
				}else if(holdingMapKeyObject instanceof Number){
					
			
					// ***For GB - below given change is to handle the double quotes issues in the actual/expected json data ***//
					Number act = Double.parseDouble((String)holdingMapKeyObject.toString().replaceAll("\"", ""));//08/06/2018 - FOR GB - Mandar
					Number  exp =  Double.parseDouble(clientMap.get(holdingMapKey).toString().replaceAll("\"", ""));//08/06/2018 - FOR GB
					
					
					
					if(!(act).equals(exp))
						//***END***//
					
					{
						log.append("Key: "+holdingMapKey+"\texpected value: "+holdingMapKeyObject+
								"\tactual value: "+clientMap.get(holdingMapKey)+"\n");
						
					}
					
				}
				
				diffMap.put("ExpectedValue", holdingMapKeyObject.toString());
				diffMap.put("ActualValue", clientMap.get(holdingMapKey).toString());
				log.append("Key: To check array elements ::: "+"\texpected value: "+holdingMapKeyObject.toString()+
						"\tactual value: "+clientMap.get(holdingMapKey).toString()+"\n");
				
				//resultMap.put(holdingMapKey, diffMap);
				resultMap.put(holdingMapKey, diffMap);
			}
		}
		
		//Checking if any of the response map keys are missing from holding map.  
		clientMapKeySet = clientMap.keySet();
		clientMapKeySet.removeAll(holdingMapKeySet);
		if(!clientMapKeySet.isEmpty()){
			
			for(String missingKey : clientMapKeySet){
				Map<String,String> diffMap = new HashMap<String, String>();
				log.append("Missing key: <"+ missingKey +"> key not found in holding map.\n");
				diffMap.put("ExpectedValue", null);
				diffMap.put("ActualValue", clientMap.get(missingKey).toString());
				resultMap.put(missingKey, diffMap);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Object> T compareJSON(String responseJson, String expectedJson, String returnType)throws Exception{
		 T t = null;
		Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
		
		StringBuilder log = new StringBuilder();

		Map<String, Object> holdingMap =(Map<String, Object>) new Gson().fromJson(expectedJson, Map.class);
		Map<String, Object> clientMap = (Map<String, Object>)new Gson().fromJson(responseJson, Map.class);
		//***
		
		JsonUtility.compareJson(holdingMap, clientMap, log, resultMap);
		//System.out.println(log.toString());
		//System.out.println(resultMap);
		
		if("String".equalsIgnoreCase(returnType)){
			t = (T) log.toString();
		}else if ("Map".equalsIgnoreCase(returnType)){
			t = (T) resultMap;
		}else{
			throw new Exception("Return type not supported");
		}
		return t;
				
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean validateJson(String json){
		ArrayList<String> SearchHeader = new ArrayList ();
		SearchHeader.add("ResponseHeader");
		SearchHeader.add("searchPaymentResponseHeader");
		SearchHeader.add("groupHierarchyResponseHeader");
        SearchHeader.add("entityProfileResponseHeader");//***For GB - 08/10/2018 ***//
		SearchHeader.add("TransactionHeader");
		
		JsonObject jsonObj = null;
		log.info("JSON request need to be parsed :" + json);
		if(json.startsWith("{"))
		{
			log.info("Json Object found");
		    jsonObj = new JsonParser().parse(json).getAsJsonObject();
		}
		else
		{
			if(json.startsWith("["))
			{
			  log.info("Json Array found");
			  JsonArray jsonArr = new JsonParser().parse(json).getAsJsonArray();
			  jsonObj = jsonArr.getAsJsonObject();
			}
			else
			{
				log.info("json can not parse as it start with " + json.charAt(0));
			}
		}
		
		String flagValue="";
		
		JsonElement jsonEle=null;
		for(String key : jsonObj.keySet()){
			jsonEle = jsonObj.get(key);
		}
		
		for (String headers : SearchHeader) {
			if (jsonEle.getAsJsonObject().keySet().contains(headers)) {
				flagValue = jsonEle.getAsJsonObject().get((headers)).getAsJsonObject().get("SuccessFlag").getAsJsonPrimitive().getAsString();
				break;
			}
		}
		
		
		
		return flagValue.equalsIgnoreCase("SUCCESS")?true:false;
	}

	
	
	
}

	

