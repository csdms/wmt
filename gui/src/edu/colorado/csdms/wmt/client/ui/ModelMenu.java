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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.control.DataURL;
import edu.colorado.csdms.wmt.client.ui.handler.DeleteModelHandler;
import edu.colorado.csdms.wmt.client.ui.handler.DialogCancelHandler;
import edu.colorado.csdms.wmt.client.ui.handler.OpenModelHandler;
import edu.colorado.csdms.wmt.client.ui.handler.SaveModelHandler;
import edu.colorado.csdms.wmt.client.ui.handler.SetupRunModelHandler;
import edu.colorado.csdms.wmt.client.ui.widgets.DroplistDialogBox;
import edu.colorado.csdms.wmt.client.ui.widgets.SaveDialogBox;

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
@Deprecated
public class ModelMenu extends PopupPanel {

  private DataManager data;
  private HTML menuButton;
  private SaveDialogBox saveDialog;
  private DroplistDialogBox openDialog;
  private DroplistDialogBox deleteDialog;

  /**
   * Sets up the Model menu, including all its menu items, as well as its
   * "hamburger" icon. The menu is only shown when the icon is clicked.
   * 
   * @param data the DataManager object for the WMT session
   */
  public ModelMenu(DataManager data) {

    super(true); // autohide
    this.getElement().getStyle().setCursor(Cursor.POINTER); // use pointer
    this.data = data;
    this.setStyleName("wmt-ModelMenu");

    // A FlexTable for the menu items. (PopupPanels can have only one child.)
    FlexTable menu = new FlexTable();
    this.add(menu);

    // The items to display on the Model menu.
    ModelMenuItem newModel = new ModelMenuItem("New Model");
    ModelMenuItem openModel =
        new ModelMenuItem("Open Model...", "fa-folder-open-o");
    ModelMenuItem closeModel = new ModelMenuItem("Close Model", "fa-folder-o");
    ModelMenuItem saveModel = new ModelMenuItem("Save Model", "fa-floppy-o");
    ModelMenuItem saveModelAs =
        new ModelMenuItem("Save Model As...", "fa-floppy-o");
    ModelMenuItem deleteModel =
        new ModelMenuItem("Delete Model...", "fa-trash-o");
    ModelMenuItem runModel = new ModelMenuItem("Run Model...", "fa-play");
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
    newModel.addClickHandler(new MenuNewModelHandler());
    openModel.addClickHandler(new MenuOpenModelHandler());
    closeModel.addClickHandler(new MenuCloseModelHandler());
    saveModel.addClickHandler(new MenuSaveModelHandler());
    saveModelAs.addClickHandler(new MenuSaveModelAsHandler());
    deleteModel.addClickHandler(new MenuDeleteModelHandler());
    runModel.addClickHandler(new SetupRunModelHandler(data));
    runStatus.addClickHandler(new MenuRunStatusHandler());
    helpButton.addClickHandler(new MenuHelpHandler());
    aboutButton.addClickHandler(new MenuAboutHandler());

    // Set up, but don't display, the "hamburger" icon for the Model menu.
    menuButton = new HTML("<i class='fa fa-bars fa-2x'></i>");
    menuButton.setStyleName("wmt-ModelMenuButton");
    menuButton
        .setTitle("Use this menu to open, close, save, delete or run a model.");

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
        final Integer iconHalfWidth = 11; // by inspection
        ModelMenu.this
            .setPopupPositionAndShow(new PopupPanel.PositionCallback() {
              @Override
              public void setPosition(int offsetWidth, int offsetHeight) {
                ModelMenu.this.setPopupPosition(
                    x - offsetWidth - iconHalfWidth, y);
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
   * Handles click on the "New Model" button in the ModelMenu. Opens a new
   * instance of WMT in a new browser tab.
   */
  public class MenuNewModelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenu.this.hide();
      Window.open(DataURL.applicationURL(data), "_blank", null);
    }
  }

  /**
   * Handles click on the "Open Model..." button in the ModelMenu. Pops up an
   * instance of {@link DroplistDialogBox} to prompt the user for a model to
   * open. Events are sent to {@link OpenModelHandler} and
   * {@link GenericCancelHandler}.
   */
  public class MenuOpenModelHandler implements ClickHandler {
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
          new OpenModelHandler(data, openDialog));
      openDialog.getChoicePanel().getCancelButton().addClickHandler(
          new DialogCancelHandler(openDialog));

      openDialog.center();
      ModelMenu.this.hide();
    }
  }

  /**
   * Handles click on the "Close Model" button in the ModelMenu. Resets the
   * WMT GUI to its default state with a call to {@link Perspective#reset()}.
   */
  public class MenuCloseModelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenu.this.hide();
      data.getPerspective().reset();
    }
  }

  /**
   * Handles click on the "Save Model" button in the ModelMenu. Saves a
   * not-previously-saved model or a new model displayed in WMT to the server
   * with a call to {@link DataTransfer#postModel(DataManager)}.
   */
  public class MenuSaveModelHandler implements ClickHandler {
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
  public class MenuSaveModelAsHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenu.this.hide();
      showSaveDialogBox();
    }
  }

  /**
   * Pops up an instance of {@link SaveDialogBox} to prompt the user to save
   * the model. Events are sent to {@link SaveModelHandler} and
   * {@link GenericCancelHandler}.
   */
  private void showSaveDialogBox() {
    saveDialog = new SaveDialogBox(data.getModel().getName());
    saveDialog.getNamePanel().setTitle(
        "Enter a name for the model. No file extension is needed.");
    saveDialog.getChoicePanel().getOkButton().addClickHandler(
        new SaveModelHandler(data, saveDialog));
    saveDialog.getChoicePanel().getCancelButton().addClickHandler(
        new DialogCancelHandler(saveDialog));
    saveDialog.center();
  }

  /**
   * Handles click on the "Delete" button in the ModelMenu. It presents an
   * instance of {@link DroplistDialogBox} with a "Delete" button.
   */
  public class MenuDeleteModelHandler implements ClickHandler {
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
          new DeleteModelHandler(data, deleteDialog));
      deleteDialog.getChoicePanel().getCancelButton().addClickHandler(
          new DialogCancelHandler(deleteDialog));

      deleteDialog.center();
      ModelMenu.this.hide();      
    }
  }

  /**
   * Handles click on the "View Run Status..." button in the ModelMenu.
   * Displays the API "run/show" page showing the status of all currently
   * running models.
   */
  public class MenuRunStatusHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenu.this.hide();
      Window.open(DataURL.showModelRun(data), "runInfoDialog", null);
    }
  }

  /**
   * Handles click on the "Help" button in the ModelMenu.
   */
  public class MenuHelpHandler implements ClickHandler {
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
  public class MenuAboutHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenuItem item = (ModelMenuItem) event.getSource();
      ModelMenu.this.hide();
      Window.alert("Clicked on: " + item.getText(0, 0));
    }
  }
}
