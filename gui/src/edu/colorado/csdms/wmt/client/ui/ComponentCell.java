package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class ComponentCell extends MenuBar {

  private static String[] COMPONENTS = {
      "Avulsion", "CEM", "HydroTrend", "SatZoneDarcyLayers"};
  private static String[] ACTIONS = {"Show parameters", "Get info", "Delete"};

  private MenuItem componentItem;
  private MenuBar actionMenu;

  public ComponentCell(String portId) {

    // Show this menu when selecting a component.
    MenuBar componentMenu = new MenuBar(true); // menu items stacked vertically
    for (int i = 0; i < COMPONENTS.length; i++) {
      componentMenu.addItem(COMPONENTS[i], new ComponentSelectionCommand(
          COMPONENTS[i]));
    }

    // Show this menu after a component has been selected.
    actionMenu = new MenuBar(true); // menu items stacked vertically
    for (int i = 0; i < ACTIONS.length; i++) {
      actionMenu.addItem(ACTIONS[i], new ComponentActionCommand(ACTIONS[i]));
    }

    componentItem = new MenuItem(portId, componentMenu);
    componentItem.setStyleName("mwmb-componentItem");
    this.addItem(componentItem);
  }

    /**
   * Replaces the generic "Component" string with the name of the selected
   * component from the menu. If the name of the component is too long, it's
   * trimmed to fit in the Grid cell. Also applies CSS rules to the
   * driverCell.
   */
  public class ComponentSelectionCommand implements Command {

    private final Integer TRIM = 12;
    private String componentName;
    private String displayName;

    public ComponentSelectionCommand(String componentName) {
      this.componentName = componentName;
      if (componentName.length() > TRIM) {
        this.displayName = componentName.substring(0, TRIM) + "\u2026";
      } else {
        this.displayName = componentName;
      }
    }

    @Override
    public void execute() {
      componentItem.setText(displayName);
//      driverCell.addStyleDependentName("connected");
      componentItem.setSubMenu(actionMenu);
//      addUsesPorts(componentName);
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
