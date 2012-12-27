package org.topq.mobile.client.test;

import java.util.Properties;

import org.topq.mobile.client.impl.MobileClient;
import org.topq.mobile.common.client.interfaces.MobileClientInterface;

public class Main {
	
	public static void main(String [ ] args) {
		try {
			Properties prop = new Properties();
			prop.put("port", "8888");
			prop.put("host", "localhost");
			prop.put("deviceSerial", "emulator-5554");
			MobileClientInterface x = new MobileClient(prop,false,false);
			x.launch("com.tal.example.loginapp.LoginActivity");
//			x.launch("org.topq.jsystem.mobile.RobotiumClientActivity");
			x.enterText(0, "tal@tal.com");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
		
	}

}
