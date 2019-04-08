package com.majesco.itaf.main;

import java.io.IOException;

import atu.testrecorder.exceptions.ATUTestRecorderException;


public class ObjectFactory {

	private static MainController mainController;
	private static ITAFWebDriver itafWebDriver;
	
	public static void initialize() throws IOException, ATUTestRecorderException {
		if (ITAFWebDriver.isBillingApplication()) {
			itafWebDriver = new ITAFWebDriverBilling();
			mainController = new MainControllerBilling();
		} else if (ITAFWebDriver.isClaimsApplication()) {
			itafWebDriver = new ITAFWebDriverClaims();
			mainController = new MainControllerClaims();
		} else if (ITAFWebDriver.isPASApplication()) {
			itafWebDriver = new ITAFWebDriverPAS();
			mainController = new MainControllerPAS();
		}
		itafWebDriver.init();
	}

	public static MainController getMainController() {
		return mainController;
	}

	public static ITAFWebDriver getWebDriver() {
		return itafWebDriver;
	}

}
