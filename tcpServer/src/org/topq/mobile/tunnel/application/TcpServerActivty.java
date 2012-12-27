package org.topq.mobile.tunnel.application;

import org.topq.mobile.tcp.impl.TcpTunnel;
import org.topq.mobile.tcp.interfaces.IIntsrumentationLauncher;

import org.topq.mobile.tunnel.application.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.util.Log;
import android.view.Menu;

public class TcpServerActivty extends Activity implements IIntsrumentationLauncher {
	private static final String TAG = "TcpServerActivituy";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	Log.i(TAG, "in on create");

    	TcpTunnel tunnel = TcpTunnel.getInstance(6262, "localhost", 4321);
    	tunnel.registerInstrumentationLauncher(this);
    	tunnel.startTunnelCommunication();
    }

	public void startInstrrumentationServer(String launcherActivityClass) {
		Bundle savedInstanceState  = new Bundle();
    	savedInstanceState.putString("launcherActivityClass", launcherActivityClass);
		startInstrumentation(new ComponentName("org.topq.mobile.robotium.server", "org.topq.mobile.robotium.server.RobotiumServerInstrumentation"), null, savedInstanceState);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tcp_server, menu);
        return true;
    }
}
