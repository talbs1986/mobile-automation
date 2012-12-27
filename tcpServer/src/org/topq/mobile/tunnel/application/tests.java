package org.topq.mobile.tunnel.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

public class tests extends AndroidTestCase {
	
	public void testMe() {
		Socket socket = null;
		BufferedReader input = null;
		String lastResult;
		JSONObject data = new JSONObject();
		try {
			data.put("launch", "");
			socket = new Socket("localhost", 6262);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter output = new PrintWriter(socket.getOutputStream());
			output.println(data);
			output.flush();
			lastResult = input.readLine();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (socket != null) {
					socket.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
