/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.widgets.SaveDialogBox;

/**
 * Handles click on the "Save" or "Save As..." buttons in the ActionButtonPanel.
 * Saves a not-previously-saved model or a new model displayed in WMT to the
 * server with a call to {@link DataTransfer#postModel(DataManager)}.
 */
public class ModelMenuPanelSaveHandler implements ClickHandler {

  private DataManager data;
  private Boolean isSaveAs;
  private SaveDialogBox saveDialog;

  public ModelMenuPanelSaveHandler(DataManager data) {
    this(data, false);
  }

  public ModelMenuPanelSaveHandler(DataManager data, Boolean isSaveAs) {
    this.data = data;
    this.isSaveAs = isSaveAs;
  }

  @Override
  public void onClick(ClickEvent event) {
    if (isSaveAs) {
      showSaveDialogBox();
    } else {
      if (!data.modelIsSaved()) {
        if (data.getMetadata().getId() == -1) {
          showSaveDialogBox();
        } else {
          data.serialize();
          DataTransfer.postModel(data);
        }
      }
    }
  }

  /**
   * Pops up an instance of {@link SaveDialogBox} to prompt the user to save the
   * model. Events are sent to {@link SaveModelHandler} and
   * {@link DialogCancelHandler}.
   */
  private void showSaveDialogBox() {
    saveDialog = new SaveDialogBox(data.getModel().getName());
    saveDialog.getFilePanel().setTitle(
        "Enter a name for the model. No file extension is needed.");
    saveDialog.getChoicePanel().getOkButton().addClickHandler(
        new SaveModelHandler(data, saveDialog));
    saveDialog.getChoicePanel().getCancelButton().addClickHandler(
        new DialogCancelHandler(saveDialog));
    saveDialog.center();
  }
}
