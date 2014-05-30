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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.Constants;

/**
 * A GWT composite widget that displays right-aligned "OK" and "Cancel"
 * buttons, with icons. Each button's text and icon can be modified.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ChoicePanel extends Composite {

  private Button okButton;
  private Button cancelButton;

  /**
   * Makes a new ChoicePanel with the default "OK" and "Cancel" buttons.
   */
  public ChoicePanel() {

    okButton = new Button(Constants.FA_OK + "OK");
    cancelButton = new Button(Constants.FA_CANCEL + "Cancel");
    HorizontalPanel buttonPanel = new HorizontalPanel();
    buttonPanel.setSpacing(5); // px
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    HorizontalPanel choicePanel = new HorizontalPanel();
    choicePanel.setWidth("100%");
    choicePanel.add(buttonPanel);

    VerticalPanel contents = new VerticalPanel();
    contents.add(choicePanel);
    
    okButton.setStyleName("wmt-Button");
    cancelButton.setStyleName("wmt-Button");
    okButton.getElement().getStyle().setMarginRight(3, Unit.PX);
    
    initWidget(contents);
  }

  /**
   * Returns the "OK" {@link Button} used in a ChoicePanel.
   */
  public Button getOkButton() {
    return okButton;
  }

  /**
   * Sets the "OK" {@link Button} used in a ChoicePanel.
   */
  public void setOkButton(Button okButton) {
    this.okButton = okButton;
  }

  /**
   * Returns the "Cancel" {@link Button} used in a ChoicePanel.
   */
  public Button getCancelButton() {
    return cancelButton;
  }

  /**
   * Sets the "Cancel" {@link Button} used in a ChoicePanel.
   */
  public void setCancelButton(Button cancelButton) {
    this.cancelButton = cancelButton;
  }

}
