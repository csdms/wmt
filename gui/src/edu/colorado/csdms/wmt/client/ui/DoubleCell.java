/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.ui;

import java.text.ParseException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.ValueBox;

import edu.colorado.csdms.wmt.client.data.ParameterJSO;

/**
 * A ValueBox that uses {@link DoubleCellParser} and {@link DoubleCellRenderer}.
 * This is the default for the "float" parameter type. {@link ValueChangeEvent}s
 * are sent to {@link DoubleCellChangeHandler}.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class DoubleCell extends ValueBox<Double> {

  private ValueCell parent;
  private ParameterJSO parameter;

  /**
   * Makes a new {@link DoubleCell} and parses, formats and displays its value.
   * 
   * @param parent the parent of the DoubleCell, a ValueCell
   */
  public DoubleCell(ValueCell parent) {

    super(Document.get().createTextInputElement(), DoubleCellRenderer
        .instance(), DoubleCellParser.instance());

    this.parent = parent;
    this.parameter = this.parent.getParameter();

    addValueChangeHandler(new DoubleCellChangeHandler());
    setStyleName("wmt-ValueBoxen");

    try {
      Double doubleValue =
          Double.valueOf(this.parameter.getValue().getDefault());
      setValue(doubleValue);
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

    String minValue = parameter.getValue().getMin();
    Double minValueD = Double.valueOf(minValue);
    String maxValue = parameter.getValue().getMax();
    Double maxValueD = Double.valueOf(maxValue);

    Boolean rangeOK = false;
    if ((getValue() <= maxValueD) && (getValue() >= minValueD)) {
      rangeOK = true;
    }

    return rangeOK;
  }

  /**
   * A class to handle edit events in an {@link DoubleCell}. The
   * {@link ValueChangeEvent} is fired when the <code>Enter</code> or
   * <code>Tab</code> keys are pressed, or when focus leaves the
   * {@link DoubleCell}. Also checks for valid contents of the boxen.
   */
  public class DoubleCellChangeHandler implements ValueChangeHandler<Double> {
    @Override
    public void onValueChange(ValueChangeEvent<Double> event) {
      GWT.log("(onValueChange:DoubleCell)");
      try {
        Double value = getValueOrThrow();
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
