/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A customized DialogBox with elements for setting a public or private model,
 * the driver and case of the model, and a name/description of the model. "OK"
 * and "Cancel" buttons are shown on the bottom of the dialog.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class SaveDialogBox extends DialogBox {

  private FieldPanel namePanel;
  private ChoicePanel choicePanel;
  
  /**
   * Makes a SaveDialogBox with a default name.
   */
  public SaveDialogBox() {
    this("Model 0");
  }
  
  /**
   * Makes a SaveDialogBox with a user-supplied name.
   */
  public SaveDialogBox(String fileName) {

    super(false); // autohide
    this.setModal(true);
    this.setText("Save Model As...");

    namePanel = new FieldPanel();
    choicePanel = new ChoicePanel();

    namePanel.setField(fileName);
    choicePanel.getOkButton().setHTML("<i class='fa fa-floppy-o'></i> Save");
    
    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.add(namePanel);
    contents.add(choicePanel);

    this.setWidget(contents);
  }

  public FieldPanel getNamePanel() {
    return namePanel;
  }

  public void setNamePanel(FieldPanel namePanel) {
    this.namePanel = namePanel;
  }

  public ChoicePanel getChoicePanel() {
    return choicePanel;
  }

  public void setChoicePanel(ChoicePanel choicePanel) {
    this.choicePanel = choicePanel;
  }
}
