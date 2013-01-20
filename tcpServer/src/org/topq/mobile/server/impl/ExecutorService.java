package org.topq.mobile.server.impl;

import org.topq.mobile.server.interfaces.IExecutorService;
import org.topq.mobile.server.interfaces.IDataCallback;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ExecutorService extends Service {
	
	private static final String TAG = "ExecutorService";
	private IDataCallback commandExecutor;
	
	private IExecutorService.Stub apiEndPoint = new IExecutorService.Stub() {
		
		@Override
		public String executeCommand(String data) {
			Log.d(TAG, "Recieved : "+data);
			try {
				return commandExecutor.dataReceived(data);
			} 
			catch (RemoteException e) {
				Log.e(TAG,"Error in command execution",e);
			}
			return null;
		}
		
		@Override
		public void registerExecutor(IDataCallback executor) {
			Log.d(TAG,"Registering Executor : "+executor);
			commandExecutor = executor;
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		if (ExecutorService.class.getName().equals(intent.getAction())) {
		    Log.d(TAG, "Bound by intent " + intent);
		    return this.apiEndPoint;
		} 
		else {
		    return null;
		}
	}

	@Override
	public void onCreate() {		
		super.onCreate();
		Log.i(TAG, "Is created");
	}
	
}
