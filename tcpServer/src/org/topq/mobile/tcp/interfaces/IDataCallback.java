package org.topq.mobile.tcp.interfaces;

import org.json.JSONObject;
import org.topq.mobile.robotium.server.RobotiumServerInstrumentation;

import android.os.Parcel;
import android.os.Parcelable;

public interface IDataCallback extends Parcelable {

	public JSONObject dataReceived(String data);
	
	public static final Parcelable.Creator<IDataCallback> CREATOR = new Parcelable.Creator<IDataCallback>() {
		public IDataCallback createFromParcel(Parcel in) {
			return RobotiumServerInstrumentation.myInstance;
		}

		public IDataCallback[] newArray(int size) {
			IDataCallback[] result = new IDataCallback[1];
			result[0] = RobotiumServerInstrumentation.myInstance;
			return result;
		}
	};
}
