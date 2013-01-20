package org.topq.mobile.server.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.topq.mobile.common.server.utils.CommandParser;
import org.topq.mobile.common.server.utils.ScriptParser;
import org.topq.mobile.server.interfaces.IIntsrumentationLauncher;
import org.topq.mobile.server.interfaces.IDataCallback;

import android.util.Log;

public class TcpServer implements Runnable {

	private static String TAG;
	private int listenerPort;
	private boolean serverLiving = true;
	private IIntsrumentationLauncher instrumentationLauncher;
	private IDataCallback dataExecutor;
	
	private TcpServer(int listenerPort) {
		this.listenerPort = listenerPort;
		this.instrumentationLauncher = null;
		this.dataExecutor = null;
		TcpServer.TAG = "TcpServer("+this.listenerPort+")";
	}

	public static TcpServer getInstance(int listenerPort) {
		return new TcpServer(listenerPort);
	}
	
	public void startServerCommunication() {
		Thread serverThread = new Thread(this);
		Log.i(TAG,"About to launch server");
		serverThread.start();	
		Log.i(TAG,"Server has start");
	}
	
	public void stopServerCommunication() {
		Log.d(TAG,"About to stop server");
		this.serverLiving = false;
	}
	
	public void registerInstrumentationLauncher(IIntsrumentationLauncher instruLauncher) {
		Log.d(TAG, "Registering instrumenation launcher : "+instruLauncher);
		this.instrumentationLauncher = instruLauncher;
	}
	
	public void registerDataExecutor(IDataCallback dataExecutor) {
		Log.d(TAG, "Registering data launcher : "+dataExecutor);
		this.dataExecutor = dataExecutor;
	}
	
	public void run() {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(this.listenerPort);
			while (this.serverLiving) {
				Log.i(TAG, "Server is waiting for connection ...");
				clientSocket = serverSocket.accept();	
				PrintWriter clientOut = null;
				BufferedReader clientIn = null;
				try {
					Log.i(TAG, "Connection established");
					clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
					clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String line = clientIn.readLine();	
					if (line != null) {
						Log.d(TAG, "Received: '" + line + "'");
						ScriptParser parser = new ScriptParser(line);
						for (CommandParser command : parser.getCommands()) {
							if(command.getCommand().equals("launch") && this.instrumentationLauncher != null){
								Log.d(TAG, "Recieved launch command");
								this.instrumentationLauncher.startInstrrumentationServer(command.getArguments().getString(0));
								Thread.sleep(TimeUnit.SECONDS.toMillis(5));
							}
						}
					}
					Log.i(TAG, "Sending command to executor");
					String response = dataExecutor.dataReceived(line);
					Log.d(TAG,"Command response is : "+response);
					clientOut.println(response);	
				}  
				catch (Exception e) {
					Log.e(TAG, "Failed to process request due to" + e.getMessage());
				} 
				finally {
					// Closing resources
					if (null != clientOut) {
						clientOut.close();
					}
					try {
						if (null != clientIn) {
							clientIn.close();
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
		catch (Exception e) {
			Log.e(TAG,"Exception occured : "+e.getMessage());
		}
		finally {
			if (null != serverSocket ) {
				try {
					serverSocket.close();
				} 
				catch (IOException e) {
					Log.w(TAG, "exception was caught while closing resources", e);
				}
			}
		}
	}
	
	

}