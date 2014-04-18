/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.handler.ComponentDeleteCommand;
import edu.colorado.csdms.wmt.client.ui.handler.ComponentGetInformationCommand;
import edu.colorado.csdms.wmt.client.ui.handler.ComponentShowParametersCommand;

/**
 * A menu that defines actions that can be performed on a component. Shown after
 * a component has been selected in a {@link ComponentCell}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentActionMenu extends PopupPanel {

  private DataManager data;
  private ComponentCell cell;
  
  /**
   * Makes a new {@link ComponentActionMenu}, showing actions that can be
   * performed on a component in a {@link ComponentCell}.
   * 
   * @param data the DataManager object for the WMT session
   * @param cell the {@link ComponentCell} this menu depends on
   */
  public ComponentActionMenu(DataManager data, ComponentCell cell) {

    super(true); // autohide
    this.data = data;
    this.cell = cell;
    this.setStyleName("wmt-PopupPanel");

    // A VerticalPanel for the menu items. (PopupPanels have only one child.)
    VerticalPanel menu = new VerticalPanel();
    this.add(menu);

    // MenuItem showParameters =
    // new MenuItem(
    // "<i class='fa fa-wrench fa-fw' style='color:#333'></i> Show parameters",
    // true, new ComponentShowParametersCommand(data, cell));
    HTML showParameters =
        new HTML(
            "<i class='fa fa-wrench fa-fw' style='color:#333'></i> Show parameters");

    // XXX Temporary solution.
    showParameters.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        ComponentActionMenu.this.hide();
        ComponentShowParametersCommand cmd =
            new ComponentShowParametersCommand(ComponentActionMenu.this.data,
                ComponentActionMenu.this.cell);
        cmd.execute();
      }
    });

    showParameters.setStyleName("wmt-ComponentActionMenuItem");
    // this.addItem(showParameters);
    menu.add(showParameters);

    // MenuItem getInformation =
    // new MenuItem(
    // "<i class='fa fa-question fa-fw' style='color:#55b'></i> Get information",
    // true, new ComponentGetInformationCommand(data, cell));
    HTML getInformation =
        new HTML(
            "<i class='fa fa-question fa-fw' style='color:#55b'></i> Get information");

    // XXX Temporary solution.
    getInformation.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        ComponentActionMenu.this.hide();
        ComponentGetInformationCommand cmd =
            new ComponentGetInformationCommand(ComponentActionMenu.this.data,
                ComponentActionMenu.this.cell);
        cmd.execute();
      }
    });

    getInformation.setStyleName("wmt-ComponentActionMenuItem");
    // this.addItem(getInformation);
    menu.add(getInformation);

    // this.addSeparator();
    HTML separator = new HTML("");
    separator.setStyleName("wmt-PopupPanelSeparator");
    menu.add(separator);

    // MenuItem deleteComponent =
    // new MenuItem(
    // "<i class='fa fa-times fa-fw' style='color:#b55'></i> Delete",
    // true, new ComponentDeleteCommand(data, cell));
    HTML deleteComponent =
        new HTML("<i class='fa fa-times fa-fw' style='color:#b55'></i> Delete");

    // XXX Temporary solution.
    deleteComponent.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        ComponentActionMenu.this.hide();
        ComponentDeleteCommand cmd =
            new ComponentDeleteCommand(ComponentActionMenu.this.data,
                ComponentActionMenu.this.cell);
        cmd.execute();
      }
    });

    deleteComponent.setStyleName("wmt-ComponentActionMenuItem");
    // this.addItem(deleteComponent);
    menu.add(deleteComponent);
  }
}
