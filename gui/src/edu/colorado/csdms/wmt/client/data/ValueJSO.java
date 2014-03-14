package edu.colorado.csdms.wmt.client.data;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * A GWT JavaScript overlay (JSO) type that describes the values of a
 * parameter for a WMT component model, with "type", "default", "units",
 * "range" and "choices" attributes. Declares JSNI methods to access these
 * attributes from a JSON and modify them in memory.
 * 
 * @see <a
 *      href="http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html">http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html</a>
 * @author Mark Piper (mark.piper@colorado.edu)
 * 
 */
public class ValueJSO extends JavaScriptObject {

  // Overlay types have protected, no-arg, constructors.
  protected ValueJSO() {
  }

  /**
   * A JSNI method to access the "type" attribute of a ValueJSO. May not be
   * present, though ignored without an exception; is a string.
   */
  public final native String getType() /*-{
		return this.type;
  }-*/;

  /**
   * A JSNI method to access the "default" attribute of a ValueJSO. May not be
   * present. Coerce result to string.
   */
  public final native String getDefault() /*-{
    // "default" is reserved in JavaScript; use hash notation to access.
		return this["default"].toString();
  }-*/;

  /**
   * A generic JSNI method to set the "default" attribute of a ValueJSO.
   * 
   * @param value the value to set, of type String, Integer or Double
   */
  public final native <T> void setDefault(T value) /*-{
    // "default" is reserved in JavaScript; use hash notation to access.
    this["default"] = value;
  }-*/;
  
  /**
   * A JSNI method to access the "units" attribute of a ValueJSO. May not be
   * present, though ignored without an exception; is a string.
   */
  public final native String getUnits() /*-{
		return this.units;
  }-*/;

  /**
   * JSNI method to access the "range.min" attribute of a ValueJSO. May not be
   * present. The undefined check on "range" attribute is necessary. I'm
   * choosing to cast value to string, because long integers aren't supported.
   * 
   * @see http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsJSNI.html
   */
  public final native String getMin() /*-{
		if (typeof this.range == 'undefined') {
			return null;
		} else {
			return this.range.min.toString();
		}
  }-*/;

  /**
   * JSNI method to access the "range.max" attribute of a ValueJSO. May not be
   * present. The undefined check on "range" attribute is necessary. I'm
   * choosing to cast value to string, because long integers aren't supported.
   * 
   * @see http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsJSNI.html
   */
  public final native String getMax() /*-{
		if (typeof this.range == 'undefined') {
			return null;
		} else {
			return this.range.max.toString();
		}
  }-*/;

  /**
   * A JSNI method to access the "choices" attribute of a ValueJSO. May not be
   * present, though ignored without an exception; is an array of strings,
   * represented by a JsArrayString object.
   */
  public final native JsArrayString getChoices() /*-{
		return this.choices;
  }-*/;

  /**
   * A JSNI method to access the "files" attribute of a ValueJSO. May not be
   * present, though ignored without an exception; is an array of strings,
   * represented by a JsArrayString object.
   */
  public final native JsArrayString getFiles() /*-{
		return this.files;
  }-*/;

  /**
   * A JSNI method to set the "files" attribute of a ValueJSO.
   * 
   * @param files a JsArrayString of filenames
   */
  public final native void setFiles(JsArrayString files) /*-{
		this.files = files;
  }-*/;

  /**
   * A non-JSNI method for stringifying the attributes of a ValueJSO. Must be
   * final.
   */
  public final Vector<String> toStringVector() {

    Vector<String> retVal = new Vector<String>();
    retVal.add("type: " + getType());
    retVal.add("default: " + getDefault());
    if (getUnits() != null) {
      retVal.add("units: " + getUnits());
    }
    if ((getMin() != null) && getMax() != null) {
      retVal.add("range: [" + getMin() + ", " + getMax() + "]");
    }
    if (getChoices() != null) {
      retVal.add("choices: [" + getChoices().toString() + "]");
    }
    return retVal;
  }
}
