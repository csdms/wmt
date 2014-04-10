/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.widgets.LoginDialogBox;
import edu.colorado.csdms.wmt.client.ui.widgets.QuestionDialogBox;

/**
 * Handles login and logout events in the WMT client.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class AuthenticationHandler implements ClickHandler {

  private DataManager data;
  
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

    // XXX Should use cookie? Or get from server?
    if (data.security.isLoggedIn()) {

      String question = "Are you sure you want to log out from WMT?";
      QuestionDialogBox questionDialog = new QuestionDialogBox(question);
      questionDialog.getChoicePanel().getOkButton().setHTML(
          "<i class='fa fa-sign-out'></i> Logout");
      questionDialog.getChoicePanel().getOkButton().addClickHandler(
          new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
              DataTransfer.logout(data);
            }
          });
      questionDialog.getChoicePanel().getCancelButton().addClickHandler(
          new DialogCancelHandler(questionDialog));
      questionDialog.center();

    } else {

      LoginDialogBox loginDialog = new LoginDialogBox();
      loginDialog.getChoicePanel().getOkButton().addClickHandler(
          new LoginHandler(data, loginDialog));
      loginDialog.getChoicePanel().getCancelButton().addClickHandler(
          new DialogCancelHandler(loginDialog));
      loginDialog.center();

    }
  }

}
