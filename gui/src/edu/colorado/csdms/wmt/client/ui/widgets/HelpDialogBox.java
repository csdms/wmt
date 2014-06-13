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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.Constants;
import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.handler.DialogCancelHandler;

/**
 * A dialog box that shows information about WMT.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class HelpDialogBox extends DialogBox {

  @SuppressWarnings("unused")
  private DataManager data;
  private ClosePanel closePanel;

  /**
   * Displays a {@link HelpDialogBox}.
   */
  public HelpDialogBox(DataManager data) {

    super(false); // autohide
    this.setModal(true);
    this.setStyleName("wmt-DialogBox");
    this.setText("Help / About WMT");
    this.data = data;

    VerticalPanel contents = new VerticalPanel();
    contents.setWidth("30em");
    contents.getElement().getStyle().setMargin(1.0, Unit.EM);

    String title1 = "<h2>WMT</h2>";
    String title2 =
        "<p><b>The CSDMS Web Modeling Tool</b></br>Version: "
            + DataManager.VERSION + "</p>";
    String website =
        "<p>For more information on CSDMS, please visit our <a href='"
            + Constants.CSDMS_HOME + "'>website</a>, or <a href='mailto:"
            + Constants.CSDMS_EMAIL + "'>email</a> us.</p>";
    String help =
        "<p>For a detailed description of the WMT interface,"
            + " please see <a href='" + Constants.WMT_HELP
            + "'>WMT Help</a>.</p>";
    String tutorial =
        "<p>For a brief tutorial, please see <a href='"
            + Constants.WMT_TUTORIAL + "'>WMT Tutorial</a>.</p>";

    HTML title1Html = new HTML(title1);
    HTML title2Html = new HTML(title2);
    HTML websiteHtml = new HTML(website);
    HTML helpHtml = new HTML(help);
    HTML tutorialHtml = new HTML(tutorial);

    contents.add(title1Html);
    contents.add(title2Html);
    contents.add(websiteHtml);
    contents.add(helpHtml);
    contents.add(tutorialHtml);

    contents.setCellHorizontalAlignment(title1Html,
        HasHorizontalAlignment.ALIGN_CENTER);
    contents.setCellHorizontalAlignment(title2Html,
        HasHorizontalAlignment.ALIGN_CENTER);

    closePanel = new ClosePanel();
    contents.add(closePanel);

    this.setWidget(contents);

    /*
     * Hides the dialog box.
     */
    closePanel.getButton().addClickHandler(new DialogCancelHandler(this));

    /*
     * Intercepts a click on a link and opens the URL in a new tab.
     */
    websiteHtml.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        event.preventDefault();
        Window.open(Constants.CSDMS_HOME, "CSDMS", null);
      }
    });
    helpHtml.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        event.preventDefault();
        Window.open(Constants.WMT_HELP, "WMT_help", null);
      }
    });
    tutorialHtml.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        event.preventDefault();
        Window.open(Constants.WMT_TUTORIAL, "WMT_tutorial", null);
      }
    });
  }
}
