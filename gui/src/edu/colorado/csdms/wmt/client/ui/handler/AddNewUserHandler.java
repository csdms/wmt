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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.widgets.NewUserDialogBox;

/**
 * Handles adding a new user login to WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class AddNewUserHandler implements ClickHandler {

  private DataManager data;
  private NewUserDialogBox box;
  
  /**
   * Creates a new {@link AddNewUserHandler}.
   * 
   * @param data the DataManager object for the WMT session
   * @param box a {@link NewUserDialogBox}
   */
  public AddNewUserHandler(DataManager data, NewUserDialogBox box) {
    this.data = data;
    this.box = box;
  }
  
  @Override
  public void onClick(ClickEvent event) {
    String initialPassword = data.security.getWmtPassword();
    String reenteredPassword = box.getPasswordPanel().getField().getText();
    if (reenteredPassword.equals(initialPassword)) {
      DataTransfer.newUserLogin(data);
      box.hide();
    } else {
      Window.alert("Passwords do not match.");
      box.getPasswordPanel().getField().setText("");
    }
  }
}
