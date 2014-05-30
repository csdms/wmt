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
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A GWT composite widget that defines a label and a field (which can
 * optionally mask text) for specifying text; e.g., a file name, a login, or a
 * password.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class FieldPanel extends Composite {

  private Label fieldLabel;
  private TextBox fieldBox;

  /**
   * Defines an empty FieldPanel.
   */
  public FieldPanel() {
    this("", false);
  }

  /**
   * Defines a FieldPanel with user-supplied text.
   * 
   * @param text the String to display in the field
   */
  public FieldPanel(String text) {
    this(text, false);
  }

  /**
   * Defines an empty FieldPanel that obscures the text displayed within it.
   * 
   * @param secure set to true to obscure text
   */
  public FieldPanel(Boolean secure) {
    this("", secure);
  }

  /**
   * Defines a FieldPanel with user-supplied text displayed in the field,
   * optionally obscured.
   * 
   * @param text the String to display in the field
   * @param secure set to true to obscure text
   */
  public FieldPanel(String text, Boolean secure) {

    fieldLabel = new Label("Name:");
    fieldBox = secure ? new PasswordTextBox() : new TextBox();
    fieldBox.setText(text);

    // Styles!
    fieldBox.setStyleName("wmt-TextBoxen");
    fieldLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

    HorizontalPanel contents = new HorizontalPanel();
    contents.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    contents.setSpacing(5); // px
    contents.add(fieldLabel);
    contents.add(fieldBox);

    initWidget(contents);
  }

  /**
   * Returns the prefix label in the FieldPanel.
   */
  public Label getLabel() {
    return fieldLabel;
  }

  /**
   * Sets the prefix label in the FieldPanel.
   */
  public void setLabel(Label fieldLabel) {
    this.fieldLabel = fieldLabel;
  }

  /**
   * Returns the text displayed in the FieldPanel.
   */
  public TextBox getField() {
    return this.fieldBox;
  }

  /**
   * Sets the text displayed in the FieldPanel.
   */
  public void setField(TextBox box) {
    this.fieldBox = box;
  }
}
