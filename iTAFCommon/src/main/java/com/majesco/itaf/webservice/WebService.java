package com.majesco.itaf.webservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import com.majesco.itaf.main.Config;
import com.majesco.itaf.main.ITAFWebDriver;
import com.majesco.itaf.main.MainController;
import com.majesco.itaf.main.ObjectFactory;
import com.majesco.itaf.main.WebHelper;
import com.majesco.itaf.main.WebHelperPAS;
import com.majesco.itaf.main.WebHelperUtil;
import com.majesco.itaf.util.JDBCConnection;

public class WebService {

	private final static Logger log = Logger.getLogger(WebService.class.getName());
	private static String responseXml;
	private static HashMap<String, Object> responseHashMap = new HashMap<String, Object>();
	private static int temp = 0;
	private static String TagValue = null;
	private static HttpURLConnection con = null;

	private static Boolean ProcessStatusFlag_Nodeavailable = false;
	private static Boolean SuccessFlag_Nodeavailable = false;
	private static String ProcessStatusFlag_TagValue = null;
	private static String SuccessFlag_TagValue = null;
	private static String response_status = null;
	private static String FailedResponseTagValue = null;// Mandar

	private static boolean nodeAvailable = false;

	private static String reportNodeValue = null;
	private static boolean noResponseFile = false;
	private static String errorCodeAndMessage = null;// Mandar

	// CheckStatusUpdate--04/12/2017//
	private static String DisbursementDetailSeq = null;
	private static String PaymentId = null;
	private static String PaymentStatusDate = null;
	private static Date PaymentDate = null;
	private static DateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();
	private static MainController controller = ObjectFactory.getMainController();

	private static SSLContext SSL_CONTEXT = null;
	private static DummyHostnameVerifier HOSTNAME_VERIFIER = null;

	public static String getErrorCodeAndMessage() {
		return errorCodeAndMessage;
	}

	public static void setErrorCodeAndMessage(String errCodeAndMessage) {
		errorCodeAndMessage = errCodeAndMessage;
	}

	public static String getreportNodeValue() {
		return reportNodeValue;
	}

	public static boolean isNodeAvailable() {
		return nodeAvailable;
	}

	public static boolean isNoResponseFileTrue() {
		return noResponseFile;
	}

	// SSL related configuration
	static {
		SSLContext context = null;
		try {
			context = SSLContext.getInstance("TLSv1");
			// context.init(null, null,null};
			context.init(null, new TrustManager[] { new DummyTrustManager() }, new SecureRandom());
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			t.printStackTrace();
		}
		SSL_CONTEXT = context;
		HOSTNAME_VERIFIER = new DummyHostnameVerifier();
		// CHARSET = "UTF-8";
	}

	public static void setAuthorization(URLConnection conn, String username, char[] password) {
		String pwd = new String(password);
		if (!StringUtils.isEmpty(username) && (password != null && password.length > 0)) {
			String code = (username + ":" + pwd);
			String encoded = Base64.encodeBase64String(code.getBytes());
			conn.setRequestProperty("Authorization", "Basic " + encoded);
		}
	}

	public static URLConnection openConnection(String url, String username, char[] password) throws IOException {
		URL urlObject = new URL(url);
		URLConnection conn = urlObject.openConnection();
		setAuthorization(conn, username, password);
		conn.setUseCaches(false);
		conn.setDoOutput(true);
		if (conn instanceof HttpsURLConnection) {
			HttpsURLConnection secureConn = (HttpsURLConnection) conn;
			secureConn.setSSLSocketFactory(SSL_CONTEXT.getSocketFactory());
			secureConn.setHostnameVerifier(HOSTNAME_VERIFIER);
		}
		return conn;
	}

	public static void callWebService() throws Exception {
		OutputStreamWriter requestWriter = null;
		BufferedReader responseReader = null;
		Scanner scanner = null;

		try {

			URL wsdlUrl = new URL(WebHelper.wsdl_url);
			HttpURLConnection con = (HttpURLConnection) wsdlUrl.openConnection();

			/** Proxy settings ONLY if required **/
			System.setProperty("http.proxyHost", "192.168.100.40");
			System.setProperty("http.proxyPort", "8080");
			con.usingProxy();

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-type", "text/xml; charset=utf-8");
			con.setRequestProperty("SOAPAction", WebHelper.request_url);
			con.setDoOutput(true);
			con.setDoInput(true);

			/** Reading data from Request XML **/
			String reqXml = System.getProperty("user.dir") + "\\Resources\\Input\\WebService\\" + WebHelper.request_xml + ".xml";
			// String soapMessage = new Scanner(new
			// File(reqXml)).useDelimiter("\\A").next();
			scanner = new Scanner(new File(reqXml));
			String soapMessage = scanner.useDelimiter("\\A").next();

			/** Sending Request **/
			requestWriter = new OutputStreamWriter(con.getOutputStream());
			requestWriter.write(soapMessage);
			requestWriter.flush();

			/** Reading data from Response XML **/
			responseReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			StringBuffer stringBuffer = new StringBuffer();
			while ((line = responseReader.readLine()) != null) {
				// if (line.startsWith("<?xml ")) //DS:This condition is used
				// when response contains non-xml data also
				// {
				stringBuffer.append(line);
				stringBuffer.append("\n");
				// }
			}

			/** Writing Response to XML file **/
			String date = webDriver.getReport().getFromDate().replaceAll("[-/: ]", "");
			String fileName = WebHelper.testcaseID.toString() + "_" + WebHelperPAS.transactionType.toString() + "_" + date;
			responseXml = System.getProperty("user.dir") + "\\Resources\\Results\\XMLOutput\\" + fileName + ".xml";

			File file = new File(responseXml);
			String content = stringBuffer.toString();
			FileOutputStream fop = new FileOutputStream(file);
			if (!file.exists())
				file.createNewFile();
			byte[] contentInBytes = content.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new Exception("Error while triggering web service: " + e.getMessage());
		} finally {
			/** Closing input and output stream buffers **/
			requestWriter.close();
			responseReader.close();
			scanner.close();
		}
	}

