/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 mcflugen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Command;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.ComponentCell;

/**
 * Defines the action for the "Show parameters" menu item in a
 * {@link ComponentCell}; displays the parameters of the component in the 
 * ParameterTable.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentShowParametersCommand implements Command {

  private DataManager data;
  private ComponentCell cell;
  private String componentId;

  /**
   * Creates a new instance of {@link ComponentShowParametersCommand}.
   * 
   * @param data the DataManager object for the WMT session
   * @param cell the {@link ComponentCell} this Command acts on
   */
  public ComponentShowParametersCommand(DataManager data, ComponentCell cell) {
    this.data = data;
    this.cell = cell;
    this.componentId = cell.getComponentId();
  }

  @Override
  public void execute() {
    GWT.log("Show parameters for: " + data.getComponent(componentId).getName());
    data.getPerspective().getParameterTable().clearTable();
    data.getPerspective().getParameterTable().loadTable(componentId);
    ComponentCell wasShowingParameters = data.getShowingParameters();
    wasShowingParameters.removeStyleDependentName("showingParameters");
    data.setShowingParameters(cell);
  }
}
