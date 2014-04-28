/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.data.Constants;

/**
 * Shows a menu that allows a user to select to view the input files generated
 * by the current parameter settings, or the default settings for a component.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ViewInputFilesMenu extends PopupPanel {

  private DataManager data;
  private String componentId;
  private FileTypesMenu currentMenu;
  private FileTypesMenu defaultsMenu;
  
  /**
   * Creates a new {@link ViewInputFilesMenu}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public ViewInputFilesMenu(DataManager data, String componentId) {

    super(true); // autohide
    this.data = data;
    this.componentId = componentId;
    this.setStyleName("wmt-PopupPanel");

    // A VerticalPanel for the menu items. (PopupPanels have only one child.)
    VerticalPanel menu = new VerticalPanel();
    this.add(menu);

    // Current model
    final HTML currentButton = new HTML(Constants.FA_COGS + "Current model");
    currentButton.setTitle(Constants.PARAMETER_VIEW_CURRENT);
    menu.add(currentButton);
    currentMenu = new FileTypesMenu(this.data, this.componentId, false);
    currentButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        currentMenu.setPopupPositionAndShow(new PositionCallback() {
          final Integer x = currentButton.getElement().getAbsoluteRight();
          final Integer y = currentButton.getAbsoluteTop();
          @Override
          public void setPosition(int offsetWidth, int offsetHeight) {
            currentMenu.setPopupPosition(x, y);
          }
        });
      }
    });

    // Defaults for component
    final HTML defaultsButton =
        new HTML(Constants.FA_COG + "Defaults for component");
    defaultsButton.setTitle(Constants.PARAMETER_VIEW_DEFAULT);
    menu.add(defaultsButton);
    defaultsMenu = new FileTypesMenu(this.data, this.componentId, true);
    defaultsButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        defaultsMenu.setPopupPositionAndShow(new PositionCallback() {
          final Integer x = defaultsButton.getElement().getAbsoluteRight();
          final Integer y = defaultsButton.getAbsoluteTop();
          @Override
          public void setPosition(int offsetWidth, int offsetHeight) {
            defaultsMenu.setPopupPosition(x, y);
          }
        });
      }
    });

    // Apply a style to each button.
    Iterator<Widget> iter = menu.iterator();
    while (iter.hasNext()) {
      HTML button = (HTML) iter.next();
      button.setStyleName("wmt-PopupPanelItem");
    }
  }
}
