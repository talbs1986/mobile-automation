package org.topq.mobile.tcp.interfaces;

import org.json.JSONObject;

import android.os.Parcelable;

public interface IDataCallback extends Parcelable {

	public JSONObject dataReceived(String data);
}
