/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

/**
 * Handles click on "Help" button in the ActionButtonPanel.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ActionButtonPanelHelpHandler implements ClickHandler {

  @Override
  public void onClick(ClickEvent event) {
    Window.alert("Help");
  }
}
