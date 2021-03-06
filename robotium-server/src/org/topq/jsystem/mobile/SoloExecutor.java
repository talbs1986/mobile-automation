package org.topq.jsystem.mobile;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	private static final String SUCCESS_STRING = "SUCCESS:";
	private static final String ERROR_STRING = "ERROR:";
	private static final String RESULT_STRING = "RESULT";
	private Instrumentation instrumentation;
	private Solo solo;
	private final ISoloProvider soloProvider;

	public SoloExecutor(final ISoloProvider soloProvider, Instrumentation instrumentation) {
		super();
		this.soloProvider = soloProvider;
		this.instrumentation = instrumentation;
	}

	public JSONObject execute(final String data) throws Exception {
		ScriptParser parser;
		JSONObject result = new JSONObject();
		parser = new ScriptParser(data);
		for (CommandParser command : parser.getCommands()) {
			if (command.getCommand().equals("enterText")) {
				result.put(RESULT_STRING, enterText(command.getArguments()));
			} else if (command.getCommand().equals("isButtonVisible")) {
				result.put(RESULT_STRING, isButtonVisible(command.getArguments()));
			} else if (command.getCommand().equals("clickInControlByIndex")) {
				result.put(RESULT_STRING, clickInControlByIndex(command.getArguments()));
			} else if (command.getCommand().equals("isViewVisibleByViewName")) {
				result.put(RESULT_STRING, isViewVisibleByViewName(command.getArguments()));
			} else if (command.getCommand().equals("isViewVisibleByViewId")) {
				result.put(RESULT_STRING, isViewVisibleByViewId(command.getArguments()));
			} else if (command.getCommand().equals("clickOnButton")) {
				result.put(RESULT_STRING, clickOnButton(command.getArguments()));
			} else if (command.getCommand().equals("launch")) {
				result.put(RESULT_STRING, launch());
			} else if (command.getCommand().equals("clickInList")) {
				result.put(RESULT_STRING, clickInList(command.getArguments()));
			} else if (command.getCommand().equals("clearEditText")) {
				result.put(RESULT_STRING, clearEditText(command.getArguments()));
			} else if (command.getCommand().equals("clickOnButtonWithText")) {
				result.put(RESULT_STRING, clickOnButtonWithText(command.getArguments()));
			} else if (command.getCommand().equals("clickOnView")) {
				result.put(RESULT_STRING, clickOnView(command.getArguments()));
			} else if (command.getCommand().equals("clickOnText")) {
				result.put(RESULT_STRING, clickOnText(command.getArguments()));
			} else if (command.getCommand().equals("sendKey")) {
				result.put(RESULT_STRING, sendKey(command.getArguments()));
			} else if (command.getCommand().equals("clickOnMenuItem")) {
				result.put(RESULT_STRING, clickOnMenuItem(command.getArguments()));
			} else if (command.getCommand().equals("getText")) {
				result.put(RESULT_STRING, getText(command.getArguments()));
			} else if (command.getCommand().equals("getTextViewIndex")) {
				result.put(RESULT_STRING, getTextViewIndex(command.getArguments()));
			} else if (command.getCommand().equals("getTextView")) {
				result.put(RESULT_STRING, getTextView(command.getArguments()));
			} else if (command.getCommand().equals("getCurrentTextViews")) {
				result.put(RESULT_STRING, getCurrentTextViews(command.getArguments()));
			} else if (command.getCommand().equals("clickOnHardware")) {
				result.put(RESULT_STRING, clickOnHardware(command.getArguments()));
			} else if (command.getCommand().equals("createFileInServer")) {
				result.put(RESULT_STRING, createFileInServer(command.getArguments()));
			} else if (command.getCommand().equals("pull")) {
				return pull(command.getArguments());
			} else if (command.getCommand().equals("closeActivity")) {
				result.put(RESULT_STRING, closeActivity());
			} else if (command.getCommand().equals("activateIntent")) {
				result.put(RESULT_STRING, activateIntent(command.getArguments()));
			}
		}
		Log.i(TAG,"The Result is:"+result);
		return result;

	}

	private String isViewVisibleByViewId(JSONArray arguments) {
		String result;
		String command = "the command isViewVisible";
		try {
			int viewId = arguments.getInt(0);
			command += "(" + viewId + ")";
			View view = solo.getView(viewId);
			if (view != null) {
				if (view.isShown()) {
					result = SUCCESS_STRING + " view with ID: " + viewId + " is visible";
				} else {
					result = SUCCESS_STRING + " view with ID: " + viewId + " is not visible";
				}
			} else {
				result = ERROR_STRING + " view with ID: " + viewId + " is not found ";
			}
		} catch (Throwable e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;
		}
		return result;
	}

	private String isViewVisibleByViewName(JSONArray arguments) {
		String result;
		String command = "the command isViewVisible";
		try {
			String viewName = arguments.getString(0);
			command += "(" + viewName + ")";
			View view = findViewByName(viewName);
			if (view.isShown()) {
				result = SUCCESS_STRING + " view: " + viewName + " is visible";
			} else {
				result = SUCCESS_STRING + " view: " + viewName + " is not visible";
			}
		} catch (Throwable e) {
			String error = ERROR_STRING + command + "failed due to " + e.getMessage();
			Log.d(TAG, error);
			return error;
		}
		return result;
	}

	private String activateIntent(JSONArray arguments) {
		String command = null;
		try {
			command = "the command  activateIntent(" + arguments.getString(0) + " " + arguments.getString(1) + " " + arguments.getString(2) + " "
					+ arguments.getString(3) + " " + arguments.getString(4) + ")";

			/*
			 * if (arguments[0].equals("ACTION_VIEW")) { Intent webIntent = new Intent(Intent.ACTION_VIEW,
			 * Uri.parse(arguments[1])); Log.d(TAG, "Sending intent to " + solo.getClass().getSimpleName());
			 * solo.getCurrentActivity().startActivityForResult(webIntent, 1); }else if
			 * (arguments[0].equals("com.greenroad.PlayTrip")) {
			 */
			Log.d(TAG, "Sending intent to com.greenroad.PlayTrip");
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(arguments.getString(0));
			for (int i = 1; i < arguments.length(); i = i + 2) {

				broadcastIntent.putExtra(/*
										 * solo.getCurrentActivity(). getCallingPackage()+
										 */arguments.getString(i), arguments.getString(i + 1));
			}
			solo.getCurrentActivity().sendBroadcast(broadcastIntent);

			// }
			/*
			 * else if (arguments[0].equals("ACTION_SEND")) { Intent sendIntent = new Intent();
			 * sendIntent.setAction(arguments[0]); for(int i = 1;i<arguments.length;i=i+2){ Log.i(TAG, "check: " +
			 * arguments[i] + " " + arguments[i+1]); sendIntent.putExtra(solo.getCurrentActivity ().getCallingPackage()+
			 * "."+arguments[i],android.content.Intent.EXTRA_TEXT, arguments[i+1]); }
			 * solo.getCurrentActivity().startActivity(sendIntent); }
			 */
		} catch (JSONException e) {
			handleException(command, e);
			return null;
		}
		return SUCCESS_STRING + command;
	}

	private JSONObject pull(JSONArray arguments) {
		String command = "the command  pull";
		String allText = "";
		JSONObject result = null;
		DataInputStream in = null;
		FileInputStream fstream = null;
		try {
			command += "(" + arguments.getString(0) + ")";
			fstream = new FileInputStream(arguments.getString(0));
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) {
				allText += line;
			}
			result = new JSONObject();
			result.put("file", allText);
			in.close();
			fstream.close();
		} catch (Exception e) {
			handleException(command, e);
			return null;
		}
		return result;
	}

	private String createFileInServer(JSONArray arguments) {
		String command = "the command  createFileInServer";
		try {
			if (arguments.getBoolean(2)) {
				byte[] data = Base64.decode(arguments.getString(1), Base64.URL_SAFE);
				command += "(" + arguments.getString(0) + ", " + data + ")";
				command += "(" + arguments.getString(0) + ", " + data + ")";
				FileOutputStream fos = new FileOutputStream(arguments.getString(0));
				fos.write(data);
				fos.close();
			} else {
				command += "(" + arguments.getString(0) + ", " + arguments.getString(1) + ")";
				FileWriter out = new FileWriter(arguments.getString(0));
				out.write(arguments.getString(1));
				out.close();
			}
			Log.d(TAG, "run the command:" + command);
		} catch (Exception e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command;
	}

	private String getCurrentTextViews(JSONArray arguments) {
		String command = "the command  getCurrentTextViews";
		StringBuilder response = new StringBuilder();
		try {

			command += "(" + arguments.getString(0) + ")";

			List<TextView> textViews = solo.getCurrentTextViews(null);
			for (int i = 0; i < textViews.size(); i++) {
				response.append(i).append(",").append(textViews.get(i).getText().toString()).append(";");
			}
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command + ",Response: " + response.toString();
	}

	private String getTextView(JSONArray arguments) {
		String command = "the command  getTextView";
		String response = "";
		try {
			command += "(" + arguments.getInt(0) + ")";
			response = solo.getCurrentTextViews(null).get(arguments.getInt(0)).getText().toString();
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command + ",Response: " + response;
	}

	private String getTextViewIndex(JSONArray arguments) {
		String command = "the command  getTextViewIndex";
		StringBuilder response = new StringBuilder();
		try {
			command += "(" + arguments.getString(0) + ")";
			List<TextView> textViews = solo.getCurrentTextViews(null);
			for (int i = 0; i < textViews.size(); i++) {
				if (arguments.getString(0).trim().equals(textViews.get(i).getText().toString())) {
					response.append(i).append(";");
				}
			}
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command + ",Response: " + response.toString();
	}

	private String getText(JSONArray arguments) {
		String command = "the command  getText";
		String response = "";
		try {
			command += "(" + arguments.getString(0) + ")";
			response = solo.getText(arguments.getInt(0)).getText().toString();
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command + ",Response: " + response;
	}

	private String clickOnMenuItem(JSONArray arguments) {
		String command = "the command  clickOnMenuItem";
		try {
			command += "(" + arguments.getString(0) + ")";
			solo.clickOnMenuItem(arguments.getString(0));
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command;
	}

	private String sendKey(JSONArray arguments) {
		String command = "the command  sendKey";
		try {
			command += "(" + arguments.getString(0) + ")";
			solo.sendKey(arguments.getInt(0));
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command;
	}

	private String clickInControlByIndex(JSONArray commandParameters) throws Exception {
		String command = "The command clickInControlByIndex";
		try {
			int controlId = commandParameters.getInt(0);
			int indexToClickOn = commandParameters.getInt(1);
			command += "(controlId: " + controlId + ")";
			command += "(indexToClickOn: " + indexToClickOn + ")";
			View control = solo.getView(controlId);
			if (control != null) {
				if (indexToClickOn < control.getTouchables().size()) {
					clickOnView(control.getTouchables().get(indexToClickOn));
				} else {
					return ERROR_STRING + command + "failed due to: index to click in control is out of bounds. control touchables: "
							+ control.getTouchables().size();
				}
			} else {
				return ERROR_STRING + command + "failed due to failed to find control with id: " + controlId;
			}
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command;
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
		ArrayList<View> currentViews = solo.getCurrentViews();
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
	private String clickOnView(JSONArray arguments) {
		String command = "the command  clickOnView";
		try {
			command += "(" + arguments.getString(0) + ")";
			View view = solo.getView(arguments.getInt(0));
			clickOnView(view);
		} catch (Exception e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command;
	}

	private String clickOnButtonWithText(JSONArray arguments) {
		String command = "the command  clickOnButton";
		try {
			command += "(" + arguments.getString(0) + ")";
			solo.clickOnButton(arguments.getString(0));
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command;
	}

	private String clearEditText(JSONArray arguments) {
		String command = "the command  clearEditText";
		try {
			command += "(" + arguments.getString(0) + ")";
			solo.clearEditText(arguments.getInt(0));
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command;
	}

	private String isButtonVisible(JSONArray arguments) {
		String command = "the command isButtonVisible ";
		boolean isVisible = false;
		String result;
		try {
			String searchButtonByKey = arguments.getString(0);
			command += "(findButtonBy: " + arguments.getString(0) + ")";
			if (searchButtonByKey.equalsIgnoreCase("text")) {
				String searchButtonByTextValue = arguments.getString(1);
				command += "(Value: " + searchButtonByTextValue + ")";
				isVisible = isButtonVisibleByText(searchButtonByTextValue);
			} else if (searchButtonByKey.equalsIgnoreCase("id")) {
				int searchButtonByIntValue = arguments.getInt(1);
				command += "(Value: " + searchButtonByIntValue + ")";
				isVisible = isButtonVisibleById(searchButtonByIntValue);
			}
		} catch (Throwable e) {
			return handleException(command, e);
		}
		if (isVisible) {
			result = SUCCESS_STRING + command + " is visible";
		} else {
			result = SUCCESS_STRING + command + " is not visible";
		}

		return result;
	}

	private boolean isButtonVisibleByText(String buttonText) throws Exception {
		Button button = solo.getButton(buttonText);
		if (button != null) {
			return button.isShown();
		} else {
			throw new Exception("Button with text: " + buttonText + " was not found");
		}
	}

	private boolean isButtonVisibleById(int buttonId) throws Exception {
		ArrayList<Button> currentButtons = solo.getCurrentButtons();
		for (Button button : currentButtons) {
			if (button.getId() == buttonId){
				if (button.isShown()){
					return true;
				}
			}
		}
		return false;
	}

	private String clickInList(JSONArray arguments) {
		String command = "the command  clickInList(";
		try {
			command += "(" + arguments.getString(0) + ")";
			solo.clickInList(arguments.getInt(0));
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command;

	}

	private String clickOnButton(JSONArray params) {
		String command = "the command  clickOnButton";
		try {
			command += "(" + params.getString(0) + ")";
			solo.clickOnButton(params.getInt(0));
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command;
	}

	private String enterText(JSONArray params) {
		String command = "the command  enterText";
		try {
			command += "(" + params.getString(0) + ","+params.getString(1)+")";
			solo.enterText(params.getInt(0), params.getString(1));
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command;

	}

	private String clickOnText(JSONArray params) {
		String command = "the command clickOnText";
		try {
			command += "(" + params.getString(0) + ")";
			solo.clickOnText(params.getString(0));

		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command;

	}

	private String clickOnHardware(JSONArray keyString) {
		String command = "the command clickOnHardware";
		try {
			command += "(" + keyString.getString(0) + ")";
			int key = (keyString.getString(0) == "HOME") ? KeyEvent.KEYCODE_HOME : KeyEvent.KEYCODE_BACK;
			instrumentation.sendKeyDownUpSync(key);
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + "click on hardware";
	}

	private String launch() {
		Log.i(TAG, "Robotium: About to launch application");
		String command = "the command  launch";
		try {
			solo = soloProvider.getSolo();
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return SUCCESS_STRING + command;
	}

	private String closeActivity() {
		Log.i(TAG, "Robotium: About to close application");
		ArrayList<Activity> activities = solo.getAllOpenedActivities();
		for (Activity activity : activities) {
			activity.finish();
		}
		// solo.getCurrentActivity().finish();
		return SUCCESS_STRING + "close activity";
	}

	private String handleException(final String command,Throwable e) {
		String error =ERROR_STRING + command+" failed due to " + e.getMessage();
		Log.e(TAG,error);
		return error;
	}

	private void clickOnView(View view) throws Exception {
		if (view.isShown()) {
			solo.clickOnView(view);
		} else {
			throw new Exception("clickOnView FAILED view: " + view.getClass().getSimpleName() + " is not shown");
		}
	}
}
