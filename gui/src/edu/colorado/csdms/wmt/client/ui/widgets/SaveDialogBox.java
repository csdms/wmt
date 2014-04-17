/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.LabelsMenu;

/**
 * A customized DialogBox with a field for setting the name/description of the
 * model, and a "Labels" button for applying labels to the model (used for
 * filtering). "OK" and "Cancel" buttons are shown on the bottom of the dialog.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class SaveDialogBox extends DialogBox {
  
  @SuppressWarnings("unused")
  private DataManager data;
  private FieldPanel namePanel;
  private ChoicePanel choicePanel;
  private LabelsMenu labelsMenu;
  
  /**
   * Makes a {@link SaveDialogBox} with a default name.
   * 
   * @param data the DataManager object for the WMT session
   */
  public SaveDialogBox(DataManager data) {
    this(data, DataManager.DEFAULT_MODEL);
  }
  
  /**
   * Makes a SaveDialogBox with a user-supplied name.
   * 
   * @param data the DataManager object for the WMT session
   * @param modelName a descriptive name for the model
   */
  public SaveDialogBox(DataManager data, String modelName) {

    super(false); // autohide
    this.setModal(true);
    this.setText("Save Model As...");
    this.data = data;

    namePanel = new FieldPanel(modelName);

    final Button labelsButton = new Button(DataManager.FA_TAGS + "Labels");
    labelsButton.setStyleName("wmt-Button");
    labelsMenu = new LabelsMenu(data);
    labelsButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        labelsMenu.setPopupPositionAndShow(new PositionCallback() {
          final Integer x = labelsButton.getElement().getAbsoluteRight();
          final Integer y = labelsButton.getAbsoluteTop();
          @Override
          public void setPosition(int offsetWidth, int offsetHeight) {
            labelsMenu.setPopupPosition(x, y);
          }
        });
      }
    });

    HorizontalPanel row = new HorizontalPanel();
    row.setSpacing(5); // px
    row.add(namePanel);
    row.add(labelsButton);
    row.setCellVerticalAlignment(labelsButton,
        HasVerticalAlignment.ALIGN_MIDDLE);

    choicePanel = new ChoicePanel();
    choicePanel.getOkButton().setHTML(DataManager.FA_SAVE + "Save");

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.add(row);
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
