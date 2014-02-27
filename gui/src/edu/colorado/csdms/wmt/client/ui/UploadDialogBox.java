/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

/**
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 *
 */
public class UploadDialogBox extends DialogBox {
  
  private FormPanel form;
  private FileUpload upload;

  /**
   * 
   */
  public UploadDialogBox() {

    super(true); // autohide
    this.setModal(true);

    // Create a FormPanel and point it at a service.
    form = new FormPanel();
    form.setAction("http://localhost");
    this.setWidget(form);

    // Set the form to use the POST method with multipart MIME encoding.
    form.setEncoding(FormPanel.ENCODING_MULTIPART);
    form.setMethod(FormPanel.METHOD_POST);

    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    form.setWidget(contents);

    HorizontalPanel filePanel = new HorizontalPanel();
    filePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    filePanel.setHeight("3em");
    filePanel.setSpacing(5); // px
    contents.add(filePanel);

    Label fileLabel = new Label("File:");
    fileLabel.getElement().getStyle().setPaddingLeft(1, Unit.EM);
    fileLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    upload = new FileUpload();
    upload.setName("fileUploadFormElement");
    filePanel.add(fileLabel);
    filePanel.add(upload);

    ChoicePanel choicePanel = new ChoicePanel();
    choicePanel.getOkButton().setHTML(
        "<i class='fa fa-cloud-upload'></i> Upload");
    contents.add(choicePanel);

    // Event handler for the "Upload" button. Submits the form.
    choicePanel.getOkButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        form.submit();
      }
    });

    // This handler is called just before the form is submitted. Perform
    // validation.
    form.addSubmitHandler(new SubmitHandler() {
      @Override
      public void onSubmit(SubmitEvent event) {
        if (upload.getFilename().length() == 0) {
          Window.alert("Please choose a file for upload.");
          event.cancel();
        }
      }
    });
    
    // Event handler for the "Cancel" button. Closes UploadDialogBox.
    choicePanel.getCancelButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        UploadDialogBox.this.hide();
      }
    });
  }

  public FormPanel getForm() {
    return form;
  }

  public void setForm(FormPanel formPanel) {
    this.form = formPanel;
  }

  public FileUpload getUpload() {
    return upload;
  }

  public void setUpload(FileUpload upload) {
    this.upload = upload;
  }
}
