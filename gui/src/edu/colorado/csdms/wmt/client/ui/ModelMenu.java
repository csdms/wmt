/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

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

import edu.colorado.csdms.wmt.client.data.DataTransfer;

/**
 * Encapsulates a menu for operations on Models -- e.g., New, Open, Close and
 * Save -- as well as a "hamburger" icon to show/hide this menu. The icon and
 * menu are positioned in the upper right corner of the Arena view.
 * <p>
 * Menu item names, icons and grouping are modeled on Word, Eclipse and Chrome.
 * 
 * @see http://fortawesome.github.io/Font-Awesome/
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelMenu extends DecoratedPopupPanel {

  private DataManager data;
  private HTML menuButton;
  private SaveDialogBox saveDialog;
  private OpenDialogBox openDialog;

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
    ModelMenuItem runModel =
        new ModelMenuItem("Run Model...", "fa-play");
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
    menu.setWidget(menuIndex++, 0, new ModelMenuItem());
    menu.setWidget(menuIndex++, 0, runModel);
    menu.setWidget(menuIndex++, 0, new ModelMenuItem());
    menu.setWidget(menuIndex++, 0, helpButton);
    menu.setWidget(menuIndex++, 0, aboutButton);

    // Associate event handlers for the menu items.
    newModel.addClickHandler(new NewModelHandler());
    openModel.addClickHandler(new OpenModelHandler());
    closeModel.addClickHandler(new CloseModelHandler());
    saveModel.addClickHandler(new SaveModelHandler());
    saveModelAs.addClickHandler(new SaveModelAsHandler());
    runModel.addClickHandler(new RunModelHandler());
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
     * The zero-element constructor makes a separator from an HTML "hr" element.
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
   * Handles click on the "New Model" button in the ModelMenu.
   */
  public class NewModelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenuItem item = (ModelMenuItem) event.getSource();
      ModelMenu.this.hide();
      Window.alert("Clicked on: " + item.getText(0, 0));
      // ModelMenu.this.data.getModelTree().clear();
      // ModelMenu.this.data.getParameterTable().clearTable();
      // ModelMenu.this.data.getModelTree().initializeTree();
      // ModelMenu.this.data.getComponentList().setCellSensitivity();
    }
  }

  /**
   * Handles click on the "Open Model..." button in the ModelMenu.
   */
  public class OpenModelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {

      openDialog = new OpenDialogBox();
      openDialog.setText("Open Model...");

      // Populate the ModelDroplist with the available models on the server.
      for (int i = 0; i < data.modelNameList.size(); i++) {
        openDialog.getModelPanel().getModelDroplist().addItem(
            data.modelNameList.get(i));
      }

      openDialog.getChoicePanel().getOkButton().addClickHandler(
          new OpenOkHandler());
      openDialog.getChoicePanel().getCancelButton().addClickHandler(
          new OpenCancelHandler());

      openDialog.center();
      ModelMenu.this.hide();
    }
  }

  /**
   * Handles click on the "Close Model" button in the ModelMenu.
   */
  public class CloseModelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenuItem item = (ModelMenuItem) event.getSource();
      ModelMenu.this.hide();
      Window.alert("Clicked on: " + item.getText(0, 1));
      // ModelMenu.this.data.getModelTree().clear();
      // ModelMenu.this.data.getParameterTable().clearTable();
      // ModelMenu.this.data.getComponentList().setCellSensitivity();
    }
  }

  /**
   * Handles click on the "Save Model" button in the ModelMenu.
   */
  public class SaveModelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenuItem item = (ModelMenuItem) event.getSource();
      ModelMenu.this.hide();
      Window.alert("Clicked on: " + item.getText(0, 1));
    }
  }

  /**
   * Handles click on the "Save Model As..." button in the ModelMenu.
   */
  public class SaveModelAsHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {

      saveDialog = new SaveDialogBox();
      saveDialog.setText("Save Model As...");
      saveDialog.getFilePanel().setDirectory("river:$HOME/.wmt");
      saveDialog.getFilePanel().setFile("MyModel");

      saveDialog.getChoicePanel().getOkButton().addClickHandler(
          new SaveOkHandler());
      saveDialog.getChoicePanel().getCancelButton().addClickHandler(
          new SaveCancelHandler());

      saveDialog.center();
      ModelMenu.this.hide();
    }  
  }

  /**
   * Handles click on the "Run" button in the ModelMenu.
   */
  public class RunModelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      ModelMenuItem item = (ModelMenuItem) event.getSource();
      ModelMenu.this.hide();
      Window.alert("Clicked on: " + item.getText(0, 1));
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
   * "Open Model..." button is clicked in the ModelMenu.
   */
  public class OpenOkHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {

      openDialog.hide();
      ModelMenu.this.hide();

      // Get the selected item from the openDialog. Problem: This feels
      // fragile. I'm using the index of the selected modelName to match up
      // the index of the modelId. This should work consistently because I add
      // the modelId and modelName to the ArrayList with the same index. It
      // would be better if they both resided in the same data structure.
      Integer selIndex =
          openDialog.getModelPanel().getModelDroplist().getSelectedIndex();
      Integer modelId = data.modelIdList.get(selIndex);

      // Get the data + metadata for the selected model.
      DataTransfer.getModel(data, modelId);
    }
  }

  /**
   * Handles click on the "Cancel" button in the open dialog that appears when
   * the "Open Model..." button is clicked in the ModelMenu.
   */
  public class OpenCancelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      openDialog.hide();
      ModelMenu.this.hide();
    }
  }  
  
  /**
   * Handles click on the "OK" button in the save dialog that appears when the
   * "Save Model As..." button is clicked in the ModelMenu.
   */
  public class SaveOkHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {

      saveDialog.hide();
      ModelMenu.this.hide();

      // Set the model name on the ViewCenter tab and in the DataManager.
      String modelName = saveDialog.getFilePanel().getFile();
      data.getModel().setName(modelName);
      String tabTitle = "Model (" + data.getModel().getName() + ")";
      data.getPerspective().getViewCenter().setTabText(0, tabTitle);

      DataTransfer.serialize(data);
      
      // Update list of saved models in the WMT DataManager.
      DataTransfer.getModelList(data);
    }
  }

  /**
   * Handles click on the "Cancel" button in the save dialog that appears when
   * the "Save Model As..." button is clicked in the ModelMenu.
   */
  public class SaveCancelHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {
      saveDialog.hide();
      ModelMenu.this.hide();
    }
  }

}
