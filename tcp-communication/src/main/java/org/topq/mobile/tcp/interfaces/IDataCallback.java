package org.topq.mobile.tcp.interfaces;

import org.json.JSONObject;

public interface IDataCallback {

	public JSONObject dataReceived(String data);
}
