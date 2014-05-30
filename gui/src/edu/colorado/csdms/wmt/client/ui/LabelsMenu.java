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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.Constants;
import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.data.LabelJSO;
import edu.colorado.csdms.wmt.client.ui.handler.DialogCancelHandler;
import edu.colorado.csdms.wmt.client.ui.widgets.LabelDialogBox;

/**
 * Encapsulates an alphabetized, scrollable list of labels used to tag and
 * classify models. This menu is modeled on the "Labels" menu in Gmail.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LabelsMenu extends PopupPanel {

  private DataManager data;
  private VerticalPanel labelPanel;
  private HTML addNewHtml;
  private HTML deleteHtml;
  
  /**
   * Makes a new {@link LabelsMenu}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public LabelsMenu(DataManager data) {
    
    super(true); // autohide
    this.data = data;
    this.setStyleName("wmt-PopupPanel");
    data.getPerspective().setLabelsMenu(this);

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

    // These items are always visible on the bottom of the menu.
    HTML separator = new HTML("");
    separator.setStyleName("wmt-PopupPanelSeparator");
    addNewHtml = new HTML("Add new label");
    addNewHtml.setStyleName("wmt-PopupPanelItem");
    deleteHtml = new HTML("Delete label");
    deleteHtml.setStyleName("wmt-PopupPanelItem");
    menu.add(separator);
    menu.add(addNewHtml);
    menu.add(deleteHtml);
    
    // Show a SuggestBox to add or delete a label.
    addNewHtml.addClickHandler(new LabelAddRemoveHandler(data, "Add"));
    deleteHtml.addClickHandler(new LabelAddRemoveHandler(data, "Delete"));    
  }
  
  /**
   * A helper that loads the {@link LabelsMenu} with {@link CheckBox} labels.
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
      labelPanel.add(labelBox);
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

  /**
   * Handles adding and deleting model labels using a {@link LabelDialogBox}.
   */
  public class LabelAddRemoveHandler implements ClickHandler {

    private DataManager data;
    private String type;

    /**
     * Creates a new {@link LabelAddRemoveHandler}.
     * 
     * @param data the DataManager object for the WMT session
     * @param type the event type, currently "Add" or "Delete"
     */
    public LabelAddRemoveHandler(DataManager data, String type) {
      this.data = data;
      this.type = type; // use sentence case
    }
    
    @Override
    public void onClick(ClickEvent event) {
      
      final LabelDialogBox box = new LabelDialogBox(data);
      
      box.getChoicePanel().getCancelButton().addClickHandler(
          new DialogCancelHandler(box));
      box.getChoicePanel().getOkButton().setHTML(Constants.FA_TAGS + type);
      box.getChoicePanel().getOkButton().addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          String label = box.getSuggestBox().getText();
          if (type.equalsIgnoreCase("add")) {
            if (data.modelLabels.containsKey(label)) {
              Window.alert(Constants.ADD_LABEL_ERR);
            } else {
              DataTransfer.addLabel(data, label);
            }
          } else if (type.equalsIgnoreCase("delete")) {
            LabelJSO jso = data.modelLabels.get(label);
            if (jso != null) {
              if (!data.security.getWmtUsername().matches(jso.getOwner())) {
                Window.alert(Constants.DELETE_LABEL_ERR);
              } else {
                if (jso.isSelected()) {
                  data.updateModelSaveState(false);
                }
                DataTransfer.deleteLabel(data, jso.getId());
              }
            }
          }
          box.hide();
        }
      });

      if (type.equalsIgnoreCase("add")) {
        box.showRelativeTo(addNewHtml);
      } else if (type.equalsIgnoreCase("delete")) {
        box.showRelativeTo(deleteHtml);
      }
      box.getSuggestBox().setFocus(true);
    }
  }
}