	// CheckStatusUpdate--04/12/2017//
	public static String callWebService(String wscycledate, String wsdl, String requestXML, String user, String password) throws IOException,
			InterruptedException, Exception
	{
		OutputStreamWriter requestWriter = null;
		BufferedReader responseReader = null;
		Scanner scanner = null;
		// String errorCodeAndMessage =null;//Mandar

		StringBuffer stringBuffer = new StringBuffer();
		URL wsdlUrl = null;

		// try{
		// log.info("Inside webservice" +
		// "Transaction Name: "+MainController.controllerTransactionType+" Cycle Date : "+MainController.cycleDateCellValue);
		if (!wscycledate.equalsIgnoreCase("")) {
			wsdlUrl = new URL(WebHelper.wsdl_url);
		} else {
			wsdlUrl = new URL(wsdl);
		}

		log.info("wsdlUrl is : " + wsdlUrl);
		boolean is_https = wsdlUrl.toString().toLowerCase().contains("https");

		URLConnection newCon = null;

		if (is_https) {
			newCon = openConnection(wsdl, user, password.toCharArray());
			con = (HttpsURLConnection) newCon;
			// cons.getSSLSocketFactory().createSocket().
		} else {
			con = (HttpURLConnection) wsdlUrl.openConnection();
			String authString = user + ":" + password;
			System.out.println("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
			System.out.println("Base64 encoded auth string: " + authStringEnc);
			con.setRequestProperty("Authorization", "Basic " + authStringEnc);
		}
		log.info("Connection opened");

		setConProperties(con);
		String reqXml = null;

		// bhaskar changebusinessdate transaction through webservice START
		if (!wscycledate.equalsIgnoreCase("")) {
			wscycledate = wscycledate.replace("/", "_");// Devishree
			log.info("cycledate in path corrected to : " + wscycledate);
			reqXml = System.getProperty("user.dir") + "\\" + Config.projectName + "\\Resources\\Input\\WebService\\WebserviceFiles\\" + wscycledate
					+ "\\" + WebHelper.request_xml + ".xml";
		} else {
			reqXml = requestXML;
		}

		/** Reading data from Request XML **/
		// String reqXml = System.getProperty("user.dir") +
		// "\\Resources\\Input\\WebService\\" + WebHelper.request_xml + ".xml";
		log.info("reqXml path is : " + reqXml);
		// String soapMessage = new Scanner(new
		// File(reqXml)).useDelimiter("\\A").next();
		// scanner = new Scanner(new File(reqXml));
		// log.info("scanner object created");
		String soapMessage = new String(Files.readAllBytes(Paths.get(reqXml)));
		log.info("soapMessage is : " + soapMessage);

		/** writing data to Request **/
		requestWriter = new OutputStreamWriter(con.getOutputStream());
		requestWriter.write(soapMessage);
		requestWriter.flush();

		/** Reading data from Response XML **/
		log.info("responseReader object is created");
		String Firstline;
		try {
			if (con.getResponseCode() == 200) {
				responseReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				log.info("Responce code is OK : " + con.getResponseMessage());
				Firstline = responseReader.readLine();
				log.info("stringBuffer object is created");
				stringBuffer.append(Firstline);
				stringBuffer.append("\n");
			} else {
				errorCodeAndMessage = con.getResponseCode() + " :  " + con.getResponseMessage();
				log.info(con.getResponseCode() + " :  " + con.getResponseMessage());

				// Meghna--START--For Interface Validations-- Response code 500
				// and faultstring response//
				responseReader = new BufferedReader(new InputStreamReader(con.getErrorStream()));

				// Firstline = responseReader.readLine();
				Firstline = "";

				while (responseReader.ready()) {

					Firstline = Firstline + responseReader.readLine();
					log.info(Firstline);

				}

				log.info(Firstline);

				stringBuffer.append(Firstline);
				stringBuffer.append("\n");
				log.info(stringBuffer.toString());
			}
		} catch (IOException ioe) {
			webDriver.getReport().setMessage(ioe.getMessage());
			log.error(
					"Failed read responce from Socket with response code " + con.getResponseCode() + " <-|-> LocalizeMessage "
							+ ioe.getLocalizedMessage() + " <-|-> Message " + ioe.getMessage() + " <-|-> Cause " + ioe.getCause(), ioe);
			throw new IOException("Failed read responce from Socket with response code " + con.getResponseCode() + "  <-|-> LocalizeMessage "
					+ ioe.getLocalizedMessage() + " <-|-> Message" + ioe.getMessage() + " <-|-> Cause " + ioe.getCause());
		} catch (Exception e) {
			// throw new Exception("Error while triggering web service: " +
			// e.getMessage());
			webDriver.getReport().setMessage(e.getMessage());
			log.error(
					"Failed while reading responce from Socket with response code " + con.getResponseCode() + "  <-|-> LocalizeMessage "
							+ e.getLocalizedMessage() + " <-|-> Message " + e.getMessage() + " <-|-> Cause " + e.getCause(), e);
			throw new Exception("Failed while reading responce from Socket with response code " + con.getResponseCode() + "   <-|-> LocalizeMessage "
					+ e.getLocalizedMessage() + " <-|-> Message" + e.getMessage() + " <-|-> Cause " + e.getCause());
		} finally {
			responseReader.close();
			requestWriter.close();
			// scanner.close();
		}

		// bhaskar changebusinessdate transaction through webservice START
		if (!wscycledate.equalsIgnoreCase("")) {
			responseXml = System.getProperty("user.dir") + "\\" + Config.projectName + "\\Resources\\Input\\WebService\\WebserviceFiles\\"
					+ wscycledate + "\\" + WebHelper.request_xml + "_Response" + ".xml";// Devishree
		} else {
			responseXml = System.getProperty("user.dir") + "\\" + Config.projectName
					+ "\\Resources\\Results\\ChangeBusinessdate_Log\\ChangeBusinessDate_" + controller.xmlbusinessdate + "_Response.xml";
		}
		// bhaskar changebusinessdate transaction through webservice END
		log.info("Inside webservice response");
		log.info("responseXml path is : " + responseXml);

		try {
			File file = new File(responseXml);
			log.info("file object created");
			String content = stringBuffer.toString();
			log.info("content object created");
			FileOutputStream fop = new FileOutputStream(file);
			log.info("FileOutputStream object created");
			if (!file.exists()) {
				file.createNewFile();
				log.info("New response file created");
			}
			byte[] contentInBytes = content.getBytes();
			log.info("contentInBytes done");
			fop.write(contentInBytes);
			log.info("contentInBytes written");
			fop.flush();
			log.info("flushed");
			fop.close();
			log.info("closed");
			// return responseXml;
		} catch (IOException ioe) {
			webDriver.getReport().setMessage(ioe.getMessage() + errorCodeAndMessage);
			log.error("Failed to write  responce  in file " + responseXml + " <-|-> LocalizeMessage " + ioe.getLocalizedMessage() + " <-|-> Message "
					+ ioe.getMessage() + " <-|-> Cause " + ioe.getCause(), ioe);
			throw new IOException("Failed to write responce  in file " + responseXml + "  <-|-> LocalizeMessage " + ioe.getLocalizedMessage()
					+ " <-|-> Message" + ioe.getMessage() + " <-|-> Cause " + ioe.getCause());
		} catch (Exception e) {
			webDriver.getReport().setMessage(e.getMessage() + errorCodeAndMessage);
			log.error("Failed while writting  responce  in file " + responseXml + " <-|-> LocalizeMessage " + e.getLocalizedMessage()
					+ " <-|-> Message " + e.getMessage() + " <-|-> Cause " + e.getCause(), e);
			throw new Exception("Failed while writting  responce  in file " + responseXml + "   <-|-> LocalizeMessage " + e.getLocalizedMessage()
					+ " <-|-> Message" + e.getMessage() + " <-|-> Cause " + e.getCause());
		}

		return responseXml;
	}

	/**
	 * @throws ProtocolException
	 */
	private static void setConProperties(HttpURLConnection con) throws ProtocolException {

		con.setRequestMethod("POST");
		log.info("setRequestMethod done");
		con.setRequestProperty("Content-type", "text/xml; charset=UTF-8");
		log.info("setRequestProperty done");
		con.setRequestProperty("Connection", "keep-alive");
		log.info("setRequestProperty done");
		// con.setRequestProperty("SOAPAction","");
		// log.info("setRequestProperty done");
		con.setDoOutput(true);
		log.info("setDoOutput done");
		con.setDoInput(true);
		log.info("setDoInput done");
		// con.setInstanceFollowRedirects(true);
		// con.setUseCaches(true);
		// con.setReadTimeout(1800 * 1000);

	}

	public static String getXMLTagValue(String xmlTagName) throws IOException, Exception {
		String tagValue = null;
		File fXmlFile = new File(responseXml);

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			// First matching node
			Node firstNode = doc.getElementsByTagName(xmlTagName).item(0);
			tagValue = firstNode.getTextContent().toString();
			return tagValue;
		} catch (IOException ioe) {
			webDriver.getReport().setMessage(ioe.getMessage());
			log.error(
					"Failed to get tag value " + xmlTagName + " <-|-> LocalizeMessage " + ioe.getLocalizedMessage() + " <-|-> Message "
							+ ioe.getMessage() + " <-|-> Cause " + ioe.getCause(), ioe);
			throw new IOException("Failed to get tag value " + xmlTagName + "  <-|-> LocalizeMessage " + ioe.getLocalizedMessage() + " <-|-> Message"
					+ ioe.getMessage() + " <-|-> Cause " + ioe.getCause());
		} catch (Exception e) {
			// throw new Exception("Error while XML tag verification: " +
			// e.getMessage());
			webDriver.getReport().setMessage(e.getMessage());
			log.error("Failed create Chrome DriverInstance <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message " + e.getMessage()
					+ " <-|-> Cause " + e.getCause(), e);
			throw new Exception("Failed to read process list  <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message" + e.getMessage()
					+ " <-|-> Cause " + e.getCause());
		}
	}

