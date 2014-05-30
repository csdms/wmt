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
import com.google.gwt.core.client.JsArrayString;

/**
 * A GWT JavaScript overlay (JSO) type that describes the parameters of a WMT
 * model component. This object composes the "parameters" attribute of a
 * {@link ModelComponentJSO} object. Declares JSNI methods to access these
 * attributes from a JSON and modify them in memory.
 * 
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class ModelComponentParametersJSO extends JavaScriptObject {

  // Overlay types always have protected, zero-arg constructors.
  protected ModelComponentParametersJSO() {
  }

  /**
   * Returns, as a JsArrayString, the keys of the all the parameters. Index
   * this array with JsArrayString#get. This is a JSNI method.
   */
  public final native JsArrayString getKeys() /*-{
		var keys = Object.keys(this);
		return keys;
  }-*/;  

  /**
   * Returns, as a JsArrayString, the values of the all the parameters. Values
   * that are not of type String are coerced! Index this array with
   * JsArrayString#get. This is a JSNI method.
   */
  public final native JsArrayString getValues() /*-{
		var values = [];
		for (var key in this) {
			values = values.concat(this[key].toString());
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
			var value = this[parameterKey].toString();
			return value;
		} catch (e) {
			return null;
		}
  }-*/;
  
  /**
   * Creates or appends a single parameter (as a key-value pair) of a
   * component of a model. This is a JSNI method.
   * 
   * @param key the parameter name
   * @param value the parameter value
   */
  public final native void addParameter(String key, String value) /*-{
    this[key] = value;
  }-*/;
}
