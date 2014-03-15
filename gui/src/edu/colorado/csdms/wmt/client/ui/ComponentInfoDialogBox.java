/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.data.ComponentJSO;

/**
 * A dialog box used to display metadata (id, url, author, etc.) about a WMT
 * component.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentInfoDialogBox extends DialogBox {

  /**
   * Creates a {@link ComponentInfoDialogBox}.
   * 
   * @param componentJso a reference for the component to be displayed
   */
  public ComponentInfoDialogBox(ComponentJSO componentJso) {

    super(true); // autohide
    this.setModal(false);
    this.setText(componentJso.getName());
    this.setWidth("20em");

    String[] labels =
        {"id", "summary", "url", "author", "email", "version", "license", "doi"};
    String[] info =
        {componentJso.getId(), componentJso.getSummary(),
         componentJso.getURL(), componentJso.getAuthor(),
         componentJso.getEmail(), componentJso.getVersion(),
         componentJso.getLicense(), componentJso.getDoi()};

    Grid componentInfo = new Grid(labels.length, 2);
    componentInfo.setCellPadding(5); // px

    for (int i = 0; i < labels.length; i++) {
      componentInfo.setWidget(i, 0, new Label(labels[i] + ":"));
      componentInfo.getCellFormatter().setHorizontalAlignment(i, 0,
          HasHorizontalAlignment.ALIGN_RIGHT);
      componentInfo.setWidget(i, 1, new HTML(info[i]));
    }

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    contents.add(componentInfo);

    this.setWidget(contents);
  }
}
