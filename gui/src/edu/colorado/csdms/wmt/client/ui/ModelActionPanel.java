/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.Iterator;

import com.github.gwtbootstrap.client.ui.Tooltip;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.Widget;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.data.Constants;
import edu.colorado.csdms.wmt.client.ui.handler.ModelActionPanelOpenHandler;
import edu.colorado.csdms.wmt.client.ui.handler.ModelActionPanelSaveHandler;
import edu.colorado.csdms.wmt.client.ui.handler.SetupRunModelHandler;

/**
 * Makes a row of action buttons ("Open", "Save", "Run", etc.) for working with 
 * the model built in WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelActionPanel extends HorizontalPanel {

  @SuppressWarnings("unused")
  private DataManager data;
  private MoreActionsMenu moreMenu;
  
  /**
   * Makes a new {@link ModelActionPanel}.
   * 
   * @param data the DataManager instance for the WMT session
   */
  public ModelActionPanel(DataManager data) {

    this.data = data;
    this.setStyleName("wmt-ActionPanel");

    // Open
    Button openButton = new Button(Constants.FA_OPEN);
    Tooltip openButtonTooltip = data.setTooltip(openButton, Constants.MODEL_OPEN, "bottom");
    openButton.addClickHandler(new ModelActionPanelOpenHandler(data, openButtonTooltip));
    this.add(openButton);

    // Save
    Button saveButton = new Button(Constants.FA_SAVE);
    data.setTooltip(saveButton, Constants.MODEL_SAVE, "bottom");
    saveButton.addClickHandler(new ModelActionPanelSaveHandler(data));
    this.add(saveButton);

    // Run
    Button runButton = new Button(Constants.FA_RUN);
    data.setTooltip(runButton, Constants.MODEL_RUN, "bottom");
    runButton.addClickHandler(new SetupRunModelHandler(data));
    this.add(runButton);

    // More
    final Button moreButton = new Button("More" + Constants.FA_MORE);
    data.setTooltip(moreButton, Constants.MODEL_MORE, "right");
    this.add(moreButton);
    moreMenu = new MoreActionsMenu(data);
    moreButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        moreMenu.setPopupPositionAndShow(new PositionCallback() {
          final Integer x = moreButton.getElement().getAbsoluteLeft();
          final Integer y = moreButton.getElement().getAbsoluteBottom();
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
      button.setStyleName("wmt-ActionPanelButton");
    }
  }

  public MoreActionsMenu getMoreMenu() {
    return moreMenu;
  }

  public void setMoreMenu(MoreActionsMenu moreMenu) {
    this.moreMenu = moreMenu;
  }
}
