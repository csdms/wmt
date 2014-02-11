/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A GWT composite widget that defines a box listing files and a box showing
 * which file is selected.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ListPanel extends Composite {

  private ListBox availableFiles;
  private TextBox selectedFile;

  /**
   * Defines an empty ListPanel.
   */
  public ListPanel() {
  }

  /**
   * Defines a ListPanel with an array of model names.
   * 
   * @param availableModels a String[] of model names
   */
  public ListPanel(String[] availableModels) {

    Label availableLabel = new Label("Available:");
    availableFiles = new ListBox(false); // multiselect off
    for (int i = 0; i < availableModels.length; i++) {
      availableFiles.addItem(availableModels[i]);
    }
    availableFiles.setVisibleItemCount(5); // show 5 items

    Label selectedLabel = new Label("Selected:");
    selectedFile = new TextBox();
    selectedFile.setText(availableModels[0]);
    
    Grid paths = new Grid(2, 2);
    paths.setWidget(0, 0, availableLabel);
    paths.setWidget(0, 1, availableFiles);
    paths.setWidget(1, 0, selectedLabel);
    paths.setWidget(1, 1, selectedFile);

    // Styles!
    availableFiles.setWidth("35ch");
    selectedFile.setWidth("35ch");
    availableFiles.getElement().getStyle().setBackgroundColor("#ffc");
    selectedFile.getElement().getStyle().setBackgroundColor("#ffc");
    availableLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    selectedLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

    VerticalPanel contents = new VerticalPanel();
    contents.add(paths);

    initWidget(contents);
  }

  public ListBox getAvailableFiles() {
    return availableFiles;
  }

  public void setAvailableFiles(ListBox availableFiles) {
    this.availableFiles = availableFiles;
  }

  public TextBox getSelectedFile() {
    return selectedFile;
  }

  public void setSelectedFile(TextBox selectedFile) {
    this.selectedFile = selectedFile;
  }
}
