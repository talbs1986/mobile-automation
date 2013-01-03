package org.topq.mobile.client.impl;

import net.iharder.Base64;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.topq.mobile.common.client.enums.Attribute;
import org.topq.mobile.common.client.enums.ClientProperties;
import org.topq.mobile.common.client.enums.HardwareButtons;
import org.topq.mobile.common.client.interfaces.MobileClientInterface;
import org.topq.mobile.core.AdbController;
import org.topq.mobile.core.device.AbstractAndroidDevice;
import org.topq.mobile.core.device.USBDevice;
import org.topq.mobile.tcp.impl.TcpClient;
import org.topq.mobile.tcp.impl.TcpExecutorServer;
import org.topq.mobile.tcp.impl.TcpTunnel;

import com.android.ddmlib.InstallException;

public class MobileClient implements MobileClientInterface {

	private static Logger logger = Logger.getLogger(MobileClient.class);
	private static final String RESULT_STRING = "RESULT";
	private static final String SERVER_PACKAGE_NAME = "org.topq.mobile.tunnel.application";
	private static final String SERVER_CLASS_NAME = "TcpServerActivty";
	private static final String DEFAULT_EMULATOR_SERIAL = "emulator-5554";

	private ClientConfiguration clientConfig;
	private TcpClient tcpClient;
	private USBDevice device;
	private String deviceSerial = DEFAULT_EMULATOR_SERIAL;
	private static boolean getScreenshots = false;
	private int robotiumServerPort = TcpExecutorServer.DEFAULT_PORT;
	private int clientPort = TcpClient.DEFAULT_PORT;
	private int tunnelPort = TcpTunnel.DEFAULT_TUNNEL_PORT;
	private String tunnelHost = TcpTunnel.DEFAULT_HOSTNAME;
	private String autApkLocation;
	private String robotiumServerApkLocation;
	private String tunnelApkLocation;
	
	
	public MobileClient() throws InstallException, Exception {
		this(null,false,false);
	}
	
	public MobileClient(boolean deployServer, boolean launchServer) throws InstallException, Exception {
		this(null,deployServer,launchServer);
	}
	
	/**
	 * 
	 * @param configFileName- the location of the client config file
	 * @param deployServer - will install the serverApk (if you olrady install it the old version will be delete and the new one will be installed)
	 * @param launchServer - start the server 
	 * @throws Exception
	 */
	public MobileClient(ClientConfiguration clientConfig, boolean deployServer, boolean launchServer) throws InstallException, Exception {
		this.clientConfig = clientConfig;
		this.readConfig();
		this.buildConfig();
		this.device = AdbController.getInstance().waitForDeviceToConnect(this.deviceSerial);
		if (deployServer)
			this.deployServer();
		if (launchServer)
			this.launchServer();
		this.setPortForwarding();
		this.tcpClient = new TcpClient(this.tunnelHost, this.clientPort);
	}

	private void buildConfig() {
		if (this.clientConfig != null) {
			if (!this.clientConfig.isPropertyExist(ClientProperties.SERVER_HOST))
				this.clientConfig.buildProperty(ClientProperties.SERVER_HOST, this.tunnelHost);
			if (!this.clientConfig.isPropertyExist(ClientProperties.SERVER_PORT))
				this.clientConfig.buildProperty(ClientProperties.SERVER_PORT, String.valueOf(this.robotiumServerPort));
			if (!this.clientConfig.isPropertyExist(ClientProperties.TUNNEL_PORT))
				this.clientConfig.buildProperty(ClientProperties.TUNNEL_PORT, String.valueOf(this.tunnelPort));	
		}
	}

	private void deployServer() throws InstallException {
		if (this.autApkLocation != null && !this.autApkLocation.isEmpty()) {
			logger.info("About to deploy Application Under Test on device");
			device.installPackage(this.autApkLocation, true);
		}
		else {
			logger.error("Error , apk location is empty");
		}
		
		if (this.robotiumServerApkLocation != null && !this.robotiumServerApkLocation.isEmpty()) {
			logger.info("About to deploy robotium server on device");
			device.installPackage(this.robotiumServerApkLocation, true);
		}
		else {
			logger.error("Error , apk location is empty");
		}
		
		if (this.tunnelApkLocation != null && !this.tunnelApkLocation.isEmpty()) {
			logger.info("About to deploy Tunnel server on device");
			device.installPackage(this.tunnelApkLocation, true);
		}
		else {
			logger.error("Error , apk location is empty");
		}
	}
	
