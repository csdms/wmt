/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.widgets.LoginDialogBox;

/**
 * Handles login and logout events in the WMT client.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class AuthenticationHandler implements ClickHandler {

  private DataManager data;
  private LoginDialogBox loginDialog;
  
  /**
   * Creates a new {@link AuthenticationHandler}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public AuthenticationHandler(DataManager data) {
    this.data = data;
  }
  
  @Override
  public void onClick(ClickEvent event) {
    if (data.security.isLoggedIn()) {
      Boolean isConfirmed = Window.confirm("Are you sure you want to log out?");
      if (isConfirmed) {
        DataTransfer.logout(data);
      }
    } else {
      loginDialog = new LoginDialogBox();
      loginDialog.getChoicePanel().getOkButton().addClickHandler(
          new LoginHandler(data, loginDialog));
      loginDialog.getChoicePanel().getCancelButton().addClickHandler(
          new DialogCancelHandler(loginDialog));
      loginDialog.center();
    }
  }

}
