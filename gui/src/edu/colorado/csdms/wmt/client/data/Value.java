/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

import com.google.gwt.core.client.JsArrayString;

/**
 * A class to model the value of a parameter in the WMT GUI. It acts as a
 * wrapper around the data in a ValueJSO object.
 * <p>
 * All attributes of a ValueJSO are returned as Strings, and I'm choosing to
 * preserve this in a Value (despite the fact that a Value can have type float,
 * int, string or choice). This should be OK because we aren't doing
 * computations in WMT, but merely displaying numbers in a GUI. It also makes
 * things a lot easier.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class Value {

  private String type;
  private String units;
  private String defaultValue;
  private String minValue;
  private String maxValue;
  private String[] choices;

  /**
   * Creates an empty Value. Attributes can be set on it afterward.
   */
  public Value() {
  }

  /**
   * Creates a new Value from a read-only ValueJSO object.
   * 
   * @param valueJSO a ValueJSO object read from a parameter JSON file
   */
  public Value(ValueJSO valueJSO) {
    setType(valueJSO.getType());
    setUnits(valueJSO.getUnits());
    setDefaultValue(valueJSO.getDefault());
    setMinValue(valueJSO.getMin());
    setMaxValue(valueJSO.getMax());
    setChoices(valueJSO.getChoices());
  }

  /**
   * Returns a String giving the type attribute of the Value: float, int, string
   * or choice.
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type attribute of a Value (float, int, string or choice).
   * 
   * @param type the type to set, as a String
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @return the units
   */
  public String getUnits() {
    return units;
  }

  /**
   * @param units the units to set
   */
  public void setUnits(String units) {
    this.units = units;
  }

  /**
   * @return the defaultValue
   */
  public String getDefaultValue() {
    return defaultValue;
  }

  /**
   * @param defaultValue the defaultValue to set
   */
  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  /**
   * @return the minValue
   */
  public String getMinValue() {
    return minValue;
  }

  /**
   * @param minValue the minValue to set, as a Number
   */
  public void setMinValue(String minValue) {
    this.minValue = minValue;
  }

  /**
   * @return the maxValue
   */
  public String getMaxValue() {
    return maxValue;
  }

  /**
   * @param maxValue the maxValue to set
   */
  public void setMaxValue(String maxValue) {
    this.maxValue = maxValue;
  }

  /**
   * 
   * @return
   */
  public String[] getChoices() {
    return choices;
  }

  /**
   * 
   * @param choices
   */
  public void setChoices(JsArrayString choices) {
    Integer nChoices = choices.length();
    if (nChoices == 0) {
      return;
    }
    for (int i = 0; i < nChoices; i++) {
      this.choices[i] = choices.get(i);
    }
  }

  /**
   * Examples and unit tests.
   */
  public static void main() {

    // Zero-elt ctr.
    Value v0 = new Value();
    v0.setDefaultValue("100.0");
    System.out.println(v0.getDefaultValue());
  }
}
