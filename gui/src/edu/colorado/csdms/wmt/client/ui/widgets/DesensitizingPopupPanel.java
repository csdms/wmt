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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A {@link PopupPanel} displayed with a "glass" element to gray out the widgets
 * behind it.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DesensitizingPopupPanel extends PopupPanel {

  private String message;
  
  /**
   * Makes a {@link DesensitizingPopupPanel} with a default message.
   */
  public DesensitizingPopupPanel() {
    this("Click to close this dialog.");
  }

  /**
   * Makes a {@link DesensitizingPopupPanel} with a user-defined default
   * message.
   * 
   * @param message a String displayed in the panel
   */
  public DesensitizingPopupPanel(String message) {
    super();
    this.setAutoHideEnabled(false);
    this.setGlassEnabled(true);
    this.setStyleName("wmt-DialogBox");    

    this.message = message;
    
    Label messageLabel = new Label(this.message);
    messageLabel.getElement().getStyle().setMargin(10.0, Unit.PX);
    
    Button okButton = new Button("OK");
    okButton.setStyleName("wmt-Button");
    
    VerticalPanel panel = new VerticalPanel();
    panel.setSpacing(5); // px
    panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    panel.add(messageLabel);
    panel.add(okButton);
    
    this.setWidget(panel);
    
    okButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        DesensitizingPopupPanel.this.hide();
      }
    });
  }
}
