/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 mcflugen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.Constants;
import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.control.DataURL;
import edu.colorado.csdms.wmt.client.ui.handler.DialogCancelHandler;
import edu.colorado.csdms.wmt.client.ui.handler.ModalKeyHandler;

/**
 * A dialog box that shows information about a successfully submitted model
 * run.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class RunInfoDialogBox extends DialogBox {

  private DataManager data;
  private ChoicePanel choicePanel;

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
    contents.getElement().getStyle().setMargin(1.0, Unit.EM);

    String msg = "<h2>Success!</h2><p>You have submitted your model run.</p>";
    HTML msgHtml = new HTML(msg);
    contents.add(msgHtml);

    choicePanel = new ChoicePanel();
    choicePanel.getOkButton().setHTML(Constants.FA_STATUS + "View run status...");
    choicePanel.getCancelButton().setHTML(Constants.FA_BEER + "Close");
    contents.add(choicePanel);

    this.setWidget(contents);

    /*
     * Hides the dialog box.
     */
    DialogCancelHandler cancelHandler = new DialogCancelHandler(this);
    choicePanel.getCancelButton().addClickHandler(cancelHandler);
    
    /*
     * Opens run status page in a new tab.
     */
    ClickHandler okHandler = new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        RunInfoDialogBox.this.hide();
        Window.open(DataURL.showModelRun(RunInfoDialogBox.this.data),
            "runInfoDialog", null);
      }
    };
    choicePanel.getOkButton().addClickHandler(okHandler);

    // Apply standard handlers to "Enter" and "Esc" keys.    
    choicePanel.addDomHandler(new ModalKeyHandler(okHandler, cancelHandler),
        KeyDownEvent.getType());
  }

  public ChoicePanel getChoicePanel() {
    return choicePanel;
  }

  public void setChoicePanel(ChoicePanel choicePanel) {
    this.choicePanel = choicePanel;
  }
}
