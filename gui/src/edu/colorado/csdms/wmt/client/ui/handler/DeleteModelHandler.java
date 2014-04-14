package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.widgets.DroplistDialogBox;

/**
 * Handles click on the "Delete" button in the dialog that appears when the
 * "Delete Model..." button is clicked in the ActionButtonPanel. Deletes the
 * selected model from the server with a call to
 * {@link DataTransfer#deleteModel(DataManager, Integer)}.
 */
public class DeleteModelHandler implements ClickHandler {
  
  private DataManager data;
  private DroplistDialogBox box;
  
  /**
   * Creates a new {@link DeleteModelHandler}.
   * 
   * @param data the DataManager object for the WMT session
   * @param box the dialog box
   */
  public DeleteModelHandler(DataManager data, DroplistDialogBox box) {
    this.data = data;
    this.box = box;
  }
  
  @Override
  public void onClick(ClickEvent event) {

    box.hide();

    Integer selIndex =
        box.getDroplistPanel().getDroplist().getSelectedIndex();
    Integer modelId = data.modelIdList.get(selIndex);
    GWT.log("Deleting model: " + modelId);

    DataTransfer.deleteModel(data, modelId);

    // If the deleted model is currently displayed in the model tree, close it.
    if (data.getMetadata().getId() == modelId) {
      data.getPerspective().reset();
    }
  }
}
