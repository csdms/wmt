/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.control;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;

import edu.colorado.csdms.wmt.client.Constants;
import edu.colorado.csdms.wmt.client.data.ComponentJSO;
import edu.colorado.csdms.wmt.client.data.ComponentListJSO;
import edu.colorado.csdms.wmt.client.data.LabelJSO;
import edu.colorado.csdms.wmt.client.data.ModelJSO;
import edu.colorado.csdms.wmt.client.data.ModelListJSO;
import edu.colorado.csdms.wmt.client.data.ModelMetadataJSO;
import edu.colorado.csdms.wmt.client.ui.ComponentSelectionMenu;
import edu.colorado.csdms.wmt.client.ui.handler.AddNewUserHandler;
import edu.colorado.csdms.wmt.client.ui.handler.DialogCancelHandler;
import edu.colorado.csdms.wmt.client.ui.widgets.NewUserDialogBox;
import edu.colorado.csdms.wmt.client.ui.widgets.RunInfoDialogBox;

/**
 * A class that defines static methods for accessing, modifying and deleting,
 * through asynchronous HTTP GET and POST requests, the JSON files used to set
 * up, configure and run WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DataTransfer {
  
  // Labels. If I make a spelling error, at least it'll only be in one place.
  private static final String NEW = "new";
  private static final String OPEN = "open";
  private static final String SHOW = "show";
  private static final String LOGIN = "login";
  private static final String LOGOUT = "logout";
  private static final String ADD = "add";
  private static final String DELETE = "delete";
  private static final String EDIT = "edit"; 
  private static final String INIT = "init";
  private static final String STAGE = "stage";
  private static final String LAUNCH = "launch";
  private static final String LIST = "list";
  private static final String ATTACH = "attach";
  private static final String QUERY = "query";

  /**
   * A JSNI method for creating a String from a JavaScriptObject.
   * 
   * @see <a
   *      href="http://stackoverflow.com/questions/4872770/excluding-gwt-objectid-from-json-stringifyjso-in-devmode">this</a>
   *      discussion of '__gwt_ObjectId'
   * @param jso a JavaScriptObject
   * @return a String representation of the JavaScriptObject
   */
  public final native static <T> String stringify(T jso) /*-{
		return JSON.stringify(jso, function(key, value) {
			if (key == '__gwt_ObjectId') {
				return;
			}
			return value;
		});
  }-*/;

  /**
   * A JSNI method for evaluating JSONs. This is a generic method. It returns
   * a JavaScript object of the type denoted by the type parameter T.
   * 
   * @see <a
   *      href="http://docs.oracle.com/javase/tutorial/extra/generics/methods.html">Generic
   *      Methods</a>
   * @see <a
   *      href="http://stackoverflow.com/questions/1843343/json-parse-vs-eval">JSON.parse
   *      vs. eval()</a>
   * 
   * @param jsonStr a trusted String
   * @return a JavaScriptObject that can be cast to an overlay type
   */
  public final native static <T> T parse(String jsonStr) /*-{
		return JSON.parse(jsonStr);
  }-*/;

  /**
   * Returns a deep copy of the input JavaScriptObject.
   * <p>
   * This is the public interface to {@link #copyImpl(JavaScriptObject)},
   * which does the heavy lifting.
   * 
   * @param jso a JavaScriptObject
   */
  @SuppressWarnings("unchecked")
  public static <T extends JavaScriptObject> T copy(T jso) {
    return (T) copyImpl(jso);
  }

  /**
   * A recursive JSNI method for making a deep copy of an input
   * JavaScriptObject. This is the private implementation of
   * {@link #copy(JavaScriptObject)}.
   * 
   * @see <a
   *      href="http://stackoverflow.com/questions/4730463/gwt-overlay-deep-copy"
   *      >This</a> example code was very helpful (thanks to the author, <a
   *      href="http://stackoverflow.com/users/247462/javier-ferrero">Javier
   *      Ferrero</a>!)
   * @param obj
   */
  private static native JavaScriptObject copyImpl(JavaScriptObject obj) /*-{

		if (obj == null)
			return obj;

		var copy;

		if (obj instanceof Date) {
			copy = new Date();
			copy.setTime(obj.getTime());
		} else if (obj instanceof Array) {
			copy = [];
			for (var i = 0, len = obj.length; i < len; i++) {
				if (obj[i] == null || typeof obj[i] != "object")
					copy[i] = obj[i];
				else
					copy[i] = @edu.colorado.csdms.wmt.client.control.DataTransfer::copyImpl(Lcom/google/gwt/core/client/JavaScriptObject;)(obj[i]);
			}
		} else {
			copy = {};
			for ( var attr in obj) {
				if (obj.hasOwnProperty(attr)) {
					if (obj[attr] == null || typeof obj[attr] != "object")
						copy[attr] = obj[attr];
					else
						copy[attr] = @edu.colorado.csdms.wmt.client.control.DataTransfer::copyImpl(Lcom/google/gwt/core/client/JavaScriptObject;)(obj[attr]);
				}
			}
		}
		return copy;
  }-*/;

  /**
   * A worker that builds a HTTP query string from a HashMap of key-value
   * entries.
   * 
   * @param entries a HashMap of key-value pairs
   * @return the query, as a String
   */
  private static String buildQueryString(HashMap<String, String> entries) {

    StringBuilder sb = new StringBuilder();

    Integer entryCount = 0;
    for (Entry<String, String> entry : entries.entrySet()) {
      if (entryCount > 0) {
        sb.append("&");
      }
      String encodedName = URL.encodeQueryString(entry.getKey());
      sb.append(encodedName);
      sb.append("=");
      String encodedValue = URL.encodeQueryString(entry.getValue());
      sb.append(encodedValue);
      entryCount++;
    }

    return sb.toString();
  }

  /**
   * Makes an asynchronous HTTPS POST request to create a new user login to WMT.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static void newUserLogin(DataManager data) {

    String url = DataURL.newUserLogin(data);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, URL.encode(url));

    HashMap<String, String> entries = new HashMap<String, String>();
    entries.put("username", data.security.getWmtUsername());
    entries.put("password", data.security.getWmtPassword());
    entries.put("password2", data.security.getWmtPassword());    
    String queryString = buildQueryString(entries);

    try {
      builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(queryString, new AuthenticationRequestCallback(
              data, url, NEW));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTPS POST request to login to WMT.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static void login(DataManager data) {

    String url = DataURL.login(data);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, URL.encode(url));

    HashMap<String, String> entries = new HashMap<String, String>();
    entries.put("username", data.security.getWmtUsername());
    entries.put("password", data.security.getWmtPassword());
    String queryString = buildQueryString(entries);

    try {
      builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(queryString, new AuthenticationRequestCallback(
              data, url, LOGIN));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTPS POST request to logout from WMT.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static void logout(DataManager data) {

    String url = DataURL.logout(data);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, URL.encode(url));

    try {
      builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(null, new AuthenticationRequestCallback(data,
              url, LOGOUT));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTPS GET request to get the WMT login state from
   * the server.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static void getLoginState(DataManager data) {

    String url = DataURL.loginState(data);
    GWT.log(url);
    
    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(url));

    try {
      builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(null, new AuthenticationRequestCallback(data,
              url, LIST));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }
  
  /**
   * Makes an asynchronous HTTP GET request to retrieve the list of components
   * stored in the WMT database.
   * <p>
   * Note that on completion of the request,
   * {@link #getComponent(DataManager, String)} starts pulling data for
   * individual components from the server.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static void getComponentList(DataManager data) {

    String url = DataURL.listComponents(data);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(url));

    try {
      @SuppressWarnings("unused")
      Request request =
          builder
              .sendRequest(null, new ComponentListRequestCallback(data, url));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTP GET request to the server to retrieve the data
   * for a single component.
   * 
   * @param data the DataManager object for the WMT session
   * @param componentId the identifier of the component in the database, a
   *          String
   */
  public static void getComponent(DataManager data, String componentId) {

    String url = DataURL.showComponent(data, componentId);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(url));

    try {
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(null, new ComponentRequestCallback(data, url, componentId));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTP GET request to retrieve the list of models
   * stored in the WMT database.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static void getModelList(DataManager data) {

    String url = DataURL.listModels(data);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(url));

    try {
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(null, new ModelListRequestCallback(data, url));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes a pair of asynchronous HTTP GET requests to retrieve model data and
   * metadata from a server.
   * 
   * @param data the DataManager object for the WMT session
   * @param modelId the unique identifier for the model in the database, an
   *          Integer
   */
  public static void getModel(DataManager data, Integer modelId) {

    // The "open" URL returns metadata (name, owner) in a ModelMetadataJSO,
    // while the "show" URL returns data in a ModelJSO.
    String openURL = DataURL.openModel(data, modelId);
    GWT.log(openURL);
    String showURL = DataURL.showModel(data, modelId);
    GWT.log(showURL);

    RequestBuilder openBuilder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(openURL));
    try {
      @SuppressWarnings("unused")
      Request openRequest =
          openBuilder.sendRequest(null, new ModelRequestCallback(data, openURL,
              OPEN));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }

    RequestBuilder showBuilder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(showURL));
    try {
      @SuppressWarnings("unused")
      Request showRequest =
          showBuilder.sendRequest(null, new ModelRequestCallback(data, showURL,
              SHOW));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTP POST request to save a new model, or edits to
   * an existing model, to the server.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static void postModel(DataManager data) {

    Integer modelId = data.getMetadata().getId();

    GWT.log("all model ids: " + data.modelIdList.toString());
    GWT.log("this model id: " + modelId.toString());

    String url, type;
    if (data.modelIdList.contains(modelId)) {
      url = DataURL.editModel(data, modelId);
      type = EDIT;
    } else {
      url = DataURL.newModel(data);
      type = NEW;
    }
    GWT.log(type + ": " + url);
    GWT.log(data.getModelString());

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, URL.encode(url));

    HashMap<String, String> entries = new HashMap<String, String>();
    entries.put("name", data.getModel().getName());
    entries.put("json", data.getModelString());
    String queryString = buildQueryString(entries);

    try {
      builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(queryString, new ModelRequestCallback(data, url,
              type));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTP POST request to delete a single model from the
   * WMT server.
   * 
   * @param data the DataManager object for the WMT session
   * @param modelId the unique identifier for the model in the database
   */
  public static void deleteModel(DataManager data, Integer modelId) {

    String url = DataURL.deleteModel(data, modelId);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, URL.encode(url));

    try {
      @SuppressWarnings("unused")
      Request request =
          builder
              .sendRequest(null, new ModelRequestCallback(data, url, DELETE));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTP POST request to initialize a model run on the
   * selected server.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static void initModelRun(DataManager data) {

    String url = DataURL.newModelRun(data);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, URL.encode(url));

    HashMap<String, String> entries = new HashMap<String, String>();
    entries.put("name", data.getModel().getName());
    entries.put("description", "A model submitted from the WMT GUI."); // XXX
    entries.put("model_id", ((Integer) data.getMetadata().getId()).toString());
    String queryString = buildQueryString(entries);

    try {
      builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(queryString, new RunRequestCallback(data, url,
              INIT));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTP POST request to stage a model run on the
   * selected server.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static void stageModelRun(DataManager data) {

    String url = DataURL.stageModelRun(data);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, URL.encode(url));

    HashMap<String, String> entries = new HashMap<String, String>();
    entries.put("uuid", data.getSimulationId());
    String queryString = buildQueryString(entries);

    try {
      builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(queryString, new RunRequestCallback(data, url,
              STAGE));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTPS POST request to launch a model run on the
   * selected server.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static void launchModelRun(DataManager data) {

    String url = DataURL.launchModelRun(data);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, URL.encode(url));

    HashMap<String, String> entries = new HashMap<String, String>();
    entries.put("uuid", data.getSimulationId());
    entries.put("host", data.security.getHpccHostname());
    entries.put("username", data.security.getHpccUsername());
    entries.put("password", data.security.getHpccPassword());
    String queryString = buildQueryString(entries);

    try {
      builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(queryString, new RunRequestCallback(data, url,
              LAUNCH));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTPS POST request to add a label to WMT.
   * 
   * @param data the DataManager object for the WMT session
   * @param label the label to add, a String
   */
  public static void addLabel(DataManager data, String label) {

    String url = DataURL.addLabel(data);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, URL.encode(url));

    HashMap<String, String> entries = new HashMap<String, String>();
    entries.put("tag", label);
    String queryString = buildQueryString(entries);

    try {
      builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(queryString, new LabelRequestCallback(data, url,
              ADD));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTPS POST request to delete a label from WMT.
   * 
   * @param data the DataManager object for the WMT session
   * @param labelId the id of the label to delete, an Integer
   */
  public static void deleteLabel(DataManager data, Integer labelId) {

    String url = DataURL.deleteLabel(data, labelId);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, URL.encode(url));

    try {
      @SuppressWarnings("unused")
      Request request =
          builder
              .sendRequest(null, new LabelRequestCallback(data, url, DELETE));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTPS GET request to list all labels belonging to the
   * current user, as well as all public labels, in WMT.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static void listLabels(DataManager data) {

    String url = DataURL.listLabels(data);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(url));

    try {
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(null, new LabelRequestCallback(data, url, LIST));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTPS POST request to attach a label to a model.
   * 
   * @param data the DataManager object for the WMT session
   * @param modelId the id of the model, an Integer
   * @param labelId the id of the label to add, an Integer
   */
  public static void addModelLabel(DataManager data, Integer modelId, Integer labelId) {

    String url = DataURL.addModelLabel(data);
    GWT.log(url);
    
    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, URL.encode(url));

    HashMap<String, String> entries = new HashMap<String, String>();
    entries.put("model", modelId.toString()); // type="text" in API
    entries.put("tag", labelId.toString());
    String queryString = buildQueryString(entries);

    try {
      builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(queryString, new LabelRequestCallback(data, url,
              ATTACH));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTPS GET request to query what models use the
   * given labels, input as an List of Integer ids. 
   * 
   * @param data the DataManager object for the WMT session
   * @param labelIds a List of Integer label ids
   */
  public static void queryModelLabels(DataManager data, List<Integer> labelIds) {

    String url = DataURL.queryModelLabel(data);

    // Build the URL parameters from the list of input labelIds.
    url += "?tags=";
    for (int i = 0; i < labelIds.size(); i++) {
      if (i > 0) {
        url += ",";
      }
      url += labelIds.get(i);
    }
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(url));

    try {
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(null, new LabelRequestCallback(data, url, QUERY));
    } catch (RequestException e) {
      Window.alert(Constants.REQUEST_ERR_MSG + e.getMessage());
    }
  }

  /**
   * A RequestCallback handler class that processes WMT login and logout
   * requests.
   */
  public static class AuthenticationRequestCallback implements RequestCallback {
    
    private DataManager data;
    private String url;
    private String type;
    
    public AuthenticationRequestCallback(DataManager data, String url,
        String type) {
      this.data = data;
      this.url = url;
      this.type = type;
    }

    /*
     * A helper that performs actions on login.
     */
    private void loginActions() {
      data.security.isLoggedIn(true);
      data.getPerspective().getLoginPanel().getLoginName().setText(
          data.security.getWmtUsername());
      data.getPerspective().getLoginPanel().showStatusPanel();
      
      // Get all labels belonging to the user, as well as all public labels.
      listLabels(data);

      // Set a cookie to store the most recent username.
      // TODO Replace with the browser's login autocomplete mechanism.
      String currentCookie = Cookies.getCookie(Constants.USERNAME_COOKIE);
      if (!data.security.getWmtUsername().equals(currentCookie)) {
        Date expires =
            new Date(System.currentTimeMillis() + Constants.COOKIE_DURATION);
        Cookies.setCookie(Constants.USERNAME_COOKIE, data.security
            .getWmtUsername(), expires);
      }
    }

    /*
     * A helper that performs actions on logout.
     */
    private void logoutActions() {
      data.security.isLoggedIn(false);
      data.getPerspective().getLoginPanel().showInputPanel();
      data.getPerspective().reset();
      
      // Clear any user-owned labels from list.
      for (Map.Entry<String, LabelJSO> entry : data.modelLabels.entrySet()) {
        if (data.security.getWmtUsername().equals(entry.getValue().getOwner())) {
          data.modelLabels.remove(entry.getKey());
        }
      }
    }
    
    @Override
    public void onResponseReceived(Request request, Response response) {
      if (Response.SC_OK == response.getStatusCode()) {

        String rtxt = response.getText();
        GWT.log(rtxt);

        // Need to refresh the model list on login and logout.
        getModelList(data);

        if (type.matches(NEW)) {
          loginActions();
          addLabel(data, data.security.getWmtUsername());
        } else if (type.matches(LOGIN)) {
          loginActions();
        } else if (type.matches(LOGOUT)) {
          logoutActions();
        } else if (type.matches(LIST)) {
          String username = rtxt.replace("\"", ""); // strip quote marks
          if (username.isEmpty()) {
            logoutActions();
          } else {
            data.security.setWmtUsername(username);
            loginActions();
          }
        } else {
          Window.alert(Constants.RESPONSE_ERR_MSG);
        }

      } else if (Response.SC_BAD_REQUEST == response.getStatusCode()) {

        // Display the NewUserDialogBox if the email address isn't recognized.
        final NewUserDialogBox box = new NewUserDialogBox();
        box.getChoicePanel().getCancelButton().addClickHandler(
            new DialogCancelHandler(box));
        box.getChoicePanel().getOkButton().addClickHandler(
            new AddNewUserHandler(data, box));
        box.center();
        
      } else if (Response.SC_UNAUTHORIZED == response.getStatusCode()) {

        // Display message if email address is valid, but password is not.
        Window.alert(Constants.PASSWORD_ERR);

      } else {
        String msg =
            "The URL '" + url + "' did not give an 'OK' response. "
                + "Response code: " + response.getStatusCode();
        Window.alert(msg);
      }
    }

    @Override
    public void onError(Request request, Throwable exception) {
      Window.alert(Constants.REQUEST_ERR_MSG + exception.getMessage());
    }
  }
  
  /**
   * A RequestCallback handler class that provides the callback for a GET
   * request of the list of available components in the WMT database. On
   * success, the list of component ids are stored in the {@link DataManager}
   * object for the WMT session. Concurrently,
   * {@link DataTransfer#getComponent(DataManager, String)} is called to
   * download and store information on the listed components.
   */
  public static class ComponentListRequestCallback implements RequestCallback {

    private DataManager data;
    private String url;

    public ComponentListRequestCallback(DataManager data, String url) {
      this.data = data;
      this.url = url;
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
      if (Response.SC_OK == response.getStatusCode()) {

        String rtxt = response.getText();
        GWT.log(rtxt);
        ComponentListJSO jso = parse(rtxt);

        // Load the list of components into the DataManager. At the same time,
        // start pulling down data for the components. Asynchronicity is cool!
        for (int i = 0; i < jso.getComponents().length(); i++) {
          String componentId = jso.getComponents().get(i);
          data.componentIdList.add(componentId);
          data.retryComponentLoad.put(componentId, 0);
          getComponent(data, componentId);
        }

        // Show the list of components (id only) as placeholders in the
        // ComponentSelectionMenu.
        ((ComponentSelectionMenu) data.getPerspective().getModelTree()
            .getDriverComponentCell().getComponentMenu())
            .initializeComponents();

      } else {
        String msg =
            "The URL '" + url + "' did not give an 'OK' response. "
                + "Response code: " + response.getStatusCode();
        Window.alert(msg);
      }
    }

    @Override
    public void onError(Request request, Throwable exception) {
      Window.alert(Constants.REQUEST_ERR_MSG + exception.getMessage());
    }
  }

  /**
   * A RequestCallback handler class that provides the callback for a GET
   * request of a component. On success,
   * {@link DataManager#addComponent(ComponentJSO)} and
   * {@link DataManager#addModelComponent(ComponentJSO)} are called to store
   * the (class) component and the model component in the DataManager object
   * for the WMT session.
   */
  public static class ComponentRequestCallback implements RequestCallback {

    private DataManager data;
    private String url;
    private String componentId;

    public ComponentRequestCallback(DataManager data, String url,
        String componentId) {
      this.data = data;
      this.url = url;
      this.componentId = componentId;
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
      if (Response.SC_OK == response.getStatusCode()) {

        String rtxt = response.getText();
        GWT.log(rtxt);
        ComponentJSO jso = parse(rtxt);
        data.addComponent(jso); // "class" component
        data.addModelComponent(copy(jso)); // "instance" component, for model
        data.nComponents++;

        if (data.nComponents == data.componentIdList.size()) {
          data.showDefaultCursor();
        }
        
        // Replace the associated placeholder ComponentSelectionMenu item.
        ((ComponentSelectionMenu) data.getPerspective().getModelTree()
            .getDriverComponentCell().getComponentMenu()).replaceMenuItem(jso
            .getId());

      } else {

        // If the component didn't load, try to reload it RETRY_ATTEMPTS times.
        // If that fails, display an error message in a window.
        Integer attempt = data.retryComponentLoad.get(componentId);
        data.retryComponentLoad.put(componentId, attempt++);
        if (attempt < Constants.RETRY_ATTEMPTS) {
          getComponent(data, componentId);
        } else {
          data.nComponents++;
          String msg =
              "The URL '" + url + "' did not give an 'OK' response. "
                  + "Response code: " + response.getStatusCode();
          Window.alert(msg);
        }
      }
    }

    @Override
    public void onError(Request request, Throwable exception) {
      Window.alert(Constants.REQUEST_ERR_MSG + exception.getMessage());
    }
  }

  /**
   * A RequestCallback handler class that provides the callback for a GET
   * request of the list of available models in the WMT database. On success,
   * the list of model names and ids are stored in the {@link DataManager}
   * object for the WMT session.
   */
  public static class ModelListRequestCallback implements RequestCallback {

    private DataManager data;
    private String url;

    public ModelListRequestCallback(DataManager data, String url) {
      this.data = data;
      this.url = url;
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
      if (Response.SC_OK == response.getStatusCode()) {

        String rtxt = response.getText();
        GWT.log(rtxt);
        ModelListJSO jso = parse(rtxt);

        // Start with clean lists of model names and ids.
        data.modelIdList.clear();
        data.modelNameList.clear();

        // Load the list of models into the DataManager.
        for (int i = 0; i < jso.getModels().length(); i++) {
          data.modelIdList.add(jso.getModels().get(i).getModelId());
          data.modelNameList.add(jso.getModels().get(i).getName());
        }

      } else {
        String msg =
            "The URL '" + url + "' did not give an 'OK' response. "
                + "Response code: " + response.getStatusCode();
        Window.alert(msg);
      }
    }

    @Override
    public void onError(Request request, Throwable exception) {
      Window.alert(Constants.REQUEST_ERR_MSG + exception.getMessage());
    }
  }

  /**
   * A RequestCallback handler class that provides the callback for a model
   * GET or POST request.
   * <p>
   * On a successful GET, {@link DataManager#deserialize()} is called to
   * populate the WMT GUI. On a successful POST,
   * {@link DataTransfer#getModelList(DataManager)} is called to refresh the
   * list of models available on the server.
   */
  public static class ModelRequestCallback implements RequestCallback {

    private DataManager data;
    private String url;
    private String type;

    public ModelRequestCallback(DataManager data, String url, String type) {
      this.data = data;
      this.url = url;
      this.type = type;
    }

    /*
     * A helper for processing the return from models/show.
     */
    private void showActions(String rtxt) {
      ModelJSO jso = parse(rtxt);
      data.setModel(jso);
      data.modelIsSaved(true);
      data.deserialize();
    }

    /*
     * A helper for processing the return from models/open.
     */
    private void openActions(String rtxt) {
      ModelMetadataJSO jso = parse(rtxt);
      data.setMetadata(jso);
    }

    /*
     * A helper for processing the return from models/new and models/edit.
     */
    private void editActions() {
      data.modelIsSaved(true);
      data.getPerspective().setModelPanelTitle();
      DataTransfer.getModelList(data);
      addSelectedLabels(data.getMetadata().getId());
    }

    /*
     * A helper for adding all selected labels to a model.
     */
    private void addSelectedLabels(Integer modelId) {
      for (Map.Entry<String, LabelJSO> entry : data.modelLabels.entrySet()) {
        if (entry.getValue().isSelected()) {
          addModelLabel(data, modelId, entry.getValue().getId());
        }
      }
    }

    @Override
    public void onResponseReceived(Request request, Response response) {

      data.showDefaultCursor();
      if (Response.SC_OK == response.getStatusCode()) {

        String rtxt = response.getText();
        GWT.log(rtxt);

        if (type.matches(SHOW)) {
          showActions(rtxt);
        } else if (type.matches(OPEN)) {
          openActions(rtxt);
        } else if (type.matches(NEW)) {
          Integer modelId = Integer.valueOf(rtxt);
          data.getMetadata().setId(modelId);
          editActions();
        } else if (type.matches(EDIT)) {
          editActions();
        } else if (type.matches(DELETE)) {
          DataTransfer.getModelList(data);
        } else {
          Window.alert(Constants.RESPONSE_ERR_MSG);
        }
        
      } else {
        String msg =
            "The URL '" + url + "' did not give an 'OK' response. "
                + "Response code: " + response.getStatusCode();
        Window.alert(msg);
      }
    }

    @Override
    public void onError(Request request, Throwable exception) {
      Window.alert(Constants.REQUEST_ERR_MSG + exception.getMessage());
    }
  }

  /**
   * A RequestCallback handler class that handles the initialization, staging
   * and launching of a model run.
   */
  public static class RunRequestCallback implements RequestCallback {

    private DataManager data;
    private String url;
    private String type;

    public RunRequestCallback(DataManager data, String url, String type) {
      this.data = data;
      this.url = url;
      this.type = type;
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
      if (Response.SC_OK == response.getStatusCode()) {

        String rtxt = response.getText();
        GWT.log(rtxt);

        if (type.matches(INIT)) {
          String uuid = rtxt.replaceAll("^\"|\"$", "");
          data.setSimulationId(uuid); // store the run's uuid
          DataTransfer.stageModelRun(data);
        } else if (type.matches(STAGE)) {
          DataTransfer.launchModelRun(data);
        } else if (type.matches(LAUNCH)) {
          RunInfoDialogBox runInfo = new RunInfoDialogBox(data);
          runInfo.center();
        } else {
          Window.alert(Constants.RESPONSE_ERR_MSG);
        }

      } else {
        String msg =
            "The URL '" + url + "' did not give an 'OK' response. "
                + "Response code: " + response.getStatusCode();
        Window.alert(msg);
      }
    }

    @Override
    public void onError(Request request, Throwable exception) {
      Window.alert(Constants.REQUEST_ERR_MSG + exception.getMessage());
    }
  }

  /**
   * A RequestCallback handler class that handles listing, adding, and deleting 
   * labels.
   */
  public static class LabelRequestCallback implements RequestCallback {

    private DataManager data;
    private String url;
    private String type;

    public LabelRequestCallback(DataManager data, String url, String type) {
      this.data = data;
      this.url = url;
      this.type = type;
    }

    /*
     * A helper for adding a new label.
     */
    private void addActions(String rtxt) {
      LabelJSO jso = parse(rtxt);
      data.modelLabels.put(jso.getLabel(), jso);
      data.getPerspective().getLabelsMenu().populateMenu();
    }

    /*
     * A helper for deleting a label.
     */
    private void deleteActions(String rtxt) {
      Integer labelId = Integer.valueOf(rtxt.replaceAll("^\"|\"$", ""));
      for (Map.Entry<String, LabelJSO> entry : data.modelLabels.entrySet()) {
        LabelJSO jso = entry.getValue();
        if (jso.getId() == labelId) {
          data.modelLabels.remove(jso.getLabel());
          break;
        }
      }
      data.getPerspective().getLabelsMenu().populateMenu();
    }

    /*
     * A helper for listing all labels.
     */
    private void listActions(String rtxt) {
      LabelJSO jso = parse(rtxt);
      Integer nLabels = jso.getLabels().length();
      if (nLabels > 0) {
        for (int i = 0; i < nLabels; i++) {
          LabelJSO labelJSO = jso.getLabels().get(i);
          String label = labelJSO.getLabel();
          Boolean isUser = data.security.getWmtUsername().matches(label);
          labelJSO.isSelected(isUser);
          data.modelLabels.put(label, labelJSO);
        }
      }
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
      if (Response.SC_OK == response.getStatusCode()) {

        String rtxt = response.getText();
        GWT.log(rtxt);

        if (type.matches(ADD)) {
          addActions(rtxt);
        } else if (type.matches(DELETE)) {
          deleteActions(rtxt);
        } else if (type.matches(LIST)) {
          listActions(rtxt);
        } else if (type.matches(ATTACH)) {
          ; // Do nothing
        } else if (type.matches(QUERY)) {
          Window.alert(rtxt); // TODO
        } else {
          Window.alert(Constants.RESPONSE_ERR_MSG);
        }

      } else {
        String msg =
            "The URL '" + url + "' did not give an 'OK' response. "
                + "Response code: " + response.getStatusCode();
        Window.alert(msg);
      }
    }

    @Override
    public void onError(Request request, Throwable exception) {
      Window.alert(Constants.REQUEST_ERR_MSG + exception.getMessage());
    }
  }
  
}
