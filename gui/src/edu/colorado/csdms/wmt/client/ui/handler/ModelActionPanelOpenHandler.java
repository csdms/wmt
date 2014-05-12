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
import com.google.gwt.event.dom.client.KeyDownEvent;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.widgets.OpenDialogBox;

/**
 * Handles click on the "Open Model..." button in the ModelActionPanel. Pops up
 * an instance of {@link OpenDialogBox} to prompt the user for a model to open.
 * Events are sent to {@link OpenModelHandler} (on clicking "OK" button or
 * hitting <code>Enter</code> key) and {@link DialogCancelHandler} (on clicking
 * "Cancel" or hitting <code>Esc</code> key).
 */
public class ModelActionPanelOpenHandler implements ClickHandler {

  private DataManager data;
  private OpenDialogBox openDialog;

  /**
   * Creates a new instance of {@link ModelActionPanelOpenHandler}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public ModelActionPanelOpenHandler(DataManager data) {
    this.data = data;
  }

  @Override
  public void onClick(ClickEvent event) {

    openDialog = new OpenDialogBox(data);

    // Populate the droplist with all available models on the server.
    for (int i = 0; i < data.modelNameList.size(); i++) {
      openDialog.getDroplistPanel().getDroplist().addItem(
          data.modelNameList.get(i));
    }

    // Define handlers.
    final OpenModelHandler openHandler = new OpenModelHandler(data, openDialog);
    final DialogCancelHandler cancelHandler =
        new DialogCancelHandler(openDialog);

    // Apply handlers to OK and Cancel buttons.
    openDialog.getChoicePanel().getOkButton().addClickHandler(openHandler);
    openDialog.getChoicePanel().getCancelButton()
        .addClickHandler(cancelHandler);

    // Also apply handlers to "Enter" and "Esc" keys.    
    openDialog.addDomHandler(new ModalKeyHandler(openHandler, cancelHandler),
        KeyDownEvent.getType());

    openDialog.center();

    // Give the droplist focus.
    openDialog.getDroplistPanel().getDroplist().setFocus(true);
  }
}
