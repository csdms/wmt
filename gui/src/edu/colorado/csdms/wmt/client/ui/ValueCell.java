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

  /**
   * Makes a ValueCell from the information contained in the input
   * {@link ParameterJSO} object.
   * 
   * @param parameter a ParameterJSO object
   */
  public ValueCell(ParameterJSO parameter) {

    this.parameter = parameter;

    // If the parameter is a separator, short-circuit the method and return.
    if (this.parameter.getKey().matches("separator")) {
      return;
    }

    // Helpful locals.
    String type = this.parameter.getValue().getType();
    String value = this.parameter.getValue().getDefault();
    String range = "";

    // Make a cell to match the type -- choice, file or other.
    if (type.matches("choice")) {
      makeChoiceCell(value);
    } else if (type.matches("file")) {
      makeFileCell(value);
    } else {
      makeTextCell(value);
    }

    // If the parameter type is numeric, add a tooltip showing the valid range
    // of its value.
    if (isParameterTypeNumeric(parameter)) {
      range +=
          "Valid range: (" + parameter.getValue().getMin() + ", "
              + parameter.getValue().getMax() + ")";
      this.setTitle(range);
    }
  }

  /**
   * A worker that makes the {@link ValueCell} display a droplist for the
   * "choice" parameter type.
   * 
   * @param value the value of the parameter.
   */
  private void makeChoiceCell(String value) {
    ListBox choiceDroplist = new ListBox(false); // no multi select
    choiceDroplist.addChangeHandler(new ListSelectionHandler());

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
   * @param value the value of the parameter.
   */
  private void makeFileCell(String value) {
      ListBox fileDroplist = new ListBox(false); // no multi select
      fileDroplist.addChangeHandler(new ListSelectionHandler());

      Integer nFiles = this.parameter.getValue().getFiles().length();
      for (int i = 0; i < nFiles; i++) {
        fileDroplist.addItem(this.parameter.getValue().getFiles().get(i));
        if (fileDroplist.getItemText(i).matches(value)) {
          fileDroplist.setSelectedIndex(i);
        }
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
   * default for "float", "int", etc., parameter types.
   * 
   * @param value the value of the parameter.
   */
  private void makeTextCell(String value) {
    TextBox valueTextBox = new TextBox();
    valueTextBox.setStyleDependentName("inrange", true);
    valueTextBox.addKeyUpHandler(new TextEditHandler());

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
   * A class to handle keyboard events in the TextBox. Also checks for valid
   * TextBox contents.
   * <p>
   * Note that every key press generates an event. It might be worth considering
   * acting on only Tab or Enter key presses.
   */
  public class TextEditHandler implements KeyUpHandler {
    @Override
    public void onKeyUp(KeyUpEvent event) {
      GWT.log("(onKeyUp)");
      TextBox textBox = (TextBox) event.getSource();
      String value = textBox.getText();
      textBox.setStyleDependentName("outofrange", !isInRange(parameter, value));
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

      String modelId = ((Integer) pt.data.getMetadata().getId()).toString(); 
      upload.getHidden().setValue(modelId);

      upload.getForm().setAction(DataURL.uploadFile(pt.data));
      upload.getForm().addSubmitCompleteHandler(new UploadCompleteHandler());
      upload.center();
    }
  }

  public class UploadCompleteHandler implements FormPanel.SubmitCompleteHandler {
    @Override
    public void onSubmitComplete(SubmitCompleteEvent event) {

      upload.hide();
      Window.alert("Filename: " + upload.getUpload().getFilename()
          + "; Results: " + event.getResults());

      if (event.getResults() != null) {
        ;
      }
    }
  }

  /**
   * Checks whether a given value is within the established range of values for
   * a parameter, returning a Boolean. This method operates only on numeric
   * types.
   * 
   * @param parameter a ParameterJSO object
   * @param value a value
   */
  private Boolean isInRange(ParameterJSO parameter, String value) {
    Boolean rangeOK = true;
    if (isParameterTypeNumeric(parameter)) {
      if (!isNumeric(value)) {
        rangeOK = false;
      } else {
        Double newValueD = Double.valueOf(value);
        String minValue = parameter.getValue().getMin();
        Double minValueD = Double.valueOf(minValue);
        String maxValue = parameter.getValue().getMax();
        Double maxValueD = Double.valueOf(maxValue);
        if ((newValueD > maxValueD) || (newValueD < minValueD)) {
          rangeOK = false;
        }
      }
    }
    return rangeOK;
  }

  /**
   * Checks whether the parameter uses a numeric type value (e.g., float or
   * int). Returns a Boolean.
   * 
   * @param parameter a ParameterJSO object
   */
  private Boolean isParameterTypeNumeric(ParameterJSO parameter) {
    Boolean isNumeric = true;
    String type = parameter.getValue().getType();
    if (type.matches("string") || type.matches("choice")
        || type.matches("file")) {
      isNumeric = false;
    }
    return isNumeric;
  }

  /**
   * Checks whether the input String can be cast to a number.
   * 
   * @see <a
   *      href="http://stackoverflow.com/questions/14206768/how-to-check-if-a-string-is-numeric">This</a>
   *      discussion. Thanks, stackoverflow!
   * @param s a String
   */
  private Boolean isNumeric(String s) {
    return s.matches("[-+]?\\d*\\.?\\d+");
  }
}
