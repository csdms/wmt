/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
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
   * A worker that sets up the root TreeItem (the "driver") of the ModelTree.
   * It also initializes the {@link ModelJSO} and {@link ModelMetadataJSO}
   * objects used to save the model created with this ModelTree.
   */
  public void initializeTree() {

    this.clear();
    
    driverComponentCell = new ComponentCell(data);
    HTML driverCell = new HTML("<i class='fa fa-play-circle fa-2x'></i>");
    driverCell.setStyleName("mwmb-driverCell");
    driverCell.setTitle("Run the model");
    
    Grid driverGrid = new Grid(1, 2);
    driverGrid.setWidget(0, 0, driverComponentCell);
    driverGrid.setWidget(0, 1, driverCell);
    
    TreeItem driverItem = new TreeItem(driverGrid);
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

  @Deprecated
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
    Grid container = new Grid(1, 1);
    container.setWidget(0, 0, cell);
    TreeItem item = target.insertItem(index, container);
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
    
    // If the component already exists at a higher level in the ModelTree, set
    // a link to it and exit.

    // Add new TreeItems with ComponentCells for the "uses" ports of the
    // component.
    Integer nPorts = data.getComponent(componentId).getUsesPorts().length();
    if (nPorts == 0) {
      return;
    }
    for (int i = 0; i < nPorts; i++) {
      String portId = data.getComponent(componentId).getUsesPorts().get(i).getId();
      addTreeItem(portId, target);
    }
  }

  @Deprecated
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

  @Deprecated
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

    // Update the sensitivity of the DragCells in the ComponentList.
    data.getPerspective().getComponentList().setCellSensitivity();
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
      Grid grid = (Grid) treeItem.getWidget();
      ComponentCell cell = (ComponentCell) grid.getWidget(0, 0);
      if (cell.getComponentId() == null) {
        openComponentCells.add(cell);
      }
    }
    return openComponentCells;
  }

  @Deprecated
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
        Grid grid = (Grid) child.getWidget();
        ComponentCell cell = (ComponentCell) grid.getWidget(0, 0);
        if (cell.getComponentId() == null) {
          openComponentCells.add(cell);
        }
      }
    }
    return openComponentCells;
  }
  
  @Deprecated
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
        Grid grid = (Grid) treeItem.getWidget();
        ComponentCell cell = (ComponentCell) grid.getWidget(0, 0);
        if (cell.getComponentId() != null) {
          componentIsPresent = cell.getComponentId().matches(componentId);
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
