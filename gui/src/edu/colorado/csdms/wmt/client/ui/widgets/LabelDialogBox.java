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

import java.util.Map;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.control.DataManager;
import edu.colorado.csdms.wmt.client.data.LabelJSO;

/**
 * A customized DialogBox with a {@link SuggestBox} for entering a label and a
 * {@link ChoicePanel} displaying "OK" and "Cancel" buttons.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LabelDialogBox extends DialogBox {

  @SuppressWarnings("unused")
  private DataManager data;
  private SuggestBox suggestBox;
  private ChoicePanel choicePanel;
  
  /**
   * Makes a new {@link LabelDialogBox}.
   * 
   * @param data the DataManager object for the WMT session
   */
  public LabelDialogBox(DataManager data) {

    super(false); // autohide
    this.setModal(true);
    this.data = data;

    // OMG is this fun!
    MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
    for (Map.Entry<String, LabelJSO> entry : data.modelLabels.entrySet()) {
      oracle.add(entry.getKey());
    }
    
    suggestBox = new SuggestBox(oracle);
    choicePanel = new ChoicePanel();

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.add(suggestBox);
    contents.add(choicePanel);

    this.setWidget(contents);
  }

  public SuggestBox getSuggestBox() {
    return suggestBox;
  }

  public void setSuggestBox(SuggestBox box) {
    this.suggestBox = box;
  }

  public ChoicePanel getChoicePanel() {
    return choicePanel;
  }

  public void setChoicePanel(ChoicePanel choicePanel) {
    this.choicePanel = choicePanel;
  }
}
