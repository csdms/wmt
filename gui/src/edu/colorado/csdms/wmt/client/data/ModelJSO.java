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
import com.google.gwt.core.client.JsArray;

/**
 * A GWT JavaScript overlay (JSO) type that describes a WMT model, consisting of
 * components, their parameters, and their connections; information
 * corresponding to the "show" URL in the API. Declares JSNI methods to access
 * these attributes from a JSON and modify them in memory.
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
   * A convenience method that returns the number of components a model has.
   */
  public final native int nComponents() /*-{
    var n = 0;
		if (typeof this.model != 'undefined') {
			n = Object.keys(this.model).length;
		}
    return n;
  }-*/;

  /**
   * Gets the JsArray of the components, including their parameters and their
   * connections, that make up a model. This is a JSNI method.
   */
  public final native JsArray<ModelComponentJSO> getComponents() /*-{
		return this.model;
  }-*/;

  /**
   * Stores the JsArray of the components, including their parameters and their
   * connections, that make up a model. This is a JSNI method.
   * 
   * @param components a JsArray of ModelComponentJSO components
   */
  public final native void setComponents(JsArray<ModelComponentJSO> components) /*-{
		this.model = components;
  }-*/;
}
