/**
 * 
 */
package com.example.tcpserver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;
import org.jsystemtest.mobile.common_mobile.client.interfaces.*;
import org.jsystemtest.mobile.common_mobile.server.interfaces.IDataCallback;

/**
 * @author Bortman Limor
 *
 */
public class DataCallbackImplement implements IDataCallback{
	Intent intent= null;
	private static final String TAG = "DataCallbackImplement";
	
	public DataCallbackImplement(Intent intent){
		this.intent = intent;
	}

	/**
	 * 
	 */
	@Override
	public JSONObject dataReceived(String data) {
		JSONObject command = null;
		try {
			command = new JSONObject(data);
		} catch (JSONException e) {
			Log.e(TAG, "Can't pars command");
		}
		intent.putExtra("command", command.toString());
		return command;
	}

}
