package org.topq.mobile.robotium.server;

import org.json.JSONObject;
import org.topq.mobile.robotium.server.interfaces.ISoloProvider;
import org.topq.mobile.tcp.interfaces.IDataCallback;
import org.topq.mobile.tcp.impl.TcpExecutorServer;

import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
/**
 * 
 * @author Bortman Limor
 *
 */
public class RobotiumServerInstrumentation extends Instrumentation implements IDataCallback, ISoloProvider {
	
	private static final String TAG = "RobotiumServerInstrumentation";
	private Activity myActive = null;
	private SoloExecutor executor = null;
	private static String launcherActivityClass;
	private Solo solo = null;
	
	@Override	
	public void onCreate(Bundle arguments) {	
		Log.d(TAG, "Creating Instrumentation");	
		super.onCreate(arguments);
		if (arguments != null) {
			if (arguments.containsKey("launcherActivityClass")) {
				launcherActivityClass= arguments.getString("launcherActivityClass");
				Log.d(TAG,"Activity class is : " + arguments.getString("launcherActivityClass")); 
			} 
			else {
				Log.e (TAG, "no launcherActivityClass here!");
				System.exit(100);
			}
		}
		Log.d(TAG, "Taget Context : "+getTargetContext());
		Log.d(TAG, "This Context : "+getContext());
		Log.d(TAG, "Target Package : "+getTargetContext().getPackageName());
		start();
	}
	
	@Override	
	public void onStart() {
		super.onStart();
		TcpExecutorServer server = TcpExecutorServer.getInstance(4321);
		server.registerExecutorToServer(this);
		server.startServer();
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
		
	@Override
	public JSONObject dataReceived(String data) {
		Log.i(TAG, "Recieved data " + data);
		try {
			return getExecutor().execute(data);
		}
		catch (Exception e) {
			Log.e(TAG, "Failed to process data " + data, e);
			e.printStackTrace();
		}
		return new JSONObject();
	}
}
