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
   * Makes a SaveDialogBox with a default filename.
   */
  public SaveDialogBox() {
    this("Model 0");
  }
  
  /**
   * Makes a SaveDialogBox with a user-supplied filename.
   */
  public SaveDialogBox(String fileName) {

    super(true); // autohide
    this.setModal(true);
    this.setText("Save Model As...");

    filePanel = new FilePanel();
    choicePanel = new ChoicePanel();

    filePanel.setFile(fileName);
    choicePanel.getOkButton().setHTML("<i class='fa fa-floppy-o'></i> Save");
    
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
