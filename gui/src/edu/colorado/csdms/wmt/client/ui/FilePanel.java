/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A GWT composite widget that defines a field for specifying a file name.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class FilePanel extends Composite {

  private TextBox fileInput;

  /**
   * Defines a FilePanel with a default file name.
   */
  public FilePanel() {
    this("model.json");
  }

  /**
   * Defines a FilePanel with a user-defined value for the file name.
   * 
   * @param file a file name, a String
   */
  public FilePanel(String file) {

    Grid paths = new Grid(1, 2);

    Label fileLabel = new Label("Name:");
    fileInput = new TextBox();
    fileInput.setText(file);
    paths.setWidget(0, 0, fileLabel);
    paths.setWidget(0, 1, fileInput);

    // Styles!
    fileInput.setWidth("35ch");
    fileInput.getElement().getStyle().setBackgroundColor("#ffc");
    fileLabel.getElement().getStyle().setPaddingLeft(1, Unit.EM);
    fileLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

    VerticalPanel contents = new VerticalPanel();
    contents.add(paths);

    initWidget(contents);
  }

  public String getFile() {
    return fileInput.getText();
  }

  public void setFile(String file) {
    fileInput.setText(file);
  }
}
