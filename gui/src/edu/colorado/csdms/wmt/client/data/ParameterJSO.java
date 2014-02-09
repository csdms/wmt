package edu.colorado.csdms.wmt.client.data;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A GWT JavaScript overlay (JSO) type that describes a parameter for a WMT
 * component model, with "key", "name", "description" and "value" attributes.
 * Declares JSNI methods to access these attributes from a JSON and modify
 * them in memory.
 * 
 * @see <a
 *      href="http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html">http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html</a>
 * @author Mark Piper (mark.piper@colorado.edu)
 * 
 */
public class ParameterJSO extends JavaScriptObject {

  // Overlay types have protected, no-arg, constructors.
  protected ParameterJSO() {
  }

  /**
   * A JSNI method to access the "key" attribute of a ParameterJSO. This
   * attribute is always present, and is a string.
   */
  public final native String getKey() /*-{
		return this.key;
  }-*/;

  /**
   * A JSNI method to access the "name" attribute of a ParameterJSO. This
   * attribute is always present, and is a string.
   */
  public final native String getName() /*-{
		return this.name;
  }-*/;

  /**
   * A JSNI method to access the "description" attribute of a ParameterJSO. This
   * attribute is always present, and is a string.
   */
  public final native String getDescription() /*-{
		return this.description;
  }-*/;

  /**
   * JSNI method to get the "value" attribute of a ParameterJSO. This attribute
   * is a ValueJSO object. It's always present, but it may be empty.
   */
  public final native ValueJSO getValue() /*-{
		return this.value;
  }-*/;

  /**
   * A non-JSNI method for stringifying the attributes of a ParameterJSO. Must be
   * final.
   */
  public final Vector<String> toStringVector() {

    Vector<String> retVal = new Vector<String>();
    retVal.add("key: " + getKey());
    retVal.add("name: " + getName());
    retVal.add("description: " + getDescription());
    for (int i = 0; i < getValue().toStringVector().size(); i++) {
      retVal.add(getValue().toStringVector().get(i));
    }
    return retVal;
  }
}
