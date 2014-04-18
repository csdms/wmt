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
import edu.colorado.csdms.wmt.client.ui.widgets.LabelDialogBox;

/**
 * Encapsulates a scrollable list of labels used to tag and classify models.
 * This menu is modeled on the "Labels" menu in Gmail.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LabelsMenu extends PopupPanel {

  private static final String MENU_WIDTH = "200px"; // arbitrary, aesthetic
  private static final String MENU_HEIGHT = "20em";
  
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
    scroller.setSize(MENU_WIDTH, MENU_HEIGHT);
    menu.add(scroller);

    // Populate the menu with the stored model labels and their values.
    populateMenu();

    // These items are always visible on the bottom of the menu.
    HTML separator = new HTML("");
    separator.setStyleName("wmt-PopupPanelSeparator");
    final HTML addNewHtml = new HTML("Add new label");
    addNewHtml.setStyleName("wmt-PopupPanelItem");
    final HTML deleteHtml = new HTML("Delete label");
    deleteHtml.setStyleName("wmt-PopupPanelItem");
    menu.add(separator);
    menu.add(addNewHtml);
    menu.add(deleteHtml);
    
    // Show a SuggestBox when the user adds or deletes a label.
    addNewHtml.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final LabelDialogBox box = new LabelDialogBox(LabelsMenu.this.data);
        box.getChoicePanel().getOkButton().setHTML(DataManager.FA_TAGS + "Add");
        box.getChoicePanel().getOkButton().addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            String label = box.getSuggestBox().getText();
            LabelsMenu.this.data.modelLabels.put(label, false);
            populateMenu();
            box.hide();
          }
        });
        box.getChoicePanel().getCancelButton().addClickHandler(
            new DialogCancelHandler(box));
        box.showRelativeTo(addNewHtml);
        box.getSuggestBox().setFocus(true);
      }
    });
    deleteHtml.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        final LabelDialogBox box = new LabelDialogBox(LabelsMenu.this.data);
        box.getChoicePanel().getOkButton().setHTML(DataManager.FA_TAGS + "Delete");
        box.getChoicePanel().getOkButton().addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            String label = box.getSuggestBox().getText();
            LabelsMenu.this.data.modelLabels.remove(label);
            populateMenu();
            box.hide();
          }
        });
        box.getChoicePanel().getCancelButton().addClickHandler(
            new DialogCancelHandler(box));
        box.showRelativeTo(deleteHtml);
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
