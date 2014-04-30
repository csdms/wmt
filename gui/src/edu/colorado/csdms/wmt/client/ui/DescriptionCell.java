/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.user.client.ui.HTML;

import edu.colorado.csdms.wmt.client.data.ParameterJSO;

/**
 * A cell for the first column in a ParameterTable, holding the parameter
 * description with its units.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DescriptionCell extends HTML {

  /**
   * Makes a DescriptionCell from the information contained in the input
   * ParameterJSO object.
   * 
   * @param parameter a ParameterJSO object
   */
  public DescriptionCell(ParameterJSO parameter) {

    String units = parameter.getValue().getUnits();
    String description = parameter.getDescription();

    if (units != null) {
      description += " (" + units + ")";
    }

    this.setHTML(description);
  }
}
