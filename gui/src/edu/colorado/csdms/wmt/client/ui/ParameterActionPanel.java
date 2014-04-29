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
import edu.colorado.csdms.wmt.client.data.Constants;
import edu.colorado.csdms.wmt.client.ui.handler.ParameterActionPanelResetHandler;

/**
 * Makes a row of action buttons ("Reset", "View input files", etc.) for working
 * with the parameters of a model component in WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ParameterActionPanel extends HorizontalPanel {

  private DataManager data;
  private String componentId;
  private ViewInputFilesMenu inputFilesMenu;
  
  /**
   * Makes a new {@link ParameterActionPanel}.
   * 
   * @param data the DataManager instance for the WMT session
   */
  public ParameterActionPanel(DataManager data, String componentId) {

    this.data = data;
    this.componentId = componentId;
    this.setStyleName("wmt-ActionPanel");

    // Reset
    Button resetButton = new Button("<i class='fa fa-bolt'></i>");
    resetButton.setTitle(Constants.PARAMETER_RESET);
    resetButton.addClickHandler(new ParameterActionPanelResetHandler(this.data,
        this.componentId));
    this.add(resetButton);

    // View input files
    final Button viewFilesButton =
        new Button("<i class='fa fa-cloud-download'></i>");
    viewFilesButton.setTitle(Constants.PARAMETER_VIEW_FILE);
    this.add(viewFilesButton);
    inputFilesMenu = new ViewInputFilesMenu(this.data, this.componentId);
    viewFilesButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        inputFilesMenu.populateMenu();
        inputFilesMenu.setPopupPositionAndShow(new PositionCallback() {
          final Integer x = viewFilesButton.getElement().getAbsoluteLeft();
          final Integer y = viewFilesButton.getElement().getAbsoluteBottom();

          @Override
          public void setPosition(int offsetWidth, int offsetHeight) {
            inputFilesMenu.setPopupPosition(x, y);
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
}
