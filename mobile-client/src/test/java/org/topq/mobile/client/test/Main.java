package org.topq.mobile.client.test;

import org.topq.mobile.client.impl.MobileClient;
import org.topq.mobile.common.client.interfaces.MobileClientInterface;

public class Main {
	
	public static void main(String [ ] args) {
		try {
			MobileClientInterface x = new MobileClient(null,false,true);
//			x.launch("com.tal.example.loginapp.LoginActivity");
			x.enterText(0, "tal@tal.com");
//			x.enterText(1, "1234567");
//			x.clickOnButtonWithText("Sign in or register");
//			Thread.sleep(1000 * 5);
//			x.clickOnButtonWithText("Ok");
//			x.clickOnButtonWithText("Sign in or register");
//			Thread.sleep(1000 * 5);
//			x.clickOnButtonWithText("Ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
		
	}

}
