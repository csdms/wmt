/**
 * <License>
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
