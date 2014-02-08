/**
 * <License>
 */
package edu.colorado.csdms.wmt.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

/**
 * A GWT JavaScript overlay (JSO) type that describes a WMT model, consisting
 * of components, their parameters, and their connections. Declares JSNI
 * methods to access these attributes from a JSON and modify them in memory.
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
   * @param components a JsArray of ModelJSO components
   */
  public final native void setComponents(JsArray<ModelJSO> components) /*-{
		this.model = components;
  }-*/;  
  
  /**
   * Gets the "id" (the instance name) of a component in the model; a
   * String. This is a JSNI method.
   */
  public final native String getId() /*-{
		return this.id;
  }-*/;

  /**
   * Sets the id of a component in the model. This is a JSNI method.
   * 
   * @param id the id of the component, a String
   */
  public final native void setId(String id) /*-{
		this.id = id;
  }-*/;
  
  /**
   * Gets the "class" (the class name) of a component in the model; a
   * String. This is a JSNI method.
   */
  public final native String getClassName() /*-{
		return this["class"]; // "class" is reserved; use hash notation.
  }-*/;

  /**
   * Sets the "class" (the class name) of a component in the model; a
   * String. This is a JSNI method.
   * 
   * @param className the class name of the component, a String
   */
  public final native void setClassName(String className) /*-{
    this["class"] = className; // "class" is reserved; use hash notation.
  }-*/; 
  
  /**
   * Is the element the driver for the simulation? Returns JavaScript boolean
   * type, not Java Boolean. May not be present. This is a JSNI method.
   */
  public final native boolean isDriver() /*-{
    if (typeof this.driver == 'undefined') {
			return false;
		} else {
			return this.driver;
		}
  }-*/;

  /**
   * Returns, as a JsArrayString, the ids of the all the parameters listed
   * under the eponymous key of a model component. Index this array with
   * JsArrayString#get. This is a JSNI method.
   */
  public final native JsArrayString getParameters() /*-{
		var keys = Object.keys(this.parameters);
		return keys;
  }-*/;  

  /**
   * Returns, as a JsArrayString, the values of the all the parameters listed
   * under the "parameters" key of a model component. Values that are not of
   * type String are coerced! Index this array with JsArrayString#get. This is
   * a JSNI method.
   */
  public final native JsArrayString getValues() /*-{
		var values = [];
		for (var key in this.parameters) {
			values = values.concat(this.parameters[key].toString());
		}
		return values;
  }-*/;  
  
  /**
   * Returns, as a (coerced!) String, the value of a given parameter.
   * 
   * @param parameterId the id of the parameter, a String
   */
  public final native String getValue(String parameterId) /*-{
		try {
		  var value = this.parameters[parameterId].toString();
		  return value;
		}
		catch (e) {
		  return null;
		}
  }-*/;  
  
  /**
   * Returns, as a JsArrayString, the ids of the all the ports listed under
   * the "connect" key of a model component. Index this array with
   * JsArrayString#get. The "connect" key may not be present. This is a JSNI
   * method.
   */
  public final native JsArrayString getPorts() /*-{
		if (typeof this.connect == 'undefined') {
			return null;
		} else {
			var keys = Object.keys(this.connect);
			return keys;
		}
  }-*/;

  /**
   * Returns, as a JsArrayString, the ids of the all the components that are
   * connected to the ports listed under the "connect" key of a model
   * component. Index this array with JsArrayString#get. The "connect" key may
   * not be present. This is a JSNI method.
   */
  public final native JsArrayString getConnections() /*-{
		if (typeof this.connect == 'undefined') {
			return null;
		} else {
			var connections = [];
			for (var key in this.connect) {
			  var atString = this.connect[key];
			  var componentName = atString.substr(atString.indexOf("@") + 1);
				connections = connections.concat(componentName);
			}
			return connections;
		}
  }-*/;
  
  /**
   * Returns, as a String, the id of the component connected to the port
   * specified by the input portId.
   * 
   * @param portId the id of the port, a String
   */
  public final native String getConnection(String portId) /*-{
		if (typeof this.connect == 'undefined') {
			return null;
		}
		try {
		  var atString = this.connect[portId];
		  return atString.substr(atString.indexOf("@") + 1);
		}
		catch (e) {
		  return null;
		}
  }-*/;
}
