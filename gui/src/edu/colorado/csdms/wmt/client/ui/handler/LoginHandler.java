/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.widgets.LoginDialogBox;

/**
 * Handles login events.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LoginHandler implements ClickHandler {

  private DataManager data;
  private Boolean isLogout = false;
  private LoginDialogBox loginDialog;
  
  /**
   * Creates a new {@link LoginHandler}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public LoginHandler(DataManager data) {
    this.data = data;
  }
  
  @Override
  public void onClick(ClickEvent event) {
    HTML loginHtml = (HTML) event.getSource();
    if (isLogout) {
      loginHtml.setHTML("<a href=\"javascript:;\">Login</a>");
    } else {
      loginDialog = new LoginDialogBox();
      loginDialog.getChoicePanel().getOkButton().addClickHandler(
          new LoginAuthenticationHandler(data, loginDialog));
      loginDialog.getChoicePanel().getCancelButton().addClickHandler(
          new DialogCancelHandler(loginDialog));
      loginDialog.center();
    }
    isLogout = !isLogout;
  }

}
