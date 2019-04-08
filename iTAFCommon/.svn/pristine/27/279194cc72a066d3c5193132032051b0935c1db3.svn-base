package com.majesco.itaf.webservice;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import com.majesco.itaf.main.ITAFWebDriver;



public class WebServiceTest 
{
	private final static Logger log = Logger.getLogger(WebServiceTest.class.getName());
	public static Boolean ProcessStatusFlag_Nodeavailable = false;
	public static Boolean SuccessFlag_Nodeavailable = false;
	public static String reportNodeValue = null;
	public static String ProcessStatusFlag_TagValue = null;
	public static String SuccessFlag_TagValue = null;
	public static String temptagvalue = null;
	public static String xmlbusinessdate = null;
	
	private static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();


	public static void main(String[] args)
	{
		try
		{
			SimpleDateFormat cycledateformat = new SimpleDateFormat("MM/dd/yyyy");
			Date cycledate = cycledateformat.parse("06/30/2011");
			DateFormat xmlformat = new SimpleDateFormat("yyyy-MM-dd");
			xmlbusinessdate = xmlformat.format(cycledate);
			System.out.println(xmlbusinessdate);
			String responseXml = "D:\\WebServiceTest\\Response.xml";
			String Tag_Name = "*";
			String[] Node_Value = {"ProcessStatusFlag","SuccessFlag"};
			int index = 0;						
			ProcessStatusFlag_Nodeavailable = false;
			SuccessFlag_Nodeavailable = false;
			reportNodeValue = null;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(responseXml);
			doc.getDocumentElement().normalize();
			if(Tag_Name.equalsIgnoreCase("batchDetails"))
			{
				Node configuration = doc.getElementsByTagName(Tag_Name).item(index);
				NodeList list = configuration.getChildNodes();
				
				String NodeValue = "";  //need to be corrected
				for(int i = 0; i < list.getLength(); i++)
				{
					Node node = list.item(i);
					
					   if (NodeValue.equals(node.getNodeName())) 
					   {
						   SuccessFlag_TagValue = node.getTextContent();
						  // TagValue = node.setTextContent(arg0);
						   System.out.println("Value of SuccessFlag is :"+SuccessFlag_TagValue);
						   break;
					   }
				}
			}
			else

			{
				NodeList configuration = doc.getElementsByTagName(Tag_Name);				
				int nodevaluelength = Node_Value.length;
				System.out.println("nodevaluelength is:"+nodevaluelength);

					for(int i = 0; i < configuration.getLength(); i++)
					{
						Node node = configuration.item(i);
	
						   if (Node_Value[0].equals(node.getNodeName())) 
						   {
							   ProcessStatusFlag_Nodeavailable = true;							   
							   ProcessStatusFlag_TagValue = node.getTextContent();
							   System.out.println("Value of ProcessStatusFlag is :"+ProcessStatusFlag_TagValue);
							   if(ProcessStatusFlag_TagValue.equalsIgnoreCase("COMPLETED"))
							   {
								   //WebHelper.successFlag = "SUCCESS";
								   temptagvalue = "SUCCESS";
							   }
							   else
							   {
								   //WebHelper.successFlag = "FAILED";
								   temptagvalue = "FAILED";
								   break;
							   }							
						   }						   
						   if (Node_Value[1].equals(node.getNodeName()))
						   {
							   SuccessFlag_Nodeavailable = true;							   
							   SuccessFlag_TagValue = node.getTextContent();
							   System.out.println("Value of SuccessFlag is :"+SuccessFlag_TagValue);
							   if(SuccessFlag_TagValue.equalsIgnoreCase("SUCCESS"))
							   {
								   //WebHelper.successFlag = "SUCCESS";
								   temptagvalue = "    SUCCESS FLAG      ";
								   temptagvalue.replaceAll("^\\s+|\\s+$", "");
								   System.out.println(temptagvalue);
								   temptagvalue.trim();
								   System.out.println(temptagvalue);
/*								   Pattern trimmer = Pattern.compile("^\\s+|\\s+$");
								    //Matcher m = trimmer.matcher(temptagvalue);
								    StringBuffer out = new StringBuffer();
								    while(m.find())
								        m.appendReplacement(out, "");
								    m.appendTail(out);
								    System.out.println(out+"!");*/
							   }
							   else
							   {
								   //WebHelper.successFlag = "FAILED";
								   temptagvalue = "FAILED";
								   break;
							   }
						   }
					}
					System.out.println(temptagvalue);
					System.out.println("End of get xml response tag value");
			}
				
		}
		catch(SAXParseException sax)
		{
			log.error(sax.getMessage(), sax);
			log.fatal(sax.getCause());			
		}
		catch(FileNotFoundException we)
		{
			log.error(we.getMessage(), we);
			webDriver.getReport().setMessage(we.getMessage());
			webDriver.getReport().setStatus("FAIL");
		}
		catch(Exception e)
		{
			//throw new Exception("Error while Fetching XML Response Success Flag: " + e.getMessage());
			log.error(e.getMessage(), e);
			webDriver.getReport().setMessage(e.getMessage());
			webDriver.getReport().setStatus("FAIL");
		}
		//return TagValue;

	}

}
