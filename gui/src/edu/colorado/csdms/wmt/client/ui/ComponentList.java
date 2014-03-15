package edu.colorado.csdms.wmt.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.user.client.ui.Grid;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.data.ComponentJSO;
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
public class ComponentList extends Grid implements DragStartHandler {

  public DataManager data; // experiment with public data
  private DragCell[] cells;
  private ComponentInfoDialogBox infoDialogBox;

  /**
   * Makes a ComponentList, used in the "Components" tab of a WMT session.
   * 
   * @param data the DataManager object for the WMT session
   */
  public ComponentList(DataManager data) {

    super(data.getComponents().size(), 1);
    this.data = data;
    this.cells = new DragCell[data.getComponents().size()];
    this.setInfoDialogBox(new ComponentInfoDialogBox());
    
    for (int i = 0; i < data.getComponents().size(); i++) {
      cells[i] = new DragCell(data.getComponent(i));
      this.setWidget(i, 0, cells[i]);
    }

    this.data.setComponentList(this);
    addDomHandler(this, DragStartEvent.getType());
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

  public ComponentInfoDialogBox getInfoDialogBox() {
    return infoDialogBox;
  }

  public void setInfoDialogBox(ComponentInfoDialogBox infoDialogBox) {
    this.infoDialogBox = infoDialogBox;
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
    List<ModelCell> openCells = data.getModelTree().findOpenModelCells(); 

    // A helpful local variable.
    List<String> openPorts = new ArrayList<String>();
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

      ComponentJSO componentJso = data.getComponent(i);
      Integer nPortsProvided = componentJso.getPortsProvided().length();

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
