/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.Map;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.handler.DialogCancelHandler;
import edu.colorado.csdms.wmt.client.ui.widgets.AddLabelDialogBox;

/**
 * Encapsulates a scrollable list of labels used to tag and classify models.
 * This menu is modeled on the "Labels" menu in Gmail.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LabelsMenu extends PopupPanel {

  private static final String MENU_WIDTH = "200px"; // arbitrary, aesthetic
  
  private DataManager data;
  private VerticalPanel labelPanel;
  
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
    labelPanel = new VerticalPanel();
    ScrollPanel scroller = new ScrollPanel(labelPanel);
    scroller.setWidth(MENU_WIDTH);
    menu.add(scroller);

    // Populate the menu with the stored model labels and their values.
    populateMenu();

    // These items are always visible on the bottom of the menu.
    HTML separator = new HTML("");
    separator.setStyleName("wmt-PopupPanelSeparator");
    HTML addNewHtml = new HTML("Add new label");
    addNewHtml.setStyleName("wmt-PopupPanelItem");
    HTML deleteHtml = new HTML("Delete label");
    deleteHtml.setStyleName("wmt-PopupPanelItem");
    menu.add(separator);
    menu.add(addNewHtml);
    menu.add(deleteHtml);
    
    // Show a SuggestBox when the user adds a new label.
    addNewHtml.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final AddLabelDialogBox box = new AddLabelDialogBox(LabelsMenu.this.data);
        box.getChoicePanel().getOkButton().addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            String newLabel = box.getSuggestBox().getText();
            LabelsMenu.this.data.modelLabels.put(newLabel, false);
            populateMenu();
            box.hide();
          }
        });
        box.getChoicePanel().getCancelButton().addClickHandler(
            new DialogCancelHandler(box));
        box.showRelativeTo(labelPanel);
        box.getSuggestBox().setFocus(true);
      }
    });
  }
  
  /**
   * A helper that loads the menu with labels.
   */
  public void populateMenu() {
    labelPanel.clear();
    for (Map.Entry<String, Boolean> entry : data.modelLabels.entrySet()) {
      CheckBox labelBox = new CheckBox(entry.getKey());
      labelBox.setValue(entry.getValue());
      labelBox.setWordWrap(false);
      labelBox.setStyleName("wmt-PopupPanelCheckBoxItem");
      labelPanel.add(labelBox);
    }
  }
}
