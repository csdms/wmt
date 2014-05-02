/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A GWT composite widget that displays right-aligned "OK" and "Cancel"
 * buttons, with icons. Each button's text and icon can be modified.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ChoicePanel extends Composite {

  private Button okButton;
  private Button cancelButton;

  /**
   * Makes a new ChoicePanel with the default "OK" and "Cancel" buttons.
   */
  public ChoicePanel() {

    okButton = new Button("<i class='fa fa-check'></i> OK");
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
    
    okButton.setStyleName("wmt-Button");
    cancelButton.setStyleName("wmt-Button");
    okButton.getElement().getStyle().setMarginRight(3, Unit.PX);
    
    initWidget(contents);
  }

  /**
   * Returns the "OK" {@link Button} used in a ChoicePanel.
   */
  public Button getOkButton() {
    return okButton;
  }

  /**
   * Sets the "OK" {@link Button} used in a ChoicePanel.
   */
  public void setOkButton(Button okButton) {
    this.okButton = okButton;
  }

  /**
   * Returns the "Cancel" {@link Button} used in a ChoicePanel.
   */
  public Button getCancelButton() {
    return cancelButton;
  }

  /**
   * Sets the "Cancel" {@link Button} used in a ChoicePanel.
   */
  public void setCancelButton(Button cancelButton) {
    this.cancelButton = cancelButton;
  }

}