	// bhaskar Web Service Jbeam START
	@SuppressWarnings("resource")
	public static String callWebService_JBeam(String sWSDL_URL, String sRequestXML, String sContent_Type) throws IOException, Exception {

		OutputStreamWriter requestWriter = null;
		BufferedReader responseReader = null;
		String line = null;
		StringBuffer stringBuffer = new StringBuffer();

		// *** Generic Change : To handle JBEAM USER ID/PASS through
		// mainController***
		String name = Config.jbeamusername;
		String password = Config.jbeampassword;
		String jbeam_Version = Config.jbeamVersion;

		// code for authentication
		String authString = name + ":" + password;
		System.out.println("auth string: " + authString);
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		System.out.println("Base64 encoded auth string: " + authStringEnc);

		// creating connection
		URL wsdlUrl = new URL(sWSDL_URL.toString());
		URLConnection connection = wsdlUrl.openConnection();
		HttpURLConnection con = (HttpURLConnection) connection;
		// authenticate the request
		con.setRequestProperty("Authorization", "Basic " + authStringEnc);

		setConProperties(con);

		/** Reading data from Request XML **/
		log.info("request XML to trigger WebService is :" + sRequestXML);
		String soapMessage = new String(Files.readAllBytes(Paths.get(sRequestXML)));
		log.info("soap request message is :" + soapMessage);

		/** writing data to Request **/
		requestWriter = new OutputStreamWriter(con.getOutputStream());
		requestWriter.write(soapMessage);
		requestWriter.flush();

		// con.connect();
		/** Reading data from Response XML **/
		try {
			if (con.getResponseCode() == 200) {
				responseReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

				while ((line = responseReader.readLine()) != null) {
					stringBuffer.append(line);
					stringBuffer.append("\n");
				}
			} else {
				errorCodeAndMessage = con.getResponseCode() + " :  " + con.getResponseMessage();
				log.info(con.getResponseCode() + " :  " + con.getResponseMessage());

				responseReader = new BufferedReader(new InputStreamReader(con.getErrorStream()));

				// Firstline = responseReader.readLine();
				String Firstline = "";

				while (responseReader.ready()) {

					Firstline = Firstline + responseReader.readLine();
				}

				log.info(Firstline);
			}

		} catch (IOException ioe) {
			webDriver.getReport().setMessage(ioe.getMessage());
			log.error(
					"Failed read responce from Socket with response code " + con.getResponseCode() + " :  " + con.getResponseMessage()
							+ " <-|-> LocalizeMessage " + ioe.getLocalizedMessage() + " <-|-> Message " + ioe.getMessage() + " <-|-> Cause "
							+ ioe.getCause(), ioe);
			throw new IOException("Failed read responce from Socket with response code " + con.getResponseCode() + " :  " + con.getResponseMessage()
					+ "  <-|-> LocalizeMessage " + ioe.getLocalizedMessage() + " <-|-> Message" + ioe.getMessage() + " <-|-> Cause " + ioe.getCause());
		} catch (Exception e) {
			// throw new Exception("Error while triggering web service: " +
			// e.getMessage());
			webDriver.getReport().setMessage(e.getMessage());
			log.error("Failed while reading responce from Socket with response code " + con.getResponseCode() + " :  " + con.getResponseMessage()
					+ "  <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message " + e.getMessage() + " <-|-> Cause " + e.getCause(), e);
			throw new Exception("Failed while reading responce from Socket with response code " + con.getResponseCode() + " :  "
					+ con.getResponseMessage() + "   <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message" + e.getMessage()
					+ " <-|-> Cause " + e.getCause());
		} finally {
			responseReader.close();
			requestWriter.close();
		}

		/** Printing Response **/
		// log.info(stringBuffer.toString());

		/** Writing Response to XML file **/
		String date = webDriver.getReport().getFromDate().replaceAll("[-/: ]", "");
		String fileName = WebHelper.testcaseID.toString() + "_" + WebHelper.transactionType.toString() + "_" + date;
		// responseXml = System.getProperty("user.dir") +
		// "\\RegressionTesting_MASTER\\Resources\\Results\\XMLOutput\\" +
		// fileName + ".xml"; //Meghna
		responseXml = (Config.resultFilePath) + "\\XMLOutput\\" + fileName + ".xml";// Meghna

		File file = new File(responseXml);
		String content = stringBuffer.toString();
		FileOutputStream fop = new FileOutputStream(file);
		if (!file.exists())
			file.createNewFile();
		byte[] contentInBytes = content.getBytes();
		fop.write(contentInBytes);
		fop.flush();
		fop.close();
		return responseXml;
		
	}

