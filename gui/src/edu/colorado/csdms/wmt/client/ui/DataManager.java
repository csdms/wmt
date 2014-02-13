/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.user.client.Window;

import edu.colorado.csdms.wmt.client.data.ComponentJSO;
import edu.colorado.csdms.wmt.client.data.ModelJSO;

/**
 * A class for storing and sharing data, as well as the state of UI elements,
 * within WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DataManager {

  private Perspective perspective;
  private ComponentList componentList;
  private ModelTree modelTree;
  private ParameterTable parameterTable;

  private LinkedHashMap<String, ComponentJSO> components;
  private String draggedComponent;
  private String selectedComponent;
  private ModelJSO model;
  private String modelString; // stringified JSON

  // Experiment with public members, for convenience.
  public List<String> componentIdList;
  public List<Integer> modelIdList;
  public List<String> modelNameList;

  /**
   * Initializes the DataManager object used in a WMT session.
   */
  public DataManager() {
    componentIdList = new ArrayList<String>();
    components = new LinkedHashMap<String, ComponentJSO>();
    modelIdList = new ArrayList<Integer>();
    modelNameList = new ArrayList<String>();
  }

  /**
   * Returns the Perspective object used to organize the WMT views.
   */
  public Perspective getPerspective() {
    return perspective;
  }

  /**
   * Sets the Perspective object used to organize the WMT views.
   * 
   * @param perspective the perspective to set
   */
  public void setPerspective(Perspective perspective) {
    this.perspective = perspective;
  }

  /**
   * Returns the ComponentJSO object matching the given component id.
   * 
   * @param componentId the id of the desired component, a String.
   */
  public ComponentJSO getComponent(String componentId) {
    return components.get(componentId);
  }

  /**
   * Returns the <em>all</em> the components in a LinkedHashMap of
   * componentId-ComponentJSO pairs.
   */
  public LinkedHashMap<String, ComponentJSO> getComponents() {
    return this.components;
  }

  /**
   * Adds a component to the LinkedHashMap of components.
   * <p>
   * Once all the components have been pulled from the server, initialize the
   * ComponentList.
   * 
   * @param component the component to add, a ComponentJSO object
   */
  public void setComponent(ComponentJSO component) {
    this.components.put(component.getId(), component);

    // Sometimes get error 500.
    Window.alert(component.getId() + " " + this.components.size() + "/"
        + this.componentIdList.size());
    
    if (this.components.size() == this.componentIdList.size()) {
      perspective.initializeComponentList();
    }
  }

  /**
   * Sets a LinkedHashMap of componentId-ComponentJSO pairs representing
   * <em>all</em> the components.
   * 
   * @param components all your components are belong to us
   */
  public void setComponents(LinkedHashMap<String, ComponentJSO> components) {
    this.components = components;
  }

  /**
   * Returns the model displayed in the ModelTree, a ModelJSO object.
   */
  public ModelJSO getModel() {
    return model;
  }

  /**
   * Sets the model displayed in the ModelTree.
   * 
   * @param model the model to set, a ModelJSO object
   */
  public void setModel(ModelJSO model) {
    this.model = model;
  }

  /**
   * Gets the stringified model JSON created by DataTransfer#serialize.
   */
  public String getModelString() {
    return modelString;
  }

  /**
   * Stores the stringified model JSON created by DataTransfer#serialize.
   * 
   * @param modelString the modelString to set
   */
  public void setModelString(String modelString) {
    this.modelString = modelString;
  }

  /**
   * Returns a reference to the ComponentList used in the "Components" tab of a
   * WMT session.
   */
  public ComponentList getComponentList() {
    return componentList;
  }

  /**
   * Stores a reference to the ComponentList used in the "Components" tab of a
   * WMT session.
   * 
   * @param componentList the ComponentList instance
   */
  public void setComponentList(ComponentList componentList) {
    this.componentList = componentList;
  }

  /**
   * Returns a reference to the ModelTree used in a WMT session.
   */
  public ModelTree getModelTree() {
    return modelTree;
  }

  /**
   * Stores a reference to the ModelTree used in a WMT session.
   * 
   * @param modelTree the ModelTree instance
   */
  public void setModelTree(ModelTree modelTree) {
    this.modelTree = modelTree;
  }

  /**
   * Returns a reference to the ParameterTable used in the "Parameters" tab of a
   * WMT session.
   */
  public ParameterTable getParameterTable() {
    return parameterTable;
  }

  /**
   * Stores a reference to the ParameterTable used in the "Parameters" tab of a
   * WMT session.
   * 
   * @param parameterTable the parameterTable to set
   */
  public void setParameterTable(ParameterTable parameterTable) {
    this.parameterTable = parameterTable;
  }

  /**
   * Returns the id of the Component (a String) being dragged from the
   * "Components" tab of WMT.
   */
  public String getDraggedComponent() {
    return draggedComponent;
  }

  /**
   * Stores the id of the Component (a String) being dragged from the
   * "Components" tab of WMT.
   * 
   * @param draggedComponent the id of the dragged component, a String
   */
  public void setDraggedComponent(String draggedComponent) {
    this.draggedComponent = draggedComponent;
  }

  /**
   * Returns the id of the Component (a String) that is currently selected in
   * the ModelTree.
   */
  public String getSelectedComponent() {
    return selectedComponent;
  }

  /**
   * Stores the id of the Component that is currently selected in the ModelTree.
   * 
   * @param selectedComponent the id of the Component to set, a String
   */
  public void setSelectedComponent(String selectedComponent) {
    this.selectedComponent = selectedComponent;
  }
}
