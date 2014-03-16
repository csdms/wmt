package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ViewInputFilesDialogBox extends DialogBox {

  public ViewInputFilesDialogBox(String innerHtml) {
    
    super(false); // autohide
    this.setModal(true);
    this.setText("View Input Files");

    HTMLPanel html = new HTMLPanel(innerHtml);
    
    ScrollPanel scrollPanel = new ScrollPanel();
    scrollPanel.setSize("500px", "400px");
    scrollPanel.add(html);

    ChoicePanel choicePanel = new ChoicePanel();
    choicePanel.getOkButton().setHTML("<i class='fa fa-save'></i> Save");

    VerticalPanel panel = new VerticalPanel();
    panel.add(scrollPanel);
    
    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    contents.add(panel);
    contents.add(choicePanel);

    this.setWidget(contents);
    
    choicePanel.getCancelButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        ViewInputFilesDialogBox.this.hide();
      }
    });
  }

}
