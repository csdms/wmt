package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.control.DataURL;
import edu.colorado.csdms.wmt.client.ui.handler.DialogCancelHandler;

/**
 * A dialog box that shows information about a successfully submitted model
 * run.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class RunInfoDialogBox extends DialogBox {

  private static String MSG =
      "<h2>Success!</h2><p>You have submitted your model run.</p>"
          + "<p>Check the status of the run by selecting "
          + "<b>View Run Status...</b> under the "
          + "<i class='fa fa-bars fa-lg'></i> menu, or by visiting:</p>"
          + "<a href='https://csdms.colorado.edu/wmt/run/show'>"
          + "https://csdms.colorado.edu/wmt/run/show</a>";

  /**
   * Displays a {@link RunInfoDialogBox}.
   */
  public RunInfoDialogBox() {

    super(true); // autohide
    this.setModal(true);
    this.setText("Model Run Information");

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    contents.setWidth("25em");

    HTML msgHtml = new HTML(MSG);
    contents.add(msgHtml);

    Button closeButton = new Button("<i class='fa fa-beer'></i> Close");
    closeButton.getElement().getStyle().setMarginTop(1, Unit.EM);
    closeButton.getElement().getStyle().setMarginBottom(0.5, Unit.EM);
    contents.add(closeButton);

    this.setWidget(contents);

    /*
     * Hides the dialog box.
     */
    closeButton.addClickHandler(new DialogCancelHandler(this));
    
    /*
     * Intercepts click on link in dialog and opens the URL in a new tab.
     */
    msgHtml.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        event.preventDefault();
        Window.open(DataURL.showModelRun(), "runInfoDialog", null);
      }
    });
  }
}
