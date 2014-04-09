/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

import edu.colorado.csdms.wmt.client.control.DataURL;

/**
 * Handles click on the "Run status" button in the ModelMenuPanel. Displays the
 * API "run/show" page showing the status of all currently running models.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelMenuPanelStatusHandler implements ClickHandler {

  @Override
  public void onClick(ClickEvent event) {
    Window.open(DataURL.showModelRun(), "runInfoDialog", null);
  }
}
