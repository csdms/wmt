/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * A GWT JavaScript overlay (JSO) type that describes a WMT model, consisting of
 * components, their parameters, and their connections; information
 * corresponding to the "show" URL in the API. Declares JSNI methods to access
 * these attributes from a JSON and modify them in memory.
 * 
 * @see <a
 *      href="http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html">http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html</a>
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelJSO extends JavaScriptObject {

  // Overlay types always have protected, zero-arg constructors.
  protected ModelJSO() {
  }

  /**
   * Gets the name of the model, a String. This is a JSNI method.
   */
  public final native String getName() /*-{
		return this.name;
  }-*/;

  /**
   * Sets the model name, a String. This is a JSNI method.
   * 
   * @param name the name of the model, a String
   */
  public final native void setName(String name) /*-{
		this.name = name;
  }-*/;

  /**
   * Gets the JsArray of the components, including their parameters and their
   * connections, that make up a model. This is a JSNI method.
   */
  public final native JsArray<ModelJSO> getComponents() /*-{
		return this.model;
  }-*/;

  /**
   * Stores the JsArray of the components, including their parameters and their
   * connections, that make up a model. This is a JSNI method.
   * 
   * @param components a JsArray of ModelComponentJSO components
   */
  public final native void setComponents(JsArray<ModelComponentJSO> components) /*-{
		this.model = components;
  }-*/;
}
