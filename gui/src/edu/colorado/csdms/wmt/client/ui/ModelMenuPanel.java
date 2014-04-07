/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.handler.ModelMenuPanelDeleteHandler;
import edu.colorado.csdms.wmt.client.ui.handler.ModelMenuPanelHelpHandler;
import edu.colorado.csdms.wmt.client.ui.handler.ModelMenuPanelOpenHandler;
import edu.colorado.csdms.wmt.client.ui.handler.ModelMenuPanelSaveHandler;
import edu.colorado.csdms.wmt.client.ui.handler.ModelMenuPanelStatusHandler;
import edu.colorado.csdms.wmt.client.ui.handler.SetupRunModelHandler;

/**
 * Makes a row of action buttons ("Open", "Save", "Run", etc.) for working with 
 * the model built in WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelMenuPanel extends HorizontalPanel {

  @SuppressWarnings("unused")
  private DataManager data;
  
  /**
   * Makes a new {@link ModelMenuPanel}.
   */
  public ModelMenuPanel(DataManager data) {

    this.data = data;
    this.setSpacing(5); // px

    // Open
    Button openButton = new Button(DataManager.FA_OPEN);
    openButton.setTitle("Open model");
    openButton.addClickHandler(new ModelMenuPanelOpenHandler(data));
    this.add(openButton);

    // Save
    Button saveButton = new Button(DataManager.FA_SAVE);
    saveButton.setTitle("Save model");
    saveButton.addClickHandler(new ModelMenuPanelSaveHandler(data));
    this.add(saveButton);

    // Save As
    Button saveAsButton =
        new Button(DataManager.FA_SAVE + "<i class='fa fa-caret-right'></i>");
    saveAsButton.setTitle("Save model as...");
    saveAsButton.addClickHandler(new ModelMenuPanelSaveHandler(data, true));    
    this.add(saveAsButton);

    // Delete
    Button deleteButton = new Button(DataManager.FA_DELETE);
    deleteButton.setTitle("Delete model");
    deleteButton.addClickHandler(new ModelMenuPanelDeleteHandler(data));
    this.add(deleteButton);

    // Run
    Button runButton = new Button(DataManager.FA_RUN);
    runButton.setTitle("Run model");
    runButton.addClickHandler(new SetupRunModelHandler(data));
    this.add(runButton);

    // Run status
    Button statusButton = new Button(DataManager.FA_STATUS);
    statusButton.setTitle("Status of model run");
    statusButton.addClickHandler(new ModelMenuPanelStatusHandler());    
    this.add(statusButton);

    // Help
    Button helpButton = new Button(DataManager.FA_HELP);
    helpButton.setTitle("Help on using WMT");
    helpButton.addClickHandler(new ModelMenuPanelHelpHandler());        
    this.add(helpButton);

    // Apply a style to each button.
    Iterator<Widget> iter = this.iterator();
    while (iter.hasNext()) {
      Button button = (Button) iter.next();
      button.setStyleName("wmt-ModelMenuPanelButton");
    }
  }
}
