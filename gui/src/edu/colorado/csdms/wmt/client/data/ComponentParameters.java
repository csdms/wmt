/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * A GWT JavaScript overlay (JSO) type that describes a JSON holding
 * parameters for a WMT component model. Declares JSNI methods to access
 * contents of the JSON. Includes a non-JSNI helper method for displaying the
 * contents of the JSON file.
 * 
 * @see <a
 *      href="http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html">http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html</a>
 * @author Mark Piper (mark.piper@colorado.edu)
 * 
 */
@Deprecated
public class ComponentParameters extends JavaScriptObject {

  // Overlay types have protected, no-arg, constructors.
  protected ComponentParameters() {
  }

  /**
   * Gets the component model id from the JSON.
   */
  public final native String getId() /*-{
		return this.id;
  }-*/;

  /**
   * JSNI method that returns the JsArray of Parameters attached to the top
   * key, "component_parameters".
   */
  public final native JsArray<ParameterJSO> getParameters() /*-{
		return this.component_parameters;
  }-*/;

  /**
   * A JSNI method that returns (as an int) the number of parameters listed in
   * the JSON file.
   */
  public final native int length() /*-{
		return this.component_parameters.length;
  }-*/;

  /**
   * A JSNI method that returns the ParameterJSO at the given index.
   * <p>
   * TODO Check type and bounds of index.
   * 
   * @param index The zero-based index into the array of Parameters read from
   *          the JSON file.
   */
  public final native ParameterJSO get(Integer index) /*-{
		var parameterArray = this.component_parameters;
		return parameterArray[index];
  }-*/;

  /**
   * A JSNI method that returns a ParameterJSO by its "key" attribute.
   * 
   * @param key The key of the desired parameter, a String.
   */
  public final native ParameterJSO get(String key) /*-{
		var parameterArray = this.component_parameters;
		for (var i = 0; i < parameterArray.length; i++) {
			parameter = parameterArray[i]
			if (parameter.key === key) {
				return parameter;
			}
		}
		return null;
  }-*/;

  /**
   * A non-JSNI method for storing information about the attributes of a
   * component. Must be final.
   */
  public final Vector<String> toStringVector() {

    Vector<String> retVal = new Vector<String>();
    retVal.add("id: " + getId());
    for (int i = 0; i < getParameters().length(); i++) {
      for (int j = 0; j < get(i).toStringVector().size(); j++) {
        retVal.add(get(i).toStringVector().get(j));
      }
    }
    return retVal;
  }
}
