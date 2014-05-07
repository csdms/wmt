/**
 * <License>
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
