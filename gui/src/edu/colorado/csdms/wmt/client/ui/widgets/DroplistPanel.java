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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A GWT composite widget that defines a label and a droplist of items.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DroplistPanel extends Composite {

  private Label dropLabel;
  private ListBox droplist;

  /**
   * Defines an empty DroplistPanel.
   */
  public DroplistPanel() {
    this(null);
  }

  /**
   * Defines a DroplistPanel populated with a set of input items.
   * 
   * @param items a String[] of items to display in the droplist
   */
  public DroplistPanel(String[] items) {

    dropLabel = new Label("Available models:");
    droplist = new ListBox(false); // multiselect off
    if (items != null) {
      for (int i = 0; i < items.length; i++) {
        droplist.addItem(items[i]);
      }
    }
    droplist.setVisibleItemCount(1); // show 1 item = a droplist

    // Styles!
    droplist.setStyleName("wmt-DroplistBox");
    dropLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

    HorizontalPanel contents = new HorizontalPanel();
    contents.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    contents.setSpacing(5); // px
    contents.add(dropLabel);
    contents.add(droplist);

    initWidget(contents);
  }

  public Label getLabel() {
    return dropLabel;
  }

  public void setLabel(Label label) {
    this.dropLabel = label;
  }

  public ListBox getDroplist() {
    return droplist;
  }

  public void setDroplist(ListBox droplist) {
    this.droplist = droplist;
  }
}
