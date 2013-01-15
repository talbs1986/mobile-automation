package org.topq.mobile.robotium.server;

import org.topq.mobile.tcp.interfaces.IDataCallback;

interface IExecuterService {

	String executeCommand(String data);
	void registerExecuter(in IDataCallback exectuer);

}