package org.topq.mobile.tcp.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.topq.mobile.tcp.interfaces.IDataCallback;

import android.util.Log;

public class TcpExecutorServer implements Runnable {

	private boolean serverLiving;
	private int port;
	private String TAG;
	private List<IDataCallback> listeners;
	private static final String EXIT_JSON = "{\"Command\":\"exit\",\"Params\":[]}";
	
	private TcpExecutorServer(int port) {
		this.serverLiving = true;
		this.port = port;
		this.listeners = new ArrayList<IDataCallback>();
		this.TAG = "TcpExecutorServer("+this.port+")";
	}

	public void startServer() {
		Log.i(TAG, "Start server");	
		Log.d(TAG, "About to launch server");
		Thread serverThread = new Thread(this);
		serverThread.start();
		Log.i(TAG, "Server is up");
		try {
			while (this.serverLiving) {
				Thread.sleep(1000);
			}
		} 
		catch (InterruptedException e) {
			Log.e(TAG,"InterruptedException" ,e);
		}
		Log.i(TAG, "Server is down");
		
	}

	public void stopServer() {
		serverLiving = false;	
	}
	
	public static TcpExecutorServer getInstance(int port) {
		return new TcpExecutorServer(port);
	}
	
	public void registerExecutorToServer(IDataCallback executor) {
		Log.d(TAG,"Adding executor : "+executor);
		this.listeners.add(executor);
	}
	
	public void run() {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		try {
			Log.d(TAG, "Server is setting up socket on port : "+this.port);
			serverSocket = new ServerSocket(this.port);
			while(this.serverLiving) {
				Log.i(TAG, "Server is waiting for connection");
				clientSocket = serverSocket.accept();
				PrintWriter out = null;
				BufferedReader in = null;
				try {
					Log.i(TAG, "Connection was established");
					out = new PrintWriter(clientSocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String line = in.readLine();
					if (line != null) {
						Log.i(TAG, "Received: '" + line + "'");
						if (TcpExecutorServer.EXIT_JSON.equals(line.trim())) {
							this.serverLiving = false;
						}
						JSONObject response = null;
						for (IDataCallback listener : listeners) {
							Log.d(TAG, "Sending execute command to : "+listener);
							response = listener.dataReceived(line);
							Log.i(TAG, "Recieved response : "+response);
						}
						out.println(response);
						out.flush();
					}
				} 
				catch (Exception e) {
					Log.e(TAG, "Failed to process request due to" + e.getMessage());
				} 
				finally {
					// Closing resources
					if (null != out) {
						out.close();
					}
					try {
						if (null != in) {
							in.close();
						}
						if (null != clientSocket) {
							clientSocket.close();
						}
					} 
					catch (Exception e) {
						Log.w(TAG, "exception was caught while closing resources", e);
					}
				}
			}
		} 
		catch (IOException e) {
			Log.e(TAG, "exception was caught while handling server socket", e);
		}
		finally {
			if (null != serverSocket) {
				try {
					serverSocket.close();
				} 
				catch (IOException e) {
					Log.w(TAG, "exception was caught while closing server socket", e);
				}
			}
		}
	}

}
