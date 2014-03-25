/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Command;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.ComponentCell;

/**
 * Defines the action for the "Show parameters" menu item in a
 * {@link ComponentCell}; displays the parameters of the component in the 
 * ParameterTable.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentShowParametersCommand implements Command {

  private DataManager data;
  private String componentId;

  /**
   * Creates a new instance of {@link ComponentShowParametersCommand}.
   * 
   * @param data the DataManager object for the WMT session
   * @param componentId the id of the component to get info on
   */
  public ComponentShowParametersCommand(DataManager data, String componentId) {
    this.data = data;
    this.componentId = componentId;
  }

  @Override
  public void execute() {
    GWT.log("Show parameters for: " + data.getComponent(componentId).getName());
    data.getPerspective().getParameterTable().clearTable();
    data.getPerspective().getParameterTable().loadTable(componentId);
  }
}
