/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 mcflugen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.colorado.csdms.wmt.client.data;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A GWT JavaScript overlay (JSO) type that describes a single component of a
 * WMT model; an array of these objects composes the "model" attribute of a
 * {@link ModelJSO} object. Declares JSNI methods to access these attributes
 * from a JSON and modify them in memory.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelComponentJSO extends JavaScriptObject {

  // Overlay types always have protected, zero-arg constructors.  
  protected ModelComponentJSO() {
  }

  /**
   * Gets the "id" (the instance name) of a component in the model; a String.
   * This is a JSNI method.
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
   * Gets the "class" (the class name) of a component in the model; a String.
   * This is a JSNI method.
   */
  public final native String getClassName() /*-{
		return this["class"]; // "class" is reserved; use hash notation.
  }-*/;

  /**
   * Sets the "class" (the class name) of a component in the model; a String.
   * This is a JSNI method.
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
   * A convenience method that returns the number of parameters a component
   * has.
   */
  public final native int nParameters() /*-{
    var n = 0;
		if (typeof this.parameters != 'undefined') {
			n = Object.keys(this.parameters).length;
		}
    return n;
  }-*/;
  
  /**
   * JSNI method to get the "parameters" attribute of a ModelComponentJSO, a
   * {@link ModelComponentParametersJSO} object. Include a null check for a 
   * component without parameters.
   */
  public final native ModelComponentParametersJSO getParameters() /*-{
		return (typeof this.parameters != 'undefined') ? this.parameters: null;
  }-*/;
  
  /**
   * JSNI method to set the "parameters" attribute of a ModelComponentJSO.
   * 
   * @param parameters a {@link ModelComponentParametersJSO} object
   */
  public final native void setParameters(ModelComponentParametersJSO parameters) /*-{
		this.parameters = parameters;
  }-*/;
  
  /**
   * A convenience method that returns the number of connections (ports) a
   * component has.
   */
  public final native int nConnections() /*-{
    var n = 0;
		if (typeof this.connect != 'undefined') {
			n = Object.keys(this.connect).length;
		} 
    return n;
  }-*/;

  /**
   * JSNI method to get the "connect" attribute of a ModelComponentJSO, a
   * {@link ModelComponentConnectionsJSO} object. A component may have no
   * connections, so a null check is included.
   */
  public final native ModelComponentConnectionsJSO getConnections() /*-{
		return (typeof this.connect != 'undefined') ? this.connect : null;
  }-*/;
  
  /**
   * JSNI method to set the "connect" attribute of a ModelComponentJSO.
   * 
   * @param parameters a {@link ModelComponentConnectionsJSO} object
   */
  public final native void setConnections(ModelComponentConnectionsJSO connect) /*-{
		this.connect = connect;
  }-*/;
}
