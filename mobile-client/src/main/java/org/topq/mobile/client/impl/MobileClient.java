package org.topq.mobile.client.impl;

import net.iharder.Base64;

import org.apache.log4j.Logger;
import org.topq.mobile.client.interfaces.MobileClientInterface;
import org.topq.mobile.common.client.enums.Attribute;
import org.topq.mobile.common.client.enums.HardwareButtons;
import org.topq.mobile.common.datamodel.CommandRequest;
import org.topq.mobile.common.datamodel.CommandResponse;
import org.topq.mobile.common.server.consts.TcpConsts;
import org.topq.mobile.common.server.utils.JsonParser;
import org.topq.mobile.tcp.impl.TcpClient;

public class MobileClient implements MobileClientInterface {

	private static Logger logger = Logger.getLogger(MobileClient.class);

	private TcpClient tcpClient;
	private int serverPort;
	private String serverHost;
	
	private MobileClient(String serverHost,int serverPort) {
		try {
			this.serverPort = serverPort;
			this.serverHost = serverHost;
			this.tcpClient = new TcpClient(this.serverHost, this.serverPort);
		}
		catch(Exception e) {
			logger.error("Exception in constructor !!", e);
		}
	}
	
	public static MobileClientInterface getInstance(){
		return new MobileClient(TcpConsts.SERVER_DEFAULT_HOSTNAME,TcpConsts.SERVER_DEFAULT_PORT);
	}
	
	public static MobileClientInterface getInstance(String serverHost,int serverPort){
		return new MobileClient(serverHost,serverPort);
	}


	/**
	 * Send data using the TCP connection & wait for response Parse the response (make conversions if necessary - pixels
	 * to mms) and report
	 * 
	 * @param device
	 * @param data
	 *            serialised JSON object
	 * @throws Exception
	 */
	private String sendData(String command, String... params) throws Exception {
		String resultValue;
		try {
			CommandResponse result = sendDataAndGetJSonObj(new CommandRequest(command,params));
			resultValue = result.getResponse();
			if (!result.isSucceeded()) {
				logger.error(result);
			} 
			else {
				logger.info(result);
			}
		} catch (Exception e) {
			logger.error("Failed to send / receive data", e);
			throw e;
		}
		return resultValue;
	}
	
	/**
	 * 
	@author Bortman Limor
	 * @param command
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private CommandResponse sendDataAndGetJSonObj(CommandRequest request) throws Exception {
		String jsonRequest = JsonParser.toJson(request);
		logger.info("Sending command: " + jsonRequest);
		CommandResponse result = null;
		logger.info("Send Data to " + this.serverHost+':'+this.serverPort);

		try {
			String resultStr = null;
			if ((resultStr = this.tcpClient.sendData(jsonRequest)) == null) {
				throw new Exception("No data recvied from server! pleas check server log!");
			}
			result = JsonParser.fromJson(resultStr, CommandResponse.class);
		} 
		catch (Exception e) {
			logger.error("Failed to send / receive data", e);
			throw e;
		}
		return result;
	}

	public String launch(String launcherActivityClass) throws Exception {
		return sendData("launch",launcherActivityClass);
	}
	
	public String getTextView(int index) throws Exception {
		return sendData("getTextView", Integer.toString(index));
	}

	public String getTextViewIndex(String text) throws Exception {
		return sendData("getTextViewIndex", text);
	}

	public String getCurrentTextViews() throws Exception {
		return sendData("getCurrentTextViews", "a");
	}

	public String getText(int index) throws Exception {
		return sendData("getText", Integer.toString(index));
	}

	public String clickOnMenuItem(String item) throws Exception {
		return sendData("clickOnMenuItem", item);
	}

	public String clickOnView(int index) throws Exception {
		return sendData("clickOnView", Integer.toString(index));
	}

	public String enterText(int index, String text) throws Exception {
		return sendData("enterText", Integer.toString(index), text);
	}

	public String clickOnButton(int index) throws Exception {
		return sendData("clickOnButton", Integer.toString(index));
	}

	public String clickInList(int index) throws Exception {
		return sendData("clickInList", Integer.toString(index));
	}

	public String clearEditText(int index) throws Exception {
		return sendData("clearEditText", Integer.toString(index));
	}

	public String clickOnButtonWithText(String text) throws Exception {
		return sendData("clickOnButtonWithText", text);
	}

	public String clickOnText(String text) throws Exception {
		return sendData("clickOnText", text);
	}

	public String sendKey(int key) throws Exception {
		return sendData("sendKey", Integer.toString(key));
	}

	public String clickOnHardwereButton(HardwareButtons button) throws Exception {
		return sendData("clickOnHardware", button.name());
	}

	public byte[] pull(String fileName) throws Exception {
		CommandResponse response = sendDataAndGetJSonObj(new CommandRequest("pull", fileName));
		logger.info("command pull receved" + JsonParser.toJson(response));
		return ((response.getResponse())).getBytes("UTF-16LE");
	}

	public String push(byte[] data, String newlocalFileName) throws Exception {
		return sendData("createFileInServer", newlocalFileName, Base64.encodeBytes(data, Base64.URL_SAFE), "true");
	}

	public void closeConnection() throws Exception {
		sendData("exit");
	}
	
	public void closeActivity() throws Exception {
		sendData("closeActivity");
	}

	public String isViewVisible(String viewName) throws Exception {
		return sendData("isViewVisible", viewName);
	}

	public String clickInControlByIndex(String controlName, int indexToClickOn) throws Exception {
		return sendData("clickInControlByIndex", controlName, Integer.toString(indexToClickOn));
	}

	public String isViewVisibleByViewName(String viewName) throws Exception {
		return sendData("isViewVisibleByViewName", viewName);
	}

	public String isViewVisibleByViewId(int viewId) throws Exception {
		return sendData("isViewVisibleByViewId", String.valueOf(viewId));
	}
	public String isButtonVisible(Attribute attribute, String value) throws Exception {
		return sendData("isButtonVisible", attribute.name(), value);
	}
	
}
