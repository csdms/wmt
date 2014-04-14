/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.MoreActionsMenu;

/**
 * Handles a click on the "More" button in the ActionButtonPanel, displaying the
 * {@link MoreActionsMenu}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ActionButtonPanelMoreHandler implements ClickHandler {

  private DataManager data;
  private MoreActionsMenu menu;
  
  /**
   * Creates a new instance of {@link ActionButtonPanelMoreHandler}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public ActionButtonPanelMoreHandler(DataManager data, MoreActionsMenu menu) {
    this.data = data;
    this.menu = menu;
  }
  
  @Override
  public void onClick(ClickEvent event) {
    menu.setPopupPositionAndShow(new PositionCallback() {
      final Integer x = menu.getMoreButton().getElement().getAbsoluteLeft();
      final Integer y = menu.getMoreButton().getElement().getAbsoluteBottom();
      @Override
      public void setPosition(int offsetWidth, int offsetHeight) {
        menu.setPopupPosition(x, y);
      }
    });
  }

}
