/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

import com.google.gwt.core.client.GWT;

import edu.colorado.csdms.wmt.client.ui.DataManager;

/**
 * Excellent! This allows me to set URLs for GWT development mode in addition
 * to using Eric's API in production mode.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DataURL {
  
  private static final String API_URL = "http://csdms.colorado.edu/wmt/";  
  private static final String LOCAL_URL = GWT.getHostPageBaseURL();

  private static final String COMPONENT_LIST_URL = API_URL + "components/list";
  private static final String COMPONENT_SHOW_URL = API_URL + "components/show/";  
  
  private static final String MODEL_LIST_URL = API_URL + "models/list";
  private static final String MODEL_OPEN_URL = API_URL + "models/open/";
  private static final String MODEL_SHOW_URL = API_URL + "models/show/";
  private static final String MODEL_NEW_URL = API_URL + "models/new";

  /**
   * TODO
   * 
   * @param data
   * @return
   */
  public static String listComponents(DataManager data) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "data/components.json"; 
    } else {
      return COMPONENT_LIST_URL;
    }
  }
  
  /**
   * TODO
   * 
   * @param data
   * @param componentId
   * @return
   */
  public static String showComponent(DataManager data, String componentId) {
    if (data.isDevelopmentMode()) {
      return LOCAL_URL + "data/" + componentId + ".json";
    } else {
      return COMPONENT_SHOW_URL + componentId;
    }
  }

//  public static String listModels(DataManager data) {
//    if (data.isDevelopmentMode()) {
//      return LOCAL_URL + "save/" + componentId + ".json";
//    } else {
//      return MODEL_LIST_URL;
//    }
//  }
}
