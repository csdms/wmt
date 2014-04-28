/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;

/**
 * Handles a click on the "Reset" button in the ParameterActionPanel. Calls
 * {@link DataManager#replaceModelComponent()} to replace the current model 
 * component with the default component, then displays its parameters in
 * the ParameterTable.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ParameterActionPanelResetHandler implements ClickHandler {

  private DataManager data;
  private String componentId;
  
  /**
   * Creates a new instance of {@link ParameterActionPanelResetHandler}.
   * 
   * @param data the DataManager object for the WMT session
   * @param componentId the id of the component to be replaced
   */
  public ParameterActionPanelResetHandler(DataManager data, String componentId) {
    this.data = data;
    this.componentId = componentId;
  }
  
  @Override
  public void onClick(ClickEvent event) {
    data.replaceModelComponent(data.getComponent(componentId));
    data.getPerspective().getParameterTable().clearTable();
    data.getPerspective().getParameterTable().loadTable(componentId);
    data.modelIsSaved(false);
    data.getPerspective().setModelPanelTitle();
  }
}
