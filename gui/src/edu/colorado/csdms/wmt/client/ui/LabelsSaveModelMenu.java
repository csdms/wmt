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

import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.Constants;
import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.data.LabelJSO;

/**
 * Encapsulates an alphabetized, scrollable list of labels used to tag and
 * classify models. This menu is modeled on the "Labels" menu in Gmail.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LabelsSaveModelMenu extends PopupPanel {

  private DataManager data;
  private VerticalPanel labelPanel;

  /**
   * Makes a new {@link LabelsSaveModelMenu}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public LabelsSaveModelMenu(DataManager data) {
    
    super(true); // autohide
    this.data = data;
    this.setStyleName("wmt-PopupPanel");

    // A VerticalPanel for the menu items. (PopupPanels have only one child.)
    VerticalPanel menu = new VerticalPanel();
    this.add(menu);
    
    // All labels are listed on the labelPanel, which sits on a ScrollPanel.
    labelPanel = new VerticalPanel();
    ScrollPanel scroller = new ScrollPanel(labelPanel);
    scroller.setSize(Constants.MENU_WIDTH, Constants.MENU_HEIGHT);
    menu.add(scroller);

    // Populate the menu with the stored model labels and their values.
    populateMenu();
  }
  
  /**
   * A helper that loads the {@link LabelsSaveModelMenu} with {@link CheckBox} labels.
   * Each CheckBox has a handler that maps the selection state of the box to the
   * labels variable stored in the {@link DataManager}.
   */
  public void populateMenu() {
    labelPanel.clear();
    for (Map.Entry<String, LabelJSO> entry : data.modelLabels.entrySet()) {
      CheckBox labelBox = new CheckBox(entry.getKey());
      labelBox.setWordWrap(false);
      labelBox.setStyleName("wmt-PopupPanelCheckBoxItem");
      labelBox.setValue(entry.getValue().isSelected());
      if (data.security.isLoggedIn()
          && !data.security.getWmtUsername()
              .equals(entry.getValue().getOwner())) {
        labelBox.addStyleDependentName("public");
      }
      labelBox.addClickHandler(new LabelSelectionHandler(data, entry));

      // Force the "public" and username labels to the top, in either order.
      if (entry.getKey().equals("public")
          || entry.getKey().equals(data.security.getWmtUsername())) {
        labelPanel.insert(labelBox, 0);
      } else {
        labelPanel.add(labelBox);
      }      
    }
  }

  /**
   * Handles actions when the selection state of a label changes.
   */
  public class LabelSelectionHandler implements ClickHandler {

    private DataManager data;
    private Entry<String, LabelJSO> entry;
    
    public LabelSelectionHandler(DataManager data, Entry<String, LabelJSO> entry) {
      this.data = data;
      this.entry = entry;
    }
    
    @Override
    public void onClick(ClickEvent event) {
      CheckBox labelBox = (CheckBox) event.getSource();
      entry.getValue().isSelected(labelBox.getValue());
      data.updateModelSaveState(false);
    } 
  }

}
