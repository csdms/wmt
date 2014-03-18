/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A customized DialogBox with fields for specifying a model name and the
 * location where it shall be saved.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class SaveDialogBox extends DialogBox {

  private FieldPanel fieldPanel;
  private ChoicePanel choicePanel;
  
  /**
   * Makes a SaveDialogBox with a default filename.
   */
  public SaveDialogBox() {
    this("Model 0");
  }
  
  /**
   * Makes a SaveDialogBox with a user-supplied filename.
   */
  public SaveDialogBox(String fileName) {

    super(false); // autohide
    this.setModal(true);
    this.setText("Save Model As...");

    fieldPanel = new FieldPanel();
    choicePanel = new ChoicePanel();

    fieldPanel.setField(fileName);
    choicePanel.getOkButton().setHTML("<i class='fa fa-floppy-o'></i> Save");
    
    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.add(fieldPanel);
    contents.add(choicePanel);

    this.setWidget(contents);
  }

  public FieldPanel getFilePanel() {
    return fieldPanel;
  }

  public void setFilePanel(FieldPanel fieldPanel) {
    this.fieldPanel = fieldPanel;
  }

  public ChoicePanel getChoicePanel() {
    return choicePanel;
  }

  public void setChoicePanel(ChoicePanel choicePanel) {
    this.choicePanel = choicePanel;
  }
}
