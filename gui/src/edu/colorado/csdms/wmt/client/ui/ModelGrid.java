/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.data.ModelJSO;
import edu.colorado.csdms.wmt.client.data.ModelMetadataJSO;

/**
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelGrid extends Grid {

  private DataManager data;
  private HTML driverCell;
  private Boolean driverConnected;
  
  /**
   * 
   * @param data
   */
  public ModelGrid(DataManager data) {
    this.data = data;
    initializeGrid();
//    this.data.getPerspective().setModelGrid(this);
  }

  /**
   * A worker that sets up the root (the "driver") of the ModelGrid.
   * It also initializes the {@link ModelJSO} and {@link ModelMetadataJSO}
   * objects used to save the model created with this ModelGrid.
   */
  public void initializeGrid() {

    this.clear();
    this.resize(1, 2);

    driverCell = new HTML("<i class='fa fa-play fa-lg'></i>");
    driverCell.setStyleName("mwmb-driverCell");
    this.setWidget(0, 0, driverCell);
    this.setWidget(0, 1, new ComponentCell(data, "component", 0, 1));

    ModelJSO model = (ModelJSO) ModelJSO.createObject();
    data.setModel(model);
    ModelMetadataJSO metadata =
        (ModelMetadataJSO) ModelMetadataJSO.createObject();
    data.setMetadata(metadata);
  }

  public Boolean isDriverConnected() {
    return driverConnected;
  }

  public void isDriverConnected(Boolean driverConnected) {
    this.driverConnected = driverConnected;
    driverCell.setStyleDependentName("connected", this.driverConnected);
  }

  /**
   * Makes a "corner" style grid cell connector.
   */
  private VerticalPanel makeConnectorCellCorner() {
    VerticalPanel panel = new VerticalPanel();
    SimplePanel panel1 = new SimplePanel();
    panel1.setStyleName("mwmb-connectorCell");
    panel1.addStyleDependentName("corner");
    SimplePanel panel2 = new SimplePanel();
    panel2.setStyleName("mwmb-connectorCell");
    panel2.addStyleDependentName("empty");
    panel.add(panel1);
    panel.add(panel2);
    return panel;
  }

  /**
   * Makes an "interior" style grid cell connector.
   */
  private VerticalPanel makeConnectorCellInterior() {
    VerticalPanel panel = new VerticalPanel();
    SimplePanel panel1 = new SimplePanel();
    panel1.setStyleName("mwmb-connectorCell");
    panel1.addStyleDependentName("corner");
    SimplePanel panel2 = new SimplePanel();
    panel2.setStyleName("mwmb-connectorCell");
    panel2.addStyleDependentName("straight");
    panel.add(panel1);
    panel.add(panel2);
    return panel;
  }

  /**
   * Adds a "corner" style connector + ComponentCell to the ModelGrid for a uses
   * port of a component.
   * <p>
   * This style is used if the component has only one uses port, or if this is
   * the last of a set of uses ports for the component.
   * 
   * @param componentId the id of the parent component
   * @param portIndex the index of the uses port, as stored in the component
   * @param irow the row index of the parent component in the ModelGrid
   * @param icol the column index of the parent component in the ModelGrid
   */
  private void addCornerCell(String componentId, Integer portIndex,
      Integer irow, Integer icol) {
    Integer cRow = irow;
    Integer cCol = icol + 1;
    GWT.log("Add "
        + data.getComponent(componentId).getUsesPorts().get(portIndex).getId()
        + " at (" + cRow + "," + cCol + ")");
    setWidget(irow, icol, makeConnectorCellCorner());
    setWidget(irow, icol + 1, new ComponentCell(data, data.getComponent(
        componentId).getUsesPorts().get(portIndex).getId(), cRow, cCol));
  }
  
  /**
   * Adds a "interior" style connector + ComponentCell to the ModelGrid for a
   * uses port of a component.
   * <p>
   * This style is used when a component has multiple uses ports, and this uses
   * port is not the last in the set.
   * 
   * @param componentId the id of the parent component
   * @param portIndex the index of the uses port, as stored in the component
   * @param irow the row index of the parent component in the ModelGrid
   * @param icol the column index of the parent component in the ModelGrid
   */
  private void addInteriorCell(String componentId, Integer portIndex,
      Integer irow, Integer icol) {
    Integer cRow = irow;
    Integer cCol = icol + 1;
    GWT.log("Add "
        + data.getComponent(componentId).getUsesPorts().get(portIndex).getId()
        + " at (" + cRow + "," + cCol + ")");
    setWidget(irow, icol, makeConnectorCellInterior());
    setWidget(irow, icol + 1, new ComponentCell(data, data.getComponent(
        componentId).getUsesPorts().get(portIndex).getId(), cRow, cCol));
  }
  
  /**
   * Adds new ComponentCells to the ModelGrid for the uses ports of the current
   * component.
   * 
   * @param componentId the id of the current component
   * @param pRow the row index of the current component
   * @param pCol the column index of the current component
   */
  public void addUsesPorts(String componentId, Integer pRow, Integer pCol) {

    Integer nPorts = data.getComponent(componentId).getUsesPorts().length();

    String logMsg =
        data.getComponent(componentId).getName() + " nPorts = " + nPorts;
    if (nPorts > 0) {
      logMsg += ": ";
      for (int i = 0; i < nPorts; i++) {
        logMsg +=
            data.getComponent(componentId).getUsesPorts().get(i).getId() + " ";
      }
    }
    GWT.log(logMsg);

    if (nPorts == 0) {
      return;
    }

    resizeColumns(getColumnCount() + 1);

    if (nPorts == 1) {
      insertRow(pRow + nPorts);
      addCornerCell(componentId, nPorts - 1, nPorts + pRow, pCol);
    }

    if (nPorts >= 2) {
      for (int i = 1; i < nPorts; i++) {
        insertRow(pRow + i);
        addInteriorCell(componentId, i - 1, i + pRow, pCol);
      }
      insertRow(pRow + nPorts);
      addCornerCell(componentId, nPorts - 1, nPorts + pRow, pCol);
    }
  }
}
