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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.Constants;
import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.ui.LabelsSaveModelMenu;

/**
 * A customized DialogBox with a field for setting the name/description of the
 * model, and a "Labels" button for applying labels to the model (used for
 * filtering). "OK" and "Cancel" buttons are shown on the bottom of the dialog.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class SaveDialogBox extends DialogBox {
  
  @SuppressWarnings("unused")
  private DataManager data;
  private FieldPanel namePanel;
  private ChoicePanel choicePanel;
  private LabelsSaveModelMenu labelsMenu;
  
  /**
   * Makes a {@link SaveDialogBox} with a default name.
   * 
   * @param data the DataManager object for the WMT session
   */
  public SaveDialogBox(DataManager data) {
    this(data, Constants.DEFAULT_MODEL_NAME);
  }
  
  /**
   * Makes a SaveDialogBox with a user-supplied name.
   * 
   * @param data the DataManager object for the WMT session
   * @param modelName a descriptive name for the model
   */
  public SaveDialogBox(DataManager data, String modelName) {

    super(false); // autohide
    this.setModal(true);
    this.setText("Save Model As...");
    this.setStyleName("wmt-DialogBox");
    this.data = data;

    namePanel = new FieldPanel(modelName);

    final Button labelsButton = new Button(Constants.FA_TAGS + "Labels");
    labelsButton.setStyleName("wmt-Button");
    labelsMenu = new LabelsSaveModelMenu(data);
    labelsButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        labelsMenu.setPopupPositionAndShow(new PositionCallback() {
          final Integer x = labelsButton.getElement().getAbsoluteRight();
          final Integer y = labelsButton.getAbsoluteTop();
          @Override
          public void setPosition(int offsetWidth, int offsetHeight) {
            labelsMenu.setPopupPosition(x, y);
          }
        });
      }
    });

    HorizontalPanel row = new HorizontalPanel();
    row.setSpacing(5); // px
    row.add(namePanel);
    row.add(labelsButton);
    row.setCellVerticalAlignment(labelsButton,
        HasVerticalAlignment.ALIGN_MIDDLE);

    choicePanel = new ChoicePanel();
    choicePanel.getOkButton().setHTML(Constants.FA_SAVE + "Save");

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.add(row);
    contents.add(choicePanel);

    this.setWidget(contents);
  }

  public FieldPanel getNamePanel() {
    return namePanel;
  }

  public void setNamePanel(FieldPanel namePanel) {
    this.namePanel = namePanel;
  }

  public ChoicePanel getChoicePanel() {
    return choicePanel;
  }

  public void setChoicePanel(ChoicePanel choicePanel) {
    this.choicePanel = choicePanel;
  }
}
