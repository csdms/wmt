/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.colorado.csdms.wmt.client.ui.handler.DialogCancelHandler;

/**
 * A dialog box with a {@link FormPanel} that holds a {@link FileUpload}
 * widget and a {@link ChoicePanel}. Use it to choose a file for upload to the
 * WMT server.
 * 
 * @see <a href="http://davidwalsh.name/fakepath">This</a> blog post on
 *      <code>C:\fakepath</code> and file uploads. Doesn't matter, though; the
 *      browser takes care of this.
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class UploadDialogBox extends DialogBox {
  
  private FormPanel form;
  private Hidden hidden;
  private FileUpload upload;

  /**
   * Creates an {@link UploadDialogBox}.
   * <p>
   * Note that the form action and value of the hidden field need to be set
   * when this dialog is employed. (Enforcing MVP separation of M and V.)
   */
  public UploadDialogBox() {

    super(false); // autohide
    this.setModal(true);

    // Create a FormPanel. When employed, point it at a service.
    form = new FormPanel();
    this.setWidget(form);

    // Set the form to use the POST method with multipart MIME encoding.
    form.setEncoding(FormPanel.ENCODING_MULTIPART);
    form.setMethod(FormPanel.METHOD_POST);

    // Everything in this "contents" panel can be included in the form.
    VerticalPanel contents = new VerticalPanel();
    contents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    form.setWidget(contents);

    // Make a hidden field with name "id". When employed, set value.
    hidden = new Hidden();
    hidden.setName("id"); // to match API
    contents.add(hidden);

    HorizontalPanel filePanel = new HorizontalPanel();
    filePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    filePanel.setHeight("3em");
    filePanel.setSpacing(5); // px
    contents.add(filePanel);

    // Make an upload panel with name "file".
    Label fileLabel = new Label("File:");
    fileLabel.getElement().getStyle().setPaddingLeft(1, Unit.EM);
    fileLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    upload = new FileUpload();
    upload.setName("file"); // to match API
    filePanel.add(fileLabel);
    filePanel.add(upload);

    ChoicePanel choicePanel = new ChoicePanel();
    choicePanel.getOkButton().setHTML(
        "<i class='fa fa-cloud-upload'></i> Upload");
    contents.add(choicePanel);

    /*
     * Event handler for the "Cancel" button. Closes UploadDialogBox.
     */
    choicePanel.getCancelButton()
        .addClickHandler(new DialogCancelHandler(this));

    /*
     * Event handler for the "Upload" button. Submits the form.
     */
    choicePanel.getOkButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        form.submit();
      }
    });

    /*
     * This handler is called just before the form is submitted. Can be used
     * to perform validation.
     */
    form.addSubmitHandler(new SubmitHandler() {
      @Override
      public void onSubmit(SubmitEvent event) {
        if (upload.getFilename().length() == 0) {
          Window.alert("Please choose a file for upload.");
          event.cancel();
        }
      }
    });
  }

  public FormPanel getForm() {
    return form;
  }

  public void setForm(FormPanel formPanel) {
    this.form = formPanel;
  }

  public Hidden getHidden() {
    return hidden;
  }

  public void setHidden(Hidden hidden) {
    this.hidden = hidden;
  }
  
  public FileUpload getUpload() {
    return upload;
  }

  public void setUpload(FileUpload upload) {
    this.upload = upload;
  }
  
}
