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
    this.setWidget(0, 1, new ComponentCell(data, "component"));

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
   * Makes an "intersect" style grid cell connector.
   */
  private VerticalPanel makeConnectorCellIntersect() {
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
   * Adds new ComponentCells for the uses ports of the current component.
   * 
   * @param componentId the id of the current component
   */
  public void addUsesPorts(String componentId) {

    Integer nPorts = data.getComponent(componentId).getUsesPorts().length();
    GWT.log(data.getComponent(componentId).getName() + " nPorts = " + nPorts);

    if (nPorts == 0) {
      return;
    }
    
    resize(getRowCount() + nPorts, getColumnCount() + 1);
    
    setWidget(1, 2, new ComponentCell(data, data.getComponent(componentId)
        .getUsesPorts().get(0).getId()));
    setWidget(1, 1, makeConnectorCellIntersect());
    setWidget(2, 2, new ComponentCell(data, data.getComponent(componentId)
        .getUsesPorts().get(1).getId()));
    setWidget(2, 1, makeConnectorCellCorner());
  }
}
