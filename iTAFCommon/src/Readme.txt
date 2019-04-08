
Steps for running the new iTAF framework:

1. Unzip the iTAF-1.19.2.0-dist.zip file at any location in your P/C from where you want to execute iTAF.
2. Copy your test suite in the same directory where you have extracted the zip file.
3. This release has a updated version of "SendMail.vbs" file please copy that from this package to your respective root folder.
4. Edit startITAF.bat for providing the Config.xls path as in your test suite in command line argument.
5. You can provide APPLICATIO_NAME in one of the below 2 possible ways.
	a. Add APPLICATION_NAME (Billing/Claims/PAS) as second command line argument as per instruction provided inside startITAF.bat.
	For Example in case of Claims application:
	java -classpath %CLASSPATH% com.majesco.itaf.main.ITAFWebDriver  Claim\CommonResources\Config.xls Claims
	OR
	b. Add parameter APPLICATION_NAME in Config.xls with value as Billing/Claims/PAS as applicable in Config.xml.

6. Run the startITAF.bat.
