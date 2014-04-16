/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.control.DataManager;

/**
 * A customized DialogBox with elements for setting a public or private model,
 * the driver and case of the model, and a name/description of the model. "OK"
 * and "Cancel" buttons are shown on the bottom of the dialog.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class SaveDialogBox extends DialogBox {

  private static final String DEFAULT_CASE = "default";
  private static final String ADD_NEW_CASE = "<add a new case>";
  
  private DataManager data;
  private RadioButtonPanel accessPanel;
  private DroplistPanel driverPanel;
  private DroplistPanel casePanel;
  private FieldPanel namePanel;
  private ChoicePanel choicePanel;
  
  /**
   * Makes a SaveDialogBox with a default name.
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

    accessPanel = new RadioButtonPanel();
    accessPanel.getPanelLabel().removeFromParent();
    accessPanel.getLeftButton().setText("Public");
    accessPanel.getRightButton().setText("Private");

    // Displays the driver for the model in a droplist. If no driver has been
    // set, the first component in the list is displayed.
    driverPanel = new DroplistPanel();
    driverPanel.getLabel().removeFromParent();
    for (int i = 0; i < data.getComponents().size(); i++) {
      driverPanel.getDroplist().addItem(data.getComponent(i).getName());
      String driverId =
          data.getPerspective().getModelTree().getDriverComponentCell()
              .getComponentId();
      if (data.getComponent(i).getId().equals(driverId)) {
        driverPanel.getDroplist().setItemSelected(i, true);
      }
    }

    String[] cases = {DEFAULT_CASE, ADD_NEW_CASE};
    casePanel = new DroplistPanel(cases);
    casePanel.getLabel().removeFromParent();
    casePanel.getDroplist().addChangeHandler(new AddNewCaseHandler());

    namePanel = new FieldPanel();
    namePanel.getLabel().removeFromParent();
    namePanel.getField().setText(modelName);

    Integer nRows = 4, nCols = 2;
    Grid grid = new Grid(nRows, nCols);
    grid.setWidth("100%");
    grid.setWidget(0, 0, new Label("Visibility:"));
    grid.setWidget(0, 1, accessPanel);
    grid.setWidget(1, 0, new Label("Driver:"));
    grid.setWidget(1, 1, driverPanel);
    grid.setWidget(2, 0, new Label("Case:"));
    grid.setWidget(2, 1, casePanel);
    grid.setWidget(3, 0, new Label("Name:"));
    grid.setWidget(3, 1, namePanel);

    for (int i = 0; i < nRows; i++) {
      grid.getCellFormatter().setHorizontalAlignment(i, 0,
          HasHorizontalAlignment.ALIGN_RIGHT);
    }

    choicePanel = new ChoicePanel();
    choicePanel.getOkButton().setHTML("<i class='fa fa-floppy-o'></i> Save");

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.add(grid);
    contents.add(choicePanel);

    this.setWidget(contents);
  }

  public RadioButtonPanel getAccessPanel() {
    return accessPanel;
  }

  public void setAccessPanel(RadioButtonPanel accessPanel) {
    this.accessPanel = accessPanel;
  }

  public DroplistPanel getDriverPanel() {
    return driverPanel;
  }

  public void setDriverPanel(DroplistPanel driverPanel) {
    this.driverPanel = driverPanel;
  }

  public DroplistPanel getCasePanel() {
    return casePanel;
  }

  public void setCasePanel(DroplistPanel casePanel) {
    this.casePanel = casePanel;
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

  /**
   * 
   */
  public class AddNewCaseHandler implements ChangeHandler {

    @Override
    public void onChange(ChangeEvent event) {

      Integer selectedIndex = casePanel.getDroplist().getSelectedIndex();
      String selectedItem = casePanel.getDroplist().getItemText(selectedIndex);

      // Woo-hoo!
      if (selectedItem.equals(ADD_NEW_CASE)) {
        Window.alert(selectedItem);
      }

    }
  }
}
