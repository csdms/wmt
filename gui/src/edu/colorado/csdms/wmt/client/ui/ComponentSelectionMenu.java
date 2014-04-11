/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.handler.ComponentSelectionCommand;

/**
 * A menu that shows a list of components. This is the initial menu displayed in
 * a {@link ComponentCell}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentSelectionMenu extends MenuBar {

  private static String ALL_COMPONENTS = "__all_components";

  private DataManager data;
  private ComponentCell cell;
  private MenuItem componentItem;

  /**
   * Makes a new {@link ComponentSelectionMenu}, which shows a list of
   * components; the user chooses one to populate the {@link ComponentCell}.
   * 
   * @param data the DataManager object for the WMT session
   * @param cell the {@link ComponentCell} this menu depends on
   */
  public ComponentSelectionMenu(DataManager data, ComponentCell cell) {
    super(true); // vertical
    this.data = data;
    this.cell = cell;
    updateComponents(cell.getPortId());
  }

  /**
   * A worker for adding a new MenuItem to the {@link ComponentSelectionMenu}.
   * 
   * @param componentId the id of the component to add to the menu
   */
  private void addComponentMenuItem(String componentId) {
    MenuItem item =
        new MenuItem(data.getComponent(componentId).getName(), true,
            new ComponentSelectionCommand(data, cell, data.getComponent(
                componentId).getId()));
    item.setStyleName("wmt-ComponentSelectionMenuItem");
    this.addItem(item);
  }

  /**
   * Loads the names of the components that match the uses port of the displayed
   * component into the {@link ComponentCell} menu.
   * 
   * @param portId the id of the uses port displayed in the ComponentCell
   */
  public void updateComponents(String portId) {

    this.clearItems();

    // Display a wait message in the componentMenu.
    if (portId.matches(DataManager.DRIVER)) {
      this.addItem("Loading...", new NullCommand());
      return;
    }

    // Load all available components into the componentMenu!
    if (portId.matches(ALL_COMPONENTS)) {
      for (int i = 0; i < data.componentIdList.size(); i++) {
        String componentId = data.componentIdList.get(i);
        addComponentMenuItem(componentId);
      }
      return;
    }

    // Load only those components with provides ports matching the input portId.
    for (int i = 0; i < data.componentIdList.size(); i++) {
      String componentId = data.componentIdList.get(i);
      Integer nProvidesPorts =
          data.getComponent(componentId).getProvidesPorts().length();
      for (int j = 0; j < nProvidesPorts; j++) {
        if (data.getComponent(componentId).getProvidesPorts().get(j).getId()
            .matches(portId)) {
          addComponentMenuItem(componentId);
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
   * This method builds the initial list of MenuItems in the driver
   * {@link ComponentCell}. This list functions as a placeholder. Only the ids
   * of the components are displayed, and there is no command associated with
   * the MenuItems. These MenuItems are replaced with fully functional MenuItems
   * when their associated component is successfully loaded from the server.
   */
  public void initializeComponents() {
    this.clearItems();
    for (int i = 0; i < data.componentIdList.size(); i++) {
      MenuItem item =
          new MenuItem(data.componentIdList.get(i), true, new NullCommand());
      item.setStyleName("wmt-ComponentSelectionMenuItem");
      item.addStyleDependentName("missing");
      this.addItem(item);
    };
  }
  
  /**
   * Replaces a placeholder MenuItem, created by
   * {@link ComponentSelectionMenu#initializeComponents()}, with a fully
   * functional MenuItem showing the component name and having an associated
   * action.
   * 
   * @param componentId the id of the component associated with the MenuItem to
   *          replace
   */
  public void replaceMenuItem(String componentId) {
    List<MenuItem> allItems = this.getItems();
    for (int i = 0; i < allItems.size(); i++) {
      MenuItem currentItem = allItems.get(i);
      if (currentItem.getText().matches(componentId)) {
        MenuItem newItem =
            new MenuItem(data.getComponent(componentId).getName(), true,
                new ComponentSelectionCommand(data, cell, componentId));
        newItem.setStyleName("wmt-ComponentSelectionMenuItem");
        this.insertItem(newItem, i);
        this.removeItem(currentItem);
        return;
      }
    }
  }

  public MenuItem getComponentItem() {
    return componentItem;
  }

  public void setComponentItem(MenuItem componentItem) {
    this.componentItem = componentItem;
  }
  
  public class NullCommand implements Command {
    @Override
    public void execute() {
      // Do nothing
    }
  }
}
