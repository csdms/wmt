/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.text.ParseException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.client.IntegerParser;
import com.google.gwt.text.client.IntegerRenderer;
import com.google.gwt.user.client.ui.ValueBox;

import edu.colorado.csdms.wmt.client.data.ParameterJSO;

/**
 * A ValueBox that uses {@link IntegerParser} and {@link IntegerRenderer} to
 * display an "int" parameter type. {@link ValueChangeEvent}s are sent to
 * {@link IntegerCellChangeHandler}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class IntegerCell extends ValueBox<Integer> {

  private ValueCell parent;
  private ParameterJSO parameter;

  /**
   * Makes a new {@link IntegerCell} and parses, formats and displays its value.
   * 
   * @param parent the parent of the IntegerCell, a ValueCell
   */
  public IntegerCell(ValueCell parent) {

    super(Document.get().createTextInputElement(), IntegerRenderer.instance(),
        IntegerParser.instance());

    this.parent = parent;
    this.parameter = this.parent.getParameter();

    addValueChangeHandler(new IntegerCellChangeHandler());
    setStyleName("wmt-ValueBoxen");

    try {
      Integer integerValue =
          Integer.valueOf(this.parameter.getValue().getDefault());
      setValue(integerValue);
      setStyleDependentName("outofrange", !isInRange());
    } catch (Exception e) {
      setValue(null);
      addStyleDependentName("outofrange");
    }
  }

  /**
   * Checks whether a given value is within the established range of values for
   * the current parameter of the {@link ValueCell}, returning a Boolean.
   */
  public Boolean isInRange() {
    Integer minValue = Integer.valueOf(parameter.getValue().getMin());
    Integer maxValue = Integer.valueOf(parameter.getValue().getMax());
    Boolean rangeOK = false;
    if ((getValue() <= maxValue) && (getValue() >= minValue)) {
      rangeOK = true;
    }
    return rangeOK;
  }

  /**
   * A class to handle edit events in an {@link IntegerCell}. The
   * {@link ValueChangeEvent} is fired when the <code>Enter</code> or
   * <code>Tab</code> keys are pressed, or when focus leaves the
   * {@link IntegerCell}. Also checks for valid contents of the boxen.
   */
  public class IntegerCellChangeHandler implements ValueChangeHandler<Integer> {
    @Override
    public void onValueChange(ValueChangeEvent<Integer> event) {
      GWT.log("(onValueChange:IntegerCell)");
      try {
        Integer value = getValueOrThrow();
        if (value == null) {
          addStyleDependentName("outofrange");
          return;
        }
        setValue(value); // formats contents
        parent.setValue(value.toString());
        setStyleDependentName("outofrange", !isInRange());
      } catch (ParseException e) {
        addStyleDependentName("outofrange");
      }
    }
  }

}
