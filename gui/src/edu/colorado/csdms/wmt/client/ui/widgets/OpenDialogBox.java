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

import edu.colorado.csdms.wmt.client.Constants;
import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.LabelsMenu;

/**
 * A customized DialogBox with a droplist for selecting a model and a "Labels"
 * button for selecting labels, used to filter the list of models. "OK" and
 * "Cancel" buttons are shown on the bottom of the dialog.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class OpenDialogBox extends DialogBox {

  @SuppressWarnings("unused")
  private DataManager data;
  private DroplistPanel droplistPanel;
  private ChoicePanel choicePanel;
  private LabelsMenu labelsMenu;
  
  /**
   * Makes an {@link OpenDialogBox}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public OpenDialogBox(DataManager data) {
    
    super(false); // autohide
    this.setModal(true);
    this.setStyleName("wmt-DialogBox");
    this.setText("Open Model...");
    this.data = data;
    data.getPerspective().setOpenDialogBox(this);
    
    droplistPanel = new DroplistPanel();

    final Button labelsButton = new Button(Constants.FA_TAGS + "Labels");
    labelsButton.setStyleName("wmt-Button");
    labelsMenu = new LabelsMenu(data, this);
    labelsButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        labelsMenu.populateMenu();
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
    row.add(droplistPanel);
    row.add(labelsButton);
    row.setCellVerticalAlignment(labelsButton,
        HasVerticalAlignment.ALIGN_MIDDLE);

    choicePanel = new ChoicePanel();
    choicePanel.getOkButton().setHTML(Constants.FA_OPEN + "Open");

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.add(row);
    contents.add(choicePanel);

    this.setWidget(contents);
  }

  public DroplistPanel getDroplistPanel() {
    return droplistPanel;
  }

  public void setDroplistPanel(DroplistPanel droplistPanel) {
    this.droplistPanel = droplistPanel;
  }

  public ChoicePanel getChoicePanel() {
    return choicePanel;
  }

  public void setChoicePanel(ChoicePanel choicePanel) {
    this.choicePanel = choicePanel;
  }
}
