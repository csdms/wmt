/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.widgets.LoginDialogBox;

/**
 * Saves login information in the {@link DataManager}; calls
 * {@link DataTransfer#login()} to process a login to WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LoginAuthenticationHandler implements ClickHandler {

  private DataManager data;
  private LoginDialogBox box;
  
  /**
   * Creates a new {@link LoginAuthenticationHandler}.
   * 
   * @param data the DataManager object for the WMT session
   * @param box the dialog box
   */
  public LoginAuthenticationHandler(DataManager data, LoginDialogBox box) {
    this.data = data;
    this.box = box;
  }
  
  @Override
  public void onClick(ClickEvent event) {

    box.hide();
    
    // Get WMT username.
    String userName = box.getUsernamePanel().getField();
    data.security.setWmtUsername(userName);
    GWT.log(data.security.getWmtUsername());

    // Get WMT password.
    String password = box.getPasswordPanel().getField();
    data.security.setWmtPassword(password);
    GWT.log(data.security.getWmtPassword());

    // Authenticate the user.
    DataTransfer.login(data);    
  }
}
