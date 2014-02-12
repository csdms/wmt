/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import edu.colorado.csdms.wmt.client.data.ComponentDescriptions;
import edu.colorado.csdms.wmt.client.data.ComponentParameters;
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

  private String draggedComponent;
  private String selectedComponent;

  private ComponentDescriptions componentDescriptions;
  private LinkedHashMap<String, ComponentParameters> componentParameters =
      new LinkedHashMap<String, ComponentParameters>();
  private ModelJSO model;
  private String modelString; // stringified JSON

  // Experiment with public members.
  public List<String> componentIdList;
  public List<Integer> modelIdList;
  public List<String> modelNameList;
  public LinkedHashMap<String, String> files;

  /**
   * Initializes the DataManager object used in a WMT session.
   */
  public DataManager() {
    componentIdList = new ArrayList<String>();
    modelIdList = new ArrayList<Integer>();
    modelNameList = new ArrayList<String>();
    files = new LinkedHashMap<String, String>();
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
   * Returns the data on all the components available to WMT. This is a
   * ComponentDescriptions object, which holds a JsArray of ComponentJSO objects
   * read from "components.json".
   */
  public ComponentDescriptions getComponents() {
    return componentDescriptions;
  }

  /**
   * Stores the data on all the components available to WMT. This is a
   * ComponentDescriptions object, which holds a JsArray of ComponentJSO objects
   * read from "components.json".
   * <p>
   * Initializes the ComponentList object in viewWest. Need to think about
   * this more; not sure it's a good idea. Used in CMTJson#get.
   * 
   * @param componentDescriptions a ComponentDescriptions object
   */
  public void setComponents(ComponentDescriptions componentDescriptions) {
    this.componentDescriptions = componentDescriptions;
    perspective.initializeComponentList(); // TODO Think about this!
  }

  /**
   * Returns the set of parameters for a component, given by its id. This is a
   * ComponentParameters object, which holds a JsArray of ParameterJSO objects
   * read from files "<parameter_name>.json".
   * 
   * @param componentId a component id, a String
   */
  public ComponentParameters getParameters(String componentId) {
    return componentParameters.get(componentId);
  }

  /**
   * Stores the set of parameters, given by a ComponentParameters object, which
   * holds a JsArray of ParameterJSO objects, for a given component.
   * 
   * @param componentId the id of the component, a String
   * @param componentParameters a ComponentParameters object for the parameter
   */
  public void setParameters(String componentId,
      ComponentParameters componentParameters) {
    this.componentParameters.put(componentId, componentParameters);
  }

  /**
   * TODO
   * @return the model
   */
  public ModelJSO getModel() {
    return model;
  }

  /**
   * TODO
   * @param model the model to set
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
