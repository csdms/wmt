/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.widgets.DroplistDialogBox;

/**
 * Handles click on the "Delete" button in the ActionButtonPanel. It presents an
 * instance of {@link DroplistDialogBox} with a "Delete" button. Events are sent
 * to {@link DeleteModelHandler} and {@link DialogCancelHandler}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ActionButtonPanelDeleteHandler implements ClickHandler {

  private DataManager data;
  private DroplistDialogBox deleteDialog;

  /**
   * Creates a new instance of {@link ActionButtonPanelDeleteHandler}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public ActionButtonPanelDeleteHandler(DataManager data) {
    this.data = data;
  }
  
  @Override
  public void onClick(ClickEvent event) {
    
    // Hide the MoreActionsMenu.
    data.getPerspective().getActionButtonPanel().getMoreMenu().hide();
    
    deleteDialog = new DroplistDialogBox();
    deleteDialog.setText("Delete Model...");
    deleteDialog.getChoicePanel().getOkButton().setHTML(
        DataManager.FA_DELETE + "Delete");

    // Populate the ModelDroplist with the available models on the server.
    for (int i = 0; i < data.modelNameList.size(); i++) {
      deleteDialog.getDroplistPanel().getDroplist().addItem(
          data.modelNameList.get(i));
    }

    deleteDialog.getChoicePanel().getOkButton().addClickHandler(
        new DeleteModelHandler(data, deleteDialog));
    deleteDialog.getChoicePanel().getCancelButton().addClickHandler(
        new DialogCancelHandler(deleteDialog));

    deleteDialog.center();
  }
}
