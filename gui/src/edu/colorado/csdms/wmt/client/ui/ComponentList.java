package edu.colorado.csdms.wmt.client.ui;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.TreeItem;

import edu.colorado.csdms.wmt.client.data.Component;
import edu.colorado.csdms.wmt.client.data.ComponentJSO;
import edu.colorado.csdms.wmt.client.data.Port;
import edu.colorado.csdms.wmt.client.data.PortJSO;

/**
 * The ComponentList holds a list of Components, each encapsulated in a
 * DragCell. Though it's not draggable, it defines a DragStartHandler to store
 * the id of the dragged Component in the DataManager object. It also defines
 * a ClickHandler to store the id of the selected Component.
 * 
 * @uses {@link DragCell}, {@link DataManager}
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentList extends Grid implements DragStartHandler,
    ClickHandler {

  private DataManager data;
  private DragCell[] cells;

  /**
   * Makes a ComponentList, used in the "Components" tab of a WMT session.
   * 
   * @param data the DataManager object for the WMT session
   */
  public ComponentList(DataManager data) {

    super(data.getComponents().size(), 1);
    this.data = data;
    this.cells = new DragCell[data.getComponents().size()];

    for (int i = 0; i < data.getComponents().size(); i++) {
      cells[i] =
          new DragCell(data.getComponents().get(data.componentIdList.get(i)));
      this.setWidget(i, 0, cells[i]);
    }

    this.data.setComponentList(this);
    addDomHandler(this, DragStartEvent.getType());
    addDomHandler(this, ClickEvent.getType());
  }

  /**
   * Called on DragStart event. Gets the id of the component that's being
   * dragged and stores it in the DataManager.
   */
  @Override
  public void onDragStart(DragStartEvent event) {
    GWT.log("Dragging component: " + event.getData("text"));
    data.setDraggedComponent(event.getData("text"));
  }

  /**
   * Determines which Component in the ComponentList has been clicked. If the
   * Shift key is held down, attempt to match the selected Component with an
   * open "uses" port in the ModelTree.
   */
  @Override
  public void onClick(ClickEvent event) {

    HTMLTable.Cell tableCell =
        ((HTMLTable) event.getSource()).getCellForEvent(event);
    Component component =
        new Component(data.getComponents().get(
            data.componentIdList.get(tableCell.getRowIndex())));
    GWT.log("Selected in ComponentList: " + component.getName());

    if (event.isShiftKeyDown()) {

      // Find what "uses" ports in the ModelTree are currently open. If there
      // are none, short-circuit the method.
      Vector<ModelCell> openCells = data.getModelTree().findOpenModelCells();
      if (openCells.size() == 0) {
        return;
      }

      // In the special case of the driver being the only open "port", add
      // the selected component and short-circuit the method.
      if (openCells.get(0).getPortCell().getPort().getId().matches("driver")) {
        TreeItem target = openCells.get(0).getParentTreeItem();
        data.getModelTree().addComponent(component, target);
        target.setState(true);
        return;
      }

      // Since this is a convenience, take the first provides port of the
      // selected component.
      Port port = component.getProvidesPorts()[0];
      GWT.log("Port provided: " + port.getId());

      // Try to match a uses port with a provides port of the component.
      for (int i = 0; i < openCells.size(); i++) {
        ModelCell cell = openCells.get(i);
        GWT.log("Open port: " + cell.getPortCell().getPort().getId());
        if (port.getId().matches(cell.getPortCell().getPort().getId())) {
          GWT.log("Port match!");
          TreeItem target = cell.getParentTreeItem();
          data.getModelTree().addComponent(component, target);
          target.setState(true);
          break;
        }
      }
    }
  }

  /**
   * Returns the components listed in the ComponentList as an array of
   * DragCells.
   */
  public DragCell[] getCells() {
    return cells;
  }

  /**
   * Returns a single component of the ComponentList given its index.
   * 
   * @param index The zero-based index into the array of DragCells.
   */
  public DragCell getCell(Integer index) {
    return this.cells[index];
  }

  /**
   * Applies styles to the DragCells contained in the ComponentList, based on
   * whether the components provide ports for those exposed in the ModelTree.
   */
  public void setCellSensitivity() {

    // Find what (uses) ports in the ModelTree are currently open.
    Vector<ModelCell> openCells = data.getModelTree().findOpenModelCells();
    // GWT.log(openPorts.toString());

    // A helpful local variable.
    Vector<String> openPorts = new Vector<String>();
    for (int i = 0; i < openCells.size(); i++) {
      openPorts.add(openCells.get(i).getPortCell().getPort().getId());
    }

    // In the special case of the driver being the only open "port", sensitize
    // all the components and short-circuit the method.
    if (openPorts.contains("driver")) {
      for (int i = 0; i < cells.length; i++) {
        cells[i].setSensitive(true);
      }
      return;
    }

    // Find what components in the ComponentList *do not* provide for the
    // open ports. Set the dependent style on the grabby handle of these
    // components to "notallowed", and disable draggability.
    for (int i = 0; i < cells.length; i++) {

      Boolean portMatch = false;

      ComponentJSO componentJso = data.getComponents().get(cells[i].getId());
      Integer nPortsProvided = componentJso.getPortsProvided().length();
      // GWT.log(componentJso.getName() + ": " + nPortsProvided.toString());

      Integer portIndex = 0;
      while ((portIndex < nPortsProvided) && !portMatch) {
        PortJSO pp = componentJso.getPortsProvided().get(portIndex);
        portMatch = openPorts.contains(pp.getId());
        portIndex++;
      }

      cells[i].setSensitive(portMatch);
    }
  }
}
