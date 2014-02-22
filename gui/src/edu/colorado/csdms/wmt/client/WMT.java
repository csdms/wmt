/**
 * <License>
 */
package edu.colorado.csdms.wmt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import edu.colorado.csdms.wmt.client.data.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.DataManager;
import edu.colorado.csdms.wmt.client.ui.Perspective;

/**
 * WMT is the CSDMS Web Modeling Tool.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class WMT implements EntryPoint {

  private Perspective perspective;
  private DataManager data;

  /**
   * This is the entry point method. It draws the views that make up the WMT
   * GUI. It loads information about component models from a set of JSON
   * files, then populates the GUI with this information.
   */
  public void onModuleLoad() {

    // Initialize the DataManager object.
    data = new DataManager();
    
    // Are we running in GWT development mode or in production mode?
    data.isDevelopmentMode(!GWT.isProdMode() && GWT.isClient());

    // Set up the basic framework of views for the GUI.
    perspective = new Perspective(data);
    RootLayoutPanel.get().add(perspective);
    perspective.initializeModel();
    perspective.initializeParameterTable();

    // Retrieve (asynchronously) and store the list of available components
    // and models. Note that when DataTransfer#getComponentList completes,
    // it immediately starts pulling component data from the server with calls
    // to DataTransfer#getComponent. Asynchronous requests are cool!
    DataTransfer.getComponentList(data);
    DataTransfer.getModelList(data);
  }
}
