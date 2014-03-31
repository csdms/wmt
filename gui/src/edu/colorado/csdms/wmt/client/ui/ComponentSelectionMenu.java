/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

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
  private static String COMPONENT_ICON =
      "<i class='fa fa-plus-square fa-fw' style='color:#55b'></i> ";

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
   * @param index the index into the array of available components
   */
  private void addComponentMenuItem(Integer index) {
    MenuItem item =
        new MenuItem(COMPONENT_ICON + data.getComponent(index).getName(), true,
            new ComponentSelectionCommand(data, cell, data.getComponent(index)
                .getId()));
    item.setStyleName("wmt-ComponentCell-MenuItem");
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
      this.addItem("Loading...", new Command() {
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
        addComponentMenuItem(i);
      }
      return;
    }

    // Load only those components with provides ports matching the input portId.
    for (int i = 0; i < data.getComponents().size(); i++) {
      Integer nProvidesPorts = data.getComponent(i).getProvidesPorts().length();
      for (int j = 0; j < nProvidesPorts; j++) {
        if (data.getComponent(i).getProvidesPorts().get(j).getId().matches(
            portId)) {
          addComponentMenuItem(i);
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

  public MenuItem getComponentItem() {
    return componentItem;
  }

  public void setComponentItem(MenuItem componentItem) {
    this.componentItem = componentItem;
  }
}
