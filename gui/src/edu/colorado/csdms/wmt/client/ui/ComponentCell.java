/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.data.Constants;

/**
 * A container for displaying a model component in the {@link ModelTree}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentCell extends VerticalPanel implements ClickHandler {

  private DataManager data;
  private String portId;
  private String componentId;
  private HTML nameCell;
  private HTML menuCell;
  private PopupPanel componentMenu;
  private TreeItem enclosingTreeItem;
  private Boolean isLinked = false;

  /**
   * Creates a new {@link ComponentCell} displaying the text "driver".
   * 
   * @param data the DataManager object for the WMT session
   */
  public ComponentCell(DataManager data) {
    this(data, Constants.DRIVER);
  }

  /**
   * Creates a new {@link ComponentCell} displaying a
   * {@link ComponentSelectionMenu} with a list of available components.
   * 
   * @param data the DataManager object for the WMT session
   * @param portId the id of the corresponding port for the cell
   */
  public ComponentCell(DataManager data, String portId) {

    this.data = data;
    this.portId = portId;
    this.setVerticalAlignment(ALIGN_MIDDLE); // must set before adding children
    this.setStyleName("wmt-ComponentCell");
    this.addDomHandler(this, ClickEvent.getType());
    
    // The ComponentCell consists of a nameCell and a menuCell in a Grid.
    nameCell = new HTML(trimName(portId));
    nameCell.setStyleName("wmt-ComponentCell-NameCell");
    menuCell = new HTML(Constants.FA_SELECT);
    menuCell.setStyleName("wmt-ComponentCell-MenuCell");
    Grid grid = new Grid(1, 2); // one row, two cols
    this.add(grid);
    grid.setWidget(0, 0, nameCell);
    grid.setWidget(0, 1, menuCell);

    // The componentMenu is displayed on a click of the ComponentCell.
    componentMenu = new ComponentSelectionMenu(this.data, this);

    String tooltip = "Click to select a component";
    if (portId.matches(Constants.DRIVER)) {
      tooltip += " to be the driver for the model.";
    } else {
      tooltip += " to fill this \"" + portId + "\" port.";
    }
    data.setTooltip(this, tooltip);
  }

  /**
   * Displays the componentMenu directly beneath the ComponentCell.
   */
  @Override
  public void onClick(ClickEvent event) {
    componentMenu.setPopupPositionAndShow(new PositionCallback() {
      final Integer x = ComponentCell.this.getElement().getAbsoluteLeft();
      final Integer y = ComponentCell.this.getElement().getAbsoluteBottom();
      @Override
      public void setPosition(int offsetWidth, int offsetHeight) {
        componentMenu.setPopupPosition(x, y);
      }
    });
  }

  public String getPortId() {
    return portId;
  }

  public void setPortId(String portId) {
    this.portId = portId;
  }

  public String getComponentId() {
    return componentId;
  }

  public void setComponentId(String componentId) {
    this.componentId = componentId;
  }

  public HTML getNameCell() {
    return nameCell;
  }

  public void setNameCell(HTML nameCell) {
    this.nameCell = nameCell;
  }

  public HTML getMenuCell() {
    return menuCell;
  }

  public void setMenuCell(HTML menuCell) {
    this.menuCell = menuCell;
  }

  public PopupPanel getComponentMenu() {
    return componentMenu;
  }

  public void setComponentMenu(PopupPanel componentMenu) {
    this.componentMenu = componentMenu;
  }

  public TreeItem getEnclosingTreeItem() {
    return enclosingTreeItem;
  }

  public void setEnclosingTreeItem(TreeItem enclosingTreeItem) {
    this.enclosingTreeItem = enclosingTreeItem;
  }

  public Boolean isLinked() {
    return isLinked;
  }

  public void isLinked(Boolean isLinked) {
    this.isLinked = isLinked;
    if (this.isLinked && (this.getTitle() != null)) {
      String tooltip =
          " This component is aliased from another instance of "
              + data.getModelComponent(this.componentId).getName() + ".";
      this.setTitle(this.getTitle() + tooltip);
    }
  }

  /**
   * A worker that trims the name displayed in the {@link ComponentCell} if it's
   * too long.
   * 
   * @param name the name to display
   * @return the trimmed name
   */
  public String trimName(String name) {
    String trimmedName;
    if (name.length() > Constants.TRIM) {
      trimmedName = name.substring(0, Constants.TRIM) + Constants.ELLIPSIS;
    } else {
      trimmedName = name;
    }
    return trimmedName;
  }
}
