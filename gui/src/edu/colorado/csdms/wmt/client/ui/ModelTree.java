package edu.colorado.csdms.wmt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

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
public class ModelTree extends Tree implements DragOverHandler, DropHandler {

  public DataManager data; // experimenting with a public member variable
  private List<ModelCell> openModelCells;

  /**
   * Creates a ModelTree with an open "driver" port.
   * 
   * @param data A DataManager object.
   */
  public ModelTree(DataManager data) {

    this.data = data;
    this.openModelCells = new ArrayList<ModelCell>();

    initializeTree();
    this.data.setModelTree(this);

    // Set up ModelTree event handlers.
    addDomHandler(this, DragOverEvent.getType());
    addDomHandler(this, DropEvent.getType());
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
    data.getModel().setName("Model " + data.saveAttempts.toString());

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

    // Mark the model as unsaved with an asterisk. Is this the driver port? If
    // so, also suggest a model name.
    if (this.getItem(0).equals(target)) {
      data.getModel().setName(
          component.getName() + " " + data.saveAttempts.toString());
    }
    data.modelIsSaved(false);
    data.getPerspective().setModelPanelTitle();
    
    this.setComponent(component, target);
    
    // Ensure that the (class) component replaces the model component.
    data.replaceModelComponent(data.getComponent(component.getId()));
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

    // Update the sensitivity of the DragCells in the ComponentList.
    data.getComponentList().setCellSensitivity();
  }  
  
  /**
   * Iterate through the TreeItems of this ModelTree, finding what ModelCells
   * have open PortCells. Add the cell to the openModelCells List.
   * @return 
   * 
   * @return a Vector of ModelCells with open ports.
   */
  public List<ModelCell> findOpenModelCells() {

    // Always start with a fresh list.
    openModelCells.clear();

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

  /**
   * Handles events when a drag item hovers over a drop target. Note that
   * events are continuously spawned, so there can be a lot.
   * <p>
   * This method is apparently needed to have drop events.
   */
  @Override
  public void onDragOver(DragOverEvent event) {
    return;
  }

  /**
   * This is needed on Firefox (but not on Chrome or Safari) to prevent a
   * webpage linked to a keyword == the dragged component id from being loaded
   * if a component is dropped somewhere in the ModelTree other than the
   * component drop area. (For example, "avulsion" is currently redirected to
   * "http://jetflow.com". I think they make hydration backpacks.)
   * ComponentCell#onDrop is the true drop handler for the ModelTree.
   * <p>
   * Whew. Took some detective work.
   */
  @Override
  public void onDrop(DropEvent event) {
    event.preventDefault();
    event.stopPropagation();
  }
}
