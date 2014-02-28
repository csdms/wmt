/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A GWT composite widget that defines a label and a droplist of items.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DroplistPanel extends Composite {

  private Label dropLabel;
  private ListBox droplist;

  /**
   * Defines an empty DroplistPanel.
   */
  public DroplistPanel() {
    this(null);
  }

  /**
   * Defines a DroplistPanel populated with a set of input items.
   * 
   * @param items a String[] of items to display in the droplist
   */
  public DroplistPanel(String[] items) {

    dropLabel = new Label("Available models:");
    droplist = new ListBox(false); // multiselect off
    if (items != null) {
      for (int i = 0; i < items.length; i++) {
        droplist.addItem(items[i]);
      }
    }
    droplist.setVisibleItemCount(1); // show 1 item = a droplist

    // Styles!
    droplist.setWidth("25ch");
    dropLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

    HorizontalPanel contents = new HorizontalPanel();
    contents.setSpacing(10); // px
    contents.add(dropLabel);
    contents.add(droplist);

    initWidget(contents);
  }

  public Label getLabel() {
    return dropLabel;
  }

  public void setLabel(Label label) {
    this.dropLabel = label;
  }

  public ListBox getDroplist() {
    return droplist;
  }

  public void setDroplist(ListBox droplist) {
    this.droplist = droplist;
  }
}
