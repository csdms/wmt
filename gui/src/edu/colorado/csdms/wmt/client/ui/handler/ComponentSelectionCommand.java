/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.ComponentActionMenu;
import edu.colorado.csdms.wmt.client.ui.ComponentCell;

/**
 * Defines the action for when a user selects a component in a
 * {@link ComponentCell}; adds or sets a component in the ModelTree and sets up
 * the {@link ComponentActionMenu}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentSelectionCommand implements Command {

  private DataManager data;
  private ComponentCell cell;
  private String componentId;

  /**
   * Creates a new instance of {@link ComponentSelectionCommand}.
   * 
   * @param data the DataManager object for the WMT session
   * @param cell the {@link ComponentCell} this Command acts on
   * @param componentId the id of the selected component
   */
  public ComponentSelectionCommand(DataManager data, ComponentCell cell,
      String componentId) {
    this.data = data;
    this.cell = cell;
    this.componentId = componentId;
  }

  @Override
  public void execute() {
    updateComponentCell();
    data.getPerspective().getModelTree().addComponent(componentId,
        cell.getEnclosingTreeItem());
  }

  public void execute(Boolean useSetComponent) {
    updateComponentCell();
    if (useSetComponent) {
      data.getPerspective().getModelTree().setComponent(componentId,
          cell.getEnclosingTreeItem());
    } else {
      data.getPerspective().getModelTree().addComponent(componentId,
          cell.getEnclosingTreeItem());
    }
  }

  /**
   * A worker that updates the componentId, sets the display name, and deploys
   * the actionMenu of the {@link ComponentCell}.
   */
  public void updateComponentCell() {

    // Tell the ComponentCell what component it now holds.
    cell.setComponentId(componentId);
    String componentName = data.getComponent(componentId).getName();
    GWT.log("Selected component: " + componentName);

    // Display the name of the selected component.
    String displayName = cell.trimName(componentName);
    cell.getNameCell().setText(displayName);
    cell.addStyleDependentName("connected");

    // Replace the componentMenu with the actionMenu.
    ComponentActionMenu actionMenu = new ComponentActionMenu(data, cell);
    cell.getMenuItem().setSubMenu(actionMenu);

    // Update the tooltip text.
    String ctype;
    ctype =
        (cell.getEnclosingTreeItem().getParentItem() == null)
            ? DataManager.DRIVER : "component";
    String tooltip = "Model " + ctype + ": " + componentName + ". ";
    if (!ctype.matches(DataManager.DRIVER)) {
      tooltip += "Provides \"" + cell.getPortId() + "\" port. ";
    }
    cell.setTitle(tooltip);
    cell.getMenuCell().setTitle(
        "Click to get information, to view parameters, or to delete.");
  }
}
