/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

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

  private FilePanel filePanel;
  private ChoicePanel choicePanel;
  
  /**
   * Makes a new SaveDialogBox.
   */
  public SaveDialogBox() {

    super(true); // autohide

    filePanel = new FilePanel();
    choicePanel = new ChoicePanel();

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.add(filePanel);
    contents.add(choicePanel);

    this.setWidget(contents);
  }

  public FilePanel getFilePanel() {
    return filePanel;
  }

  public void setFilePanel(FilePanel filePanel) {
    this.filePanel = filePanel;
  }

  public ChoicePanel getChoicePanel() {
    return choicePanel;
  }

  public void setChoicePanel(ChoicePanel choicePanel) {
    this.choicePanel = choicePanel;
  }
}
