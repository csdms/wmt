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
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A customized DialogBox with a {@link DroplistPanel} for choosing an item
 * and a {@link ChoicePanel} displaying "OK" and "Cancel" buttons.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DroplistDialogBox extends DialogBox {

  private DroplistPanel itemPanel;
  private ChoicePanel choicePanel;
  
  /**
   * Makes a new DroplistDialogBox with default settings in the
   * {@link DroplistPanel} and {@link ChoicePanel}.
   */
  public DroplistDialogBox() {

    super(false); // autohide
    this.setModal(true);
    this.setStyleName("wmt-DialogBox");

    itemPanel = new DroplistPanel();
    choicePanel = new ChoicePanel();

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.add(itemPanel);
    contents.add(choicePanel);

    this.setWidget(contents);
  }

  public DroplistPanel getDroplistPanel() {
    return itemPanel;
  }

  public void setDroplistPanel(DroplistPanel itemPanel) {
    this.itemPanel = itemPanel;
  }

  public ChoicePanel getChoicePanel() {
    return choicePanel;
  }

  public void setChoicePanel(ChoicePanel choicePanel) {
    this.choicePanel = choicePanel;
  }
}
