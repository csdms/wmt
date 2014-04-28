/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.Iterator;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.data.Constants;
import edu.colorado.csdms.wmt.client.ui.handler.ModelActionPanelDeleteHandler;
import edu.colorado.csdms.wmt.client.ui.handler.ModelActionPanelHelpHandler;
import edu.colorado.csdms.wmt.client.ui.handler.ModelActionPanelSaveHandler;
import edu.colorado.csdms.wmt.client.ui.handler.ModelActionPanelStatusHandler;

/**
 * Encapsulates a menu of secondary actions on models -- "Save as", "Delete",
 * "Run status", etc.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class MoreActionsMenu extends PopupPanel {

  @SuppressWarnings("unused")
  private DataManager data;
  private LabelsMenu labelsMenu;
  private ComponentsMenu componentsMenu;
  
  /**
   * Makes a new {@link MoreActionsMenu}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public MoreActionsMenu(DataManager data) {

    super(true); // autohide
    this.getElement().getStyle().setCursor(Cursor.POINTER); // use pointer
    this.data = data;
    this.setStyleName("wmt-PopupPanel");

    // A VerticalPanel for the menu items. (PopupPanels have only one child.)
    VerticalPanel menu = new VerticalPanel();
    this.add(menu);

    // Save as
    HTML saveAsButton = new HTML(Constants.FA_SAVE + "Save model as...");
    saveAsButton.setTitle(Constants.MODEL_SAVE_AS);
    saveAsButton.addClickHandler(new ModelActionPanelSaveHandler(data, true));
    menu.add(saveAsButton);

    // Delete
    HTML deleteButton = new HTML(Constants.FA_DELETE + "Delete model...");
    deleteButton.setTitle(Constants.MODEL_DELETE);
    deleteButton.addClickHandler(new ModelActionPanelDeleteHandler(data));
    menu.add(deleteButton);

    // Manage labels
    final HTML labelsButton = new HTML(Constants.FA_TAGS + "Manage labels");
    labelsButton.setTitle(Constants.MODEL_LABELS);
    menu.add(labelsButton);
    labelsMenu = new LabelsMenu(data);
    labelsButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        labelsMenu.populateMenu();
        labelsMenu.setPopupPositionAndShow(new PositionCallback() {
          final Integer x = labelsButton.getElement().getAbsoluteRight();
          final Integer y = labelsButton.getAbsoluteTop();

          @Override
          public void setPosition(int offsetWidth, int offsetHeight) {
            labelsMenu.setPopupPosition(x, y);
          }
        });
      }
    });

    // Component information
    final HTML componentsButton =
        new HTML(Constants.FA_COG + "Component information");
    componentsButton.setTitle(Constants.COMPONENT_INFO);
    menu.add(componentsButton);
    componentsMenu = new ComponentsMenu(data);
    componentsButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        componentsMenu.populateMenu();
        componentsMenu.setPopupPositionAndShow(new PositionCallback() {
          final Integer x = componentsButton.getElement().getAbsoluteRight();
          final Integer y = componentsButton.getAbsoluteTop();
          @Override
          public void setPosition(int offsetWidth, int offsetHeight) {
            componentsMenu.setPopupPosition(x, y);
          }
        });
      }
    });

    // Run status
    HTML statusButton = new HTML(Constants.FA_STATUS + "View run status...");
    statusButton.setTitle(Constants.MODEL_RUN_STATUS);
    statusButton.addClickHandler(new ModelActionPanelStatusHandler(data));
    menu.add(statusButton);

    // Help
    HTML helpButton = new HTML(Constants.FA_HELP + "Help");
    helpButton.setTitle(Constants.MODEL_HELP);
    helpButton.addClickHandler(new ModelActionPanelHelpHandler(data));
    menu.add(helpButton);

    // Apply a style to each button.
    Iterator<Widget> iter = menu.iterator();
    while (iter.hasNext()) {
      HTML button = (HTML) iter.next();
      button.setStyleName("wmt-PopupPanelItem");
    }
  }
}
