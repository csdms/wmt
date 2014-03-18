package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;

/**
 * Handles a click on the "Cancel" button in a {@link DialogBox}. Cancels the
 * action and closes both the dialog.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DialogCancelHandler implements ClickHandler {

  private DialogBox box;
  
  /**
   * Creates a new {@link DialogCancelHandler} for the "Cancel" button in a 
   * dialog box.
   * 
   * @param box the reference for the dialog box
   */
  public DialogCancelHandler(DialogBox box) {
    this.box = box;
  }

  @Override
  public void onClick(ClickEvent event) {
    if ((box != null) && (box.isShowing())) {
      box.hide();
    }
  }
}
