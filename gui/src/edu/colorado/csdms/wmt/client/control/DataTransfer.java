/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.control;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

import edu.colorado.csdms.wmt.client.data.ComponentJSO;
import edu.colorado.csdms.wmt.client.data.ComponentListJSO;
import edu.colorado.csdms.wmt.client.data.ModelJSO;
import edu.colorado.csdms.wmt.client.data.ModelListJSO;
import edu.colorado.csdms.wmt.client.data.ModelMetadataJSO;

/**
 * A class that defines static methods for accessing, modifying and deleting,
 * through asynchronous HTTP GET and POST requests, the JSON files used to set
 * up, configure and run WMT.
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
   * A worker that returns a HashMap of entries used in a HTTP query string.
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
      Window.alert(ERR_MSG + e.getMessage());
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
      Window.alert(ERR_MSG + e.getMessage());
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
              "open"));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
    }

    RequestBuilder showBuilder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(showURL));
    try {
      @SuppressWarnings("unused")
      Request showRequest =
          showBuilder.sendRequest(null, new ModelRequestCallback(data, showURL,
              "show"));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
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

    String url;
    if (data.modelIdList.contains(modelId)) {
      url = DataURL.editModel(data, modelId);
    } else {
      url = DataURL.newModel(data);
    }
    GWT.log(url);
    GWT.log(data.getModelString());

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, URL.encode(url));

    HashMap<String, String> entries =
        makeQueryEntries(data.getModel().getName(), data.getModelString());
    String queryString = buildQueryString(entries);

    try {
      builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
      @SuppressWarnings("unused")
      Request request =
          builder.sendRequest(queryString, new ModelRequestCallback(data, url,
              "new/edit"));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
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
          builder.sendRequest(null, new ModelRequestCallback(data, url,
              "delete"));
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
   * {@link DataManager#addComponent(ComponentJSO)} and
   * {@link DataManager#addModelComponent(ComponentJSO)} are called to store
   * the (class) component and the model component in the DataManager object
   * for the WMT session.
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
        data.addComponent(jso); // "class" component
        data.addModelComponent(copy(jso)); // "instance" component, for model
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

    @Override
    public void onResponseReceived(Request request, Response response) {
      if (Response.SC_OK == response.getStatusCode()) {

        String rtxt = response.getText();
        GWT.log(rtxt);
        
        if (type.matches("show")) {
          ModelJSO jso = parse(rtxt);
          data.setModel(jso);
          data.modelIsSaved(true);
          data.deserialize();
        }
        if (type.matches("open")) {
          ModelMetadataJSO jso = parse(rtxt);
          data.setMetadata(jso);
        }
        if (type.matches("new/edit")) {
          DataTransfer.getModelList(data);
          data.modelIsSaved(true);
          data.getPerspective().setModelPanelTitle();
        }
        if (type.matches("delete")) {
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
