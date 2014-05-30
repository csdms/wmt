/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 mcflugen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.colorado.csdms.wmt.client.ui;

import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.colorado.csdms.wmt.client.Constants;
import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataURL;

/**
 * Shows a menu that allows a user to select to view the input files generated
 * by the current parameter settings, or the default settings for a component.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ViewInputFilesMenu extends PopupPanel {

  private DataManager data;
  private String componentId;
  private VerticalPanel menu;
  private FileTypesMenu currentMenu;
  private FileTypesMenu defaultsMenu;
  
  /**
   * Creates a new {@link ViewInputFilesMenu}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public ViewInputFilesMenu(DataManager data, String componentId) {

    super(true); // autohide
    this.data = data;
    this.componentId = componentId;
    this.setStyleName("wmt-PopupPanel");

    // A VerticalPanel for the menu items. (PopupPanels have only one child.)
    menu = new VerticalPanel();
    this.add(menu);

    populateMenu();
  }

  /**
   * A helper that fills in the {@link ViewInputFilesMenu}.
   */
  public void populateMenu() {

    menu.clear();
    
    // Current model
    final HTML currentButton = new HTML(Constants.FA_COGS + "Current model");
    currentButton.setStyleName("wmt-PopupPanelItem");
    currentButton.setStyleDependentName("disabled", !data.modelIsSaved());
    currentButton.setTitle(Constants.PARAMETER_VIEW_CURRENT);
    menu.add(currentButton);
    currentMenu = new FileTypesMenu(false);
    currentButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        if (!data.modelIsSaved()) {
          return;
        }
        currentMenu.setPopupPositionAndShow(new PositionCallback() {
          final Integer x = currentButton.getElement().getAbsoluteRight();
          final Integer y = currentButton.getAbsoluteTop();
          @Override
          public void setPosition(int offsetWidth, int offsetHeight) {
            currentMenu.setPopupPosition(x, y);
          }
        });
      }
    });

    // Defaults for component
    final HTML defaultsButton =
        new HTML(Constants.FA_COG + "Defaults for component");
    defaultsButton.setStyleName("wmt-PopupPanelItem");
    defaultsButton.setTitle(Constants.PARAMETER_VIEW_DEFAULT);
    menu.add(defaultsButton);
    defaultsMenu = new FileTypesMenu(true);
    defaultsButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        defaultsMenu.setPopupPositionAndShow(new PositionCallback() {
          final Integer x = defaultsButton.getElement().getAbsoluteRight();
          final Integer y = defaultsButton.getAbsoluteTop();
          @Override
          public void setPosition(int offsetWidth, int offsetHeight) {
            defaultsMenu.setPopupPosition(x, y);
          }
        });
      }
    });
  }
  
  /**
   * Displays a menu of file types; currently, HTML, plain text and JSON.
   */
  public class FileTypesMenu extends PopupPanel {

    private Boolean useDefaults;
    private VerticalPanel menu;

    /**
     * Creates a new {@link FileTypesMenu}.
     * 
     * @param useDefaults set to true to show default parameters for component
     */
    public FileTypesMenu(Boolean useDefaults) {

      super(true); // autohide
      this.useDefaults = useDefaults;
      this.setStyleName("wmt-PopupPanel");

      // A VerticalPanel for the menu items. (PopupPanels have only one child.)
      menu = new VerticalPanel();
      this.add(menu);

      populateMenu();
    }

    /**
     * A helper used to load the {@link FileTypesMenu}.
     */
    public void populateMenu() {

      // HTML
      HTML htmlButton = new HTML("HTML");
      htmlButton.addClickHandler(new FileTypesClickHandler("html"));
      menu.add(htmlButton);

      // Text
      HTML textButton = new HTML("Plain text");
      textButton.addClickHandler(new FileTypesClickHandler("text"));
      menu.add(textButton);

      // JSON
      HTML jsonButton = new HTML("JSON");
      jsonButton.addClickHandler(new FileTypesClickHandler("json"));
      menu.add(jsonButton);

      // Apply a style to each button.
      Iterator<Widget> iter = menu.iterator();
      while (iter.hasNext()) {
        HTML button = (HTML) iter.next();
        button.setStyleName("wmt-PopupPanelItem");
      }
    }

    /**
     * Handles a click in the {@link FileTypesMenu}.
     */
    public class FileTypesClickHandler implements ClickHandler {

      private String type;

      public FileTypesClickHandler(String type) {
        this.type = type;
      }

      @Override
      public void onClick(ClickEvent event) {
        FileTypesMenu.this.hide();
        ViewInputFilesMenu.this.hide();
        if (!useDefaults && !data.modelIsSaved()) {
          Window.alert("Model must be saved to view current input files.");
          return;
        }
        String url =
            DataURL.formatComponent(data, componentId, type, useDefaults);
        Window.open(url, "_blank", null);
      }
    }
  }
}
