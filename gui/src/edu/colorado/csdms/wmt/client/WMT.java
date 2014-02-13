/**
 * <License>
 */
package edu.colorado.csdms.wmt.client;

import java.util.Map.Entry;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import edu.colorado.csdms.wmt.client.data.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.DataManager;
import edu.colorado.csdms.wmt.client.ui.Perspective;

/**
 * The CSDMS Web Modeling Tool.
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

    // Set up the basic framework of views for the GUI.
    perspective = new Perspective(data);
    RootLayoutPanel.get().add(perspective);
    perspective.initializeArena();
    perspective.initializeParameterTable();

    // Retrieve (asynchronously) and store the list of available components
    // and models. Note that when DataTransfer#getComponentList completes,
    // it immediately starts pulling component data from the server with calls
    // to DataTransfer#getComponent. Asynchronous requests are cool!
    DataTransfer.getComponentList(data);
    DataTransfer.getModelList(data);

    // Load the JSON files used to populate the GUI. A HashMap is used with
    // the file basename as the key and the file type as the value.
//    data.files.put("components.json", "component");
//    data.files.put("avulsion.json", "parameter");
//    data.files.put("cem.json", "parameter");
//    data.files.put("hydrotrend.json", "parameter");
//    data.files.put("waves.json", "parameter");
//    data.files.put("avulsion1.json", "new");
//    for (Entry<String, String> entry : data.files.entrySet()) {
//      DataTransfer.get(data, entry.getKey(), entry.getValue());
//    }

    // Disable the native browser right-click context menu.
    RootLayoutPanel.get().addDomHandler(new ContextMenuHandler() {
      @Override
      public void onContextMenu(ContextMenuEvent event) {
        event.preventDefault();
        event.stopPropagation();
      }
    }, ContextMenuEvent.getType());
  }
}
