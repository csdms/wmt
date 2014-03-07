/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.control;

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
import edu.colorado.csdms.wmt.client.data.ModelJSO;
import edu.colorado.csdms.wmt.client.data.ModelMetadataJSO;
import edu.colorado.csdms.wmt.client.ui.ComponentList;
import edu.colorado.csdms.wmt.client.ui.ModelCell;
import edu.colorado.csdms.wmt.client.ui.ModelTree;
import edu.colorado.csdms.wmt.client.ui.ParameterTable;
import edu.colorado.csdms.wmt.client.ui.Perspective;

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

  private List<ComponentJSO> components; // "class" components
  private List<ComponentJSO> modelComponents; // "instance" components
  private String draggedComponent;
  private String selectedComponent;
  
  private ModelJSO model;
  private ModelMetadataJSO metadata;
  private Boolean modelIsSaved = false;
  private String modelString; // stringified JSON
  
  private String simulationId; // the uuid of a submitted run
  private String hostname;
  private String username;
  private String password;
  
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
   * A convenience method that returns the prefix (a String) to be displayed
   * before the name of the tab title in the WMT interface. Currently a Font
   * Awesome icon.
   * 
   * @param tabName the name of the tab: "model", "parameter" or "component"
   */
  public String tabPrefix(String tabName) {
    String prefix = "";
    if (tabName.matches("model")) {
      prefix = "<i class='fa fa-globe'></i> ";
    } else if (tabName.matches("parameter")) {
      prefix = "<i class='fa fa-wrench'></i> ";
    } else if (tabName.matches("component")) {
      prefix = "<i class='fa fa-cogs'></i> ";
    }
    return prefix;
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
   * A convenience method that adds a component to the ArrayList of model
   * components.
   * <p>
   * Compare with {@link #addComponent(ComponentJSO)} for "class" components.
   * 
   * @param modelComponent the model component to add, a {@link ComponentJSO}
   */
  public void addModelComponent(ComponentJSO modelComponent) {
    this.modelComponents.add(modelComponent);
  }

  /**
   * Replaces the item in DataManager's ArrayList of model components with a
   * copy of the input component. A regex on the id of the model component is
   * used to identify the component to replace.
   * 
   * @param component the replacement component, a {@link ComponentJSO}
   */
  public void replaceModelComponent(ComponentJSO component) {
    for (int i = 0; i < modelComponents.size(); i++) {
      if (modelComponents.get(i).getId().matches(component.getId())) {
        ComponentJSO copy = DataTransfer.copy(component);
        modelComponents.set(i, copy);
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
   * Returns the ModelMetadataJSO object used to store the metadata for a
   * model.
   */
  public ModelMetadataJSO getMetadata() {
    return metadata;
  }

  /**
   * Stores the ModelMetadataJSO object that holds a model's metadata.
   * 
   * @param metadata the model's ModelMetadataJSO object
   */
  public void setMetadata(ModelMetadataJSO metadata) {
    this.metadata = metadata;
  }

  /**
   * Returns the save state of the model. (True = saved)
   */
  public Boolean modelIsSaved() {
    return modelIsSaved;
  }

  /**
   * Stores the save state of the model. (True = saved)
   * 
   * @param modelIsSaved the model save state, a Boolean
   */
  public void modelIsSaved(Boolean modelIsSaved) {
    this.modelIsSaved = modelIsSaved;
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
   * Returns the uuid of a run sumbmitted to a server.
   */
  public String getSimulationId() {
    return simulationId;
  }

  /**
   * Stores the uuid of a run submitted to a server.
   * 
   * @param simulationId the uuid, a String
   */
  public void setSimulationId(String simulationId) {
    this.simulationId = simulationId;
  }

  /**
   * Returns the hostname of the machine where the user wants the model to be
   * run.
   */
  public String getHostname() {
    return hostname;
  }

  /**
   * Stores the hostname of the machine where the user wants the model to be
   * run.
   * 
   * @param hostname
   */
  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  /**
   * Returns the user's username for the host on which the model is to be run.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Stores the user's username for the host on which the model is to be run.
   * 
   * @param username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Returns the user's password for the host on which the model is to be run.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Stores the user's password for the host on which the model is to be run.
   * 
   * @param password
   */
  public void setPassword(String password) {
    this.password = password;
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
    
    ModelSerializer serializer = new ModelSerializer(this);
    serializer.serialize();
    
    // Stringify the model.
    modelString = DataTransfer.stringify(model);
  }

  /**
   * Extracts the information contained in the {@link ModelJSO} object
   * returned from opening a model (model menu > "Open Model...") and uses it
   * to populate the {@link ModelTree}.
   */
  public void deserialize() {

    perspective.setModelPanelTitle();
    
    /*
     * What I hope this looks like when finished.
     */
    ModelSerializer serializer = new ModelSerializer(this);
    serializer.deserialize();
    

//    Integer nModelComponents = model.getComponents().length();
//    Integer nModelComponentsUsed = 0;
//
//    // Locate the driver.
//    Integer driverIndex = 0;
//    for (int i = 0; i < nModelComponents; i++) {
//      if (model.getComponents().get(i).isDriver()) {
//        driverIndex = i;
//        break;
//      }
//    }
//
//    // Set up the root of the ModelTree with the driver.
//    ModelJSO driver = model.getComponents().get(driverIndex);
//    Component driverComponent =
//        new Component(getModelComponent(driver.getId()));
//    TreeItem root = modelTree.getItem(0);
//    modelTree.setComponent(driverComponent, root);
//    root.setState(true);
//    nModelComponentsUsed++;
//
//    // Find matches for the driver's open ports supplied by the model.
//    List<ModelCell> openCells = modelTree.findOpenModelCells();
//    if (driver.nPorts() > 0) {
//      for (int i = 0; i < driver.nPorts(); i++) {
//        String portId = driver.getPorts().get(i);
//        String componentId = driver.getConnection(portId);
//        GWT.log(portId + ":" + componentId);
//        if (componentId == null) {
//          continue;
//        }
//        for (int j = 0; j < openCells.size(); j++) {
//          if (openCells.get(j).getPortCell().getPort().getId().matches(portId)) {
//            Component component = new Component(getModelComponent(componentId));
//            TreeItem leaf = openCells.get(j).getParentTreeItem();
//            modelTree.setComponent(component, leaf);
//            leaf.setState(true);
//            nModelComponentsUsed++;
//          }
//        }
//      }
//    }
//
//    // Loop to fill in open ports in ModelTree with the remaining components
//    // in the model.
//    Integer index = 0;
//    while (nModelComponentsUsed < nModelComponents) {
//      ModelJSO mc = model.getComponents().get(index);
//      index++;
//      List<ModelCell> cells = modelTree.findOpenModelCells();
//
//      if (mc.nPorts() > 0) {
//        for (int i = 0; i < mc.nPorts(); i++) {
//          String portId = mc.getPorts().get(i);
//          String componentId = mc.getConnection(portId);
//          GWT.log(portId + ":" + componentId);
//          if (componentId == null) {
//            continue;
//          }
//          for (int j = 0; j < cells.size(); j++) {
//            if (cells.get(j).getPortCell().getPort().getId().matches(portId)) {
//              Component component =
//                  new Component(getModelComponent(componentId));
//              TreeItem leaf = cells.get(j).getParentTreeItem();
//              modelTree.setComponent(component, leaf);
//              leaf.setState(true);
//              nModelComponentsUsed++;
//            }
//          }
//        }
//      }
//    }
//
//    // Loop to load the parameters for the components in the model.
//    for (int i = 0; i < nModelComponents; i++) {
//      ModelJSO mc = model.getComponents().get(i);
//      ComponentJSO mcJSO = getModelComponent(mc.getId());
//      if (mc.nParameters() > 0) {
//        for (int j = 0; j < mc.nParameters(); j++) {
//          String key = mc.getParameters().get(j);
//          String value = mc.getValue(key);
//          mcJSO.getParameter(key).getValue().setDefault(value);
//        }
//      }
//    }
  }
}
