package org.topq.mobile.robotium.server;

import org.topq.mobile.robotium.server.interfaces.ISoloProvider;
import org.topq.mobile.tcp.interfaces.IDataCallback;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

public class ExecuterService extends Service {
	
	private static final String TAG = "ExecuterService";
	private IDataCallback commandExecuter;
	
	private IExecuterService.Stub apiEndPoint = new IExecuterService.Stub() {
		
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
		public void registerExecuter(IDataCallback executer) {
			Log.d(TAG,"Registering Executer : "+executer);
			commandExecuter = executer;
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		if (ExecuterService.class.getName().equals(intent.getAction())) {
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
