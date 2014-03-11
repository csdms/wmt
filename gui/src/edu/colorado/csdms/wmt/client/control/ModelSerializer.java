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
  private List<ModelComponentJSO> modelComponents;

  /**
   * Instatiates a ModelSerializer and stores a reference to the
   * {@link DataManager}.
   * 
   * @param data the DataManager for the WMT session.
   */
  public ModelSerializer(DataManager data) {
    this.data = data;
    this.modelTree = this.data.getModelTree();
    nModelComponents = this.data.getModel().nComponents(); // dev vs. prod mode!
    modelComponents = new ArrayList<ModelComponentJSO>();
  }

  /**
   * Translates the model displayed in WMT into a {@link ModelJSO} object, which
   * completely describes the state of the model, and stores it in the
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
    // ModelComponentJSO into the components JsArray and move to the next leaf.
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
   * @param componentJso a {@link ComponentJSO} representing the model component
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
   * @param componentJso a {@link ComponentJSO} representing the model component
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
   * @param componentJso a {@link ComponentJSO} representing the model component
   * @return a {@link ModelComponentConnectionsJSO} representing the connections
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
   * Extracts the information contained in the {@link ModelJSO} object returned
   * from opening a model (model menu > "Open Model...") and uses it to populate
   * the {@link ModelTree}.
   */
  public void deserialize() {

    // Load the model components list. (It's a convenience.)
    for (int i = 0; i < nModelComponents; i++) {
      modelComponents.add(data.getModel().getComponents().get(i));
    }

    // Locate and deserialize the driver.
    ModelComponentJSO driver = deserializeComponent("driver", null);

    // Deserialize the components connected to the driver.
    matchConnections(driver);

    // Loop to fill in open ports in ModelTree, checking the connections of all
    // the components in the model.
    Iterator<ModelComponentJSO> iter = modelComponents.iterator();
    while (iter.hasNext()) {
      ModelComponentJSO modelComponent = (ModelComponentJSO) iter.next();
      matchConnections(modelComponent);
    }
  }

  /**
   * Deserializes a single model component.
   * 
   * @param componentId the id of the model component
   * @param cell the model cell in which to place the deserialized component
   */
  private ModelComponentJSO deserializeComponent(String componentId,
      ModelCell cell) {

    // Locate the model component.
    ModelComponentJSO modelComponent = getComponent(componentId);

    // Set a new component in the open model cell (or the root node, if driver).
    TreeItem node = null;
    if (modelComponent.isDriver()) {
      node = modelTree.getItem(0);
      GWT.log("Model driver = " + modelComponent.getClassName());
    } else {
      node = cell.getParentTreeItem();
    }
    Component component =
        new Component(data.getModelComponent(modelComponent.getId()));
    modelTree.setComponent(component, node);
    node.setState(true);

    // Load the component's parameters.
    deserializeParameters(modelComponent);

    return modelComponent;
  }

  /**
   * Deserializes the all the parameters of a single incoming model component
   * and stores them with the model in the {@link DataManager}.
   * 
   * @param modelComponent a model component with parameters
   */
  private void deserializeParameters(ModelComponentJSO modelComponent) {
    if (modelComponent.nParameters() > 0) {
      for (int j = 0; j < modelComponent.nParameters(); j++) {
        String key = modelComponent.getParameters().getKeys().get(j);
        String value = modelComponent.getParameters().getValues().get(j);
        data.getModelComponent(modelComponent.getId()).getParameter(key)
            .getValue().setDefault(value);
      }
    }
  }

  /**
   * A worker that locates and returns the requested {@link ModelComponentJSO}
   * object. Returns null if the component is not found.
   * <p>
   * If "driver" is passed as the componentId, the driver component is located
   * and returned.
   * 
   * @param componentId the id of a model component
   */
  private ModelComponentJSO getComponent(String componentId) {
    Iterator<ModelComponentJSO> iter = modelComponents.iterator();
    while (iter.hasNext()) {
      ModelComponentJSO modelComponent = (ModelComponentJSO) iter.next();
      if ((componentId.matches("driver") && modelComponent.isDriver())
          || modelComponent.getId().matches(componentId)) {
        return modelComponent;
      }
    }
    return null;
  }

  /**
   * Attempts to deserialize the listed connections of a model component.
   * 
   * @param modelComponent a {@link ModelComponentJSO} object.
   */
  private void matchConnections(ModelComponentJSO modelComponent) {

    // If the model component has no connections, that's it.
    if (modelComponent.nConnections() == 0) {
      return;
    }

    // Get a list of open model cells in the model tree. For the driver,
    // consider only its immediate children.
    List<ModelCell> openCells = new ArrayList<ModelCell>();
    if (modelComponent.isDriver()) {
      openCells = modelTree.findOpenModelCells(modelTree.getItem(0));
    } else {
      openCells = modelTree.findOpenModelCells();
    }

    // Find matches for the open model cells ith components supplied by the
    // model.
    for (int i = 0; i < modelComponent.nConnections(); i++) {

      // Get the "portId @ componentId" of the connection.
      String portId = modelComponent.getConnections().getPortNames().get(i);
      String componentId =
          modelComponent.getConnections().getConnection(portId);
      GWT.log(modelComponent.getId() + ": " + portId + "@" + componentId);
      if (componentId == null) {
        continue;
      }

      // Match the connection with an open model cell through its port.
      for (int j = 0; j < openCells.size(); j++) {
        ModelCell cell = openCells.get(j);
        if (cell.getPortCell().getPort().getId().matches(portId)) {
          deserializeComponent(componentId, cell);
        }
      }
    }
  }

}
