package org.topq.mobile.common.datamodel;

public class CommandRequest {
	
	private String command;
	private String[] params;
	
	public CommandRequest() {}

	public CommandRequest(String command,String... params) {
		this.command = command;
		this.params = params;
	}
	
	public String getCommand() {
		return command;
	}

	public String[] getParams() {
		return params;
	}

}
