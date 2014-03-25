/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.TreeItem;

import edu.colorado.csdms.wmt.client.control.DataManager;

/**
 * A container for displaying a model component in the {@link ModelTree}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentCell extends MenuBar {

  private static String DRIVER = "driver";
  private static Integer TRIM = 12; // the number of characters to display

  private DataManager data;
  private String portId;
  private String componentId;
  private ComponentSelectionMenu componentMenu;
  private TreeItem enclosingTreeItem;

  /**
   * Creates a new {@link ComponentCell} displaying the text "driver".
   * 
   * @param data the DataManager object for the WMT session
   */
  public ComponentCell(DataManager data) {
    this(data, DRIVER);
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
    this.componentMenu = new ComponentSelectionMenu(this.data, this);
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
