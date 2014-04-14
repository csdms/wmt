/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.Widget;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.handler.ActionButtonPanelDeleteHandler;
import edu.colorado.csdms.wmt.client.ui.handler.ActionButtonPanelHelpHandler;
import edu.colorado.csdms.wmt.client.ui.handler.ActionButtonPanelOpenHandler;
import edu.colorado.csdms.wmt.client.ui.handler.ActionButtonPanelSaveHandler;
import edu.colorado.csdms.wmt.client.ui.handler.ActionButtonPanelStatusHandler;
import edu.colorado.csdms.wmt.client.ui.handler.SetupRunModelHandler;

/**
 * Makes a row of action buttons ("Open", "Save", "Run", etc.) for working with 
 * the model built in WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ActionButtonPanel extends HorizontalPanel {

  @SuppressWarnings("unused")
  private DataManager data;
  
  /**
   * Makes a new {@link ActionButtonPanel}.
   */
  public ActionButtonPanel(DataManager data) {

    this.data = data;
    this.setStyleName("wmt-ActionButtonPanel");

    // Open
    Button openButton = new Button(DataManager.FA_OPEN);
    openButton.setTitle("Open model");
    openButton.addClickHandler(new ActionButtonPanelOpenHandler(data));
    this.add(openButton);

    // Save
    Button saveButton = new Button(DataManager.FA_SAVE);
    saveButton.setTitle("Save model");
    saveButton.addClickHandler(new ActionButtonPanelSaveHandler(data));
    this.add(saveButton);

    // Save As
    Button saveAsButton =
        new Button(DataManager.FA_SAVE + "<i class='fa fa-caret-right'></i>");
    saveAsButton.setTitle("Save model as...");
    saveAsButton.addClickHandler(new ActionButtonPanelSaveHandler(data, true));    
    this.add(saveAsButton);

    // Delete
    Button deleteButton = new Button(DataManager.FA_DELETE);
    deleteButton.setTitle("Delete model");
    deleteButton.addClickHandler(new ActionButtonPanelDeleteHandler(data));
    this.add(deleteButton);

    // Run
    Button runButton = new Button(DataManager.FA_RUN);
    runButton.setTitle("Run model");
    runButton.addClickHandler(new SetupRunModelHandler(data));
    this.add(runButton);

    // Run status
    Button statusButton = new Button(DataManager.FA_STATUS);
    statusButton.setTitle("Status of model run");
    statusButton.addClickHandler(new ActionButtonPanelStatusHandler(data));    
    this.add(statusButton);

    // Help
    Button helpButton = new Button(DataManager.FA_HELP);
    helpButton.setTitle("Help on using WMT");
    helpButton.addClickHandler(new ActionButtonPanelHelpHandler());        
    this.add(helpButton);
    
    // More
    Button moreButton = new Button("More <i class='fa fa-caret-down'></i>");
    this.add(moreButton);
    final MoreActionsMenu moreMenu = new MoreActionsMenu(data, moreButton);
    moreButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        moreMenu.setPopupPositionAndShow(new PositionCallback() {
          final Integer x = moreMenu.getMoreButton().getElement()
              .getAbsoluteLeft();
          final Integer y = moreMenu.getMoreButton().getElement()
              .getAbsoluteBottom();
          @Override
          public void setPosition(int offsetWidth, int offsetHeight) {
            moreMenu.setPopupPosition(x, y);
          }
        });
      }
    });

    // Apply a style to each button.
    Iterator<Widget> iter = this.iterator();
    while (iter.hasNext()) {
      Button button = (Button) iter.next();
      button.setStyleName("wmt-ModelMenuPanelButton");
    }
  }
}
