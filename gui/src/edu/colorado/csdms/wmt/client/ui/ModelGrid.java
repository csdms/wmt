/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.data.ModelJSO;
import edu.colorado.csdms.wmt.client.data.ModelMetadataJSO;

/**
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 *
 */
public class ModelGrid extends Grid {

  private DataManager data;
  private HTML driverCell;
  
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
    this.setWidget(0, 1, new ComponentCell("Component"));

    ModelJSO model = (ModelJSO) ModelJSO.createObject();
    data.setModel(model);
    ModelMetadataJSO metadata =
        (ModelMetadataJSO) ModelMetadataJSO.createObject();
    data.setMetadata(metadata);
  }

}
