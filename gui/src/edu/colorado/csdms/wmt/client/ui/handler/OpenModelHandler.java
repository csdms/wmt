package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.ModelMenu;
import edu.colorado.csdms.wmt.client.ui.widgets.DroplistDialogBox;

/**
 * Handles click on the "OK" button in the open dialog that appears when the
 * "Open Model..." button is clicked in the {@link ModelMenu}. Calls
 * {@link DataTransfer#getModel(DataManager, Integer)} to pull the selected
 * model from the server.
 */
public class OpenModelHandler implements ClickHandler {
  
  private DataManager data;
  private DroplistDialogBox box;
  
  /**
   * Creates a new {@link OpenModelHandler}.
   * 
   * @param data the DataManager object for the WMT session
   * @param box the dialog box
   */
  public OpenModelHandler(DataManager data, DroplistDialogBox box) {
    this.data = data;
    this.box = box;
  }
  
  @Override
  public void onClick(ClickEvent event) {

    data.showWaitCursor();
    box.hide();

    data.getPerspective().reset();

    // Get the selected item from the openDialog. This feels fragile. I'm
    // using the index of the selected modelName to match up the index of
    // the modelId. This should work consistently because I add the modelId
    // and modelName to the ArrayList with the same index. It would be
    // better if they both resided in the same data structure.
    Integer selIndex =
        box.getDroplistPanel().getDroplist().getSelectedIndex();
    Integer modelId = data.modelIdList.get(selIndex);

    // Get the data + metadata for the selected model. On success, #getModel
    // calls DataManager#deserialize, which populates the WMT GUI.
    DataTransfer.getModel(data, modelId);
  }
}
