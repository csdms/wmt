/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import edu.colorado.csdms.wmt.client.control.DataManager;

/**
 * Displays a model component in the ModelGrid.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentCell extends MenuBar {

  private static String[] ACTIONS = {"Show parameters", "Get info", "Delete"};
  private static Integer TRIM = 12; // the number of characters to display

  private DataManager data;
  private MenuBar componentMenu;
  private MenuItem componentItem;
  private MenuBar actionMenu;

  /**
   * Creates a new {@link ComponentCell}.
   * 
   * @param dat the DataManager object for the WMT session
   * @param displayName the text to display in the cell
   */
  public ComponentCell(DataManager data, String displayName) {

    this.data = data;
    
    // Show this menu when selecting a component.
    componentMenu = new MenuBar(true); // menu items stacked vertically
    updateComponents();

    // Show this menu after a component has been selected.
    actionMenu = new MenuBar(true); 
    updateActions();

    componentItem = new MenuItem(trimName(displayName), componentMenu);
    componentItem.setStyleName("mwmb-componentItem");
    this.addItem(componentItem);
  }

  /**
   * Loads the names of the available components into the {@link ComponentCell}
   * menu.
   */
  public void updateComponents() {
    componentMenu.clearItems();
    for (int i = 0; i < data.getComponents().size(); i++) {
      componentMenu.addItem(data.getComponent(i).getName(),
          new ComponentSelectionCommand(data.getComponent(i).getId()));
    }
  }
  
  /**
   * Loads the names of the actions that can be performed on a component into
   * the {@link ComponentCell} menu.
   */
  public void updateActions() {
    actionMenu.clearItems();
    for (int i = 0; i < ACTIONS.length; i++) {
      actionMenu.addItem(ACTIONS[i], new ComponentActionCommand(ACTIONS[i]));
    }
  }
  
  /**
   * A worker that trims the name displayed in the {@link ComponentCell} if it's
   * too long.
   * 
   * @param name the name to display
   * @return the trimmed name
   */
  private String trimName(String name) {
    String trimmedName;
    if (name.length() > TRIM) {
      trimmedName = name.substring(0, TRIM) + "\u2026";
    } else {
      trimmedName = name;
    }
    return trimmedName;
  }
  
  /**
   * Replaces the generic display name with the name of the selected component
   * from the menu. If the name of the component is too long, it's trimmed to
   * fit in the Grid cell. Applies CSS rules to the driverCell.
   */
  public class ComponentSelectionCommand implements Command {

    private String componentId;
    private String displayName;

    public ComponentSelectionCommand(String componentId) {
      this.componentId = componentId;
      String componentName = data.getComponent(componentId).getName();
      this.displayName = trimName(componentName);
    }

    @Override
    public void execute() {
      componentItem.setText(displayName);
      componentItem.addStyleDependentName("connected");
      componentItem.setSubMenu(actionMenu);
      data.getPerspective().getModelGrid().isDriverConnected(true);
      data.getPerspective().getModelGrid().addUsesPorts(componentId);
    }
  }

  /**
   * Performs the actions listed in the actionMenu.
   */
  public class ComponentActionCommand implements Command {

    private String action;

    public ComponentActionCommand(String action) {
      this.action = action;
    }

    @Override
    public void execute() {
      GWT.log("Action performed: " + action);
    }
  }

}
