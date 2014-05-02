/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;

import edu.colorado.csdms.wmt.client.Constants;
import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.widgets.LoginPanel;
import edu.colorado.csdms.wmt.client.ui.widgets.QuestionDialogBox;

/**
 * Handles login and logout events in the WMT client.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class AuthenticationHandler implements ClickHandler {

  private DataManager data;
  private LoginPanel panel;

  /**
   * Creates a new {@link AuthenticationHandler}.
   * 
   * @param data the DataManager object for the WMT session
   * @param panel the {@link LoginPanel} object for the WMT session
   */
  public AuthenticationHandler(DataManager data, LoginPanel panel) {
    this.data = data;
    this.panel = panel;
  }

  @Override
  public void onClick(ClickEvent event) {

    if (data.security.isLoggedIn()) {

      final QuestionDialogBox questionDialog =
          new QuestionDialogBox(Constants.QUESTION_SIGN_OUT);
      questionDialog.getChoicePanel().getOkButton().setHTML(Constants.SIGN_OUT);

      // Define handlers.
      ClickHandler okHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          DataTransfer.logout(data);
          questionDialog.hide();
        }
      };
      DialogCancelHandler cancelHandler =
          new DialogCancelHandler(questionDialog);

      // Apply handlers to OK and Cancel buttons.
      questionDialog.getChoicePanel().getOkButton().addClickHandler(okHandler);
      questionDialog.getChoicePanel().getCancelButton().addClickHandler(
          cancelHandler);

      // Also apply handlers to "Enter" and "Esc" keys.
      questionDialog
          .addDomHandler(new ModalKeyHandler(okHandler, cancelHandler),
              KeyDownEvent.getType());

      questionDialog.center();
      questionDialog.getChoicePanel().getOkButton().setFocus(true);

    } else {

      // Get WMT username.
      String userName = panel.getEmailBox().getText();
      data.security.setWmtUsername(userName);
      GWT.log(data.security.getWmtUsername());

      // Get WMT password.
      String password = panel.getPasswordBox().getText();
      data.security.setWmtPassword(password);
      GWT.log(data.security.getWmtPassword());

      // Authenticate the user.
      DataTransfer.login(data);
    }
  }
}
