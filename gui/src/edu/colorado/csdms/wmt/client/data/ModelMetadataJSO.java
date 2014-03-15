/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dev.javac.JsniMethod;

/**
 * A GWT JavaScript overlay (JSO) type that gives the metadata for a WMT
 * model, including the owner, the model id and the model name; information
 * corresponding to the "open" URL in the API. Declares JSNI methods to access
 * these attributes.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelMetadataJSO extends JavaScriptObject {

  // Overlay types always have protected, zero-arg constructors.
  protected ModelMetadataJSO() {
  }

  /**
   * Gets the name of the model, a String. This is a JSNI method.
   */
  public final native String getName() /*-{
		return this.name;
  }-*/;

  /**
   * Sets the name of the model, a String. This is a JSNI method.
   * 
   * @param name the name of the model
   */
  public final native void setName(String name) /*-{
		this.name = name;
  }-*/;

  /**
   * A JSNI method to get the id of the model, an int used to uniquely
   * identify it in the database. The user can't modify this id -- it's set by
   * the API. Be aware that this is different than {@link ModelJSO#getId()},
   * which is used to get the id of a component.
   * <p>
   * Returns a value of -1 if no model metadata are present.
   */
  public final native int getId() /*-{
		if (typeof this.id == 'undefined') {
			return -1;
		} else {
			return this.id;
		}
  }-*/;

  /**
   * Sets the id of the model, an int. This is a JSNI method.
   * 
   * @param id the id of the model assigned by the WMT API
   */
  public final native void setId(int id) /*-{
		this.id = id;
  }-*/;

  /**
   * A JSNI method to get the owner of the model, a String.
   */
  public final native String getOwner() /*-{
		return this.owner;
  }-*/;

  /**
   * Sets the owner of the model, a String. This is a JSNI method.
   * 
   * @param owner the username of the owner of the model
   */
  public final native void setOwner(String owner) /*-{
		this.owner = owner;
  }-*/;
}
