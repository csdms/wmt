/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.widgets.OpenDialogBox;

/**
 * Handles click on the "Open Model..." button in the ModelActionPanel. Pops up an
 * instance of {@link OpenDialogBox} to prompt the user for a model to open.
 * Events are sent to {@link OpenModelHandler} and {@link DialogCancelHandler}.
 */
public class ModelActionPanelOpenHandler implements ClickHandler {

  private DataManager data;
  private OpenDialogBox openDialog;

  /**
   * Creates a new instance of {@link ModelActionPanelOpenHandler}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public ModelActionPanelOpenHandler(DataManager data) {
    this.data = data;
  }

  @Override
  public void onClick(ClickEvent event) {

    openDialog = new OpenDialogBox(data);

    // Populate the droplist with the available models on the server.
    for (int i = 0; i < data.modelNameList.size(); i++) {
      openDialog.getDroplistPanel().getDroplist().addItem(
          data.modelNameList.get(i));
    }

    openDialog.getChoicePanel().getOkButton().addClickHandler(
        new OpenModelHandler(data, openDialog));
    openDialog.getChoicePanel().getCancelButton().addClickHandler(
        new DialogCancelHandler(openDialog));

    openDialog.center();

    // Give the droplist focus.
    openDialog.getDroplistPanel().getDroplist().setFocus(true);    
  }
}
