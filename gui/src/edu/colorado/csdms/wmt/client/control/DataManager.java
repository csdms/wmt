/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import edu.colorado.csdms.wmt.client.data.ComponentJSO;
import edu.colorado.csdms.wmt.client.data.ModelJSO;
import edu.colorado.csdms.wmt.client.data.ModelMetadataJSO;
import edu.colorado.csdms.wmt.client.ui.ModelTree;
import edu.colorado.csdms.wmt.client.ui.Perspective;

/**
 * A class for storing and sharing data, as well as the state of UI elements,
 * within WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DataManager {

  public static String DRIVER = "driver";
  
  private Boolean developmentMode;

  // Get the state of UI elements through the Perspective. 
  private Perspective perspective;

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
   * @param tabName the name of the tab: "model" or "parameter"
   */
  public String tabPrefix(String tabName) {
    String prefix = "";
    if (tabName.matches("model")) {
      prefix = "<i class='fa fa-cogs'></i> ";
    } else if (tabName.matches("parameter")) {
      prefix = "<i class='fa fa-wrench'></i> ";
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
   * A convenience method that adds a component to the ArrayList of components.
   * <p>
   * Once all the components have been pulled from the server, sort them
   * alphabetically and initialize the {@link ModelTree}.
   * 
   * @param component the component to add, a ComponentJSO object
   */
  public void addComponent(ComponentJSO component) {
    this.components.add(component);
    if (this.components.size() == this.componentIdList.size()) {
      sortComponents();
      perspective.getModelTree().getDriverComponentCell().getComponentMenu()
          .updateComponents();
    }
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
    perspective.getModelTree().getDriverRunCell().setStyleDependentName("active", modelIsSaved);
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

    ModelSerializer serializer = new ModelSerializer(this);
    serializer.deserialize();
  }
}
