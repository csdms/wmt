/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.handler.MenuOpenModelHandler;

/**
 * Makes a row of action buttons ("Open", "Save", "Run", etc.) for working with 
 * the model built in WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelMenuPanel extends HorizontalPanel {

  private DataManager data;
  
  /**
   * Makes a new {@link ModelMenuPanel}.
   */
  public ModelMenuPanel(DataManager data) {

    this.data = data;
    this.setSpacing(5); // px

    // Open
    Button openButton = new Button("<i class='fa fa-folder-open-o'></i>");
    openButton.setTitle("Open model");
    openButton.addClickHandler(new MenuOpenModelHandler(data));
    this.add(openButton);

    Button saveButton = new Button("<i class='fa fa-floppy-o'></i>");
    saveButton.setTitle("Save model");
    this.add(saveButton);

    Button saveAsButton =
        new Button(
            "<i class='fa fa-floppy-o'></i> <i class='fa fa-caret-right'></i>");
    saveAsButton.setTitle("Save model as...");
    this.add(saveAsButton);

    Button deleteButton = new Button("<i class='fa fa-trash-o'></i>");
    deleteButton.setTitle("Delete model");
    this.add(deleteButton);

    Button runButton = new Button("<i class='fa fa-play'></i>");
    runButton.setTitle("Run model");
    this.add(runButton);

    Button statusButton = new Button("<i class='fa fa-info'></i>");
    statusButton.setTitle("Status of model run");
    this.add(statusButton);

    Button helpButton = new Button("<i class='fa fa-question'></i>");
    helpButton.setTitle("Help on using WMT");
    this.add(helpButton);

    // Apply a style to each button.
    Iterator<Widget> iter = this.iterator();
    while (iter.hasNext()) {
      Button button = (Button) iter.next();
      button.setStyleName("wmt-ModelMenuPanelButton");
    }
  }
}
