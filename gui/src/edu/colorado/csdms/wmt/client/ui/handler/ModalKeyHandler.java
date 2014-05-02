/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

/**
 * Calls a {@link ClickHandler} when the <code>Enter</code> or <code>Esc</code>
 * key is pressed.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModalKeyHandler implements KeyDownHandler {

  private ClickHandler okHandler;
  private ClickHandler cancelHandler;

  /**
   * Creates a new {@link ModalKeyHandler}.
   * 
   * @param okHandler the handler mapped to the "OK" button
   * @param cancelHandler the handler mapped to the "Cancel" button
   */
  public ModalKeyHandler(ClickHandler okHandler, ClickHandler cancelHandler) {
    this.okHandler = okHandler;
    this.cancelHandler = cancelHandler;
  }

  @Override
  public void onKeyDown(KeyDownEvent event) {
    Integer keyCode = event.getNativeKeyCode();
    if (keyCode == KeyCodes.KEY_ESCAPE) {
      cancelHandler.onClick(null);
    } else if (keyCode == KeyCodes.KEY_ENTER) {
      okHandler.onClick(null);
    }
  }
}
