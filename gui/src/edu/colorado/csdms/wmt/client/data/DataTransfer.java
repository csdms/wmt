/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

import edu.colorado.csdms.wmt.client.ui.DataManager;

/**
 * A class that defines static methods for accessing the JSON files used to
 * set up, configure and run WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DataTransfer {

  private static final String ERR_MSG = "Failed to send the request: ";
  private static final String BASE_URL = "http://csdms.colorado.edu/wmt/";
  private static final String MODEL_LIST_URL = BASE_URL + "models/list";
  private static final String MODEL_OPEN_URL = BASE_URL + "models/open/";
  private static final String MODEL_SHOW_URL = BASE_URL + "models/show/";
  private static final String COMPONENT_LIST_URL = BASE_URL + "components/list";
  private static final String COMPONENT_SHOW_URL = BASE_URL + "components/show/";

  /**
   * A JSNI method for creating a String from a JavaScriptObject.
   * 
   * @see <a
   *      href="http://stackoverflow.com/questions/4872770/excluding-gwt-objectid-from-json-stringifyjso-in-devmode">this</a>
   *      discussion of '__gwt_ObjectId'
   * @param jso a JavaScriptObject
   * @return a String representation of the JavaScriptObject
   */
  private final native static <T> String stringify(T jso) /*-{
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
   * Makes an asynchronous HTTP GET request to retrieve the simple list of
   * components stored in the WMT database.
   * <p>
   * Note that on completion of the request,
   * {@link #getComponent(DataManager, String)} starts pulling data for
   * individual components from the server.
   * 
   * @param data the DataManager object for the WMT session
   */
  @SuppressWarnings("unused")
  public static void getComponentList(DataManager data) {

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(COMPONENT_LIST_URL));

    try {
      Request request =
          builder.sendRequest(null, new ComponentListRequestCallback(data,
              COMPONENT_LIST_URL));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
    }
  }  
  
  /**
   * Makes an asynchronous HTTP GET request to the server to retrieve data for
   * a component, including its "provides" and "uses" ports as well as its
   * parameters.
   * 
   * @param data the DataManager object for the WMT session
   * @param componentId the identifier of the component in the database
   */
  @SuppressWarnings("unused")
  public static void getComponent(DataManager data, String componentId) {

    String url = COMPONENT_SHOW_URL + componentId;

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(url));
    GWT.log(url);
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

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(MODEL_LIST_URL));

    try {
      Request request =
          builder.sendRequest(null, new ModelListRequestCallback(data,
              MODEL_LIST_URL));
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

    // The "open" URL is for metadata (name, owner), while the "show" URL
    // returns a ModelJSO.
    String openURL = MODEL_OPEN_URL + modelId.toString();
    String showURL = MODEL_SHOW_URL + modelId.toString();

    RequestBuilder openBuilder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(openURL));
    GWT.log(openURL);
    try {
      Request openRequest =
          openBuilder
              .sendRequest(null, new ModelRequestCallback(data, openURL));
    } catch (RequestException e) {
      Window.alert(ERR_MSG + e.getMessage());
    }

    RequestBuilder showBuilder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(showURL));
    GWT.log(showURL);
    try {
      Request showRequest =
          showBuilder
              .sendRequest(null, new ModelRequestCallback(data, showURL));
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
  public static void postModel(DataManager data, String modelName) {

  }
  
  /**
   * See "test_4_cem.json" for an example of what the JSON should look like.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static void serialize(DataManager data) {

    // Get the model that the ModelTree stored in the DataManager.
    ModelJSO model = data.getModel();
    GWT.log(model.getName());
    GWT.log(((Integer) model.getComponents().length()).toString());

    // Sneakily create a JsArray of ModelJSO objects. These are for the
    // components that make up the model.
    @SuppressWarnings("unchecked")
    JsArray<ModelJSO> components = (JsArray<ModelJSO>) ModelJSO.createArray();

    // Create a series of new ModelJSO objects representing components in
    // the model and push them into the components JsArray. When finished,
    // set the component JsArray into the model.
    String[] classNames = {"CEM", "Avulsion", "HydroTrend"};
    String[] idNames = {"cem_0", "avulsion_0", "hydrotrend_0"};
    for (int i = 0; i < classNames.length; i++) {
      ModelJSO modelComponent = (ModelJSO) ModelJSO.createObject();
      modelComponent.setClassName(classNames[i]);
      modelComponent.setId(idNames[i]);
      components.push(modelComponent);
    }
    model.setComponents(components);

    // Check that the components are present, and that the count has been
    // updated.
    for (int i = 0; i < classNames.length; i++) {
      GWT.log(model.getComponents().get(i).getClassName());
    }
    GWT.log(((Integer) model.getComponents().length()).toString());

    // Stringify the JSON. This is what I'd like to write to a file. (Or
    // transfer back to the server, however we do this.)
    String modelString = stringify(model);
    GWT.log(modelString);
    data.setModelString(modelString);

    // Put the file back on the server.
    // XXX Hacky.
    postModel(data, model.getName());

    // ModelTree tree = data.getModelTree();
    //
    // Iterator<TreeItem> iter = tree.treeItemIterator();
    // while (iter.hasNext()) {
    // TreeItem treeItem = (TreeItem) iter.next();
    // ModelCell cell = (ModelCell) treeItem.getWidget();
    // GWT.log(cell.getComponentCell().getComponent().getName());
    // }
  }

  /**
   * A RequestCallback handler class that provides the callback for a GET
   * request of the list of available components in the WMT database.
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
   * request of a component.
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
        ComponentJSO jso = parse(rtxt);
        data.setComponent(jso);

        // Window.alert(rtxt);
        // ModelJSO jso = parse(rtxt);
        // GWT.log("name = " + jso.getName());
        // GWT.log("model id = " + jso.getModelId());
        // GWT.log("owner = " + jso.getOwner());
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
   * request of the list of available models in the WMT database.
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
        ModelListJSO jso = parse(rtxt);

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
   * A RequestCallback handler class that provides the callback for a GET
   * request of a model.
   */
  public static class ModelRequestCallback implements RequestCallback {

    private DataManager data;
    private String url;

    public ModelRequestCallback(DataManager data, String url) {
      this.data = data;
      this.url = url;
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
      if (Response.SC_OK == response.getStatusCode()) {
        String rtxt = response.getText();
        Window.alert(rtxt);
        ModelJSO jso = parse(rtxt);
        data.setModel(jso);
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
