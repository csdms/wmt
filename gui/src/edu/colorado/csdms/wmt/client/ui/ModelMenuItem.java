/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;

/**
 * A class for making the menu items that appear on the {@link ModelMenu}.
 * 
 * @see http://fortawesome.github.io/Font-Awesome/
 * @author Mark Piper (mark.piper@colorado.edu)
 */
@Deprecated
public class ModelMenuItem extends Grid {

  /**
   * The zero-element constructor makes a separator.
   */
  public ModelMenuItem() {
    super(1, 1);
    this.setWidget(0, 0, new HTML(""));
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
    this.setStyleName("wmt-ModelMenuItem");
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
    this.setStyleName("wmt-ModelMenuItem");
  }
}
