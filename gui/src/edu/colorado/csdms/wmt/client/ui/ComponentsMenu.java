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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.Constants;
import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.data.ComponentJSO;
import edu.colorado.csdms.wmt.client.ui.widgets.ComponentInfoDialogBox;

/**
 * Encapsulates an alphabetized, scrollable list of components.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentsMenu extends PopupPanel {
  
  private DataManager data;
  private VerticalPanel componentsPanel;
  
  /**
   * Makes a new {@link ComponentsMenu}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public ComponentsMenu(DataManager data) {

    super(true); // autohide
    this.data = data;
    this.setStyleName("wmt-PopupPanel");

    // A VerticalPanel for the menu items. (PopupPanels have only one child.)
    VerticalPanel menu = new VerticalPanel();
    this.add(menu);
    
    // Components are listed on the componentsPanel, situated on a ScrollPanel.
    componentsPanel = new VerticalPanel();
    ScrollPanel scroller = new ScrollPanel(componentsPanel);
    scroller.setSize(Constants.MENU_WIDTH, Constants.MENU_HEIGHT);
    menu.add(scroller);
    
    // Populate the menu with the components.
    populateMenu();
  }

  /**
   * A helper that loads the {@link ComponentsMenu} with components.
   */
  public void populateMenu() {
    componentsPanel.clear();    
    for (int i = 0; i < data.getComponents().size(); i++) {
      String componentId = data.componentIdList.get(i);
      final ComponentJSO componentJSO = data.getComponent(componentId);
      HTML item = new HTML(componentJSO.getName());
      item.setStyleName("wmt-ComponentSelectionMenuItem");
      item.addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          ComponentInfoDialogBox componentInfoDialogBox =
              data.getPerspective().getComponentInfoBox();
          componentInfoDialogBox.update(componentJSO);
          componentInfoDialogBox.center();
        }
      });
      componentsPanel.add(item);
    }
  }
}
