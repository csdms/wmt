/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.control.DataManager;

/**
 * A container for displaying a model component in the {@link ModelTree}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentCell extends VerticalPanel {

  private static Integer TRIM = 10; // the number of characters to display

  private DataManager data;
  private String portId;
  private String componentId;
  private HTML nameCell;
  private MenuBar menuCell;
  private MenuItem menuItem;
  private ComponentSelectionMenu componentMenu;
  private TreeItem enclosingTreeItem;
  private Boolean isLinked = false;

  /**
   * Creates a new {@link ComponentCell} displaying the text "driver".
   * 
   * @param data the DataManager object for the WMT session
   */
  public ComponentCell(DataManager data) {
    this(data, DataManager.DRIVER);
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
    
    // The ComponentCell consists of a nameCell and a menuCell in a Grid.
    nameCell = new HTML(trimName(portId));
    menuCell = new MenuBar();
    Grid grid = new Grid(1, 2); // one row, two cols
    this.add(grid);
    grid.setWidget(0, 0, nameCell);
    grid.setWidget(0, 1, menuCell);
    
    // The menuCell has one item that shows a list of components when selected.
    componentMenu = new ComponentSelectionMenu(this.data, this);
    menuItem =
        new MenuItem("<i class='fa fa-chevron-down'></i>", true, componentMenu);
    menuCell.addItem(menuItem);

    // Styles. Note rule is set on menuItem, not menuCell.
    this.setStyleName("wmt-ComponentCell");
    nameCell.setStyleName("wmt-ComponentCell-NameCell");
    menuItem.setStyleName("wmt-ComponentCell-MenuCell");
    
    String tooltip = "Click to select a component";
    if (portId.matches(DataManager.DRIVER)) {
      tooltip += " to be the driver for the model.";
    } else {
      tooltip += " to fill this \"" + portId + "\" port.";
    }
    menuCell.setTitle(tooltip);
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

  public MenuBar getMenuCell() {
    return menuCell;
  }

  public void setMenuCell(MenuBar menuCell) {
    this.menuCell = menuCell;
  }

  public MenuItem getMenuItem() {
    return menuItem;
  }

  public void setMenuItem(MenuItem menuItem) {
    this.menuItem = menuItem;
  }

  public ComponentSelectionMenu getComponentMenu() {
    return componentMenu;
  }

  public void setComponentMenu(ComponentSelectionMenu componentMenu) {
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
    if (name.length() > TRIM) {
      trimmedName = name.substring(0, TRIM) + "\u2026";
    } else {
      trimmedName = name;
    }
    return trimmedName;
  }
}
