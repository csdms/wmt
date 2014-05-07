/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.widgets.NewUserDialogBox;

/**
 * Handles adding a new user login to WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class AddNewUserHandler implements ClickHandler {

  private DataManager data;
  private NewUserDialogBox box;
  
  /**
   * Creates a new {@link AddNewUserHandler}.
   * 
   * @param data the DataManager object for the WMT session
   * @param box a {@link NewUserDialogBox}
   */
  public AddNewUserHandler(DataManager data, NewUserDialogBox box) {
    this.data = data;
    this.box = box;
  }
  
  @Override
  public void onClick(ClickEvent event) {
    String initialPassword = data.security.getWmtPassword();
    String reenteredPassword = box.getPasswordPanel().getField().getText();
    if (reenteredPassword.equals(initialPassword)) {
      DataTransfer.newUserLogin(data);
      box.hide();
    } else {
      Window.alert("Passwords do not match.");
      box.getPasswordPanel().getField().setText("");
    }
  }
}
