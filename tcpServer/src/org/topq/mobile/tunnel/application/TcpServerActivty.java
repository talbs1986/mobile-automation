package org.topq.mobile.tunnel.application;

import org.topq.mobile.common.client.enums.ClientProperties;
import org.topq.mobile.robotium.server.ExecutorService;
import org.topq.mobile.robotium.server.IExecutorService;
import org.topq.mobile.tcp.impl.TcpServer;
import org.topq.mobile.tcp.interfaces.IDataCallback;
import org.topq.mobile.tcp.interfaces.IIntsrumentationLauncher;

import org.topq.mobile.tunnel.application.R;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;

public class TcpServerActivty extends Activity implements IIntsrumentationLauncher,IDataCallback {
	private static final String TAG = "TcpServerActivity";
	private int serverPort;
	private static boolean firstLaunch = true;
	private IExecutorService api;
	private ServiceConnection serviceConnection = new ServiceConnection() {
		  @Override
		  public void onServiceConnected(ComponentName name, IBinder service) {
		    Log.i(TAG, "Service connection established");
		    api = IExecutorService.Stub.asInterface(service);   
		  }
		 
		  @Override
		  public void onServiceDisconnected(ComponentName name) {
		    Log.i(TAG, "Service connection closed");      
		  }
	};
		 
			
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (firstLaunch) {
        	firstLaunch = false;
	    	readConfiguration();
	    	
	    	TcpServer tunnel = TcpServer.getInstance(this.serverPort);
	    	tunnel.registerInstrumentationLauncher(this);
	    	tunnel.registerDataExecuter(this);
	    	tunnel.startTunnelCommunication();
	    	
	    	Intent service = new Intent(ExecutorService.class.getName());
	    	startService(service);
	    	bindService(service,serviceConnection , 0);
	    	
//	    	startActivity(new Intent(this,RobotiumServerActivity.class));
        }
    }

	@Override
	public String dataReceived(String data) {
		String result = null;
		try {
			result = api.executeCommand(data);
		}
		catch (RemoteException e) {
			Log.e(TAG,"Error in service API",e);
		}  
		return result;
	}

	public void startInstrrumentationServer(String launcherActivityClass) {
		Bundle savedInstanceState  = new Bundle();
    	savedInstanceState.putString("launcherActivityClass", launcherActivityClass);
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
    		this.serverPort = Integer.parseInt(tmpVal);	
    	}
    	else {
    		Log.d(TAG, "Using default server port");
    		this.serverPort = TcpServer.DEFAULT_SERVER_PORT;
    	}
    	Log.i(TAG, "Recived Argument server port : "+String.valueOf(this.serverPort));   	
    }

	@Override
	public IBinder asBinder() {
		// TODO Auto-generated method stub
		return null;
	}
}
