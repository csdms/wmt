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
package edu.colorado.csdms.wmt.client.ui;

import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.Widget;

import edu.colorado.csdms.wmt.client.Constants;
import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.handler.ParameterActionPanelResetHandler;
import edu.colorado.csdms.wmt.client.ui.widgets.ComponentInfoDialogBox;

/**
 * Makes a row of action buttons ("Reset", "View input files", etc.) for working
 * with the parameters of a model component in WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ParameterActionPanel extends HorizontalPanel {

  private DataManager data;
  private String componentId;
  private ViewInputFilesMenu inputFilesMenu;
  
  /**
   * Makes a new {@link ParameterActionPanel}.
   * 
   * @param data the DataManager instance for the WMT session
   */
  public ParameterActionPanel(DataManager data, String componentId) {

    this.data = data;
    this.componentId = componentId;
    this.setStyleName("wmt-ActionPanel");

    // Reset
    Button resetButton = new Button("<i class='fa fa-bolt'></i>");
    resetButton.setTitle(Constants.PARAMETER_RESET);
    resetButton.addClickHandler(new ParameterActionPanelResetHandler(this.data,
        this.componentId));
    this.add(resetButton);

    // View input files
    final Button viewFilesButton =
        new Button("<i class='fa fa-external-link'></i>");
    viewFilesButton.setTitle(Constants.PARAMETER_VIEW_FILE);
    this.add(viewFilesButton);
    inputFilesMenu = new ViewInputFilesMenu(this.data, this.componentId);
    viewFilesButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        inputFilesMenu.populateMenu();
        inputFilesMenu.setPopupPositionAndShow(new PositionCallback() {
          final Integer x = viewFilesButton.getElement().getAbsoluteLeft();
          final Integer y = viewFilesButton.getElement().getAbsoluteBottom();
          @Override
          public void setPosition(int offsetWidth, int offsetHeight) {
            inputFilesMenu.setPopupPosition(x, y);
          }
        });
      }
    });

    // Component help
    Button helpButton = new Button(Constants.FA_HELP);
    helpButton.setTitle(Constants.COMPONENT_INFO);
    helpButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        ComponentInfoDialogBox componentInfoDialogBox =
            ParameterActionPanel.this.data.getPerspective()
                .getComponentInfoBox();
        componentInfoDialogBox.update(ParameterActionPanel.this.data
            .getComponent(ParameterActionPanel.this.componentId));
        componentInfoDialogBox.center();
      }
    });
    this.add(helpButton);
    
    // Apply a style to each button.
    Iterator<Widget> iter = this.iterator();
    while (iter.hasNext()) {
      Button button = (Button) iter.next();
      button.setStyleName("wmt-ActionPanelButton");
    }
  }
}
