/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.control.DataManager;

/**
 * Encapsulates a menu of secondary actions on models -- "Save as", "Delete",
 * "Run status", etc.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class MoreActionsMenu extends PopupPanel {

  private DataManager data;
  private Button moreButton;
  
  /**
   * Makes a new {@link MoreActionsMenu}.
   * 
   * @param data the DataManager object for the WMT session
   * @param moreButton the "More" button to which this menu is attached
   */
  public MoreActionsMenu(DataManager data, Button moreButton) {

    super(true); // autohide
    this.getElement().getStyle().setCursor(Cursor.POINTER); // use pointer
    this.data = data;
    this.setMoreButton(moreButton);
    this.setStyleName("wmt-MoreActionsMenu");
    
    // A VerticalPanel for the menu items. (PopupPanels can have only one child.)
    VerticalPanel menu = new VerticalPanel();
    this.add(menu);
    
    HTML openButton = new HTML("<i class='fa fa-folder-open-o fa-fw'></i> Open...");
    HTML saveButton = new HTML("<i class='fa fa-floppy-o fa-fw'></i> Save");
    HTML helpButton = new HTML("Help");
    
    openButton.setStyleName("wmt-MoreActionsMenuItem");
    saveButton.setStyleName("wmt-MoreActionsMenuItem");
    helpButton.setStyleName("wmt-MoreActionsMenuItem");    
    
    menu.add(openButton);
    menu.add(saveButton);
    menu.add(helpButton);

  }

  public Button getMoreButton() {
    return moreButton;
  }

  public void setMoreButton(Button moreButton) {
    this.moreButton = moreButton;
  }
}
