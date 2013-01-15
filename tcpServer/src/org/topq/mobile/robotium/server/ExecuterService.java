package org.topq.mobile.robotium.server;


import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ExecuterService extends Service {

	List<MessageListener> listeners = new ArrayList<MessageListener>();
	
	private static final String TAG = "ExecuterService";
	
	private IExecuterService.Stub apiEndPoint = new IExecuterService.Stub() {
		
		@Override
		public String getLatestMessage() throws RemoteException {
			return TAG+" : Message";
		}

		@Override
		public void addListener(MessageListener listener) throws RemoteException {
			Log.i(TAG,"Adding Listener ...");
			listeners.add(listener);
		}

		@Override
		public void removeListener(MessageListener listener) throws RemoteException {
			Log.i(TAG,"Removing Listener ...");
			listeners.remove(listener);
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		
		super.onCreate();
		Log.i(TAG, "I was created");
	}

}
