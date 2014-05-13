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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A customized DialogBox with a droplist for choosing the host to run the
 * model, as well as input fields for logging into the host.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class RunDialogBox extends DialogBox {

  private DroplistPanel hostPanel;
  private FieldPanel usernamePanel;
  private FieldPanel passwordPanel;
  private ChoicePanel choicePanel;
  
  /**
   * Makes a new RunDialogBox.
   */
  public RunDialogBox() {

    super(false); // autohide
    this.setModal(true);
    this.setStyleName("wmt-DialogBox");
    this.setText("Run Model...");

    hostPanel = new DroplistPanel();

    HTML separator = new HTML();
    separator.setStyleName("wmt-PopupPanelSeparator");
    
    usernamePanel = new FieldPanel();
    passwordPanel = new FieldPanel(true); // uses PasswordTextBox
    choicePanel = new ChoicePanel();
    
    hostPanel.getLabel().setText("Host:");
    usernamePanel.getLabel().setText("Username:");
    passwordPanel.getLabel().setText("Password:");
    choicePanel.getOkButton().setHTML("<i class='fa fa-play'></i> Run");

    VerticalPanel panel = new VerticalPanel();
    panel.setWidth("100%");
    panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    panel.setSpacing(10); // px
    panel.add(hostPanel);
    panel.add(separator);
    
    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.getElement().getStyle().setMargin(5, Unit.PX);
    contents.add(panel);
    contents.add(usernamePanel);
    contents.add(passwordPanel);
    contents.add(choicePanel);

    this.setWidget(contents);
  }

  public DroplistPanel getHostPanel() {
    return hostPanel;
  }

  public void setHostPanel(DroplistPanel hostPanel) {
    this.hostPanel = hostPanel;
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
