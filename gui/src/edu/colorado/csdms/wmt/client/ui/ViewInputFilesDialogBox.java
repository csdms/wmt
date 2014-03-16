package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.control.DataTransfer;
import edu.colorado.csdms.wmt.client.data.ModelComponentParametersJSO;

public class ViewInputFilesDialogBox extends DialogBox {

  public ViewInputFilesDialogBox(ModelComponentParametersJSO jso, String type) {

    super(false); // autohide
    this.setModal(false);
    this.setText("View Input Files");

    Integer nKeys = jso.getKeys().length();
    String content;
    if (nKeys == 0) {
      content = "No files are available for this component";
    } else {
      String key = jso.getKeys().get(0);
      String value = jso.getValues().get(0);
      if (type.matches("text")) {
        content =
            "<p><pre>" + key + "</pre></p>" + "<p><pre>" + value + "</pre></p>";
      } else if (type.matches("html")) {
        content = "<h2>" + key + "</h2>" + "<p><pre>" + value + "</pre></p>";
      } else {
        content = DataTransfer.stringify(jso);
      }
    }
    HTML html = new HTML(content);

    ScrollPanel scrollPanel = new ScrollPanel();
    scrollPanel.setSize("500px", "400px");
    scrollPanel.add(html);

    ChoicePanel choicePanel = new ChoicePanel();
    choicePanel.getOkButton().setHTML("<i class='fa fa-save'></i> Save");

    VerticalPanel panel = new VerticalPanel();
    panel.add(scrollPanel);

    VerticalPanel mainPanel = new VerticalPanel();
    mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    mainPanel.add(panel);
    mainPanel.add(choicePanel);

    this.setWidget(mainPanel);

    choicePanel.getCancelButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        ViewInputFilesDialogBox.this.hide();
      }
    });
  }

}
