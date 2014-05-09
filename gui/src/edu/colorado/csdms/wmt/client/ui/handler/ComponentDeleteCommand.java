/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.TreeItem;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.ComponentCell;
import edu.colorado.csdms.wmt.client.ui.ComponentSelectionMenu;
import edu.colorado.csdms.wmt.client.ui.ModelTree;

/**
 * Defines the action for the "Delete" menu item in a {@link ComponentCell};
 * deletes the model component, replacing it with an open uses port of the
 * parent model component.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
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

    GWT.log("Delete: " + data.getComponent(componentId).getName());

    // For convenience, get the ModelTree reference and the reference to the
    // targeted TreeItem.
    ModelTree tree = data.getPerspective().getModelTree();
    TreeItem target = cell.getEnclosingTreeItem();

    // Delete all children of the target TreeItem.
    target.removeItems();

    // If the parameters of the about-to-be-deleted component, or any of its
    // children, are displayed, clear the ParameterTable.
    String showing = data.getPerspective().getParameterTable().getComponentId();
    if ((showing != null)
        && (componentId.contains(showing) || !tree.isComponentPresent(showing))) {
      data.getPerspective().getParameterTable().clearTable();
    }

    // If this isn't the driver, delete the target TreeItem and replace it with
    // a new one sporting the appropriate open uses port. If it is the driver,
    // reinitialize the ModelTree, update the available components and deselect
    // the component label.
    if (target.getParentItem() != null) {
      TreeItem parent = target.getParentItem();
      Integer targetIndex = parent.getChildIndex(target);
      parent.removeItem(target);
      tree.insertTreeItem(cell.getPortId(), parent, targetIndex);
    } else {
      tree.initializeTree();
      ((ComponentSelectionMenu) tree.getDriverComponentCell()
          .getComponentMenu()).updateComponents();
      try {
        data.modelLabels.get(data.getComponent(componentId).getName())
            .isSelected(false);
        data.getPerspective().getLabelsMenu().populateMenu();
      } catch (Exception e) {
        GWT.log(e.toString());
      }
      data.saveAttempts++;
    }

    // Update the title of the Model tab.
    data.modelIsSaved(false);
    data.getPerspective().setModelPanelTitle();
  }
}
