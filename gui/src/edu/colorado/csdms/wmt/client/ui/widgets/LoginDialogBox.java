/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A {@link DialogBox} with {@link FieldPanel}s for entering a username and
 * password.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LoginDialogBox extends DialogBox {

  private FieldPanel usernamePanel;
  private FieldPanel passwordPanel;
  private ChoicePanel choicePanel;
  
  /**
   * Makes a new {@link LoginDialogBox} with username and password
   * {@link FieldPanel}s.
   */
  public LoginDialogBox() {

    super(false); // autohide
    this.setModal(true);
    this.setStyleName("wmt-DialogBox");
    this.setText("Login to WMT");

    usernamePanel = new FieldPanel();
    passwordPanel = new FieldPanel(true); // uses PasswordTextBox
    choicePanel = new ChoicePanel();

    usernamePanel.getLabel().setText("Email address:");
    passwordPanel.getLabel().setText("Password:");
    choicePanel.getOkButton().setHTML("<i class='fa fa-sign-in'></i> Login");

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.add(usernamePanel);
    contents.add(passwordPanel);
    contents.add(choicePanel);

    this.setWidget(contents);
  }

  public FieldPanel getUsernamePanel() {
    return usernamePanel;
  }

  public void setUsernamePanel(FieldPanel usernamePanel) {
    this.usernamePanel = usernamePanel;
  }

  public FieldPanel getPasswordPanel() {
    return passwordPanel;
  }

  public void setPasswordPanel(FieldPanel passwordPanel) {
    this.passwordPanel = passwordPanel;
  }

  public ChoicePanel getChoicePanel() {
    return choicePanel;
  }

  public void setChoicePanel(ChoicePanel choicePanel) {
    this.choicePanel = choicePanel;
  }
}
