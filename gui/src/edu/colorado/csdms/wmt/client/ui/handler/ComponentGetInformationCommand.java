/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.ComponentCell;
import edu.colorado.csdms.wmt.client.ui.widgets.ComponentInfoDialogBox;

/**
 * Defines the action for the "Get info" menu item in a {@link ComponentCell};
 * shows the {@link ComponentInfoDialogBox}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
@SuppressWarnings("unused")
public class ComponentGetInformationCommand implements Command {

  private DataManager data;
  private ComponentCell cell;
  private String componentId;

  /**
   * Creates a new instance of {@link ComponentGetInformationCommand}.
   * 
   * @param data the DataManager object for the WMT session
   * @param cell the {@link ComponentCell} this Command acts on
   */
  public ComponentGetInformationCommand(DataManager data, ComponentCell cell) {
    this.data = data;
    this.cell = cell;
    this.componentId = cell.getComponentId();
  }

  @Override
  public void execute() {
    GWT.log("Get info for: " + data.getComponent(componentId).getName());
    ComponentInfoDialogBox componentInfoDialogBox =
        data.getPerspective().getComponentInfoBox();
    componentInfoDialogBox.update(data.getComponent(componentId));
    componentInfoDialogBox.center();
  }
}