	private void launchServer() throws Exception {
		logger.info("About to launch server on device");
		if (this.clientConfig != null)
			device.executePackageWithArguments(SERVER_PACKAGE_NAME, SERVER_CLASS_NAME, this.clientConfig.toMap());
		else
			device.executePackageWithArguments(SERVER_PACKAGE_NAME, SERVER_CLASS_NAME, null);
	}

	
	/**
	 * Read all the details from the given properties and populate the object members.
	 * 
	 * @param configProperties
	 */
	private void readConfig() {
		if (this.clientConfig != null && !this.clientConfig.isEmpty()) {
			if (this.clientConfig.isPropertyExist(ClientProperties.SERVER_HOST)) {
				this.robotiumServerPort = Integer.parseInt(this.clientConfig.getProperty(ClientProperties.SERVER_HOST));
			}
			logger.debug("Robotium Server Port is set to" + this.robotiumServerPort);
			
			if (this.clientConfig.isPropertyExist(ClientProperties.TUNNEL_PORT)) {
				this.tunnelPort = Integer.parseInt(this.clientConfig.getProperty(ClientProperties.TUNNEL_PORT));
			}
			logger.debug("Tunnel Port is set to" + this.tunnelPort);
			
			if (this.clientConfig.isPropertyExist(ClientProperties.CLIENT_PORT)) {
				this.clientPort = Integer.parseInt(this.clientConfig.getProperty(ClientProperties.CLIENT_PORT));
			}
			logger.debug("Client Port is set to" + this.clientPort);
	
			if (this.clientConfig.isPropertyExist(ClientProperties.DEVICE_SERIAL)) {
				this.deviceSerial = this.clientConfig.getProperty(ClientProperties.DEVICE_SERIAL);
			}
			logger.debug("Device serial is set to" + this.deviceSerial);
	
			if (this.clientConfig.isPropertyExist(ClientProperties.SERVER_APK_LOCATION)) {
				this.robotiumServerApkLocation = this.clientConfig.getProperty(ClientProperties.SERVER_APK_LOCATION);
			}
			logger.debug("Robotium Server APK location is set to:" + this.robotiumServerApkLocation);
			
			if (this.clientConfig.isPropertyExist(ClientProperties.TUNNEL_APK_LOCATION)) {
				this.tunnelApkLocation = this.clientConfig.getProperty(ClientProperties.TUNNEL_APK_LOCATION);
			}
			logger.debug("Tunnel APK location is set to:" + this.tunnelApkLocation);
	
			if (this.clientConfig.isPropertyExist(ClientProperties.SERVER_HOST)) {
				this.tunnelHost = this.clientConfig.getProperty(ClientProperties.SERVER_HOST);
			}
			logger.debug("Tunnel Host is set to" + this.tunnelHost);
		}
		else {
			logger.debug("Using Default Values");
		}
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
			JSONObject result = sendDataAndGetJSonObj(command, params);

			if (result.isNull(RESULT_STRING)) {
				logger.error("No data recieved from the device");
				return NO_DATA_STRING;
			}
			resultValue = (String) result.get(RESULT_STRING);
			if (resultValue.contains(ERROR_STRING)) {
				logger.error(result);
				device.getScreenshot(null);
			} else if (resultValue.contains(SUCCESS_STRING)) {
				logger.info(result);
			}

		} catch (Exception e) {
			logger.error("Failed to send / receive data", e);
			throw e;
		}
		if (getScreenshots) {
			device.getScreenshot(null);
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
	private JSONObject sendDataAndGetJSonObj(String command, String... params) throws Exception {
		JSONObject jsonobj = new JSONObject();
		jsonobj.put("Command", command);
		jsonobj.put("Params", params);
		logger.info("Sending command: " + jsonobj.toString());
		JSONObject result = null;
		logger.info("Send Data to " + device.getSerialNumber());

		try {
			String resultStr = null;
			if ((resultStr = tcpClient.sendData(jsonobj)) == null) {
				throw new Exception("No data recvied from server! pleas check server log!");
			}
			result = new JSONObject(resultStr);
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
		String response = sendData("getTextViewIndex", text);
		return response;
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
		JSONObject jsonObj = sendDataAndGetJSonObj("pull", fileName);
		logger.info("command pull receved" + jsonObj);
		return ((jsonObj.getString("file"))).getBytes("UTF-16LE");
	}

	public String push(byte[] data, String newlocalFileName) throws Exception {
		String result = sendData("createFileInServer", newlocalFileName, Base64.encodeBytes(data, Base64.URL_SAFE), "true");
		return result;
	}

	public void closeConnection() throws Exception {
		sendData("exit");
	}
	public AbstractAndroidDevice getDevice() throws Exception {
		return device;
	}

	private void setPortForwarding() throws Exception {
		device.setPortForwarding(this.clientPort, this.tunnelPort);
		device.setPortForwarding(this.tunnelPort, this.robotiumServerPort);
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
