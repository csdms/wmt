/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

import edu.colorado.csdms.wmt.client.ui.DataManager;

/**
 * A class that defines static methods for accessing, through asynchronous HTTP
 * GET and POST requests, the JSON files used to set up, configure and run WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DataTransfer {

  private static final String ERR_MSG = "Failed to send the request: ";

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
   * A JSNI method for evaluating JSONs.
   * 
   * Note that this is a generic method. It returns a JavaScript object of the
   * type denoted by the type parameter T.
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
  private final native static <T> T parse(String jsonStr) /*-{
		return eval("(" + jsonStr + ")");
  }-*/;

  /**
   * A worker that returns a HashMap of entries used to build a HTTP query
   * string.
   * 
   * @param modelName the name of the model, a String
   * @param jsonStr the stringified JSON describing the model
   */
  private static HashMap<String, String> makeQueryEntries(String modelName,
      String jsonStr) {
    HashMap<String, String> m = new HashMap<String, String>();
    m.put("name", modelName);
    m.put("json", jsonStr);
    return m;
  }

  /**
   * A worker that builds a HTTP query string from a HashMap of key-value
   * entries (e.g., returned from {@link #makeQueryEntries(String, String)}).
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
   * Makes an asynchronous HTTP GET request to retrieve the list of components
   * stored in the WMT database.
   * <p>
   * Note that on completion of the request,
   * {@link #getComponent(DataManager, String)} starts pulling data for
   * individual components from the server.
   * 
   * @param data the DataManager object for the WMT session
   */
  @SuppressWarnings("unused")
  public static void getComponentList(DataManager data) {

    String url = DataURL.listComponents(data);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(url));

    try {
      Request request =
          builder
              .sendRequest(null, new ComponentListRequestCallback(data, url));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTP GET request to the server to retrieve data for a
   * component, including its "provides" and "uses" ports as well as its
   * parameters.
   * 
   * @param data the DataManager object for the WMT session
   * @param componentId the identifier of the component in the database
   */
  @SuppressWarnings("unused")
  public static void getComponent(DataManager data, String componentId) {

    String url = DataURL.showComponent(data, componentId);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(url));

    try {
      Request request =
          builder.sendRequest(null, new ComponentRequestCallback(data, url));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTP GET request to retrieve the list of models
   * stored in the WMT database.
   * 
   * @param data the DataManager object for the WMT session
   */
  @SuppressWarnings("unused")
  public static void getModelList(DataManager data) {

    String url = DataURL.listModels(data);
    GWT.log(url);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(url));

    try {
      Request request =
          builder.sendRequest(null, new ModelListRequestCallback(data, url));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes a pair of asynchronous HTTP GET requests to retrieve model data and
   * metadata from a server.
   * 
   * @param data the DataManager object for the WMT session
   * @param modelId the unique identifier for the model in the database
   */
  @SuppressWarnings("unused")
  public static void getModel(DataManager data, Integer modelId) {

    // The "open" URL returns metadata (name, owner), while the "show" URL
    // returns a ModelJSO.
    String openURL = DataURL.openModel(data, modelId);
    String showURL = DataURL.showModel(data, modelId);

    RequestBuilder openBuilder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(openURL));
    GWT.log(openURL);
    try {
      Request openRequest =
          openBuilder.sendRequest(null, new ModelRequestCallback(data, openURL,
              "open"));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
    }

    RequestBuilder showBuilder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(showURL));
    GWT.log(showURL);
    try {
      Request showRequest =
          showBuilder.sendRequest(null, new ModelRequestCallback(data, showURL,
              "show"));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
    }
  }

  /**
   * Makes an asynchronous HTTP request to POST a model to the server.
   * 
   * @param data the DataManager object for the WMT session
   * @param modelName the name the user gave the model
   */
  @SuppressWarnings("unused")
  public static void postModel(DataManager data) {

    String url = DataURL.newModel(data);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, URL.encode(url));

    HashMap<String, String> entries =
        makeQueryEntries(data.getModel().getName(), data.getModelString());
    String queryString = buildQueryString(entries);

    try {
      builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
      Request request =
          builder.sendRequest(queryString, new ModelRequestCallback(data, url,
              "new"));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
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
          getComponent(data, componentId);
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
      Window.alert(ERR_MSG + exception.getMessage());
    }
  }

  /**
   * A RequestCallback handler class that provides the callback for a GET
   * request of a component. On success,
   * {@link DataManager#setComponent(ComponentJSO)} is called to store the
   * component in the DataManager object for the WMT session.
   */
  public static class ComponentRequestCallback implements RequestCallback {

    private DataManager data;
    private String url;

    public ComponentRequestCallback(DataManager data, String url) {
      this.data = data;
      this.url = url;
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
      if (Response.SC_OK == response.getStatusCode()) {
        String rtxt = response.getText();
        GWT.log(rtxt);
        ComponentJSO jso = parse(rtxt);
        data.setComponent(jso);
      } else {
        String msg =
            "The URL '" + url + "' did not give an 'OK' response. "
                + "Response code: " + response.getStatusCode();
        Window.alert(msg);
      }
    }

    @Override
    public void onError(Request request, Throwable exception) {
      Window.alert(ERR_MSG + exception.getMessage());
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
      Window.alert(ERR_MSG + exception.getMessage());
    }
  }

  /**
   * A RequestCallback handler class that provides the callback for a model GET
   * or POST request.
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

    @Override
    public void onResponseReceived(Request request, Response response) {
      if (Response.SC_OK == response.getStatusCode()) {

        String rtxt = response.getText();
        GWT.log(rtxt);

        // On successful GET, deserialize the ModelJSO and populate the GUI.
        if (type.matches("show")) {
          ModelJSO jso = parse(rtxt);
          data.setModel(jso);
          data.deserialize();
        }

        // On successful POST, update list of saved models in the DataManager.
        if (type.matches("new")) {
          DataTransfer.getModelList(data);
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
      Window.alert(ERR_MSG + exception.getMessage());
    }
  }
}
