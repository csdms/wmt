/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.data.ComponentJSO;
import edu.colorado.csdms.wmt.client.ui.handler.DialogCancelHandler;

/**
 * A dialog box used to display metadata (id, url, author, etc.) about a WMT
 * component.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentInfoDialogBox extends DialogBox {

  private static String[] LABELS = {
      "id", "summary", "url", "author", "email", "version", "license", "doi"};
  private Grid grid;

  /**
   * Creates an {@link ComponentInfoDialogBox}. It must be populated later by
   * calling {@link ComponentInfoDialogBox#update()}.
   */
  public ComponentInfoDialogBox() {

    this.setAutoHideEnabled(false);
    this.setModal(false);

    grid = new Grid(LABELS.length, 2);
    grid.setCellPadding(5); // px

    for (int i = 0; i < LABELS.length; i++) {
      Label label = new Label(LABELS[i] + ":");
      label.getElement().getStyle().setMarginLeft(2, Unit.EM);
      grid.setWidget(i, 0, label);
      grid.getCellFormatter().setHorizontalAlignment(i, 0,
          HasHorizontalAlignment.ALIGN_RIGHT);
    }

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    contents.add(grid);
    contents.setWidth("40em");

    Button closeButton = new Button("<i class='fa fa-times'></i> Close");
    closeButton.getElement().getStyle().setMarginTop(1, Unit.EM);
    closeButton.getElement().getStyle().setMarginBottom(0.5, Unit.EM);

    HorizontalPanel buttonPanel = new HorizontalPanel();
    buttonPanel.setWidth("100%");
    buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    buttonPanel.add(closeButton);
    contents.add(buttonPanel);

    this.setWidget(contents);

    // Hides the dialog box.
    closeButton.addClickHandler(new DialogCancelHandler(this));
  }

  /**
   * Updates the {@link ComponentInfoDialogBox} with information from the
   * input component.
   * 
   * @param componentJso a {@link ComponentJSO} instance
   */
  public void update(final ComponentJSO componentJso) {

    this.setText(componentJso.getName());

    String url =
        "<a href='" + componentJso.getURL() + "'>" + componentJso.getURL()
            + "</a>";
    String[] info =
        {
            componentJso.getId(), componentJso.getSummary(), url,
            componentJso.getAuthor(), componentJso.getEmail(),
            componentJso.getVersion(), componentJso.getLicense(),
            componentJso.getDoi()};

    HTML urlHtml = null;
    for (int i = 0; i < LABELS.length; i++) {
      if (url.equals(info[i])) {
        urlHtml = new HTML(info[i]);
        grid.setWidget(i, 1, urlHtml);
      } else {
        grid.setWidget(i, 1, new HTML(info[i]));
      }
    }

    /*
     *  Intercept the click on the component URL and open it in a new tab.
     */
    urlHtml.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        event.preventDefault();
        Window.open(componentJso.getURL(), "componentInfoDialog", null);
      }
    });
  }
}
