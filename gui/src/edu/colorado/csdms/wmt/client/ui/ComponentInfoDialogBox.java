package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.data.ComponentJSO;

public class ComponentInfoDialogBox extends DialogBox {

  public ComponentInfoDialogBox(ComponentJSO componentJso) {

    super(true); // autohide
    this.setModal(false);
    this.setText(componentJso.getName());


    String[] labels =
        {"id", "url", "author", "email", "version", "license", "doi"};

    Grid componentInfo = new Grid(labels.length, 2); // 2 cols
    componentInfo.setCellPadding(5); // px

    for (int i = 0; i < labels.length; i++) {
      componentInfo.setWidget(i, 0, new Label(labels[i] + ":"));
      componentInfo.getCellFormatter().setHorizontalAlignment(i, 0,
          HasHorizontalAlignment.ALIGN_RIGHT);
    }
    
    componentInfo.setWidget(0, 1, new Label(componentJso.getId()));
    String html = "<a href='" + componentJso.getURL() + "'>" + componentJso.getURL() + "</a>";
    componentInfo.setWidget(1, 1, new HTML(html));


    

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    contents.setWidth("25em");
    contents.add(componentInfo);

    this.setWidget(contents);
  }


}
