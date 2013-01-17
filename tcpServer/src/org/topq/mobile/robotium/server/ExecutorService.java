package org.topq.mobile.robotium.server;

import org.topq.mobile.tcp.interfaces.IDataCallback;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ExecutorService extends Service {
	
	private static final String TAG = "ExecuterService";
	private IDataCallback commandExecuter;
	
	private IExecutorService.Stub apiEndPoint = new IExecutorService.Stub() {
		
		@Override
		public String executeCommand(String data) {
			Log.d(TAG, "Recieved : "+data);
			try {
				return commandExecuter.dataReceived(data);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		public void registerExecutor(IDataCallback executor) {
			Log.d(TAG,"Registering Executer : "+executor);
			commandExecuter = executor;
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		if (ExecutorService.class.getName().equals(intent.getAction())) {
		    Log.d(TAG, "Bound by intent " + intent);
		    return apiEndPoint;
		} 
		else {
		    return null;
		}
	}

	@Override
	public void onCreate() {		
		super.onCreate();
		Log.i(TAG, "I was created");
	}
	
}
