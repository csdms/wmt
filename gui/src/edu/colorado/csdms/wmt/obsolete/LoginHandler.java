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
package edu.colorado.csdms.wmt.obsolete;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;

/**
 * Saves login information in the {@link DataManager}; calls
 * {@link DataTransfer#login()} to process a login to WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
@Deprecated
public class LoginHandler implements ClickHandler {

  private DataManager data;
  private LoginDialogBox box;
  
  /**
   * Creates a new {@link LoginHandler}.
   * 
   * @param data the DataManager object for the WMT session
   * @param box the dialog box
   */
  public LoginHandler(DataManager data, LoginDialogBox box) {
    this.data = data;
    this.box = box;
  }
  
  @Override
  public void onClick(ClickEvent event) {

    box.hide();
    
    // Get WMT username.
    String userName = box.getUsernamePanel().getField().getText();
    data.security.setWmtUsername(userName);
    GWT.log(data.security.getWmtUsername());

    // Get WMT password.
    String password = box.getPasswordPanel().getField().getText();
    data.security.setWmtPassword(password);
    GWT.log(data.security.getWmtPassword());

    // Authenticate the user.
    DataTransfer.login(data);    
  }
}
