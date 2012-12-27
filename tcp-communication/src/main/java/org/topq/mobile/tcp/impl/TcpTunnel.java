package org.topq.mobile.tcp.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.topq.mobile.common.server.utils.CommandParser;
import org.topq.mobile.common.server.utils.ScriptParser;
import org.topq.mobile.tcp.interfaces.IIntsrumentationLauncher;

import android.util.Log;

public class TcpTunnel implements Runnable {

	private static String TAG;
	private int listenerPort;
	private int sendDataPort;
	private String sendDataHostName;
	private boolean serverLiving = true;
	private IIntsrumentationLauncher instrumentationLauncher;
	
	private TcpTunnel(int listenerPort,String sendDataHostName,int sendDataPort) {
		this.listenerPort = listenerPort;
		this.sendDataHostName = sendDataHostName;
		this.sendDataPort = sendDataPort;
		this.instrumentationLauncher = null;
		TcpTunnel.TAG = "TcpTunnel("+this.sendDataHostName+":"+this.listenerPort+"->"+this.sendDataPort+")";
	}

	public static TcpTunnel getInstance(int listenerPort,String sendDataHostName,int sendDataPort) {
		return new TcpTunnel(listenerPort,sendDataHostName,sendDataPort);
	}
	
	public void startTunnelCommunication() {
		Thread tunnelThread = new Thread(this);
		Log.i(TAG,"About to launch tunnel");
		tunnelThread.start();	
		Log.i(TAG,"Tunnel has start");
	}
	
	public void stopTunnelCommunication() {
		Log.d(TAG,"About to stop tunnel");
		this.serverLiving = false;
	}
	
	public void registerInstrumentationLauncher(IIntsrumentationLauncher instruLauncher) {
		Log.d(TAG, "Registering instrumenation launcher : "+instruLauncher);
		this.instrumentationLauncher = instruLauncher;
	}
	
	public void run() {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(this.listenerPort);
			while (this.serverLiving) {
				Log.d(TAG, "Server is waiting for connection");
				clientSocket = serverSocket.accept();	
				PrintWriter clientOut = null;
				BufferedReader clientIn = null;
				try {
					Log.d(TAG, "Connection was established");
					clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
					clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String line = clientIn.readLine();
					PrintWriter serverOutput = null;
					BufferedReader serverInput = null;			
					
					if (line != null) {
						Log.d(TAG, "Received: '" + line + "'");
						ScriptParser parser = new ScriptParser(line);
						for (CommandParser command : parser.getCommands()) {
							if(command.getCommand().equals("launch") && this.instrumentationLauncher != null){
								this.instrumentationLauncher.startInstrrumentationServer(command.getArguments().getString(0));
								Thread.sleep(TimeUnit.SECONDS.toMillis(5));
							}
							Socket socket = new Socket(this.sendDataHostName, this.sendDataPort);
							socket.setTcpNoDelay(true);
							serverOutput = new PrintWriter(socket.getOutputStream());
							serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							serverOutput.append(line+"\n");
							serverOutput.flush();
						}
					}
					clientOut.println(serverInput.readLine());	
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
			Log.e(TAG,"Exception accored : "+e.getMessage());
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
