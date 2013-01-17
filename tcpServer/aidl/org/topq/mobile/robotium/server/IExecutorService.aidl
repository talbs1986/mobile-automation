package org.topq.mobile.robotium.server;

import org.topq.mobile.tcp.interfaces.IDataCallback;

interface IExecutorService {

	String executeCommand(String data);
	void registerExecutor(IDataCallback executor);

}