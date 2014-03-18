/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.user.client.ui.DialogBox;
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
    this.setText("Run Model...");

    hostPanel = new DroplistPanel();
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
    panel.add(hostPanel);
    
    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
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
