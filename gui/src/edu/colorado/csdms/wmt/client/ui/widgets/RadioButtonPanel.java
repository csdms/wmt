/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * A GWT composite that displays a label and a pair of radio buttons wrapped in
 * a {@link HorizontalPanel}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class RadioButtonPanel extends Composite {

  private Label panelLabel;
  private RadioButton leftButton;
  private RadioButton rightButton;
  
  /**
   * Makes a new {@link RadioButtonPanel} with default labels.
   */
  public RadioButtonPanel() {
    
    panelLabel = new Label("Name:");
    leftButton = new RadioButton("RadioButtonPanel", "True");
    rightButton = new RadioButton("RadioButtonPanel", "False");
    rightButton.setValue(true);
    
    HorizontalPanel contents = new HorizontalPanel();
    contents.setSpacing(5); // px
    contents.add(panelLabel);
    contents.add(leftButton);
    contents.add(rightButton);
    
    initWidget(contents);
  }

  public Label getPanelLabel() {
    return panelLabel;
  }

  public void setPanelLabel(Label panelLabel) {
    this.panelLabel = panelLabel;
  }

  public RadioButton getLeftButton() {
    return leftButton;
  }

  public void setLeftButton(RadioButton leftButton) {
    this.leftButton = leftButton;
  }

  public RadioButton getRightButton() {
    return rightButton;
  }

  public void setRightButton(RadioButton rightButton) {
    this.rightButton = rightButton;
  }
}
