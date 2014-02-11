/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A customized DialogBox with a droplist for choosing a model and ok-cancel
 * buttons.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class OpenDialogBox extends DialogBox {

  private DroplistPanel modelPanel;
  private ChoicePanel choicePanel;
  
  /**
   * Makes a new SaveDialogBox.
   */
  public OpenDialogBox() {

    super(true); // autohide
    this.setModal(true);

    modelPanel = new DroplistPanel();
    choicePanel = new ChoicePanel();

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.add(modelPanel);
    contents.add(choicePanel);

    this.setWidget(contents);
  }

  public DroplistPanel getModelPanel() {
    return modelPanel;
  }

  public void setModelPanel(DroplistPanel modelPanel) {
    this.modelPanel = modelPanel;
  }

  public ChoicePanel getChoicePanel() {
    return choicePanel;
  }

  public void setChoicePanel(ChoicePanel choicePanel) {
    this.choicePanel = choicePanel;
  }
}
