package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.widgets.RunDialogBox;

/**
 * Handles click on the "Run" button in the dialog that appears when the
 * "Run Model..." button is clicked in the ModelMenuPanel. Initializes a
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
    data.security.setHpccHostname(hostName);
    GWT.log(data.security.getHpccHostname());

    // Get username.
    String userName = box.getUsernamePanel().getField();
    data.security.setHpccUsername(userName);
    GWT.log(data.security.getHpccUsername());

    // Get password.
    String password = box.getPasswordPanel().getField();
    data.security.setHpccPassword(password);
    GWT.log(data.security.getHpccPassword());

    // Initialize the model run.
    DataTransfer.initModelRun(data);
  }
}
