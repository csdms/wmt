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
   * A JSNI method to get the id of the model, an int used to uniquely
   * identify it in the database. The user can't modify this id -- it's set by
   * the API. Be aware that this is different than {@link #getId()}, which is
   * used to get the id of a component.
   */
  public final native int getModelId() /*-{
    if (typeof this.id == 'undefined') {
			return -1;
		} else {
			return this.id;
		}
  }-*/;  

  /**
   * A JSNI method to get the owner of the model, a String. The user can't
   * modify this id -- it's set by the API.
   */
  public final native String getOwner() /*-{
		return this.owner;
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
   * Sets the driver object of a model component. This is a JSNI method.
   */
  public final native void setDriver() /*-{
		this.driver = true;
  }-*/;  
  
  /**
   * Returns, as a JsArrayString, the keys of the all the parameters listed
   * under the eponymous object of a model component. Index this array with
   * JsArrayString#get. This is a JSNI method.
   */
  public final native JsArrayString getParameters() /*-{
		var keys = Object.keys(this.parameters);
		return keys;
  }-*/;  

  /**
   * Creates or appends to the "parameter" object of a ModelJSO a single
   * parameter (as a key-value pair) of a component of a model. This is a JSNI
   * method.
   * 
   * @param key the parameter name
   * @param value the parameter value
   */
  public final native void setParameter(String key, String value) /*-{
    if (typeof this.parameters == 'undefined') {
			var p = {};
      p[key] = value;
		  this.parameters = p;
		} else {
      this.parameters[key] = value;
		}
  }-*/;  
  
  /**
   * Returns, as a JsArrayString, the values of the all the parameters listed
   * under the "parameters" object of a model component. Values that are not of
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
   * @param parameterKey the key of the parameter, a String
   */
  public final native String getValue(String parameterKey) /*-{
		try {
		  var value = this.parameters[parameterKey].toString();
		  return value;
		}
		catch (e) {
		  return null;
		}
  }-*/;  
  
  /**
   * Returns, as a JsArrayString, the ids of the all the ports listed under
   * the "connect" object of a model component. Index this array with
   * JsArrayString#get. The "connect" object may not be present. This is a JSNI
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
   * connected to the ports listed under the "connect" object of a model
   * component. Index this array with JsArrayString#get. The "connect" object
   * may not be present. This is a JSNI method.
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
  
  /**
   * Creates or appends the "connect" object of a ModelJSO with a key-value
   * pair of the form "usesPortId":"providesPortId@componentId". This is a
   * JSNI method.
   * 
   * @param portId the id of the uses port, a String
   * @param componentId the component id that provides the port, a String
   */
  public final native void setConnection(String portId, String componentId) /*-{
    var c = null;
    if (componentId != null) {
      c = portId + "@" + componentId;
    }
    if (typeof this.connect == 'undefined') {
			var p = {};
      p[portId] = c;
		  this.connect= p;
		} else {
      this.connect[portId] = c;
		}
  }-*/;  
}
