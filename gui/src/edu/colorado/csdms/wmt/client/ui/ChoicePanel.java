/**
 * 
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Mark Piper (mark.piper@colorado.edu)
 * 
 */
public class ChoicePanel extends Composite {

  private Button okButton;
  private Button cancelButton;

  /**
   * Makes a new choice panel with default "OK" and "Cancel" buttons.
   */
  public ChoicePanel() {

    okButton = new Button("<i class='fa fa-folder-open-o'></i> OK");
    cancelButton = new Button("<i class='fa fa-ban'></i> Cancel");
    HorizontalPanel buttonPanel = new HorizontalPanel();
    buttonPanel.setSpacing(5); // px
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    HorizontalPanel choicePanel = new HorizontalPanel();
    choicePanel.setWidth("100%");
    choicePanel.add(buttonPanel);

    VerticalPanel contents = new VerticalPanel();
    contents.add(choicePanel);
    
    initWidget(contents);
  }

  public Button getOkButton() {
    return okButton;
  }

  public void setOkButton(Button okButton) {
    this.okButton = okButton;
  }

  public Button getCancelButton() {
    return cancelButton;
  }

  public void setCancelButton(Button cancelButton) {
    this.cancelButton = cancelButton;
  }

}
