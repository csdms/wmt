/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.ComponentCell;

/**
 * Defines the action for the "Delete" menu item in a {@link ComponentCell};
 * deletes the model component, replacing it with an open uses port of the
 * parent model component.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
@SuppressWarnings("unused")
public class ComponentDeleteCommand implements Command {

  private DataManager data;
  private ComponentCell cell;
  private String componentId;

  /**
   * Creates a new instance of {@link ComponentDeleteCommand}.
   * 
   * @param data the DataManager object for the WMT session
   * @param cell the {@link ComponentCell} this Command acts on
   */
  public ComponentDeleteCommand(DataManager data, ComponentCell cell) {
    this.data = data;
    this.cell = cell;
    this.componentId = cell.getComponentId();
  }

  @Override
  public void execute() {
    GWT.log("Delete " + data.getComponent(componentId).getName());
  }
}
