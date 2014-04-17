/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.Iterator;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.colorado.csdms.wmt.client.control.DataManager;

/**
 * Encapsulates a scrollable list of labels used to tag and classify models.
 * This menu is modeled on the "Labels" menu in Gmail.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LabelsMenu extends PopupPanel {

  private static final String MENU_WIDTH = "200px"; // arbitrary, aesthetic
  
  @SuppressWarnings("unused")
  private DataManager data;
  
  /**
   * Makes a new {@link LabelsMenu}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public LabelsMenu(DataManager data) {

    super(true); // autohide
    this.getElement().getStyle().setCursor(Cursor.POINTER); // use pointer
    this.data = data;
    this.setStyleName("wmt-PopupPanel");

    // A VerticalPanel for the menu items. (PopupPanels have only one child.)
    VerticalPanel menu = new VerticalPanel();
    this.add(menu);
    
    // All labels are listed on the labelPanel, which sits on a ScrollPanel.
    VerticalPanel labelPanel = new VerticalPanel();
    ScrollPanel scroller = new ScrollPanel(labelPanel);
    scroller.setWidth(MENU_WIDTH);
    menu.add(scroller);
    
    // XXX Ten temporary labels.
    CheckBox label0 = new CheckBox("low avulsion");
    label0.setValue(true);
    labelPanel.add(label0);
    CheckBox label9 = new CheckBox("high waves");
    labelPanel.add(label9);
    CheckBox label1 = new CheckBox("Ganges");
    labelPanel.add(label1);
    CheckBox label2 = new CheckBox("thesis");
    labelPanel.add(label2);
    CheckBox label3 = new CheckBox("AGU talk");
    labelPanel.add(label3);
    CheckBox label4 = new CheckBox("2013");
    labelPanel.add(label4);
    CheckBox label5 = new CheckBox("Nature paper");
    label5.setValue(true);
    labelPanel.add(label5);
    CheckBox label6 = new CheckBox("CEM");
    label6.setValue(true);
    labelPanel.add(label6);
    CheckBox label7 = new CheckBox("What happens if someone makes a really long label?");
    labelPanel.add(label7);
    CheckBox label8 = new CheckBox("HydroTrend");
    labelPanel.add(label8);
    
    // Apply a style to each label.
    Iterator<Widget> iter = labelPanel.iterator();
    while (iter.hasNext()) {
      CheckBox button = (CheckBox) iter.next();
      button.setWordWrap(false);
      button.setStyleName("wmt-PopupPanelCheckBoxItem");
    }
    
    // These items are always visible on the bottom of the menu.
    HTML separator = new HTML("");
    separator.setStyleName("wmt-PopupPanelSeparator");
    HTML addNewHtml = new HTML("Add new label");
    addNewHtml.setStyleName("wmt-PopupPanelItem");
    HTML manageHtml = new HTML("Manage labels");
    manageHtml.setStyleName("wmt-PopupPanelItem");
    menu.add(separator);
    menu.add(addNewHtml);
    menu.add(manageHtml);
  }
}
