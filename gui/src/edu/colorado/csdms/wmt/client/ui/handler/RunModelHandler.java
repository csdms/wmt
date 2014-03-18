package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.ModelMenu;
import edu.colorado.csdms.wmt.client.ui.RunDialogBox;

/**
 * Handles click on the "Run" button in the dialog that appears when the
 * "Run Model..." button is clicked in the {@link ModelMenu}. Initializes a
 * model run with a call to {@link DataTransfer#initModelRun(DataManager)}.
 */
public class RunModelHandler implements ClickHandler {
  
  private DataManager data;
  private RunDialogBox box;
  
  /**
   * Creates a new {@link RunModelHandler}.
   * 
   * @param data the DataManager object for the WMT session
   * @param box the dialog box
   */
  public RunModelHandler(DataManager data, RunDialogBox box) {
    this.data = data;
    this.box = box;
  }
  
  @Override
  public void onClick(ClickEvent event) {

    box.hide();

    // Get host.
    Integer selIndex =
        box.getHostPanel().getDroplist().getSelectedIndex();
    String hostName =
        box.getHostPanel().getDroplist().getItemText(selIndex);
    data.setHostname(hostName);
    GWT.log(data.getHostname());

    // Get username.
    String userName = box.getUsernamePanel().getField();
    data.setUsername(userName);
    GWT.log(data.getUsername());

    // Get password.
    String password = box.getPasswordPanel().getField();
    data.setPassword(password);
    GWT.log(data.getPassword());

    // Initialize the model run.
    DataTransfer.initModelRun(data);
  }
}
