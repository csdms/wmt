/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.widgets.OpenDialogBox;

/**
 * Handles click on the "Open Model..." button in the ModelActionPanel. Pops up
 * an instance of {@link OpenDialogBox} to prompt the user for a model to open.
 * Events are sent to {@link OpenModelHandler} (on clicking "OK" button or
 * hitting <code>Enter</code> key) and {@link DialogCancelHandler} (on clicking
 * "Cancel" or hitting <code>Esc</code> key).
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

    // Define open and cancel handlers.
    final OpenModelHandler openHandler = new OpenModelHandler(data, openDialog);
    final DialogCancelHandler cancelHandler =
        new DialogCancelHandler(openDialog);

    // Set the open and cancel handlers on the "OK" and "Cancel" buttons.
    openDialog.getChoicePanel().getOkButton().addClickHandler(openHandler);
    openDialog.getChoicePanel().getCancelButton()
        .addClickHandler(cancelHandler);

    // Also use the handlers for the "Enter" and "Esc" keys.
    // XXX There's likely a more stylish way to do this.
    openDialog.addDomHandler(new KeyDownHandler() {
      @Override
      public void onKeyDown(KeyDownEvent event) {
        Integer keyCode = event.getNativeKeyCode();
        if (keyCode == KeyCodes.KEY_ESCAPE) {
          cancelHandler.onClick(null);
        } else if (keyCode == KeyCodes.KEY_ENTER) {
          openHandler.onClick(null);
        }
      }
    }, KeyDownEvent.getType());

    openDialog.center();

    // Give the droplist focus.
    openDialog.getDroplistPanel().getDroplist().setFocus(true);
  }
}
