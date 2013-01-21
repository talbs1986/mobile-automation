package org.topq.mobile.server.application;

import java.net.InetAddress;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

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
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class TcpServerActivity extends Activity implements IIntsrumentationLauncher,IDataCallback {
	private static final String TAG = "TcpServerActivity";
	private int serverPort;
	private boolean firstLaunch = true;
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
	
	/**
     * Get IP address from first non-localhost interface
     * @param ipv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } 
        catch (Exception e) {
        	Log.e(TAG, "Execption while getting ip", e);
        }
        return "";
    }
		 
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.firstLaunch) {
        	this.firstLaunch = false;
	    	readConfiguration();
	    	
	    	setContentView(R.layout.activity_tcp_server);
			TextView serverDetails = (TextView)findViewById(R.id.server_details);
		    Resources res = getResources();
		    String str = res.getString(R.string.server_details);
			String text = String.format(str,getIPAddress(), this.serverPort);
		    serverDetails.setText(text);

	    	TcpServer server = TcpServer.getInstance(this.serverPort);
	    	server.registerInstrumentationLauncher(this);
	    	server.registerDataExecutor(this);
	    	server.startServerCommunication();
	    	
	    	Intent service = new Intent(ExecutorService.class.getName());
	    	startService(service);
	    	bindService(service,this.serviceConnection , 0);
        }
    }

	@Override
	public String dataReceived(String data) {
		String result = null;
		try {
			Log.d(TAG, "Executing command : "+data);
			result = this.serviceApi.executeCommand(data);
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
		// NOT USED
		return null;
	}
}
