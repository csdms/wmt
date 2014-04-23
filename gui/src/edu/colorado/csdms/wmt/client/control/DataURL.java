/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.control;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * A class defining static methods that return URLs for accessing components
 * and models. Works in GWT development mode and in production mode, accessing
 * the WMT API.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DataURL {

  // By same-origin policy, must use HTTPS consistently across all URLs.
  private static final String BASE_URL = "https://csdms.colorado.edu/";
  private static final String API_URL = BASE_URL + "wmt-server/";
  private static final String API_DEV_URL = BASE_URL + "wmt/api-dev/";
  private static final String LOCAL_URL = GWT.getHostPageBaseURL();

  private static final String LOGIN_PATH = "account/login";
  private static final String LOGOUT_PATH = "account/logout";
  private static final String USERNAME_PATH = "account/username";
  
  private static final String COMPONENTS_LIST_PATH = "components/list";
  private static final String COMPONENTS_SHOW_PATH = "components/show/";

  private static final String MODELS_LIST_PATH = "models/list";
  private static final String MODELS_OPEN_PATH = "models/open/";
  private static final String MODELS_SHOW_PATH = "models/show/";
  private static final String MODELS_NEW_PATH = "models/new";
  private static final String MODELS_EDIT_PATH = "models/edit/";
  private static final String MODELS_DELETE_PATH = "models/delete/";
  private static final String MODELS_UPLOAD_PATH = "models/upload";

  private static final String RUN_NEW_PATH = "run/new";
  private static final String RUN_SHOW_PATH = "run/show";
  private static final String RUN_STAGE_PATH = "run/stage";
  private static final String RUN_LAUNCH_PATH = "run/launch";

  /**
   * A helper that returns the base URL for the selected API (development or
   * production).
   * 
   * @param data the DataManager object for the WMT session
   */
  private static String getApiUrl(DataManager data) {
    return data.isApiDevelopmentMode() ? API_DEV_URL : API_URL;
  }

  /**
   * A wrapper around Window.Location that returns the application URL in
   * either development or production mode.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String applicationURL(DataManager data) {
    return Window.Location.getHref();
  }

  /**
   * Returns the account login URL provided by the API.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String login(DataManager data) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "save/authenticate.json";
    } else {
      return getApiUrl(data) + LOGIN_PATH;
    }
  }

  /**
   * Returns the account logout URL provided by the API.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String logout(DataManager data) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "save/authenticate.json";
    } else {
      return getApiUrl(data) + LOGOUT_PATH;
    }
  }

  /**
   * Returns the URL that gives the username of the user, if the user is
   * currently logged in.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String loginState(DataManager data) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "save/authenticate.json";
    } else {
      return getApiUrl(data) + USERNAME_PATH;
    }
  }

  /**
   * Returns the URL for the list of available components on the server.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String listComponents(DataManager data) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "data/components.json";
    } else {
      return getApiUrl(data) + COMPONENTS_LIST_PATH;
    }
  }

  /**
   * Returns the URL to access a specific component by its id.
   * 
   * @param data the DataManager object for the WMT session
   * @param componentId the id of the desired component
   */
  public static String showComponent(DataManager data, String componentId) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "data/" + componentId + ".json";
    } else {
      return getApiUrl(data) + COMPONENTS_SHOW_PATH + componentId;
    }
  }

  /**
   * Returns the URL to format the parameters of a component, given its id.
   * 
   * @param data the DataManager object for the WMT session
   * @param componentId the id of the desired component
   * @param format the output format: HTML, text or JSON
   * @param useDefaults set to true to use the defaults for the component
   */
  public static String formatComponent(DataManager data, String componentId,
      String format, Boolean useDefaults) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "data/" + componentId + ".json";
    } else {
      String modelId = ((Integer) data.getMetadata().getId()).toString();
      if (useDefaults) {
        modelId = "0";
      }
      String url = getApiUrl(data) + "models/" + modelId + "/" + componentId 
          + "/format?format=" + format;
      return url;
    }
  }

  /**
   * Returns the URL for the list of available models from the server.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String listModels(DataManager data) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "save/model_list.json";
    } else {
      return getApiUrl(data) + MODELS_LIST_PATH;
    }
  }

  /**
   * Returns the URL to access the metadata for a model, given its id.
   * 
   * @param data the DataManager object for the WMT session
   * @param modelId the id of the model, an Integer set by the API
   */
  public static String openModel(DataManager data, Integer modelId) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "save/open" + modelId.toString() + ".json";
    } else {
      return getApiUrl(data) + MODELS_OPEN_PATH + modelId.toString();
    }
  }

  /**
   * Returns the URL to access the data (connections, parameters) for a model,
   * given its id.
   * 
   * @param data the DataManager object for the WMT session
   * @param modelId the id of the model, an Integer set by the API
   */
  public static String showModel(DataManager data, Integer modelId) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "save/show" + modelId.toString() + ".json";
    } else {
      return getApiUrl(data) + MODELS_SHOW_PATH + modelId.toString();
    }
  }

  /**
   * Returns the URL for posting a new model to the server.
   * <p>
   * Note that this appears to work only in production mode.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String newModel(DataManager data) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "save/saved.json";
    } else {
      return getApiUrl(data) + MODELS_NEW_PATH;
    }
  }

  /**
   * Returns the URL for updating an existing model on the server, given its
   * id.
   * 
   * @param data the DataManager object for the WMT session
   * @param modelId the id of the model, an Integer set by the API
   */
  public static String editModel(DataManager data, Integer modelId) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "save/saved.json";
    } else {
      return getApiUrl(data) + MODELS_EDIT_PATH + modelId.toString();
    }
  }

  /**
   * Returns the URL for deleting an existing model from the server, given its
   * id.
   * 
   * @param data the DataManager object for the WMT session
   * @param modelId the id of the model, an Integer set by the API
   */
  public static String deleteModel(DataManager data, Integer modelId) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "save/saved.json";
    } else {
      return getApiUrl(data) + MODELS_DELETE_PATH + modelId.toString();
    }
  }

  /**
   * Returns the URL for posting a file associated with a model to the server.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String uploadFile(DataManager data) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "save/saved.json";
    } else {
      return getApiUrl(data) + MODELS_UPLOAD_PATH;
    }
  }

  /**
   * Returns the URL used to create a new model run.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String newModelRun(DataManager data) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "save/saved.json";
    } else {
      return getApiUrl(data) + RUN_NEW_PATH;
    }
  }

  /**
   * Returns the URL for API page displaying the status of all current model
   * runs on the server.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String showModelRun(DataManager data) {
    return getApiUrl(data) + RUN_SHOW_PATH;
  }

  /**
   * Returns the URL used to stage a model run.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String stageModelRun(DataManager data) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "save/saved.json";
    } else {
      return getApiUrl(data) + RUN_STAGE_PATH;
    }
  }

  /**
   * Returns the URL used to launch a model run. Note that the URL uses HTTPS
   * because a username and password are being transferred.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String launchModelRun(DataManager data) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "save/saved.json";
    } else {
      return getApiUrl(data) + RUN_LAUNCH_PATH;
    }
  }
}
