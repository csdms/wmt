/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * A GWT JavaScript overlay (JSO) type that describes the JSON returned on a
 * HTTP GET call to <a
 * href="http://csdms.colorado.edu/wmt/components/list">components/list</a>. Declares
 * JSNI methods to access attributes.
 * 
 * @see <a
 *      href="http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html">http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html</a>
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ComponentListJSO extends JavaScriptObject {

  // Overlay types always have protected, zero-arg constructors.
  protected ComponentListJSO() {
  }    
  
  /**
   * Gets the JsArrayString of component ids (which are Strings, obvs). This
   * is a JSNI method.
   */
  public final native JsArrayString getComponents() /*-{
		return this;
  }-*/;  
}
