/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TreeItem;

import edu.colorado.csdms.wmt.client.control.DataManager;

/**
 * Displays a model component in the ModelTree.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentCell extends MenuBar {

  private static String[] ACTIONS = {"Show parameters", "Get info", "Delete"};
  private static String DRIVER_TEXT = "component";
  private static String ALL_COMPONENTS = "__all_components";
  private static Integer TRIM = 12; // the number of characters to display

  private DataManager data;
  private MenuBar componentMenu;
  private MenuItem componentItem;
  private MenuBar actionMenu;
  private TreeItem enclosingTreeItem;

  /**
   * Creates a new {@link ComponentCell} displaying the text "component".
   * 
   * @param data the DataManager object for the WMT session
   */
  public ComponentCell(DataManager data) {
    this(data, DRIVER_TEXT);
  }
  
  /**
   * Creates a new {@link ComponentCell}.
   * 
   * @param data the DataManager object for the WMT session
   * @param portId the id of the corresponding port for the cell
   */
  public ComponentCell(DataManager data, String portId) {

    this.data = data;

    // Show this menu when selecting a component.
    componentMenu = new MenuBar(true); // menu items stacked vertically
    updateComponents(portId);

    // Show this menu after a component has been selected.
    actionMenu = new MenuBar(true);
    updateActions();

    componentItem = new MenuItem(trimName(portId), componentMenu);
    componentItem.setStyleName("mwmb-componentItem");
    this.addItem(componentItem);
  }

  /**
   * Loads the names of the components that match the uses port of the displayed
   * component into the {@link ComponentCell} menu.
   * 
   * @param portId the id of the uses port displayed in the ComponentCell
   */
  public void updateComponents(String portId) {

    componentMenu.clearItems();

    // Display a wait message in the componentMenu.
    if (portId.matches(DRIVER_TEXT)) {
      componentMenu.addItem("Loading...", new Command() {
        @Override
        public void execute() {
          // Do nothing.
        }
      });
      return;
    }

    // Load all available components into the componentMenu!
    if (portId.matches(ALL_COMPONENTS)) {
      for (int i = 0; i < data.getComponents().size(); i++) {
        componentMenu.addItem(data.getComponent(i).getName(),
            new ComponentSelectionCommand(data.getComponent(i).getId()));
      }
      return;
    }

    // Load only those components with provides ports matching the input portId.
    for (int i = 0; i < data.getComponents().size(); i++) {
      Integer nProvidesPorts = data.getComponent(i).getProvidesPorts().length();
      for (int j = 0; j < nProvidesPorts; j++) {
        String providesId =
            data.getComponent(i).getProvidesPorts().get(j).getId();
        if (providesId.matches(portId)) {
          componentMenu.addItem(data.getComponent(i).getName(),
              new ComponentSelectionCommand(data.getComponent(i).getId()));
        }
      }
    }
  }
  
  /**
   * Loads the names of all available components into the {@link ComponentCell}
   * menu.
   */
  public void updateComponents() {
    updateComponents(ALL_COMPONENTS);
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
  
  public TreeItem getEnclosingTreeItem() {
    return enclosingTreeItem;
  }

  public void setEnclosingTreeItem(TreeItem enclosingTreeItem) {
    this.enclosingTreeItem = enclosingTreeItem;
  }

  /**
   * Replaces the generic display name with the name of the selected component
   * from the menu. If the name of the component is too long, it's trimmed to
   * fit in the {@link ComponentCell}.
   */
  public class ComponentSelectionCommand implements Command {

    private String componentId;
    private String displayName;

    public ComponentSelectionCommand(String componentId) {
      this.componentId = componentId;
      String componentName = data.getComponent(this.componentId).getName();
      this.displayName = trimName(componentName);
    }

    @Override
    public void execute() {
      componentItem.setText(displayName);
      componentItem.addStyleDependentName("connected");
      componentItem.setSubMenu(actionMenu);
      data.getPerspective().getModelTree().addComponent(componentId,
          enclosingTreeItem);
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
