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
package edu.colorado.csdms.wmt.client.ui.handler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.widgets.RunDialogBox;

/**
 * Handles click on the "Run" button in the dialog that appears when the
 * "Run Model..." button is clicked in the ModelActionPanel. Initializes a
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
    String userName = box.getUsernamePanel().getField().getText();
    data.security.setHpccUsername(userName);
    GWT.log(data.security.getHpccUsername());

    // Get password.
    String password = box.getPasswordPanel().getField().getText();
    data.security.setHpccPassword(password);
    GWT.log(data.security.getHpccPassword());

    // Initialize the model run.
    DataTransfer.initModelRun(data);
  }
}
