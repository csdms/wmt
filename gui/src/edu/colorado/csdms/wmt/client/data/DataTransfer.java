/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

import java.util.Iterator;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TreeItem;

import edu.colorado.csdms.wmt.client.ui.DataManager;
import edu.colorado.csdms.wmt.client.ui.ModelCell;
import edu.colorado.csdms.wmt.client.ui.ModelTree;

/**
 * A class that defines static methods for accessing the JSON files used to
 * set up, configure and run WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DataTransfer {

  private static final String ERR_MSG = "Failed to send the request: ";
  private static final String DATA_URL = GWT.getHostPageBaseURL() + "data/";
  private static final String SAVE_URL = GWT.getHostPageBaseURL() + "save/";

  private static final String BASE_URL =
      "http://csdms.colorado.edu/wmt/models/";
  private static final String MODEL_LIST_URL = BASE_URL + "list";

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
   * 
   * @param jsonStr a String that you trust
   * @return a JavaScriptObject that you can cast to an overlay type
   */
  private final native static <T> T parse(String jsonStr) /*-{
		return eval("(" + jsonStr + ")");
  }-*/;  

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
   * Makes an asynchronous HTTP request to get a JSON file from the server.
   * 
   * @param data the DataManager object for the WMT session
   * @param fn a JSON file name
   * @param ft the file type: component, parameter or model
   */
  public static void get(final DataManager data, String fn, String ft) {

    // Helpful locals.
    final String fileType = ft;
    String fileName = fn;
    String jsonURL = DATA_URL + fileName;
    GWT.log(jsonURL);

    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, URL.encode(jsonURL));

    try {
      @SuppressWarnings("unused")
      Request request = builder.sendRequest(null, new RequestCallback() {

        @Override
        public void onResponseReceived(Request request, Response response) {
          if (Response.SC_OK == response.getStatusCode()) {
            String rtxt = response.getText();
            String prefix = fileType + ": ";
            try {
              if (fileType == "component") {
                ComponentDescriptions json = parse(rtxt);
                data.setComponents(json);
                GWT.log(prefix + data.getComponents().get(0).getName());
              }
              if (fileType == "parameter") {
                ComponentParameters json = parse(rtxt);
                data.setParameters(json.getId(), json);
                GWT.log(prefix + data.getParameters(json.getId()).getId());
              }
            } catch (Exception e) {
              GWT.log("Error:" + e.toString());
            }
          } else {
            String msg =
                response.getStatusCode() + " : " + response.getStatusText();
            Window.alert(msg);
          }
        }

        @Override
        public void onError(Request request, Throwable exception) {
          Window.alert(ERR_MSG + exception.getMessage());
        }
      });

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
    String openURL = BASE_URL + "open/" + modelId.toString();
    String showURL = BASE_URL + "show/" + modelId.toString();

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
   * Makes an asynchronous HTTP request to POST a JSON file from the server.
   * 
   * @param data the DataManager object for the WMT session
   * @param fn a JSON file name
   * @param the file type: component, parameter or model
   */
  public static void post(final DataManager data, String fn, String ft) {

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
    post(data, model.getName() + ".json", "model");

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
        ModelListJSO json = parse(rtxt);

        // Load the list into the DataManager.
        for (int i = 0; i < json.getModels().length(); i++) {
          Integer key = json.getModels().get(i).getModelId();
          String value = json.getModels().get(i).getName();
          data.modelList.put(key, value);
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
//        ModelJSO json = parse(rtxt);
//        GWT.log("name = " + json.getName());
//        GWT.log("model id = " + json.getModelId());
//        GWT.log("owner = " + json.getOwner());
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
