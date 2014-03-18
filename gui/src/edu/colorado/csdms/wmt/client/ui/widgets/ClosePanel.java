/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Makes a {@link HorizontalPanel} with a centered "Close" button. Reusable!
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ClosePanel extends HorizontalPanel {

  private Button button;
  
  /**
   * Makes a new {@link ClosePanel} with a centered "Close" button.
   */
  public ClosePanel() {

    this.setWidth("100%");
    this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

    button = new Button("<i class='fa fa-times'></i> Close");
    button.getElement().getStyle().setMarginTop(1, Unit.EM);
    button.getElement().getStyle().setMarginBottom(0.5, Unit.EM);

    this.add(button);
  }

  public Button getButton() {
    return button;
  }

  public void setButton(Button button) {
    this.button = button;
  }
}
