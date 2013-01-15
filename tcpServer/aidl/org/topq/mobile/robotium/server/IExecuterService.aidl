package org.topq.mobile.robotium.server;

import org.topq.mobile.robotium.server.MessageListener;

interface IExecuterService {

	String getLatestMessage();
  	void addListener(MessageListener listener);
  	void removeListener(MessageListener listener);
}