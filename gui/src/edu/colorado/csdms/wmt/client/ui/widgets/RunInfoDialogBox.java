package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
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
          + "<a href='" + DataURL.showModelRun() + "'>"
          + DataURL.showModelRun() + "</a>";
  private ClosePanel closePanel;

  /**
   * Displays a {@link RunInfoDialogBox}.
   */
  public RunInfoDialogBox() {

    super(true); // autohide
    this.setModal(true);
    this.setText("Model Run Information");

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    contents.setWidth("30em");

    HTML msgHtml = new HTML(MSG);
    contents.add(msgHtml);

    closePanel = new ClosePanel();
    closePanel.getButton().setHTML("<i class='fa fa-beer'></i> Close");
    contents.add(closePanel);

    this.setWidget(contents);

    /*
     * Hides the dialog box.
     */
    closePanel.getButton().addClickHandler(new DialogCancelHandler(this));
    
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
