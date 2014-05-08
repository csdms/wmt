/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.control;

import com.google.gwt.user.client.Window;

import edu.colorado.csdms.wmt.client.Constants;

/**
 * A class defining static methods that return URLs for accessing components
 * and models. Works in GWT development mode and in production mode, accessing
 * the WMT API.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DataURL {

  /**
   * A helper that returns the base URL for the selected API (development or
   * production).
   * 
   * @param data the DataManager object for the WMT session
   */
  private static String getApiUrl(DataManager data) {
    return data.isApiDevelopmentMode() ? Constants.API_DEV_URL
        : Constants.API_URL;
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
   * Returns the new user account login URL provided by the API.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String newUserLogin(DataManager data) {
    if (data.isDevelopmentMode()) {
      return Constants.LOCAL_URL + "save/authenticate.json";
    } else {
      return getApiUrl(data) + Constants.NEW_USER_LOGIN_PATH;
    }
  }

  /**
   * Returns the account login URL provided by the API.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String login(DataManager data) {
    if (data.isDevelopmentMode()) {
      return Constants.LOCAL_URL + "save/authenticate.json";
    } else {
      return getApiUrl(data) + Constants.LOGIN_PATH;
    }
  }

  /**
   * Returns the account logout URL provided by the API.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String logout(DataManager data) {
    if (data.isDevelopmentMode()) {
      return Constants.LOCAL_URL + "save/authenticate.json";
    } else {
      return getApiUrl(data) + Constants.LOGOUT_PATH;
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
      return Constants.LOCAL_URL + "save/authenticate.json";
    } else {
      return getApiUrl(data) + Constants.USERNAME_PATH;
    }
  }

  /**
   * Returns the API URL for adding a new label to WMT.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String addLabel(DataManager data) {
    if (data.isDevelopmentMode()) {
      return Constants.LOCAL_URL + "save/labels.json";
    } else {
      return getApiUrl(data) + Constants.LABELS_NEW_PATH;
    }
  }

  /**
   * Returns the API URL for deleting a label from WMT.
   * 
   * @param data the DataManager object for the WMT session
   * @param labelId the id of the label to delete, an Integer
   */
  public static String deleteLabel(DataManager data, Integer labelId) {
    if (data.isDevelopmentMode()) {
      return Constants.LOCAL_URL + "save/labels.json";
    } else {
      return getApiUrl(data) + Constants.LABELS_DELETE_PATH
          + labelId.toString();
    }
  }

  /**
   * Returns the API URL for listing all labels belonging to the current user,
   * as well as all public labels, in WMT.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String listLabels(DataManager data) {
    if (data.isDevelopmentMode()) {
      return Constants.LOCAL_URL + "save/labels.json";
    } else {
      return getApiUrl(data) + Constants.LABELS_LIST_PATH;
    }
  }

  /**
   * Returns the API URL for attaching a new label to a model.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String addModelLabel(DataManager data) {
    if (data.isDevelopmentMode()) {
      return Constants.LOCAL_URL + "save/labels.json";
    } else {
      return getApiUrl(data) + Constants.LABELS_MODEL_ADD_PATH;
    }
  }

  /**
   * Returns the URL for the list of available components on the server.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String listComponents(DataManager data) {
    if (data.isDevelopmentMode()) {
      return Constants.LOCAL_URL + "data/components.json";
    } else {
      return getApiUrl(data) + Constants.COMPONENTS_LIST_PATH;
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
      return Constants.LOCAL_URL + "data/" + componentId + ".json";
    } else {
      return getApiUrl(data) + Constants.COMPONENTS_SHOW_PATH + componentId;
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
      return Constants.LOCAL_URL + "data/" + componentId + ".json";
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
      return Constants.LOCAL_URL + "save/model_list.json";
    } else {
      return getApiUrl(data) + Constants.MODELS_LIST_PATH;
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
      return Constants.LOCAL_URL + "save/open" + modelId.toString() + ".json";
    } else {
      return getApiUrl(data) + Constants.MODELS_OPEN_PATH + modelId.toString();
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
      return Constants.LOCAL_URL + "save/show" + modelId.toString() + ".json";
    } else {
      return getApiUrl(data) + Constants.MODELS_SHOW_PATH + modelId.toString();
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
      return Constants.LOCAL_URL + "save/saved.json";
    } else {
      return getApiUrl(data) + Constants.MODELS_NEW_PATH;
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
      return Constants.LOCAL_URL + "save/saved.json";
    } else {
      return getApiUrl(data) + Constants.MODELS_EDIT_PATH + modelId.toString();
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
      return Constants.LOCAL_URL + "save/saved.json";
    } else {
      return getApiUrl(data) + Constants.MODELS_DELETE_PATH
          + modelId.toString();
    }
  }

  /**
   * Returns the URL for posting a file associated with a model to the server.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String uploadFile(DataManager data) {
    if (data.isDevelopmentMode()) {
      return Constants.LOCAL_URL + "save/saved.json";
    } else {
      return getApiUrl(data) + Constants.MODELS_UPLOAD_PATH;
    }
  }

  /**
   * Returns the URL used to create a new model run.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String newModelRun(DataManager data) {
    if (data.isDevelopmentMode()) {
      return Constants.LOCAL_URL + "save/saved.json";
    } else {
      return getApiUrl(data) + Constants.RUN_NEW_PATH;
    }
  }

  /**
   * Returns the URL for API page displaying the status of all current model
   * runs on the server.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String showModelRun(DataManager data) {
    return getApiUrl(data) + Constants.RUN_SHOW_PATH;
  }

  /**
   * Returns the URL used to stage a model run.
   * 
   * @param data the DataManager object for the WMT session
   */
  public static String stageModelRun(DataManager data) {
    if (data.isDevelopmentMode()) {
      return Constants.LOCAL_URL + "save/saved.json";
    } else {
      return getApiUrl(data) + Constants.RUN_STAGE_PATH;
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
      return Constants.LOCAL_URL + "save/saved.json";
    } else {
      return getApiUrl(data) + Constants.RUN_LAUNCH_PATH;
    }
  }
}
