package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.ModelMenu;
import edu.colorado.csdms.wmt.client.ui.SaveDialogBox;

/**
 * Handles click on the "OK" button in the save dialog that appears when the
 * "Save Model As..." button is clicked in the {@link ModelMenu}. Uses
 * {@link DataManager#serialize()} to serialize the model, then posts it to
 * the server with {@link DataTransfer#postModel(DataManager)}.
 */
public class SaveModelHandler implements ClickHandler {
  
  private DataManager data;
  private SaveDialogBox box;
  
  /**
   * Creates a new {@link SaveModelHandler}.
   * 
   * @param data the DataManager object for the WMT session
   * @param box the dialog box
   */
  public SaveModelHandler(DataManager data, SaveDialogBox box) {
    this.data = data;
    this.box = box;
  }
  
  @Override
  public void onClick(ClickEvent event) {

    box.hide();

    // Set the model name in the DataManager.
    String modelName = box.getFilePanel().getField();
    if (modelName.isEmpty()) {
      return;
    }
    if (!data.getModel().getName().matches(modelName)) {
      data.getModel().setName(modelName);
      data.saveAttempts++;
    }

    // Serialize the model from the GUI and post it to the server.
    data.serialize();
    DataTransfer.postModel(data);
  }
}
