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

import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * A GWT JavaScript overlay (JSO) type that describes ports that a WMT component
 * model provides and uses, with "id" and "required" attributes. Declares JSNI
 * methods to access these attributes from a JSON and modify them in memory.
 * 
 * @see <a
 *      href="http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html">http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsOverlay.html</a>
 * @author Mark Piper (mark.piper@colorado.edu)
 */
public class PortJSO extends JavaScriptObject {

  // Overlay types have protected, no-arg, constructors.
  protected PortJSO() {
  }

  /**
   * JSNI method to get the "id" attribute of a port. If no port is present,
   * null is returned.
   */
  public final native String getId() /*-{
		return (typeof this.id == 'undefined') ? null : this.id;
  }-*/;

  /**
   * A JSNI method to set the "id" of a port.
   * 
   * @param id the port identifier, a String
   */
  public final native void setId(String id) /*-{
		this.id = id;
  }-*/;
  
  /**
   * JSNI method to get the "required" attribute of a port. If this attribute is
   * not present, false is returned. Note that the return is a JS boolean, not a
   * J Boolean.
   */
  public final native boolean isRequired() /*-{
		return (typeof this.required == 'undefined') ? false : this.required;
  }-*/;
  
  /**
   * A JSNI method to set the "required" attribute of a port.
   * 
   * @param required true if port is required
   */
  public final native void isRequired(boolean required) /*-{
		this.required = required;
  }-*/;

  /**
   * A JSNI method to access the "exchange_items" attribute of a PortJSO. May not be
   * present, though ignored without an exception; is an array of strings,
   * represented by a JsArrayString object.
   */
  public final native JsArrayString getExchangeItems() /*-{
		return this.exchange_items;
  }-*/;

  /**
   * A non-JSNI method for stringifying the attributes of a port. Must be final.
   */
  public final Vector<String> toStringVector() {

    Vector<String> retVal = new Vector<String>();
    Boolean isRequired = isRequired();
    retVal.add("id: " + getId());
    retVal.add("required: " + isRequired.toString());
    return retVal;
  }
}
