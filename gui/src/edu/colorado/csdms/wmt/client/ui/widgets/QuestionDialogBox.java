/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A {@link DialogBox} that prompts the user with a yes/no question.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class QuestionDialogBox extends DialogBox {

  private HTML questionHtml;
  private ChoicePanel choicePanel;
  
  /**
   * Makes a new {@link QuestionDialogBox}.
   * 
   * @param question the question to be displayed, a String
   */
  public QuestionDialogBox(String question) {

    super(false); // autohide
    this.setModal(true);
    this.setText("Question");
    
    setQuestionHtml(new HTML(question));
    setChoicePanel(new ChoicePanel());
    
    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.add(questionHtml);
    contents.add(choicePanel);

    questionHtml.getElement().getStyle().setPadding(1, Unit.EM);
    
    this.setWidget(contents);    
  }

  public HTML getQuestionHtml() {
    return questionHtml;
  }

  public void setQuestionHtml(HTML questionHtml) {
    this.questionHtml = questionHtml;
  }

  public ChoicePanel getChoicePanel() {
    return choicePanel;
  }

  public void setChoicePanel(ChoicePanel choicePanel) {
    this.choicePanel = choicePanel;
  }
}
