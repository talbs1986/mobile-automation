package org.topq.mobile.robotium.server;

import org.topq.mobile.common.client.enums.ClientProperties;
import org.topq.mobile.tunnel.application.R;

import android.app.Activity;
import android.content.ComponentName;
import android.os.Bundle;

public class RobotiumServerActivity extends Activity {


	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tcp_server);
		
	}
	
	public void startInstrrumentationServer(String launcherActivityClass) {
		Bundle savedInstanceState  = new Bundle();
    	savedInstanceState.putString("launcherActivityClass", launcherActivityClass);
    	savedInstanceState.putString(ClientProperties.SERVER_PORT.name(), String.valueOf("6262"));
		startInstrumentation(new ComponentName("org.topq.mobile.tunnel.application", "org.topq.mobile.robotium.server.RobotiumServerInstrumentation"), null, savedInstanceState);
	}

}