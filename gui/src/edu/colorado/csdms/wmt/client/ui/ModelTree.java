/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.data.ModelJSO;
import edu.colorado.csdms.wmt.client.data.ModelMetadataJSO;
import edu.colorado.csdms.wmt.client.ui.handler.ComponentSelectionCommand;

/**
 * A ModelTree is used to graphically represent the construction of a simulation
 * through component models, each represented by a {@link ModelCell}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelTree extends Tree {

  private DataManager data;
  private ComponentCell driverComponentCell;

  /**
   * Creates a ModelTree with an open "driver" port.
   * 
   * @param data A DataManager object.
   */
  public ModelTree(DataManager data) {

    this.data = data;
    initializeTree();
  }

  /**
   * A worker that sets up the root TreeItem (the "driver") of the ModelTree. It
   * also initializes the {@link ModelJSO} and {@link ModelMetadataJSO} objects
   * used to save the model created with this ModelTree.
   */
  public void initializeTree() {

    this.clear();

    driverComponentCell = new ComponentCell(data);
    TreeItem driverItem = new TreeItem(driverComponentCell);
    driverComponentCell.setEnclosingTreeItem(driverItem);
    this.addItem(driverItem);

    ModelJSO model = (ModelJSO) ModelJSO.createObject();
    data.setModel(model);
    ModelMetadataJSO metadata =
        (ModelMetadataJSO) ModelMetadataJSO.createObject();
    data.setMetadata(metadata);
  }

  public ComponentCell getDriverComponentCell() {
    return driverComponentCell;
  }

  public void setDriverComponentCell(ComponentCell driverComponentCell) {
    this.driverComponentCell = driverComponentCell;
  }

  /**
   * Adds a new TreeItem with a {@link ComponentCell} to the ModelTree at the
   * targeted leaf location. Uses
   * {@link #insertTreeItem(String, TreeItem, Integer)}.
   * 
   * @param portId the id of the exposed uses port at the leaf location
   * @param target the targeted leaf TreeItem
   * @return the reference to the created TreeItem
   */
  public TreeItem addTreeItem(String portId, TreeItem target) {
    return insertTreeItem(portId, target, target.getChildCount());
  }

  /**
   * Inserts a new TreeItem with a {@link ComponentCell} to the ModelTree at the
   * targeted leaf location.
   * 
   * @param portId the id of the exposed uses port at the leaf location
   * @param target the targeted leaf TreeItem
   * @param index the index into the children of the targeted TreeItem
   * @return the reference to the created TreeItem
   */
  public TreeItem insertTreeItem(String portId, TreeItem target, Integer index) {
    ComponentCell cell = new ComponentCell(data, portId);
    TreeItem item = target.insertItem(index, cell);    
    item.setStyleName("wmt-TreeItem");
    cell.setEnclosingTreeItem(item);
    return item;
  }

  /**
   * Adds a component to the {@link ComponentCell} used by the targeted
   * TreeItem. Uses {@link #setComponent(String, TreeItem)}.
   * 
   * @param componentId the id of the component to add
   * @param target the TreeItem to which the component is to be added
   */
  public void addComponent(String componentId, TreeItem target) {

    String componentName = data.getComponent(componentId).getName();
    GWT.log("Adding component: " + componentName);
    this.setComponent(componentId, target);
    target.setState(true);

    // Ensure that the (class) component replaces the model component.
    data.replaceModelComponent(data.getComponent(componentId));

    // Is this the driver? If so, display the component's parameters. Also
    // suggest a model name.
    if (this.getItem(0).equals(target)) {
      data.getModel().setName(
          componentName + " " + data.saveAttempts.toString());
      data.setSelectedComponent(componentId);
      data.getPerspective().getParameterTable().loadTable(componentId);
    }

    // Mark the model state as unsaved.
    data.modelIsSaved(false);
    data.getPerspective().setModelPanelTitle();
  }

  /**
   * Sets the desired component, and its {@link ComponentCell}, in the targeted
   * TreeItem.
   * 
   * @param componentId the id of the component to set
   * @param target the TreeItem where the component is to be set
   */
  public void setComponent(String componentId, TreeItem target) {

    // If this component already exists elsewhere in the ModelTree, set a link
    // to it and exit.
    if (thisComponentIsADuplicate(componentId)) {
      GWT.log("This component is a duplicate!");
      ComponentCell cell = (ComponentCell) target.getWidget();
      cell.isLinked(true);
      cell.getNameCell().addStyleDependentName("linked");
      return;
    }

    // Add new TreeItems with ComponentCells for the "uses" ports of the
    // component.
    Integer nPorts = data.getComponent(componentId).getUsesPorts().length();
    if (nPorts == 0) {
      return;
    }
    for (int i = 0; i < nPorts; i++) {
      String portId =
          data.getComponent(componentId).getUsesPorts().get(i).getId();
      TreeItem newItem = addTreeItem(portId, target);

      // If the new port has a connected component higher in the ModelTree,
      // create the component and set a link to it.
      String connectedId = providesComponentIsADuplicate(portId);
      if (connectedId != null) {
        GWT.log("This provides port has been used elsewhere!");
        ComponentCell newCell = (ComponentCell) newItem.getWidget();
        ComponentSelectionCommand cmd =
            new ComponentSelectionCommand(data, newCell, connectedId);
        cmd.updateComponentCell();
        newCell.isLinked(true);
        newCell.getNameCell().addStyleDependentName("linked");
      }
    }
  }

  /**
   * Iterate through the {@link TreeItem}s of this ModelTree, finding what
   * {@link ComponentCell}s have open ports. Add the cell to a List, which is
   * returned. The iterator descends the tree from top to bottom, ignoring the
   * level (and sublevels, etc.) of the children.
   * 
   * @return a List of open ComponentCells
   */
  public List<ComponentCell> findOpenComponentCells() {
    List<ComponentCell> openComponentCells = new ArrayList<ComponentCell>();
    Iterator<TreeItem> iter = this.treeItemIterator();
    while (iter.hasNext()) {
      TreeItem treeItem = (TreeItem) iter.next();
      ComponentCell cell = (ComponentCell) treeItem.getWidget();
      if (cell.getComponentId() == null) {
        openComponentCells.add(cell);
      }
    }
    return openComponentCells;
  }

  /**
   * Finds the open {@link ComponentCell}s among the children of a specified
   * {@link TreeItem} in the ModelTree. This search doesn't descend lower than
   * the children of the input parent TreeItem.
   * 
   * @param parent a TreeItem in the ModelTree
   * @return a List of ComponentCells with open ports
   */
  public List<ComponentCell> findOpenComponentCells(TreeItem parent) {
    List<ComponentCell> openComponentCells = new ArrayList<ComponentCell>();
    if (parent != null) {
      for (int i = 0; i < parent.getChildCount(); i++) {
        TreeItem child = parent.getChild(i);
        ComponentCell cell = (ComponentCell) child.getWidget();
        if (cell.getComponentId() == null) {
          openComponentCells.add(cell);
        }
      }
    }
    return openComponentCells;
  }

  /**
   * Checks whether a given component is present in the ModelTree.
   * 
   * @param componentId the id of component to check
   * @return true if the component is in the ModelTree
   */
  public Boolean isComponentPresent(String componentId) {
    Boolean componentIsPresent = false;
    if (componentId != null) {
      Iterator<TreeItem> iter = this.treeItemIterator();
      while (iter.hasNext() && !componentIsPresent) {
        TreeItem treeItem = (TreeItem) iter.next();
        ComponentCell cell = (ComponentCell) treeItem.getWidget();
        if (cell.getComponentId() != null) {
          componentIsPresent = cell.getComponentId().matches(componentId);
        }
      }
    }
    return componentIsPresent;
  }

  /**
   * Checks whether the input port has appeared higher in the ModelTree
   * hierarchy, and has a connected component. If so, the id of the component is
   * returned; otherwise, a null object is returned.
   * <p>
   * I'm concerned that this technique may be inefficient since each TreeItem is
   * hit in iterating through the ModelTree.
   * 
   * @param portId the id the port to check
   */
  public Boolean thisComponentIsADuplicate(String portId) {

    GWT.log("Checking for connection on port: " + portId);

    Integer nMatches = 0;

    Iterator<TreeItem> iter = this.treeItemIterator();
    while (iter.hasNext()) {

      TreeItem treeItem = (TreeItem) iter.next();
      ComponentCell cell = (ComponentCell) treeItem.getWidget();

      if (cell.getComponentId() != null) {
        String cellPortId = cell.getPortId();

        // When the port is listed as "driver", it obscures the provides port
        // of the connected component. Find the provides port and use it.
        if (cellPortId.matches(DataManager.DRIVER)) {
          if (data.getComponent(cell.getComponentId()).nProvidesPorts() > 0) {
            cellPortId =
                data.getComponent(cell.getComponentId()).getProvidesPorts()
                    .get(0).getId();
          }
        }

        if (cellPortId.matches(portId)) {
          nMatches++;
          if (nMatches > 1) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Checks whether the input provides port has been filled higher in the 
   * ModelTree. Returns the id of the component that fills it.
   * 
   * @param portId the id the port to check
   */
  public String providesComponentIsADuplicate(String portId) {

    GWT.log("Checking for connection on port: " + portId);

    Iterator<TreeItem> iter = this.treeItemIterator();
    while (iter.hasNext()) {

      TreeItem treeItem = (TreeItem) iter.next();
      ComponentCell cell = (ComponentCell) treeItem.getWidget();

      if (cell.getComponentId() != null) {
        String cellPortId = cell.getPortId();

        // When the port is listed as "driver", it obscures the provides port
        // of the connected component. Find the provides port and use it.
        if (cellPortId.matches(DataManager.DRIVER)) {
          if (data.getComponent(cell.getComponentId()).nProvidesPorts() > 0) {
            cellPortId =
                data.getComponent(cell.getComponentId()).getProvidesPorts()
                    .get(0).getId();
          }
        }

        if (cellPortId.matches(portId)) {
          return cell.getComponentId();
        }
      }
    }
    return null;
  }
}
