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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
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

  private static String[] LABELS = {"summary", "url", "author"};
  private Grid grid;
  private ClosePanel closePanel;

  /**
   * Creates an {@link ComponentInfoDialogBox}. It must be populated later by
   * calling {@link ComponentInfoDialogBox#update()}.
   */
  public ComponentInfoDialogBox() {

    this.setAutoHideEnabled(false);
    this.setModal(false);
    this.setStyleName("wmt-DialogBox");
        
    grid = new Grid(LABELS.length, 1);
    grid.setCellPadding(5); // px

    closePanel = new ClosePanel();
    
    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    contents.setWidth("35em");
    contents.add(grid);
    contents.add(closePanel);

    this.setWidget(contents);

    /*
     * Hides the dialog box.
     */
    closePanel.getButton().addClickHandler(new DialogCancelHandler(this));
  }

  /**
   * Updates the {@link ComponentInfoDialogBox} with information from the
   * input component.
   * 
   * @param componentJso a {@link ComponentJSO} instance
   */
  public void update(final ComponentJSO componentJso) {

    String title = componentJso.getName();
    if (componentJso.getVersion() != null) {
      title += " v" + componentJso.getVersion();
    }
    if (componentJso.getDoi() != null) {
      title += " (" + componentJso.getDoi() + ")";
    }
    this.setText(title);

    String url = "<a href='" + componentJso.getURL() + "'>" 
        + componentJso.getURL() + "</a>";
    String author = (componentJso.getAuthor() == null) 
        ? " " : componentJso.getAuthor();
    String[] info = {componentJso.getSummary(), url, 
        "Model developer: " + author};

    HTML urlHtml = null;
    for (int i = 0; i < LABELS.length; i++) {
      if (url.equals(info[i])) {
        urlHtml = new HTML(info[i]);
        grid.setWidget(i, 0, urlHtml);
      } else {
        grid.setWidget(i, 0, new HTML(info[i]));
      }
    }

    /*
     *  Intercepts the click on the component URL and opens it in a new tab.
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
