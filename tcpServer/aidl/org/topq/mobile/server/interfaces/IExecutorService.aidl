package org.topq.mobile.server.interfaces;

import org.topq.mobile.server.interfaces.IDataCallback;

interface IExecutorService {

	String executeCommand(String data);
	void registerExecutor(IDataCallback executor);

}