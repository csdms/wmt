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
 * A GWT composite widget that defines a box listing files and a box showing
 * which file is selected.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DroplistPanel extends Composite {

  private ListBox modelDroplist;

  /**
   * Defines an empty DroplistPanel.
   */
  public DroplistPanel() {
    this(null);
  }

  /**
   * Defines a ListBox with a droplist of model names.
   * 
   * @param modelNames a String[] of model names
   */
  public DroplistPanel(String[] modelNames) {

    Label availableLabel = new Label("Available models:");
    modelDroplist = new ListBox(false); // multiselect off
    if (modelNames != null) {
      for (int i = 0; i < modelNames.length; i++) {
        modelDroplist.addItem(modelNames[i]);
      }
    }
    modelDroplist.setVisibleItemCount(1); // show 1 item = a droplist

    // Styles!
    modelDroplist.setWidth("25ch");
    availableLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

    HorizontalPanel contents = new HorizontalPanel();
    contents.setSpacing(10); // px
    contents.add(availableLabel);
    contents.add(modelDroplist);

    initWidget(contents);
  }

  public ListBox getModelDroplist() {
    return modelDroplist;
  }

  public void setModelDroplist(ListBox modelDroplist) {
    this.modelDroplist = modelDroplist;
  }
}
