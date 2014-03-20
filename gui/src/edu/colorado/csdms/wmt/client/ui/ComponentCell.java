package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import edu.colorado.csdms.wmt.client.control.DataManager;

public class ComponentCell extends MenuBar {

  private static String[] ACTIONS = {"Show parameters", "Get info", "Delete"};

  private DataManager data;
  private MenuBar componentMenu;
  private MenuItem componentItem;
  private MenuBar actionMenu;

  public ComponentCell(DataManager data, String displayName) {

    this.data = data;
    
    // Show this menu when selecting a component.
    componentMenu = new MenuBar(true); // menu items stacked vertically
    updateComponents();

    // Show this menu after a component has been selected.
    actionMenu = new MenuBar(true); 
    updateActions();

    componentItem = new MenuItem(displayName, componentMenu);
    componentItem.setStyleName("mwmb-componentItem");
    this.addItem(componentItem);
  }

  /**
   * A worker that loads the names of the available components into the
   * componentMenu.
   */
  public void updateComponents() {
    componentMenu.clearItems();
    for (int i = 0; i < data.getComponents().size(); i++) {
      componentMenu.addItem(data.getComponent(i).getName(),
          new ComponentSelectionCommand(data.getComponent(i).getId()));
    }
  }
  
  /**
   * A worker that loads the names of the actions that can be performed on a 
   * component.
   */
  public void updateActions() {
    actionMenu.clearItems();
    for (int i = 0; i < ACTIONS.length; i++) {
      actionMenu.addItem(ACTIONS[i], new ComponentActionCommand(ACTIONS[i]));
    }
  }
  
  /**
   * Replaces the generic "Component" string with the name of the selected
   * component from the menu. If the name of the component is too long, it's
   * trimmed to fit in the Grid cell. Also applies CSS rules to the driverCell.
   */
  public class ComponentSelectionCommand implements Command {

    private final Integer TRIM = 12; // the number of characters to display
    private String componentId;
    private String displayName;

    public ComponentSelectionCommand(String componentId) {
      this.componentId = componentId;
      String componentName = data.getComponent(componentId).getName();
      if (componentName.length() > TRIM) {
        this.displayName = componentName.substring(0, TRIM) + "\u2026";
      } else {
        this.displayName = componentName;
      }
    }

    @Override
    public void execute() {
      componentItem.setText(displayName);
      componentItem.setSubMenu(actionMenu);
      data.getPerspective().getModelGrid().isDriverConnected(true);
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
