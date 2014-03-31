/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

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
public class ComponentActionMenu extends MenuBar {

  /**
   * Makes a new {@link ComponentActionMenu}, showing actions that can be
   * performed on a component in a {@link ComponentCell}.
   * 
   * @param data the DataManager object for the WMT session
   * @param cell the {@link ComponentCell} this menu depends on
   */
  public ComponentActionMenu(DataManager data, ComponentCell cell) {

    super(true); // vertical

    MenuItem showParameters =
        new MenuItem(
            "<i class='fa fa-wrench fa-fw' style='color:#333'></i> Show parameters",
            true, new ComponentShowParametersCommand(data, cell));
    showParameters.setStyleName("wmt-ComponentCell-MenuItem");
    this.addItem(showParameters);

    MenuItem getInformation =
        new MenuItem(
            "<i class='fa fa-question fa-fw' style='color:#55b'></i> Get information",
            true, new ComponentGetInformationCommand(data, cell));
    getInformation.setStyleName("wmt-ComponentCell-MenuItem");
    this.addItem(getInformation);

    this.addSeparator();
    
    MenuItem deleteComponent =
        new MenuItem(
            "<i class='fa fa-times fa-fw' style='color:#b55'></i> Delete",
            true, new ComponentDeleteCommand(data, cell));
    deleteComponent.setStyleName("wmt-ComponentCell-MenuItem");
    this.addItem(deleteComponent);
  }
}
