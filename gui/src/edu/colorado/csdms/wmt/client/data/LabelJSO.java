/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * A GWT JavaScript overlay (JSO) type that describes the JSON returned on a
 * HTTP GET call to <a
 * href="http://csdms.colorado.edu/wmt/api/tag/list">tag/list</a>. Declares
 * JSNI methods to access attributes.
 * 
 * @see <a
 *      href="http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html">http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html</a>
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class LabelJSO extends JavaScriptObject {

  // Overlay types always have protected, zero-arg constructors.
  protected LabelJSO() {
  }

  /**
   * Gets the owner of a label, a String. This is a JSNI method.
   */
  public final native String getOwner() /*-{
		return this.owner;
  }-*/;

  /**
   * Gets the text of the label, a String. This is a JSNI method.
   */
  public final native String getLabel() /*-{
		return this.tag;
  }-*/;

  /**
   * A JSNI method to get the id of the label, an int. 
   */
  public final native int getId() /*-{
		return this.id;
  }-*/;    
  
  /**
   * Gets a JsArray of labels. This is a JSNI method.
   */
  public final native JsArray<LabelJSO> getLabels() /*-{
		return this;
  }-*/;  
}
