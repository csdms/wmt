/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.data.Component;
import edu.colorado.csdms.wmt.client.data.Port;

/**
 * A 1 x 4 Grid holding HTML widgets for a port, a connector, a component, and
 * a control, each with custom styles and behaviors. Used as input for a
 * TreeItem in creating a {@link ModelTree}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelCell extends Grid {

  private PortCell portCell;
  private ConnectorCell connectorCell;
  private ComponentCell componentCell;
  private ControlCell controlCell;
  private TreeItem parentTreeItem; // Oh $DEITY is this ugly.
  private Boolean isConnected;

  /**
   * Creates an empty ModelCell.
   */
  public ModelCell() {
    this(new Port(), new Component());
  }

  /**
   * Creates a ModelCell from a {@link Port} and, optionally, a
   * {@link Component}.
   * 
   * @param port A port to associate with the ModelCell.
   * @param component A component to associate with the ModelCell. (optional)
   */
  public ModelCell(Port port, Component component) {

    super(1, 4);
    this.setStyleName("wmt-ModelCell");

    portCell = new PortCell(port);
    connectorCell = new ConnectorCell();
    componentCell = new ComponentCell(component);
    controlCell = new ControlCell();

    this.setWidget(0, 0, portCell);
    this.setWidget(0, 1, connectorCell);
    this.setWidget(0, 2, componentCell);
    this.setWidget(0, 3, controlCell);

    this.isConnected = false;
  }

  /**
   * An alternate constructor for ModelCell that uses the id of a {@link Port}
   * and the name of a {@link Component}. Be sure to set the other properties
   * of the Port and Component afterward using their mutators.
   * 
   * @param portId The id of the port to create, as a String.
   * @param componentName The name of the component, as a String.
   */
  public ModelCell(String portId, String componentName) {
    this(new Port(portId), new Component(componentName));
  }

  /**
   * Getter for the cell wrapping the Port in a ModelCell.
   */
  public PortCell getPortCell() {
    return portCell;
  }

  /**
   * Setter for the cell wrapping the Port in a ModelCell.
   * 
   * @param portCell An instance of PortCell.
   */
  public void setPortCell(PortCell portCell) {
    this.portCell = portCell;
  }

  /**
   * Getter for the cell wrapping the connector in a ModelCell.
   */
  public ConnectorCell getConnectorCell() {
    return connectorCell;
  }

  /**
   * Setter for the cell wrapping the connector in a ModelCell.
   * 
   * @param connectorCell An instance of ConnectorCell.
   */
  public void setConnectorCell(ConnectorCell connectorCell) {
    this.connectorCell = connectorCell;
  }

  /**
   * Getter for the cell wrapping the Component in a ModelCell.
   */
  public ComponentCell getComponentCell() {
    return componentCell;
  }

  /**
   * Setter for the cell wrapping the Component in a ModelCell.
   * 
   * @param componentCell An instance of ComponentCell.
   */
  public void setComponentCell(ComponentCell componentCell) {

    this.componentCell = componentCell;
    this.setWidget(0, 2, componentCell);
    controlCell.setVisible(true);

    this.componentCell.removeStyleDependentName("unconnected");
    this.componentCell.addStyleDependentName("connected");

    portCell.removeStyleDependentName("allowed");
    portCell.removeStyleDependentName("required");
    portCell.removeStyleDependentName("optional");
    portCell.addStyleDependentName("connected");
    portCell.setTitle("This port is connected.");
  }

  /**
   * Getter for the cell wrapping control items (like a delete button) in a
   * ModelCell.
   */
  public ControlCell getControlCell() {
    return controlCell;
  }

  /**
   * Setter for the cell wrapping control items (like a delete button) in a
   * ModelCell.
   * 
   * @param controlCell An instance of ControlCell.
   */
  public void setControlCell(ControlCell controlCell) {
    this.controlCell = controlCell;
  }

  /**
   * Getter for the TreeItem that's the parent of the ModelCell.
   */
  public TreeItem getParentTreeItem() {
    return parentTreeItem;
  }

  /**
   * Setter for the TreeItem that's the parent of the ModelCell.
   * 
   * @param parentTreeItem A TreeItem.
   */
  public void setParentTreeItem(TreeItem parentTreeItem) {
    this.parentTreeItem = parentTreeItem;
  }

  /**
   * Does this ModelCell have a Port and a Component? Then it's connected.
   * Helpful in determining how much of a ModelTree is filled.
   */
  public Boolean isConnected() {
    return this.isConnected;
  }

  /**
   * Set whether this ModelCell is connected; i.e., it has a Port and a
   * Component.
   * 
   * @param isConnected
   */
  public void isConnected(Boolean isConnected) {
    this.isConnected = isConnected;
  }


  /**
   * Cell 1/4 of a ModelCell, it's a display and event handling wrapper around
   * a port. An inner class.
   */
  public class PortCell extends HTML {

    private Port port;

    /**
     * Creates a PortCell from the given Port.
     * 
     * @param port An instance of the Port class; required.
     */
    public PortCell(Port port) {

      super(port.getId());
      setPort(port);

      setStyleName("wmt-PortCell");
      String tooltipText;
      if (port.isRequired()) {
        addStyleDependentName("required");
        tooltipText = "This is a required port.";
      } else {
        addStyleDependentName("optional");
        tooltipText = "This is an optional port.";
      }
      if (port.getId().matches("driver")) {
        tooltipText +=
            " Drag over a component to act as the driver of the model.";
      } else {
        tooltipText +=
            " Drag over a component that provides a \"" + port.getId()
                + "\" port to fill it.";
      }
      setTitle(tooltipText);
    }

    /**
     * Getter for the Port used in a PortCell.
     */
    public Port getPort() {
      return port;
    }

    /**
     * Setter for the Port used in a PortCell.
     * 
     * @param port An instance of Port.
     */
    public void setPort(Port port) {
      this.port = port;
    }

  } // PortCell inner class

  /**
   * Cell 2/4 of a ModelCell; the connector between the PortCell and the
   * ComponentCell. An inner class.
   */
  public class ConnectorCell extends HTML {

    /**
     * Creates a ConnectorCell. Currently = a space.
     */
    public ConnectorCell() {
      super("");
      setStyleName("wmt-ConnectorCell");
    }
  } // ConnectorCell inner class

  /**
   * Cell 3/4 of a ModelCell, it's a display and event handling wrapper around
   * a component. An inner class.
   */
  public class ComponentCell extends HTML implements ClickHandler {

    private Component component;
    private Boolean linked = false; // is this a link to another instance?

    /**
     * Makes a ComponentCell for the given Component.
     * 
     * @param component An instance of the Component class; required.
     */
    public ComponentCell(Component component) {

      super(component.getName());
      setComponent(component);

      String tooltip = "";
      if (component.getName().contains("</i>")) {
        tooltip = "Drag a component here to add it to the model.";
      } else {
        tooltip = "Model component: " + component.getName() 
            + ". Click to view its parameters.";
      }
      setTitle(tooltip);

      setStyleName("wmt-ComponentCell");
      addStyleDependentName("unconnected");

      // Associate event handlers.
      addDomHandler(this, ClickEvent.getType());
    }

    /**
     * Getter for the Component used in a ComponentCell.
     */
    public Component getComponent() {
      return component;
    }

    /**
     * Setter for the Component used in a ComponentCell.
     * 
     * @param component
     */
    public void setComponent(Component component) {
      this.component = component;
    }

    /**
     * Does this ComponentCell hold a link to another instance of a component?
     * Returns status as a Boolean.
     */
    public Boolean isLinked() {
      return linked;
    }

    /**
     * Sets whether the ComponentCell holds a link to another instance of a
     * component.
     * 
     * @param linked the "linked" setting, a Boolean
     */
    public void isLinked(Boolean linked) {
      this.linked = linked;
    }

    /**
     * On a mouse click, stores the id of the selected component in the
     * DataManager.
     */
    @Override
    public void onClick(ClickEvent event) {
      ModelTree tree = (ModelTree) ModelCell.this.getParent();
      tree.data.setSelectedComponent(getComponent().getId());
      GWT.log("Selected component: " + tree.data.getSelectedComponent());
    }

  } // ComponentCell inner class

  /**
   * Cell 4/4 of a ModelCell, it holds controls for acting on a ModelCell. An
   * inner class.
   */
  public class ControlCell extends VerticalPanel {

    private HTML deleteButton;
    private HTML helpButton;
    private HTML runButton;

    /**
     * Constructor. The ControlCell is hidden on an open port, then shown when
     * a component is attached to the port (see ModelCell#setComponentCell).
     */
    public ControlCell() {

      setVisible(false);
      setStyleName("wmt-ControlCell");
      this.setHorizontalAlignment(ALIGN_CENTER);
      this.setVerticalAlignment(ALIGN_TOP);

      deleteButton = new HTML("<i class='fa fa-times fa-fw'></i>");
      this.add(deleteButton);
      deleteButton.addStyleName("wmt-ControlCell-delete");
      deleteButton.setTitle("Remove this component");

      helpButton = new HTML("<i class='fa fa-question fa-fw'></i>");
      this.add(helpButton);
      helpButton.addStyleName("wmt-ControlCell-help");
      helpButton.setTitle("View information about this component");

      if (getPortCell().getPort().getId().matches("driver")) {
        runButton = new HTML("<i class='fa fa-play fa-fw'></i>");
      } else {
        runButton = new HTML("<p></p>");
      }
      this.add(runButton);
      runButton.addStyleName("wmt-ControlCell-run");
      runButton.setTitle("Run the model");

      /*
       * Clicking the delete button in the ControlCell removes the Component,
       * as well as anything beneath it in the ModelTree.
       * 
       * TODO This is really close to ModelTree#addComponent. Make
       * #removeComponent?
       */
      deleteButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {

          // Get the ModelTree. Also get the targeted TreeItem and delete all
          // of its children.
          ModelTree tree = (ModelTree) parentTreeItem.getTree();
          parentTreeItem.removeItems();

          // The deleted Component.
          String deleted = getComponentCell().getComponent().getId();

          // If the deleted Component is the selectedComponent, unset it.
          String selected = tree.data.getSelectedComponent();
          if (deleted.contains(selected)) {
            tree.data.setSelectedComponent(null);
          }

          // If the deleted Component, or any of its children, are currently
          // displaying their parameters in the ParameterTable, clear the
          // ParameterTable. If this isn't the driver, show the info message.
          String showing = tree.data.getPerspective().getParameterTable().getComponentId();
          if (deleted.contains(showing)) {
            tree.data.getPerspective().getParameterTable().clearTable();
            if (parentTreeItem.getParentItem() != null) {
              tree.data.getPerspective().getParameterTable().showInfoMessage();
            }
          }
          if (!tree.isComponentPresent(showing)) {
            tree.data.getPerspective().getParameterTable().clearTable();
          }

          // Make a new ModelCell (overwriting the current). Attach it to the
          // target TreeItem.
          Port openPort;
          if (parentTreeItem.getParentItem() != null) {
            openPort = new Port();
            openPort.setId(portCell.port.getId());
            openPort.isRequired(portCell.port.isRequired());
          } else {
            openPort = tree.initializeTree(); // at root of ModelTree.
          }
          Component infoComponent = Component.makeInfoComponent();
          ModelCell newCell = new ModelCell(openPort, infoComponent);
          newCell.setParentTreeItem(parentTreeItem);
          parentTreeItem.setWidget(newCell);

          // Update the title of the Model tab.
          tree.data.modelIsSaved(false);
          if (openPort.getId().matches("driver")) {
            tree.data.saveAttempts++;
          }
          tree.data.getPerspective().setModelPanelTitle();
        }
      });

      /*
       * Clicking the help button displays a dialog box with information about
       * the component, including a link to its web page on the CSDMS website.
       */
      helpButton.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(final ClickEvent event) {

//          ModelTree tree = (ModelTree) parentTreeItem.getTree();
//          String componentId = componentCell.getComponent().getId();
//          ComponentJSO componentJso = tree.data.getComponent(componentId);
//
//          final ComponentInfoDialogBox componentInfoDialogBox =
//              tree.data.getComponentList().getInfoDialogBox();
//          componentInfoDialogBox.update(componentJso);
//          componentInfoDialogBox
//              .setPopupPositionAndShow(new PositionCallback() {
//                Integer x = event.getClientX();
//                Integer y = event.getClientY();
//
//                @Override
//                public void setPosition(int offsetWidth, int offsetHeight) {
//                  Integer nudge = 5; // px
//                  componentInfoDialogBox.setPopupPosition(x + nudge, y + nudge);
//                }
//              });
        }
      });
    }

  } // ControlCell inner class

} // ModelCell class
