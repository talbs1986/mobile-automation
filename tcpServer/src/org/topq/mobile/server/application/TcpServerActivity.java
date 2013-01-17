package org.topq.mobile.server.application;

import org.topq.mobile.common.client.enums.ClientProperties;
import org.topq.mobile.common.server.consts.TcpConsts;
import org.topq.mobile.server.interfaces.IExecutorService;
import org.topq.mobile.server.interfaces.IDataCallback;

import org.topq.mobile.server.application.R;
import org.topq.mobile.server.impl.ExecutorService;
import org.topq.mobile.server.impl.TcpServer;
import org.topq.mobile.server.interfaces.IIntsrumentationLauncher;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;

public class TcpServerActivity extends Activity implements IIntsrumentationLauncher,IDataCallback {
	private static final String TAG = "TcpServerActivity";
	private int serverPort;
	private static boolean firstLaunch = true;
	private IExecutorService serviceApi;
	private ServiceConnection serviceConnection = new ServiceConnection() {
		  @Override
		  public void onServiceConnected(ComponentName name, IBinder service) {
		    Log.i(TAG, "Service connection established");
		    serviceApi = IExecutorService.Stub.asInterface(service);   
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
	    	
	    	TcpServer server = TcpServer.getInstance(this.serverPort);
	    	server.registerInstrumentationLauncher(this);
	    	server.registerDataExecutor(this);
	    	server.startServerCommunication();
	    	
	    	Intent service = new Intent(ExecutorService.class.getName());
	    	startService(service);
	    	bindService(service,serviceConnection , 0);
        }
    }

	@Override
	public String dataReceived(String data) {
		String result = null;
		try {
			Log.d(TAG, "Executing command : "+data);
			result = serviceApi.executeCommand(data);
		}
		catch (RemoteException e) {
			Log.e(TAG,"Error in service API",e);
		}  
		return result;
	}

	public void startInstrrumentationServer(String launcherActivityClass) {
		Log.i(TAG, "Launching instrumentation for : "+launcherActivityClass);
		Bundle savedInstanceState  = new Bundle();
    	savedInstanceState.putString("launcherActivityClass", launcherActivityClass);
		startInstrumentation(new ComponentName("org.topq.mobile.server.application", "org.topq.mobile.server.impl.RobotiumExecutor"), null, savedInstanceState);
		Log.i(TAG, "Finished instrumentation launch");
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tcp_server, menu);
        return true;
    }
    
    private void readConfiguration() {
    	Log.i(TAG, "Reading user configurations");
    	String tmpVal = getIntent().getStringExtra(ClientProperties.SERVER_PORT.name());
    	if (tmpVal != null && tmpVal.length() != 0) {
    		Log.d(TAG, "Recieved server port : "+tmpVal);
    		this.serverPort = Integer.parseInt(tmpVal);	
    	}
    	else {
    		Log.d(TAG, "Using default server port");
    		this.serverPort = TcpConsts.SERVER_DEFAULT_PORT;
    	}
    	Log.i(TAG, "Done parsing configurations");   	
    }

	@Override
	public IBinder asBinder() {
		// TODO Auto-generated method stub
		return null;
	}
}
