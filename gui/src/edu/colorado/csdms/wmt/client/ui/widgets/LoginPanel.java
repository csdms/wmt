/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A GWT composite widget that provides boxes for an email address and an
 * obscured password, as well as a "Sign In" button.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LoginPanel extends Composite {

  private TextBox emailBox;
  private PasswordTextBox passwordBox;
  private HTML signInButton;
  
  /**
   * Makes a new, empty, {@link LoginPanel}.
   */
  public LoginPanel() {

    emailBox = new TextBox();
    emailBox.setStyleName("wmt-LoginBox");
    emailBox.getElement().setAttribute("placeholder", "Email");

    passwordBox = new PasswordTextBox();
    passwordBox.setStyleName("wmt-LoginBox");
    passwordBox.getElement().setAttribute("placeholder", "Password");

    signInButton = new HTML("Sign In");
    signInButton.setStyleName("wmt-SignInButton");
    
    HorizontalPanel contents = new HorizontalPanel();
    contents.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    contents.setStyleName("wmt-LoginPanel");
    contents.add(emailBox);
    contents.add(passwordBox);
    contents.add(signInButton);
    
    initWidget(contents);
  }

  public TextBox getEmailBox() {
    return emailBox;
  }

  public void setEmailBox(TextBox emailBox) {
    this.emailBox = emailBox;
  }

  public PasswordTextBox getPasswordBox() {
    return passwordBox;
  }

  public void setPasswordBox(PasswordTextBox passwordBox) {
    this.passwordBox = passwordBox;
  }

  public HTML getSignInButton() {
    return signInButton;
  }

  public void setSignInButton(HTML signInButton) {
    this.signInButton = signInButton;
  }
}
