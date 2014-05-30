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
package edu.colorado.csdms.wmt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.Perspective;

/**
 * WMT is the CSDMS Web Modeling Tool.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class WMT implements EntryPoint {
  
  private Perspective perspective;
  private DataManager data;

  /**
   * This is the entry point method. It draws the views that make up the WMT
   * GUI. It loads information about component models from a set of JSON
   * files on the server, then populates the GUI with this information.
   */
  public void onModuleLoad() {

    // Initialize the DataManager object.
    data = new DataManager();

    // Is GWT in development or production mode?
    data.isDevelopmentMode(!GWT.isProdMode() && GWT.isClient());

    // Are we using the development mode of the API?
    data.isApiDevelopmentMode(Constants.USE_API_DEV_MODE);

    // Load WMT's CSS rules. 
    // (See http://www.gwtproject.org/doc/latest/DevGuideUiCss.html#cssfiles)
    Resources.INSTANCE.css().ensureInjected();

    // Set up the basic framework of views for the GUI.
    perspective = new Perspective(data);
    RootLayoutPanel.get().add(perspective);
    perspective.initializeModel();
    perspective.initializeParameterTable();
    
    // Check whether the user is already logged in.
    DataTransfer.getLoginState(data);

    // Retrieve (asynchronously) and store the list of available components
    // and models. Note that when DataTransfer#getComponentList completes,
    // it immediately starts pulling component data from the server with calls
    // to DataTransfer#getComponent. Asynchronous requests are cool!
    data.showWaitCursor();
    DataTransfer.getComponentList(data);
    DataTransfer.getModelList(data);

    // Trap browser reload and close events (they're indistinguishable), and
    // present a message to the user.
    Window.addWindowClosingHandler(new Window.ClosingHandler() {
      @Override
      public void onWindowClosing(ClosingEvent event) {
        if (!data.isDevelopmentMode() && !data.modelIsSaved()) {
          event.setMessage(Constants.CLOSE_MSG);
        }
      }
    });
  }
}
