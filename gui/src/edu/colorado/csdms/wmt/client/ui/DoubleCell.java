/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 mcflugen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
    Double minValue = Double.valueOf(parameter.getValue().getMin());
    Double maxValue = Double.valueOf(parameter.getValue().getMax());
    Boolean rangeOK = false;
    if ((getValue() <= maxValue) && (getValue() >= minValue)) {
      rangeOK = true;
    }
    return rangeOK;
  }

  /**
   * A class to handle edit events in a {@link DoubleCell}. The
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
