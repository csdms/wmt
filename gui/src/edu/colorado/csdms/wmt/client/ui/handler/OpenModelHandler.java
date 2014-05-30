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

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.ui.widgets.OpenDialogBox;

/**
 * Handles click on the "OK" button in the open dialog that appears when the
 * "Open Model..." button is clicked in the ModelActionPanel. Calls
 * {@link DataTransfer#getModel(DataManager, Integer)} to pull the selected
 * model from the server.
 */
public class OpenModelHandler implements ClickHandler {
  
  private DataManager data;
  private OpenDialogBox box;
  
  /**
   * Creates a new {@link OpenModelHandler}.
   * 
   * @param data the DataManager object for the WMT session
   * @param box the dialog box
   */
  public OpenModelHandler(DataManager data, OpenDialogBox box) {
    this.data = data;
    this.box = box;
  }
  
  @Override
  public void onClick(ClickEvent event) {

    data.showWaitCursor();
    box.hide();

    data.getPerspective().reset();

    // Get the selected item from the openDialog. This feels fragile. I'm
    // using the index of the selected modelName to match up the index of
    // the modelId. This should work consistently because I add the modelId
    // and modelName to the ArrayList with the same index. It would be
    // better if they both resided in the same data structure.
    Integer selIndex =
        box.getDroplistPanel().getDroplist().getSelectedIndex();
//    Integer modelId = data.modelIdList.get(selIndex);
    
    String modelName = box.getDroplistPanel().getDroplist().getItemText(selIndex);
    Integer modelNameIndex = data.modelNameList.indexOf(modelName);
    Integer modelId = data.modelIdList.get(modelNameIndex);

    // Get the data + metadata for the selected model. On success, #getModel
    // calls DataManager#deserialize, which populates the WMT GUI.
    DataTransfer.getModel(data, modelId);
  }
}
