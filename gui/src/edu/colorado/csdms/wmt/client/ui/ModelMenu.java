/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.control.DataURL;

/**
 * Encapsulates a menu for operations on Models -- e.g., New, Open, Close and
 * Save -- as well as a "hamburger" icon to show/hide this menu. The icon and
 * menu are positioned in the upper right corner of the Arena view.
 * <p>
 * Menu item names, icons and grouping are modeled on Word, Eclipse and
 * Chrome.
 * 
 * @see http://fortawesome.github.io/Font-Awesome/
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelMenu extends DecoratedPopupPanel {

  private DataManager data;
  private HTML menuButton;
  private SaveDialogBox saveDialog;
  private DroplistDialogBox openDialog;
  private DroplistDialogBox deleteDialog;
  private RunDialogBox runDialog;

  /**
   * Sets up the Model menu, including all its menu items, as well as its
   * "hamburger" icon. The menu is only shown when the icon is clicked.
   */
  public ModelMenu(DataManager data) {

    super(true); // autohide
    this.setWidth("25ch"); // ch = character width // XXX Remove hard code?
    this.getElement().getStyle().setCursor(Cursor.POINTER); // use pointer
    this.data = data;

    // A FlexTable for the menu items. (PopupPanels can have only one child.)
    FlexTable menu = new FlexTable();
    this.add(menu);

    // The items to display on the Model menu.
    ModelMenuItem newModel = new ModelMenuItem("New Model");
    ModelMenuItem openModel =
        new ModelMenuItem("Open Model...", "fa-folder-open-o");
    ModelMenuItem closeModel =
        new ModelMenuItem("Close Model", "fa-folder-o");
    ModelMenuItem saveModel =
        new ModelMenuItem("Save Model", "fa-floppy-o");
    ModelMenuItem saveModelAs =
        new ModelMenuItem("Save Model As...", "fa-floppy-o");
    ModelMenuItem deleteModel =
        new ModelMenuItem("Delete Model...", "fa-trash-o");
    ModelMenuItem runModel =
        new ModelMenuItem("Run Model...", "fa-play");
    ModelMenuItem runStatus = new ModelMenuItem("View Run Status...");    
    ModelMenuItem helpButton = new ModelMenuItem("Help");
    ModelMenuItem aboutButton = new ModelMenuItem("About");

    // Add menu items to FlexTable.
    Integer menuIndex = 0;
    menu.setWidget(menuIndex++, 0, newModel); // side effect intentional
    menu.setWidget(menuIndex++, 0, openModel);
    menu.setWidget(menuIndex++, 0, new ModelMenuItem());
    menu.setWidget(menuIndex++, 0, closeModel);
    menu.setWidget(menuIndex++, 0, saveModel);
    menu.setWidget(menuIndex++, 0, saveModelAs);
    menu.setWidget(menuIndex++, 0, deleteModel);
    menu.setWidget(menuIndex++, 0, new ModelMenuItem());
    menu.setWidget(menuIndex++, 0, runModel);
    menu.setWidget(menuIndex++, 0, runStatus);
    menu.setWidget(menuIndex++, 0, new ModelMenuItem());
    menu.setWidget(menuIndex++, 0, helpButton);
    menu.setWidget(menuIndex++, 0, aboutButton);

    // Associate custom event handlers with the menu items.
    newModel.addClickHandler(new NewModelHandler());
    openModel.addClickHandler(new OpenModelHandler());
    closeModel.addClickHandler(new CloseModelHandler());
    saveModel.addClickHandler(new SaveModelHandler());
    saveModelAs.addClickHandler(new SaveModelAsHandler());
    deleteModel.addClickHandler(new DeleteModelHandler());
    runModel.addClickHandler(new RunModelHandler());
    runStatus.addClickHandler(new RunStatusHandler());
    helpButton.addClickHandler(new HelpHandler());
    aboutButton.addClickHandler(new AboutHandler());

    // Set up, but don't display, the "hamburger" icon for the Model menu.
    menuButton = new HTML("<i class='fa fa-bars fa-2x'></i>");
    menuButton.setStyleName("wmt-ModelMenuButton");
    menuButton.setTitle("Model menu");

    /*
     * Toggle the visibility of the Model menu on a click (MouseDownEvent) of
     * the "hamburger" icon. Right-align the menu below the icon. Need to call
     * #setPopupPositionAndShow instead of #setPopupPosition and #show to get
     * the correct size of the menu.
     */
    menuButton.addDomHandler(new MouseDownHandler() {
      @Override
      public void onMouseDown(MouseDownEvent event) {
        final Integer x = menuButton.getElement().getAbsoluteRight();
        final Integer y = menuButton.getElement().getAbsoluteBottom();
        ModelMenu.this
            .setPopupPositionAndShow(new PopupPanel.PositionCallback() {
              @Override
              public void setPosition(int offsetWidth, int offsetHeight) {
                ModelMenu.this.setPopupPosition(x - offsetWidth, y);
              }
            });
      }
    }, MouseDownEvent.getType());
  }

  /**
   * Returns the ModelMenu's menu button.
   */
  public HTML getMenuButton() {
    return this.menuButton;
  }

  /**
   * Sets the HTML used in the ModelMenu's menu button.
   * 
   * @param html the HTML to display
   */
  public void setMenuButton(HTML html) {
    this.menuButton = html;
  }

  /**
   * An inner class for making the menu items that appear on the Model menu.
   */
  public class ModelMenuItem extends Grid {

    /**
     * The zero-element constructor makes a separator from an HTML "hr"
     * element.
     */
    public ModelMenuItem() {
      super(1, 1);
      this.setWidget(0, 0, new HTML("<hr>"));
      this.setStyleName("wmt-ModelMenuSeparator");
    }

    /**
     * Makes a menu item for the Model menu.
     * 
     * @param menuText the text to display in the menu item
     */
    public ModelMenuItem(String menuText) {
      super(1, 1);
      this.setWidget(0, 0, new HTML(menuText));
    }

    /**
     * Makes a menu item for the Model menu that includes an icon.
     * 
     * @param menuText the text to display in the menu item
     * @param faIcon a Font Awesome icon name
     */
    public ModelMenuItem(String menuText, String faIcon) {
      super(1, 2);
      this.setWidget(0, 0, new HTML("<i class='fa " + faIcon + " fa-fw'>"));
      this.setWidget(0, 1, new HTML(menuText));
    }
  }

  /**
   * Handles click on the "New Model" button in the ModelMenu. Opens a new
   * instance of WMT in a new browser tab.
   */
  public class NewModelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenu.this.hide();
      Window.open(DataURL.applicationURL(data), "_blank", null);
    }
  }

  /**
   * Handles click on the "Open Model..." button in the ModelMenu. Pops up an
   * instance of {@link DroplistDialogBox} to prompt the user for a model to
   * open. Events are sent to {@link OpenOkHandler} and
   * {@link GenericCancelHandler}.
   */
  public class OpenModelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {

      openDialog = new DroplistDialogBox();
      openDialog.setText("Open Model...");
      openDialog.getChoicePanel().getOkButton().setHTML(
          "<i class='fa fa-folder-open-o'></i> Open");      

      // Populate the ModelDroplist with the available models on the server.
      for (int i = 0; i < data.modelNameList.size(); i++) {
        openDialog.getDroplistPanel().getDroplist().addItem(
            data.modelNameList.get(i));
      }

      openDialog.getChoicePanel().getOkButton().addClickHandler(
          new OpenOkHandler());
      openDialog.getChoicePanel().getCancelButton().addClickHandler(
          new GenericCancelHandler());

      openDialog.center();
      ModelMenu.this.hide();
    }
  }

  /**
   * Handles click on the "Close Model" button in the ModelMenu. Resets the
   * WMT GUI to its default state with a call to {@link Perspective#reset()}.
   */
  public class CloseModelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenu.this.hide();
      data.getPerspective().reset();
    }
  }

  /**
   * Pops up an instance of {@link SaveDialogBox} to prompt the user to save
   * the model. Events are sent to {@link SaveOkHandler} and
   * {@link GenericCancelHandler}.
   */
  private void showSaveDialogBox() {
    saveDialog = new SaveDialogBox(data.getModel().getName());
    saveDialog.getFilePanel().setTitle(
        "Enter a name for the model. No file extension is needed.");
    saveDialog.getChoicePanel().getOkButton().addClickHandler(
        new SaveOkHandler());
    saveDialog.getChoicePanel().getCancelButton().addClickHandler(
        new GenericCancelHandler());
    saveDialog.center();
  }

  /**
   * Handles click on the "Save Model" button in the ModelMenu. Saves a
   * not-previously-saved model or a new model displayed in WMT to the server
   * with a call to {@link DataTransfer#postModel(DataManager)}.
   */
  public class SaveModelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {

      ModelMenu.this.hide();

      // If the model hasn't been saved previously (i.e., it has an id of -1),
      // show the SaveDialogBox; otherwise, serialize the model and post it to
      // the server.
      GWT.log("modelIsSaved = " + data.modelIsSaved() + "; "
          + "modelHasBeenSaved = " + (data.getMetadata().getId() != -1));
      if (!data.modelIsSaved()) {
        if (data.getMetadata().getId() == -1) {
          showSaveDialogBox();
        } else {
          data.serialize();
          DataTransfer.postModel(data);
        }
      }
    }
  }

  /**
   * Handles click on the "Save Model As..." button in the ModelMenu. Prompts
   * the user for a model name using {@link ModelMenu#showSaveDialogBox()}.
   */
  public class SaveModelAsHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenu.this.hide();
      showSaveDialogBox();
    }
  }

  /**
   * Handles click on the "Delete" button in the ModelMenu. It presents an
   * instance of {@link DroplistDialogBox} with a "Delete" button.
   */
  public class DeleteModelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      
      deleteDialog = new DroplistDialogBox();
      deleteDialog.setText("Delete Model...");
      deleteDialog.getChoicePanel().getOkButton().setHTML(
          "<i class='fa fa-trash-o'></i> Delete");

      // Populate the ModelDroplist with the available models on the server.
      for (int i = 0; i < data.modelNameList.size(); i++) {
        deleteDialog.getDroplistPanel().getDroplist().addItem(
            data.modelNameList.get(i));
      }

      deleteDialog.getChoicePanel().getOkButton().addClickHandler(
          new DeleteOkHandler());
      deleteDialog.getChoicePanel().getCancelButton().addClickHandler(
          new GenericCancelHandler());

      deleteDialog.center();
      ModelMenu.this.hide();      
    }
  }

  /**
   * Handles click on the "Run" button in the ModelMenu. Displays
   * {@link RunDialogBox}.
   */
  public class RunModelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {

      if (!data.modelIsSaved()) {
        String msg = "The model must be saved to the server"
            + " before it can be run.";
        Window.alert(msg);
        return;
      }
      
      runDialog = new RunDialogBox();

      // TODO This should be configured. Can't desensitize ListBox elements.
//      String hosts[] =
//          {"CSDMS supercomputer (Beach)", 
//          "University of Colorado supercomputer (Janus)",
//          "Localhost"};
      String hosts[] =
          {"beach.colorado.edu", 
          "janus.colorado.edu",
          "localhost"};
      for (int i = 0; i < hosts.length; i++) {
        runDialog.getHostPanel().getDroplist().addItem(hosts[i]);
      }

      runDialog.getChoicePanel().getOkButton().addClickHandler(
          new RunOkHandler());
      runDialog.getChoicePanel().getCancelButton().addClickHandler(
          new GenericCancelHandler());

      runDialog.center();
      ModelMenu.this.hide();
    }
  }

  /**
   * Handles click on the "View Run Status..." button in the ModelMenu.
   * Displays the API "run/show" page showing the status of all currently
   * running models.
   */
  public class RunStatusHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenu.this.hide();
      Window.open(DataURL.showModelRun(), "runInfoDialog", null);
    }
  }

  /**
   * Handles click on the "Help" button in the ModelMenu.
   */
  public class HelpHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenuItem item = (ModelMenuItem) event.getSource();
      ModelMenu.this.hide();
      Window.alert("Clicked on: " + item.getText(0, 0));
    }
  }

  /**
   * Handles click on the "About" button in the ModelMenu.
   */
  public class AboutHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenuItem item = (ModelMenuItem) event.getSource();
      ModelMenu.this.hide();
      Window.alert("Clicked on: " + item.getText(0, 0));
    }
  }

  /**
   * Handles click on the "OK" button in the open dialog that appears when the
   * "Open Model..." button is clicked in the ModelMenu. Calls
   * {@link DataTransfer#getModel(DataManager, Integer)} to pull the selected
   * model from the server.
   */
  public class OpenOkHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {

      openDialog.hide();

      data.getPerspective().reset();

      // Get the selected item from the openDialog. This feels fragile. I'm
      // using the index of the selected modelName to match up the index of
      // the modelId. This should work consistently because I add the modelId
      // and modelName to the ArrayList with the same index. It would be
      // better if they both resided in the same data structure.
      Integer selIndex =
          openDialog.getDroplistPanel().getDroplist().getSelectedIndex();
      Integer modelId = data.modelIdList.get(selIndex);

      // Get the data + metadata for the selected model. On success, #getModel
      // calls DataManager#deserialize, which populates the WMT GUI.
      DataTransfer.getModel(data, modelId);
    }
  }

  /**
   * Handles click on the "OK" button in the save dialog that appears when the
   * "Save Model As..." button is clicked in the ModelMenu. Uses
   * {@link DataManager#serialize()} to serialize the model, then posts it to
   * the server with {@link DataTransfer#postModel(DataManager)}.
   */
  public class SaveOkHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {

      saveDialog.hide();

      // Set the model name in the DataManager.
      String modelName = saveDialog.getFilePanel().getField();
      if (modelName.isEmpty()) {
        return;
      }      
      if (!data.getModel().getName().matches(modelName)) {
        data.getModel().setName(modelName);
        data.saveAttempts++;
      }

      // Serialize the model from the GUI and post it to the server.
      data.serialize();
      DataTransfer.postModel(data);
    }
  }

  /**
   * Handles click on the "Delete" button in the dialog that appears when the
   * "Delete Model..." button is clicked in the ModelMenu. Deletes the
   * selected model from the server with a call to
   * {@link DataTransfer#deleteModel(DataManager, Integer)}.
   */
  public class DeleteOkHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {

      deleteDialog.hide();

      Integer selIndex =
          deleteDialog.getDroplistPanel().getDroplist().getSelectedIndex();
      Integer modelId = data.modelIdList.get(selIndex);
      GWT.log("Deleting model: " + modelId);

      DataTransfer.deleteModel(data, modelId);

      // If the deleted model is currently displayed, close it.
      if (data.getMetadata().getId() == modelId) {
        data.getPerspective().reset();
      }
    }
  }  

  /**
   * Handles click on the "Run" button in the dialog that appears when the
   * "Run Model..." button is clicked in the ModelMenu. Initializes a model
   * run with a call to {@link DataTransfer#initModelRun(DataManager)}.
   */
  public class RunOkHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      
      runDialog.hide();

      // Get host.
      Integer selIndex =
          runDialog.getHostPanel().getDroplist().getSelectedIndex();
      String hostName =
          runDialog.getHostPanel().getDroplist().getItemText(selIndex);
      data.setHostname(hostName);
      GWT.log(data.getHostname());
      
      // Get username.
      String userName = runDialog.getUsernamePanel().getField();
      data.setUsername(userName);
      GWT.log(data.getUsername());

      // Get password.
      String password = runDialog.getPasswordPanel().getField();
      data.setPassword(password);
      GWT.log(data.getPassword());

      // Initialize the model run.
      DataTransfer.initModelRun(data);
    }
  }

  /**
   * Handles click on the "Cancel" button in any dialog spawned from the
   * ModelMenu. Cancels action and closes both the dialog and the ModelMenu.
   */
  public class GenericCancelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenu.this.hide();
      if ((openDialog != null) && (openDialog.isShowing())) {
        openDialog.hide();
      }
      if ((saveDialog != null) && (saveDialog.isShowing())) {
        saveDialog.hide();
      }
      if ((deleteDialog != null) && (deleteDialog.isShowing())) {
        deleteDialog.hide();
      }
      if ((runDialog != null) && (runDialog.isShowing())) {
        runDialog.hide();
      }
    }
  }
}
