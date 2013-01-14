package org.topq.mobile.robotium.server;

import org.topq.mobile.tunnel.application.R;

import android.app.Activity;
import android.os.Bundle;

public class RobotiumServerActivity extends Activity {


	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tcp_server);
	}

}