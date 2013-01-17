package org.topq.mobile.robotium.server;

import java.io.Serializable;

import org.json.JSONObject;
import org.topq.mobile.common.client.enums.ClientProperties;
import org.topq.mobile.robotium.server.interfaces.ISoloProvider;
import org.topq.mobile.tcp.interfaces.IDataCallback;
import org.topq.mobile.tunnel.application.TcpServerActivty;

import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
/**
 * 
 * @author Bortman Limor
 *
 */
public class RobotiumServerInstrumentation extends Instrumentation implements ISoloProvider {
	
	private static final String TAG = "RobotiumServerInstrumentation";
	private Activity myActive = null;
	private SoloExecutor executor = null;
	private String launcherActivityClass;
	private Solo solo = null;
	private IExecutorService api;
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		  @Override
		  public void onServiceConnected(ComponentName name, IBinder service) {
		    Log.i(TAG, "Service connection established");
		    api = IExecutorService.Stub.asInterface(service);   
		    try {
				api.registerExecutor(executorListener);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		 
		  @Override
		  public void onServiceDisconnected(ComponentName name) {
		    Log.i(TAG, "Service connection closed");      
		  }
	};
	
	private IDataCallback.Stub executorListener = new IDataCallback.Stub() {
		@Override
		public String dataReceived(String data) throws RemoteException {
			String result = null;
			try {
				result = getExecutor().execute(data).toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}
	};
	
	@Override	
	public void onCreate(Bundle arguments) {	
		Log.d(TAG, "Creating Instrumentation");	
		super.onCreate(arguments);
		if (arguments != null) {
			if (arguments.containsKey("launcherActivityClass")) {
				this.launcherActivityClass= arguments.getString("launcherActivityClass");
				Log.d(TAG,"Activity class is : " + arguments.getString("launcherActivityClass")); 
			} 
			else {
				Log.e (TAG, "no launcherActivityClass here!");
				System.exit(100);
			}
		}
		Log.d(TAG, "Target Context : "+getTargetContext());
		Log.d(TAG, "This Context : "+getContext());
		Log.d(TAG, "Target Package : "+getTargetContext().getPackageName());
    	Intent service = new Intent(ExecutorService.class.getName());	
    	getContext().bindService(service,serviceConnection , 0);
    
		start();
	}
	
	@Override	
	public void onStart() {
		super.onStart();
	}

	void prepareLooper() {  
		Looper.prepare(); 
	}
		
	@Override	
	public Solo getSolo() {	
		if(myActive == null) {
			Log.i(TAG, "Starting main activity");
			Intent intent = new Intent(Intent.ACTION_MAIN);		
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClassName(getTargetContext().getPackageName(),launcherActivityClass);	
			myActive = startActivitySync(intent);
			Log.d(TAG, "App is started");
		}
		if(solo == null){
			prepareLooper();
			solo = new Solo(this,myActive);	
		}
		return solo;
	}
		
	public SoloExecutor getExecutor(){	
		if(executor == null) {			
			executor = new SoloExecutor(this, this);
		}		
		return executor;	
	}
}
