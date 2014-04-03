/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import edu.colorado.csdms.wmt.client.control.DataURL;
import edu.colorado.csdms.wmt.client.data.ParameterJSO;
import edu.colorado.csdms.wmt.client.ui.widgets.UploadDialogBox;

/**
 * Used to display the value of a parameter in a {@link ParameterTable}, a
 * ValueCell renders as a ListBox (droplist) if the parameter type = "choice" or
 * "file"; otherwise, it renders as an editable TextBox. Changes to the value in
 * a ValueCell are stored in the WMT DataManager.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ValueCell extends HorizontalPanel {

  private ParameterJSO parameter;
  private UploadDialogBox upload;
  private ListBox fileDroplist;

  /**
   * Makes a ValueCell from the information contained in the input
   * {@link ParameterJSO} object.
   * 
   * @param parameter a ParameterJSO object
   */
  public ValueCell(ParameterJSO parameter) {

    this.parameter = parameter;
    this.setStyleName("wmt-ValueCell");

    // If the parameter is a separator, short-circuit the method and return.
    if (this.parameter.getKey().matches("separator")) {
      return;
    }

    // Helpful locals.
    String type = this.parameter.getValue().getType();
    String value = this.parameter.getValue().getDefault();
    String range = "";

    // Make a cell to match the type -- choice, file or other.
    // TODO Make classes for ChoiceCell, FileCell and TextCell.
    if (type.matches("choice")) {
      makeChoiceCell(value);
    } else if (type.matches("file")) {
      makeFileCell(value);
    } else if (type.matches("int")) {
      IntegerCell ibox = new IntegerCell(this);
      this.add(ibox);
    } else if (type.matches("float")) {
      DoubleCell dbox = new DoubleCell(this);
      this.add(dbox);
    } else {
      makeTextCell(value);
    }

    // If the parameter type is numeric, add a tooltip showing the valid range
    // of its value.
    if (isParameterTypeNumeric()) {
      range +=
          "Valid range: (" + parameter.getValue().getMin() + ", "
              + parameter.getValue().getMax() + ")";
      this.setTitle(range);
    }
  }

  public ParameterJSO getParameter() {
    return parameter;
  }

  public void setParameter(ParameterJSO parameter) {
    this.parameter = parameter;
  }

  /**
   * A worker that makes the {@link ValueCell} display a droplist for the
   * "choice" parameter type.
   * 
   * @param value the value of the parameter, a String
   */
  private void makeChoiceCell(String value) {
    ListBox choiceDroplist = new ListBox(false); // no multi select
    choiceDroplist.addChangeHandler(new ListSelectionHandler());
    choiceDroplist.setStyleName("wmt-DroplistBox");

    Integer nChoices = this.parameter.getValue().getChoices().length();
    for (int i = 0; i < nChoices; i++) {
      choiceDroplist.addItem(this.parameter.getValue().getChoices().get(i));
      if (choiceDroplist.getItemText(i).matches(value)) {
        choiceDroplist.setSelectedIndex(i);
      }
    }
    choiceDroplist.setVisibleItemCount(1); // show one item -- a droplist
    this.add(choiceDroplist);
  }
  
  /**
   * A worker that makes the {@link ValueCell} display a droplist and a file
   * upload button for the "file" parameter type.
   * 
   * @param value the value of the parameter, a String
   */
  private void makeFileCell(String value) {
    fileDroplist = new ListBox(false); // no multi select
    fileDroplist.addChangeHandler(new ListSelectionHandler());
    fileDroplist.setStyleName("wmt-FileUploadBox");

    // Load the droplist. If the value of the incoming parameter isn't listed
    // in the component, append it to the end of the list and select it.
    Integer nFiles = this.parameter.getValue().getFiles().length();
    Integer selectedIndex = -1;
    for (int i = 0; i < nFiles; i++) {
      fileDroplist.addItem(this.parameter.getValue().getFiles().get(i));
      if (fileDroplist.getItemText(i).matches(value)) {
        selectedIndex = i;
      }
    }
    if (selectedIndex > 0) {
      fileDroplist.setSelectedIndex(selectedIndex);
    } else {
      fileDroplist.addItem(value);
      fileDroplist.setSelectedIndex(fileDroplist.getItemCount() - 1);
    }
    fileDroplist.setVisibleItemCount(1); // show one item -- a droplist
    this.add(fileDroplist);

    Button uploadButton = new Button("<i class='fa fa-cloud-upload'></i>");
    uploadButton.setStyleDependentName("slim", true);
    uploadButton.addClickHandler(new UploadHandler());

    uploadButton.setTitle("Upload file to server");
    this.add(uploadButton);

    this.setCellVerticalAlignment(fileDroplist, ALIGN_MIDDLE);
    uploadButton.getElement().getStyle().setMarginLeft(3, Unit.PX);
  }

  /**
   * A worker that makes the {@link ValueCell} display a text box. This is the
   * default for the "string" parameter type.
   * 
   * @param value the value of the parameter.
   */
  private void makeTextCell(String value) {
    TextBox valueTextBox = new TextBox();
    valueTextBox.addKeyUpHandler(new TextEditHandler());
    valueTextBox.setStyleName("wmt-TextBoxen");
    valueTextBox.setWidth("200px");
    valueTextBox.setText(value);
    this.add(valueTextBox);
  }

  /**
   * Passes the modified value up to
   * {@link ParameterTable#setValue(ParameterJSO, String)}. This isn't an
   * elegant solution, but ParameterTable knows the component this parameter
   * belongs to and it has access to the DataManager object for storage.
   * 
   * @param value the value read from the ValueCell
   */
  public void setValue(String value) {
    ParameterTable pt = (ParameterTable) ValueCell.this.getParent();
    pt.setValue(parameter, value);
  }

  /**
   * Checks whether the current {@link ValueCell} parameter uses a numeric type
   * value (e.g., float or int). Returns a Boolean.
   */
  private Boolean isParameterTypeNumeric() {
    Boolean isNumeric = true;
    String type = parameter.getValue().getType();
    if (type.matches("string") || type.matches("choice")
        || type.matches("file")) {
      isNumeric = false;
    }
    return isNumeric;
  }

  /**
   * A class to handle selection in the "choices" ListBox.
   */
  public class ListSelectionHandler implements ChangeHandler {
    @Override
    public void onChange(ChangeEvent event) {
      GWT.log("(onChange)");
      ListBox listBox = (ListBox) event.getSource();
      String value = listBox.getValue(listBox.getSelectedIndex());
      setValue(value);
    }
  }
  
  /**
   * A class to handle keyboard events in the TextBox.
   * <p>
   * Note that every key press generates an event. It might be worth considering
   * acting on only Tab or Enter key presses.
   */
  public class TextEditHandler implements KeyUpHandler {
    @Override
    public void onKeyUp(KeyUpEvent event) {
      GWT.log("(onKeyUp:text)");
      TextBox textBox = (TextBox) event.getSource();
      String value = textBox.getText();
      setValue(value);
    }
  }

  /**
   * Handles a click on the Upload button.
   */
  public class UploadHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent event) {

      ParameterTable pt = (ParameterTable) ValueCell.this.getParent();
      if (!pt.data.modelIsSaved()) {
        String msg =
            "The model must be saved to the server"
                + " before files can be uploaded.";
        Window.alert(msg);
        return;
      }

      upload = new UploadDialogBox();
      upload.setText("Upload File...");

      // Get the id of the model this file belongs to.
      String modelId = ((Integer) pt.data.getMetadata().getId()).toString(); 
      upload.getHidden().setValue(modelId);

      // Where the form is to be submitted.
      upload.getForm().setAction(DataURL.uploadFile(pt.data));
      
      upload.getForm().addSubmitCompleteHandler(new UploadCompleteHandler());
      upload.center();
    }
  }

  /**
   * When the upload is complete and successful, add the name of the uploaded
   * file to the {@link ValueCell} fileDroplist, select it, and save it as the
   * value of this parameter.
   */
  public class UploadCompleteHandler implements FormPanel.SubmitCompleteHandler {
    @Override
    public void onSubmitComplete(SubmitCompleteEvent event) {
      
      upload.hide();
      
      if (event.getResults() != null) {        
        
        // Strip the fakepath from the filename.
        String fileName =
            upload.getUpload().getFilename().replace("C:\\fakepath\\", "");
        
        // Add the filename to the fileDroplist, but only if it's not there
        // already.
        Integer listIndex = -1;
        for (int i = 0; i < fileDroplist.getItemCount(); i++) {
          if (fileDroplist.getItemText(i).matches(fileName)) {
            listIndex = i;
          }
        }
        if (listIndex > 0) {
          fileDroplist.setSelectedIndex(listIndex);
        } else {
          fileDroplist.addItem(fileName);
          fileDroplist.setSelectedIndex(fileDroplist.getItemCount() - 1);
        }
        
        // Like, important.
        setValue(fileName);
        
        // Say everything is alright.
        Window.alert("File uploaded!");
        
        // Mark the model as unsaved.
        ParameterTable pt = (ParameterTable) ValueCell.this.getParent();
        pt.data.modelIsSaved(false);
        pt.data.getPerspective().setModelPanelTitle();
      }
    }
  }
}
