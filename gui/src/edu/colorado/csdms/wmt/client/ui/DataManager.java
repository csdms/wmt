/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.TreeItem;

import edu.colorado.csdms.wmt.client.data.Component;
import edu.colorado.csdms.wmt.client.data.ComponentJSO;
import edu.colorado.csdms.wmt.client.data.DataTransfer;
import edu.colorado.csdms.wmt.client.data.ModelJSO;

/**
 * A class for storing and sharing data, as well as the state of UI elements,
 * within WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DataManager {

  private Boolean developmentMode;
  
  private Perspective perspective;
  private ComponentList componentList;
  private ModelTree modelTree;
  private ParameterTable parameterTable;

  private List<ComponentJSO> components;      // "class" components
  private List<ComponentJSO> modelComponents; // "instance" components
  private String draggedComponent;
  private String selectedComponent;
  private ModelJSO model;
  private String modelString; // stringified JSON

  // Experiment with public members, for convenience.
  public List<String> componentIdList;
  public List<Integer> modelIdList;
  public List<String> modelNameList;
  public Integer saveAttempts = 0;

  /**
   * Initializes the DataManager object used in a WMT session.
   */
  public DataManager() {
    componentIdList = new ArrayList<String>();
    components = new ArrayList<ComponentJSO>();
    modelComponents = new ArrayList<ComponentJSO>();
    modelIdList = new ArrayList<Integer>();
    modelNameList = new ArrayList<String>();
  }

  /**
   * Returns true if GWT is running in development mode; false for production
   * mode.
   */
  public Boolean isDevelopmentMode() {
    return developmentMode;
  }

  /**
   * Stores the GWT mode: true if in development, false if in production.
   * 
   * @param developmentMode a Boolean, set to true for development mode
   */
  public void isDevelopmentMode(Boolean developmentMode) {
    this.developmentMode = developmentMode;
  }

  /**
   * Returns the {@link Perspective} object used to organize the WMT views.
   */
  public Perspective getPerspective() {
    return perspective;
  }

  /**
   * Sets the {@link Perspective} object used to organize the WMT views.
   * 
   * @param perspective the perspective to set
   */
  public void setPerspective(Perspective perspective) {
    this.perspective = perspective;
  }

  /**
   * A convenience method that returns the {@link ComponentJSO} object
   * matching the given component id.
   * 
   * @param componentId the id of the desired component, a String
   */
  public ComponentJSO getComponent(String componentId) {
    Iterator<ComponentJSO> iter = components.iterator();
    while (iter.hasNext()) {
      ComponentJSO component = (ComponentJSO) iter.next();
      if (component.getId().matches(componentId)) {
        return component;
      }
    }
    return null;
  }  
  
  /**
   * A convenience method that returns the {@link ComponentJSO} object at the
   * given position in the ArrayList of components.
   * 
   * @param index an offset into the ArrayList of components
   */
  public ComponentJSO getComponent(Integer index) {
    return components.get(index);
  }
  
  /**
   * A convenience method that adds a component to the ArrayList of
   * components.
   * <p>
   * Once all the components have been pulled from the server, sort them
   * alphabetically and initialize the {@link ComponentList}.
   * 
   * @param component the component to add, a ComponentJSO object
   */
  public void addComponent(ComponentJSO component) {
    this.components.add(component);
    if (this.components.size() == this.componentIdList.size()) {
      sortComponents();
      perspective.initializeComponentList();
    } // XXX This is fragile.
  }

  /**
   * Performs an in-place sort of the ArrayList of components using a
   * {@link Comparator}.
   */
  public void sortComponents() {
    Collections.sort(components, new Comparator<ComponentJSO>() {
      @Override
      public int compare(ComponentJSO o1, ComponentJSO o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
  }  
  
  /**
   * Returns the <em>all</em> the components in the ArrayList of
   * {@link ComponentJSO} objects.
   */
  public List<ComponentJSO> getComponents() {
    return this.components;
  }

  /**
   * Sets an ArrayList of ComponentJSOs representing <em>all</em> the
   * components.
   * 
   * @param components all your components are belong to us
   */
  public void setComponents(List<ComponentJSO> components) {
    this.components = components;
  }

  /**
   * A convenience method that returns the {@link ComponentJSO} object
   * matching the given model component id, or null if no match is found.
   * <p>
   * Compare with {@link #getComponent(String)} for "class" components.
   * 
   * @param modelComponentId the id of the desired model component, a String
   */
  public ComponentJSO getModelComponent(String modelComponentId) {
    if (modelComponentId != null) {
      Iterator<ComponentJSO> iter = modelComponents.iterator();
      while (iter.hasNext()) {
        ComponentJSO component = (ComponentJSO) iter.next();
        if (component.getId().matches(modelComponentId)) {
          return component;
        }
      }
    }
    return null;
  }  
  
  /**
   * A convenience method that returns the {@link ComponentJSO} object at the
   * given position in the ArrayList of model components.
   * <p>
   * Compare with {@link #getComponent(Integer)} for "class" components.
   * 
   * @param index an offset into the ArrayList of model components
   */
  public ComponentJSO getModelComponent(Integer index) {
    return modelComponents.get(index);
  }  

  /**
   * A convenience method that adds a component to the ArrayList of
   * model components.
   * <p>
   * Compare with {@link #addComponent(ComponentJSO)} for "class" components.
   * 
   * @param modelComponent the model component to add, a {@link ComponentJSO}
   */
  public void addModelComponent(ComponentJSO modelComponent) {
    this.modelComponents.add(modelComponent);
  }  

  /**
   * Replaces the item in DataManager's ArrayList of model components with the
   * input component. A regex on the id of the model component is used to
   * identify the component to replace.
   * 
   * @param component the replacement component, a {@link ComponentJSO}
   */
  public void replaceModelComponent(ComponentJSO component) {
    for (int i = 0; i < modelComponents.size(); i++) {
      if (modelComponents.get(i).getId().matches(component.getId())) {
        modelComponents.set(i, component);
        return;
      }
    }
  }  
  
  /**
   * Replaces <em>all</em> of the model components with copies of the (class)
   * components using {@link DataTransfer#copy()}.
   */
  public void resetModelComponents() {
    for (int i = 0; i < modelComponents.size(); i++) {
      ComponentJSO copy = DataTransfer.copy(components.get(i));
      modelComponents.set(i, copy);
    }
  }
  
  /**
   * Returns the <em>all</em> the model components in the ArrayList of
   * {@link ComponentJSO} objects.
   * <p>
   * Compare with {@link #getComponents()} for "class" components.
   */
  public List<ComponentJSO> getModelComponents() {
    return this.modelComponents;
  }
  
  /**
   * Sets an ArrayList of ComponentJSOs representing <em>all</em> the model
   * components.
   * 
   * @param components all your components are belong to us
   */
  public void setModelComponents(List<ComponentJSO> modelComponents) {
    this.modelComponents = modelComponents;
  }  
  
  /**
   * Returns the model displayed in the {@link ModelTree}, a {@link ModelJSO}
   * object.
   */
  public ModelJSO getModel() {
    return model;
  }

  /**
   * Sets the model displayed in the {@link ModelTree}.
   * 
   * @param model the model to set, a ModelJSO object
   */
  public void setModel(ModelJSO model) {
    this.model = model;
  }

  /**
   * Gets the stringified model JSON created by {@link #serialize()}.
   */
  public String getModelString() {
    return modelString;
  }

  /**
   * Stores the stringified model JSON created by {@link #serialize()}.
   * 
   * @param modelString the modelString to set
   */
  public void setModelString(String modelString) {
    this.modelString = modelString;
  }

  /**
   * Returns a reference to the {@link ComponentList} used in the "Components"
   * tab of a WMT session.
   */
  public ComponentList getComponentList() {
    return componentList;
  }

  /**
   * Stores a reference to the {@link ComponentList} used in the "Components"
   * tab of a WMT session.
   * 
   * @param componentList the ComponentList instance
   */
  public void setComponentList(ComponentList componentList) {
    this.componentList = componentList;
  }

  /**
   * Returns a reference to the {@link ModelTree} used in a WMT session.
   */
  public ModelTree getModelTree() {
    return modelTree;
  }

  /**
   * Stores a reference to the {@link ModelTree} used in a WMT session.
   * 
   * @param modelTree the ModelTree instance
   */
  public void setModelTree(ModelTree modelTree) {
    this.modelTree = modelTree;
  }

  /**
   * Returns a reference to the {@link ParameterTable} used in the
   * "Parameters" tab of a WMT session.
   */
  public ParameterTable getParameterTable() {
    return parameterTable;
  }

  /**
   * Stores a reference to the {@link ParameterTable} used in the "Parameters"
   * tab of a WMT session.
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
   * the {@link ModelTree}.
   */
  public String getSelectedComponent() {
    return selectedComponent;
  }

  /**
   * Stores the id of the Component that is currently selected in the
   * {@link ModelTree}.
   * 
   * @param selectedComponent the id of the Component to set, a String
   */
  public void setSelectedComponent(String selectedComponent) {
    this.selectedComponent = selectedComponent;
  }
  
  /**
   * Translates the model displayed in WMT into a {@link ModelJSO} object,
   * which completely describes the state of the model. This object is
   * converted to a string (with {@link DataTransfer#stringify()}) which can
   * be uploaded to a server.
   */
  public void serialize() {

    // Create a JsArray of ModelJSO objects for the components that make up
    // the model.
    @SuppressWarnings("unchecked")
    JsArray<ModelJSO> componentsArray =
        (JsArray<ModelJSO>) ModelJSO.createArray();

    // Iterate through the leaves of the ModelTree. For each leaf, create a
    // ModelJSO object to hold the component, its ports and its parameters.
    // When loaded with information from the GUI, push the ModelJSO into the
    // components JsArray and move to the next leaf.
    Iterator<TreeItem> iter = modelTree.treeItemIterator();
    while (iter.hasNext()) {

      TreeItem treeItem = (TreeItem) iter.next();
      ModelCell cell = (ModelCell) treeItem.getWidget();

      // Skip linked components and empty components.
      if (cell.getComponentCell().isLinked()) {
        continue;
      }
      if (cell.getComponentCell().getComponent().getId() == null) {
        continue;
      }

      ModelJSO modelComponent = (ModelJSO) ModelJSO.createObject();

      // Awkward. Still need Component, though, I think.
      Component component = cell.getComponentCell().getComponent();
      ComponentJSO componentJSO = getComponent(component.getId());

      modelComponent.setId(componentJSO.getId());
      modelComponent.setClassName(componentJSO.getId()); // XXX Check this.
      if (cell.getPortCell().getPort().getId().matches("driver")) {
        modelComponent.setDriver();
      }

      // Load the component's parameters into the ModelJSO. All that's needed
      // for a ModelJSO are the key-value pairs. (Note: Can't pass arrays into
      // JSNI methods.) Include zero parameter check because Java is dumb.
      Integer nParameters = componentJSO.getParameters().length();
      if (nParameters > 0) {
        for (int i = 0; i < nParameters; i++) {
          String key = componentJSO.getParameters().get(i).getKey();
          if (key.matches("separator")) {
            continue;
          }
          String value =
              componentJSO.getParameters().get(i).getValue().getDefault();
          modelComponent.setParameter(key, value);
        }
      }

      // Load the connected ports.
      for (int i = 0; i < treeItem.getChildCount(); i++) {
        TreeItem child = treeItem.getChild(i);
        ModelCell childCell = (ModelCell) child.getWidget();
        String portId = childCell.getPortCell().getPort().getId();
        String componentId =
            childCell.getComponentCell().getComponent().getId();
        modelComponent.setConnection(portId, componentId);
      }

      // Push the component into the components JsArray.
      componentsArray.push(modelComponent);
    }

    // Set the component JsArray into the model.
    model.setComponents(componentsArray);

    // Stringify the ModelJSO object. Store the result in the DataManager.
    modelString = DataTransfer.stringify(model);
  }  
  
  /**
   * Extracts the information contained in the {@link ModelJSO} object
   * returned from opening a model (model menu > "Open Model...") and uses it
   * to populate the {@link ModelTree}.
   */
  public void deserialize() {

    // TODO Refactor to improve clarity. And performance.
    
    // Set model name on tab.
    perspective.setModelPanelTitle(true);

    Integer nModelComponents = model.getComponents().length();
    Integer nModelComponentsUsed = 0;

    // Locate the driver.
    Integer driverIndex = 0;
    for (int i = 0; i < nModelComponents; i++) {
      if (model.getComponents().get(i).isDriver()) {
        driverIndex = i;
        break;
      }
    }

    // Set up the root of the ModelTree with the driver.
    ModelJSO driver = model.getComponents().get(driverIndex);
    Component driverComponent =
        new Component(getModelComponent(driver.getId()));
    TreeItem root = modelTree.getItem(0);
    modelTree.setComponent(driverComponent, root);
    root.setState(true);
    nModelComponentsUsed++;

    // Find matches for the driver's open ports supplied by the model.
    List<ModelCell> openCells = modelTree.findOpenModelCells();
    for (int i = 0; i < driver.getPorts().length(); i++) {
      String portId = driver.getPorts().get(i);
      String componentId = driver.getConnection(portId);
      GWT.log(portId + ":" + componentId);
      if (componentId == null) {
        continue;
      }
      for (int j = 0; j < openCells.size(); j++) {
        if (openCells.get(j).getPortCell().getPort().getId().matches(portId)) {
          Component component = new Component(getModelComponent(componentId));
          TreeItem leaf = openCells.get(j).getParentTreeItem();
          modelTree.setComponent(component, leaf);
          leaf.setState(true);
          nModelComponentsUsed++;
        }
      }
    }

    // Loop to fill in open ports in ModelTree with the remaining components
    // in the model.
    Integer index = 0;
    while (nModelComponentsUsed < nModelComponents) {
      ModelJSO mc = model.getComponents().get(index);
      index++;
      List<ModelCell> cells = modelTree.findOpenModelCells();
      for (int i = 0; i < mc.getPorts().length(); i++) {
        String portId = mc.getPorts().get(i);
        String componentId = mc.getConnection(portId);
        GWT.log(portId + ":" + componentId);
        if (componentId == null) {
          continue;
        }
        for (int j = 0; j < cells.size(); j++) {
          if (cells.get(j).getPortCell().getPort().getId().matches(portId)) {
            Component component = new Component(getModelComponent(componentId));
            TreeItem leaf = cells.get(j).getParentTreeItem();
            modelTree.setComponent(component, leaf);
            leaf.setState(true);
            nModelComponentsUsed++;
          }
        }
      }
    }

    // Loop to load the parameters for the components in the model.
    for (int i = 0; i < nModelComponents; i++) {
      ModelJSO mc = model.getComponents().get(i);
      ComponentJSO mcJSO = getModelComponent(mc.getId());
      for (int j = 0; j < mc.getParameters().length(); j++) {
        String key = mc.getParameters().get(j);
        String value = mc.getValue(key);
        mcJSO.getParameter(key).getValue().setDefault(value);
      }
    }
  }
}
