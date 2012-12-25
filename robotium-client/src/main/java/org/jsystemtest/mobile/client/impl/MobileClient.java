package org.jsystemtest.mobile.client.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import net.iharder.Base64;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsystemtest.mobile.client.infrastructure.TcpClient;
import org.jsystemtest.mobile.common_mobile.client.enums.HardwareButtons;
import org.jsystemtest.mobile.common_mobile.client.enums.Attribute;
import org.jsystemtest.mobile.common_mobile.client.interfaces.MobileClientInterface;
import org.jsystemtest.mobile.core.AdbController;
import org.jsystemtest.mobile.core.GeneralEnums;
import org.jsystemtest.mobile.core.device.AbstractAndroidDevice;
import org.jsystemtest.mobile.core.device.USBDevice;

import com.android.ddmlib.InstallException;

public class MobileClient implements MobileClientInterface {

	private static Logger logger = Logger.getLogger(MobileClient.class);;

	private static final String SERVER_PACKAGE_NAME = "org.topq.jsystem.mobile";
	private static final String SERVER_CLASS_NAME = "RobotiumServer";
	private static final String SERVER_TEST_NAME = "testMain";
	private TcpClient tcpClient;
	private USBDevice device;
	private static boolean getScreenshots = false;
	private static String pakageName = null;
	private static int port = 4321;
	private static String deviceSerial;
	private static String apkLocation = null;
	private static String host = "localhost";
	private static final String RESULT_STRING = "RESULT";
	private static final String CONFIG_FILE = "/data/conf.txt";
	private static String launcherActivityFullClassname = null;

	public MobileClient(String configFileName) throws Exception {
		this(configFileName, true);
	}
	/**
	 * @param configFileName
	 * @param deployServer
	 * @throws Exception
	 */
	public MobileClient(String configFileName, boolean deployServer) throws Exception {
		this(configFileName, deployServer, true);
	}
	/**
	 * 
	 * @param configFileName- the location of the client config file
	 * @param deployServer - will install the serverApk (if you olrady install it the old version will be delete and the new one will be installed)
	 * @param launchServer - start the server 
	 * @throws Exception
	 */
	public MobileClient(Properties configProperties, boolean deployServer, boolean launchServer) throws InstallException, Exception {
		readConfigFile(configProperties);
//		launchClient(launcherActivityFullClassname);
//		launchServer(deployServer, launchServer, configProperties);
		device = AdbController.getInstance().waitForDeviceToConnect(deviceSerial);
//		setPortForwarding();
//		device.setPortForwarding(4321,4321);
		device.setPortForwarding(6262,4321);
		device.setPortForwarding(8888,6262);
		tcpClient = new TcpClient(host, port);
	}

	public MobileClient(String configFileName, boolean deployServer, boolean launchServer) throws Exception {
		final File configFile = new File(configFileName);
		if (!configFile.exists()) {
			throw new IOException("Configuration file was not found in " + configFileName);
		}

		Properties configProperties = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(configFile);
			configProperties.load(in);

		} finally {
			in.close();
		}

		readConfigFile(configProperties);

		device = AdbController.getInstance().waitForDeviceToConnect(deviceSerial);
//		if (deployServer) {
//			device.installPackage(apkLocation, true);
//			String serverConfFile = configProperties.getProperty("ServerConfFile");
//			logger.debug("Server Conf File:" + serverConfFile);
//			device.pushFileToDevice(CONFIG_FILE, serverConfFile);
//		}
//		if (launchServer) {
//			logger.info("Start server on device");
//			device.startServer(pakageName, launcherActivityFullClassname);
//		}
		setPortForwarding();	
		tcpClient = new TcpClient(host, port);
	}


		private void launchServer(boolean deployServer, boolean launchServer, Properties configProperties) throws InstallException, Exception {
		if (deployServer) {
			logger.info("About to deploy server on device");
			device.installPackage(apkLocation, true);
			String serverConfFile = configProperties.getProperty("serverConfFile");
			logger.debug("Server Conf File:" + serverConfFile);
			device.pushFileToDevice(CONFIG_FILE, serverConfFile);
		}
		if (launchServer) {
			logger.info("About to launch server on device");
			device.runTestOnDevice(SERVER_PACKAGE_NAME, SERVER_CLASS_NAME, SERVER_TEST_NAME);
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
	public String sendData(String command, String... params) throws Exception {
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
	public JSONObject sendDataAndGetJSonObj(String command, String... params) throws Exception {
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
		} catch (Exception e) {
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
		device.setPortForwarding(port, GeneralEnums.SERVERPORT);
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
	/*####################################################Privet################*/
	/**
	 * Read all the details from the given properties and populate the object members.
	 * 
	 * @param configProperties
	 */
	private void readConfigFile(final Properties configProperties) {
		if (isPropertyExist(configProperties, "port")) {
			port = Integer.parseInt(configProperties.getProperty("port"));
		}
		logger.debug("Port is set to" + port);

		if (!isPropertyExist(configProperties, "deviceSerial")) {
			throw new IllegalStateException("Device serial was not specified in config file");
		}
		deviceSerial = configProperties.getProperty("deviceSerial");

		logger.debug("Device serial is set to" + deviceSerial);

		if (isPropertyExist(configProperties, "apkLocation")) {
			apkLocation = configProperties.getProperty("apkLocation");
		}
		logger.debug("APK location is set to:" + apkLocation);

		if (isPropertyExist(configProperties, "host")) {
			host = configProperties.getProperty("host");
		}
		logger.debug("Host is set to" + host);
	}
	/**
	 * Check if the property with the specified key exists in the specified properties object.
	 * 
	 * @param configProperties
	 * @param key
	 * @return true if and only if the property with the specified key exists.
	 */
	private boolean isPropertyExist(Properties configProperties, String key) {
		final String value = configProperties.getProperty(key);
		return value != null && !value.isEmpty();
	}
	
	
	public static void main(String [ ] args) {
		try {
			Properties prop = new Properties();
			prop.put("port", "8888");
			prop.put("host", "localhost");
			prop.put("deviceSerial", "emulator-5554");
			MobileClientInterface x = new MobileClient(prop,false,false);
			x.launch("com.tal.example.loginapp.LoginActivity");
//			x.launch("org.topq.jsystem.mobile.RobotiumClientActivity");
			x.enterText(0, "tal@tal.com");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
		
	}

	
}
