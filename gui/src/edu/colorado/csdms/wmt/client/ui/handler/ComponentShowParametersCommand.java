package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Command;

import edu.colorado.csdms.wmt.client.control.DataManager;

public class ComponentShowParametersCommand implements Command {

  private DataManager data;
  private String componentId;
  
  public ComponentShowParametersCommand(DataManager data, String componentId) {
    this.data = data;
    this.componentId = componentId;
  }

  @Override
  public void execute() {
    GWT.log("Show parameters for " + data.getComponent(componentId).getName());
  }

}
