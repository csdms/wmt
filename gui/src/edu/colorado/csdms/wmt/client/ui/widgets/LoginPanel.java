/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.user.client.ui.Button;
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

  private static final String SIGN_IN = "Sign In";
  private static final String SIGN_OUT = "Sign Out";
  
  private TextBox emailBox;
  private PasswordTextBox passwordBox;
  private HTML loginName;
  private Button signInButton;
  private HorizontalPanel inputPanel;
  private HorizontalPanel statusPanel;
  
  /**
   * Makes a new, empty, {@link LoginPanel}.
   */
  public LoginPanel() {

    // TextBoxes for entering email and password.
    emailBox = new TextBox();
    emailBox.setStyleName("wmt-LoginBox");
    emailBox.getElement().setAttribute("placeholder", "Email");
    passwordBox = new PasswordTextBox();
    passwordBox.setStyleName("wmt-LoginBox");
    passwordBox.getElement().setAttribute("placeholder", "Password");

    // The inputPanel, shown initially, holds the email and password boxes.
    inputPanel = new HorizontalPanel();
    inputPanel.add(emailBox);
    inputPanel.add(passwordBox);
    
    // A widget to show the user's email address when logged in.
    loginName = new HTML();
    loginName.setStyleName("wmt-SignInButton");
    
    // The status panel, initially hidden, shows the user's loginName.
    statusPanel = new HorizontalPanel();
    statusPanel.add(loginName);

    signInButton = new Button(SIGN_IN);
    signInButton.setStyleName("wmt-SignInButton");
    
    HorizontalPanel contents = new HorizontalPanel();
    contents.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    contents.setStyleName("wmt-LoginPanel");
    contents.add(inputPanel);
    contents.add(statusPanel);
    contents.add(signInButton);

    showInputPanel();
    initWidget(contents);
  }

  /**
   * Prefaces the sign in (the default setting) action: clears and shows the
   * {@link LoginPanel}'s email and password boxes; displays "Sign In" on the
   * button.
   */
  public void showInputPanel() {
    emailBox.setText(null);
    passwordBox.setText(null);
    inputPanel.setVisible(true);
    statusPanel.setVisible(false);
    signInButton.setText(SIGN_IN);
  }
  
  /**
   * Prefaces the sign out action: shows the user's login name; displays
   * "Sign Out" on the button.
   */
  public void showStatusPanel() {
    inputPanel.setVisible(false);
    statusPanel.setVisible(true);
    signInButton.setText(SIGN_OUT);
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

  public HTML getLoginName() {
    return loginName;
  }

  public void setLoginName(HTML loginName) {
    this.loginName = loginName;
  }

  public Button getSignInButton() {
    return signInButton;
  }

  public void setSignInButton(Button signInButton) {
    this.signInButton = signInButton;
  }
}
