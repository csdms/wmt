/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A GWT composite widget that defines a pair of fields for specifying a
 * directory path and a file name.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class FilePanel extends Composite {

  private TextBox dirInput;
  private TextBox fileInput;

  /**
   * Defines a FilePanel with default directory and file names.
   */
  public FilePanel() {
    this("$HOME/.wmt", "project.json");
  }

  /**
   * Defines a FilePanel with user-defined values for the directory and file
   * names.
   * 
   * @param directory a directory path, a String
   * @param file a file name, a String
   */
  public FilePanel(String directory, String file) {

    Grid paths = new Grid(2, 2);

    Label dirLabel = new Label("Directory path:");
    dirInput = new TextBox();
    dirInput.setText(directory);
    paths.setWidget(0, 0, dirLabel);
    paths.setWidget(0, 1, dirInput);

    Label fileLabel = new Label("Name:");
    fileInput = new TextBox();
    fileInput.setText(file);
    paths.setWidget(1, 0, fileLabel);
    paths.setWidget(1, 1, fileInput);

    // Styles!
    dirInput.setWidth("35ch");
    fileInput.setWidth("35ch");
    dirInput.getElement().getStyle().setBackgroundColor("#ffc");
    fileInput.getElement().getStyle().setBackgroundColor("#ffc");
    dirLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    fileLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

    VerticalPanel contents = new VerticalPanel();
    contents.add(paths);

    initWidget(contents);
  }

  public String getDirectory() {
    return dirInput.getText();
  }

  public void setDirectory(String directory) {
    dirInput.setText(directory);
  }

  public String getFile() {
    return fileInput.getText();
  }

  public void setFile(String file) {
    fileInput.setText(file);
  }

}
