/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.data.ParameterJSO;

/**
 * Builds a table of parameters for a single WMT model component. The value of
 * the parameter is editable.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ParameterTable extends FlexTable {

  public DataManager data;
  private String componentId; // the id of the displayed component
  private ParameterActionPanel actionPanel;

  /**
   * Initializes a table of parameters for a single WMT model component. The
   * table is empty until {@link #loadTable()} is called.
   * 
   * @param data the DataManager instance for the WMT session
   */
  public ParameterTable(DataManager data) {

    this.data = data;
    this.setWidth("100%");
  }

  /**
   * A worker that displays an informational message in the ParameterTable.
   */
  public void showInfoMessage() {
    HTML infoMessage =
        new HTML("Select a model component to view and edit its parameters");
    infoMessage.setStyleName("wmt-ParameterTableMessage");
    this.setWidget(0, 0, infoMessage);
  }
  
  /**
   * A worker that loads the ParameterTable with parameter values for the
   * selected model component. Displays a {@link ViewInputFilesPanel} at the
   * bottom of the table.
   * 
   * @param the id of the component whose parameters are to be displayed
   */
  public void loadTable(String componentId) {

    // The component whose parameters are to be displayed.
    this.setComponentId(componentId);

    // Return if the selected component doesn't have parameters.
    if (data.getModelComponent(componentId).getParameters() == null) {
      this.clearTable();
      Window.alert("No parameters defined for this component.");
      return;
    }

    // Set the component name on the tab holding the ParameterTable.
    data.getPerspective().setParameterPanelTitle(componentId);

    // Keep track of where we are in the table.
    Integer tableRowIndex = 0;
    
    // Add the ParameterActionPanel. Align it with the ModelActionPanel.
    actionPanel = new ParameterActionPanel(data, componentId);
    actionPanel.getElement().getStyle().setMarginTop(-3.0, Unit.PX);
    this.setWidget(tableRowIndex, 0, actionPanel);
    tableRowIndex++;
    
    // Build the parameter table.
    Integer nParameters =
        data.getModelComponent(componentId).getParameters().length();
    for (int i = 0; i < nParameters; i++) {
      ParameterJSO parameter =
          data.getModelComponent(componentId).getParameters().get(i);
      this.setWidget(tableRowIndex, 0, new DescriptionCell(parameter));
      if (parameter.getKey().matches("separator")) {
        this.getFlexCellFormatter().setColSpan(tableRowIndex, 0, 2);
        this.getFlexCellFormatter().setStyleName(tableRowIndex, 0,
            "wmt-ParameterSeparator");
      } else {
        this.setWidget(tableRowIndex, 1, new ValueCell(parameter));
        this.getFlexCellFormatter().setStyleName(tableRowIndex, 0,
            "wmt-ParameterDescription");
        this.getFlexCellFormatter().setHorizontalAlignment(tableRowIndex, 1,
            HasHorizontalAlignment.ALIGN_RIGHT);
      }
      tableRowIndex++;
    }
  }

  /**
   * Stores the modified value of a parameter of a model component in the WMT
   * {@link DataManager}.
   * 
   * @param parameter the ParameterJSO object for the parameter being modified
   * @param newValue the new parameter value, a String
   */
  public void setValue(ParameterJSO parameter, String newValue) {

    String key = parameter.getKey();
    String previousValue =
        data.getModelComponent(componentId).getParameter(key).getValue()
            .getDefault();
    GWT.log(componentId + ": " + key + ": " + newValue);

    // Don't update state when tabbing between fields or moving within field.
    // XXX Would be better to handle this further upstream.
    if (!newValue.matches(previousValue)) {
      data.getModelComponent(componentId).getParameter(key).getValue()
          .setDefault(newValue);
      data.modelIsSaved(false);
      data.getPerspective().setModelPanelTitle();
    }
  }

  /**
   * Deletes the contents of the ParameterTable and resets the tab title to
   * "Parameters".
   */
  public void clearTable() {
    this.setComponentId(null);
    data.getPerspective().setParameterPanelTitle(null);
    this.removeAllRows();
    this.clear(true);
  }

  /**
   * Returns the id of the model component (a String) whose parameters are
   * displayed in the ParameterTable.
   */
  public String getComponentId() {
    return componentId;
  }

  /**
   * Stores the id of the model component (a String) whose parameters are
   * displayed in the ParameterTable.
   * 
   * @param componentId a component id, a String
   */
  public void setComponentId(String componentId) {
    this.componentId = componentId;
  }
}
