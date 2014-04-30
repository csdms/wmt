/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.github.gwtbootstrap.client.ui.Tooltip;
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
  private Tooltip tooltip;
  private OpenDialogBox openDialog;

  /**
   * Creates a new instance of {@link ModelActionPanelOpenHandler}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public ModelActionPanelOpenHandler(DataManager data, Tooltip tooltip) {
    this.data = data;
    this.tooltip = tooltip;
  }

  @Override
  public void onClick(ClickEvent event) {

    // Necessary to work around a bug in the tooltip.
    tooltip.hide();
    
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
  }
}
