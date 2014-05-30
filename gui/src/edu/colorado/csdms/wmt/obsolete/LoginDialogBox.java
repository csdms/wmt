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
package edu.colorado.csdms.wmt.obsolete;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.ui.widgets.ChoicePanel;
import edu.colorado.csdms.wmt.client.ui.widgets.FieldPanel;

/**
 * A {@link DialogBox} with {@link FieldPanel}s for entering a username and
 * password.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
@Deprecated
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