	// bhaskar Web Service Jbeam END

	// bhaskar save response data START
	public static void getXMLResponseData(String ctrlValue1, String ctrlValue2, String testCase, String wscycledate, String responseXml)
			throws Exception {
		// File ResponseFile = new File(responseXml);
		// File ResponseFile = new
		// File("D:\\\\Selenium_Workspace_New\\iTAFSeleniumWeb\\HTML5_POC\\Resources\\Results\\XMLOutput\\POC_TC1_NB_Web_Service_20160617180753.xml");

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document doc = dBuilder.parse(responseXml);
			doc.getDocumentElement().normalize();

			String TagName = null;
			String ctrlValue1Part = ctrlValue1;
			if (ctrlValue1Part.contains(":")) {
				String[] TagNameentries = ctrlValue1.split("\\:");
				TagName = TagNameentries[0];
				ctrlValue1 = TagNameentries[1];
			} else {
				controller.pauseFun("TagName Missing in PathToNode Column");
				return;
			}

			if (ctrlValue2.contains("|") && ctrlValue1.contains("|")) {
				String[] columnentries = ctrlValue2.split("\\|");
				String[] Pathentries = ctrlValue1.split("\\|");
				log.info("column entries length:" + columnentries.length);
				log.info("Xpath entries length:" + Pathentries.length);
				if (columnentries.length == Pathentries.length) {
					for (int i = 0; i < columnentries.length; i++) {
						responseHashMap.put(columnentries[i], Pathentries[i]);
						String NodeValue = responseHashMap.get(columnentries[i]).toString();
						// String colHeader = columnentries[i].toString();
						log.info("tempColumnValue :" + NodeValue);
						readXMLResponseData(TagName, NodeValue, columnentries, doc, testCase, wscycledate);
					}
				} else {
					controller.pauseFun("PathToNode Entries not matching with ColumnNames");
					return;
				}
			}
		} catch (IOException ioe) {
			webDriver.getReport().setMessage(ioe.getMessage());
			log.error("IOException in getXMLResponseData  <-|-> LocalizeMessage " + ioe.getLocalizedMessage() + " <-|-> Message " + ioe.getMessage()
					+ " <-|-> Cause " + ioe.getCause(), ioe);
			throw new Exception("IOException in getXMLResponseData  <-|-> LocalizeMessage " + ioe.getLocalizedMessage() + " <-|-> Message"
					+ ioe.getMessage() + " <-|-> Cause " + ioe.getCause());
		} catch (Exception e) {
			// throw new Exception("Error while saving XML Response Data: " +
			// e.getMessage());
			webDriver.getReport().setMessage(e.getMessage());
			log.error("IOException in getXMLResponseData <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message " + e.getMessage()
					+ " <-|-> Cause " + e.getCause(), e);
			throw new Exception("IOException in getXMLResponseData  <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message"
					+ e.getMessage() + " <-|-> Cause " + e.getCause());
		}
	}

	@SuppressWarnings("resource")
	public static void readXMLResponseData(String TagName, String NodeValue, String[] columnentries, Document doc, String testCase, String wscycledate)
			throws Exception {
		Node configuration = doc.getElementsByTagName(TagName).item(0);
		NodeList list = configuration.getChildNodes();
		log.info("list.getLength() " + list.getLength());

		for (int i = 0; i < list.getLength(); i++) {

			Node node = list.item(i);

			if (NodeValue.equals(node.getNodeName())) {
				String xmlnodevalue = node.getTextContent();
				log.info(xmlnodevalue);

				// bhaskar save response single data in UniqueNumber sheet START
				try {
					FileInputStream in = new FileInputStream(Config.transactionInfo);
					// XX
					// HSSFWorkbook uniqueWB = new HSSFWorkbook(in);
					XSSFWorkbook uniqueWB = new XSSFWorkbook(in);
					XSSFSheet uniqueNumberSheet = uniqueWB.getSheet("DataSheet");
					// HSSFSheet uniqueNumberSheet =
					// uniqueWB.getSheet("DataSheet");
					//
					HashMap<String, Integer> uniqueValuesHashMap = WebHelperUtil.getValueFromHashMap(uniqueNumberSheet);
					// HSSFRow uniqueRow = null;
					XSSFRow uniqueRow = null;
					XSSFRow uniqueRow1 = null;
					// HSSFRow uniqueRow1 = null;

					int rowNum = uniqueNumberSheet.getPhysicalNumberOfRows();
					log.info("%%%%%%%%*********" + rowNum);

					for (int j = 0; j < columnentries.length; j++) {
						uniqueRow1 = uniqueNumberSheet.getRow(0);
						// HSSFCell uniqueCell1 = uniqueRow1.createCell(j+1);
						XSSFCell uniqueCell1 = uniqueRow1.createCell(j + 1);
						uniqueCell1.setCellValue(columnentries[j]);
					}

					for (int rIndex = 0; rIndex <= rowNum; rIndex++) {
						uniqueRow = uniqueNumberSheet.getRow(rIndex);
						String uniqueTestcaseID = WebHelperUtil.getCellData("TestCaseID", uniqueNumberSheet, rIndex, uniqueValuesHashMap);
						log.info("uniqueTestcaseID" + uniqueTestcaseID);
						if (testCase.equals(uniqueTestcaseID))// &&
																// MainController.controllerTransactionType.toString().equals(uniqueTransactionType)
						{
							uniqueRow = uniqueNumberSheet.getRow(rIndex);
							break;
						} else if (rIndex == rowNum - 1) {
							uniqueRow = uniqueNumberSheet.createRow(rowNum);
						}
					}

					if (temp < columnentries.length) {
						// HSSFCell uniqueCell = uniqueRow.createCell(temp+1);
						XSSFCell uniqueCell = uniqueRow.createCell(temp + 1);
						uniqueCell.setCellValue(xmlnodevalue);
						temp++;
					}

					in.close();
					FileOutputStream out = new FileOutputStream(Config.transactionInfo);
					uniqueWB.write(out);
				} catch (IOException we) {
					log.error(we.getMessage(), we);
					webDriver.getReport().setMessage(we.getMessage());
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					webDriver.getReport().setMessage(e.getMessage());
				}
				// bhaskar save response single data in UniqueNumber sheet END
				break;

			}

		}

	}

	// bhaskar save response data END

	// bhaskar WebService recovery sceanrio START

	public static void setXMLResponseTagValue(String responseXml, String Tag_Name, String Node_Value, int index) throws Exception {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(responseXml);
			doc.getDocumentElement().normalize();
			if (Tag_Name.equalsIgnoreCase("RequestHeader")) {
				Node configuration = doc.getElementsByTagName(Tag_Name).item(index);
				NodeList list = configuration.getChildNodes();

				String NodeValue = Node_Value;
				for (int i = 0; i < list.getLength(); i++) {
					Node node = list.item(i);

					if (NodeValue.equals(node.getNodeName())) {
						String randomno = null;

						DateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss"); // yyyy-MM-dd
																									// HH:mm:ss
						Date dateobj = new Date();
						randomno = simpleDateFormat.format(dateobj);
						randomno = randomno.replace(" ", "");
						randomno = randomno.replace(":", "");
						randomno = randomno.replace("_", "");
						randomno = randomno.replace("/", "");
						randomno = randomno.replace("-", "");
						// randomno = randomno.replace("\", "");
						// randomno = randomno.replace("\", "");
						randomno = "1" + randomno;
						log.info(randomno);

						node.setTextContent(randomno);
						break;
					}
				}

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(responseXml));
				transformer.transform(source, result);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// throw new
			// Exception("Error while Fetching XML Response Success Flag: " +
			// e.getMessage());
			webDriver.getReport().setMessage(e.getMessage());
		}
	}

	// Added for Check Status interface-- start--04/12/2017
	public static void setXMLAPTagValue(String responseXml, String Tag_Name, String Node_Value1, String Node_Value2, String Node_Value3,
			String ctrlValue, int index) throws Exception

	{
		String Policy_No = ctrlValue;
		ResultSet rs = null;
		Connection conn = null;
		Statement st = null;
		
		conn = JDBCConnection.establishHTML5BillingDBConn();
		st = conn.createStatement();
		if (Config.databaseType.equalsIgnoreCase("MsSQL"))
		{
			if ("GroupBilling".equalsIgnoreCase(Config.productTeam)) {
				
				rs = st.executeQuery("select * from DISBURSEMENT_INTERFACE where DISBURSEMENT_SEQ IN"
						+ "(select DISBURSEMENT_SEQ from DISBURSEMENT_SUMMARY where PAYEE_INDICATOR IN"
						+ "(select PAYEE_INDICATOR from WORK_QUEUE_SUMMARY where PAYEE_CODE IN"
						+ "(select SYSTEM_ENTITY_CODE from ENTITY_REGISTER Where SOURCE_SYSTEM_ENTITY_CODE ='" + Policy_No
						+ "')))order by disbursement_seq");

				if (!rs.isBeforeFirst()) {

					rs = st.executeQuery("select * from DISBURSEMENT_INTERFACE where DISBURSEMENT_SEQ IN"
							+ "(select DISBURSEMENT_SEQ from DISBURSEMENT_SUMMARY where PAYEE_INDICATOR IN"
							+ "(select PAYEE_INDICATOR from WORK_QUEUE_SUMMARY where MEMBER_SYSTEM_CODE IN"
							+ "(select SYSTEM_ENTITY_CODE from ENTITY_REGISTER Where member_id ='" + Policy_No + "')))order by disbursement_seq");
				}
				if (!rs.isBeforeFirst()) {

					rs = st.executeQuery("select * from disbursement_detail where DISBURSEMENT_SEQ IN"
							+ "(select DISBURSEMENT_SEQ from DISBURSEMENT_SUMMARY where PAYEE_INDICATOR IN"
							+ "(select PAYEE_INDICATOR from WORK_QUEUE_SUMMARY where PAYEE_CODE IN"
							+ "(select SYSTEM_ENTITY_CODE from ENTITY_REGISTER Where SOURCE_SYSTEM_ENTITY_CODE ='" + Policy_No
							+ "')))order by disbursement_seq");
				}
			} else {
				rs = st.executeQuery("select * from disbursement_interface where policy_no='" + Policy_No + "' or account_no='" + Policy_No
						+ "'  or broker_no='" + Policy_No + "' order by disbursement_seq");// 'POL2122082017115856'
			}

		} else if (Config.databaseType.equalsIgnoreCase("Oracle"))

		{
			rs = st.executeQuery("select * from disbursement_interface where policy_no='" + Policy_No + "' or account_no='" + Policy_No
					+ "' or broker_no='" + Policy_No + "' order by disbursement_seq");

			if (!rs.isBeforeFirst()) {
				rs = st.executeQuery("select * from disbursement_history where policy_no='" + Policy_No + "' or account_no='" + Policy_No
						+ "' or broker_no='" + Policy_No + "' order by disbursement_seq");
			}

		} else {
			log.error("Databse is not selected ");

		}
		while (rs.next())

		{
			// PaymentDate=rs.getString("DISBURSEMENT_SEQ")
			// For GB - 24/07/2018
			// *** For GB - 08/02/2018 - Disbursement_Seq is used for
			// GroupBilling for Base its DisbursementDetailSeq***//
			if ("GroupBilling".equalsIgnoreCase(Config.productTeam)) {
				DisbursementDetailSeq = rs.getString("DISBURSEMENT_SEQ");
				PaymentId = rs.getString("PAYMENT_SEQ");
			} else {
				DisbursementDetailSeq = rs.getString(1).toString();
			}

			// PaymentId=rs.getString("PAYMENT_SEQ");
			PaymentDate = rs.getDate("DISBURSEMENT_DATE");
			PaymentStatusDate = dtFormat.format(PaymentDate);

			if (WebHelper.transactionType.toString().equalsIgnoreCase("FlatFile")) {
				PaymentStatusDate = PaymentStatusDate.replaceAll("-", "");
			}

		}
		rs.close();
		st.close();
		JDBCConnection.closeConnection(conn);
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(responseXml);
			doc.getDocumentElement().normalize();

			if (Tag_Name.equalsIgnoreCase("CheckStatusUpdate")) {

				Node configuration = doc.getElementsByTagName(Tag_Name).item(index);
				NodeList list = configuration.getChildNodes();

				String NodeValue1 = Node_Value1;
				// String NodeValue2 = Node_Value2;
				String NodeValue3 = Node_Value3;
				for (int i = 0; i < list.getLength(); i++) {

					Node node = list.item(i);

					if (NodeValue1.equals(node.getNodeName())) {
						log.info(DisbursementDetailSeq);
						node.setTextContent(DisbursementDetailSeq);

					}
					/*
					 * if (NodeValue2.equals(node.getNodeName()))
					 * 
					 * { log.info(PaymentId); node.setTextContent(PaymentId);
					 * 
					 * 
					 * 
					 * }
					 */
					if (NodeValue3.equals(node.getNodeName()))

					{
						log.info(PaymentStatusDate);
						node.setTextContent(PaymentStatusDate);

					}
				}

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(responseXml));
				transformer.transform(source, result);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			webDriver.getReport().setMessage(e.getMessage());
		}
	}

	// Added for Check Status interface--end--04/12/2017

	// Added for EFT Transaction// -----Meghna
	public static void setReadValueXML(String responseXml, String Tag_Name, String Node_Value, int index, String valueToBewritten) throws Exception {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(responseXml);
			doc.getDocumentElement().normalize();

			// if(Tag_Name.equalsIgnoreCase("RequestHeader"))
			// {
			Node configuration = doc.getElementsByTagName(Tag_Name).item(index);
			NodeList list = configuration.getChildNodes();

			String NodeValue = Node_Value;
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);

				if (NodeValue.equals(node.getNodeName())) {
					log.info(valueToBewritten);

					node.setTextContent(valueToBewritten);
					break;
				}

				/*
				 * else { uniqueNumber = ReadFromExcel(ctrlValue);
				 * 
				 * }
				 */
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(responseXml));
			transformer.transform(source, result);
			// }

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// throw new
			// Exception("Error while Fetching XML Response Success Flag: " +
			// e.getMessage());
			webDriver.getReport().setMessage(e.getMessage());
		}

	}

	// Added for EFT Transaction// -----Meghna
	public static void RunDBQueries(String QTerminationmessage, String QPolicyNumber, String QAgentNumber, String QAccountNumber) throws Exception {
		try {
			Connection conn = JDBCConnection.establishHTML5BillingDBConn();
			Statement st = conn.createStatement();

			if (!QPolicyNumber.equals("")) {
				st.execute("UPDATE job_schedule SET job_status = '" + QTerminationmessage + "' WHERE job_status = 'SCHEDULED' AND policy_no = '"
						+ QPolicyNumber + "'");
			}

			if (!QAccountNumber.equals("")) {
				st.execute("UPDATE job_schedule SET job_status = '"
						+ QTerminationmessage
						+ "' WHERE job_status <> 'COMPLETED' AND account_system_code IN (select system_entity_code from entity_register where entity_type= 'ACCOUNT' and source_system_entity_code = '"
						+ QAccountNumber + "')");
			}

			if (!QAgentNumber.equals("")) {
				st.execute("UPDATE job_schedule SET job_status = '"
						+ QTerminationmessage
						+ "' WHERE job_status = 'SCHEDULED' AND broker_system_code IN (SELECT system_entity_code FROM entity_register WHERE source_system_entity_code = '"
						+ QAgentNumber + "')");
			}
			if(Config.databaseType.equalsIgnoreCase("ORACLE"))
			{
			    st.execute("commit");
			}
			st.close();
			JDBCConnection.closeConnection(conn);
		}

		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new Exception("Error in RunDBQueries : " + e.getMessage());
		}
	}

	// bhaskar WebService Response status START 15-Nov-2016
	public static String getXMLResponseStatus(String responseXml, String Tag_Name, String[] Node_Value, int index) throws Exception {
		try {
			// Nodeavailable = false;
			String unMatcheNodeValue = ""; // Mandar
			noResponseFile = false;
			ProcessStatusFlag_Nodeavailable = false;
			SuccessFlag_Nodeavailable = false;
			reportNodeValue = null;
			response_status = null;
			Node node = null; // Mandar
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(responseXml);
			doc.getDocumentElement().normalize();
			NodeList configuration = doc.getElementsByTagName(Tag_Name);
			// Mandar
			if (Node_Value[0] == "ProcessStatusFlag" && Node_Value[1] == "SuccessFlag") {
				for (int i = 0; i < configuration.getLength(); i++) {
					node = configuration.item(i);

					if (Node_Value[0].equals(node.getNodeName())) {
						ProcessStatusFlag_Nodeavailable = true;
						ProcessStatusFlag_TagValue = node.getTextContent();
						log.info("Value of ProcessStatusFlag is :" + ProcessStatusFlag_TagValue);
						// if(ProcessStatusFlag_TagValue.equalsIgnoreCase("COMPLETED"))
						if (ProcessStatusFlag_TagValue.contains("COMPLETE") || ProcessStatusFlag_TagValue.contains("SUCCESS"))
							
						{
							response_status = "SUCCESS";
						} else {
							response_status = "FAILED";

							// Mandar --- To handle failures -reporting at this
							// stage itself -- for validations 17/11/2017 ****

							String Node_Value3 = "Description";
							FailedResponseTagValue = getXMLResponseTagValue(responseXml, Tag_Name, Node_Value3, index);
							webDriver.getReport().setMessage("REQUEST FAILED : Error Msg displayed -- " + FailedResponseTagValue);
							webDriver.getReport().setStatus("FAIL");
							log.info(("REQUEST FAILED : Value of SuccessFlag is --" + ProcessStatusFlag_TagValue + "-- ERROR MSG DISPLAYED --" + FailedResponseTagValue));
							response_status = "FAILED";
							Node_Value3 = null;

							// *******
							break;
						}
					}
					if (Node_Value[1].equals(node.getNodeName())) {
						SuccessFlag_Nodeavailable = true;
						SuccessFlag_TagValue = node.getTextContent();
						log.info("Value of SuccessFlag is :" + SuccessFlag_TagValue);
						if (SuccessFlag_TagValue.equalsIgnoreCase("SUCCESS")) {
							response_status = "SUCCESS";
						} else {
							response_status = "FAILED";
							// Mandar --- To handle failures -reporting at this
							// stage itself -- for validations 17/11/2014 ****

							String Node_Value3 = "Description";
							FailedResponseTagValue = getXMLResponseTagValue(responseXml, Tag_Name, Node_Value3, index);
							webDriver.getReport().setMessage("REQUEST FAILED : Error Msg displayed -- " + FailedResponseTagValue);
							webDriver.getReport().setStatus("FAIL");
							log.info(("REQUEST FAILED : Value of SuccessFlag is --" + SuccessFlag_TagValue + "-- ERROR MSG DISPLAYED --" + FailedResponseTagValue));
							response_status = "FAILED";
							Node_Value3 = null;

							// *******
							break;
						}
					}
				}
			} else {
				// boolean response_flag = true;//Mandar - 17/11/2017

				// *** below given change is made to handle various validation
				// conditions *** -- Mandar 17/11/2017
				boolean response_flag = false;// Nim -Mandar

				int icountResp = doc.getElementsByTagName(Node_Value[0]).getLength();

				int iCountWebServiceXls = Node_Value[1].split("\\|").length;

				if (iCountWebServiceXls > icountResp) {
					String[] XlsError = Node_Value[1].split("\\|");
					NodeList nodeList = doc.getElementsByTagName(Node_Value[0]);
					ArrayList<String> list = new ArrayList<String>();
					for (int i = 0; i < nodeList.getLength(); i++) {
						node = nodeList.item(i);
						// if (Node_Value[0].equals(node.getNodeName()))
						// {
						list.add(node.getTextContent());
					}

					// *** For writing Unmatched Entries ***
					for (int i = 0; i < XlsError.length; i++) {
						// Node node = configuration.item(i);
						String sError = XlsError[i];
						if (!list.contains(sError)) {
							unMatcheNodeValue = unMatcheNodeValue + sError;
						}
					}

				} else {// ****
						// Mandar
					for (int i = 0; i < configuration.getLength(); i++) {
						// Node node = configuration.item(i);
						node = configuration.item(i);

						if (Node_Value[0].equals(node.getNodeName())) {
							ProcessStatusFlag_Nodeavailable = true;
							ProcessStatusFlag_TagValue = node.getTextContent();
							log.info("Value of ProcessStatusFlag is :" + ProcessStatusFlag_TagValue);
							// Mandar
							if (((Node_Value[1]).toString()).contains(ProcessStatusFlag_TagValue)) {

								// response_flag = response_flag & true;//Mandar
								// -- 17/11/2017

								// *** Mandar
								if (unMatcheNodeValue == "")
									response_flag = true;
								// ***
							}
							
							else {
								// response_flag = response_flag &
								// false;//Mandar -- 17/11/2017
								response_flag = false;// mandar
								unMatcheNodeValue = unMatcheNodeValue + ProcessStatusFlag_TagValue.toString();
							}
							// Mandar

						}
					}// Mandar
				}
				if (response_flag == true) {
					response_status = "SUCCESS";
				} else {
				
					// *********Mandar -- 17/11/2017 -- To handle summary result
					// file ***
					webDriver.getReport().setMessage(
							" FAILED : Expected Error Msgs : " + " ' " + Node_Value[1] + " ' " + " Not Matching/Missing/Additional in Actual Msgs : "
									+ " ' " + unMatcheNodeValue + " ' ");// Mandar
					log.info(("Node are not matching :  Expected Msg -- " + Node_Value[1] + "Actual Msg --" + unMatcheNodeValue));
					webDriver.getReport().setStatus("FAIL");
					response_status = "FAILED";
					// ***

				}

			}

		} catch (SAXParseException sax) {
			log.fatal(sax.getCause(), sax);
			webDriver.getReport().setMessage("BLANK WEBSERVICE RESPONSE");
			webDriver.getReport().setStatus("FAIL");
			noResponseFile = true;
			response_status = "BLANK";
		} catch (FileNotFoundException we) {
			log.error(we.getLocalizedMessage(), we);
			webDriver.getReport().setMessage(we.getMessage());
			webDriver.getReport().setStatus("FAIL");
		} catch (Exception e) {
			// throw new
			// Exception("Error while Fetching XML Response Success Flag: " +
			// e.getMessage());
			log.error(e.getLocalizedMessage(), e);
			webDriver.getReport().setMessage(e.getMessage());
			webDriver.getReport().setStatus("FAIL");
		}
		return response_status;
	}

	// bhaskar WebService Response status END 15-Nov-2016

	public static String getXMLResponseTagValue(String responseXml, String Tag_Name, String Node_Value, int index) throws SAXParseException,
			Exception {
		try {
			nodeAvailable = false;
			reportNodeValue = null;
			TagValue = null;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(responseXml);
			doc.getDocumentElement().normalize();
			if (Tag_Name.equalsIgnoreCase("batchDetails")) {
				Node configuration = doc.getElementsByTagName(Tag_Name).item(index);
				NodeList list = configuration.getChildNodes();

				String NodeValue = Node_Value;
				for (int i = 0; i < list.getLength(); i++) {
					Node node = list.item(i);

					if (NodeValue.equals(node.getNodeName())) {
						TagValue = node.getTextContent();
						// TagValue = node.setTextContent(arg0);
						log.info("In XML " + Node_Value + "says :" + TagValue);
						break;
					}
				}
			} else {
				NodeList configuration = doc.getElementsByTagName(Tag_Name);

				String NodeValue = Node_Value;
				reportNodeValue = NodeValue;
				// for(int i = 0; i < list.getLength(); i++)
				for (int i = 0; i < configuration.getLength(); i++) {
					// Node node = list.item(i);
					Node node = configuration.item(i);

					if (NodeValue.equals(node.getNodeName())) {
						nodeAvailable = true;
						TagValue = node.getTextContent();
						// log.info("Value of SuccessFlag is :"+TagValue);
						log.info("In XML " + Node_Value + "says :" + TagValue);
						break;
					}
				}

			}

		} catch (NullPointerException ne) {
			log.error("Failed Extract Tag Value <-|-> LocalizeMessage " + ne.getLocalizedMessage() + " <-|-> Message " + ne.getMessage()
					+ " <-|-> Cause " + ne.getCause(), ne);
			throw new NullPointerException("Failed Extract Tag Value <-|-> LocalizeMessage " + ne.getLocalizedMessage() + " <-|-> Message"
					+ ne.getMessage() + " <-|-> Cause " + ne.getCause());
		} catch (SAXParseException sax) {
			log.fatal("Failed to Parse into XML <-|-> LocalizeMessage " + sax.getCause() + " <-|-> LocalizeMessage " + sax.getLocalizedMessage()
					+ " <-|-> Message " + sax.getMessage() + " <-|-> Cause " + sax.getCause(), sax);
			webDriver.getReport().setMessage("TAG NOT FOUND");
			throw new SAXParseException("Failed to Parse into XML <-|-> LocalizeMessage " + sax.getCause() + " <-|-> LocalizeMessage "
					+ sax.getLocalizedMessage() + " <-|-> Message " + sax.getMessage() + " <-|-> Cause " + sax.getCause(), null);
		} catch (FileNotFoundException we) {
			log.error("XML File not found <-|-> LocalizeMessage " + we.getLocalizedMessage() + " <-|-> Message " + we.getMessage() + " <-|-> Cause "
					+ we.getCause(), we);
			webDriver.getReport().setMessage(we.getMessage());
			webDriver.getReport().setStatus("FAIL");
			throw new FileNotFoundException("XML File not found <-|-> LocalizeMessage " + we.getLocalizedMessage() + " <-|-> Message "
					+ we.getMessage() + " <-|-> Cause " + we.getCause());
		} catch (Exception e) {
			// throw new
			// Exception("Error while Fetching XML Response Success Flag: " +
			// e.getMessage());
			log.error("Failed while fatching Tag Value <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message" + e.getMessage()
					+ " <-|-> Cause " + e.getCause(), e);
			webDriver.getReport().setMessage(e.getMessage());
			webDriver.getReport().setStatus("FAIL");
			throw new Exception("Failed while fatching Tag Value <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message"
					+ e.getMessage() + " <-|-> Cause " + e.getCause());
		}
		return TagValue;
	}

	// bhaskar WebService recovery sceanrio END

	public static String getXMLResponseStatusFlatFile(String responseXml, String Tag_Name, String[] Node_Value, int index, String validateTag,
			String validationMsg) throws Exception {
		try {
			// Nodeavailable = false;
			String unMatcheNodeValue = "";
			noResponseFile = false;
			ProcessStatusFlag_Nodeavailable = false;
			SuccessFlag_Nodeavailable = false;
			reportNodeValue = null;
			response_status = null;
			Node node = null; // Mandar
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(responseXml);
			doc.getDocumentElement().normalize();
			NodeList configuration = doc.getElementsByTagName(Tag_Name);

			// Meghna--To verify validation messages//
			if (!(validateTag.equals("") || validationMsg.equals(""))) {

				// *** below given change is made to handle various validation
				// conditions *** -- Mandar 17/11/2017
				boolean response_flag = false;// Nim -Mandar

				int icountResp = doc.getElementsByTagName(validateTag).getLength();

				int iCountWebServiceXls = validationMsg.split("\\|").length;

				if (iCountWebServiceXls > icountResp) {
					String[] XlsError = validationMsg.split("\\|");
					NodeList nodeList = doc.getElementsByTagName(validateTag);
					ArrayList<String> list = new ArrayList<String>();
					for (int i = 0; i < nodeList.getLength(); i++) {
						node = nodeList.item(i);
						// if (Node_Value[0].equals(node.getNodeName()))
						// {
						list.add(node.getTextContent());
					}

					// *** For writing Unmatched Entries ***
					for (int i = 0; i < XlsError.length; i++) {
						// Node node = configuration.item(i);
						String sError = XlsError[i];
						if (!list.contains(sError)) {
							unMatcheNodeValue = unMatcheNodeValue + sError;
						}
					}

				} else {// ****
						// Mandar
					for (int i = 0; i < configuration.getLength(); i++) {
						// Node node = configuration.item(i);
						node = configuration.item(i);

						// if (Node_Value[0].equals(node.getNodeName()))
						if (validateTag.equals(node.getNodeName()))

						{
							ProcessStatusFlag_Nodeavailable = true;
							ProcessStatusFlag_TagValue = node.getTextContent();
							log.info("Value of ProcessStatusFlag is :" + ProcessStatusFlag_TagValue);
						
							if (((validationMsg).toString()).contains(ProcessStatusFlag_TagValue)) {

								// response_flag = response_flag & true;//Mandar
								// -- 17/11/2017

								// *** Mandar
								if (unMatcheNodeValue == "")
									response_flag = true;
								// ***
							}
					

							else {
								// response_flag = response_flag &
								// false;//Mandar -- 17/11/2017
								response_flag = false;// mandar
								unMatcheNodeValue = unMatcheNodeValue + ProcessStatusFlag_TagValue.toString();
							}
							// Mandar

						}
					}// Mandar
				}

				if (response_flag == true) {
					webDriver.getReport().setMessage(" SUCCESS : Expected Error Msgs : " + " ' " + validationMsg + " ' " + " is matching");// Mandar
					response_status = "SUCCESS";
				} else {
					
					webDriver.getReport().setMessage(
							" FAILED : Expected Error Msgs : " + " ' " + validationMsg + " ' " + " Not Matching/Missing/Additional in Actual Msgs : "
									+ " ' " + unMatcheNodeValue + " ' ");// Mandar
					log.info(("Node are not matching :  Expected Msg -- " + validationMsg + "Actual Msg --" + unMatcheNodeValue));
					webDriver.getReport().setStatus("FAIL");
					response_status = "FAILED";
					// ***

				}

			}

			// Meghna--Validation not be checked//
			else {
				for (int i = 0; i < configuration.getLength(); i++) {
					node = configuration.item(i);

					if (Node_Value[0].equals(node.getNodeName())) {
						ProcessStatusFlag_Nodeavailable = true;
						ProcessStatusFlag_TagValue = node.getTextContent();
						log.info("Value of ProcessStatusFlag is :" + ProcessStatusFlag_TagValue);
						// if(ProcessStatusFlag_TagValue.equalsIgnoreCase("COMPLETED"))
						if (ProcessStatusFlag_TagValue.contains("SUCCESS")) {
							response_status = "SUCCESS";
						} else {
							response_status = "FAILED";
							break;
						}
					}
					if (Node_Value[1].equals(node.getNodeName())) {
						SuccessFlag_Nodeavailable = true;
						SuccessFlag_TagValue = node.getTextContent();
						log.info("Value of SuccessFlag is :" + SuccessFlag_TagValue);
						if (SuccessFlag_TagValue.equalsIgnoreCase("SUCCESS")) {
							response_status = "SUCCESS";
						} else {
							response_status = "FAILED";
							break;
						}
					}
				}
			}
		} catch (SAXParseException sax) {
			log.fatal(sax.getCause(), sax);
			webDriver.getReport().setMessage("BLANK WEBSERVICE RESPONSE");
			webDriver.getReport().setStatus("FAIL");
			noResponseFile = true;
			response_status = "BLANK";
		} catch (FileNotFoundException we) {
			log.error(we.getLocalizedMessage(), we);
			webDriver.getReport().setMessage(we.getMessage());
			webDriver.getReport().setStatus("FAIL");
		} catch (Exception e) {
			// throw new
			// Exception("Error while Fetching XML Response Success Flag: " +
			// e.getMessage());
			log.error(e.getLocalizedMessage(), e);
			webDriver.getReport().setMessage(e.getMessage());
			webDriver.getReport().setStatus("FAIL");
		}
		return response_status;
	}

	private static class DummyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	private static class DummyHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

}
