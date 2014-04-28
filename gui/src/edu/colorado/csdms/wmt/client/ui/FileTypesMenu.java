/**
 * <License>
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

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataURL;

/**
 * Displays a menu of file types; currently, HTML, plain text and JSON.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class FileTypesMenu extends PopupPanel {

  private DataManager data;
  private String componentId;
  private Boolean useDefaults;
  
  /**
   * Creates a new {@link FileTypesMenu}.
   * 
   * @param data the DataManager object for the WMT session
   * @param componentId the component displayed in the ParameterTable
   * @param useDefaults set to true to show default parameters for component
   */
  public FileTypesMenu(DataManager data, String componentId, Boolean useDefaults) {

    super(true); // autohide
    this.data = data;
    this.componentId = componentId;
    this.useDefaults = useDefaults;
    this.setStyleName("wmt-PopupPanel");

    // A VerticalPanel for the menu items. (PopupPanels have only one child.)
    VerticalPanel menu = new VerticalPanel();
    this.add(menu);

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
