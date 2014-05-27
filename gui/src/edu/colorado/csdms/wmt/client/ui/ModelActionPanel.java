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
package edu.colorado.csdms.wmt.client.ui;

import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.Widget;

import edu.colorado.csdms.wmt.client.Constants;
import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.handler.ModelActionPanelOpenHandler;
import edu.colorado.csdms.wmt.client.ui.handler.ModelActionPanelSaveHandler;
import edu.colorado.csdms.wmt.client.ui.handler.SetupRunModelHandler;

/**
 * Makes a row of action buttons ("Open", "Save", "Run", etc.) for working with
 * the model built in WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelActionPanel extends HorizontalPanel {

  private DataManager data;
  private MoreActionsMenu moreMenu;

  /**
   * Makes a new {@link ModelActionPanel}.
   * 
   * @param data the DataManager instance for the WMT session
   */
  public ModelActionPanel(DataManager data) {

    this.data = data;
    this.setStyleName("wmt-ActionPanel");

    // Open
    Button openButton = new Button(Constants.FA_OPEN);
    openButton.setTitle(Constants.MODEL_OPEN);
    openButton.addClickHandler(new ModelActionPanelOpenHandler(data));
    this.add(openButton);

    // Save
    Button saveButton = new Button(Constants.FA_SAVE);
    saveButton.setTitle(Constants.MODEL_SAVE);
    saveButton.addClickHandler(new ModelActionPanelSaveHandler(data));
    this.add(saveButton);

    // Run
    Button runButton = new Button(Constants.FA_RUN);
    runButton.setTitle(Constants.MODEL_RUN);
    runButton.addClickHandler(new SetupRunModelHandler(data));
    this.add(runButton);

    // More
    final Button moreButton = new Button("More" + Constants.FA_MORE);
    moreButton.setTitle(Constants.MODEL_MORE);
    this.add(moreButton);
    moreMenu = new MoreActionsMenu(data);
    moreButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (!ModelActionPanel.this.data.security.isLoggedIn()) {
          return;
        }
        moreMenu.populateMenu();
        moreMenu.setPopupPositionAndShow(new PositionCallback() {
          final Integer x = moreButton.getElement().getAbsoluteLeft();
          final Integer y = moreButton.getElement().getAbsoluteBottom();
          @Override
          public void setPosition(int offsetWidth, int offsetHeight) {
            moreMenu.setPopupPosition(x, y);
          }
        });
      }
    });

    // Apply a style to each button.
    Iterator<Widget> iter = this.iterator();
    while (iter.hasNext()) {
      Button button = (Button) iter.next();
      button.setStyleName("wmt-ActionPanelButton");
    }
  }

  public MoreActionsMenu getMoreMenu() {
    return moreMenu;
  }

  public void setMoreMenu(MoreActionsMenu moreMenu) {
    this.moreMenu = moreMenu;
  }
}
