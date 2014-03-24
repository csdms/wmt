package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import edu.colorado.csdms.wmt.client.control.DataManager;

public class ComponentDeleteCommand implements Command {

  private DataManager data;
  private String componentId;
  
  public ComponentDeleteCommand(DataManager data, String componentId) {
    this.data = data;
    this.componentId = componentId;
  }

  @Override
  public void execute() {
    GWT.log("Delete " + data.getComponent(componentId).getName());
  }

}
