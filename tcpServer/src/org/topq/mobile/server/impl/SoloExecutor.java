package org.topq.mobile.server.impl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.topq.mobile.common.datamodel.CommandRequest;
import org.topq.mobile.common.datamodel.CommandResponse;
import org.topq.mobile.common.server.utils.JsonParser;
import org.topq.mobile.server.interfaces.ISoloProvider;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class SoloExecutor {

	private static final String TAG = "SoloExecutor";
	private Instrumentation instrumentation;
	private Solo solo;
	private final ISoloProvider soloProvider;

	public SoloExecutor(final ISoloProvider soloProvider, Instrumentation instrumentation) {
		super();
		this.soloProvider = soloProvider;
		this.instrumentation = instrumentation;
	}

	public String execute(final String data) throws Exception {
//		ScriptParser parser;
//		JSONObject result = new JSONObject();
//		parser = new ScriptParser(data);
		CommandRequest request = JsonParser.fromJson(data, CommandRequest.class);
		CommandResponse response = new CommandResponse();
//		for (CommandParser command : parser.getCommands()) {
			String commandStr = request.getCommand();
			if (commandStr.equals("enterText")) {
				response = enterText(request.getParams());
			} 
			else if (commandStr.equals("isButtonVisible")) {
				response = isButtonVisible(request.getParams());
			} 
			else if (commandStr.equals("clickInControlByIndex")) {
				response = clickInControlByIndex(request.getParams());
			} 
			else if (commandStr.equals("isViewVisibleByViewName")) {
				response = isViewVisibleByViewName(request.getParams());
			} 
			else if (commandStr.equals("isViewVisibleByViewId")) {
				response = isViewVisibleByViewId(request.getParams());
			} 
			else if (commandStr.equals("clickOnButton")) {
				response = clickOnButton(request.getParams());
			} 
			else if (commandStr.equals("launch")) {
				response = launch();
			} 
			else if (commandStr.equals("clickInList")) {
				response = clickInList(request.getParams());
			} 
			else if (commandStr.equals("clearEditText")) {
				response = clearEditText(request.getParams());
			} 
			else if (commandStr.equals("clickOnButtonWithText")) {
				response = clickOnButtonWithText(request.getParams());
			} 
			else if (commandStr.equals("clickOnView")) {
				response = clickOnView(request.getParams());
			} 
			else if (commandStr.equals("clickOnText")) {
				response = clickOnText(request.getParams());
			} 
			else if (commandStr.equals("sendKey")) {
				response = sendKey(request.getParams());
			} 
			else if (commandStr.equals("clickOnMenuItem")) {
				response = clickOnMenuItem(request.getParams());
			} 
			else if (commandStr.equals("getText")) {
				response = getText(request.getParams());
			} 
			else if (commandStr.equals("getTextViewIndex")) {
				response = getTextViewIndex(request.getParams());
			} 
			else if (commandStr.equals("getTextView")) {
				response = getTextView(request.getParams());
			} 
			else if (commandStr.equals("getCurrentTextViews")) {
				response = getCurrentTextViews(request.getParams());
			} 
			else if (commandStr.equals("clickOnHardware")) {
				response = clickOnHardware(request.getParams());
			} 
			else if (commandStr.equals("createFileInServer")) {
				response = createFileInServer(request.getParams());
			} 
			else if (commandStr.equals("pull")) {
				response = pull(request.getParams());
			} 
			else if (commandStr.equals("closeActivity")) {
				response = closeActivity();
			} 
			else if (commandStr.equals("activateIntent")) {
				response = activateIntent(request.getParams());
			}
//		}
			response.setOriginalCommand(request.getCommand());
			response.setParams(request.getParams());
			String result = JsonParser.toJson(response);
		Log.i(TAG,"The Result is:"+result);
		return result;

	}

	private CommandResponse isViewVisibleByViewId(String[] arguments) {
		CommandResponse result = new CommandResponse();
		String command = "the command isViewVisible";
		try {
			int viewId = Integer.parseInt(arguments[0]);
			command += "(" + viewId + ")";
			View view = this.solo.getView(viewId);
			if (view != null) {
				if (view.isShown()) {
					result.setResponse("view with ID: " + viewId + " is visible");
					result.setSucceeded(true);
				} 
				else {
					result.setResponse("view with ID: " + viewId + " is not visible");
					result.setSucceeded(true);
				}
			} 
			else {
				result.setResponse("view with ID: " + viewId + " is not found ");
				result.setSucceeded(false);
			}
		} 
		catch (Throwable e) {
			result.setResponse(command + "failed due to " + e.getMessage());
			result.setSucceeded(false);
			Log.d(TAG, result.getResponse());
		}
		return result;
	}

	private CommandResponse isViewVisibleByViewName(String[] arguments) {
		CommandResponse result = new CommandResponse();
		String command = "the command isViewVisible";
		try {
			String viewName = arguments[0];
			command += "(" + viewName + ")";
			View view = findViewByName(viewName);
			if (view.isShown()) {
				result.setResponse("view: " + viewName + " is visible");
				result.setSucceeded(true);
			} 
			else {
				result.setResponse("view: " + viewName + " is not visible");
				result.setSucceeded(true);
			}
		} 
		catch (Throwable e) {
			result.setResponse(command + "failed due to " + e.getMessage());
			Log.d(TAG, result.getResponse());
		}
		return result;
	}

	private CommandResponse activateIntent(String[] arguments) {
		String command = null;
		CommandResponse result = new CommandResponse();
		try {
			command = "the command  activateIntent(" + arguments[0] + " " + arguments[1] + " " + arguments[2] + " "
					+ arguments[3] + " " + arguments[4] + ")";

			/*
			 * if (arguments[0].equals("ACTION_VIEW")) { Intent webIntent = new Intent(Intent.ACTION_VIEW,
			 * Uri.parse(arguments[1])); Log.d(TAG, "Sending intent to " + solo.getClass().getSimpleName());
			 * solo.getCurrentActivity().startActivityForResult(webIntent, 1); }else if
			 * (arguments[0].equals("com.greenroad.PlayTrip")) {
			 */
			Log.d(TAG, "Sending intent");
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(arguments[0]);
			for (int i = 1; i < arguments.length; i = i + 2) {
				broadcastIntent.putExtra(/*
										 * solo.getCurrentActivity(). getCallingPackage()+
										 */arguments[i], arguments[i + 1]);
			}
			this.solo.getCurrentActivity().sendBroadcast(broadcastIntent);
			result.setResponse("Activate Intent Succeeded");
			result.setSucceeded(true);
			// }
			/*
			 * else if (arguments[0].equals("ACTION_SEND")) { Intent sendIntent = new Intent();
			 * sendIntent.setAction(arguments[0]); for(int i = 1;i<arguments.length;i=i+2){ Log.i(TAG, "check: " +
			 * arguments[i] + " " + arguments[i+1]); sendIntent.putExtra(solo.getCurrentActivity ().getCallingPackage()+
			 * "."+arguments[i],android.content.Intent.EXTRA_TEXT, arguments[i+1]); }
			 * solo.getCurrentActivity().startActivity(sendIntent); }
			 */
		} 
		catch (Exception e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse pull(String[] arguments) {
		String command = "the command  pull";
		String allText = "";
		CommandResponse result = new CommandResponse();
		DataInputStream in = null;
		FileInputStream fstream = null;
		try {
			command += "(" + arguments[0] + ")";
			fstream = new FileInputStream(arguments[0]);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) {
				allText += line;
			}
			result.setResponse(allText);
			result.setSucceeded(true);
			in.close();
			fstream.close();
		} 
		catch (Exception e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse createFileInServer(String[] arguments) {
		String command = "the command  createFileInServer";
		CommandResponse result = new CommandResponse();
		try {
			if (Boolean.parseBoolean(arguments[2])) {
				byte[] data = Base64.decode(arguments[1], Base64.URL_SAFE);
				command += "(" + arguments[0] + ", " + data + ")";
				command += "(" + arguments[0] + ", " + data + ")";
				FileOutputStream fos = new FileOutputStream(arguments[0]);
				fos.write(data);
				fos.close();
			} 
			else {
				command += "(" + arguments[0] + ", " + arguments[1] + ")";
				FileWriter out = new FileWriter(arguments[0]);
				out.write(arguments[1]);
				out.close();
			}
			result.setResponse(command);
			result.setSucceeded(true);
			Log.d(TAG, "run the command:" + command);
		} 
		catch (Exception e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse getCurrentTextViews(String[] arguments) {
		String command = "the command  getCurrentTextViews";
		CommandResponse result = new CommandResponse();
		StringBuilder response = new StringBuilder();
		try {
			command += "(" + arguments[0] + ")";
			List<TextView> textViews = this.solo.getCurrentTextViews(null);
			for (int i = 0; i < textViews.size(); i++) {
				response.append(i).append(",").append(textViews.get(i).getText().toString()).append(";");
			}
			result.setResponse(command+",Response: " + response.toString());
			result.setSucceeded(true);
		} 
		catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse getTextView(String[] arguments) {
		String command = "the command  getTextView";
		CommandResponse result = new CommandResponse();
		String response = "";
		try {
			command += "(" + arguments[0] + ")";
			response = this.solo.getCurrentTextViews(null).get(Integer.parseInt(arguments[0])).getText().toString();
			result.setResponse(command + ",Response: " + response);
			result.setSucceeded(true);
		} 
		catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse getTextViewIndex(String[] arguments) {
		String command = "the command  getTextViewIndex";
		CommandResponse result = new CommandResponse();
		StringBuilder response = new StringBuilder();
		try {
			command += "(" + arguments[0] + ")";
			List<TextView> textViews = this.solo.getCurrentTextViews(null);
			for (int i = 0; i < textViews.size(); i++) {
				if (arguments[0].trim().equals(textViews.get(i).getText().toString())) {
					response.append(i).append(";");
				}
			}
			result.setResponse(command + ",Response: " + response.toString());
			result.setSucceeded(true);
		} 
		catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse getText(String[] arguments) {
		String command = "the command  getText";
		CommandResponse result = new CommandResponse();
		String response = "";
		try {
			command += "(" + arguments[0] + ")";
			response = this.solo.getText(Integer.parseInt(arguments[0])).getText().toString();
			result.setResponse(command + ",Response: " + response);
			result.setSucceeded(true);
		} 
		catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse clickOnMenuItem(String[] arguments) {
		String command = "the command  clickOnMenuItem";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + arguments[0] + ")";
			this.solo.clickOnMenuItem(arguments[0]);
			result.setResponse(command);
			result.setSucceeded(true);
		} 
		catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse sendKey(String[] arguments) {
		String command = "the command  sendKey";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + arguments[0] + ")";
			this.solo.sendKey(Integer.parseInt(arguments[0]));
			result.setResponse(command);
			result.setSucceeded(true);
		} 
		catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse clickInControlByIndex(String[] commandParameters) throws Exception {
		String command = "The command clickInControlByIndex";
		CommandResponse result = new CommandResponse();
		try {
			int controlId = Integer.parseInt(commandParameters[0]);
			int indexToClickOn = Integer.parseInt(commandParameters[1]);
			command += "(controlId: " + controlId + ")";
			command += "(indexToClickOn: " + indexToClickOn + ")";
			View control = this.solo.getView(controlId);
			if (control != null) {
				if (indexToClickOn < control.getTouchables().size()) {
					clickOnView(control.getTouchables().get(indexToClickOn));
					result.setResponse(command);
					result.setSucceeded(true);
				} 
				else {
					result.setResponse(command + "failed due to: index to click in control is out of bounds. control touchables: "
							+ control.getTouchables().size());
					result.setSucceeded(false);
				}
			} 
			else {
				result.setResponse(command + "failed due to failed to find control with id: " + controlId);
				result.setSucceeded(false);
			}
		} 
		catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * This method will search the requested view / control by its name in the currentViews <br>
	 * 
	 * @param viewName
	 * @return View<br>
	 * @throws Exception
	 *             in case view was not found<br>
	 */
	private View findViewByName(String viewName) throws Exception {
		ArrayList<View> currentViews = this.solo.getCurrentViews();
		for (View view : currentViews) {
			if (view.getClass().getName().contains(viewName)) {
				return view;
			}
		}
		throw new Exception("View : " + viewName + " was not found in current views ");
	}

	/**
	 * This method is expecting viewId as argument in jsonArray<br>
	 * I am not sure how this can be known in advance<Br>
	 * There is no method that retrieves the viewId back to client<br>
	 * I think it is best to use the view name and find by method written<br>
	 * Will change soon.
	 * 
	 * @param arguments
	 *            expecting viewId in arg in index 0;<br>
	 * @return
	 */
	private CommandResponse clickOnView(String[] arguments) {
		String command = "the command  clickOnView";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + arguments[0] + ")";
			View view = this.solo.getView(Integer.parseInt(arguments[0]));
			clickOnView(view);
			result.setResponse(command);
			result.setSucceeded(true);
		} 
		catch (Exception e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse clickOnButtonWithText(String[] arguments) {
		String command = "the command  clickOnButton";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + arguments[0] + ")";
			this.solo.clickOnButton(arguments[0]);
			result.setResponse(command);
			result.setSucceeded(true);
		}
		catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse clearEditText(String[] arguments) {
		String command = "the command  clearEditText";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + arguments[0] + ")";
			this.solo.clearEditText(Integer.parseInt(arguments[0]));
			result.setResponse(command);
			result.setSucceeded(true);
		} 
		catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse isButtonVisible(String[] arguments) {
		String command = "the command isButtonVisible ";
		CommandResponse result = new CommandResponse();
		boolean isVisible = false;
		try {
			String searchButtonByKey = arguments[0];
			command += "(findButtonBy: " + arguments[0] + ")";
			if (searchButtonByKey.equalsIgnoreCase("text")) {
				String searchButtonByTextValue = arguments[1];
				command += "(Value: " + searchButtonByTextValue + ")";
				isVisible = isButtonVisibleByText(searchButtonByTextValue);
			} 
			else if (searchButtonByKey.equalsIgnoreCase("id")) {
				int searchButtonByIntValue = Integer.parseInt(arguments[1]);
				command += "(Value: " + searchButtonByIntValue + ")";
				isVisible = isButtonVisibleById(searchButtonByIntValue);
			}
		} 
		catch (Throwable e) {
			return handleException(command, e);
		}
		if (isVisible) {
			result.setResponse(command + " is visible");
			result.setSucceeded(true);
		} 
		else {
			result.setResponse(command + " is not visible");
			result.setSucceeded(true);
		}
		return result;
	}

	private boolean isButtonVisibleByText(String buttonText) throws Exception {
		Button button = this.solo.getButton(buttonText);
		if (button != null) {
			return button.isShown();
		} 
		else {
			throw new Exception("Button with text: " + buttonText + " was not found");
		}
	}

	private boolean isButtonVisibleById(int buttonId) throws Exception {
		ArrayList<Button> currentButtons = this.solo.getCurrentButtons();
		for (Button button : currentButtons) {
			if (button.getId() == buttonId){
				if (button.isShown()){
					return true;
				}
			}
		}
		return false;
	}

	private CommandResponse clickInList(String[] arguments) {
		String command = "the command  clickInList(";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + arguments[0] + ")";
			this.solo.clickInList(Integer.parseInt(arguments[0]));
			result.setResponse(command);
			result.setSucceeded(true);
		}
		catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse clickOnButton(String[] params) {
		String command = "the command  clickOnButton";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + params[0] + ")";
			this.solo.clickOnButton(Integer.parseInt(params[0]));
			result.setResponse(command);
			result.setSucceeded(true);
		} 
		catch (Throwable e) {
			return handleException(command, e);
		}
		return result;
	}

	private CommandResponse enterText(String[] params) {
		String command = "the command  enterText";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + params[0] + ","+params[1]+")";
			this.solo.enterText(Integer.parseInt(params[0]), params[1]);
			result.setResponse(command);
			result.setSucceeded(true);
		} 
		catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;

	}

	private CommandResponse clickOnText(String[] params) {
		String command = "the command clickOnText";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + params[0] + ")";
			this.solo.clickOnText(params[0]);
			result.setResponse(command);
			result.setSucceeded(true);
		}
		catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;

	}

	private CommandResponse clickOnHardware(String[] keyString) {
		String command = "the command clickOnHardware";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + keyString[0] + ")";
			int key = (keyString[0] == "HOME") ? KeyEvent.KEYCODE_HOME : KeyEvent.KEYCODE_BACK;
			this.instrumentation.sendKeyDownUpSync(key);
			result.setResponse("click on hardware");
			result.setSucceeded(true);
		} 
		catch (Throwable e) {
			result =  handleException(command, e);
		}
		return result;
	}

	private CommandResponse launch() {
		Log.i(TAG, "Robotium: About to launch application");
		CommandResponse result = new CommandResponse();
		String command = "the command  launch";
		try {
			this.solo = this.soloProvider.getSolo();
			result.setResponse(command);
			result.setSucceeded(true);
		} 
		catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse closeActivity() {
		Log.i(TAG, "Robotium: About to close application");
		CommandResponse result = new CommandResponse();
		ArrayList<Activity> activities = this.solo.getAllOpenedActivities();
		for (Activity activity : activities) {
			activity.finish();
		}
		// solo.getCurrentActivity().finish();
		result.setResponse("close activity");
		result.setSucceeded(true);
		return result;
	}

	private CommandResponse handleException(final String command,Throwable e) {
		CommandResponse result = new CommandResponse();
		result.setResponse(command+" failed due to " + e.getMessage());
		Log.e(TAG,result.getResponse());
		return result;
	}

	private void clickOnView(View view) throws Exception {
		if (view.isShown()) {
			this.solo.clickOnView(view);
		} 
		else {
			throw new Exception("clickOnView FAILED view: " + view.getClass().getSimpleName() + " is not shown");
		}
	}
}
