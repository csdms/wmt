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
import edu.colorado.csdms.wmt.client.ui.widgets.DroplistDialogBox;

/**
 * Handles click on the "Delete" button in the dialog that appears when the
 * "Delete Model..." button is clicked in the ModelActionPanel. Deletes the
 * selected model from the server with a call to
 * {@link DataTransfer#deleteModel(DataManager, Integer)}.
 */
public class DeleteModelHandler implements ClickHandler {
  
  private DataManager data;
  private DroplistDialogBox box;
  
  /**
   * Creates a new {@link DeleteModelHandler}.
   * 
   * @param data the DataManager object for the WMT session
   * @param box the dialog box
   */
  public DeleteModelHandler(DataManager data, DroplistDialogBox box) {
    this.data = data;
    this.box = box;
  }
  
  @Override
  public void onClick(ClickEvent event) {

    box.hide();

    Integer selIndex =
        box.getDroplistPanel().getDroplist().getSelectedIndex();
    Integer modelId = data.modelIdList.get(selIndex);

    /*
     * Check whether the current user owns the model selected for deletion. If
     * not, issue a message and exit. This might entail a call to
     * models/open/<id>.
     * 
     * Alternately, check owner on server and return a status code != 200. The
     * client can then display a message to the user.
     */
    
    GWT.log("Deleting model: " + modelId);
    DataTransfer.deleteModel(data, modelId);

    // If the deleted model is currently displayed in the model tree, close it.
    if (data.getMetadata().getId() == modelId) {
      data.getPerspective().reset();
    }
  }
}
