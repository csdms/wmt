/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.Constants;

/**
 * A {@link DialogBox} that prompts a user to reenter their password when
 * creating a new WMT sign in.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class NewUserDialogBox extends DialogBox {

  private FieldPanel passwordPanel;
  private ChoicePanel choicePanel;
  
  /**
   * Creates a {@link NewUserDialogBox}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public NewUserDialogBox() {

    super(false); // autohide
    this.setModal(true);
    this.setText("New User");
    this.setStyleName("wmt-DialogBox");
    
    String msg = "This email address is not registered with WMT."
        + " If you would like to use this address as your sign in,"
        + " please reenter your password below and click \"New User\";"
        + " if not, click \"Cancel\".";
    HTML msgHtml = new HTML(msg);
    
    passwordPanel = new FieldPanel(true); // uses PasswordTextBox
    passwordPanel.getLabel().setText("Reenter password:");
    
    choicePanel = new ChoicePanel();
    choicePanel.getOkButton().setHTML(Constants.FA_USER + "New User");
    
    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    contents.setWidth("30em");
    contents.setSpacing(5); // px
    contents.getElement().getStyle().setPaddingTop(5.0, Unit.PX);
    contents.add(msgHtml);
    contents.add(passwordPanel);
    contents.add(choicePanel);

    this.setWidget(contents);
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
