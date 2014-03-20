package edu.colorado.csdms.wmt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.data.Component;
import edu.colorado.csdms.wmt.client.data.ModelJSO;
import edu.colorado.csdms.wmt.client.data.ModelMetadataJSO;
import edu.colorado.csdms.wmt.client.data.Port;

/**
 * A ModelTree is used to graphically represent the construction of a
 * simulation through component models, each represented by a
 * {@link ModelCell}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelTree extends Tree {

  public DataManager data; // experimenting with a public member variable

  /**
   * Creates a ModelTree with an open "driver" port.
   * 
   * @param data A DataManager object.
   */
  public ModelTree(DataManager data) {

    this.data = data;
    initializeTree();
    this.data.setModelTree(this);
  }

  /**
   * A worker that sets up the root TreeItem (the "driver") of the ModelTree.
   * It also initializes the {@link ModelJSO} and {@link ModelMetadataJSO}
   * objects used to save the model created with this ModelTree.
   * <p>
   * The returned Port is optional.
   */
  public Port initializeTree() {

    this.clear();
    Port driverPort = new Port("driver", true);
    addTreeItem(driverPort, null);

    ModelJSO model = (ModelJSO) ModelJSO.createObject();
    data.setModel(model);
    ModelMetadataJSO metadata =
        (ModelMetadataJSO) ModelMetadataJSO.createObject();
    data.setMetadata(metadata);

    return driverPort;
  }

  /**
   * Adds a new TreeItem with a ModelCell to the ModelTree at the targeted
   * leaf location, or at the root if the target is missing.
   * 
   * @param port the Port used to create a ModelCell for the TreeItem
   * @param target the targeted leaf TreeItem
   * @return the reference to the created TreeItem
   */
  public TreeItem addTreeItem(Port port, TreeItem target) {

    ModelCell cell = new ModelCell(port, Component.makeInfoComponent());

    TreeItem item = null;
    if (target == null) {
      item = new TreeItem(cell);
      this.addItem(item);
    } else {
      item = target.addItem(cell);
      item.setStyleName("wmt-TreeItem");
    }
    cell.setParentTreeItem(item); // Clumsy

    return item;
  }

  /**
   * Adds a Component to the ModelCell used by the targeted TreeItem. Uses
   * {@link #setComponent(Component, TreeItem)}.
   * 
   * @param component the Component to add
   * @param target the TreeItem to which the Component is to be added
   */
  public void addComponent(Component component, TreeItem target) {

    GWT.log("Adding component: " + component.getName());
    this.setComponent(component, target);

    // Ensure that the (class) component replaces the model component.
    data.replaceModelComponent(data.getComponent(component.getId()));

    // Is this the driver? If so, display the component's parameters. Also
    // suggest a model name. 
    if (this.getItem(0).equals(target)) {
      data.getModel().setName(
          component.getName() + " " + data.saveAttempts.toString());
      data.setSelectedComponent(component.getId());
      data.getPerspective().getParameterTable().loadTable();
    }
    
    // Mark the model state as unsaved.
    data.modelIsSaved(false);
    data.getPerspective().setModelPanelTitle();
  }

  /**
   * Adds a Component to the ModelCell used by the targeted TreeItem.
   * 
   * @param component the Component to add
   * @param target the TreeItem to which the Component is to be added
   */
  public void setComponent(Component component, TreeItem target) {

    // Get the ModelCell used by the TreeItem target.
    ModelCell cell = (ModelCell) target.getWidget();

    // If the Component already exists at a higher level in the ModelTree, set
    // a link to it and exit.
    Component connected1 = hasConnectedInstance(cell.getPortCell().getPort());
    if (connected1 != null) {
      cell.setComponentCell(cell.new ComponentCell(connected1));
      cell.getComponentCell().addStyleDependentName("linked");
      cell.getComponentCell().isLinked(true);
      return;
    }

    // Connect the new Component to the ModelCell.
    cell.setComponentCell(cell.new ComponentCell(component));
    cell.isConnected(true);

    // Add new, empty, TreeItems for the "uses" ports of the Component.
    for (int i = 0; i < component.getUsesPorts().length; i++) {

      Port newPort = new Port();
      newPort.setId(component.getUsesPorts()[i].getId());
      newPort.isRequired(component.getUsesPorts()[i].isRequired());

      TreeItem newItem = addTreeItem(newPort, target);

      // If this new Port has a connected Component higher in the ModelTree,
      // set a link to it.
      ModelCell newCell = (ModelCell) newItem.getWidget();
      Component connected2 = hasConnectedInstance(newPort);
      if (connected2 != null) {
        newCell.setComponentCell(newCell.new ComponentCell(connected2));
        newCell.getComponentCell().addStyleDependentName("linked");
        newCell.getComponentCell().isLinked(true);
      }
    }
  }

  /**
   * Iterate through the {@link TreeItem}s of this ModelTree, finding what
   * {@link ModelCell}s have open PortCells. Add the cell to the
   * openModelCells List. The iterator descends the tree from top to bottom,
   * ignoring the level (and sublevels, etc.) of the children. 
   * 
   * @return a List of ModelCells with open ports
   */
  public List<ModelCell> findOpenModelCells() {

    List<ModelCell> openModelCells = new ArrayList<ModelCell>();

    Iterator<TreeItem> iter = this.treeItemIterator();
    while (iter.hasNext()) {
      TreeItem treeItem = (TreeItem) iter.next();
      ModelCell cell = (ModelCell) treeItem.getWidget();
      if (cell.getComponentCell().getComponent().getId() == null) {
        openModelCells.add(cell);
      }
    }

    return openModelCells;
  }

  /**
   * Finds the open {@link ModelCell}s among the children of a specified
   * {@link TreeItem} in the ModelTree. This search doesn't descend lower than
   * the children of the input parent TreeItem.
   * 
   * @param parent a TreeItem in the ModelTree
   * @return a List of ModelCells with open ports
   */
  public List<ModelCell> findOpenModelCells(TreeItem parent) {

    List<ModelCell> openModelCells = new ArrayList<ModelCell>();

    if (parent != null) {
      for (int i = 0; i < parent.getChildCount(); i++) {
        TreeItem child = parent.getChild(i);
        ModelCell cell = (ModelCell) child.getWidget();
        if (cell.getComponentCell().getComponent().getId() == null) {
          openModelCells.add(cell);
        }
      }
    }

    return openModelCells;
  }
  
  
  /**
   * Checks whether a given component is present in the ModelTree. This is an
   * overloaded version of {@link #isComponentPresent(String)}.
   * 
   * @param component a Component to check
   * @return true if the component is in the ModelTree
   */
  public Boolean isComponentPresent(Component component) {
    String componentId = component.getId();
    return isComponentPresent(componentId);
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
        ModelCell cell = (ModelCell) treeItem.getWidget();
        if (cell.getComponentCell().getComponent().getId() != null) {
          componentIsPresent =
              cell.getComponentCell().getComponent().getId().matches(
                  componentId);
        }
      }
    }

    return componentIsPresent;
  }

  /**
   * Checks whether the input Port has already appeared higher up in the
   * ModelTree hierarchy, and has a connected Component. If so, the Component
   * is returned; otherwise, a null object is returned.
   * <p>
   * I'm concerned that this may be inefficient, and slow to iterate through a
   * large ModelTree, since each TreeItem is hit.
   * 
   * @param port the Port object
   */
  public Component hasConnectedInstance(Port port) {

    Component connected = null;

    Iterator<TreeItem> iter = this.treeItemIterator();
    while (iter.hasNext()) {
      TreeItem treeItem = (TreeItem) iter.next();
      ModelCell cell = (ModelCell) treeItem.getWidget();
      if (cell.isConnected()) {
        Component cellComponent = cell.getComponentCell().getComponent();
        String cellPortId = cell.getPortCell().getPort().getId();
        if (cellPortId.matches("driver")) {
          if (cellComponent.getProvidesPorts().length > 0) {
            cellPortId = cellComponent.getProvidesPorts()[0].getId();
          }
        }
        // GWT.log("match? " + cellPortId + " " + port.getId());
        if (cellPortId.matches(port.getId())) {
          connected = cellComponent;
        }
      }
    }

    return connected;
  }

}
