/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * A GWT JavaScript overlay (JSO) type that describes the JSON returned on a
 * HTTP GET call to <a
 * href="http://csdms.colorado.edu/wmt/models/list">models/list</a>. Declares
 * JSNI methods to access attributes.
 * 
 * @see <a
 *      href="http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html">http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html</a>
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelListJSO extends JavaScriptObject {

  // Overlay types always have protected, zero-arg constructors.
  protected ModelListJSO() {
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
   * A JSNI method to get the id of the model, an int used to uniquely
   * identify it in the database. The user can't modify this id -- it's set by
   * the API. 
   */
  public final native int getModelId() /*-{
		return this.id;
  }-*/;    
  
  /**
   * Gets the JsArray of models. This is a JSNI method.
   */
  public final native JsArray<ModelListJSO> getModels() /*-{
		return this;
  }-*/;  
}
