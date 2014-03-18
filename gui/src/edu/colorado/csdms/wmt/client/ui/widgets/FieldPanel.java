/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A GWT composite widget that defines a label and a field (which can
 * optionally mask text) for specifying text; e.g., a file name, a login, or a
 * password.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class FieldPanel extends Composite {

  private Label fieldLabel;
  private TextBox field;

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

    Grid paths = new Grid(1, 2);

    fieldLabel = new Label("Name:");
    field = secure ? new PasswordTextBox() : new TextBox();
    field.setText(text);
    paths.setWidget(0, 0, fieldLabel);
    paths.setWidget(0, 1, field);

    // Styles!
    field.setWidth("15em");
    fieldLabel.getElement().getStyle().setPaddingLeft(1, Unit.EM);
    fieldLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

    VerticalPanel contents = new VerticalPanel();
    contents.add(paths);

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
  public String getField() {
    return field.getText();
  }

  /**
   * Sets the text displayed in the FieldPanel.
   */
  public void setField(String text) {
    field.setText(text);
  }
}
