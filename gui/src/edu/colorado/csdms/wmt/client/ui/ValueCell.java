/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

import edu.colorado.csdms.wmt.client.data.ParameterJSO;

/**
 * Used to display the value of a parameter in a ParameterTable, a ValueCell
 * renders as a ListBox if the parameter type = "choice"; otherwise, it renders
 * as an editable TextBox. Changes to the value in a ValueCell are stored in
 * the WMT DataManager.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ValueCell extends SimplePanel {

  private TextBox valueTextBox;
  private ListBox valueListBox;
  private ParameterJSO parameter;

  /**
   * Makes a ValueCell from the information contained in the input ParameterJSO
   * object.
   * 
   * @param parameter a ParameterJSO object
   */
  public ValueCell(ParameterJSO parameter) {

    this.parameter = parameter;
    
    // If the parameter doesn't have a value (e.g., a separator),
    // short-circuit the method and return.
    if (this.parameter.getValue() == null) {
      return;
    }
    
    // Helpful locals.
    String type = this.parameter.getValue().getType();
    String value = this.parameter.getValue().getDefault();
    String range = "";

    // Initialize both boxes, though only one will be added to panel.
    valueTextBox = new TextBox();
    valueListBox = new ListBox(false); // no multi select

    if (!type.matches("choice")) {
      valueTextBox.setText(value);
      valueTextBox.getElement().getStyle().setBackgroundColor("#ffc");
      this.add(valueTextBox);
    } else {
      Integer nChoices = this.parameter.getValue().getChoices().length();
      for (int i = 0; i < nChoices; i++) {
        valueListBox.addItem(this.parameter.getValue().getChoices().get(i));
        if (valueListBox.getItemText(i).matches(value)) {
          valueListBox.setSelectedIndex(i);
        }
      }
      valueListBox.setVisibleItemCount(1); // show one item -- a droplist
      this.add(valueListBox);
    }

    // If appropriate, add a tooltip showing the valid range of the value.
    if (!type.matches("string") && !type.matches("choice")) {
      range +=
          "Valid range = ( " + parameter.getValue().getMin() + ", "
              + parameter.getValue().getMax() + " )";
      this.setTitle(range);
    }
    
    /*
     * Handles keyboard events in the TextBox -- every key press, so there could
     * be many. Might consider acting on only <Tab> or <Enter>.
     */
    valueTextBox.addKeyUpHandler(new KeyUpHandler() {
      @Override
      public void onKeyUp(KeyUpEvent event) {
        GWT.log("(onKeyUp)");
        String value = valueTextBox.getText();
        setValue(value);
      }
    });

    /*
     * Handles selection in the "choices" ListBox.
     */
    valueListBox.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        GWT.log("(onChange)");
        String value = valueListBox.getValue(valueListBox.getSelectedIndex());
        setValue(value);
      }
    });
  }

  /**
   * Passes the modified value up to ParameterTable#setValue. This isn't an
   * elegant solution, but ParameterTable knows the component this parameter
   * belongs to and it has access to the DataManager object for storage.
   * 
   * @param value
   */
  public void setValue(String value) {
    ParameterTable pt = (ParameterTable) ValueCell.this.getParent();
    pt.setValue(parameter, value);
  }

}
