/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 mcflugen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.Constants;
import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.handler.ComponentSelectionCommand;

/**
 * A {@link PopupPanel} menu that shows a list of components. This is the
 * initial menu displayed in a {@link ComponentCell}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentSelectionMenu extends PopupPanel {

  private DataManager data;
  private ComponentCell cell;
  private VerticalPanel menu;
  private MenuItem componentItem;

  /**
   * Makes a new {@link ComponentSelectionMenu}, which shows a list of
   * components; the user chooses one to populate the {@link ComponentCell}.
   * 
   * @param data the DataManager object for the WMT session
   * @param cell the {@link ComponentCell} this menu depends on
   */
  public ComponentSelectionMenu(DataManager data, ComponentCell cell) {
    super(true); // autohide
    this.data = data;
    this.cell = cell;
    this.setStyleName("wmt-PopupPanel");
    
    // A VerticalPanel for the menu items. (PopupPanels have only one child.)
    menu = new VerticalPanel();
    this.add(menu);
    
    updateComponents(cell.getPortId());
  }

  /**
   * A worker for adding a new MenuItem to the bottom of the
   * {@link ComponentSelectionMenu}.
   * 
   * @param componentId the id of the component to add to the menu
   */
  private void insertComponentMenuItem(String componentId) {
    insertComponentMenuItem(componentId, menu.getWidgetCount());
  }

  /**
   * A worker for adding a new MenuItem to the {@link ComponentSelectionMenu} at
   * the given index.
   * 
   * @param componentId the id of the component to add to the menu
   * @param index where to add the component to the menu
   */
  private void insertComponentMenuItem(String componentId, Integer index) {
    HTML item = new HTML(data.getComponent(componentId).getName());
    item.setStyleName("wmt-ComponentSelectionMenuItem");
    item.addClickHandler(new ComponentSelectionHandler(componentId));
    menu.insert(item, index);
  }

  /**
   * Loads the names of the components that match the uses port of the displayed
   * component into the {@link ComponentCell} menu.
   * 
   * @param portId the id of the uses port displayed in the ComponentCell
   */
  public void updateComponents(String portId) {

    menu.clear();

    // Display a wait message in the componentMenu.
    if (portId.matches(Constants.DRIVER)) {
      HTML item = new HTML("Loading...");
      menu.add(item);
      return;
    }

    // Load all available components into the componentMenu!
    if (portId.matches(Constants.ALL_COMPONENTS)) {
      for (int i = 0; i < data.componentIdList.size(); i++) {
        String componentId = data.componentIdList.get(i);
        insertComponentMenuItem(componentId);
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
          insertComponentMenuItem(componentId);
        }
      }
    }
  }

  /**
   * Loads the names of all available components into the {@link ComponentCell}
   * menu.
   */
  public void updateComponents() {
    updateComponents(Constants.ALL_COMPONENTS);
  }

  /**
   * This method builds the initial list of MenuItems in the driver
   * {@link ComponentCell}. This list functions as a placeholder. Only the ids
   * of the components are displayed, and there is no command associated with
   * the MenuItems. These MenuItems are replaced with fully functional MenuItems
   * when their associated component is successfully loaded from the server.
   */
  public void initializeComponents() {
    menu.clear();
    for (int i = 0; i < data.componentIdList.size(); i++) {
      HTML item = new HTML(data.componentIdList.get(i));
      item.setStyleName("wmt-ComponentSelectionMenuItem");
      item.addStyleDependentName("missing");
      menu.add(item);
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
    for (int i = 0; i < menu.getWidgetCount(); i++) {
      HTML currentItem = (HTML) menu.getWidget(i);
      if (currentItem.getText().matches(componentId)) {
        insertComponentMenuItem(componentId, i);
        menu.remove(currentItem);
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

  /**
   * Handles a click on a menu item in the {@link ComponentSelectionMenu}.
   * <p>
   * <b>Note:</b> This class wraps {@link ComponentSelectionCommand}. It might
   * be helpful to port the code from there to this handler.
   */
  public class ComponentSelectionHandler implements ClickHandler {

    private String componentId;
    
    /**
     * Makes a new {@link ComponentSelectionHandler}.
     * 
     * @param componentId the id of the component of the selected menu item
     */
    public ComponentSelectionHandler(String componentId) {
      this.componentId = componentId;
    }
    
    @Override
    public void onClick(ClickEvent event) {
      ComponentSelectionMenu.this.hide();
      if (!data.security.isLoggedIn()) {
        return;
      }
      ComponentSelectionCommand cmd =
          new ComponentSelectionCommand(data, cell, data.getComponent(
              componentId).getId());
      cmd.execute();
    }
  }
}
