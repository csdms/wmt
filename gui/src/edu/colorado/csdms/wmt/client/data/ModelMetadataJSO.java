/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A GWT JavaScript overlay (JSO) type that gives the metadata for a WMT model,
 * including the owner, the model id and the model name; information
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
   * A JSNI method to get the id of the model, an int used to uniquely identify
   * it in the database. The user can't modify this id -- it's set by the API.
   * Be aware that this is different than {@link ModelJSO#getId()}, which is
   * used to get the id of a component.
   */
  public final native int getId() /*-{
		return this.id;
  }-*/;

  /**
   * A JSNI method to get the owner of the model, a String.
   */
  public final native String getOwner() /*-{
		return this.owner;
  }-*/;
}
