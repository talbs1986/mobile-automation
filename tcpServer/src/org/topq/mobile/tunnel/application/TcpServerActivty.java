package org.topq.mobile.tunnel.application;

import org.topq.mobile.common.client.enums.ClientProperties;
import org.topq.mobile.robotium.server.ExecuterService;
import org.topq.mobile.robotium.server.RobotiumServerActivity;
import org.topq.mobile.tcp.impl.TcpClient;
import org.topq.mobile.tcp.impl.TcpExecutorServer;
import org.topq.mobile.tcp.impl.TcpTunnel;
import org.topq.mobile.tcp.interfaces.IIntsrumentationLauncher;

import org.topq.mobile.tunnel.application.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class TcpServerActivty extends Activity implements IIntsrumentationLauncher {
	private static final String TAG = "TcpServerActivituy";
	private int tunnelPort;
	private int robotiumServerPort;
	private String tunnelHostName;
	private static boolean firstLaunch = true;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (firstLaunch) {
        	firstLaunch = false;
    	Log.i(TAG, "in on create");
    	readConfiguration();
    	
    	TcpTunnel tunnel = TcpTunnel.getInstance(this.tunnelPort, this.tunnelHostName, this.robotiumServerPort);
    	tunnel.registerInstrumentationLauncher(this);
    	tunnel.startTunnelCommunication();
    	
    	startService(new Intent(ExecuterService.class.getName()));
    	
    	Intent intent = new Intent(this,RobotiumServerActivity.class);
    	startActivity(intent);
        }
    }

	public void startInstrrumentationServer(String launcherActivityClass) {
		Bundle savedInstanceState  = new Bundle();
    	savedInstanceState.putString("launcherActivityClass", launcherActivityClass);
    	savedInstanceState.putString(ClientProperties.SERVER_PORT.name(), String.valueOf(this.robotiumServerPort));
		startInstrumentation(new ComponentName("org.topq.mobile.tunnel.application", "org.topq.mobile.robotium.server.RobotiumServerInstrumentation"), null, savedInstanceState);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tcp_server, menu);
        return true;
    }
    
    private void readConfiguration() {
    	String tmpVal = getIntent().getStringExtra(ClientProperties.TUNNEL_PORT.name());
    	if (tmpVal != null && tmpVal.length() != 0) {
    		this.tunnelPort = Integer.parseInt(tmpVal);	
    	}
    	else {
    		Log.d(TAG, "Using default tunnel port");
    		this.tunnelPort = TcpTunnel.DEFAULT_TUNNEL_PORT;
    	}
    	tmpVal = getIntent().getStringExtra(ClientProperties.SERVER_PORT.name());
    	if (tmpVal != null && tmpVal.length() != 0) {
    		this.robotiumServerPort = Integer.parseInt(tmpVal);	
    	}
    	else {
    		Log.d(TAG, "Using default server port");
    		this.robotiumServerPort = TcpExecutorServer.DEFAULT_PORT;
    	}
    	tmpVal = getIntent().getStringExtra(ClientProperties.SERVER_HOST.name());
    	if (tmpVal != null && tmpVal.length() != 0) {
    		this.tunnelHostName = tmpVal;	
    	}
    	else {
    		Log.d(TAG, "Using default hostname");
    		this.tunnelHostName = TcpTunnel.DEFAULT_HOSTNAME;
    	}

    	Log.i(TAG, "Recived Argument tunnel port : "+String.valueOf(this.tunnelPort));
    	Log.i(TAG, "Recived Argument Robotium server port : "+String.valueOf(this.robotiumServerPort));
    	Log.i(TAG, "Recived Argument tunnel host name : "+String.valueOf(this.tunnelHostName));
    	
    }
}
