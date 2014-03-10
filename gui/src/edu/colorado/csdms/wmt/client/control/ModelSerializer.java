package edu.colorado.csdms.wmt.client.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.TreeItem;

import edu.colorado.csdms.wmt.client.data.Component;
import edu.colorado.csdms.wmt.client.data.ComponentJSO;
import edu.colorado.csdms.wmt.client.data.ModelComponentConnectionsJSO;
import edu.colorado.csdms.wmt.client.data.ModelComponentJSO;
import edu.colorado.csdms.wmt.client.data.ModelComponentParametersJSO;
import edu.colorado.csdms.wmt.client.data.ModelJSO;
import edu.colorado.csdms.wmt.client.ui.ModelCell;
import edu.colorado.csdms.wmt.client.ui.ModelTree;

/**
 * Serializes the model built in a WMT session.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelSerializer {

  private DataManager data;
  private ModelTree modelTree;
  private TreeItem treeItem;
  private Integer nModelComponents;

  /**
   * Instatiates a ModelSerializer and stores a reference to the
   * {@link DataManager}.
   * 
   * @param data the DataManager for the WMT session.
   */
  public ModelSerializer(DataManager data) {
    this.data = data;
    this.modelTree = data.getModelTree();
    nModelComponents = data.getModel().getComponents().length();
  }

  /**
   * Translates the model displayed in WMT into a {@link ModelJSO} object,
   * which completely describes the state of the model, and stores it in the
   * {@link DataManager}.
   */
  public void serialize() {

    // Create a JsArray of ModelComponentJSO objects for the components that
    // make up the model.
    @SuppressWarnings("unchecked")
    JsArray<ModelComponentJSO> componentsArray =
        (JsArray<ModelComponentJSO>) ModelComponentJSO.createArray();

    // Iterate through the leaves of the ModelTree. For each leaf, create a
    // ModelComponentJSO object to hold the component, its ports and its
    // parameters. When loaded with information from the GUI, push the
    // ModelComponentJSO into the components JsArray and move to the next
    // leaf.
    Iterator<TreeItem> iter = modelTree.treeItemIterator();
    while (iter.hasNext()) {

      treeItem = (TreeItem) iter.next();
      ModelCell cell = (ModelCell) treeItem.getWidget();

      // Skip linked components and empty components.
      if (cell.getComponentCell().isLinked()) {
        continue;
      }
      if (cell.getComponentCell().getComponent().getId() == null) {
        continue;
      }

      // Serialize this model component.
      Component component = cell.getComponentCell().getComponent();
      ComponentJSO componentJso = data.getModelComponent(component.getId());
      ModelComponentJSO modelComponent = serializeComponent(componentJso);
      if (cell.getPortCell().getPort().getId().matches("driver")) {
        modelComponent.setDriver();
      }

      // Push the component into the components JsArray.
      componentsArray.push(modelComponent);
    }

    // Set the component JsArray into the model.
    data.getModel().setComponents(componentsArray);
  }

  /**
   * Serializes a single model component.
   * 
   * @param componentJso a {@link ComponentJSO} representing the model
   *          component
   * @return a {@link ModelComponentJSO} representing the model component
   */
  public ModelComponentJSO serializeComponent(ComponentJSO componentJso) {

    // Make a new ModelComponentJSO object to hold the component.
    ModelComponentJSO modelComponent =
        (ModelComponentJSO) ModelComponentJSO.createObject();

    modelComponent.setId(componentJso.getId());
    modelComponent.setClassName(componentJso.getId()); // XXX Check this.
    modelComponent.setParameters(serializeParameters(componentJso));
    modelComponent.setConnections(serializeConnections(componentJso));

    return modelComponent;
  }

  /**
   * Serializes the parameters of a model component.
   * 
   * @param componentJso a {@link ComponentJSO} representing the model
   *          component
   * @return a {@link ModelComponentParametersJSO} representing the parameters
   */
  public ModelComponentParametersJSO serializeParameters(
      ComponentJSO componentJso) {

    // Make a new ModelComponentParametersJSO object to hold the parameters.
    ModelComponentParametersJSO modelComponentParameters =
        (ModelComponentParametersJSO) ModelComponentParametersJSO
            .createObject();

    // Add the parameters and their values as key-value pairs.
    Integer nParameters = componentJso.getParameters().length();
    if (nParameters > 0) {
      for (int i = 0; i < nParameters; i++) {
        String key = componentJso.getParameters().get(i).getKey();
        if (key.matches("separator")) {
          continue;
        }
        String value =
            componentJso.getParameters().get(i).getValue().getDefault();
        modelComponentParameters.addParameter(key, value);
      }
    }
    return modelComponentParameters;
  }

  /**
   * Serializes the ports of the model component.
   * 
   * @param componentJso a {@link ComponentJSO} representing the model
   *          component
   * @return a {@link ModelComponentConnectionsJSO} representing the
   *         connections
   */
  public ModelComponentConnectionsJSO serializeConnections(
      ComponentJSO componentJso) {

    // Make a new ModelComponentConnectionsJSO object to hold the connections.
    ModelComponentConnectionsJSO modelComponentConnections =
        (ModelComponentConnectionsJSO) ModelComponentConnectionsJSO
            .createObject();

    // Add the ports for the model component.
    for (int i = 0; i < treeItem.getChildCount(); i++) {
      TreeItem child = treeItem.getChild(i);
      ModelCell childCell = (ModelCell) child.getWidget();
      String portId = childCell.getPortCell().getPort().getId();
      String componentId = childCell.getComponentCell().getComponent().getId();
      modelComponentConnections.addConnection(portId, componentId);
    }
    return modelComponentConnections;
  }

  /**
   * Extracts the information contained in the {@link ModelJSO} object
   * returned from opening a model (model menu > "Open Model...") and uses it
   * to populate the ModelTree.
   */
  public void deserialize() {

    // Load the model components into an ArrayList.
    List<ModelComponentJSO> modelComponents =
        new ArrayList<ModelComponentJSO>();
    for (int i = 0; i < nModelComponents; i++) {
      modelComponents.add(data.getModel().getComponents().get(i));
    }

    // Locate the driver and pop it off of the modelComponents list.
    ModelComponentJSO driver = getDriver(modelComponents);
    if (driver == null) {
      return; // XXX Throw error message when driver not found?
    }
    modelComponents.remove(driver);
    GWT.log("Model driver = " + driver.getClassName());

    // Set up the root of the ModelTree with the driver.
    Component driverComponent =
        new Component(data.getModelComponent(driver.getId()));
    TreeItem root = modelTree.getItem(0);
    modelTree.setComponent(driverComponent, root);
    root.setState(true);

    // Find matches for the driver's open ports supplied by the model. Pop
    // each match off of the modelComponents list.
    List<ModelCell> openCells = modelTree.findOpenModelCells();
    if (driver.nConnections() > 0) {

      ModelComponentConnectionsJSO connect = driver.getConnections();

      for (int i = 0; i < driver.nConnections(); i++) {

        // Get the "portId @ componentId" of the connection.
        String portId = connect.getPortNames().get(i);
        String componentId = connect.getConnection(portId);
        GWT.log(portId + "@" + componentId);
        if (componentId == null) {
          continue;
        }
        
        // Find the open ModelCell that matches the connected port.
        for (int j = 0; j < openCells.size(); j++) {
          if (openCells.get(j).getPortCell().getPort().getId().matches(portId)) {
            Component component =
                new Component(data.getModelComponent(componentId));
            TreeItem leaf = openCells.get(j).getParentTreeItem();
            modelTree.setComponent(component, leaf);
            leaf.setState(true);
            // TODO Pop the matching modelComponent
//            nModelComponentsUsed++;
          }
        }

      }
    }

  }

  /**
   * A worker that locates and returns the driver from a list of
   * {@link ModelComponentJSO} objects extracted from a {@link ModelJSO}
   * object.
   * 
   * @param modelComponents a list of {@link ModelComponentJSO} objects
   * @return the driver of the model
   */
  private ModelComponentJSO getDriver(List<ModelComponentJSO> modelComponents) {
    Iterator<ModelComponentJSO> iter = modelComponents.iterator();
    while (iter.hasNext()) {
      ModelComponentJSO modelComponent = (ModelComponentJSO) iter.next();
      if (modelComponent.isDriver()) {
        return modelComponent;
      }
    }
    return null;
  }
}
