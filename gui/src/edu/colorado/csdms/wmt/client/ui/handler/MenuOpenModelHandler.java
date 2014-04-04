/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.widgets.DroplistDialogBox;

/**
 * Handles click on the "Open Model..." button in the ModelMenuPanel. Pops up an
 * instance of {@link DroplistDialogBox} to prompt the user for a model to open.
 * Events are sent to {@link OpenModelHandler} and {@link GenericCancelHandler}.
 */
public class MenuOpenModelHandler implements ClickHandler {

  private DataManager data;
  private DroplistDialogBox openDialog;

  /**
   * Creates a new instance of {@link MenuOpenModelHandler}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public MenuOpenModelHandler(DataManager data) {
    this.data = data;
  }

  @Override
  public void onClick(ClickEvent event) {

    openDialog = new DroplistDialogBox();
    openDialog.setText("Open Model...");
    openDialog.getChoicePanel().getOkButton().setHTML(
        DataManager.FA_OPEN + "Open");

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
  }
}
