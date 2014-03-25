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
import edu.colorado.csdms.wmt.client.ui.handler.ComponentDeleteCommand;
import edu.colorado.csdms.wmt.client.ui.handler.ComponentGetInformationCommand;
import edu.colorado.csdms.wmt.client.ui.handler.ComponentShowParametersCommand;

/**
 * Displays a model component in the ModelTree.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentCell extends MenuBar {

  private static String DRIVER = "driver";
  private static String ALL_COMPONENTS = "__all_components";
  private static String COMPONENT_ICON =
      "<i class='fa fa-plus fa-fw'></i> ";
  private static Integer TRIM = 12; // the number of characters to display

  private DataManager data;
  private String portId;
  private String componentId;
  private MenuBar componentMenu;
  private MenuItem componentItem;
  private MenuBar actionMenu;
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
   * Creates a new {@link ComponentCell}.
   * 
   * @param data the DataManager object for the WMT session
   * @param portId the id of the corresponding port for the cell
   */
  public ComponentCell(DataManager data, String portId) {

    this.data = data;
    this.setPortId(portId);

    // Show this menu when selecting a component.
    componentMenu = new MenuBar(true); // menu items stacked vertically
    updateComponents(portId);
    componentItem = new MenuItem(trimName(portId), componentMenu);
    componentItem.setStyleName("mwmb-componentItem");
    this.addItem(componentItem);
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

  /**
   * Loads the names of the components that match the uses port of the displayed
   * component into the {@link ComponentCell} menu.
   * 
   * @param portId the id of the uses port displayed in the ComponentCell
   */
  public void updateComponents(String portId) {

    componentMenu.clearItems();

    // Display a wait message in the componentMenu.
    if (portId.matches(DRIVER)) {
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

  /**
   * A worker to add a new MenuItem to the componentMenu.
   * 
   * @param index
   */
  private void addComponentMenuItem(Integer index) {
    MenuItem item =
        new MenuItem(COMPONENT_ICON + data.getComponent(index).getName(), true,
            new ComponentSelectionCommand(data.getComponent(index).getId()));
    componentMenu.addItem(item);
  }
  
  /**
   * Loads the actions that can be performed on a component into the
   * {@link ComponentCell} menu.
   */
  public void updateActions() {
    actionMenu.clearItems();

    MenuItem showParameters =
        new MenuItem("<i class='fa fa-wrench fa-fw'></i> Show parameters",
            true, new ComponentShowParametersCommand(data, componentId));
    actionMenu.addItem(showParameters);
    
    MenuItem getInformation  =
        new MenuItem("<i class='fa fa-question fa-fw'></i> Get information",
            true, new ComponentGetInformationCommand(data, componentId));
    actionMenu.addItem(getInformation);
    
    MenuItem deleteComponent  =
        new MenuItem("<i class='fa fa-times fa-fw'></i> Delete",
            true, new ComponentDeleteCommand(data, componentId));
    actionMenu.addItem(deleteComponent);
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
   * Takes action when the user selects a component from the componentMenu.
   */
  public class ComponentSelectionCommand implements Command {

    private String componentId;
    private String displayName;

    /**
     * Creates a new {@link ComponentSelectionCommand}.
     * 
     * @param componentId the id of the selected component
     */
    public ComponentSelectionCommand(String componentId) {
      this.componentId = componentId;
      String componentName = data.getComponent(componentId).getName();
      this.displayName = trimName(componentName);
    }

    @Override
    public void execute() {
      
      setComponentId(componentId);
      
      // Display the name of the selected component.
      componentItem.setText(displayName);
      componentItem.addStyleDependentName("connected");

      // Add the component to the ModelTree.
      data.getPerspective().getModelTree().addComponent(componentId,
          enclosingTreeItem);

      // Replace the componentMenu with the actionMenu.
      actionMenu = new MenuBar(true);
      componentItem.setSubMenu(actionMenu);
      updateActions();
    }
  }
}
