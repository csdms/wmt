/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 mcflugen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SuggestBox;

import edu.colorado.csdms.wmt.client.Constants;

/**
 * A GWT composite widget that provides boxes for an email address and an
 * obscured password, as well as a "Sign In" button.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LoginPanel extends Composite {
  
  private MultiWordSuggestOracle oracle;
  private SuggestBox emailBox;
  private PasswordTextBox passwordBox;
  private HTML loginName;
  private Button signInButton;
  private HorizontalPanel inputPanel;
  private HorizontalPanel statusPanel;
  
  /**
   * Makes a new {@link LoginPanel} displaying empty email and password boxes.
   */
  public LoginPanel() {

    // Use a Cookie and a SuggestBox to help autocomplete the user's login.
    // TODO Replace with the browser's login autocomplete mechanism.
    oracle = new MultiWordSuggestOracle();

    // TextBoxes for entering email and password.
    emailBox = new SuggestBox(oracle);
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

    signInButton = new Button(Constants.SIGN_IN);
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
    String storedWmtUsername = Cookies.getCookie(Constants.USERNAME_COOKIE);
    if (storedWmtUsername != null) {
      oracle.add(storedWmtUsername);
    }
    emailBox.setText(null);
    passwordBox.setText(null);
    inputPanel.setVisible(true);
    statusPanel.setVisible(false);
    signInButton.setHTML(Constants.SIGN_IN);
  }
  
  /**
   * Prefaces the sign out action: shows the user's login name; displays
   * "Sign Out" on the button.
   */
  public void showStatusPanel() {
    inputPanel.setVisible(false);
    statusPanel.setVisible(true);
    signInButton.setHTML(Constants.SIGN_OUT);
  }
  
  public SuggestBox getEmailBox() {
    return emailBox;
  }

  public void setEmailBox(SuggestBox emailBox) {
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
