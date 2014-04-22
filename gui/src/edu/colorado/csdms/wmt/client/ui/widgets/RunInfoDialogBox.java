package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataURL;
import edu.colorado.csdms.wmt.client.ui.handler.DialogCancelHandler;

/**
 * A dialog box that shows information about a successfully submitted model
 * run.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class RunInfoDialogBox extends DialogBox {

  private DataManager data;
  private ClosePanel closePanel;

  /**
   * Displays a {@link RunInfoDialogBox}.
   */
  public RunInfoDialogBox(DataManager data) {

    super(true); // autohide
    this.setModal(true);
    this.setStyleName("wmt-DialogBox");
    this.setText("Model Run Information");
    this.data = data;

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    contents.setWidth("30em");

    String msg = "<h2>Success!</h2><p>You have submitted your model run.</p>"
          + "<p>Check the status of the run by selecting the Run status "
          + "button, or by visiting:</p>"
          + "<a href='" + DataURL.showModelRun(data) + "'>"
          + DataURL.showModelRun(data) + "</a>";
    HTML msgHtml = new HTML(msg);
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
        Window.open(DataURL.showModelRun(RunInfoDialogBox.this.data),
            "runInfoDialog", null);
      }
    });
  }
}
