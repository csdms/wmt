/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A customized DialogBox with a {@link DroplistPanel} for choosing an item
 * and a {@link ChoicePanel} displaying "OK" and "Cancel" buttons.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DroplistDialogBox extends DialogBox {

  private DroplistPanel itemPanel;
  private ChoicePanel choicePanel;
  
  /**
   * Makes a new DroplistDialogBox with default settings in the
   * {@link DroplistPanel} and {@link ChoicePanel}.
   */
  public DroplistDialogBox() {

    super(false); // autohide
    this.setModal(true);
    this.setStyleName("wmt-DialogBox");

    itemPanel = new DroplistPanel();
    choicePanel = new ChoicePanel();

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.add(itemPanel);
    contents.add(choicePanel);

    this.setWidget(contents);
  }

  public DroplistPanel getDroplistPanel() {
    return itemPanel;
  }

  public void setDroplistPanel(DroplistPanel itemPanel) {
    this.itemPanel = itemPanel;
  }

  public ChoicePanel getChoicePanel() {
    return choicePanel;
  }

  public void setChoicePanel(ChoicePanel choicePanel) {
    this.choicePanel = choicePanel;
  }
}
