package edu.colorado.csdms.wmt.client.data;

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * A GWT JavaScript overlay (JSO) type that describes a JSON holding
 * descriptions of WMT component models. Declares JSNI methods to access
 * contents of the JSON. Includes a non-JSNI helper method for displaying the
 * contents of the JSON file.
 * 
 * @see <a
 *      href="http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html">http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html</a>
 * @author Mark Piper (mark.piper@colorado.edu)
 * 
 */
public class ComponentDescriptions extends JavaScriptObject {

  // Overlay types have protected, no-arg, constructors.
  protected ComponentDescriptions() {
  }

  /**
   * JSNI method that returns the JsArray of Components attached to the top
   * key, "component_descriptions".
   */
  public final native JsArray<ComponentJSO> getComponents() /*-{
		return this.component_descriptions;
  }-*/;

  
  /**
   * A JSNI method that returns (as an int) the number of components listed in
   * the JSON file.
   */
  public final native int length() /*-{
		return this.component_descriptions.length;
  }-*/;
  
  
  /**
   * A JSNI method that returns the Component at the given index.
   * <p>
   * TODO Check type and bounds of index.
   * 
   * @param index The zero-based index into the array of components read from
   *          the JSON file.
   */
  public final native ComponentJSO get(Integer index) /*-{
		var componentArray = this.component_descriptions;
		return componentArray[index];
  }-*/;

  /**
   * A JSNI method that returns a ComponentJSO by its "id" attribute.
   * 
   * @param id The id of the desired component, a String.
   */
  public final native ComponentJSO get(String id) /*-{
		var componentArray = this.component_descriptions;
		for (var i = 0; i < componentArray.length; i++) {
		  component = componentArray[i]
		  if (component.id === id) {
		    return component;
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
    for (int i = 0; i < getComponents().length(); i++) {
      for (int j = 0; j < get(i).toStringVector().size(); j++) {
        retVal.add(get(i).toStringVector().get(j));
      }
    }
    return retVal;
  }
}
