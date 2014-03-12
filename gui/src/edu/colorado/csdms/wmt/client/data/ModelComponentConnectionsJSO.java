package edu.colorado.csdms.wmt.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * A GWT JavaScript overlay (JSO) type that describes the connections of a WMT
 * model component. This object composes the "connect" attribute of a
 * {@link ModelComponentJSO} object. Declares JSNI methods to access these
 * attributes from a JSON and modify them in memory.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelComponentConnectionsJSO extends JavaScriptObject {

  // Overlay types always have protected, zero-arg constructors.
  protected ModelComponentConnectionsJSO() {
  }
  
  /**
   * Returns, as a JsArrayString, the ids of the all the ports of a model
   * component. Index this array with JsArrayString#get. This is a JSNI method.
   */
  public final native JsArrayString getPortIds() /*-{
		var keys = Object.keys(this);
		return keys;
  }-*/;  
  
  /**
   * Returns, as a JsArrayString, the ids of the all the components that are
   * connected to the ports of a model component. Index this array with
   * JsArrayString#get. This is a JSNI method.
   */
  public final native JsArrayString getConnections() /*-{
		var connections = [];
		for (var key in this) {
			var atString = this[key];
			var componentName = atString.substr(atString.indexOf("@") + 1);
			connections = connections.concat(componentName);
		}
		return connections;
  }-*/;

  /**
   * Returns, as a String, the id of the component connected to the port
   * specified by the input portId.
   * 
   * @param portId the id of the port, a String
   */
  public final native String getConnection(String portId) /*-{
		try {
			var atString = this[portId];
			return atString.substr(atString.indexOf("@") + 1);
		} catch (e) {
			return null;
		}
  }-*/;

  /**
   * Creates or appends this object with a key-value pair of the form
   * "usesPortId":"providesPortId@componentId". This is a JSNI method.
   * 
   * @param portId the id of the uses port, a String
   * @param componentId the component id that provides the port, a String
   */
  public final native void addConnection(String portId, String componentId) /*-{
		var c = null;
		if (componentId != null) {
			c = portId + "@" + componentId;
		}
		this[portId] = c;
  }-*/;
}
