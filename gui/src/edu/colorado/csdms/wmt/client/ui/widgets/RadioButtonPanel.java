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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * A GWT composite that displays a label and a pair of radio buttons wrapped in
 * a {@link HorizontalPanel}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class RadioButtonPanel extends Composite {

  private Label panelLabel;
  private RadioButton leftButton;
  private RadioButton rightButton;
  
  /**
   * Makes a new {@link RadioButtonPanel} with default labels.
   */
  public RadioButtonPanel() {
    
    panelLabel = new Label("Name:");
    leftButton = new RadioButton("RadioButtonPanel", "True");
    rightButton = new RadioButton("RadioButtonPanel", "False");
    rightButton.setValue(true);
    
    HorizontalPanel contents = new HorizontalPanel();
    contents.setSpacing(5); // px
    contents.add(panelLabel);
    contents.add(leftButton);
    contents.add(rightButton);
    
    initWidget(contents);
  }

  public Label getPanelLabel() {
    return panelLabel;
  }

  public void setPanelLabel(Label panelLabel) {
    this.panelLabel = panelLabel;
  }

  public RadioButton getLeftButton() {
    return leftButton;
  }

  public void setLeftButton(RadioButton leftButton) {
    this.leftButton = leftButton;
  }

  public RadioButton getRightButton() {
    return rightButton;
  }

  public void setRightButton(RadioButton rightButton) {
    this.rightButton = rightButton;
  }
}
