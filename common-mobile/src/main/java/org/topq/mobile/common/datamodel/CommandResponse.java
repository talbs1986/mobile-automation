package org.topq.mobile.common.datamodel;

public class CommandResponse {
	
	private String originalCommand;
	private String[] params;
	private String response;
	private boolean isSucceeded;
	
	public CommandResponse() {
		this.originalCommand = null;
		this.params = null;
		this.response = null;
		this.isSucceeded = false;
	}
	
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public boolean isSucceeded() {
		return isSucceeded;
	}

	public void setSucceeded(boolean isSucceeded) {
		this.isSucceeded = isSucceeded;
	}

	public String getOriginalCommand() {
		return originalCommand;
	}

	public String[] getParams() {
		return params;
	}
	
	public void setOriginalCommand(String originalCommand) {
		this.originalCommand = originalCommand;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

}
